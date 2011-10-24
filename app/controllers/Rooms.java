package controllers;

import play.mvc.With;
import models.Room;
import models.User;

@With(Security.class)
public class Rooms extends CRUD {

    public static void roomPhoto(long id) {
        final Room room = Room.findById(id);
        notFoundIfNull(room);
        response.setContentTypeIfNotSet(room.icon.type());
        renderBinary(room.icon.get());
    }

}
