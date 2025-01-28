import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class BookSubmitPanel {
    private JFrame frame;
    private JTextField studentIdField, bookIdField;
    private JLabel resultLabel;
    private JTable bookTable;
    private DefaultTableModel tableModel;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/library";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456789";

    public BookSubmitPanel() {
        frame = new JFrame("Book Submit Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 600);

        JPanel panel = new JPanel(new BorderLayout());

        // Top Panel for input and buttons
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(3, 2, 10, 10));

        JLabel studentLabel = new JLabel("Enter Student ID:");
        studentIdField = new JTextField();
        JLabel bookLabel = new JLabel("Enter Book ID:");
        bookIdField = new JTextField();
        JButton submitButton = new JButton("Submit Book");
        JButton refreshButton = new JButton("Refresh List");
        resultLabel = new JLabel("", SwingConstants.CENTER);

        topPanel.add(studentLabel);
        topPanel.add(studentIdField);
        topPanel.add(bookLabel);
        topPanel.add(bookIdField);
        topPanel.add(submitButton);
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
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String studentId = studentIdField.getText().trim();
                String bookId = bookIdField.getText().trim();
                if (!studentId.isEmpty() && !bookId.isEmpty()) {
                    submitBook(studentId, bookId);
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

    private void submitBook(String studentId, String bookId) {
        String query = "UPDATE students SET Submit_Book = NOW() WHERE student_id = ? AND EXISTS (SELECT 1 FROM books WHERE BOOK_ID = ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, bookId);
            int updated = pstmt.executeUpdate();
            if (updated > 0) {
                resultLabel.setText("Book Submitted Successfully");
            } else {
                resultLabel.setText("Student ID or Book ID not found!");
            }
        } catch (SQLException e) {
            resultLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BookSubmitPanel::new);
    }

    public void setVisible(boolean b) {
        frame.setVisible(b);
    }
}
