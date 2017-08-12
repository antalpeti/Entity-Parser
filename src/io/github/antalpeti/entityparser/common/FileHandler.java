package io.github.antalpeti.entityparser.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
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

  public void listDirectories(File directory, List<File> files) {
    File[] fileList = directory.listFiles();
    for (File file : fileList) {
      if (file.isDirectory()) {
        files.add(file);
        listDirectories(file, files);
      } else if (file.isFile()) {
        continue;
      }
    }
  }

  /**
   * Count all files in the directory and all sub directories.
   * 
   * @param directory
   *          actual examined directory
   * @param countedFilesList
   *          this is for the sum of the files
   * @param files
   *          the collection of found files
   * @param extension
   *          if no extension then count all files
   */
  public void listFiles(File directory, List<Double> countedFilesList, List<File> files, String extension) {
    File[] fileList = directory.listFiles();
    for (File file : fileList) {
      if (file.isDirectory()) {
        listFiles(file, countedFilesList, files, extension);
      } else if (file.isFile()) {
        if (!Util.isEmpty(extension) && file.getName().endsWith(extension)) {
          files.add(file);
          countedFilesList.set(0, countedFilesList.get(0) + 1d);
        } else if (Util.isEmpty(extension)) {
          files.add(file);
          countedFilesList.set(0, countedFilesList.get(0) + 1d);
        }
        continue;
      }
    }
  }
}
