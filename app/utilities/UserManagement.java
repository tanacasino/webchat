package utilities;

import org.apache.commons.codec.digest.DigestUtils;

import play.Logger;

public class UserManagement {

    public static String toHashPassword(String plainPassword) {
        String hashedPassword = DigestUtils.sha256Hex(plainPassword);
        return hashedPassword;
    }

}
