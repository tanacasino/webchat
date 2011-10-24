package models;

import java.util.*;

import javax.persistence.*;

import play.data.binding.As;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.*;

@Entity
public class Room extends Model {

    @Required
    @MinSize(3)
    @MaxSize(30)
    public String name;

    @Required
    @ManyToOne
    public User owner;

    @Required
    @ManyToMany
    public Set<User> members;

    @Required
    public Date created;

    public Blob icon;

    public Room(String roomName, boolean isPublic, User owner) {
        this.name = roomName;
        this.owner = owner;
        this.members = new HashSet<User>();
        this.members.add(owner);
        this.created = new Date();
    }

    public static List<Room> findJoinedRoomsByUser(User user) {
        // ユーザが所属するルーム一覧の取得.
        // FIXME JOINとか使わないとどう考えてもだめだよー。HQLとJPAがわからないので一旦放置.
        List<Room> rooms = findAll();
        List<Room> ret = new ArrayList<Room>();
        for (Room room : rooms) {
            if (room.members.contains(user)) {
                ret.add(room);
            }
        }
        return ret;
    }

    @Override
    public String toString() {
        return "id: " + this.id + ", name: " + this.name + ", created: " + this.created;
    }

}