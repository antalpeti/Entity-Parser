package io.github.antalpeti.entityparser.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
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

  public InputStream createInputStream(String filename) {
    InputStream inputStream = null;
    try {
      inputStream = new FileInputStream(filename);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return inputStream;
  }

  public void storeProperties(String filename, String key, String value) {
    Properties properties = getProperties(filename);
    properties.setProperty(key, value);

    OutputStream outputStream = createOutputStream(filename);
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

  public String loadProperty(String filename, String key) {
    InputStream inputStream = createInputStream(filename);
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

  public Properties getProperties(String filename) {
    if (!isPropertiesFileExist(filename)) {
      return new Properties();
    }
    InputStream inputStream = createInputStream(filename);
    Properties properties = new Properties();
    try {
      properties.load(inputStream);
      return properties;
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

  public boolean isPropertiesFileExist(String filename) {
    Path currentRelativePath = Paths.get("");
    String configPropertiesAbsolutePath = currentRelativePath.toAbsolutePath().toString() + File.separator + filename;
    File f = new File(configPropertiesAbsolutePath);
    return f.exists() && !f.isDirectory();
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
