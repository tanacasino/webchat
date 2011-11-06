package controllers;

import groovy.ui.text.FindReplaceUtility;

import java.util.Date;

import models.Room;
import models.RoomFile;
import models.User;
import play.Logger;
import play.data.Upload;
import play.db.jpa.Blob;
import play.db.jpa.JPABase;
import play.mvc.With;
import websocket.ChatEvent.DeleteFile;
import websocket.ChatEvent.NewFile;
import websocket.WebSocketEventStreamManager;

@With(Security.class)
public class RoomFiles extends CRUD {

    public static void upload(Long roomId, Upload upload) {
        if (roomId == null | roomId < 1 || upload == null) {
            badRequest();
        }
        Logger.info("File Upload: RoomId=%s, File=%s", roomId, upload.toString());
        String username = Security.connected();
        User user = User.findByName(username);
        Room room = Room.findById(roomId);
        notFoundIfNull(room);
        if (!room.members.contains(user)) {
            forbidden();
        }

        Blob blob = new Blob();
        blob.set(upload.asStream(), upload.getContentType());
        RoomFile rfile = new RoomFile();
        rfile.file = blob;
        rfile.name = upload.getFileName();
        rfile.owner = user;
        rfile.size = upload.getSize();
        rfile.uploadedAt = new Date();
        rfile.room = room;
        rfile.validateAndSave();

        // Redirect.
        // ホントはクライアント側でAjaxリクエストにするべき.
        // Ajaxリクエストで画面遷移なしでさばけるようになればこれは削除しましょう。
        Chat.index();
    }

    public static void download(Long id) {
        if (id == null || id < 1) {
            badRequest();
        }
        RoomFile file = RoomFile.findById(id);
        notFoundIfNull(file);
        String username = Security.connected();
        User user = User.findByName(username);
        if (!file.room.members.contains(user)) {
            forbidden();
        }
        response.setContentTypeIfNotSet(file.file.type());
        renderBinary(file.file.get(), file.name, file.file.length());
    }

    public static void rm(Long id) {
        if (id == null || id <1) {
            badRequest();
        }
        RoomFile file = RoomFile.findById(id);
        notFoundIfNull(file);
        String username = Security.connected();
        User user = User.findByName(username);
        if (!file.room.members.contains(user)) {
            forbidden();
        }
        file.delete();
        DeleteFile event = new DeleteFile(file.id.toString(), username, user.fullname, file.room.id.toString());
        WebSocketEventStreamManager.send(event, user, file.room);
        Chat.index();
    }

}
