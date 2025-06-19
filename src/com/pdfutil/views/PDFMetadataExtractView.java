package com.pdfutil.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

public class PDFMetadataExtractView extends JPanel {
	 private JTextField filePathField;
	    private JTextField outputFolderPathField;
	    private JButton runButton;
	    private JButton viewResultButton;
	    private JTextArea consoleOutputArea_data;

    public PDFMetadataExtractView() {
    	 setLayout(new BorderLayout());

         JPanel inputPanel = new JPanel(new GridLayout(2, 3));
         JLabel fileLabel = new JLabel("PDF File:");
         filePathField = new JTextField();
         JButton fileButton = new JButton("Browse");
         fileButton.addActionListener(e -> chooseFile(filePathField));

         JLabel outputFolderLabel = new JLabel("Output Folder:");
         outputFolderPathField = new JTextField();
         outputFolderPathField.setText(new File("Reports").getAbsolutePath()); // Set default location
         JButton outputFolderButton = new JButton("Browse");
         outputFolderButton.addActionListener(e -> chooseDirectory(outputFolderPathField));

         inputPanel.add(fileLabel);
         inputPanel.add(filePathField);
         inputPanel.add(fileButton);
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

    public String getSelectedFilePath() {
        return filePathField.getText();
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