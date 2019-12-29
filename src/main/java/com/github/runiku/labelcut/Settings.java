package com.github.runiku.labelcut;

import java.io.*;
import java.util.Properties;

public class Settings {

    Properties properties;

    File configDir;

    File configFile;

    public boolean isAutoReader() {
        return Boolean.parseBoolean(properties.getProperty("auto_reader", Boolean.toString(true)));
    }

    public void setAutoReader(boolean value) {
        properties.setProperty("auto_reader", Boolean.toString(value));
    }

    public String getAdobeReaderExec() {
        return properties.getProperty("adobe_reader_exec", "AcroRd32.exe");
    }

    public void setAdobeReaderExec(String value) {
        properties.setProperty("adobe_reader_exec", value);
    }

    public float getPDFPositionX() {
        return Float.parseFloat(properties.getProperty("pdf_position_x", Float.toString(20)));
    }

    public void setPDFPositionX(float value) {
        properties.setProperty("pdf_position_x", Float.toString(value));
    }

    public float getPDFPositionY() {
        return Float.parseFloat(properties.getProperty("pdf_position_y", Float.toString(665)));
    }

    public void setPDFPositionY(float value) {
        properties.setProperty("pdf_position_y", Float.toString(value));
    }

    public float getPDFPositionWidth() {
        return Float.parseFloat(properties.getProperty("pdf_position_width", Float.toString(270)));
    }

    public void setPDFPositionWidth(float value) {
        properties.setProperty("pdf_position_width", Float.toString(value));
    }

    public float getPDFPositionHeight() {
        return Float.parseFloat(properties.getProperty("pdf_position_height", Float.toString(100)));
    }

    public void setPDFPositionHeight(float value) {
        properties.setProperty("pdf_position_height", Float.toString(value));
    }

    public int getPDFRotation() {
        return Integer.parseInt(properties.getProperty("pdf_rotation", Integer.toString(90)));
    }

    public void setPDFRotation(int value) {
        properties.setProperty("pdf_rotation", Integer.toString(value));
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Settings() throws IOException {
        String path = System.getenv("APPDATA");
        if (pathExists(path)) {
            configDir = new File(path + "\\LabelPrinting");
            if (!configDir.exists())
                configDir.mkdirs();

            configFile = new File(configDir.getPath() + "\\properties.json");
            if (!configFile.exists())
                configFile.createNewFile();
        }

        properties = new Properties();
        properties.load(new FileReader(configFile));
    }

    public void save() throws IOException {
        properties. store(new FileWriter(configFile), null);
    }

    boolean pathExists(String path) {
        File file = new File(path);
        if(file.isDirectory() && file.exists()) {
            return true;
        } else {
            return false;
        }
    }
}
