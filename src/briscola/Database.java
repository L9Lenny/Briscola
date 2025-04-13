package briscola;


import log.ColorLogger;
import utils.Crypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * This class handles database operations for user registration, login, and game statistics.
 */

public class Database {

	private final ColorLogger log = new ColorLogger(Database.class);

	private final Crypt c = new Crypt();
	
	private final String[] str = c.getStr();
	
	private static final String driver = "com.mysql.cj.jdbc.Driver";


    private final Connection conn;
	private final JFrame frame;
	private String nomeUtente;
	private String userPassword;

	/**
	 * Constructor for the Database class.
	 * Initializes the database connection.
	 *
	 * @param frame The JFrame used for displaying dialog messages
	 */

	public Database(JFrame frame) {
		this.frame = frame;


        try {

			log.info("Decrypting database credentials...");

            String password = c.decrypt(str[2]);
            String hostname = c.decrypt(str[3]);
            String username = c.decrypt(str[4]);
            String port = str[0];
            String database = str[1];
            String url = String.format("jdbc:mysql://%s:%s/%s", hostname, port, database);

			try {
				log.info("Loading database driver...");
				Class.forName(Database.driver);
				log.info("Database driver loaded successfully.");
			} catch (ClassNotFoundException e) {
				log.error("Database driver not found");
				throw new RuntimeException("Database driver not found", e);
			}

			log.info("Establishing database connection...");

			conn = DriverManager.getConnection(url, username, password);

			log.info("Database connection established successfully.");

        } catch (Exception e) {
			log.error("Error decrypting the host, password and username");
			throw new RuntimeException("Error initializing database connection", e);
        }

	}

	/**
	 * Registers a new user in the database.
	 *
	 * @param username The username of the user
	 * @param password The password of the user
	 */

	public void registerUser(String username, String password) {
		String sql;
		
		try {
			sql = c.decrypt(str[5]);
		} catch (Exception e) {
			log.error("Error decrypting the sql query");
			throw new RuntimeException("Error decrypting the sql query", e);
		}
		
		try (PreparedStatement statement = conn.prepareStatement(sql)) {
			statement.setString(1, username);
			statement.setString(2, password);
			try {
				statement.executeUpdate();
				JOptionPane.showMessageDialog(frame, "Utente registrato con successo", "Register",
						JOptionPane.INFORMATION_MESSAGE);
				setRegistrationTimestamp();
				log.info("User " + username + " registered successfully");
			} catch (SQLIntegrityConstraintViolationException e) {
				JOptionPane.showMessageDialog(frame, "Utente già registrato", "Register",
						JOptionPane.INFORMATION_MESSAGE);
				log.info("Already registered user: " + username);
			}
		} catch (SQLException e) {
			log.error("SQL error during user registration for user: " + username);
			JOptionPane.showMessageDialog(frame, "Errore durante la registrazione. Riprova più tardi.", "Errore",
					JOptionPane.ERROR_MESSAGE);
			throw new RuntimeException("SQL error during user registration for user " + username, e);
		}
	}

	/**
	 * Logs in a user with the specified username and password.
	 *
	 * @param username The username of the user
	 * @param password The password of the user
	 * @return True if login is successful, false otherwise
	 */

	public boolean loginUser(String username, String password) {
		String sql;
		
		try {
			sql = c.decrypt(str[6]);
		} catch (Exception e) {
			log.error("Error decrypting the sql query");
			throw new RuntimeException("Error decrypting the sql query", e);
		}
		
		try (PreparedStatement statement = conn.prepareStatement(sql)) {
			statement.setString(1, username);
			statement.setString(2, password);
			ResultSet resultSet = statement.executeQuery();
			nomeUtente = username;
			userPassword = password;
			boolean loggedIn = resultSet.next();
			if (loggedIn) {
				updateLastLogin();
				log.info("User " + username + " logged in successfully");
			} else {
				log.info("Failed login attempt for user " + username);
			}
			return loggedIn;
		} catch (SQLException e) {
			log.error("SQL error during login for user: " + username);
			throw new RuntimeException("SQL error during login for user: " + username, e);
		}
    }

	/**
	 * Updates user statistics for a won game.
	 */

	public void vittoria() {

		updateGameStatistics(str[7], str[8], "victory");
	}

	/**
	 * Updates user statistics for a drawn game.
	 */

	public void pareggio() {

		updateGameStatistics(str[9], str[10], "draw");
	}

	/**
	 * Updates user statistics for a lost game.
	 */

	public void sconfitta() {

		updateGameStatistics(str[11], str[12], "defeat");
	}


