package edu.uoc.practica.bd.uocdb.exercise2;

import edu.uoc.practica.bd.util.DBAccessor;
import edu.uoc.practica.bd.util.FileUtilities;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Exercise2InsertAndUpdateDataFromFile {

  private FileUtilities fileUtilities;

  public Exercise2InsertAndUpdateDataFromFile() {
    super();
    fileUtilities = new FileUtilities();
  }

  public static void main(String[] args) {
    Exercise2InsertAndUpdateDataFromFile app = new Exercise2InsertAndUpdateDataFromFile();
    app.run();
  }

  private void run() {
    List<List<String>> fileContents = null;

    try {
      fileContents = fileUtilities.readFileFromClasspath("exercise2.data");
    } catch (FileNotFoundException e) {
      System.err.println("ERROR: File not found");
      e.printStackTrace();
    } catch (IOException e) {
      System.err.println("ERROR: I/O error");
      e.printStackTrace();
    }

		if (fileContents == null) {
			return;
		}

    DBAccessor dbaccessor = new DBAccessor();
    dbaccessor.init();
    Connection conn = dbaccessor.getConnection();

	if (conn == null) {
		return;
	}

	// TODO Prepare everything before updating or inserting
	PreparedStatement psUpdateOwner = null;
	PreparedStatement psInsertOwner = null;
	PreparedStatement psUpdateDog = null;
	PreparedStatement psInsertDog = null;
	PreparedStatement psInsertAccount = null;
	PreparedStatement psUpdatePriorities = null;
	ResultSet rs = null;

    try {    	
      // TODO Update or insert the owner, dog and bank_account from every row in file
	String sqlUpdateOwner = "UPDATE owner SET phone = ?, address = ? WHERE id_owner = ?";
	String sqlInsertOwner = "INSERT INTO owner VALUES (?,?,?,?)";
	String sqlUpdateDog = "UPDATE dog SET breed = ?, birth = ?, sex = ?, color = ?, fur = ?, id_owner = ? WHERE id_dog = ?";
	String sqlInsertDog = "INSERT INTO dog (id_dog, name_dog, breed, birth, sex, color, fur, id_owner) VALUES (?,?,?,?,?,?,?,?)";
	String sqlInsertAccount = "INSERT INTO bank_account (name_owner, iban, priority, id_owner) VALUES (?,?,1,?) RETURNING id_account";
	String sqlUpdatePriorities = "UPDATE bank_account SET priority = priority + 1 WHERE id_owner = ? AND id_account <> ?";

	psUpdateOwner = conn.prepareStatement(sqlUpdateOwner);
	psInsertOwner = conn.prepareStatement(sqlInsertOwner);
	psUpdateDog = conn.prepareStatement(sqlUpdateDog);
	psInsertDog = conn.prepareStatement(sqlInsertDog);
	psInsertAccount = conn.prepareStatement(sqlInsertAccount);
	psUpdatePriorities = conn.prepareStatement(sqlUpdatePriorities);


	for (List <String> row: fileContents) {
		//update owner
		psUpdateOwner.clearParameters();
		setPSUpdateOwner(psUpdateOwner, row);

		if (psUpdateOwner.executeUpdate() == 0) {
			System.out.println("Owner does not exist. Inserting ...");

			psInsertOwner.clearParameters();
			setPSInsertOwner(psInsertOwner, row);
			psInsertOwner.executeUpdate();
		} else {
			System.out.println("Owner updated correctly.");
		}

		//update dog
		psUpdateDog.clearParameters();
		setPSUpdateDog(psUpdateDog, row);

		if (psUpdateDog.executeUpdate() == 0) {
			System.out.println("Dog does not exist. Inserting ...");

			psInsertDog.clearParameters();
			setPSInsertDog(psInsertDog, row);
			psInsertDog.executeUpdate();
		} else {
			System.out.println("Dog updated correctly.");
		}

		//create new bank account
		psInsertAccount.clearParameters();
		setPSInsertAccount(psInsertAccount, row);

		rs = psInsertAccount.executeQuery();

		//Returns generated id_account primary key
		if (rs.next()) {
			System.out.println("Bank Account with id_account = " + rs.getLong(1) + " created on table BANK_ACCOUNT");;

			// Update priorities
			psUpdatePriorities.clearParameters();
			setPSUpdatePriorities(psUpdatePriorities, row, rs.getLong(1));
			psUpdatePriorities.executeUpdate();
		}
	}

		// TODO Validate transaction
		conn.commit();
    }
    // TODO Close resources and check exceptions
	catch (SQLException e1) {
		System.err.println("ERROR: Executing SQL on OWNER or DOG or BANK_ACCOUNT");
		System.err.println(e1.getMessage());

		try {
			System.err.println("Rolling back and reverting changes");
			conn.rollback();
		} catch (SQLException e2) {
			System.out.println("Message:  " + e2.getMessage());
			System.out.println("SQLState:  " + e2.getSQLState());
			System.out.println("ErrorCode: " + e2.getErrorCode());
		}
	} finally {
		if (psUpdateOwner != null) {
			try {
				psUpdateOwner.close();
			} catch (SQLException e) {
				System.err.println("ERROR: Closing connection UPDATE owner.");
				System.err.println(e.getMessage());
			}
		}

		if (psInsertOwner != null) {
			try {
				psInsertOwner.close();
			} catch (SQLException e) {
				System.err.println("ERROR: Closing connection INSERT owner.");
				System.err.println(e.getMessage());
			}
		}

		if (psUpdateDog != null) {
			try {
				psUpdateDog.close();
			} catch (SQLException e) {
				System.err.println("ERROR: Closing connection UPDATE dog.");
				System.err.println(e.getMessage());
			}
		}

		if (psInsertDog != null) {
			try {
				psInsertDog.close();
			} catch (SQLException e) {
				System.err.println("ERROR: Closing connection INSERT dog.");
				System.err.println(e.getMessage());
			}
		}

		if (psInsertAccount != null) {
			try {
				psInsertAccount.close();
			} catch (SQLException e) {
				System.err.println("ERROR: Closing connection INSERT bank_account.");
				System.err.println(e.getMessage());
			}
		}

		if (psUpdatePriorities != null) {
			try {
				psUpdatePriorities.close();
			} catch (SQLException e) {
				System.err.println("ERROR: Closing connection UPDATE bank_account.");
				System.err.println(e.getMessage());
			}
		}

		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				System.err.println("ERROR: Closing ResultSet rs.");
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

  private java.sql.Date getBirthDate(String date) {
	  try {
		  Calendar c = Calendar.getInstance();
		  c.setTime(getDateFromStringOrNull(date));
		  java.sql.Date sqlDate = new java.sql.Date(c.getTime().getTime());
		  return sqlDate;
		  
	} catch (Exception e) {
		System.err.println("ERROR: getting birth date");
        System.err.println(e.getMessage());
		return null;
	}
	  }

  private void setPSUpdateOwner(PreparedStatement updateStatement, List<String> row)
      throws SQLException {
    String[] rowArray = (String[]) row.toArray(new String[0]);
    
    setValueOrNull(updateStatement, 1, getValueIfNotNull(rowArray, 2)); // phone
    setValueOrNull(updateStatement, 2, getValueIfNotNull(rowArray, 3)); // address
    setValueOrNull(updateStatement, 3, 
    		getIntegerFromStringOrNull(getValueIfNotNull(rowArray, 0))); // id_owner
  }

  private void setPSInsertOwner(PreparedStatement insertStatement, List<String> row)
      throws SQLException {
    String[] rowArray = (String[]) row.toArray(new String[0]);

    setValueOrNull(insertStatement, 1,
            getIntegerFromStringOrNull(getValueIfNotNull(rowArray, 0)));  // id_owner
    setValueOrNull(insertStatement, 2, getValueIfNotNull(rowArray, 1)); // name_owner
    setValueOrNull(insertStatement, 3, getValueIfNotNull(rowArray, 2)); // phone
    setValueOrNull(insertStatement, 4, getValueIfNotNull(rowArray, 3));  // address
    
  }
  
  private void setPSUpdateDog(PreparedStatement updateStatement, List<String> row)
	      throws SQLException {
	    String[] rowArray = (String[]) row.toArray(new String[0]);
	    
	    setValueOrNull(updateStatement, 1, getValueIfNotNull(rowArray, 6)); // breed
	    setValueOrNull(updateStatement, 2, getBirthDate(getValueIfNotNull(rowArray, 7))); // birth
	    setValueOrNull(updateStatement, 3, getValueIfNotNull(rowArray, 8)); // sex
	    setValueOrNull(updateStatement, 4, getValueIfNotNull(rowArray, 9)); // color
	    setValueOrNull(updateStatement, 5, getValueIfNotNull(rowArray, 10)); // fur
	    setValueOrNull(updateStatement, 6, 
	    		getIntegerFromStringOrNull(getValueIfNotNull(rowArray, 0))); // id_owner
        setValueOrNull(updateStatement, 7, 
	    		getIntegerFromStringOrNull(getValueIfNotNull(rowArray, 4))); // id_dog
  }

  private void setPSInsertDog(PreparedStatement insertStatement, List<String> row)
	      throws SQLException {
	    String[] rowArray = (String[]) row.toArray(new String[0]);

	    setValueOrNull(insertStatement, 1,
	            getIntegerFromStringOrNull(getValueIfNotNull(rowArray, 4)));  // id_dog
	    setValueOrNull(insertStatement, 2, getValueIfNotNull(rowArray, 5)); // name_dog
	    setValueOrNull(insertStatement, 3, getValueIfNotNull(rowArray, 6)); // breed
	    setValueOrNull(insertStatement, 4, getBirthDate(getValueIfNotNull(rowArray, 7)));  // birth
	    setValueOrNull(insertStatement, 5, getValueIfNotNull(rowArray, 8));  // sex
	    setValueOrNull(insertStatement, 6, getValueIfNotNull(rowArray, 9));  // color
	    setValueOrNull(insertStatement, 7, getValueIfNotNull(rowArray, 10));  // fur
	    setValueOrNull(insertStatement, 8, 
	    		getIntegerFromStringOrNull(getValueIfNotNull(rowArray, 0)));   // id_owner
	    
	    
	    
  }

  private void setPSInsertAccount(PreparedStatement insertStatement, List<String> row)
      throws SQLException {
    String[] rowArray = (String[]) row.toArray(new String[0]);

    setValueOrNull(insertStatement, 1, getValueIfNotNull(rowArray, 1)); // name_owner
    setValueOrNull(insertStatement, 2, getValueIfNotNull(rowArray, 11)); // iban
    setValueOrNull(insertStatement, 3,
    		getIntegerFromStringOrNull(getValueIfNotNull(rowArray, 0))); // id_owner
  }
  
  private void setPSUpdatePriorities(PreparedStatement insertStatement, List<String> row, Long id_account)
      throws SQLException {
    String[] rowArray = (String[]) row.toArray(new String[0]);

    setValueOrNull(insertStatement, 1,
    		getIntegerFromStringOrNull(getValueIfNotNull(rowArray, 0))); // id_owner
    setValueOrNull(insertStatement, 2, 
    		id_account); // id_account
  }


	private Integer getIntegerFromStringOrNull(String integer) {
    return (integer != null) ? Integer.valueOf(integer) : null;
  }

  private String getValueIfNotNull(String[] rowArray, int index) {
    return (index < rowArray.length && rowArray[index].length() > 0) ? rowArray[index] : null;
  }

  private void setValueOrNull(PreparedStatement preparedStatement, int parameterIndex,
      Integer value) throws SQLException {
		if (value == null) {
			preparedStatement.setNull(parameterIndex, Types.INTEGER);
		} else {
			preparedStatement.setInt(parameterIndex, value);
		}
  }
  
  private void setValueOrNull(PreparedStatement preparedStatement, int parameterIndex,
	      Long value) throws SQLException {
			if (value == null) {
				preparedStatement.setNull(parameterIndex, Types.INTEGER);
			} else {
				preparedStatement.setInt(parameterIndex, Integer.valueOf(value.intValue()));
			}
	  }

  
  
  private void setValueOrNull(PreparedStatement preparedStatement, int parameterIndex, String value)
      throws SQLException {
		if (value == null || value.length() == 0) {
			preparedStatement.setNull(parameterIndex, Types.VARCHAR);
		} else {
			preparedStatement.setString(parameterIndex, value);
		}
  }
  
  private void setValueOrNull(PreparedStatement preparedStatement, int parameterIndex, java.sql.Date date)
	      throws SQLException {
			if (date == null) {
				preparedStatement.setNull(parameterIndex, Types.DATE);
			} else {
				preparedStatement.setDate(parameterIndex, date);
			}
	  }
  
  /**
   * Function that, given a date in String format, returns the date in Date format. 
   */
  private java.sql.Date getDateFromStringOrNull(String date) {
		if (date == null) {
			return null;
		}
		try {
			Date d = new SimpleDateFormat("yyyy-MM-dd").parse(date);
			java.sql.Date sqlDate = new java.sql.Date(d.getTime());
			return sqlDate;
		} catch (ParseException e) {
			return null;
		}
  }




}
