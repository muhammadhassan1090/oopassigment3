package rpmsapp;

// Importing necessary libraries for email sending, user input
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Properties;

// Custom exception for vital sign threshold violations
class VitalThresholdException extends Exception {
    public VitalThresholdException(String message) {
        super(message);
    }
}

// Custom exception for notification-related errors
class NotificationException extends Exception {
    public NotificationException(String message) {
        super(message);
    }
}

// Base class for all users (patients, doctors, administrators) with common attributes
class User {
    private String id;      // Unique identifier for the user
    private String name;    // User's name
    private String email;   // User's email address
    private String password;// User's password for authentication

    // Constructor to initialize a user
    public User(String id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Getters and setters for user attributes
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // Displays user information
    public void displayInfo() {
        System.out.println("User ID: " + id + " | Name: " + name + " | Email: " + email);
    }
}

// Patient class, inherits from User, manages patient-specific data
class Patient extends User {
    private MedicalHistory medicalHistory; // Stores patient's medical history

    // Constructor to initialize a patient
    public Patient(String id, String name, String email, String password) {
        super(id, name, email, password);
        medicalHistory = new MedicalHistory();
    }

    // Overrides displayInfo to include patient role
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Role: Patient");
    }

    // Uploads vital signs to the database for monitoring
    public void uploadVitals(VitalsDatabase db, VitalSign vital) {
        db.addVitalSign(getId(), vital);
        System.out.println("Vitals uploaded successfully for patient " + getName());
    }

    // Adds feedback to the patient's medical history
    public void addFeedback(Feedback feedback) {
        medicalHistory.addFeedback(feedback);
    }

    // Displays the patient's medical history
    public void viewMedicalHistory() {
        System.out.println("Medical History for " + getName() + ":");
        medicalHistory.displayHistory();
    }

    // Getter for medical history
    public MedicalHistory getMedicalHistory() { return medicalHistory; }
}

// Doctor class, inherits from User, manages doctor-specific data
class Doctor extends User {
    private List<Patient> patients; // List of patients assigned to the doctor

    // Constructor to initialize a doctor
    public Doctor(String id, String name, String email, String password) {
        super(id, name, email, password);
        patients = new ArrayList<>();
    }

    // Overrides displayInfo to include doctor role and patient count
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Role: Doctor | Number of Patients: " + patients.size());
    }

    // Getter for the list of patients
    public List<Patient> getPatients() { return patients; }

    // Adds a patient to the doctor's list if not already present
    public void addPatient(Patient patient) {
        if (!patients.contains(patient)) {
            patients.add(patient);
            System.out.println("Patient " + patient.getName() + " added to Dr. " + getName() + "'s list.");
        }
    }

    // Provides feedback and prescription for a patient
    public Feedback provideFeedback(String feedbackText, Prescription prescription) {
        Feedback feedback = new Feedback(feedbackText, prescription);
        System.out.println("Feedback provided by Dr. " + getName());
        return feedback;
    }
}

// Administrator class, inherits from User, manages admin-specific actions
class Administrator extends User {
    // Constructor to initialize an administrator
    public Administrator(String id, String name, String email, String password) {
        super(id, name, email, password);
    }

    // Overrides displayInfo to include admin role
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Role: Administrator");
    }
}

// Class to store a patient's vital signs
class VitalSign {
    private int heartRate;      // Heart rate in beats per minute
    private int oxygenLevel;    // Oxygen saturation percentage
    private String bloodPressure; // Blood pressure in format "systolic/diastolic"
    private double temperature; // Body temperature in Celsius

    // Constructor to initialize vital signs
    public VitalSign(int heartRate, int oxygenLevel, String bloodPressure, double temperature) {
        this.heartRate = heartRate;
        this.oxygenLevel = oxygenLevel;
        this.bloodPressure = bloodPressure;
        this.temperature = temperature;
    }

    // Getters for vital sign attributes
    public int getHeartRate() { return heartRate; }
    public int getOxygenLevel() { return oxygenLevel; }
    public String getBloodPressure() { return bloodPressure; }
    public double getTemperature() { return temperature; }

    // Displays the vital signs
    public void displayVitals() {
        System.out.println("Heart Rate: " + heartRate + " bpm, Oxygen Level: " + oxygenLevel +
                           "%, Blood Pressure: " + bloodPressure + ", Temperature: " + temperature + " °C");
    }
}

// Class to store a single vital sign record for a patient
class VitalRecord {
    private String patientId;   // ID of the patient
    private VitalSign vitalSign;// Vital sign data

