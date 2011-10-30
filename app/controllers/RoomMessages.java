package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Room;
import models.RoomMessage;
import models.User;
import play.mvc.With;

@With(Secure.class)
public class RoomMessages extends CRUD {

    static class MessageJSON {
        public final Long id;
        public final String message;
        public final Long timestamp;
        public final String username;
        public final String fullname;
        public MessageJSON(RoomMessage rm) {
            this.id = rm.id;
            this.message = rm.message;
            this.timestamp = rm.sentAt.getTime();
            this.username = rm.who.name;
            this.fullname = rm.who.fullname;
        }
    }

    public static void fetch(Long message, Long room, Integer size) {
        if (size == null) {
            size = 20;
        }
        if (message == null || message < 1) {
            badRequest();
        }
        if (room == null || room < 1) {
            badRequest();
        }
        User user = User.findByName(Security.connected());
        Room r = Room.findById(room);
        if (!r.members.contains(user)) {
            forbidden();
        }
        List<MessageJSON> messages = new ArrayList<RoomMessages.MessageJSON>();
        for (RoomMessage rm : RoomMessage.findOldMessagesById(message, r, size)) {
            messages.add(new MessageJSON(rm));
        }
        renderJSON(messages);
    }

}
