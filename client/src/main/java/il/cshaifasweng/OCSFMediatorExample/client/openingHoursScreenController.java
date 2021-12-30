package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.io.IOException;
import java.time.LocalTime;


public class openingHoursScreenController {
    Message clientMsg = new Message();
    boolean manager;
    String chosen_clinic;

    @FXML
    Pane menubar;
    @FXML
    private TextField OpeningHourTF;
    @FXML
    private TextField ClosingHourTF;
    @FXML
    private Button ChangeHoursBtn;

    @FXML
    private ComboBox<String> ClinicsList;
    @FXML
    private TextField closeHourTF;

    @FXML
    private Button submitChangeHoursBtn;

    @FXML
    private TextField openHourTF;

    @FXML
    void pressChangeHoursBtn(ActionEvent event) {
        openHourTF.setVisible(true);
        closeHourTF.setVisible(true);
        submitChangeHoursBtn.setVisible(true);
    }

    @FXML
    void pressSubmitChangeHoursBtn(ActionEvent event){
        clientMsg.setAction("change hours");
        clientMsg.setClinicName(chosen_clinic);
        try{
            if(!openHourTF.getText().equals("")) {
                clientMsg.setOpeningHour(LocalTime.parse(openHourTF.getText()));
            }
            if(!closeHourTF.getText().equals("")) {
                clientMsg.setClosingHour(LocalTime.parse(closeHourTF.getText()));
            }
            SimpleClient.getClient().sendToServer(clientMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onChangeHoursEvent(ChangeHoursEvent event){
        OpeningHourTF.setText(String.valueOf(clientMsg.getOpeningHour()));
        ClosingHourTF.setText(String.valueOf(clientMsg.getClosingHour()));
    }

    @FXML
    void initialize() throws IOException {
        Parent menuBarParent = App.loadFXML("menuBar.fxml");
        String cssPath = getClass().getResource("/style.css").toString();
        menuBarParent.getStylesheets().add(cssPath);
        menubar.getChildren().clear();
        menubar.getChildren().add(menuBarParent);
        EventBus.getDefault().register(this);
        manager = App.getUserType().equals("Manager");
        try {
            ChangeHoursBtn.setVisible(false);
            openHourTF.setVisible(false);
            closeHourTF.setVisible(false);
            submitChangeHoursBtn.setVisible(false);
            Message msg= new Message();
            msg.setAction("GetAllClinics");
            SimpleClient.getClient().openConnection();
            SimpleClient.getClient().sendToServer(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Subscribe
    public void onClinicListUpdateEvent(ClinicListUpdateEvent event) {
        for (String clinic : event.getClinicNames()) {
            ClinicsList.getItems().add(clinic);
        }
    }


    @Subscribe
    public void onShowHoursEvent(showHoursEvent event){
        OpeningHourTF.setText(String.valueOf(event.getOpening_hour()));
        ClosingHourTF.setText(String.valueOf(event.getClosing_hour()));
        if (manager) {
            ChangeHoursBtn.setVisible(true);
        }
        openHourTF.clear();
        closeHourTF.clear();
        openHourTF.setVisible(false);
        closeHourTF.setVisible(false);
        submitChangeHoursBtn.setVisible(false);
        clientMsg.setOpeningHour(null);
        clientMsg.setClosingHour(null);
    }

    @FXML
    void chooseClinic() {
        String chosenClinic = ClinicsList.getSelectionModel().getSelectedItem();
        chosen_clinic = chosenClinic;
        clientMsg.setClinicName(chosenClinic);
        clientMsg.setAction("pull opening hours");
        try {
            SimpleClient.getClient().sendToServer(clientMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
