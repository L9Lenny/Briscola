package utils;

import briscola.Briscola;
import log.ColorLogger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class manages file reading and writing operations.
 */

public class FileManager {

	private final ColorLogger log = new ColorLogger(Briscola.class);

	private String path; // File path
	private FileWriter fw = null; // FileWriter object for file writing
	private BufferedReader br = null; // BufferedReader object for file reading

	/**
	 * Constructor for the FileManager class.
	 *
	 * @param path The path of the file to perform read and write operations on
	 */

	public FileManager(String path) {

		if(path != null) {
			this.path = path;
		}

	}

	/**
	 * Appends a boolean value to the file.
	 *
	 * @param value The boolean value to append to the file
	 */


	public void append(boolean value) {
		try {
			fw = new FileWriter(path, false);
			fw.append(String.valueOf(value));
		}catch(IOException e) {
			log.error("Error appending to file " + path);
			throw new RuntimeException("Error appending to file " + path, e);
		}finally {
			try {
				if(fw != null){
					fw.close();
				}
			} catch (IOException e) {
				log.error("Error closing FileWriter");
			}
		}
	}

	/**
	 * Appends a string to the file.
	 *
	 * @param value The string to append to the file
	 */

	public void append(String value) {
		try {
			fw = new FileWriter(path, false);
			fw.append(value);
		}catch(IOException e) {
			log.error("Error appending to file " + path);
			throw new RuntimeException("Error appending to file " + path, e);
		}finally {
			try {
				if (fw != null){
					fw.close();
				}
			} catch (IOException e) {
				log.error("Error closing FileWriter");
			}
		}
	}

	/**
	 * Reads a boolean value from the file.
	 *
	 * @return The boolean value read from the file
	 */

	public boolean readBoolean() {
		try {
			br = new BufferedReader(new FileReader(path));

			String str;

			if((str = br.readLine()) != null) {
				return Boolean.parseBoolean(str);
			}

		}catch(IOException e) {
			log.error("Error reading from file " + path);
			throw new RuntimeException("Error reading from file " + path, e);
		}finally {
			try {
				if (br != null){
					br.close();
				}
			} catch (IOException e) {
				log.error("Error closing BufferedReader");
			}
		}

		return false;
	}

	/**
	 * Reads a string from the file.
	 *
	 * @return The string read from the file
	 */

	public String readString() {

		try {
			br = new BufferedReader(new FileReader(path));

			String str;

			if((str = br.readLine()) != null) {
				return str;
			}

		}catch(IOException e) {
			log.error("Error reading from file " + path);
			throw new RuntimeException("Error reading from file " + path, e);
		}finally {
			try {
				if(br != null){
					br.close();
				}
			} catch (IOException e) {
				log.error("Error closing BufferedReader");
			}
		}


		return "";
	}

	/**
	 * Sets the file path.
	 *
	 * @param path The new file path
	 */

	public void setPath(String path) {
		if(path != null) {
			this.path = path;
		}
	}

	/**
	 * Gets the file path.
	 *
	 * @return The file path
	 */
	public String getPath() {
		return path;
	}


	/**
	 * Writes username and password to the file on separate lines.
	 * Format:
	 * username
	 * password
	 *
	 * @param username The username to store
	 * @param password The password to store
	 */
	public void writeCredentials(String username, String password) {
		if (username == null || password == null) {
			log.error("Username or password cannot be null");
			throw new IllegalArgumentException("Username and password cannot be null");
		}

		if (username.contains("\n") || password.contains("\n")) {
			log.error("Username or password contains newline characters");
			throw new IllegalArgumentException("Username and password cannot contain newline characters");
		}

		try {
			fw = new FileWriter(path, false);
			fw.write(username + "\n" + password);
			log.info("Credentials saved successfully for user: " + username);
		} catch (IOException e) {
			log.error("Error writing credentials to file " + path);
			throw new RuntimeException("Error writing credentials to file", e);
		} finally {
			try {
				if (fw != null) {
					fw.close();
				}
			} catch (IOException e) {
				log.error("Error closing FileWriter");
			}
		}
	}

	/**
	 * Reads username and password from separate lines in the file.
	 *
	 * @return A String array where [0] is username and [1] is password.
	 *         Returns null if the file doesn't contain both lines.
	 */
	public String[] readCredentials() {
		try {
			br = new BufferedReader(new FileReader(path));
			String username = br.readLine();
			String password = br.readLine();

			if (username != null && password != null) {
				log.info("Credentials loaded successfully");
				return new String[]{username, password};
			}
			log.warn("No valid credentials found in file");
			return null;
		} catch (IOException e) {
			log.error("Error reading credentials from file " + path);
			throw new RuntimeException("Error reading credentials from file", e);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				log.error("Error closing BufferedReader");
			}
		}
	}

}
