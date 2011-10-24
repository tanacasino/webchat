package models;

import java.util.*;
import javax.persistence.*;

import play.data.binding.As;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.*;

@Entity
public class RoomMessage extends Model {

    @Required
    @Lob
    @MinSize(1)
    @MaxSize(3000)
    public String message;

    @Required
    @ManyToOne
    public Room room;

    @Required
    @ManyToOne
    public User who;

    @Required
    public Date sentAt;

    public RoomMessage(String message, Room room, User who) {
        this.message = message;
        this.room = room;
        this.who = who;
        this.sentAt = new Date();
    }

    @Override
    public String toString() {
        return "id: " + super.getId() + ", msg: " + this.message + ", room:"
                + this.room.id + ", who:" + this.who.name + ", date: "
                + this.sentAt;
    }

    public static List<RoomMessage> getRecentMessage(Room room, int size) {
        return RoomMessage.find("room = ? order by id desc", room).fetch(size);
    }

}
