import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

class ParkingSpace {
    String id;       // Unique ID of the parking space
    double price;    // Base price of the parking space
    boolean reserved; // Whether the space is reserved
    boolean isVIP;   // Whether the space is VIP

    public ParkingSpace(String id, double price, boolean isVIP) {
        this.id = id;
        this.price = price;
        this.reserved = false;
        this.isVIP = isVIP;
    }

    public double getDynamicPrice() {
        int hour = LocalTime.now().getHour();
        if (hour >= 8 && hour <= 18) {  // Peak hours: 8 AM to 6 PM
            return price * 1.2;
        } else {  // Off-peak hours
            return price * 0.9;
        }
    }

    @Override
    public String toString() {
        return "Space ID: " + id + ", Price: $" + String.format("%.2f", getDynamicPrice()) +
                " | " + (isVIP ? "VIP" : "Regular");
    }
}

class User {
    String id;          
    Map<String, ParkingSpace> reservationHistory;  // History of reserved spaces

    public User(String id) {
        this.id = id;
        this.reservationHistory = new HashMap<>();
    }

    public void viewReservationHistory(JTextArea historyArea) {
        if (reservationHistory.isEmpty()) {
            historyArea.setText("No reservations made yet.");
        } else {
            StringBuilder history = new StringBuilder("Your reservation history:\n");
            for (ParkingSpace space : reservationHistory.values()) {
                history.append(space).append("\n");
            }
            historyArea.setText(history.toString());
        }
    }

    public boolean reserveParkingSpace(ParkingSpace space) {
        if (!space.reserved) {
            space.reserved = true;
            reservationHistory.put(space.id, space);
            return true;
        }
        return false;
    }

    public void vacateParkingSpace(ParkingSpace space) {
        space.reserved = false;
        reservationHistory.remove(space.id);
    }
}

public class ParkingSystemApp {
    private JFrame frame;
    private Map<String, ParkingSpace> parkingSpaces = new HashMap<>();
    private Map<String, User> users = new HashMap<>();
    private User currentUser;

    public ParkingSystemApp() {
        initializeParkingSpaces();
        initializeUsers();
        initializeGUI();
    }

    private void initializeParkingSpaces() {
        parkingSpaces.put("A1", new ParkingSpace("A1", 5.0, false));
        parkingSpaces.put("A2", new ParkingSpace("A2", 7.0, true));
        parkingSpaces.put("A3", new ParkingSpace("A3", 6.0, false));
        parkingSpaces.put("B1", new ParkingSpace("B1", 4.0, false));
        parkingSpaces.put("B2", new ParkingSpace("B2", 8.0, true));
        parkingSpaces.put("B3", new ParkingSpace("B3", 6.5, false));
    }

    private void initializeUsers() {
        users.put("user1", new User("user1"));
        users.put("user2", new User("user2"));
    }

    private void initializeGUI() {
        frame = new JFrame("Parking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        JPanel userPanel = new JPanel();
        JLabel userLabel = new JLabel("Enter Username:");
        JTextField userField = new JTextField(10);
        JButton loginButton = new JButton("Login");
        userPanel.add(userLabel);
        userPanel.add(userField);
        userPanel.add(loginButton);

        JPanel actionPanel = new JPanel();
        JButton reserveButton = new JButton("Reserve");
        JButton vacateButton = new JButton("Vacate");
        JButton historyButton = new JButton("View History");
        JTextArea infoArea = new JTextArea(10, 50);
        infoArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(infoArea);

        actionPanel.add(reserveButton);
        actionPanel.add(vacateButton);
        actionPanel.add(historyButton);

        frame.add(userPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(actionPanel, BorderLayout.SOUTH);

        // Login Button Action
        loginButton.addActionListener(e -> {
            String username = userField.getText().trim().toLowerCase();  // Normalize input
            currentUser = users.get(username);
            if (currentUser != null) {
                infoArea.setText("Welcome, " + username + "!\n");
                displayParkingSpaces(infoArea);
            } else {
                infoArea.setText("Invalid username. Available usernames: user1, user2.\n");
            }
        });

        // Reserve Button Action
        reserveButton.addActionListener(e -> {
            if (currentUser == null) {
                infoArea.setText("Please log in first.\n");
                return;
            }
            String spaceId = JOptionPane.showInputDialog(frame, "Enter Space ID to Reserve:").toUpperCase();
            ParkingSpace space = parkingSpaces.get(spaceId);
            if (space == null) {
                infoArea.setText("Space " + spaceId + " does not exist.\n");
            } else if (space.reserved) {
                infoArea.setText("Space " + spaceId + " is already reserved.\n");
            } else {
                if (currentUser.reserveParkingSpace(space)) {
                    infoArea.setText("Space " + spaceId + " has been reserved.\n");
                    displayParkingSpaces(infoArea);
                } else {
                    infoArea.setText("Unable to reserve the space. Please try again.\n");
                }
            }
        });

        // Vacate Button Action
        vacateButton.addActionListener(e -> {
            if (currentUser == null) {
                infoArea.setText("Please log in first.\n");
                return;
            }
            String spaceId = JOptionPane.showInputDialog(frame, "Enter Space ID to Vacate:").toUpperCase();
            ParkingSpace space = parkingSpaces.get(spaceId);
            if (space == null) {
                infoArea.setText("Space " + spaceId + " does not exist.\n");
            } else if (!space.reserved) {
                infoArea.setText("Space " + spaceId + " is not reserved.\n");
            } else {
                currentUser.vacateParkingSpace(space);
                infoArea.setText("Space " + spaceId + " has been vacated.\n");
                displayParkingSpaces(infoArea);
            }
        });

        // History Button Action
        historyButton.addActionListener(e -> {
            if (currentUser == null) {
                infoArea.setText("Please log in first.\n");
                return;
            }
            currentUser.viewReservationHistory(infoArea);
        });

        frame.setVisible(true);
    }

    private void displayParkingSpaces(JTextArea infoArea) {
        StringBuilder display = new StringBuilder("Available Parking Spaces:\n");
        for (ParkingSpace space : parkingSpaces.values()) {
            String status = space.reserved ? "Reserved" : "Available";
            display.append(space).append(" | Status: ").append(status).append("\n");
        }
        infoArea.setText(display.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ParkingSystemApp::new);
    }
}