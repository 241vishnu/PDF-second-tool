package com.pdfutil;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import com.pdfutil.controllers.PDFDataValidationController;
import com.pdfutil.controllers.PDFImageComparisonController;
import com.pdfutil.controllers.PDFMetadataExtractController;
import com.pdfutil.controllers.PDFTextCompareController;
import com.pdfutil.views.PDFDataValidationView;
import com.pdfutil.views.PDFImageComparisonView;
import com.pdfutil.views.PDFMetadataExtractView;
import com.pdfutil.views.PDFTextCompareView;

public class PDFUtilApp {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("PDF Utility Application");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(800, 300);
			frame.setLayout(new BorderLayout());
			frame.setResizable(false); // Disable maximize button

			JTabbedPane tabbedPane = new JTabbedPane();

			PDFDataValidationView pdfDataValidationView = new PDFDataValidationView();
			new PDFDataValidationController(pdfDataValidationView);

			PDFImageComparisonView imageComparisonView = new PDFImageComparisonView();
			new PDFImageComparisonController(imageComparisonView);

			PDFMetadataExtractView pdfMetadataExtractView = new PDFMetadataExtractView();
			new PDFMetadataExtractController(pdfMetadataExtractView);
			
			PDFTextCompareView pdfTextCompareView = new PDFTextCompareView();
			new PDFTextCompareController(pdfTextCompareView);

			tabbedPane.addTab("PDF Data Validation",  pdfDataValidationView);
			tabbedPane.addTab("PDF Image Comparison", imageComparisonView);
			tabbedPane.addTab("PDF Metadata Extract",  pdfMetadataExtractView);
			tabbedPane.addTab("PDF Text Compare", pdfTextCompareView);

			frame.add(tabbedPane, BorderLayout.CENTER);

			// Add logo banner at the bottom
			ImageIcon logoIcon = new ImageIcon( System.getProperty("user.dir") +
					"\\template\\NFUMBanner_Footr.png");
			JLabel logoLabel = new JLabel(logoIcon);
			JPanel logoPanel = new JPanel();
			logoPanel.setSize(800, 50);
			logoPanel.add(logoLabel);
			frame.add(logoPanel, BorderLayout.SOUTH);

			frame.setLocationRelativeTo(null); // Center the frame on the screen
			frame.setVisible(true);
		});
	}
}