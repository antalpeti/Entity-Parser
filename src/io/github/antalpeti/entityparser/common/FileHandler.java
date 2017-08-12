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

  public OutputStream createOutputStream(String filePath) {
    OutputStream outputStream = null;
    try {
      outputStream = new FileOutputStream(filePath);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return outputStream;
  }

  public InputStream createInputStream(String filePath) {
    InputStream inputStream = null;
    try {
      inputStream = new FileInputStream(filePath);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return inputStream;
  }

  public void storeProperties(final File selectedDirectory, String filePath, String key, String value) {
    OutputStream outputStream = createOutputStream(filePath);
    Properties properties = new Properties();
    properties.setProperty(key, value);
    try {
      properties.store(outputStream, null);
    } catch (IOException e1) {
      e1.printStackTrace();
    } finally {
      if (outputStream != null) {
        try {
          outputStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public String loadProperty(String filePath, String key) {
    InputStream inputStream = createInputStream(filePath);
    Properties properties = new Properties();
    try {
      properties.load(inputStream);
      return properties.getProperty(key);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return null;
  }
}
