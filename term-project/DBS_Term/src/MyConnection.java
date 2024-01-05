

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public class MyConnection {

	public static Connection makeConnection() {
		Connection con=null;
		try {

			// load and register the Driver
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

			String url = "jdbc:sqlserver://DESKTOP-RCA42SP\\SQLEXPRESS01:1433;databaseName=LectureDB;encrypt=false";
			
			// Get connection using URL through Driver Manager
			con = DriverManager.getConnection(url, "sa", "bat3011");

			System.out.println("Database connection Established Succusfully");
			System.out.println(" Connected!..");


		} catch (Exception e) {
			System.out.println("Connection Failed");
			System.out.println(e.getMessage());
	
		}
		return con;

	}

}
