package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {
	@Override
	String search(String text) throws Exception {	// text is the input text the user types in.
		//Write your code here
		String result = null;
		String pg_getResponse = "SELECT response FROM chat_keyphrases WHERE lower(keyphrase) = lower('" + text + "')";
		
		Connection postgres_connect = null;
		PreparedStatement pg_stmt = null;
		ResultSet rs = null;
		
		try {
			postgres_connect = getConnection();
			pg_stmt = postgres_connect.prepareStatement(pg_getResponse);
			rs = pg_stmt.executeQuery();
			
			while (rs.next()) {
				result = rs.getString("response");
			}
		} catch (SQLException e) {
			log.info("SQLException while reading from DB: {}", e.toString());
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pg_stmt != null)
					pg_stmt.close();
				if (postgres_connect != null)
					postgres_connect.close();
			} catch (SQLException ex) {
				log.info("SQLException while closing DB: {}", ex.toString());
			}
		}
		
		if (result != null)
			return result;
		throw new Exception("NOT FOUND");
	}
	
	
	private Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

		log.info("Username: {} Password: {}", username, password);
		log.info ("dbUrl: {}", dbUrl);
		
		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}

}
