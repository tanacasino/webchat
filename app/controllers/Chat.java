package controllers;

import play.*;
import play.libs.F.Either;
import play.libs.F.EventStream;
import play.libs.F.Promise;
import play.mvc.*;
import play.mvc.Http.WebSocketClose;
import play.mvc.Http.WebSocketEvent;
import play.db.jpa.*;
import play.db.jpa.GenericModel.JPAQuery;

import groovy.ui.text.FindReplaceUtility;

import java.util.*;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;

import models.*;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.WebSocketController;
import websocket.ChatEvent;
import websocket.WebSocketEventStreamManager;
import websocket.ChatEvent.Event;
import websocket.ChatEvent.Message;
import static play.libs.F.Matcher.ClassOf;
import static play.mvc.Http.WebSocketEvent.SocketClosed;
import static play.mvc.Http.WebSocketEvent.TextFrame;

@With(Secure.class)
public class Chat extends Controller {

    @Before
    static void setConnectedUser() {
        if (Security.isConnected()) {
            User user = User.findByName(Security.connected());
            renderArgs.put("user", user);
        }
    }

    static class RoomInfo {
        public final Long id;
        public final String name;
        public final String owner;
        public final List<RoomMessage> messages;
        public final List<User> members;
        public final List<RoomFile> files;
        public final Blob icon;

        RoomInfo(Long id, String name, String owner, List<RoomMessage> messages, List<User> members, List<RoomFile> files, Blob icon) {
            this.id = id;
            this.name = name;
            this.owner = owner;
            this.messages = messages;
            this.members = members;
            this.files = files;
            this.icon = icon;
        }
    }

    public static void index() {
        User user = renderArgs.get("user", User.class);
        List<Room> joinedRooms = Room.findJoinedRoomsByUser(user);
        List<RoomInfo> rooms = new ArrayList<RoomInfo>();
        for (Room room : joinedRooms) {
            List<RoomMessage> messages = RoomMessage.getRecentMessage(room, 20);
            List<User> memberList = new ArrayList<User>((room.members));
            List<RoomFile> roomFiles = RoomFile.find("byRoom", room).fetch();
            RoomInfo info = new RoomInfo(room.id, room.name, room.owner.name, messages, memberList, roomFiles, room.icon);
            rooms.add(info);
        }
        render(rooms);
    }

    /**
     * メッセージの送信. Ajax POSTで送付される
     * @param roomId
     * @param text
     */
    public static void send(Long roomId, String text) {
        Logger.info("Receive Message. roomId=%s text=%s", roomId, text);
        User user = renderArgs.get("user", User.class);
        if (roomId == null || roomId < 1) {
            Logger.error("Invalid room id received. RoomId=%s User=%s", roomId, user);
            badRequest();
        }
        if (StringUtils.isBlank(text)) {
            Logger.error("Invalid text received. Text=%s RoomId=%s User=%s", text,roomId, user);
            badRequest();
        }
        Room room = Room.findById(roomId);
        if (!room.members.contains(user)) {
            forbidden();
        }
        RoomMessage msg = new RoomMessage(text, room, user);
        msg.validateAndSave();
        WebSocketEventStreamManager.sendMessage(msg.id.toString(), user, text, room);
    }

    public static String toJson(ChatEvent.Event event) {
        Gson gson = new Gson();
        String json = gson.toJson(event);
        return json;
    }

    public static class WebSocket extends WebSocketController {

        public static void connect() {
            // ユーザの取得
            String username = Security.connected();
            if (username == null) {
                Logger.error("Anonymous connected!!!");
                return;
            }
            //String fullname = User.findByName(username).fullname;

            EventStream eventStream = WebSocketEventStreamManager.getEventStream(username); // WebSocketでオンライン・オフライン管理もできるはず.

            while (inbound.isOpen()) {
                // WebSocketEvent or ChatEventの発生までwaitする
                Either<WebSocketEvent, ChatEvent.Event> e =
                        await(Promise.waitEither(inbound.nextEvent(), eventStream.nextEvent()));

                // e._1 はWebScoketEvent
                // e._2 はChatEvent

                // TODO それぞれのイベントに対して適切な処理を行う.
                // TODO 許可リクエストを受けた場合はクローズするなど...
                // WebSocketEventは３種類パターンマッチ
                // 1. TextFrame => テキストの受信イベント
                // 2. BinaryFrame => バイナリデータの受信イベント
                // 3. SocketClosed => WebSocketのクローズイベント

                // TextFrameの場合は eventStreamにメッセージイベントを発生させる.
                for (String text : TextFrame.match(e._1)) {
                    Logger.info("Recieve message: " + text);
                   //  WebSocketEventStreamManager.sendMessage(username, fullname, text);
                }

                // SocketClosedの場合は eventStreamにオフラインイベントを発生させる.
                // また disconnectすることで終了.
                for (WebSocketClose closed : SocketClosed.match(e._1)) {
                    Logger.info("SocketClosed: " + closed);
                    WebSocketEventStreamManager.removeEventStream(username, eventStream); // イベントストリームの削除
                    disconnect();
                }

                /*
                 * これより下は自分で作成したイベント. WebSocketのコネクションが作成されたらOnlineイベント作成するぜよ.
                 */

                // OnlineEvent
                for (ChatEvent.Online online : ClassOf(ChatEvent.Online.class).match(e._2)) {
                    Logger.info("Online : %s", online.username);
                    if (!username.equals(online.username)) {
                        outbound.send(toJson(online));
                    }
                }

                // OfflineEvent
                for (ChatEvent.Offline offline : ClassOf(ChatEvent.Offline.class).match(e._2)) {
                    Logger.info("Offline : %s", offline.username);
                    if (!username.equals(offline.username)) {
                        outbound.send(toJson(offline));
                    }
                }

                // NewMessageEvent
                for (ChatEvent.Message message : ClassOf(ChatEvent.Message.class).match(e._2)) {
                    outbound.send(toJson(message));
                }
            }
        }

    }

}
