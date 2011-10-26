package websocket;

import org.apache.commons.lang.time.DateUtils;

public class ChatEvent {
    /*
     * Event
     */
    public static abstract class Event {
        final public String username;
        final public String fullname;
        final public String type;
        final public Long timestamp;

        public Event(String username, String fullname, String type) {
            this.username = username;
            this.fullname = fullname;
            this.type = type;
            this.timestamp = System.currentTimeMillis();
        }
    }

    public static class Online extends Event {
        public Online(String username, String fullname) {
            super(username, fullname, "online");
        }

    }

    public static class Offline extends Event {
        public Offline(String username, String fullname) {
            super(username, fullname, "offline");
        }
    }

    public static class NewMessage extends Event {
        final public String mid;
        final public String text;
        final public String room;

        public NewMessage(String mid, String username, String fullname, String text, String room) {
            super(username, fullname, "new_message");
            this.mid = mid;
            this.text = text;
            this.room = room;
        }
    }

    public static class DeleteMessage extends Event {
        final public String mid;
        final public String room;

        public DeleteMessage(String mid, String username, String fullname, String room) {
            super(username, fullname, "delete_message");
            this.mid = mid;
            this.room = room;
        }
    }

    public static class UpdateMessage extends Event {
        final public String mid;
        final public String text;
        final public String room;

        public UpdateMessage(String mid, String username, String fullname, String text, String room) {
            super(username, fullname, "update_message");
            this.mid = mid;
            this.text = text;
            this.room = room;
        }
    }

    public static class NewFile extends Event {
        final public String fid;
        final public String room;

        public NewFile(String fid, String username, String fullname, String room) {
            super(username, fullname, "new_file");
            this.fid = fid;
            this.room = room;
        }
    }

    public static class DeleteFile extends Event {
        final public String fid;
        final public String room;

        public DeleteFile(String fid, String username, String fullname, String room) {
            super(username, fullname, "delete_file");
            this.fid = fid;
            this.room = room;
        }
    }

}