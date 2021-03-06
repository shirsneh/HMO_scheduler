package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.swing.*;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * userController
 * helper controller that contains helper function to search users in database and finding desired data
 * of users. Also helps with log in and log out and with updating fields of users.
 */

public class userController {
    private static List<User> getAllUsersFromDB() {
        CriteriaBuilder builder = Main.session.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        query.from(User.class);
        return Main.session.createQuery(query).getResultList();
    }

    public static void getUser(Message msg) throws NoSuchAlgorithmException {
        List<User> users = getAllUsersFromDB();
        boolean user_found = false;
        boolean wrong_password = false;
        for (User user : users) {
            if (user.getUsername().equals(msg.getUsername())) {
                user_found = true;
                if (user.checkPassword(msg.getPassword())) {
                    if (!user.isLoggedIn()) {
                        if (user instanceof HMO_Manager) {
                            msg.setUserType("HMO_Manager");
                            user.setLoggedIn(true);
                            msg.setStatus("logged in");
                            msg.setUser(user);
                            msg.setFirst_name(user.getFirstName());
                        }else if (user instanceof Manager) {
                            msg.setUserType("Manager");
                            user.setLoggedIn(true);
                            msg.setStatus("logged in");
                            msg.setUser(user);
                            msg.setFirst_name(user.getFirstName());
                        } else if (user instanceof Employee) {
                            msg.setUserType("Employee");
                            user.setLoggedIn(true);
                            msg.setStatus("logged in");
                            msg.setUser(user);
                            msg.setFirst_name(user.getFirstName());
                        } else if (user instanceof Patient) {
                            msg.setUserType("Patient");
                            user.setLoggedIn(true);
                            msg.setStatus("logged in");
                            msg.setUser(user);
                            msg.setFirst_name(user.getFirstName());
                        }
                    } else {
                        msg.setStatus("you are already logged in");
                    }
                } else {
                    wrong_password = true;
                    break;
                }
            }
        }
        if (!user_found || wrong_password) {
            msg.setStatus("Wrong username or password");
        }
    }

    public static void getUserWithCardNumber(Message msg) throws NoSuchAlgorithmException {
        List<User> users = getAllUsersFromDB();
        boolean user_found = false;
        for (User user : users) {
            if (user.checkCard(msg.getUserCardNumber())) {
                user_found = true;
                if (!user.isLoggedIn()) {
                    if(user instanceof HMO_Manager)
                    {
                        msg.setUserType("HMO_Manager");
                        user.setLoggedIn(true);
                        msg.setStatus("logged in");
                        msg.setUser(user);
                    }else if (user instanceof Manager) {
                        msg.setUserType("Manager");
                        user.setLoggedIn(true);
                        msg.setStatus("logged in");
                        msg.setUser(user);
                    } else if (user instanceof Employee) {
                        msg.setUserType("Employee");
                        user.setLoggedIn(true);
                        msg.setStatus("logged in");
                        msg.setUser(user);
                    }else if (user instanceof Patient) {
                        msg.setUserType("Patient");
                        user.setLoggedIn(true);
                        msg.setStatus("logged in");
                        msg.setUser(user);
                    }
                } else {
                    msg.setStatus("you are already logged in");
                }
            }
        }
        if(!user_found) {
            msg.setStatus("Wrong CardNumber");
        }
    }

    public static void logOut(Message msg) throws NoSuchAlgorithmException {
        List<User> users = getAllUsersFromDB();
        boolean user_found = false;
        for (User user : users) {
            if (user.getUsername().equals(msg.getUsername())) {
                user_found = true;
                if (user.isLoggedIn()) {
                    user.setLoggedIn(false);
                    msg.setStatus("logout");
                    msg.setUser(user);
                } else {
                    msg.setStatus("you are already logged out");
                }
            }
        }
        if(!user_found) {
            msg.setStatus("can't find user");
        }
    }

    public static List<Employee> getAllEmployeesFromDB(){
        CriteriaBuilder builder = Main.session.getCriteriaBuilder();
        CriteriaQuery<Employee> query = builder.createQuery(Employee.class);
        query.from(Employee.class);
        return Main.session.createQuery(query).getResultList();
    }

