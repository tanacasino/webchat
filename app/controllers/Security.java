package controllers;

import java.util.List;

import play.Logger;
import play.db.jpa.JPABase;
import net.sf.oval.internal.Log;
import utilities.UserManagement;
import models.*;

public class Security extends Secure.Security {

    public static Log LOG = Log.getLog(Security.class);

    static boolean authenticate(String username, String password) {
        Logger.info("authenticate called by %s", username);
        boolean auth = User.connect(username, password) != null;
        if (auth) {
            Logger.info("Logged in %s", username);
        } else {
            Logger.info("Failed to auth &s", username);
        }
        return auth;
    }

}