    // Constructor to initialize a vital record
    public VitalRecord(String patientId, VitalSign vitalSign) {
        this.patientId = patientId;
        this.vitalSign = vitalSign;
    }

    // Getters for vital record attributes
    public String getPatientId() { return patientId; }
    public VitalSign getVitalSign() { return vitalSign; }
}

// Class to manage a database of vital sign records
class VitalsDatabase {
    private List<VitalRecord> vitalRecords; // List of vital records

    // Constructor to initialize the database
    public VitalsDatabase() {
        vitalRecords = new ArrayList<>();
    }

    // Adds a vital sign record to the database
    public void addVitalSign(String patientId, VitalSign vital) {
        vitalRecords.add(new VitalRecord(patientId, vital));
    }

    // Displays all vital signs for a given patient
    public void displayPatientVitals(String patientId) {
        boolean found = false;
        System.out.println("Vitals for patient ID " + patientId + ":");
        for (VitalRecord record : vitalRecords) {
            if (record.getPatientId().equalsIgnoreCase(patientId)) {
                record.getVitalSign().displayVitals();
                found = true;
            }
        }
        if (!found) {
            System.out.println("No vitals recorded for patient with ID " + patientId);
        }
    }
}

// Class to represent an appointment between a doctor and a patient
class Appointment {
    private String appointmentDate; // Date of the appointment
    private Doctor doctor;         // Doctor for the appointment
    private Patient patient;       // Patient for the appointment
    private String status;         // Status of the appointment (Requested, Approved, Cancelled)

    // Constructor to initialize an appointment
    public Appointment(String appointmentDate, Doctor doctor, Patient patient, String status) {
        this.appointmentDate = appointmentDate;
        this.doctor = doctor;
        this.patient = patient;
        this.status = status;
    }

    // Getters for appointment attributes
    public String getAppointmentDate() { return appointmentDate; }
    public Doctor getDoctor() { return doctor; }
    public Patient getPatient() { return patient; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Displays appointment details
    public void displayAppointment() {
        System.out.println("Appointment Date: " + appointmentDate + " | Doctor: " + doctor.getName() +
                           " | Patient: " + patient.getName() + " | Status: " + status);
    }
}

// Class to manage appointments
class AppointmentManager {
    private List<Appointment> appointments; // List of all appointments

    // Constructor to initialize the appointment manager
    public AppointmentManager() {
        appointments = new ArrayList<>();
    }

    // Requests a new appointment
    public void requestAppointment(String date, Doctor doctor, Patient patient) {
        appointments.add(new Appointment(date, doctor, patient, "Requested"));
        System.out.println("Appointment requested on " + date + " for patient " + patient.getName());
    }

    // Approves an appointment by index
    public void approveAppointment(int index) {
        if (index >= 0 && index < appointments.size()) {
            appointments.get(index).setStatus("Approved");
            System.out.println("Appointment approved.");
        } else {
            System.out.println("Invalid appointment index.");
        }
    }

    // Cancels an appointment by index
    public void cancelAppointment(int index) {
        if (index >= 0 && index < appointments.size()) {
            appointments.get(index).setStatus("Cancelled");
            System.out.println("Appointment cancelled.");
        } else {
            System.out.println("Invalid appointment index.");
        }
    }

    // Getter for the list of appointments
    public List<Appointment> getAppointments() { return appointments; }

    // Displays all appointments
    public void displayAppointments() {
        if (appointments.isEmpty()) {
            System.out.println("No appointments scheduled.");
        } else {
            for (int i = 0; i < appointments.size(); i++) {
                System.out.println("Index: " + i);
                appointments.get(i).displayAppointment();
            }
        }
    }
}

// Class to represent a prescription for a patient
class Prescription {
    private String medication; // Name of the medication
    private String dosage;    // Dosage instructions
    private String schedule;  // Schedule for taking the medication
    private Patient patient;  // Patient associated with the prescription

    // Constructor to initialize a prescription
    public Prescription(String medication, String dosage, String schedule, Patient patient) {
        this.medication = medication;
        this.dosage = dosage;
        this.schedule = schedule;
        this.patient = patient;
    }

    // Getters for prescription attributes
    public String getMedication() { return medication; }
    public String getDosage() { return dosage; }
    public String getSchedule() { return schedule; }
    public Patient getPatient() { return patient; }

