package edu.uoc.practica.bd.util;

public class Column {

  private String label;
  private int columnWidth;
  private String attributeName;

  public Column() {
    super();
  }

  public Column(String label, int columnWidth, String attributeName) {
    super();
    this.setLabel(label);
    this.setColumnWidth(columnWidth);
    this.setAttributeName(attributeName);
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public int getColumnWidth() {
    return columnWidth;
  }

  public void setColumnWidth(int columnWidth) {
    if (columnWidth < this.getLabel().length()) {
      this.columnWidth = this.getLabel().length();
    } else {
      this.columnWidth = columnWidth;
    }
  }

  public String getAttributeName() {
    return attributeName;
  }

  public void setAttributeName(String attributeName) {
    this.attributeName = attributeName;
  }
}
