package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.events.*;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

/**
 * StationMainPage
 * The main page of the appointment station.
 * Handles - lab's and nurse's appointment schedule and print ticket for existing appointment with patient number
 * and room number.
 * Also contains a logout button.
 */

public class StationMainPage {

    @FXML
    private Button printTicketBtn;

    @FXML
    private Button labAppBtn;

    @FXML
    private Button nurseAppBtn;

    @FXML
    private TextArea appText;

    @FXML
    private Button logoutBtn;

    @FXML
    void pressOnPrintTicketBtn(ActionEvent event) {
        try {
            Message msg = new Message();
            msg.setUsername(App.getUsername());
            msg.setClinicName(App.getClinic_name());
            msg.setAction("Provide Ticket");
            SimpleClient.getClient().sendToServer(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void pressOnLabAppBtn(ActionEvent event) {
        try {
            Message msg = new Message();
            msg.setUsername(App.getUsername());
            msg.setClinicName(App.getClinic_name());
            msg.setAction("newLabApp");
            SimpleClient.getClient().sendToServer(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void pressOnNurseAppBtn(ActionEvent event) {
        try {
            Message msg = new Message();
            msg.setUsername(App.getUsername());
            msg.setClinicName(App.getClinic_name());
            msg.setAction("newNurseApp");
            SimpleClient.getClient().sendToServer(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void initialize() {
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onAppointmentTicketEvent(appointmentTicketEvent event){
        System.out.println("in appointmentTicketEvent");
        Appointment app = event.getApp();
        long tickerNum = event.getTicketNum();
        if(app!=null){
            if (app instanceof doctorApp) {
                appText.setText("Welcome " + app.getPatient().getFullName() + "\nTicket number : " + tickerNum + " " + "\nDoctor: " + app.getEmployee().getFullName() + "\nRoom number " + app.getEmployee().getRoom_num());
            }
            if (app instanceof childrenDoctorApp) {
                appText.setText("Welcome " + app.getPatient().getFullName() + "\nTicket number : " + tickerNum + " " + "\nDoctor: " + app.getEmployee().getFullName() + "\nRoom number " + app.getEmployee().getRoom_num());
            }
            if (app instanceof familyDoctorApp) {
                appText.setText("Welcome " + app.getPatient().getFullName() + "\nTicket number : " + tickerNum + " " + "\nDoctor: " + app.getEmployee().getFullName() + "\nRoom number " + app.getEmployee().getRoom_num());
            }
            if (app instanceof Covid19VaccineApp) {
                appText.setText("Welcome " + app.getPatient().getFullName() + "\nTicket number : " + tickerNum + "\nRoom number " + app.getEmployee().getRoom_num());
            }
            if (app instanceof InfluenzaVaccineApp) {
                appText.setText("Welcome " + app.getPatient().getFullName() + "\nTicket number : " + tickerNum + "\nRoom number " + app.getEmployee().getRoom_num());
            }
            if (app instanceof LabApp) {
                appText.setText("Welcome " + app.getPatient().getFullName() + "\nTicket number : " + tickerNum + "\nLab Worker: " + app.getEmployee().getFullName() + "\nRoom number " + app.getEmployee().getRoom_num());
            }
            if (app instanceof NurseApp) {
                appText.setText("Welcome " + app.getPatient().getFullName() + "\nTicket number : " + tickerNum + "\nNurse: " + app.getEmployee().getFullName() + "\nRoom number " + app.getEmployee().getRoom_num());
            }
        }
        else {
            appText.setText("You don't have any appointment today.");
        }
    }

    @Subscribe
    public void gotNurseApp(nurseAppEvent event){
        try {
            Message msg = new Message();
            msg.setAppointment(event.getApp());
            msg.setAction("nurseAppCounter");
            SimpleClient.getClient().sendToServer(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void gotLabApp(labAppEvent event){
        try {
            Message msg = new Message();
            msg.setAppointment(event.getApp());
            msg.setAction("labAppCounter");
            SimpleClient.getClient().sendToServer(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void gotNurseAppCount(nurseAppCounterEvent event){
        appText.setText("Welcome "+event.getApp().getPatient().getFullName()+"\nAppointment number: "+event.getAppCount()+"\nRoom number "+event.getApp().getEmployee().getRoom_num());

    }

    @Subscribe
    public void gotLabAppCount(labAppCounterEvent event){
        appText.setText("Welcome "+event.getApp().getPatient().getFullName()+"\nAppointment number: "+event.getAppCount()+"\nRoom number "+event.getApp().getEmployee().getRoom_num());
    }

    @FXML
    void pressOnLogout(){
        App.logout(true,"Station");
    }
}
