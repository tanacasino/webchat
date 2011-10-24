package models;

import javax.persistence.*;

import org.apache.commons.codec.digest.DigestUtils;

import play.data.validation.Password;
import play.data.validation.Required;
import play.db.jpa.*;
import utilities.UserManagement;

@Entity
public class User extends Model {

    @Required
    @Column(unique = true)
    public String name;

    @Required
    @Password
    public String password;

    @Required
    public String fullname;

    @Required
    public boolean isAdmin;

    public Blob avatar;


	public User(String name, String password, String fullname, boolean isAdmin, Blob avatar) {
        this.name = name;
        this.password = password;
        this.fullname = fullname;
        this.isAdmin = isAdmin;
        this.avatar = avatar;
    }

    public static User connect(String name, String password) {
        String hashedPassword = UserManagement.toHashPassword(password);
        return find("byNameAndPassword", name, hashedPassword).first();
    }

    public static User findByName(String name) {
        return find("byName", name).first();
    }

    public void setPassword(String password) {
        this.password = UserManagement.toHashPassword(password);
    }

    @Override
    public String toString() {
        return "id: " + this.id + ", name: " + name;
    }

}
