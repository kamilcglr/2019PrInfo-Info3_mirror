/**
 * 
 */
package fr.tse.ProjetInfo3.mvc.dto;

/**
 * @author Laïla
 *
 */
public class UserApp {

	private String username;
	private String password;
	private String twitterName;
	private String mail;
	public UserApp(String username, String password, String twitterName, String mail) {
		this.username=username;
		this.password=password;
		this.twitterName=twitterName;
		this.mail=mail;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getTwitterName() {
		return twitterName;
	}
	public void setTwitterName(String twitterName) {
		this.twitterName = twitterName;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	
}
