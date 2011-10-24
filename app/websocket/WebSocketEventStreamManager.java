package websocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Room;
import models.RoomMessage;
import models.User;

import com.google.gson.Gson;

import play.Logger;
import play.db.jpa.JPABase;
import play.libs.F.EventStream;
import play.libs.F.*;
import websocket.ChatEvent.Event;
import websocket.ChatEvent.*;

public class WebSocketEventStreamManager {

    // 1WebSocketコネクションにつき 1EventStreamを管理する.
    private static final Map<String, List<EventStream<Event>>> eventStreamsMap = new HashMap<String, List<EventStream<Event>>>();

    public static EventStream getEventStream(String username) {
        synchronized (eventStreamsMap) {
            EventStream<Event> eventStream = new EventStream<ChatEvent.Event>();
            if (eventStreamsMap.containsKey(username)) {
                eventStreamsMap.get(username).add(eventStream);
            } else {
                ArrayList<EventStream<Event>> streams = new ArrayList<EventStream<Event>>();
                streams.add(eventStream);
                eventStreamsMap.put(username, streams);
                // NOTE: ここでオンラインイベント発生でいいんじゃないか？
            }
            return eventStream;
        }
    }

    public static void removeEventStream(String username, EventStream<ChatEvent.Event> eventStream) {
        synchronized (eventStreamsMap) {
            if (eventStreamsMap.containsKey(username)) {
                List<EventStream<Event>> streams = eventStreamsMap.get(username);
                streams.remove(eventStream);
                if (streams.size() == 0) {
                    eventStreamsMap.remove(username);
                    // NOTE: ここでオフラインイベント発生でいいんじゃないか？
                }
            } else {
                Logger.error("Failed to releaseEventStream. username: %s", username);
            }
        }
    }

    /**
     * send all eventStream. FIXME ロックが大きい気がする大きなメッセージになるとボトルネックになるかも。よりよい管理を考える必要があり。
     */
    public static void sendMessage(String mid, User user, String text, Room room) {
        Message event = new Message(mid, user.name, user.fullname, text, room.id.toString());
        // ルームのメンバーにのみ送信する.
        synchronized (eventStreamsMap) {
            for (User member : room.members) {
                if (eventStreamsMap.containsKey(member.name)) {
                    List<EventStream<Event>> streams = eventStreamsMap.get(member.name);
                    for (EventStream<Event> stream : streams) {
                        stream.publish(event);
                    }
                }
            }
        }
    }

}
