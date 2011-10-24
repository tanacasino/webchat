package controllers;

import play.mvc.With;
import models.User;

@With(Security.class)
public class Users extends CRUD {

    public static void userPhoto(long id) {
        final User user = User.findById(id);
        notFoundIfNull(user);
        response.setContentTypeIfNotSet(user.avatar.type());
        renderBinary(user.avatar.get());
    }

}