    // Displays prescription details
    public void displayPrescription() {
        System.out.println("Medication: " + medication + " | Dosage: " + dosage + " | Schedule: " + schedule);
    }
}

// Class to store feedback from a doctor, optionally with a prescription
class Feedback {
    private String feedbackText; // Text of the feedback
    private Prescription prescription; // Associated prescription, if any

    // Constructor to initialize feedback
    public Feedback(String feedbackText, Prescription prescription) {
        this.feedbackText = feedbackText;
        this.prescription = prescription;
    }

    // Getter for the prescription
    public Prescription getPrescription() { return prescription; }

    // Displays feedback and associated prescription
    public void displayFeedback() {
        System.out.println("Feedback: " + feedbackText);
        if (prescription != null) {
            System.out.println("Prescription:");
            prescription.displayPrescription();
        }
    }
}

// Class to manage a patient's medical history
class MedicalHistory {
    private List<Feedback> feedbackRecords; // List of feedback records

    // Constructor to initialize medical history
    public MedicalHistory() {
        feedbackRecords = new ArrayList<>();
    }

    // Adds feedback to the medical history
    public void addFeedback(Feedback feedback) {
        feedbackRecords.add(feedback);
    }

    // Getter for feedback records
    public List<Feedback> getFeedbackRecords() { return feedbackRecords; }

    // Displays all feedback in the medical history
    public void displayHistory() {
        if (feedbackRecords.isEmpty()) {
            System.out.println("No medical history available.");
        } else {
            for (Feedback f : feedbackRecords) {
                f.displayFeedback();
            }
        }
    }
}

// Interface for sending notifications (e.g., email, SMS)
interface Notifiable {
    void sendNotification(String message, String recipient) throws NotificationException;
}

// Class to send email notifications using Jakarta Mail
class EmailNotification implements Notifiable {
    private String smtpHost = "smtp.gmail.com"; // SMTP server host (Gmail)
    private String smtpPort = "587";            // SMTP server port
    private String username;                    // SMTP username (email address)
    private String password;                    // SMTP password (or app-specific password)

    // Constructor to initialize email notification with credentials
    public EmailNotification(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Sends a notification email to the recipient
    @Override
    public void sendNotification(String message, String recipient) throws NotificationException {
        if (recipient == null || recipient.isEmpty()) {
            throw new NotificationException("Invalid recipient for email.");
        }
        if (username == null || password == null) {
            throw new NotificationException("SMTP username or password not provided.");
        }
        sendEmail(message, recipient, username, password);
    }

    // Configures and sends the email using Jakarta Mail
    private void sendEmail(String message, String recipient, String username, String password) throws NotificationException {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Create a session with authentication
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create and send the email message
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(username));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            mimeMessage.setSubject("RPMS Notification");
            mimeMessage.setText(message);
            Transport.send(mimeMessage);
            System.out.println("Real email sent to " + recipient + ": " + message);
        } catch (MessagingException e) {
            throw new NotificationException("Error sending email: " + e.getMessage());
        }
    }
}

// Class to send SMS notifications (simulated by console output)
class SMSNotification implements Notifiable {
    // Sends an SMS notification to the recipient
    @Override
    public void sendNotification(String message, String recipient) throws NotificationException {
        if (recipient == null || recipient.isEmpty()) {
            throw new NotificationException("Invalid recipient for SMS.");
        }
        System.out.println("SMS sent to " + recipient + ": " + message);
    }
}

// Service class to handle sending notifications
class NotificationService {
    private Notifiable notifier; // Notifier implementation (e.g., EmailNotification)
    private String recipient;   // Recipient of the notification

    // Constructor to initialize the notification service
    public NotificationService(Notifiable notifier, String recipient) {
        this.notifier = notifier;
        this.recipient = recipient;
    }

    // Sends an alert using the configured notifier
    public void sendAlert(String message) throws NotificationException {
        if (notifier == null) {
            throw new NotificationException("Notification service not configured.");
        }
        notifier.sendNotification(message, recipient);
    }
}

// Interface for triggering alerts
interface Alertable {
    void triggerAlert(String message) throws NotificationException;
}

// Class to handle emergency alerts based on vital signs
class EmergencyAlert implements Alertable {
    private VitalSign vital;               // Vital sign to check
    private NotificationService notificationService; // Service to send alerts
    private Patient patient;               // Patient associated with the alert

    // Constructor to initialize an emergency alert
    public EmergencyAlert(Patient patient, VitalSign vital, NotificationService notificationService) {
        this.patient = patient;
        this.vital = vital;
        this.notificationService = notificationService;
    }

