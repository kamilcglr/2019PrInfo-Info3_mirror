/**
 * 
 */
package fr.tse.ProjetInfo3.mvc.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author ALAMI IDRISSI Taha This class will use the Design Pattern Singleton
 *         so we could have an instance of Connection class That we'll use to
 *         connect to our database
 */
public class SingletonDBConnection {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:~/user";

	// Database credentials
	static final String USER = "sa";
	static final String PASS = "";

	// create a private connection (can be accessed only by the
	// SingletonDBConnection class)
	private static Connection connection;

	// create a private Constructor ( can be accessed only via the getters of this
	// class)
	private SingletonDBConnection() {

		try {
			// STEP 1: Register JDBC driver
			Class.forName(JDBC_DRIVER);
			connection = DriverManager.getConnection(DB_URL, USER, PASS);

		} catch (SQLException se) {
			se.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	// the getters that will return the connection
	public static Connection getInstance() {
		if (connection == null) {
			new SingletonDBConnection();
		}
		return connection;
	}

}
