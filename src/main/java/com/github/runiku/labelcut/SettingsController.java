package com.github.runiku.labelcut;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class SettingsController {
    Stage stage;

    File previewFile;

    Settings settings;

    @FXML
    Button btn_save;

    @FXML
    ToggleButton btn_preview;

    @FXML
    TextField pdf_position_x;

    @FXML
    TextField pdf_position_y;

    @FXML
    TextField pdf_position_width;

    @FXML
    TextField pdf_position_height;

    @FXML
    TextField pdf_rotation;

    @FXML
    TextField adobe_reader_filename;

    @FXML
    ImageView pageView;

    @FXML
    void onBtnSaveAction(ActionEvent event) {
        System.out.println("Save...");
        if (previewFile != null) {
            try {
                refreshPreview(previewFile);
            } catch (IOException ex) {
                System.out.println("ERROR: " + ex.getMessage());
                utils.showDialog("A problem occured", ex.getMessage());
            }
        }

        // Save Settings
        settings.setPDFPositionX(Float.parseFloat(pdf_position_x.getText()));
        settings.setPDFPositionY(Float.parseFloat(pdf_position_y.getText()));
        settings.setPDFPositionWidth(Float.parseFloat(pdf_position_width.getText()));
        settings.setPDFPositionHeight(Float.parseFloat(pdf_position_height.getText()));
        settings.setPDFRotation(Integer.parseInt(pdf_rotation.getText()));
        settings.setAdobeReaderExec(adobe_reader_filename.getText());

        try {
            settings.save();
        } catch (IOException ex) {
            System.out.println("ERROR: " + ex.getMessage());
            utils.showDialog("A problem occured", ex.getMessage());
        }
    }

    @FXML
    void onBtnPreviewAction(ActionEvent event) {
        if (btn_preview.isSelected()) {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF file (*.pdf)", "*.pdf");
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setTitle("Select Label PDF");
            File file = fileChooser.showOpenDialog(stage);

            if (file != null) {
                try {
                    previewFile = file;
                    refreshPreview(file);
                } catch (IOException ex) {
                    System.out.println("ERROR: " + ex.getMessage());
                    utils.showDialog("A problem occured", ex.getMessage());
                }
            }
        } else {
            previewFile = null;
            pageView.setImage(null);
        }
    }

    @FXML
    void onLoadDefaultSettings(ActionEvent event) {
        pdf_position_x.setText("20");
        pdf_position_y.setText("665");
        pdf_position_width.setText("270");
        pdf_position_height.setText("100");
        pdf_rotation.setText("90");
        adobe_reader_filename.setText("AcroRd32.exe");
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    void refreshPreview(File file) throws IOException {
        if (file == null)
            throw new IOException("File can't be null");

        File preview = utils.generatePDF(
                file,
                "preview.pdf",
                0,
                Float.parseFloat(pdf_position_x.getText()),
                Float.parseFloat(pdf_position_y.getText()),
                Float.parseFloat(pdf_position_width.getText()),
                Float.parseFloat(pdf_position_height.getText())
        );

        pageView.setImage(pdfToImage(preview));
    }

    Image pdfToImage(File input) throws IOException {
        PDDocument document = PDDocument.load(input);
        PDFRenderer renderer = new PDFRenderer(document);

        BufferedImage image = renderer.renderImageWithDPI(0, 300, ImageType.RGB);
        document.close();

        return SwingFXUtils.toFXImage(image, null);
    }

    public void init() {
        try {
            settings = new Settings();
        } catch (IOException ex) {
            System.out.println("ERROR: " + ex.getMessage());
            utils.showDialog("A problem occured", ex.getMessage());
        }

        // Load settings
        pdf_position_x.setText(Float.toString(settings.getPDFPositionX()));
        pdf_position_y.setText(Float.toString(settings.getPDFPositionY()));
        pdf_position_width.setText(Float.toString(settings.getPDFPositionWidth()));
        pdf_position_height.setText(Float.toString(settings.getPDFPositionHeight()));
        pdf_rotation.setText(Integer.toString(settings.getPDFRotation()));
        adobe_reader_filename.setText(settings.getAdobeReaderExec());
    }
}
