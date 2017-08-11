package io.github.antalpeti.entityparser.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class FileHandler {
  
  private static FileHandler instance = null;
  
  public static FileHandler getInstance() {
    if (instance == null) {
      instance = new FileHandler();
    }
    return instance;
  }
  
  private static OutputStream outputStream = null;
  private static InputStream inputStream = null;
  private static Properties properties = new Properties();
  
  public void initStreams(String filePath) {
    initOutputStream(filePath);
    initInputStream(filePath);
  }

  private void initOutputStream(String filePath) {
    try {
      outputStream = new FileOutputStream(filePath);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
  
  private void initInputStream(String filePath) {
    try {
      inputStream = new FileInputStream(filePath);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void storeProperties(final File selectedDirectory, String key, String value) {
    properties.setProperty(key, value);
    try {
      properties.store(outputStream, null);
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }
  

  public String loadProperty(String key) {
    try {
      properties.load(inputStream);
      return properties.getProperty(key);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
