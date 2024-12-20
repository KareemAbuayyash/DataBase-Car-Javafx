// In ReportsSection.java
package com.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ReportsSection {
    private VBox view;

    public ReportsSection() {
        view = new VBox(10);
        view.setPadding(new Insets(10));

        Label lblTitle = new Label("Reports");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Buttons for each report
        Button btnReportA = ButtonFactory.createActionButton("Services on Specific Car/Customer");
        Button btnReportB = ButtonFactory.createActionButton("Sales by Employee");
        Button btnReportC = ButtonFactory.createActionButton("Payments by Customer");
        Button btnReportD = ButtonFactory.createActionButton("Revenue by Service Type");
        Button btnReportE = ButtonFactory.createActionButton("Service Frequency");
        Button btnReportF = ButtonFactory.createActionButton("Service Cost History");

        // Style buttons (optional)
        btnReportA.setMaxWidth(Double.MAX_VALUE);
        btnReportB.setMaxWidth(Double.MAX_VALUE);
        btnReportC.setMaxWidth(Double.MAX_VALUE);
        btnReportD.setMaxWidth(Double.MAX_VALUE);
        btnReportE.setMaxWidth(Double.MAX_VALUE);
        btnReportF.setMaxWidth(Double.MAX_VALUE);

        // Add event handlers
        btnReportA.setOnAction(e -> showReportA());
        btnReportB.setOnAction(e -> showReportB());
        btnReportC.setOnAction(e -> showReportC());
        btnReportD.setOnAction(e -> showReportD());
        btnReportE.setOnAction(e -> showReportE());
        btnReportF.setOnAction(e -> showReportF());

        // Arrange buttons in a VBox
        VBox buttonsBox = new VBox(10, btnReportA, btnReportB, btnReportC, btnReportD, btnReportE, btnReportF);
        buttonsBox.setAlignment(Pos.TOP_CENTER);
        buttonsBox.setPadding(new Insets(20));

        // Add title and buttons to the main view
        view.getChildren().addAll(lblTitle, buttonsBox);
    }

    public VBox getView() {
        return view;
    }

    // Methods to display each report
    private void showReportA() {
        ReportAView reportA = new ReportAView();
        view.getChildren().clear();
        view.getChildren().addAll(new Label("Report A: Services on Specific Car/Customer"), reportA.getView());
    }

    private void showReportB() {
        ReportBView reportB = new ReportBView();
        view.getChildren().clear();
        view.getChildren().addAll(new Label("Report B: Sales by Employee"), reportB.getView());
    }
    private void showReportC() {
        ReportCView reportC = new ReportCView();
        view.getChildren().clear();
        view.getChildren().addAll(new Label("Report C: Payments by Customer"), reportC.getView());
    }
    private void showReportD() {
        ReportDView reportD = new ReportDView();
        view.getChildren().clear();
        view.getChildren().addAll(new Label("Report D: Revenue by Service Type"), reportD.getView());
    }
    private void showReportE() {
        ReportEView reportE = new ReportEView();
        view.getChildren().clear();
        view.getChildren().addAll(new Label("Report E: Service Frequency"), reportE.getView());
    }
    private void showReportF() {
        ReportFView reportF = new ReportFView();
        view.getChildren().clear();
        view.getChildren().addAll(new Label("Report F: Service Cost History"), reportF.getView());
    }
}
