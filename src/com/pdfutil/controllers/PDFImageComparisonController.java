package com.pdfutil.controllers;

import javax.swing.*;

import com.pdfutil.views.PDFImageComparisonView;

import de.redsix.pdfcompare.PdfComparator;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class PDFImageComparisonController {

	private PDFImageComparisonView view;

	public PDFImageComparisonController(PDFImageComparisonView view) {
		this.view = view;
		this.view.addRunButtonListener(new RunButtonListener());
		this.view.addViewResultButtonListener(new ViewResultButtonListener());

	}

	private class RunButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String actualFilePath = view.getActualFilePath();
			String expectedFilePath = view.getExpectedFilePath();
			String outputFolderPath = view.getOutputFolderPath();

			if (validateFilePaths(actualFilePath, expectedFilePath, outputFolderPath)) {

				comparePDFs(actualFilePath, expectedFilePath, outputFolderPath);
				System.out.println("PDF Pixel to Pixel Comparison Completed...");
				System.out.println("Result file saved in: " + outputFolderPath + "\\result.pdf");
				JOptionPane.showMessageDialog(view, "Completed");
				view.setViewResultButtonEnabled(true); // Enable the View Result button
			} else {
				JOptionPane.showMessageDialog(view,
						"Please ensure all file paths are valid PDF files \n Both Actual and Expected file names are same.");
			}
		}
	}

	private class ViewResultButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String outputFolderPath = view.getOutputFolderPath();
			File outputFile = new File(outputFolderPath, "result.pdf"); // Assuming the result file is named
																		// "result.pdf"
			if (outputFile.exists()) {
				try {
					Desktop.getDesktop().open(outputFile);
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(view, "Failed to open the result file.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(view, "Result file not found.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private boolean validateFilePaths(String actualFilePath, String expectedFilePath, String outputFolderPath) {
		return isPDFFile(actualFilePath) && isPDFFile(expectedFilePath) && isDirectory(outputFolderPath)
				&& !isFileEqual(actualFilePath, expectedFilePath);
	}

	private boolean isPDFFile(String filePath) {
		return filePath != null && filePath.endsWith(".pdf") && new File(filePath).exists();
	}

	private boolean isDirectory(String dirPath) {
		return dirPath != null && new File(dirPath).isDirectory();
	}

	private boolean isFileEqual(String actualFilePath, String expectedFilePath) {

		return actualFilePath.equals(expectedFilePath);
	}

	private void comparePDFs(String actualFilePath, String expectedFilePath, String outputFolderPath) {
		// Implement the logic to compare the actual and expected PDF files
		// and save the results to the output folder.
		System.out.println("PDF Pixel to Pixel Comparison Started...");

		File actual = new File(actualFilePath);
		File expect = new File(expectedFilePath);
		String diffOut = (outputFolderPath + "\\result");
		try {
			Boolean diffFound = new PdfComparator(expect, actual).compare().writeTo(diffOut);
			if (diffFound) {
				System.out.println("PDFs are not identical");
			} else {
				System.out.println("PDFs are identical");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}