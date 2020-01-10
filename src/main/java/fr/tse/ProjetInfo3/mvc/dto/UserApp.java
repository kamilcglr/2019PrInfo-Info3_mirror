package fr.tse.ProjetInfo3.mvc.dto;

/**
 * @author Laïla
 * This class contains informations about user of application.
 */
public class UserApp {

    private int id;
    private String userName;
    private String password;
    private String mail;

    public UserApp(String userName, String password, String mail) {
        this.userName = userName;
        this.password = password;
        this.mail = mail;
    }

    public UserApp() {
        this.userName = "";
        this.password = "";
        this.mail = "";
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