	/**
	 * Updates user statistics for a game based on the provided SQL queries.
	 *
	 * @param sqlSelect The SQL query for selecting user statistics
	 * @param sqlUpdate The SQL query for updating user statistics
	 * @param gameResult The result of the game ("victory", "draw", or "defeat")
	 */
	private void updateGameStatistics(String sqlSelect, String sqlUpdate, String gameResult) {
		try {
			log.info("Decrypting SQL queries for " + gameResult);
			sqlSelect = c.decrypt(sqlSelect);
			sqlUpdate = c.decrypt(sqlUpdate);

			try (PreparedStatement selectStatement = conn.prepareStatement(sqlSelect);
				 PreparedStatement updateStatement = conn.prepareStatement(sqlUpdate)) {

				// Set the username parameter for the select statement
				selectStatement.setString(1, nomeUtente);

				// Execute the select statement to retrieve user statistics
				ResultSet resultSet = selectStatement.executeQuery();

				// If the user statistics exist
				if (resultSet.next()) {
					// Update the user statistics with the username parameter
					updateStatement.setString(1, nomeUtente);
					updateStatement.executeUpdate();
					log.info("User's game statistics updated for " + nomeUtente + " after " + gameResult);
				} else {
					// If no user statistics found for the provided username
					log.warn("No user found with the specified credentials for " + nomeUtente);
				}
			}
		} catch (Exception e) {
			log.error("Error updating game statistics for user: " + nomeUtente);
			throw new RuntimeException("Error updating game statistics for user: " + nomeUtente , e);
		}
	}



	/**
	 * Retrieves statistics for top players from the database.
	 *
	 * @return A HashMap containing player usernames and their respective statistics
	 */

	public HashMap<String, PlayerStats> topPlayers() {
	    HashMap<String, PlayerStats> playerStats = new HashMap<>();
	    String sql;
	    try {
			log.info("Decrypting SQL query for retrieving top players");
	        sql = c.decrypt(str[13]);
	    } catch (Exception e) {
			log.error("Error decrypting the SQL query for top players");
			throw new RuntimeException("Error decrypting the SQL query for top players", e);
	    }

		try (PreparedStatement statement = conn.prepareStatement(sql);
			 ResultSet resultSet = statement.executeQuery()) {

			while (resultSet.next()) {
				String username = resultSet.getString("nomeUtente");
				if (!username.equals("admin")) {
					int gamesWon = resultSet.getInt("partiteVinte");
					int gamesLost = resultSet.getInt("partitePerse");
					playerStats.put(username, new PlayerStats(gamesWon, gamesLost));
					log.info("Retrieved stats for player: " + username);
				}
			}
        } catch (SQLException e) {
			log.error("SQL error while retrieving top players");
			throw new RuntimeException("SQL error while retrieving top players", e);
		}

	    return playerStats;
	}

	/**
	 * Retrieves the username of the currently logged-in user.
	 *
	 * @return The username of the logged-in user
	 */
	
	public String getUsername() {
		return nomeUtente;
	}

	/**
	 * Retrieves the password of the currently logged-in user.
	 *
	 * @return The password of the logged-in user
	 */

	public String getPassword(){
		return userPassword;
	}

	/**
	 * Updates the last login timestamp for the current user.
	 */
	public void updateLastLogin() {
		String sql;
		try {
			log.info("Preparing SQL query for updating last login timestamp");
			sql = "UPDATE Giocatori SET ultimo_accesso = CURRENT_TIMESTAMP WHERE nomeUtente = ?";

			try (PreparedStatement statement = conn.prepareStatement(sql)) {
				statement.setString(1, nomeUtente);
				int rowsAffected = statement.executeUpdate();

				if (rowsAffected > 0) {
					log.info("Last login timestamp updated for user: " + nomeUtente);
				} else {
					log.warn("No user found with username: " + nomeUtente);
				}
			}
		} catch (SQLException e) {
			log.error("SQL error while updating last login timestamp for user: " + nomeUtente);
			throw new RuntimeException("Error updating last login timestamp", e);
		}
	}

	/**
	 * Sets the registration timestamp for a new user
	 */
	public void setRegistrationTimestamp() {
		String sql = "UPDATE Giocatori SET data_registrazione = CURRENT_TIMESTAMP WHERE nomeUtente = ?";

		try (PreparedStatement statement = conn.prepareStatement(sql)) {
			statement.setString(1, nomeUtente);
			int rowsAffected = statement.executeUpdate();

			if (rowsAffected > 0) {
				log.info("Registration timestamp set for user: " + nomeUtente);
			} else {
				log.warn("No user found with username: " + nomeUtente);
			}
		} catch (SQLException e) {
			log.error("SQL error while setting registration timestamp for user: " + nomeUtente);
			throw new RuntimeException("Error setting registration timestamp", e);
		}
	}


}
