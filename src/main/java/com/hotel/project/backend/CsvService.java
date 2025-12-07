package com.hotel.project.backend;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class CsvService {
  public static final String DATA_FOLDER = "data/";

  public static <T extends Serialisable> void saveList(String filename, List<T> list) {
    List<String> lines = new ArrayList<>();
    for (T item : list) {
      lines.add(item.toCsv());
    }
    try {
      File file = new File(DATA_FOLDER, filename);
      File directory = file.getParentFile();
      // 3. FORCE directory creation if it is missing
      if (!directory.exists()) {
        System.out.println("[DEBUG] Directory missing. Creating: " + directory.getAbsolutePath());
        boolean success = directory.mkdirs();
        if (!success) {
          System.err.println("[ERROR] Failed to create directory! Check file permissions.");
        }
      }

      // 4. Write the file
      Path path = file.toPath();
      Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
      System.out.println("[CSV] Saved file to: " + path.toAbsolutePath());
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public static List<String> loadLines(String filename) {
    try {
      File file = new File(DATA_FOLDER, filename);
      Path path = file.toPath();

      // 5. Only read if file actually exists and is a file
      if (file.exists() && file.isFile()) {
        System.out.println("[DEBUG] Reading file: " + file.getAbsolutePath());
        return Files.readAllLines(path);
      } else {
        System.out.println("[DEBUG] File not found (First run?): " + file.getAbsolutePath());
      }
    } catch (IOException e) {
      System.err.println("[ERROR] Could not load " + filename);
      e.printStackTrace();
    }
    return new ArrayList<>();
  }
}
