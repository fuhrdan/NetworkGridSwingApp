import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class NetworkGridSwingApp {

    public static void main(String[] args) {
        // Invoke the GUI creation on the Event Dispatch Thread
        SwingUtilities.invokeLater(NetworkGridSwingApp::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        // Create the main frame
        JFrame frame = new JFrame("Network Connections");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(60, 4)); // Create a 60x4 grid layout

        // Fetch the connections (IPs) to be displayed in the grid
        List<Connection> connections = fetchConnections();

        // Loop through the connections and add buttons for each one
        for (Connection conn : connections) {
            JButton button = new JButton(conn.ipAddress);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showDetails(conn); // Show details when button is clicked
                }
            });
            frame.add(button);
        }

        // Set frame size and visibility
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    // Fetch network connections using the 'netstat' command
    private static List<Connection> fetchConnections() {
        List<Connection> connections = new ArrayList<>();
        try {
            // Execute netstat command to get connection details
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "netstat -n");
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("TCP") || line.contains("UDP")) {
                    String[] parts = line.trim().split("\\s+");
                    if (parts.length >= 2) {
                        // Extract IP address (from the local address column)
                        String ipAddress = parts[1].split(":")[0];
                        connections.add(new Connection(ipAddress, "0", "0", "0 KB", "0 KB")); // Placeholder data
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connections;
    }

    // Display the details for the selected connection (IP address)
    private static void showDetails(Connection conn) {
        // Create a dialog with the connection details
        JOptionPane.showMessageDialog(
                null,
                "IP Address: " + conn.ipAddress + "\n" +
                        "Incoming Packets: " + conn.incomingPackets + "\n" +
                        "Outgoing Packets: " + conn.outgoingPackets + "\n" +
                        "Total Data Sent: " + conn.totalDataSent + "\n" +
                        "Total Data Received: " + conn.totalDataReceived,
                "Connection Details",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // Connection class to store connection details (IP and traffic info)
    private static class Connection {
        String ipAddress;
        String incomingPackets;
        String outgoingPackets;
        String totalDataSent;
        String totalDataReceived;

        public Connection(String ipAddress, String incomingPackets, String outgoingPackets, String totalDataSent, String totalDataReceived) {
            this.ipAddress = ipAddress;
            this.incomingPackets = incomingPackets;
            this.outgoingPackets = outgoingPackets;
            this.totalDataSent = totalDataSent;
            this.totalDataReceived = totalDataReceived;
        }
    }
}