package com.pdfutil.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PDFTextCompareView extends JPanel {
	private JTextField actualFilePathField;
	private JTextField expectedFilePathField;
	private JButton runButton;
	private JButton viewResultButton;
	private JTextArea consoleTextArea;

	public PDFTextCompareView() {
		setLayout(new BorderLayout());

		JPanel inputPanel = new JPanel(new GridLayout(2, 3));
		inputPanel.add(new JLabel("Actual PDF File:"));
		actualFilePathField = new JTextField();
		inputPanel.add(actualFilePathField);
		JButton browseActualButton = new JButton("Browse");
		inputPanel.add(browseActualButton);

		inputPanel.add(new JLabel("Expected PDF File:"));
		expectedFilePathField = new JTextField();
		inputPanel.add(expectedFilePathField);
		JButton browseExpectedButton = new JButton("Browse");
		inputPanel.add(browseExpectedButton);

		runButton = new JButton("Run");
		runButton.setBackground(Color.YELLOW);
		 runButton.setPreferredSize(new Dimension(100, 50)); // Set the preferred size of the Run button
         JPanel runButtonPanel = new JPanel();
         runButtonPanel.add(runButton);
		viewResultButton = new JButton("View Result");
		viewResultButton.setEnabled(false);
		viewResultButton.setBackground(Color.GREEN);
		viewResultButton.setPreferredSize(new Dimension(100, 50)); // Set the preferred size of the View Result button
		runButtonPanel.add(viewResultButton);
		 add(inputPanel, BorderLayout.NORTH);
         add(runButtonPanel, BorderLayout.CENTER);

		browseActualButton.addActionListener(e -> chooseFile(actualFilePathField));
		browseExpectedButton.addActionListener(e -> chooseFile(expectedFilePathField));
	}

	private void chooseFile(JTextField textField) {
		JFileChooser fileChooser = new JFileChooser();
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
		}
	}

	public String getActualFilePath() {
		return actualFilePathField.getText();
	}

	public String getExpectedFilePath() {
		return expectedFilePathField.getText();
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