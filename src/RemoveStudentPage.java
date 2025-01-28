import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RemoveStudentPage {
    private JFrame frame;
    private JTextField studentIdField;
    private JLabel resultLabel;
    private JTable studentTable;
    private DefaultTableModel tableModel;

    public RemoveStudentPage() {
        frame = new JFrame("Remove Student");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel panel = new JPanel(new BorderLayout());

        // Top Panel for input and buttons
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2, 2, 10, 10));

        JLabel label = new JLabel("Enter Student ID to Remove:");
        studentIdField = new JTextField();
        JButton removeButton = new JButton("Remove Student");
        JButton refreshButton = new JButton("Refresh List");
        resultLabel = new JLabel("", SwingConstants.CENTER);

        topPanel.add(label);
        topPanel.add(studentIdField);
        topPanel.add(removeButton);
        topPanel.add(refreshButton);

        // Center Panel for displaying the student table
        JPanel centerPanel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel(new String[]{"Student ID", "Name", "Reg No", "Issue Book", "Submit Book", "Fee"}, 0);
        studentTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(studentTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(resultLabel, BorderLayout.SOUTH);

        frame.add(panel);

        // Action Listeners
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String studentId = studentIdField.getText().trim();
                if (!studentId.isEmpty()) {
                    removeStudent(studentId);
                    loadStudents();
                } else {
                    resultLabel.setText("Please enter a Student ID.");
                }
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadStudents();
            }
        });

        frame.setVisible(true);
        loadStudents();
    }

    private void loadStudents() {
        String url = "jdbc:mysql://localhost/library";
        String username = "root";
        String password = "123456789";


        String query = "SELECT * FROM students";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            tableModel.setRowCount(0); // Clear existing rows

            while (resultSet.next()) {
                String studentId = resultSet.getString("student_id");
                String name = resultSet.getString("name");
                String regNo = resultSet.getString("regno");
                String issueBook = resultSet.getString("Issue_Book");
                String submitBook = resultSet.getString("Submit_Book");
                String fee = resultSet.getString("fee");

                tableModel.addRow(new Object[]{studentId, name, regNo, issueBook, submitBook, fee});
            }

        } catch (SQLException e) {
            resultLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void removeStudent(String studentId) {
        String url = "jdbc:mysql://localhost:3306/your_database_name"; // Replace with your DB URL
        String username = "your_username"; // Replace with your DB username
        String password = "your_password"; // Replace with your DB password

        String query = "DELETE FROM students WHERE student_id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, studentId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                resultLabel.setText("Student removed successfully.");
            } else {
                resultLabel.setText("No student found with the given ID.");
            }

        } catch (SQLException e) {
            resultLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RemoveStudentPage::new);
    }

    public void setVisible(boolean b) {
    }
}