    public static Employee getEmployee(int employeeId) {
        List<Employee> employees = getAllEmployeesFromDB();
        for (Employee employee : employees) {
            if (employee.getUserId() == employeeId) {
                return employee;
            }
        }
        return null;
    }
    public static Employee getEmployeeFromUserName(String username) {           //new
        List<Employee> employees = getAllEmployeesFromDB();
        for (Employee employee : employees) {
            if (employee.getUsername().equals(username)) {
                return employee;
            }
        }
        return null;
    }
    public static List<Manager> getAllManagersFromDB(){
        CriteriaBuilder builder = Main.session.getCriteriaBuilder();
        CriteriaQuery<Manager> query = builder.createQuery(Manager.class);
        query.from(User.class);
        return Main.session.createQuery(query).getResultList();
    }

    public static Manager getManagerById(int id) {
        List<Manager> managers = getAllManagersFromDB();
        for (Manager manager : managers) {
            if (manager.getUserId() == id) {
                return manager;
            }
        }
        return null;
    }

    public static Manager getManagerByUserName(String name) {
        List<Manager> managers = getAllManagersFromDB();
        for (Manager manager : managers) {
            if (manager.getUsername() == name) {
                return manager;
            }
        }
        return null;
    }


    public static User getUserByUsername(String username) {
        CriteriaBuilder builder = Main.session.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query.select(root);
        query.where(builder.equal(root.get("username"), username));
        return Main.session.createQuery(query).getSingleResult();
    }

    public static Manager getManagerByUsername(String username) {
        CriteriaBuilder builder = Main.session.getCriteriaBuilder();
        CriteriaQuery<Manager> query = builder.createQuery(Manager.class);
        Root<Manager> root = query.from(Manager.class);
        query.select(root);
        query.where(builder.equal(root.get("username"), username));
        return Main.session.createQuery(query).getSingleResult();
    }

    public static List<String> getAllManagersNamesFromDB(){
        CriteriaBuilder builder = Main.session.getCriteriaBuilder();
        CriteriaQuery<Manager> query = builder.createQuery(Manager.class);
        Root<Manager> root = query.from(Manager.class);
        query.select(root);
        return Main.session.createQuery(query).getResultList().stream()
                .map((Manager::getFullName)).collect(Collectors.toList());
    }

    public static User getUserById(int id) {
        CriteriaBuilder builder = Main.session.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query.select(root);
        query.where(builder.equal(root.get("user_id"), id));
        return Main.session.createQuery(query).getSingleResult();
    }

    public static Employee getEmployeeByFullName(String name){
        String[] str = name.split(" ");
        CriteriaBuilder builder = Main.session.getCriteriaBuilder();
        CriteriaQuery<Employee> query = builder.createQuery(Employee.class);
        Root<Employee> root = query.from(Employee.class);
        query.select(root);
        query.where(builder.equal(root.get("first_name"), str[0]), builder.equal(root.get("last_name"), str[1]));
        return Main.session.createQuery(query).getSingleResult();
    }

    public static Patient getPatientByCardNum(String card_num) {
        CriteriaBuilder builder = Main.session.getCriteriaBuilder();
        CriteriaQuery<Patient> query = builder.createQuery(Patient.class);
        Root<Patient> root = query.from(Patient.class);
        query.select(root);
        query.where(builder.equal(root.get("card_num"), card_num));
        return Main.session.createQuery(query).getSingleResult();
    }

    public static Doctor getDoctorByName(String name) {
        CriteriaBuilder builder = Main.session.getCriteriaBuilder();
        CriteriaQuery<Doctor> query = builder.createQuery(Doctor.class);
        Root<Doctor> root = query.from(Doctor.class);
        query.select(root);
        String[] nameArr = name.split(" ");
        query.where(builder.equal(root.get("first_name"), nameArr[0]), builder.equal(root.get("last_name"), nameArr[1]));
        return Main.session.createQuery(query).getSingleResult();
    }

    public static List<Nurse> getNursesByClinic(String clinic_name) {
        CriteriaBuilder builder = Main.session.getCriteriaBuilder();
        CriteriaQuery<Nurse> query = builder.createQuery(Nurse.class);
        Root<Nurse> root = query.from(Nurse.class);
        query.select(root);
        query.where(builder.equal(root.get("main_clinic"), clinic_name));
        return Main.session.createQuery(query).getResultList();
    }

    public static List<LabWorker> getLabWorkersByClinic(String clinic_name) {
        CriteriaBuilder builder = Main.session.getCriteriaBuilder();
        CriteriaQuery<LabWorker> query = builder.createQuery(LabWorker.class);
        Root<LabWorker> root = query.from(LabWorker.class);
        query.select(root);
        query.where(builder.equal(root.get("main_clinic"), clinic_name));
        return Main.session.createQuery(query).getResultList();
    }

