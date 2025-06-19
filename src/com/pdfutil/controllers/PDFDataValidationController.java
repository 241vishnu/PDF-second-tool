package com.pdfutil.controllers;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JOptionPane;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import com.pdfutil.views.PDFDataValidationView;

public class PDFDataValidationController {

	int srNo = 1, passCount = 0, failCount = 0;
	String fieldName, outputFolderPath;

	private PDFDataValidationView view;

	public PDFDataValidationController(PDFDataValidationView view) {
		this.view = view;
		this.view.addRunButtonListener(new RunButtonListener());
		this.view.addViewResultButtonListener(new ViewResultButtonListener());
	}

	private class RunButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String filePath = view.getSelectedFilePath();
			outputFolderPath = view.getOutputFolderPath();
			if (validatePDF(filePath)) {
				extractDataFromPDF(filePath);
				JOptionPane.showMessageDialog(view, "Validation Completed", "Completed",
						JOptionPane.INFORMATION_MESSAGE);
				view.setViewResultButtonEnabled(true); // Enable the View Result button
			} else {
				JOptionPane.showMessageDialog(view, "Invalid PDF File", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private class ViewResultButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// String outputFolderPath = view.getOutputFolderPath();
			File resultFile = new File(outputFolderPath, "PDFDataValidation.html");
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

	private void extractDataFromPDF(String filePath) {
		createHtmlReport(outputFolderPath, "PDF Data Validation Report");
		try (PDDocument document = Loader.loadPDF(new File(filePath))) {
			PDFTextStripper pdfStripper = new PDFTextStripper();
			String text = pdfStripper.getText(document);

			fieldName = "PolicyNumber";
			String policyNo_act = getNextWord(text, "Your policy number");
			String policyNo_exp = getDataFromExcel(fieldName);
			compareData(policyNo_act, policyNo_exp);

			fieldName = "PolicyHolderName";
			String policyHolderName_act = getNextWord(text, "Policyholder");
			String policyHolderName_exp = getDataFromExcel(fieldName);
			compareData(policyHolderName_act, policyHolderName_exp);

			fieldName = "IssueDate";
			String issueDate_act = getNextWord(text, "Date of issue");
			String issueDate_exp = getDataFromExcel(fieldName);
			compareData(issueDate_act, issueDate_exp);
			

			File outputFile = new File(outputFolderPath, "pdfData.txt");
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
				writer.write(text);
			}
			System.out.println("Data extracted from PDF and written to " + outputFile.getAbsolutePath());
			updateHtmlReportCounts();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void compareData(String actual, String expected) {
		if (actual.equals(expected)) {
			passCount++;
			appendToHtmlReport("<tr><td>" + srNo++ + "</td><td>" + fieldName + "</td><td>" + actual + "</td><td>"
					+ expected + "</td><td><strong><font color=\"Green\">PASS</font></strong></td></tr>");
			System.out.println("Fields are matching >>> " + actual + " & " + expected);
		} else {
			failCount++;
			appendToHtmlReport("<tr><td>" + srNo++ + "</td><td>" + fieldName + "</td><td>" + actual + "</td><td>"
					+ expected + "</td><td><strong><font color=\"Red\">FAIL</font></strong></td></tr>");
			System.out.println("Fields are not matching >>> " + actual + " & " + expected);
		}
	}

	public void createHtmlReport(String outputFolderPath, String title) {
		File reportFolder = new File(outputFolderPath);
		if (!reportFolder.exists()) {
			reportFolder.mkdirs();
		}
		File htmlFile = new File(reportFolder, "PDFDataValidation.html");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(htmlFile))) {
			writer.write("<html>");
			writer.write("<head><title>" + title + "</title>");
			writer.write(
					"\n<script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>");
			writer.write("\n<script type=\"text/javascript\">  ");
			writer.write("\ngoogle.charts.load(\"current\", {packages:[\"corechart\"]}); \n"
					+ "google.charts.setOnLoadCallback(drawChart); ");
			writer.write("function drawChart() ");
			writer.write(
					"\n{ var data = google.visualization.arrayToDataTable([ ['Fields', 'Result'], ['Passed',     XXPASSEDXX],  ['Failed',    XXFAILEDXX]]); ");
			writer.write("\nvar options = { slices: { 0: { color: 'Green' }, 1: { color: 'Red' } }, pieHole: 0.3,}  ");
			writer.write("\nvar chart = new google.visualization.PieChart(document.getElementById('donutchart')); ");
			writer.write("\nchart.draw(data, options); } ");
			writer.write("</script> ");
			writer.write("<body>");
			writer.write("<table align=\"center\" border='2' width='80%'> <tbody>");
			writer.write(
					"<tr style=\"height:40px\"> <td width=\"40%\"><b>Total No. of Fields Validated </b></td> <td width=\"10%\" align=\"center\"><b> 3 </b></td> <td rowspan=\"4\" > <div id=\"donutchart\"> </div>   </td>  </tr>");
			writer.write(
					" <tr style=\"height:40px\"> <td><b>No. of Fields Passed </b></td><td align=\"center\"><b> XXPASSEDXX </b></td></tr>");
			writer.write(
					" <tr style=\"height:40px\"> <td><b>No. of Fields Failed </b></td><td align=\"center\"><b> XXFAILEDXX </b></td></tr>");
			writer.write(
					"<tr style=\"height:40px\" ><td><b>Over All result </b></td><td align=\"center\"><b> XXSTATUSXX </b></td></tr></tbody>");
			writer.write("<h1 align=\"center\">" + title + "</h1>");
			writer.write("<table border='2' width='80%' align=\"center\">");
			writer.write("<tr>");
			writer.write("<th>Sr. No #</th>");
			writer.write("<th>Field</th>");
			writer.write("<th>Actual Value</th>");
			writer.write("<th>Expected Value</th>");
			writer.write("<th>Result</th>");
			writer.write("</tr>");
			System.out.println("HTML report created at " + htmlFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void appendToHtmlReport(String content) {
		File htmlFile = new File(outputFolderPath, "PDFDataValidation.html");
		if (!htmlFile.exists()) {
			System.out.println("HTML report does not exist. Creating a new one.");
			createHtmlReport(outputFolderPath, "PDF Data Validation Report");
			return;
		}
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(htmlFile, true))) {
			writer.write(content);
			System.out.println("Data appended to HTML report at " + htmlFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateHtmlReportCounts() {
		File htmlFile = new File(outputFolderPath, "PDFDataValidation.html");

		try {
			StringBuilder htmlContent = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(new FileReader(htmlFile))) {
				String line;
				while ((line = reader.readLine()) != null) {
					htmlContent.append(line).append("\n");
				}
			}
			String content = htmlContent.toString();
			content = content.replaceAll("XXPASSEDXX", String.valueOf(passCount));
			content = content.replaceAll("XXFAILEDXX", String.valueOf(failCount));
			content = content.replaceAll("XXSTATUSXX",
					failCount == 0 ? "<font color=\"Green\">  PASS </font>" : "<font color=\"Red\"> FAIL </font>");
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(htmlFile))) {
				writer.write(content);
			}
			System.out.println("HTML report counts updated at " + htmlFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		passCount = 0;
		failCount = 0;
		srNo = 1;
	}

	public static String getNextWord(String fileContent, String textToSearch) {
		int index = fileContent.indexOf(textToSearch);
		if (index == -1) {
			return null; // textToSearch not found
		}

		int startIndex = index + textToSearch.length();
		int endIndex = fileContent.indexOf('\n', startIndex);
		if (endIndex == -1) {
			endIndex = fileContent.length(); // If no newline, read until the end of the content
		}

		String line = fileContent.substring(startIndex, endIndex).trim();

		return line.length() > 0 ? line : null;
	}

	/**
	 * Get data from Excel file
	 * 
	 * @param columnName
	 * @return
	 */
	public String getDataFromExcel(String columnName) {
		String excelFilePath = System.getProperty("user.dir") + "\\TestData\\PolicyDetails.xlsx";
		Fillo fillo = new Fillo();
		String result = null;
		try {
			Connection connection = fillo.getConnection(excelFilePath);
			String query = String.format("SELECT * FROM PolicyData WHERE TC_ID='TC01'");
			Recordset recordset = connection.executeQuery(query);
			if (recordset.next()) {
				result = recordset.getField(columnName).trim();
			}
			recordset.close();
		} catch (FilloException e) {
			e.printStackTrace();
		}
		return result;
	}

}