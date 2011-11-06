package controllers;

import java.util.ArrayList;
import java.util.List;

import models.User;
import play.Logger;
import play.Play;
import play.data.Upload;
import play.data.binding.Binder;
import play.db.jpa.Blob;
import play.mvc.Before;
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

    public static void profile() {
        User user = User.findByName(Security.connected());
        notFoundIfNull(user);
        render(user);
    }

    public static void updateFullname(String fullname) {
        if (fullname == null || fullname.isEmpty()) {
            badRequest();
        }
        String username = Security.connected();
        User user = User.findByName(username);
        notFoundIfNull(user);
        user.fullname = fullname;
        user.save();
        Users.profile();
    }

    public static void updateAvatar(Upload avatar) {
        if (avatar == null) {
            badRequest();
        }
        /*
         * NOTE: 画像の種類やサイズまでチェックしたほうが良いと思う. ちろもんHTML側でバリデーションも必要.
         */
        if (!avatar.getContentType().startsWith("image/")) {
            error("Not supported ContentType: " + avatar.getContentType());
        }
        String username = Security.connected();
        User user = User.findByName(username);
        notFoundIfNull(user);
        Blob blob = new Blob();
        blob.set(avatar.asStream(), avatar.getContentType());
        user.avatar = blob;
        user.save();
        Users.profile();
    }

}
