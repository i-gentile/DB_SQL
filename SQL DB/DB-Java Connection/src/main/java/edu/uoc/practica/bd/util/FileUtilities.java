package edu.uoc.practica.bd.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods to manage file content.
 */
public class FileUtilities {

  /**
   * Reads a comma separated file from the classpath.
   */
  public List<List<String>> readFileFromClasspath(String file)
      throws FileNotFoundException, IOException {
    InputStream is = getClass().getClassLoader().getResourceAsStream(file);
    InputStreamReader inputStreamReader = new InputStreamReader(is);
    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
    return readFileFromBufferedReader(bufferedReader);
  }

  /**
   * Reads a comma separated file from the filesystem.
   */
  public List<List<String>> readFileFromFilesystem(String file)
      throws FileNotFoundException, IOException {
    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
    return readFileFromBufferedReader(bufferedReader);
  }

  /**
   * Reads a comma separacted file from a Reader.
   * <ul>
   * <li>Lines started with '#' are ignored.</li>
   * <li>Spaces before and after the comma are ignored.</li>
   * <li>Fields can be surrounded by quotes.
   */
  private List<List<String>> readFileFromBufferedReader(
      BufferedReader bufferedReader) throws FileNotFoundException,
      IOException {
    List<List<String>> fileRows = new ArrayList<List<String>>();
    String line = bufferedReader.readLine();
    while (line != null && line.length() > 0) {
      if (line.charAt(0) != '#') {
        List<String> rowValues = new ArrayList<String>();
        String[] tokens = line
            .split(",(?=([^\"]*\"[^\"]*\")*(?![^\"]*\"))");
        for (String token : tokens) {
          String processedToken = stripQutoes(token);
          rowValues.add(processedToken);
        }
        fileRows.add(rowValues);
      }
      line = bufferedReader.readLine();
    }
    bufferedReader.close();
    return fileRows;
  }

  private String stripQutoes(String token) {
    String tokenWithoutSpaces = token.trim();
    if (tokenWithoutSpaces.length() > 0) {
      if (tokenWithoutSpaces.charAt(0) == '"'
          && tokenWithoutSpaces
          .charAt(tokenWithoutSpaces.length() - 1) == '"') {
        return tokenWithoutSpaces.substring(1,
            tokenWithoutSpaces.length() - 1);
      }
    }
    return tokenWithoutSpaces;
  }
}