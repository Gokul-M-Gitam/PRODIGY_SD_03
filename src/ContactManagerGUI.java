import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class ContactManagerGUI extends JFrame {
    private JTextField nameField, phoneField, emailField;
    private JTable contactsTable;
    private DefaultTableModel tableModel;
    private ArrayList<Contact> contacts;

    public ContactManagerGUI() {
        contacts = new ArrayList<>();
        setTitle("Contact Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 400));
        initializeGUI();
        loadContactsFromFile();
        pack();
        setLocationRelativeTo(null); // Center the frame on screen
    }

    private void initializeGUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        mainPanel.add(createInputPanel(), BorderLayout.NORTH);
        mainPanel.add(createContactsPanel(), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        inputPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx++;
        nameField = new JTextField(20);
        inputPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        inputPanel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx++;
        phoneField = new JTextField(20);
        inputPanel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        inputPanel.add(new JLabel("Email Address:"), gbc);
        gbc.gridx++;
        emailField = new JTextField(20);
        inputPanel.add(emailField, gbc);

        return inputPanel;
    }

    private JPanel createContactsPanel() {
        JPanel contactsPanel = new JPanel(new BorderLayout());
        contactsPanel.setBorder(BorderFactory.createTitledBorder("Contacts"));

        String[] columnNames = {"Name", "Phone Number", "Email Address"};
        tableModel = new DefaultTableModel(columnNames, 0);
        contactsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(contactsTable);
        contactsPanel.add(scrollPane, BorderLayout.CENTER);

        return contactsPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton addButton = new JButton("Add Contact");
        addButton.addActionListener(e -> addContact());
        buttonPanel.add(addButton);

        JButton editButton = new JButton("Edit Contact");
        editButton.addActionListener(e -> editContact());
        buttonPanel.add(editButton);

        JButton deleteButton = new JButton("Delete Contact");
        deleteButton.addActionListener(e -> deleteContact());
        buttonPanel.add(deleteButton);

        JButton saveButton = new JButton("Save Contacts");
        saveButton.addActionListener(e -> saveContactsToFile());
        buttonPanel.add(saveButton);

        JButton loadButton = new JButton("Load Contacts");
        loadButton.addActionListener(e -> loadContactsFromFile());
        buttonPanel.add(loadButton);

        return buttonPanel;
    }

    private void addContact() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        if (!name.isEmpty() && !phone.isEmpty() && !email.isEmpty()) {
            Contact contact = new Contact(name, phone, email);
            contacts.add(contact);
            updateContactsDisplay();
            clearFields();
        } else {
            showMessage("Please enter name, phone number, and email address.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editContact() {
        int selectedRow = contactsTable.getSelectedRow();
        if (selectedRow >= 0) {
            String name = (String) tableModel.getValueAt(selectedRow, 0);
            String phone = (String) tableModel.getValueAt(selectedRow, 1);
            String email = (String) tableModel.getValueAt(selectedRow, 2);

            nameField.setText(name);
            phoneField.setText(phone);
            emailField.setText(email);

            contacts.remove(selectedRow);
            tableModel.removeRow(selectedRow);
        } else {
            showMessage("Please select a contact to edit.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteContact() {
        int selectedRow = contactsTable.getSelectedRow();
        if (selectedRow >= 0) {
            contacts.remove(selectedRow);
            tableModel.removeRow(selectedRow);
        } else {
            showMessage("Please select a contact to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateContactsDisplay() {
        tableModel.setRowCount(0); // Clear existing rows
        for (Contact contact : contacts) {
            Object[] rowData = {contact.getName(), contact.getPhoneNumber(), contact.getEmailAddress()};
            tableModel.addRow(rowData);
        }
    }

    private void clearFields() {
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
    }

    private void saveContactsToFile() {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("contacts.dat"))) {
            outputStream.writeObject(contacts);
            showMessage("Saved contacts to file.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            ex.printStackTrace();
            showMessage("Failed to save contacts to file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadContactsFromFile() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("contacts.dat"))) {
            contacts = (ArrayList<Contact>) inputStream.readObject();
            updateContactsDisplay();
        } catch (IOException | ClassNotFoundException ex) {
            // If file does not exist or cannot be read, initialize contacts as empty
            contacts = new ArrayList<>();
        }
    }

    private void showMessage(Object message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ContactManagerGUI app = new ContactManagerGUI();
            app.setVisible(true);
        });
    }
}

