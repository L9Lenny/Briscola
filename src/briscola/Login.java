package briscola;

import utils.FileManager;
import utils.ImageLoader;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * The Login class represents the user login interface.
 * It allows users to login with a username and password.
 */

public class Login {

	private JFrame frame;
	private final FileManager fm = new FileManager("res/Login/remember.txt");
	private JTextField txtCcc;
	private JPasswordField textField;
	private boolean logged = checkLogin();
	Register register;
	private boolean hide = true;
	private JCheckBox chckbxNewCheckBox;
	private final Database db;
	private final ImageLoader imgLoad = new ImageLoader();

	/**
	 * Constructs a new Login object.
	 * @param briscola The Briscola instance associated with the login process.
	 * @param db The database instance used for user login.
	 */

	public Login(Briscola briscola, Database db) {
		this.db = db;
		register = new Register(this, db);
		initialize(briscola);
	}

	/**
	 * Initializes the user login interface.
	 * @param briscola The Briscola instance associated with the login process.
	 */

	private void initialize(Briscola briscola) {
		frame = new JFrame();
		frame.setBounds(100, 100, 416, 446);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JButton eye = new JButton(imgLoad.loadImage("res/Login/hide2.png"));
		eye.setBorderPainted(false);
		eye.setContentAreaFilled(false);
		eye.setFocusPainted(false);
		eye.addActionListener(e -> {
            if (hide) {
                eye.setIcon(imgLoad.loadImage("res/Login/view2.png"));
                eye.repaint();
                eye.validate();
                textField.setEchoChar((char) 0);
                hide = false;
            } else {
                eye.setIcon(imgLoad.loadImage("res/Login/hide2.png"));
                eye.repaint();
                eye.validate();
                textField.setEchoChar('•');
                hide = true;
            }

        });
		eye.setBounds(280, 208, 20, 20);
		frame.getContentPane().add(eye);
		frame.getContentPane().setComponentZOrder(eye, 0);

		JLabel lblNewLabel = new JLabel("Login");
		lblNewLabel.setBounds(148, 11, 105, 69);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 35));
		frame.getContentPane().add(lblNewLabel);

		txtCcc = new JTextField();
		txtCcc.setBounds(78, 126, 229, 26);
		txtCcc.setBackground(new Color(255, 255, 255));
		txtCcc.setFont(new Font("Tahoma", Font.PLAIN, 20));
		txtCcc.setColumns(10);
		txtCcc.setBorder(new LineBorder(Color.CYAN));
		frame.getContentPane().add(txtCcc);

		JLabel lblNewLabel_1 = new JLabel("UserName");
		lblNewLabel_1.setBounds(78, 107, 68, 14);
		frame.getContentPane().add(lblNewLabel_1);
		textField = new JPasswordField();
		textField.setBounds(78, 204, 229, 26);
		textField.setFont(new Font("Tahoma", Font.PLAIN, 20));
		textField.setColumns(10);
		textField.setBorder(new LineBorder(Color.CYAN));
		textField.setBackground(Color.WHITE);

		textField.addActionListener(e -> {
            frame.repaint();
            frame.revalidate();
        });

		frame.getContentPane().add(textField);

		JLabel lblNewLabel_1_1 = new JLabel("Password");
		lblNewLabel_1_1.setBounds(78, 185, 68, 14);
		frame.getContentPane().add(lblNewLabel_1_1);

		JButton btnNewButton = new JButton("Login");
		btnNewButton.setBounds(78, 269, 229, 23);
		btnNewButton.addActionListener(e -> {
			if (!logged) {
				String username = txtCcc.getText();
				char[] chars = textField.getPassword();
				String password = new String(chars);

                if(db.loginUser(username, password)){
                    logged = true;
                    Briscola.getFrame().setVisible(true);
                    Briscola.getFrame().repaint();
                    Briscola.getFrame().revalidate();
                    frame.setVisible(false);
                }

                if (!logged) {
					JOptionPane.showMessageDialog(frame, "Utente non registrato", "Login",
							JOptionPane.INFORMATION_MESSAGE);
				}


			} else {
				JOptionPane.showMessageDialog(frame, "Utente già loggato", "Login",
						JOptionPane.INFORMATION_MESSAGE);
				Briscola.getFrame().setVisible(true);
				Briscola.getFrame().repaint();
				Briscola.getFrame().revalidate();
				frame.setVisible(false);
			}
			if (chckbxNewCheckBox.isSelected()){
				writeLogin(txtCcc.getText(), textField.getPassword());
			}
		});
		btnNewButton.setBackground(Color.CYAN);
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnNewButton.setFocusPainted(false);
		frame.getContentPane().add(btnNewButton);

		JButton btnRegister = new JButton("Register now");
		btnRegister.setBounds(67, 332, 229, 23);
		btnRegister.addActionListener(e -> {
			register.getFrame().setVisible(true);
			frame.setVisible(false);
		});
		btnRegister.setForeground(Color.BLUE);
		btnRegister.setContentAreaFilled(false);
		btnRegister.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnRegister.setBackground(new Color(240, 240, 240));
		btnRegister.setBorderPainted(false);
		btnRegister.setContentAreaFilled(true);
		btnRegister.setFocusPainted(false);
		frame.getContentPane().add(btnRegister);

		chckbxNewCheckBox = new JCheckBox("Ricorda");
		chckbxNewCheckBox.setBounds(78, 237, 97, 23);
		frame.getContentPane().add(chckbxNewCheckBox);
	}

	/**
	 * Retrieves the frame associated with the login interface.
	 * @return The JFrame instance representing the login interface.
	 */

	public JFrame getFrame() {
		return frame;
	}

	/**
	 * Checks if the user is currently logged in.
	 * @return true if the user is logged in, false otherwise.
	 */

	public boolean isLogged() {
		return logged;
	}

	/**
	 * Retrieves the frame associated with the Briscola game.
	 * @return The JFrame instance representing the Briscola game interface.
	 */

	public JFrame getBriscolaFrame() {
		return Briscola.getFrame();
	}


	/**
	 * Checks if the user was previously logged in.
	 * @return true if the user was previously logged in, false otherwise.
	 */

	private boolean checkLogin() {

		if (fm.readCredentials() != null){
			return true;
		}else {
			return false;
		}
	}

	private void writeLogin(String username, char[] password) {

		fm.writeCredentials(username, new String(password));

	}

	/**
	 * Sets the login status.
	 * @param logged The boolean value representing the login status.
	 */

	public void setLogged(boolean logged) {
		this.logged = logged;
	}

	/**
	 * Retrieves the database instance associated with user login.
	 * @return The Database instance.
	 */

	public Database getDb() {
		return db;
	}

	/**
	 * Retrieves the file manager instance used for managing login status.
	 * @return The FileManager instance.
	 */

	public FileManager getFm(){
		return  fm;
	}

}
