package com.github.runiku.labelcut;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.util.Matrix;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Controller {

    Settings settings;

    Stage stage;

    File currentFile = null;

    @FXML
    Button fileButton;

    @FXML
    ScrollPane scrollPaneFit;

    @FXML
    ScrollPane scrollPaneOriginal;

    @FXML
    ImageView pageViewFit;

    @FXML
    ImageView pageViewOriginal;

    @FXML
    CheckBox autoReaderCheckbox;

    @FXML
    Button settingsButton;

    void setStage(Stage stage) {
        this.stage = stage;
    }

    void setCurrentFile(String path) {
        try {
            currentFile = new File(path);
            File output = generatePDF(currentFile);
            pageViewOriginal.setImage(pdfToImage(currentFile));
            pageViewFit.setImage(pdfToImage(output));
            if (autoReaderCheckbox.isSelected()) {
                openInAdobe(output);
            }
        } catch (IOException ex) {
            System.out.println("ERROR: " + ex.getMessage());
            utils.showDialog("A problem occured", ex.getMessage());
        }
    }

    @FXML
    void onAutoReaderCheckboxAction(ActionEvent event) {
        System.out.println(autoReaderCheckbox.isSelected());
        settings.setAutoReader(autoReaderCheckbox.isSelected());
        try {
            settings.save();
        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
            utils.showDialog("A problem occured", ex.getMessage());
        }
    }

    @FXML
    void onOpenADRAction() {
        if (currentFile == null) {
            utils.showDialog("No PDF read", "Please open a PDF file first");
            return;
        }

        try {
            openInAdobe(currentFile);
        } catch (IOException ex) {
            System.out.println("ERROR: " + ex.getMessage());
            utils.showDialog("A problem occured", ex.getMessage());
        }
    }

    @FXML
    void onSettingsButtonAction(ActionEvent event) {
        openSettingsStage();
    }

    @FXML
    void onScrollScrollPaneFit(ScrollEvent event) {
    }

    @FXML
    void onInfoAction() {
        utils.showDialog(
                "About",
                "used Icons and libraries\n\n" +
                        "Ant Design icons by HeskeyBaozi (https://github.com/ant-design/ant-design-icons)\n\n" +
                        "Apache PDFBox (pdfbox.apache.org)"
        );
    }

    @FXML
    void onFileButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF file (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Select Label PDF");
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                File output = generatePDF(file);
                currentFile = output;
                pageViewOriginal.setImage(pdfToImage(file));
                pageViewFit.setImage(pdfToImage(output));
                if (autoReaderCheckbox.isSelected()) {
                    openInAdobe(output);
                }
            } catch (IOException ex) {
                System.out.println("ERROR: " + ex.getMessage());
                utils.showDialog("A problem occured", ex.getMessage());
            }
        }

    }

    File generatePDF(File input) throws IOException {
        String tmpPath = System.getProperty("java.io.tmpdir");
        File tmpDir = new File(tmpPath);
        if (!tmpDir.exists())
            tmpDir.mkdirs();

        File output = new File(tmpDir.getPath() + "\\" + "output.pdf");
        cropPDFToSize(input, output);

        System.out.println(output.getPath());

        return output;
    }

    Image pdfToImage(File input) throws IOException {
        PDDocument document = PDDocument.load(input);
        PDFRenderer renderer = new PDFRenderer(document);

        BufferedImage image = renderer.renderImageWithDPI(0, 300, ImageType.RGB);
        document.close();

        return SwingFXUtils.toFXImage(image, null);
    }

    void cropPDFToSize(File input, File output) throws IOException {
        PDDocument pdDocument = PDDocument.load(input);

        PDPage page = pdDocument.getPage(0);
        page.setRotation(settings.getPDFRotation()); // 90
        PDPageContentStream cs = new PDPageContentStream(pdDocument, page, PDPageContentStream.AppendMode.PREPEND, false, false);
        Matrix matrix = Matrix.getRotateInstance(Math.toRadians(0), 0, 0);
        cs.transform(matrix);
        cs.close();

        page.setCropBox(new PDRectangle(
                settings.getPDFPositionX(),
                settings.getPDFPositionY(),
                settings.getPDFPositionWidth(),
                settings.getPDFPositionHeight()
        )); // x: 20, y: 665, width: 270, height: 100

        pdDocument.save(output);
        pdDocument.close();
    }

    void openInAdobe(File file) throws IOException {
        Runtime.getRuntime().exec(settings.getAdobeReaderExec() + " " + file.getPath());
    }

    void openSettingsStage() {

        try {
            Stage settingsStage = new Stage();
            settingsStage.setTitle("LabelCut Settings");

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("settings.fxml"));
            Parent root = (Parent) loader.load();
            SettingsController controller = (SettingsController) loader.getController();
            settingsStage.setScene(new Scene(root, 650, 450));
            controller.setStage(settingsStage);
            controller.init();
            settingsStage.initModality(Modality.APPLICATION_MODAL);
            settingsStage.showAndWait();

            System.out.println("INFO: Reload settings");
            settings = new Settings();
        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
            utils.showDialog("A problem occured", ex.getMessage());
        }
    }

    public void init() {
        try {
            settings = new Settings();
        } catch (IOException ex) {
            System.out.println("ERROR: " + ex.getMessage());
            utils.showDialog("A problem occured", ex.getMessage());
        }

        // Checkbox
        if (settings.isAutoReader()) {
            autoReaderCheckbox.setSelected(true);
        }
    }
}
