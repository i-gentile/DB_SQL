package edu.uoc.practica.bd.util;

/**
 * Helper methods to manage Arrays.
 */
public class ArrayUtilities {

  /**
   * Returns true if the passed value is present in the passed array, ignoring upper case.
   *
   * @param array Array where to look for the value
   * @param value Value to look in the array
   * @return True if the value is in the array.
   */
  public static boolean isValueInArray(String[] array, String value) {
    boolean ret = false;

    for (int i = 0; i < array.length; i++) {
      if (array[i].equalsIgnoreCase(value)) {
        ret = true;
        break;
      }
    }
    return ret;
  }
}