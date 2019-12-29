package com.github.runiku.labelcut;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.Matrix;

import java.io.File;
import java.io.IOException;

public class utils {

    public static void showDialog(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);

        TextArea area = new TextArea(text);
        area.setWrapText(true);
        area.setEditable(false);

        alert.getDialogPane().setContent(area);
        alert.show();
    }

    public static void cropPDFToSize(File input, File output, int rotation, float x, float y, float width, float height)
    throws IOException {
        PDDocument pdDocument = PDDocument.load(input);

        PDPage page = pdDocument.getPage(0);
        page.setRotation(rotation);
        PDPageContentStream cs = new PDPageContentStream(pdDocument, page, PDPageContentStream.AppendMode.PREPEND, false, false);
        Matrix matrix = Matrix.getRotateInstance(Math.toRadians(0), 0, 0);
        cs.transform(matrix);
        cs.close();

        page.setCropBox(new PDRectangle(x, y, width, height));

        pdDocument.save(output);
        pdDocument.close();
    }

    public static File generatePDF(File input, String outputName, int rotation, float x, float y, float width, float height) throws IOException {
        String tmpPath = System.getProperty("java.io.tmpdir");
        File tmpDir = new File(tmpPath);
        if (!tmpDir.exists())
            tmpDir.mkdirs();

        File output = new File(tmpDir.getPath() + "\\" + outputName);
        cropPDFToSize(input, output, rotation, x, y, width, height);

        System.out.println(output.getPath());

        return output;
    }
}
