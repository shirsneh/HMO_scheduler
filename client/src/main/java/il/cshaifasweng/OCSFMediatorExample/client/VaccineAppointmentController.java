package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.client.events.GotClinicsWithVaccine;
import il.cshaifasweng.OCSFMediatorExample.client.events.GotLabWorkersVaccineEven;
import il.cshaifasweng.OCSFMediatorExample.client.events.GotVaccineEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.SavedAppEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * VaccineAppointmentController
 * Handels scheduling appointment to vaccines.
 * Contains - 2 combo boxes for type of vaccine and clinic names that has chosen vaccine service.
 * After choosing vaccine type and clinic - display table with available appointments for the patient selection.
 * After patient will select an appointment and press on schedule appointment button - a message will be sent to server
 * with relevant data and the program will attempt to schedule chosen appointment. An alert will pop with the result of
 * the attempt - appointment saved successfully or not.
 *
 */

public class VaccineAppointmentController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private Label chooseLabel;

    @FXML
    private URL location;

    @FXML
    private TableColumn<AvailableApp, String> ClinicCol1;

    @FXML
    private ChoiceBox<String> ClinicName;

    @FXML
    private TableColumn<AvailableApp, String> EmployeeName1;

    @FXML
    private Pane Menubar;

    @FXML
    private Label warningtext1;

    @FXML
    private TableView<AvailableApp> Table1;

    @FXML
    private TableColumn<AvailableApp, LocalTime> TimeCol1;

    @FXML
    private ComboBox<String> VaccineType;

    @FXML
    private Label cliniclabel;

    @FXML
    private TableColumn<AvailableApp, LocalDate> dateCol1;

    @FXML
    private Button ok;

    @FXML
    private Button save;

    @FXML
    private Label vaccinelabel;

    @FXML
    private Label warningtext;

    private String vaccine_Type;
    private boolean isCovid_vaccine = false;
    private boolean isInfluenza_vaccine = false;
    private Covid19VaccineApp covid19VaccineApp;
    private InfluenzaVaccineApp influenzaVaccineApp;

    @FXML
    void SaveNewApp(ActionEvent event) {
        if (Table1.getSelectionModel().getSelectedItems() != null && Table1.getSelectionModel().getSelectedIndex() != -1) {
            warningtext.setVisible(false);
            try {
                AvailableApp availableApp = Table1.getSelectionModel().getSelectedItem();
                Message msg = new Message();
                msg.setClinicName(availableApp.getClinicCol());
                msg.setUsername(App.getUsername());
                msg.setAppDate(availableApp.getDateCol());
                msg.setEmployee_id(availableApp.getEmployee_id());
                msg.setAppTime(availableApp.getTimeCol());
                msg.setRole(availableApp.getRole());
                if (vaccine_Type.equals("influenza vaccine"))
                    msg.setAction("Add influenza vaccine appointment");
                else
                    msg.setAction("Add covid vaccine appointment");
                SimpleClient.getClient().openConnection();
                SimpleClient.getClient().sendToServer(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            warningtext1.setVisible(true);
    }

    @Subscribe
    public void onGotLabWorkersVaccineEven(GotLabWorkersVaccineEven event) {
        List<LabWorker> labWorkers = event.getLabworkers();
        List<Appointment> appointmentList = event.getAppointmentList();
        Clinic clinic = event.getClinic();
        LocalDate date = LocalDate.now();
        date = date.plusDays(1);
        LocalTime time = clinic.getOpeningHour();
        LocalDate end_date = date.plusWeeks(3);
        end_date = end_date.plusDays(1);
        while (date.isBefore(end_date)) {
            while (time.isBefore(clinic.getClosingHour())) {
                for (LabWorker labWorker : labWorkers) {
                    boolean available = true;
                    for (Appointment app : appointmentList) {
                        if (app.getTime().equals(time) && app.getDate().equals(date) && app.getEmployee().getUsername().equals(labWorker.getUsername())) {
                            available = false;
                            break;
                        }
                    }

                    if (available) {
                        if (time.isAfter(labWorker.getStart_working_hour()) && time.isBefore(labWorker.getFinish_working_hour())) {
                            dateCol1.setCellValueFactory(new PropertyValueFactory<>("dateCol"));
                            TimeCol1.setCellValueFactory(new PropertyValueFactory<>("timeCol"));
                            EmployeeName1.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
                            ClinicCol1.setCellValueFactory(new PropertyValueFactory<>("clinicCol"));
                            Table1.getItems().add(new AvailableApp(date, time, labWorker.getFullName(), clinic.getName(), labWorker.getUserId(), labWorker.getRole()));
                        }
                    }
                }

                time = time.plusMinutes(10);
            }
            time = clinic.getOpeningHour();
            date = date.plusDays(1);
        }
        dateCol1.setVisible(true);
        TimeCol1.setVisible(true);
        EmployeeName1.setVisible(true);
        ClinicCol1.setVisible(true);
        Table1.setVisible(true);
        save.setVisible(true);
    }

    @FXML
    public void chooseClinic() {
        vaccine_Type = VaccineType.getSelectionModel().getSelectedItem();
        if ((vaccine_Type == null)) {
            warningtext1.setText("Please choose vaccine type first!");
            warningtext1.setVisible(true);
        } else {
            warningtext1.setVisible(false);
            cliniclabel.setVisible(true);
            ClinicName.setVisible(true);
            ClinicName.getItems().clear();
            try {
                Message msg = new Message();
                msg.setAction("GetAllClinicsWithVaccine");
                msg.setService_name(vaccine_Type);
                SimpleClient.getClient().openConnection();
                SimpleClient.getClient().sendToServer(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void ViewTable(ActionEvent event) {
        String clinic_name;
        if (ClinicName.getSelectionModel() == null || ClinicName.getSelectionModel().getSelectedIndex() == -1) {
            warningtext1.setText("Please choose a clinic first!");
            warningtext1.setVisible(true);
        } else {
            if (VaccineType.getSelectionModel().getSelectedItem().equals("covid vaccine") && isCovid_vaccine) {
                showAlert("Error", "You're already vaccinated you can't reserve a covid 19 vaccine appointment");
                ChangeScreens.changeToMainPage();
            }
            if (VaccineType.getSelectionModel().getSelectedItem().equals("covid vaccine") && covid19VaccineApp != null) {
                showAlert("Error", "You've already reserved a covid 19 vaccine appointment in " + covid19VaccineApp.getDate() + " " + covid19VaccineApp.getTime() + " in clinic: " + covid19VaccineApp.getClinic().getName());
                ChangeScreens.changeToViewAppsScreen();
            }
            if (VaccineType.getSelectionModel().getSelectedItem().equals("influenza vaccine") && isInfluenza_vaccine) {
                showAlert("Error", "You're already vaccinated you can't reserve an influenza vaccine appointment");
                ChangeScreens.changeToMainPage();
            }
            if (VaccineType.getSelectionModel().getSelectedItem().equals("influenza vaccine") && influenzaVaccineApp != null) {
                showAlert("Error", "You've already reserved an influenza vaccine appointment in " + influenzaVaccineApp.getDate() + " " + influenzaVaccineApp.getTime() + " in clinic: " + influenzaVaccineApp.getClinic().getName());
                ChangeScreens.changeToViewAppsScreen();
            } else {
                clinic_name = ClinicName.getSelectionModel().getSelectedItem();
                warningtext.setVisible(false);
                warningtext1.setVisible(false);
                VaccineType.setVisible(false);
                cliniclabel.setVisible(false);
                vaccinelabel.setVisible(false);
                ClinicName.setVisible(false);
                ok.setVisible(false);
                try {
                    Message msg = new Message();
                    msg.setClinicName(clinic_name);
                    msg.setRole("lab worker");
                    msg.setAction("Get LabWorkers and clinic apps");
                    SimpleClient.getClient().openConnection();
                    SimpleClient.getClient().sendToServer(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @FXML
    void initialize() throws IOException {
        Parent menuBarParent = App.loadFXML("menuBar.fxml");
        Menubar.getChildren().clear();
        Menubar.getChildren().add(menuBarParent);
        EventBus.getDefault().register(this);
        warningtext.setVisible(false);
        warningtext.setVisible(false);
        dateCol1.setVisible(false);
        TimeCol1.setVisible(false);
        EmployeeName1.setVisible(false);
        ClinicCol1.setVisible(false);
        Table1.setVisible(false);
        save.setVisible(false);
        chooseLabel.setVisible(false);
        warningtext.setVisible(false);
        dateCol1.setVisible(false);
        TimeCol1.setVisible(false);
        EmployeeName1.setVisible(false);
        ClinicCol1.setVisible(false);
        Table1.setVisible(false);
        vaccinelabel.setText("Choose vaccine type");
        vaccinelabel.setVisible(true);
        VaccineType.getItems().clear();
        VaccineType.getItems().add("covid vaccine");
        VaccineType.getItems().add("influenza vaccine");
        VaccineType.setVisible(true);
        ok.setVisible(true);
        warningtext1.setVisible(false);
        try {
            Message msg = new Message();
            msg.setUsername(App.getUsername());
            msg.setAction("Get vaccine");
            SimpleClient.getClient().openConnection();
            SimpleClient.getClient().sendToServer(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Subscribe
    public void onSavedApp(SavedAppEvent event) {
        if (event.isSaved()) {
            showAlert("Saved", "The appointment was saved successfully!");
            ChangeScreens.changeToViewAppsScreen();
        } else {
            showAlert("Error", "This appointment wasn't saved please try again!");
        }
    }

    public void showAlert(String title, String head) {
        Platform.runLater(new Runnable() {
            public void run() {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(head);
                alert.showAndWait();
            }
        });
    }

    @Subscribe
    public void onClinicWithVaccineEvent(GotClinicsWithVaccine event) {
        for (String clinic : event.getClinicNames()) {
            if (!ClinicName.getItems().contains(clinic)) {
                ClinicName.getItems().add(clinic);
            }
        }
        cliniclabel.setVisible(true);
        ok.setVisible(true);
        warningtext1.setVisible(false);
    }

    @Subscribe
    public void OnGotVaccineEvent(GotVaccineEvent event) {
        isCovid_vaccine = event.isCovid_vaccine();
        isInfluenza_vaccine = event.isInfluenza_vaccine();
        covid19VaccineApp = event.getCovid19VaccineApp();
        influenzaVaccineApp = event.getInfluenzaVaccineApp();
    }
}
