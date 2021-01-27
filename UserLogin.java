
package GUIApplication;

/**
 *
 * @author BOUJIQUE
 */
public class UserLogin {
    String username, password;
    UserLogin(String username, String password){
        this.username = username;
        this.password = password;
    }
    public boolean verify(String inputName, String inputPass){
        return inputName.equals(username) && inputPass.equals(password);
    }
}
