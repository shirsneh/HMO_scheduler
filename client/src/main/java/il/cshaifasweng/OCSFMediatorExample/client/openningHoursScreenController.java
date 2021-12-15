package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Clinic;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;


import java.io.IOException;
import java.sql.Time;
import java.util.List;


public class openningHoursScreenController {
    Message clientMsg = new Message();
    boolean manager;
    /*
    @FXML
    private MenuItem ChangeAppBtn;
*/
    @FXML
    private TableColumn<Time, Time> OpenningHourColumn;
    @FXML
    private TableColumn<Time, Time> ClosingHourColumn;
    @FXML
    private Button ChangeHoursBtn;

    @FXML
    private Menu ClinicsBtn;

    @FXML
    private ChoiceBox<String> ClinicsList;

    @FXML
    private TextField closeHourTF;
    /*
        @FXML
        private Menu MainPageBtn;
    */
    @FXML
    private MenuItem OpenningHoursBtn;

    @FXML
    private Button submitChangeHoursBtn;
    /*
        @FXML
        private MenuItem contactInfoBtn;

        @FXML
        private MenuItem newAppBtn;
    */
    @FXML
    private TextField openHourTF;
    /* @FXML
        private MenuItem scheduledAppBtn;
     */
    @FXML
    void pressChangeAppBtn(ActionEvent event) {}

    @FXML
    void pressContactInfoBtn(ActionEvent event) {}

    @FXML
    void pressMainPageBtn(ActionEvent event) {}

    @FXML
    void pressNewAppBtn(ActionEvent event) {}
    @FXML
    void pressOpenningHoursBtn(ActionEvent event) {
        OpenningHoursBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {

            }
        });
    }

    @FXML
    void pressChangeHoursBtn(ActionEvent event) {
        ChangeHoursBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                openHourTF.setVisible(true);
                closeHourTF.setVisible(true);
            }
        });
    }

    @FXML
    void pressSubmitChangeHoursBtn(ActionEvent event){
        submitChangeHoursBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                clientMsg.setAction("change hours");
                try{
                    if(!openHourTF.getText().equals("")) {
                        clientMsg.setOpenningHour(Time.valueOf(openHourTF.getText()));
                    }
                    if(!closeHourTF.getText().equals("")) {
                        clientMsg.setClosingHour(Time.valueOf(closeHourTF.getText()));
                    }
                    OpenningHourColumn.setText(String.valueOf(clientMsg.getClinic().getOpenningHour()));
                    ClosingHourColumn.setText(String.valueOf(clientMsg.getClinic().getClosingHour()));
                    SimpleClient.getClient().sendToServer(clientMsg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    void pressScheduledAppBtn(ActionEvent event) {
    }

    @FXML
    void initialize() {
        manager = App.getType().equals("Manager");
    }
    @FXML
    void ShowClinics(MouseEvent event) {
        ClinicsList.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // the messege need to stay the same so ill be able to change it with input- meaby
                clientMsg.setAction("GetAllClinics");
                try {
                    //not sure if this is right --I want to send the msg to server-yoni
                    SimpleClient.getClient().sendToServer(clientMsg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                List<Clinic> clinics = Message.getClinicList();
                for (Clinic clinic : clinics) {
                    ClinicsList.getItems().add(clinic.getName());
                }
                String chosenClinic = ClinicsList.getSelectionModel().getSelectedItem();
                if(chosenClinic!=null){
                    clientMsg.setClinicName(chosenClinic);
                    clientMsg.setAction("GetClinicFromName");
                    try {
                        //not sure if this is right --I want to send the msg to server-yoni
                        SimpleClient.getClient().sendToServer(clientMsg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    OpenningHourColumn.setText(String.valueOf(clientMsg.getClinic().getOpenningHour()));
                    ClosingHourColumn.setText(String.valueOf(clientMsg.getClinic().getClosingHour()));
                    if(manager) {
                        ChangeHoursBtn.setVisible(true);
                    }
                    clientMsg.setOpenningHour(null);
                    clientMsg.setClosingHour(null);
                }
            }
        });
    }

}
