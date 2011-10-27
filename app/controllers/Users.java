package controllers;

import models.User;
import play.Play;
import play.mvc.With;
import play.vfs.VirtualFile;

@With(Security.class)
public class Users extends CRUD {

    public static void avatar(String username) {
        final User user = User.findByName(username);
        notFoundIfNull(user);
        if (!user.avatar.exists()) {
            VirtualFile vf = Play.getVirtualFile("/public/images/icons/user.png");
            if (vf == null || !vf.exists()) {
                notFound();
            }
            renderBinary(vf.getRealFile());
        }
        response.setContentTypeIfNotSet(user.avatar.type());
        renderBinary(user.avatar.get());
    }

}
