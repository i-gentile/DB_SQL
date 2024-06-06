package edu.uoc.practica.bd.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Some helper methods to get data and format it.
 */
public class FormatUtilities {

  private static final int ALIGN_RIGHT = 0;
  private static final int ALIGN_LEFT = 1;

  public static String fillWithSpacesOnTheRight(String text, int width) {
    return fillWithSpaces(text, width, ALIGN_LEFT);
  }

  public static String fillWithSpacesOnTheLeft(String text, int width) {
    return fillWithSpaces(text, width, ALIGN_RIGHT);
  }

  /**
   * Adds blanks before or after the <code>text</code> passed as parameter until the text has the
   * width specified by the <code>width</code> parameter.
   *
   * @param text      Text to format
   * @param width     Final number of chars in the string
   * @param alignment Place where to add the characters
   * @return The text passed as parameter filled with spaces, before or after according to
   * <code>position</code> and with the width indicated by <code>size</code>
   */
  private static String fillWithSpaces(String text, int width, int alignment) {

    StringBuffer sb = new StringBuffer();
    String aux = "";
    int whitespaces;

    if (text != null) {
      aux = text;
    }

    whitespaces = width - aux.length();

    if (whitespaces > 0) {
      if (alignment == ALIGN_LEFT) {
        sb.append(aux);
      }
      for (int i = 0; i < whitespaces; i++) {
        sb.append(" ");
      }
      if (alignment == ALIGN_RIGHT) {
        sb.append(aux);
      }
    } else {
      sb.append(aux.substring(0, width));
    }

    return sb.toString();
  }

  /**
   * Capitalizes the first letter of the string
   *
   * @param text String to capitalize
   * @return String with the first letter capitalized
   */
  public static String capitalize(String text) {
    if (text == null || text.length() == 0) {
      return "";
    }

    char chars[] = text.toCharArray();
    chars[0] = Character.toUpperCase(chars[0]);
    return new String(chars);
  }

  /**
   * Allows to query the user through the screen and returns a string with the user input. A set of
   * accepted values can be defined. Use null if no restriction has to be applied.
   *
   * @param message        Message to show to the user
   * @param possibleValues Accepted values.
   * @return User input
   */
  public static String getStringFromConsole(String message,
      String[] possibleValues) {
    String ret = null;
    boolean ok = false;

    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    try {
      do {
        printMessageWithAllowedValues(message, possibleValues);
        ret = in.readLine();
        if (possibleValues != null) {
          if (ArrayUtilities.isValueInArray(possibleValues, ret)) {
            ok = true;
          } else {
            System.out.println("Not a valid value");
          }
        } else {
          ok = true;
        }
      } while (!ok);
    } catch (IOException ex) {
      System.out.println("Error processing the data introduced");
    }
    return ret;
  }

  /**
   * Allows to query the user through the screen and returns an int with the user input.
   *
   * @param message Message to show to the user
   * @return User input
   */
  public static Integer getIntegerFromConsole(String message) {
    Integer ret = null;
    String aux = null;
    boolean ok = false;

    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    do {
      System.out.println(message);
      try {
        aux = in.readLine();
        ret = Integer.valueOf(aux);
        ok = true;
      } catch (IOException ex) {
        ok = true;
        System.out.println("Error processing the data introduced");
      } catch (NumberFormatException ex) {
        System.out.println("Incorrect number");
      }
    } while (!ok);

    return ret;
  }

  /**
   * Shows a message to the user, showing possible values in brackets.
   *
   * @param message       Message to show to the user.
   * @param allowedValues List of allowed values.
   */
  private static void printMessageWithAllowedValues(String message,
      String[] allowedValues) {
    String values = "";

    if ((allowedValues != null) && (allowedValues.length > 0)) {
      StringBuffer sb = new StringBuffer();
      sb.append("(");
      for (int i = 0; i < allowedValues.length - 1; i++) {
        sb.append(allowedValues[i]).append("/");
      }
      sb.append(allowedValues[allowedValues.length - 1]);
      sb.append(")");
      values = sb.toString();
    }
    System.out.print(message + values + ": ");
  }
}