
package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.jamonapi.utils.FileUtils;

import play.Logger;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;

@Entity
public class RoomFile extends Model {

    @Required
    @MinSize(3)
    @MaxSize(100)
    public String name;

    @Required
    public Blob file;

    @Required
    public Long size;

    @Required
    @ManyToOne
    public User owner;

    @Required
    @ManyToOne
    public Room room;

    @Required
    public Date uploadedAt;

    @Override
    public String toString() {
        return "id: " + id + ", name: " + name + ", room: " + room.id + ", owner: " + owner + ", uploadedAt: " + uploadedAt;
    }
    public String getReadableSize() {
        return utilities.FileUtils.getReadableSize(size);
    }

    public static List<RoomFile> findRoomFilesByRoom(Room room) {
        return RoomFile.find("room = ?  order by id desc", room).fetch();
    }

}