    public static Patient getPatientByUsername(String username) {
        CriteriaBuilder builder = Main.session.getCriteriaBuilder();
        CriteriaQuery<Patient> query = builder.createQuery(Patient.class);
        Root<Patient> root = query.from(Patient.class);
        query.select(root);
        query.where(builder.equal(root.get("username"), username));
        return Main.session.createQuery(query).getSingleResult();
    }

    public static List<Doctor> getDoctorsByRole(String role, String clinicName) {
        CriteriaBuilder builder = Main.session.getCriteriaBuilder();
        CriteriaQuery<Doctor> query = builder.createQuery(Doctor.class);
        Root<Doctor> root = query.from(Doctor.class);
        query.select(root);
        query.where(builder.equal(root.get("role"), role));
        List<Doctor> doctorList = Main.session.createQuery(query).getResultList();
        doctorList.removeIf(doctor -> !doctor.getMain_clinic().equals(clinicName));
        return doctorList;
    }

    public static SpecialDoctor getSpecialDoctorByUsername(String username) {
        CriteriaBuilder builder = Main.session.getCriteriaBuilder();
        CriteriaQuery<SpecialDoctor> query = builder.createQuery(SpecialDoctor.class);
        Root<SpecialDoctor> root = query.from(SpecialDoctor.class);
        query.select(root);
        query.where(builder.equal(root.get("username"), username));
        return Main.session.createQuery(query).getSingleResult();
    }

    public static List<SpecialDoctor> getSpecialDoctor(String role, Patient patient) {
        CriteriaBuilder builder = Main.session.getCriteriaBuilder();
        CriteriaQuery<SpecialDoctor> query = builder.createQuery(SpecialDoctor.class);
        Root<SpecialDoctor> root = query.from(SpecialDoctor.class);
        query.select(root);
        query.where(builder.equal(root.get("role"), role));
        List<SpecialDoctor> specialDoctorList = Main.session.createQuery(query).getResultList();
        List<specialDoctorApp> appList = appointmentController.getSpecialPatientApps(patient);
        if (appList != null && !appList.isEmpty()) {
            for (specialDoctorApp doctorApp : appList) {
                if (doctorApp.getSpecialDoctor().getRole().equals(role)) {
                    specialDoctorList.remove(doctorApp.getSpecialDoctor());
                    specialDoctorList.add(0, doctorApp.getSpecialDoctor());
                }
            }
        }
        return specialDoctorList;
    }

    public static List<LabWorker> getLabWorkers(String clinicName) {
        CriteriaBuilder builder = Main.session.getCriteriaBuilder();
        CriteriaQuery<LabWorker> query = builder.createQuery(LabWorker.class);
        Root<LabWorker> root = query.from(LabWorker.class);
        query.select(root);
        query.where(builder.equal(root.get("main_clinic"), clinicName));
        return Main.session.createQuery(query).getResultList();
    }

    public static void addQuestionnaire(Patient patient, boolean met, boolean fever, boolean cough, boolean tired, boolean taste, boolean smell) {
        CovidQuestionnaire questionnaire = new CovidQuestionnaire(patient, met, fever, cough, tired, taste, smell);
        Main.session.save(questionnaire);
        Main.session.flush();
    }

    public static GreenPass getUserGreenPass(User user) {
        CriteriaBuilder builder = Main.session.getCriteriaBuilder();
        CriteriaQuery<GreenPass> query = builder.createQuery(GreenPass.class);
        Root<GreenPass> root = query.from(GreenPass.class);
        query.select(root);
        query.where(builder.equal(root.get("user"), user));
        query.orderBy(builder.desc(root.get("issue_date")));
        List<GreenPass> greenPass = Main.session.createQuery(query).getResultList();
        if (greenPass != null)
            if (!greenPass.isEmpty())
                if (greenPass.get(0).getExpiration_date().isAfter(LocalDate.now()))
                    return greenPass.get(0);
        List<Covid19VaccineApp> covid19VaccineApp = appointmentController.getVaccineApp(user.getUsername());
        if (covid19VaccineApp == null)
            return null;
        if (covid19VaccineApp.isEmpty())
            return null;
        if (covid19VaccineApp.get(0).getDate().isBefore(LocalDate.now().minusYears(1)))
            return null;
        GreenPass greenPass1 = new GreenPass(user, covid19VaccineApp.get(0).getDate(), covid19VaccineApp.get(0).getDate().plusYears(1));
        Main.session.save(greenPass1);
        Main.session.flush();
        return greenPass1;
    }

}





