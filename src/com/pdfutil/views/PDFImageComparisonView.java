package com.pdfutil.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class PDFImageComparisonView extends JPanel {
    private JTextField actualFilePathField;
    private JTextField expectedFilePathField;
    private JTextField outputFolderPathField;
    private JButton runButton;
    private JButton viewResultButton;
   

    public PDFImageComparisonView() {
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 3));
        JLabel actualFileLabel = new JLabel("Actual PDF File:");
        actualFilePathField = new JTextField();
        JButton actualFileButton = new JButton("Browse");
        actualFileButton.addActionListener(e -> chooseFile(actualFilePathField));

        JLabel expectedFileLabel = new JLabel("Expected PDF File:");
        expectedFilePathField = new JTextField();
        JButton expectedFileButton = new JButton("Browse");
        expectedFileButton.addActionListener(e -> chooseFile(expectedFilePathField));

        JLabel outputFolderLabel = new JLabel("Output Folder:");
        outputFolderPathField = new JTextField();
        outputFolderPathField.setText(new File("Reports").getAbsolutePath());
        JButton outputFolderButton = new JButton("Browse");
        outputFolderButton.addActionListener(e -> chooseDirectory(outputFolderPathField));

        inputPanel.add(actualFileLabel);
        inputPanel.add(actualFilePathField);
        inputPanel.add(actualFileButton);
        inputPanel.add(expectedFileLabel);
        inputPanel.add(expectedFilePathField);
        inputPanel.add(expectedFileButton);
        inputPanel.add(outputFolderLabel);
        inputPanel.add(outputFolderPathField);
        inputPanel.add(outputFolderButton);

        runButton = new JButton("Run");
        runButton.setBackground(Color.YELLOW);
        runButton.setPreferredSize(new Dimension(100, 50)); // Set the preferred size of the Run button
        JPanel runButtonPanel = new JPanel();
        runButtonPanel.add(runButton);

        viewResultButton = new JButton("View Result");
        viewResultButton.setBackground(Color.GREEN);
        viewResultButton.setPreferredSize(new Dimension(100, 50)); // Set the preferred size of the View Result button
        viewResultButton.setEnabled(false); // Initially disable the View Result button
        runButtonPanel.add(viewResultButton);

        add(inputPanel, BorderLayout.NORTH);
        add(runButtonPanel, BorderLayout.CENTER);
        
    }

    private void chooseFile(JTextField textField) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Files", "pdf"));
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void chooseDirectory(JTextField textField) {
        JFileChooser directoryChooser = new JFileChooser();
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = directoryChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            textField.setText(directoryChooser.getSelectedFile().getAbsolutePath());
        }
    }

    public String getActualFilePath() {
        return actualFilePathField.getText();
    }

    public String getExpectedFilePath() {
        return expectedFilePathField.getText();
    }

    public String getOutputFolderPath() {
        return outputFolderPathField.getText();
    }

    public void addRunButtonListener(ActionListener listener) {
        runButton.addActionListener(listener);
    }

    public void addViewResultButtonListener(ActionListener listener) {
        viewResultButton.addActionListener(listener);
    }

    public void setViewResultButtonEnabled(boolean enabled) {
        viewResultButton.setEnabled(enabled);
    }

}