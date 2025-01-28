import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class IssueBookPanel {
    private JFrame frame;
    private JTextField studentIdField, bookIdField;
    private JLabel resultLabel;
    private JTable bookTable;
    private DefaultTableModel tableModel;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/library";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456789";

    public IssueBookPanel() {
        frame = new JFrame("Book Issue Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);

        JPanel panel = new JPanel(new BorderLayout());

        // Top Panel for input and buttons
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(3, 2, 10, 10));

        JLabel studentLabel = new JLabel("Enter Student ID:");
        studentIdField = new JTextField();
        JLabel bookLabel = new JLabel("Enter Book ID:");
        bookIdField = new JTextField();
        JButton issueButton = new JButton("Issue Book");
        JButton refreshButton = new JButton("Refresh List");
        resultLabel = new JLabel("", SwingConstants.CENTER);

        topPanel.add(studentLabel);
        topPanel.add(studentIdField);
        topPanel.add(bookLabel);
        topPanel.add(bookIdField);
        topPanel.add(issueButton);
        topPanel.add(refreshButton);

        // Center Panel for displaying the book table
        JPanel centerPanel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel(new String[]{"BOOK_ID", "CATEGORY", "NAME", "AUTHOR", "QUANTITY"}, 0);
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(resultLabel, BorderLayout.SOUTH);

        frame.add(panel);

        // Action Listeners
        issueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String studentId = studentIdField.getText().trim();
                String bookId = bookIdField.getText().trim();
                if (!studentId.isEmpty() && !bookId.isEmpty()) {
                    issueBook(studentId, bookId);
                    loadBookData();
                } else {
                    resultLabel.setText("Please enter both Student ID and Book ID.");
                }
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadBookData();
            }
        });

        frame.setVisible(true);
        loadBookData();
    }

    private void loadBookData() {
        String query = "SELECT * FROM books";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getString("BOOK_ID"), rs.getString("CATEGORY"),
                        rs.getString("NAME"), rs.getString("AUTHOR"), rs.getInt("QUANTITY")});
            }
        } catch (SQLException e) {
            resultLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void issueBook(String studentId, String bookId) {
        String query = "UPDATE students SET Issue_Book = NOW() WHERE student_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, studentId);
            int updated = pstmt.executeUpdate();
            if (updated > 0) {
                resultLabel.setText("Book Issued Successfully");
            } else {
                resultLabel.setText("Student ID not found!");
            }
        } catch (SQLException e) {
            resultLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(IssueBookPanel::new);
    }

    public void setVisible(boolean b) {
        frame.setVisible(b);
    }
}
