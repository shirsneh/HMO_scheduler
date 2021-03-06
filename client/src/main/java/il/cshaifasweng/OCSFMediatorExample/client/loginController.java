package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.events.loginEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * loginController
 * Contains 2 text fields for username and password and a button to login.
 * Sends a message to server with username and password in order to log in.
 * If everything is all right - the user is logged in and the screen switch to relevant main page according to user type.
 * If there was a problem - displays relevant warning.
 */

public class loginController implements Initializable {
    private SimpleClient client;
    private static int sessionID = 0;

    @FXML
    private TextField userTF;
    @FXML
    private TextField passwordTF;
    @FXML
    private Button loginButton;
    @FXML
    private TextArea loginFailedWarning;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        EventBus.getDefault().register(this);
        loginFailedWarning.setVisible(false);
    }

    @FXML
    void authorize(ActionEvent event)  {
        loginFailedWarning.setVisible(false);
        try {
            String username = userTF.getText();
            String password = passwordTF.getText();
            if (username.equals("") || password.equals("")) {
                loginFailedWarning.setText("Please enter username and password");
                loginFailedWarning.setVisible(true);
            } else {
                    SimpleClient.setClientNull();
                    client = SimpleClient.getClient();
                    if (client != null) {
                        try {
                            client.openConnection();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Message msg = new Message();
                        msg.setUsername(username);
                        msg.setPassword(password);
                        msg.setAction("login");
                        SimpleClient.getClient().sendToServer(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateSessionID() {
        sessionID++;
        return "session: " + sessionID;
    }


    @Subscribe
    public void onLoginEvent(loginEvent event) {
        String status = event.getStatus();
        if (status != null) {
            if (status.equals("you are already logged in")) {
                loginFailedWarning.setText("Login Failed- user is already logged in");
                loginFailedWarning.setVisible(true);
            } else if (status.equals(("Wrong username or password"))){
                loginFailedWarning.setText("Login Failed- incorrect username or password.\nPlease try again or go to the main office");
                loginFailedWarning.setVisible(true);
            }else {
                App.setUsername(event.getUsername());
                App.setUserType(event.getUserType());
                App.setFirst_name(event.getFirst_name());
                EventBus.getDefault().unregister(this);
                ChangeScreens.changeToMainPage();
            }
        }
    }
}

