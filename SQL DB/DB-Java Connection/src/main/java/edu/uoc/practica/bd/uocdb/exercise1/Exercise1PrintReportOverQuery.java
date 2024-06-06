package edu.uoc.practica.bd.uocdb.exercise1;

import edu.uoc.practica.bd.util.Column;
import edu.uoc.practica.bd.util.DBAccessor;
import edu.uoc.practica.bd.util.Report;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Exercise1PrintReportOverQuery {

  public static void main(String[] args) {
    Exercise1PrintReportOverQuery app = new Exercise1PrintReportOverQuery();
    app.run();
  }

  private void run() {
    DBAccessor dbaccessor = new DBAccessor();
    dbaccessor.init();
    Connection conn = dbaccessor.getConnection();

    if (conn != null) {
      Statement cstmt = null;
      ResultSet resultSet = null;

      try {

    	  List<Column> columns = Arrays.asList(new Column("Account", 28, "iban"),
    	            new Column("Holder", 16, "name_owner"),
    	            new Column("Date", 10, "date"),
    	            new Column("Name Dog", 9, "name_dog"),
    	            new Column("Name Vaccine", 13, "name_vaccine"),
    	            new Column("Price", 5, "price"),
    	            new Column("Comments", 20, "comments"));

        Report report = new Report();
        report.setColumns(columns);
        List<Object> list = new ArrayList<Object>();

        // TODO Execute SQL sentence
          cstmt = conn.createStatement();
          resultSet = cstmt.executeQuery("SELECT * FROM INVOICES");

        // TODO Loop over results and get the main values
          String iban;
          String name_owner;
          String date;
          String name_dog;
          String name_vaccine;
          Double price;
          String comments;

          while (resultSet.next()) {
              iban = resultSet.getString(1);
              name_owner = resultSet.getString(2);
              date = resultSet.getString(3);
              name_dog = resultSet.getString(4);
              name_vaccine = resultSet.getString(5);
              price = resultSet.getDouble(6);
              comments = resultSet.getString(7);

              Exercise1Row row = new Exercise1Row(
                      iban,
                      name_owner,
                      date,
                      name_dog,
                      name_vaccine,
                      price,
                      comments);
              list.add(row);
          }

        // TODO End loop
          if (list.isEmpty()) {
              System.out.println("List without data");
          } else {
              report.printReport(list);
          }
      } catch (SQLException e) {
          System.err.println("ERROR: List not available");
          System.err.println(e.getMessage());
      }
      // TODO Close All resources
      finally {
          if (resultSet != null) {
              try {
                  resultSet.close();
              } catch (SQLException e) {
                  System.err.println("ERROR: Closing resultSet");
                  System.err.println(e.getMessage());
              }
          }
          if (cstmt != null) {
              try {
                  cstmt.close();
              } catch (SQLException e) {
                  System.err.println("ERROR: Closing statement");
                  System.err.println(e.getMessage());
              }
          }
          if (conn != null) {
              try {
                  conn.close();
              } catch (SQLException e) {
                  System.err.println("ERROR: Closing connection");
                  System.err.println(e.getMessage());
              }
          }
      }
    }
  }
}
