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
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import com.pdfutil.views.PDFMetadataExtractView;

public class PDFMetadataExtractController {
	String pdfFilePath;
	int passCount = 0, failCount = 0, srNo = 1;
	String fieldName, outputFolderPath;

	private PDFMetadataExtractView view;

	public PDFMetadataExtractController(PDFMetadataExtractView view) {
		this.view = view;
		this.view.addRunButtonListener(new RunButtonListener());
		this.view.addViewResultButtonListener(new ViewResultButtonListener());
	}

	private class RunButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			
			pdfFilePath = view.getSelectedFilePath();
			outputFolderPath = view.getOutputFolderPath();
			createHtmlReport(outputFolderPath, "PDF Font Style Report");
			if (validatePDFFile(pdfFilePath)) {
				extractMetadata(pdfFilePath);
				updateHtmlReportCounts();
				JOptionPane.showMessageDialog(view, "Validation Completed", "Completed",
						JOptionPane.INFORMATION_MESSAGE);
				view.setViewResultButtonEnabled(true); // Enable the View Result button
			} else {
				JOptionPane.showMessageDialog(view, "Invalid PDF File", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private boolean validatePDFFile(String filePath) {
		File file = new File(filePath);
		return file.exists() && file.isFile() && filePath.toLowerCase().endsWith(".pdf");
	}

	private void extractMetadata(String filePath) {
		try (PDDocument document = Loader.loadPDF(new File(pdfFilePath))) {
			if (document.getNumberOfPages() < 1) {
				System.out.println("Invalid page number.");
				return;
			}

			PDFTextStripper stripper = new PDFTextStripper() {
				@Override
				protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
					fontStyle(textPositions, "P-1000045722/00");
					fontStyle(textPositions, "Demands and needs");
					fontStyle(textPositions, "premium table to be added here later");
				}
			};
			stripper.setSortByPosition(true);
			stripper.setStartPage(1);
			stripper.setEndPage(1);
			stripper.getText(document);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void fontStyle(List<TextPosition> textPositions, String value) {
		boolean found = false;
		StringBuilder currentLine = new StringBuilder();
		String previousFont = "";
		fieldName = value;

		for (TextPosition text : textPositions) {
			String currentFont = text.getFont().getName();
			String unicode = text.getUnicode();
			
			currentLine.append(unicode);

			if (currentLine.toString().contains(value) && !found) {
				found = true;
				if (!currentFont.equals(previousFont)) {
					System.out.println("Font Style: " + currentFont);
					String[] tmp = currentFont.split("\\+");
					currentFont = tmp[tmp.length - 1];
					compareData(currentFont, getDataFromExcel(value));
					previousFont = currentFont;
					break;
				}
			}
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
	
	private class ViewResultButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// String outputFolderPath = view.getOutputFolderPath();
			File resultFile = new File(outputFolderPath, "PDFMetaData.html");
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

	/**
	 * This is to generate the HTML report
	 * 
	 * @param outputFolderPath
	 * @param title
	 */
	public void createHtmlReport(String outputFolderPath, String title) {
		File reportFolder = new File(outputFolderPath);
		if (!reportFolder.exists()) {
			reportFolder.mkdirs();
		}
		File htmlFile = new File(reportFolder, "PDFMetaData.html");
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
			writer.write("<th>Actual Font</th>");
			writer.write("<th>Expected Font</th>");
			writer.write("<th>Result</th>");
			writer.write("</tr>");
			System.out.println("HTML report created at " + htmlFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void appendToHtmlReport(String content) {
		File htmlFile = new File(outputFolderPath, "PDFMetaData.html");
		if (!htmlFile.exists()) {
			System.out.println("HTML report does not exist. Creating a new one.");
			createHtmlReport(outputFolderPath, "PDF Font Style Report");
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
		File htmlFile = new File(outputFolderPath, "PDFMetaData.html");

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

	/**
	 * Get data from Excel file
	 * 
	 * @param columnName
	 * @return
	 */
	public String getDataFromExcel(String columnName) {
		String excelFilePath = System.getProperty("user.dir") + "\\TestData\\FontValidation.xlsx";
		Fillo fillo = new Fillo();
		String result = null;
		try {
			Connection connection = fillo.getConnection(excelFilePath);
			String query = String.format("SELECT Font FROM PolicyData WHERE Field ='"+columnName+"'");
			Recordset recordset = connection.executeQuery(query);
			if (recordset.next()) {
				result = recordset.getField("Font").trim();
			}
			recordset.close();
		} catch (FilloException e) {
			e.printStackTrace();
		}
		return result;
	}

}