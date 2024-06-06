package edu.uoc.practica.bd.util;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Class to show lists of data in the user interface
 */
public class Report {

  private List<Column> columns;

  private PrintWriter out = null;

  /**
   * Default constructor
   */
  public Report() {
    super();
    String encoding = System.getProperty("file.encoding");
    setPrintWriter(encoding);
  }

  /**
   * Shows the list on the screen
   *
   * @param list List to be shown
   */
  public void printReport(List<?> list) {
    if (!list.isEmpty()) {
      printHeader();
    }
    for (Object object : list) {
      printLine(object);
    }
    out.flush();
  }

  /**
   * Sets the printWriter that is going to be use to print on the screen.
   *
   * @param codepage Code to apply
   */
  private void setPrintWriter(String codepage) {
    OutputStreamWriter osw = null;
    try {
      osw = new OutputStreamWriter(System.out, codepage);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      throw new RuntimeException("ERROR: Setting the Print Writer", e);
    }
    out = new PrintWriter(osw);
  }

  /**
   * Writes the list header
   */
  private void printHeader() {
    StringBuffer head = new StringBuffer();
    StringBuffer line = new StringBuffer();

    for (Column column : columns) {
      head.append(FormatUtilities.fillWithSpacesOnTheRight(
          column.getLabel(), column.getColumnWidth()));
      head.append(" ");

      for (int j = 0; j < column.getColumnWidth(); j++) {
        line.append("=");
      }
      line.append(" ");
    }
    out.println(head.toString());
    out.println(line.toString());
  }

  /**
   * Writes one line of the list
   *
   * @param bean Element to be written
   */
  private void printLine(Object bean) {
    Method method = null;
    Object value = null;
    Class<? extends Object> beanClass = bean.getClass();
    StringBuffer sb = new StringBuffer();

    for (Column column : columns) {
      try {
        String methodName = "get_" + column.getAttributeName();
        method = beanClass.getMethod(methodName, new Class[0]);
      } catch (SecurityException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      }

      try {
        value = method.invoke(bean, new Object[0]);
      } catch (IllegalArgumentException e1) {
        e1.printStackTrace();
      } catch (IllegalAccessException e1) {
        e1.printStackTrace();
      } catch (InvocationTargetException e1) {
        e1.printStackTrace();
      }

      String message = null;
      if (value == null) {
        message = FormatUtilities.fillWithSpacesOnTheRight("-", column.getColumnWidth());
      } else if (value instanceof Integer || value instanceof Float) {
        message = FormatUtilities.fillWithSpacesOnTheLeft(
            value.toString(), column.getColumnWidth());
      } else {
        message = FormatUtilities.fillWithSpacesOnTheRight(value.toString(),
            column.getColumnWidth());
      }

      sb.append(message);
      sb.append(" ");
    }
    out.println(sb.toString());
  }

  public List<Column> getColumns() {
    return columns;
  }

  public void setColumns(List<Column> columns) {
    this.columns = columns;
  }
}
