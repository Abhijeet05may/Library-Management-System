import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Edit_Admin extends JFrame {

    private JComboBox<String> userIdComboBox;
    private JComboBox<String> columnComboBox;
    private JTextField updatedValueField;
    private JButton submitButton, resetButton, backButton, searchButton, viewAllButton;
    private JLabel infoLabel;

    public Edit_Admin() {
        initComponents();
    }

    private void initComponents() {
        // Frame settings
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Edit Admin Panel");
        setSize(600, 400);
        setLayout(null);

        // Labels
        JLabel titleLabel = new JLabel("Edit Admin Panel", SwingConstants.CENTER);
        titleLabel.setBounds(150, 10, 300, 30);
        add(titleLabel);

        JLabel userIdLabel = new JLabel("Select User ID:");
        userIdLabel.setBounds(50, 60, 120, 25);
        add(userIdLabel);

        JLabel columnLabel = new JLabel("Select Column:");
        columnLabel.setBounds(50, 100, 120, 25);
        add(columnLabel);

        JLabel updatedValueLabel = new JLabel("Enter New Value:");
        updatedValueLabel.setBounds(50, 140, 120, 25);
        add(updatedValueLabel);

        // Info label for messages
        infoLabel = new JLabel("");
        infoLabel.setBounds(150, 280, 300, 25);
        add(infoLabel);

        // ComboBox for User ID
        userIdComboBox = new JComboBox<>();
        userIdComboBox.setBounds(180, 60, 150, 25);
        populateUserIdComboBox();
        add(userIdComboBox);

        // ComboBox for Column Selection
        columnComboBox = new JComboBox<>(new String[]{"Name", "Password", "Contact"});
        columnComboBox.setBounds(180, 100, 150, 25);
        add(columnComboBox);

        // TextField for Updated Value
        updatedValueField = new JTextField();
        updatedValueField.setBounds(180, 140, 150, 25);
        add(updatedValueField);

        // Buttons
        submitButton = new JButton("Submit");
        submitButton.setBounds(50, 200, 100, 30);
        submitButton.addActionListener(new SubmitActionListener());
        add(submitButton);

        resetButton = new JButton("Reset");
        resetButton.setBounds(170, 200, 100, 30);
        resetButton.addActionListener(e -> resetFields());
        add(resetButton);

        backButton = new JButton("Back");
        backButton.setBounds(290, 200, 100, 30);
        backButton.addActionListener(e -> dispose());
        add(backButton);

        searchButton = new JButton("Search");
        searchButton.setBounds(350, 60, 100, 25);
        searchButton.addActionListener(new SearchActionListener());
        add(searchButton);

        viewAllButton = new JButton("View All");
        viewAllButton.setBounds(410, 200, 100, 30);
        viewAllButton.addActionListener(new ViewAllActionListener());
        add(viewAllButton);

        setVisible(true);
    }

    private void populateUserIdComboBox() {
        String url = "jdbc:mysql://localhost:3306/library?useSSL=false";
        String user = "root";
        String password = "123456789";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = conn.prepareStatement("SELECT User_ID FROM admin");
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                userIdComboBox.addItem(rs.getString("User_ID"));
            }
        } catch (Exception e) {
            infoLabel.setText("Error loading User IDs: " + e.getMessage());
        }
    }

    private void resetFields() {
        updatedValueField.setText("");
        infoLabel.setText("");
    }

    private class SubmitActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String userId = (String) userIdComboBox.getSelectedItem();
            String column = (String) columnComboBox.getSelectedItem();
            String updatedValue = updatedValueField.getText();

            if (userId == null || updatedValue.isEmpty()) {
                infoLabel.setText("Please fill all fields.");
                return;
            }

            String url = "jdbc:mysql://localhost:3306/library?useSSL=false";
            String user = "root";
            String password = "123456789";
            String query = "UPDATE admin SET " + column + " = ? WHERE User_ID = ?";

            try (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement pst = conn.prepareStatement(query)) {

                pst.setString(1, updatedValue);
                pst.setString(2, userId);
                int rows = pst.executeUpdate();

                if (rows > 0) {
                    infoLabel.setText("Updated successfully.");
                } else {
                    infoLabel.setText("No record found to update.");
                }
            } catch (Exception ex) {
                infoLabel.setText("Error: " + ex.getMessage());
            }
        }
    }

    private class SearchActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String userId = (String) userIdComboBox.getSelectedItem();
            if (userId == null) {
                infoLabel.setText("No User ID selected.");
                return;
            }

            String url = "jdbc:mysql://localhost:3306/library?useSSL=false";
            String user = "root";
            String password = "123456789";
            String query = "SELECT * FROM admin WHERE User_ID = ?";

            try (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement pst = conn.prepareStatement(query)) {

                pst.setString(1, userId);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(Edit_Admin.this,
                            "Name: " + rs.getString("Name") + "\n" +
                                    "Password: " + rs.getString("Password") + "\n" +
                                    "Contact: " + rs.getString("Contact"));
                } else {
                    infoLabel.setText("No record found for User ID " + userId);
                }
            } catch (Exception ex) {
                infoLabel.setText("Error: " + ex.getMessage());
            }
        }
    }

    private class ViewAllActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String url = "jdbc:mysql://localhost:3306/library?useSSL=false";
            String user = "root";
            String password = "123456789";
            String query = "SELECT * FROM admin";

            try (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement pst = conn.prepareStatement(query);
                 ResultSet rs = pst.executeQuery()) {

                JTable table = new JTable();
                DefaultTableModel model = new DefaultTableModel();
                table.setModel(model);

                model.addColumn("User_ID");
                model.addColumn("Name");
                model.addColumn("Password");
                model.addColumn("Contact");

                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("User_ID"),
                            rs.getString("Name"),
                            rs.getString("Password"),
                            rs.getString("Contact")
                    });
                }

                JOptionPane.showMessageDialog(Edit_Admin.this, new JScrollPane(table), "All Admin Records", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                infoLabel.setText("Error: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Edit_Admin::new);
    }
}
