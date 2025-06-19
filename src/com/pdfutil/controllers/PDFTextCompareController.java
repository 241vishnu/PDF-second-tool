package com.pdfutil.controllers;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.swing.JOptionPane;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.pdfutil.views.PDFTextCompareView;

public class PDFTextCompareController {

	private PDFTextCompareView view;
	private String outputFolderPath;

	public PDFTextCompareController(PDFTextCompareView view) {
		this.view = view;
		this.view.addRunButtonListener(new RunButtonListener());
		this.view.addViewResultButtonListener(new ViewResultButtonListener());
	}

	private class RunButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String actualFilePath = view.getActualFilePath();
			String expectedFilePath = view.getExpectedFilePath();
			outputFolderPath = System.getProperty("user.dir") + "\\output";

			if (validatePDF(actualFilePath) && validatePDF(expectedFilePath)) {
				comparePDFs(actualFilePath, expectedFilePath);
				JOptionPane.showMessageDialog(view, "Comparison Completed", "Completed",
						JOptionPane.INFORMATION_MESSAGE);
				view.setViewResultButtonEnabled(true); // Enable the View Result button
			} else {
				JOptionPane.showMessageDialog(view, "Invalid PDF File(s)", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private class ViewResultButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			File resultFile = new File(outputFolderPath, "PDFTextComparisonResult.txt");
			if (resultFile.exists()) {
				try {
					Desktop.getDesktop().open(resultFile);
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(view, "Failed to open the result file.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(view, "Result file not found.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private boolean validatePDF(String filePath) {
		File file = new File(filePath);
		return file.exists() && file.isFile() && filePath.toLowerCase().endsWith(".pdf");
	}

	private void comparePDFs(String actualFilePath, String expectedFilePath) {
		try (PDDocument actualDoc = Loader.loadPDF(new File(actualFilePath));
			PDDocument expectedDoc = Loader.loadPDF(new File(expectedFilePath))) {

			PDFTextStripper pdfStripper = new PDFTextStripper();
			String actualText = pdfStripper.getText(actualDoc);
			String expectedText = pdfStripper.getText(expectedDoc);

			File outputFile = new File(outputFolderPath, "PDFTextComparisonResult.txt");
			if (!outputFile.getParentFile().exists()) {
				outputFile.getParentFile().mkdirs();
			}if (!outputFile.exists()) {
				outputFile.createNewFile();
			}
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
				writer.write("\n\nDifferences:\n");
				writer.write(getDifferences(actualText, expectedText));
			}
			System.out.println("Comparison result written to " + outputFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getDifferences(String actualText, String expectedText) {
		StringBuilder differences = new StringBuilder();
		String[] actualLines = actualText.split("\n");
		String[] expectedLines = expectedText.split("\n");

		int maxLength = Math.max(actualLines.length, expectedLines.length);
		for (int i = 0; i < maxLength; i++) {
			String actualLine = i < actualLines.length ? actualLines[i] : "";
			String expectedLine = i < expectedLines.length ? expectedLines[i] : "";
			if (!actualLine.equals(expectedLine)) {
				differences.append("Line: ").append(i + 1).append("\n");
				differences.append("Actual: ").append(actualLine).append("\n");
				differences.append("Expected: ").append(expectedLine).append("\n\n");
			}
		}
		return differences.toString();
	}
	
}