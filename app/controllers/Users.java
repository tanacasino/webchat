package controllers;

import play.mvc.With;
import models.User;

@With(Security.class)
public class Users extends CRUD {

    public static void avatar(String username) {
        final User user = User.findByName(username);
        notFoundIfNull(user);
        response.setContentTypeIfNotSet(user.avatar.type());
        renderBinary(user.avatar.get());
    }

}