    // Checks if vital signs are within normal thresholds
    public static boolean isWithinThreshold(VitalSign vital) {
        if (vital == null) return true;
        try {
            String[] bpParts = vital.getBloodPressure().split("/");
            if (bpParts.length != 2) {
                return false;
            }
            double systolic = Double.parseDouble(bpParts[0]);
            double diastolic = Double.parseDouble(bpParts[1]);
            
            // Define normal ranges for vital signs
            boolean heartRateOk = vital.getHeartRate() >= 60 && vital.getHeartRate() <= 100;
            boolean oxygenOk = vital.getOxygenLevel() >= 95;
            boolean bpOk = systolic >= 90 && systolic <= 140 && diastolic >= 60 && diastolic <= 90;
            boolean tempOk = vital.getTemperature() >= 36.1 && vital.getTemperature() <= 37.2;
            
            return heartRateOk && oxygenOk && bpOk && tempOk;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Checks vital signs and triggers an alert if abnormal
    public void checkVitals() throws VitalThresholdException, NotificationException {
        if (vital == null || patient == null) {
            throw new VitalThresholdException("Vital or patient information missing.");
        }
        if (!isWithinThreshold(vital)) {
            String message = "Alert! Patient " + patient.getId() + "'s vital signs are abnormal: " +
                             "HR=" + vital.getHeartRate() + ", O2=" + vital.getOxygenLevel() +
                             ", BP=" + vital.getBloodPressure() + ", Temp=" + vital.getTemperature();
            triggerAlert(message);
        }
    }

    // Triggers the alert using the notification service
    @Override
    public void triggerAlert(String message) throws NotificationException {
        notificationService.sendAlert(message);
    }
}

// Class to handle panic button alerts
class PanicButton implements Alertable {
    private Patient patient;               // Patient triggering the alert
    private NotificationService notificationService; // Service to send alerts
    private Doctor doctor;                 // Doctor to be notified

    // Constructor to initialize a panic button
    public PanicButton(Patient patient, Doctor doctor, NotificationService notificationService) {
        this.patient = patient;
        this.doctor = doctor;
        this.notificationService = notificationService;
    }

    // Triggers an emergency alert
    @Override
    public void triggerAlert(String message) throws NotificationException {
        if (patient == null || doctor == null) {
            throw new NotificationException("Patient or doctor information missing.");
        }
        message = "Emergency! Patient " + patient.getId() + " needs immediate attention.";
        notificationService.sendAlert(message);
        System.out.println("Doctor " + doctor.getName() + " notified of panic alert.");
    }

    // Simulates pressing the panic button
    public void pressPanicButton() throws NotificationException {
        triggerAlert("");
    }
}

// Server class to handle chat between doctor and patient
class ChatServer {
    private List<String> messages; // List of chat messages
    private Doctor doctor;         // Doctor in the chat
    private Patient patient;       // Patient in the chat

    // Constructor to initialize the chat server
    public ChatServer(Doctor doctor, Patient patient) {
        this.messages = new ArrayList<>();
        this.doctor = doctor;
        this.patient = patient;
    }

    // Adds a message to the chat
    public void sendMessage(String sender, String message) {
        messages.add(sender + ": " + message);
    }

    // Returns a copy of the chat messages
    public List<String> getMessages() { return new ArrayList<>(messages); }
}

// Client class for chat functionality
class ChatClient {
    private User user;        // User participating in the chat
    private ChatServer server;// Chat server instance

    // Constructor to initialize the chat client
    public ChatClient(User user, ChatServer server) {
        this.user = user;
        this.server = server;
    }

    // Sends a message through the server
    public void sendMessage(String message) {
        server.sendMessage(user.getName(), message);
    }

    // Displays all messages in the chat
    public void displayMessages() {
        for (String msg : server.getMessages()) {
            System.out.println(msg);
        }
    }
}

// Class to handle video calls between doctor and patient
class VideoCall {
    private String meetingLink; // Link for the video call
    private Doctor doctor;      // Doctor in the call
    private Patient patient;    // Patient in the call

    // Constructor to initialize a video call
    public VideoCall(Doctor doctor, Patient patient, String meetingLink) {
        this.doctor = doctor;
        this.patient = patient;
        this.meetingLink = meetingLink;
    }

    // Starts the video call and returns the meeting link
    public String startCall() throws Exception {
        if (doctor == null || patient == null) {
            throw new Exception("Invalid doctor or patient for video call.");
        }
        return "Video call started: " + meetingLink;
    }
}

// Service class to handle sending reminders
class ReminderService {
    private List<Appointment> appointments; // List of appointments
    private List<Prescription> prescriptions; // List of prescriptions
    private Notifiable notifier;           // Notifier for sending reminders

    // Constructor to initialize the reminder service
    public ReminderService(Notifiable notifier) {
        this.appointments = new ArrayList<>();
        this.prescriptions = new ArrayList<>();
        this.notifier = notifier;
    }

    // Adds an appointment to the reminder list
    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    // Adds a prescription to the reminder list
    public void addPrescription(Prescription prescription) {
        prescriptions.add(prescription);
    }

    // Sends reminders for approved appointments
    public void sendAppointmentReminder() throws NotificationException {
        for (Appointment a : appointments) {
            if ("Approved".equals(a.getStatus())) {
                String message = "Reminder: Appointment with Dr. " + a.getDoctor().getName() +
                                 " on " + a.getAppointmentDate();
                notifier.sendNotification(message, a.getPatient().getEmail());
            }
        }
    }

    // Sends reminders for medication schedules
    public void sendMedicationReminder() throws NotificationException {
        for (Prescription p : prescriptions) {
            String message = "Reminder: Take " + p.getMedication() + " (" + p.getDosage() +
                             ") as per schedule: " + p.getSchedule();
            notifier.sendNotification(message, p.getPatient().getEmail());
        }
    }
}

// Main application class for the Remote Patient Monitoring System
public class RPMSApp {
    private List<Patient> patients;         // List of all patients
    private List<Doctor> doctors;           // List of all doctors
    private List<Administrator> admins;     // List of all administrators
    private VitalsDatabase vitalsDB;        // Database for vital signs
    private AppointmentManager appointmentManager; // Manager for appointments
    private Scanner sc;                     // Scanner for user input
    private User currentUser;               // Currently logged-in user
    private String smtpUsername;            // SMTP username for email notifications
    private String smtpPassword;            // SMTP password for email notifications

    // Constructor to initialize the RPMS application
    public RPMSApp() {
        patients = new ArrayList<>();
        doctors = new ArrayList<>();
        admins = new ArrayList<>();
        vitalsDB = new VitalsDatabase();
        appointmentManager = new AppointmentManager();
        sc = new Scanner(System.in);
        setupSMTPCredentials(); // Prompt for SMTP credentials at startup
    }

    // Prompts the user to enter SMTP credentials for email notifications
    private void setupSMTPCredentials() {
        System.out.println("--- SMTP Configuration ---");
        System.out.println("Enter SMTP Username (e.g., your-email@gmail.com): ");
        smtpUsername = sc.nextLine();
        System.out.println("Enter SMTP Password (e.g., your app-specific password for Gmail): ");
        smtpPassword = sc.nextLine();
        System.out.println("SMTP credentials configured successfully.");
    }

    // Handles user login by verifying credentials
    private boolean login() {
        System.out.println("Enter User ID: ");
        String id = sc.nextLine();
        System.out.println("Enter Password: ");
        String password = sc.nextLine();

        // Check if the user is a patient
        for (Patient p : patients) {
            if (p.getId().equals(id) && p.getPassword().equals(password)) {
                currentUser = p;
                System.out.println("Logged in as Patient: " + p.getName());
                return true;
            }
        }
        // Check if the user is a doctor
        for (Doctor d : doctors) {
            if (d.getId().equals(id) && d.getPassword().equals(password)) {
                currentUser = d;
                System.out.println("Logged in as Doctor: " + d.getName());
                return true;
            }
        }
        // Check if the user is an administrator
        for (Administrator a : admins) {
            if (a.getId().equals(id) && a.getPassword().equals(password)) {
                currentUser = a;
                System.out.println("Logged in as Administrator: " + a.getName());
                return true;
            }
        }
        System.out.println("Invalid ID or password.");
        return false;
    }

    // Main loop to run the application
    public void run() {
        System.out.println("--- Welcome to RPMS ---");
        while (true) {
            System.out.println("\n1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    register(); // Register a new user
                    break;
                case 2:
                    if (login()) { // Log in and redirect to appropriate menu
                        if (currentUser instanceof Patient) {
                            patientMenuLoop();
                        } else if (currentUser instanceof Doctor) {
                            doctorMenuLoop();
                        } else if (currentUser instanceof Administrator) {
                            adminMenuLoop();
                        }
                    } else {
                        System.out.println("Please try logging in again.");
                    }
                    break;
                case 3:
                    System.out.println("Exiting system. Goodbye!");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // Handles user registration
    private void register() {
        System.out.println("\n--- Register ---");
        System.out.println("1. Register as Patient");
        System.out.println("2. Register as Doctor");
        System.out.println("3. Register as Administrator");
        System.out.print("Enter your choice: ");

        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        switch (choice) {
            case 1:
                addPatient(); // Register a new patient
                break;
            case 2:
                addDoctor(); // Register a new doctor
                break;
            case 3:
                addAdministrator(); // Register a new administrator
                break;
            default:
                System.out.println("Invalid choice.");
        }
        System.out.println("Registration complete. Please login to continue.");
    }

    // Menu loop for patients
    private void patientMenuLoop() {
        while (true) {
            System.out.println("\n--- Patient Menu ---");
            System.out.println("1. Upload Vitals");
            System.out.println("2. View Medical History");
            System.out.println("3. Schedule Appointment");
            System.out.println("4. Start Chat");
            System.out.println("5. Start Video Call");
            System.out.println("6. Trigger Panic Button");
            System.out.println("7. Logout");
            System.out.print("Enter your choice: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1: uploadVitals(); break;
                case 2: viewMedicalHistory(); break;
                case 3: scheduleAppointment(); break;
                case 4: startChat(); break;
                case 5: startVideoCall(); break;
                case 6: triggerPanicButton(); break;
                case 7: return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    // Menu loop for doctors
    private void doctorMenuLoop() {
        while (true) {
            System.out.println("\n--- Doctor Menu ---");
            System.out.println("1. Provide Feedback");
            System.out.println("2. Approve/Cancel Appointment");
            System.out.println("3. Start Chat");
            System.out.println("4. Start Video Call");
            System.out.println("5. Send Reminders");
            System.out.println("6. Logout");
            System.out.print("Enter your choice: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1: doctorFeedback(); break;
                case 2: modifyAppointment(); break;
                case 3: startChat(); break;
                case 4: startVideoCall(); break;
                case 5: sendReminders(); break;
                case 6: return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    // Menu loop for administrators
    private void adminMenuLoop() {
        while (true) {
            System.out.println("\n--- Administrator Menu ---");
            System.out.println("1. Add Patient");
            System.out.println("2. Add Doctor");
            System.out.println("3. Add Administrator");
            System.out.println("4. View All Appointments");
            System.out.println("5. Display All User Information");
            System.out.println("6. Send Reminders");
            System.out.println("7. Logout");
            System.out.print("Enter your choice: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1: addPatient(); break;
                case 2: addDoctor(); break;
                case 3: addAdministrator(); break;
                case 4: appointmentManager.displayAppointments(); break;
                case 5: displayAllUserInfo(); break;
                case 6: sendReminders(); break;
                case 7: return;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    // Adds a new patient to the system
    private void addPatient() {
        System.out.println("Enter Patient ID: ");
        String id = sc.nextLine();
        System.out.println("Enter Patient Name: ");
        String name = sc.nextLine();
        System.out.println("Enter Patient Email: ");
        String email = sc.nextLine();
        System.out.println("Enter Patient Password: ");
        String password = sc.nextLine();
        patients.add(new Patient(id, name, email, password));
        System.out.println("Patient " + name + " added.");
    }

    // Adds a new doctor to the system
    private void addDoctor() {
        System.out.println("Enter Doctor ID: ");
        String id = sc.nextLine();
        System.out.println("Enter Doctor Name: ");
        String name = sc.nextLine();
        System.out.println("Enter Doctor Email: ");
        String email = sc.nextLine();
        System.out.println("Enter Doctor Password: ");
        String password = sc.nextLine();
        doctors.add(new Doctor(id, name, email, password));
        System.out.println("Doctor " + name + " added.");
    }

    // Adds a new administrator to the system
    private void addAdministrator() {
        System.out.println("Enter Administrator ID: ");
        String id = sc.nextLine();
        System.out.println("Enter Administrator Name: ");
        String name = sc.nextLine();
        System.out.println("Enter Administrator Email: ");
        String email = sc.nextLine();
        System.out.println("Enter Administrator Password: ");
        String password = sc.nextLine();
        admins.add(new Administrator(id, name, email, password));
        System.out.println("Administrator " + name + " added.");
    }

    // Allows a patient to upload vital signs and checks for abnormalities
    private void uploadVitals() {
        Patient patient = (Patient) currentUser;
        String patientId = patient.getId();
        try {
            System.out.println("Enter Heart Rate (bpm): ");
            int heartRate = Integer.parseInt(sc.nextLine());
            System.out.println("Enter Oxygen Level (%): ");
            int oxygenLevel = Integer.parseInt(sc.nextLine());
            System.out.println("Enter Blood Pressure (e.g., 120/80): ");
            String bp = sc.nextLine();
            String[] bpParts = bp.split("/");
            if (bpParts.length != 2) {
                System.out.println("Invalid blood pressure format. Please use 'systolic/diastolic'.");
                return;
            }
            try {
                Double.parseDouble(bpParts[0]);
                Double.parseDouble(bpParts[1]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid blood pressure values. Please enter numbers.");
                return;
            }
            System.out.println("Enter Temperature (°C): ");
            double temp = Double.parseDouble(sc.nextLine());
            
            // Create a new vital sign record
            VitalSign vital = new VitalSign(heartRate, oxygenLevel, bp, temp);
            patient.uploadVitals(vitalsDB, vital);
            vitalsDB.displayPatientVitals(patientId);
            
            // Find doctors associated with the patient
            List<Doctor> associatedDoctors = new ArrayList<>();
            for (Doctor d : doctors) {
                if (d.getPatients().contains(patient)) {
                    associatedDoctors.add(d);
                }
            }
            if (associatedDoctors.isEmpty()) {
                System.out.println("No doctors associated with this patient to receive alerts.");
            }
            // Check vitals and send alerts if necessary
            for (Doctor d : associatedDoctors) {
                NotificationService ns = new NotificationService(new EmailNotification(smtpUsername, smtpPassword), d.getEmail());
                EmergencyAlert alert = new EmergencyAlert(patient, vital, ns);
                try {
                    alert.checkVitals();
                } catch (VitalThresholdException | NotificationException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter numeric values where required.");
        }
    }

    // Allows a patient to schedule an appointment with a doctor
    private void scheduleAppointment() {
        Patient patient = (Patient) currentUser;
        System.out.println("Enter Doctor ID: ");
        String doctorId = sc.nextLine();
        Doctor doctor = findDoctorById(doctorId);
        if (doctor == null) {
            System.out.println("Doctor not found.");
            return;
        }
        System.out.println("Enter Appointment Date (e.g., 2025-03-25): ");
        String date = sc.nextLine();
        appointmentManager.requestAppointment(date, doctor, patient);
        doctor.addPatient(patient);
    }

    // Allows a doctor to approve or cancel an appointment
    private void modifyAppointment() {
        appointmentManager.displayAppointments();
        System.out.println("Enter Appointment Index: ");
        try {
            int index = Integer.parseInt(sc.nextLine());
            System.out.println("Enter 'A' to Approve or 'C' to Cancel: ");
            String action = sc.nextLine();
            if ("A".equalsIgnoreCase(action)) {
                appointmentManager.approveAppointment(index);
            } else if ("C".equalsIgnoreCase(action)) {
                appointmentManager.cancelAppointment(index);
            } else {
                System.out.println("Invalid action.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid index. Please enter a number.");
        }
    }

    // Allows a doctor to provide feedback and a prescription for a patient
    private void doctorFeedback() {
        Doctor doctor = (Doctor) currentUser;
        System.out.println("Enter Patient ID: ");
        String patientId = sc.nextLine();
        Patient patient = findPatientById(patientId);
        if (patient == null) {
            System.out.println("Patient not found.");
            return;
        }
        System.out.println("Enter Feedback: ");
        String feedbackText = sc.nextLine();
        System.out.println("Enter Medication Name: ");
        String medication = sc.nextLine();
        System.out.println("Enter Dosage: ");
        String dosage = sc.nextLine();
        System.out.println("Enter Schedule: ");
        String schedule = sc.nextLine();

        Prescription prescription = new Prescription(medication, dosage, schedule, patient);
        Feedback feedback = doctor.provideFeedback(feedbackText, prescription);
        patient.addFeedback(feedback);
        System.out.println("Feedback added.");
    }

    // Displays the medical history for the current patient
    private void viewMedicalHistory() {
        Patient patient = (Patient) currentUser;
        patient.viewMedicalHistory();
    }

    // Displays information for all users in the system
    private void displayAllUserInfo() {
        System.out.println("\n--- Patients ---");
        for (Patient p : patients) p.displayInfo();
        System.out.println("\n--- Doctors ---");
        for (Doctor d : doctors) d.displayInfo();
        System.out.println("\n--- Administrators ---");
        for (Administrator a : admins) a.displayInfo();
    }

    // Starts a chat session between a doctor and a patient
    private void startChat() {
        Doctor doctor = null;
        Patient patient = null;
        if (currentUser instanceof Doctor) {
            doctor = (Doctor) currentUser;
            System.out.println("Enter Patient ID: ");
            String patientId = sc.nextLine();
            patient = findPatientById(patientId);
            if (patient == null) {
                System.out.println("Patient not found.");
                return;
            }
        } else if (currentUser instanceof Patient) {
            patient = (Patient) currentUser;
            System.out.println("Enter Doctor ID: ");
            String doctorId = sc.nextLine();
            doctor = findDoctorById(doctorId);
            if (doctor == null) {
                System.out.println("Doctor not found.");
                return;
            }
        }

        ChatServer server = new ChatServer(doctor, patient);
        ChatClient doctorClient = new ChatClient(doctor, server);
        ChatClient patientClient = new ChatClient(patient, server);

        System.out.println("Chat started between Dr. " + doctor.getName() + " and " + patient.getName());
        while (true) {
            System.out.println("Enter message (or 'exit'): ");
            String message = sc.nextLine();
            if ("exit".equalsIgnoreCase(message)) break;
            System.out.println("From (doctor/patient): ");
            String sender = sc.nextLine();
            if ("doctor".equalsIgnoreCase(sender)) {
                doctorClient.sendMessage(message);
            } else if ("patient".equalsIgnoreCase(sender)) {
                patientClient.sendMessage(message);
            } else {
                System.out.println("Invalid sender.");
            }
            doctorClient.displayMessages();
        }
    }

    // Starts a video call between a doctor and a patient
    private void startVideoCall() {
        Doctor doctor = null;
        Patient patient = null;
        if (currentUser instanceof Doctor) {
            doctor = (Doctor) currentUser;
            System.out.println("Enter Patient ID: ");
            String patientId = sc.nextLine();
            patient = findPatientById(patientId);
            if (patient == null) {
                System.out.println("Patient not found.");
                return;
            }
        } else if (currentUser instanceof Patient) {
            patient = (Patient) currentUser;
            System.out.println("Enter Doctor ID: ");
            String doctorId = sc.nextLine();
            doctor = findDoctorById(doctorId);
            if (doctor == null) {
                System.out.println("Doctor not found.");
                return;
            }
        }
        System.out.println("Enter Meeting Link: ");
        String link = sc.nextLine();

        VideoCall call = new VideoCall(doctor, patient, link);
        try {
            System.out.println(call.startCall());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Sends appointment and medication reminders to patients
    private void sendReminders() {
        ReminderService reminderService = new ReminderService(new EmailNotification(smtpUsername, smtpPassword));
        for (Appointment a : appointmentManager.getAppointments()) {
            reminderService.addAppointment(a);
        }
        for (Patient p : patients) {
            for (Feedback f : p.getMedicalHistory().getFeedbackRecords()) {
                if (f.getPrescription() != null) {
                    reminderService.addPrescription(f.getPrescription());
                }
            }
        }
        try {
            reminderService.sendAppointmentReminder();
            reminderService.sendMedicationReminder();
        } catch (NotificationException e) {
            System.out.println("Error sending reminders: " + e.getMessage());
        }
    }

    // Triggers a panic button alert for the patient
    private void triggerPanicButton() {
        Patient patient = (Patient) currentUser;
        List<Doctor> associatedDoctors = new ArrayList<>();
        for (Doctor d : doctors) {
            if (d.getPatients().contains(patient)) {
                associatedDoctors.add(d);
            }
        }
        if (associatedDoctors.isEmpty()) {
            System.out.println("No doctors associated with this patient.");
            return;
        }

        for (Doctor d : associatedDoctors) {
            NotificationService ns = new NotificationService(new EmailNotification(smtpUsername, smtpPassword), d.getEmail());
            PanicButton panicButton = new PanicButton(patient, d, ns);
            try {
                panicButton.pressPanicButton();
            } catch (NotificationException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // Finds a patient by ID
    private Patient findPatientById(String id) {
        for (Patient p : patients) {
            if (p.getId().equalsIgnoreCase(id)) return p;
        }
        return null;
    }

    // Finds a doctor by ID
    private Doctor findDoctorById(String id) {
        for (Doctor d : doctors) {
            if (d.getId().equalsIgnoreCase(id)) return d;
        }
        return null;
    }

    // Main method to start the application
    public static void main(String[] args) {
        new RPMSApp().run();
    }
}