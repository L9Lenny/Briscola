package briscola;

import log.ColorLogger;
import org.jetbrains.annotations.NotNull;
import utils.ImageLoader;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * The main class responsible for managing the Briscola game application.
 */

public class Briscola {

	private final ColorLogger log = new ColorLogger(Briscola.class);


	private static JFrame frame;
	private JPanel panel;
	private Login login;
    private String carte;
	private CerchioLabel carteRimanenti;
	private byte temp = 0;
	private MenuPanel menu;
    private JButton[] cardButtons = new JButton[3];
	private final Briscola b = this;
	private BackgroundPanel bp;
	private Database db;
	private int n = 0;
	private final ImageLoader imgLoad = new ImageLoader();

	/**
	 * Constructs a new instance of the Briscola class and initializes the game.
	 */

	public Briscola() {
		initialize();
		log.info("Application started");
	}

	/**
	 * Initializes the JFrame and JPanel components for the game.
	 */

	private void initialize() {
		frame = new JFrame();
		frame.setSize(1178, 861);
		centerFrame(frame);
		frame.setVisible(false);
		frame.setResizable(false);
		frame.setIconImage(imgLoad.loadImage("res/logo/logo.png").getImage());
		frame.setTitle("JBriscola");
		panel = new JPanel();
		panel.setSize(1178, 861);
		panel.setLayout(null);
	}

	/**
	 * Initializes the game components with the provided Database instance.
	 *
	 * @param db The Database instance to be used for the game.
	 */

	public void inizialize2(Database db) {
		if (frame.isVisible()) {
			
			this.db = db;
			login = new Login(this, db);
			String[] credential = login.getFm().readCredentials();
			if (credential != null) {
				db.loginUser(credential[0], credential[1]);
			}
            Register register = new Register(login, login.getDb());

			if(n == 10){
                db.loginUser(db.getUsername(), db.getPassword());
                login.setLogged(true);
            }

			//panel.setBounds(0, 0, 1040, 667);
			frame.getContentPane().add(panel, BorderLayout.CENTER);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			StartMenu m = new StartMenu(frame, panel, login, register);
			m.game().addActionListener(e -> {
				if (login.isLogged()) {
					carte = m.getCarteType();
					log.info("User is logged in. CarteType: " + carte);
					game();
				} else {
					JOptionPane.showMessageDialog(frame, "Utente non loggato", "Login",
							JOptionPane.INFORMATION_MESSAGE);
					log.warn("Attempt to start game without login");
				}
			});
		} else {
			log.warn("Frame is not visible");
		}
	}

	/**
	 * Sets the background of the game panel.
	 */


	private void setBackground() {

		panel.removeAll();
		
		bp = new BackgroundPanel("res/Background/background9.jpg");
		
		panel.add(bp, BorderLayout.CENTER);

		JLabel mazzo = new JLabel(imgLoad.loadImage("res/Cards/back.png"));
		mazzo.setBounds(80, 155, 139, 168);
		

		carteRimanenti = new CerchioLabel();
		carteRimanenti.setSize(60, 60);
		carteRimanenti.calcolaCentro(carteRimanenti, mazzo);

		bp.add(carteRimanenti);
		bp.add(mazzo);

		bp.setComponentZOrder(mazzo, 1);
		bp.setComponentZOrder(carteRimanenti, 0);


		panel.repaint();
		bp.repaint();
		panel.revalidate();
		bp.revalidate();

	}

	/**
	 * Starts the game by initializing game components and setting up key bindings.
	 */

	public void game() {
		log.info("Game started");
		temp = 0;
		setBackground();
        Mazzo mazzo1 = new Mazzo(bp, carte, carteRimanenti, login.getDb(), b);
		Giocatore giocatore = new Giocatore(login.getDb().getUsername());
		log.info(giocatore.getNickName());
		Giocatore giocatore2 = new CPU();
		mazzo1.distribuisci(giocatore, giocatore2);
		log.info(giocatore.getMano().toString());
		log.info(giocatore2.getMano().toString());
		carteRimanenti.setNumero(40 - (mazzo1.getIndice() - 1));
		cardButtons = mazzo1.getCardButtons();
		InputMap inputMap = panel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = panel.getActionMap();

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "esc");

		actionMap.put("esc", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (temp == 0) {
					menu = new MenuPanel(cardButtons, value -> temp = value, b);
					panel.add(menu, BorderLayout.CENTER);
					panel.setComponentZOrder(menu, 0);
					for (JButton card : cardButtons) {
						card.setEnabled(false);
					}
					panel.repaint();
					temp = 1;
				} else {
					for (JButton card : cardButtons) {
						card.setEnabled(true);
					}
					menu.setVisible(false);
					temp = 0;
				}
			}
		});
	}


	/**
	 * Centers the provided JFrame on the screen.
	 *
	 * @param f The JFrame to be centered.
	 */

	private void centerFrame(@NotNull JFrame f) {
		f.setLocationRelativeTo(null);
	}

	/**
	 * Retrieves the Database instance used in the game.
	 *
	 * @return The Database instance.
	 */

	public Database getDb() {
		return db;
	}

	/**
	 * Sets the value of 'n'.
	 *
	 * @param n The value to be set for 'n'.
	 */

	public void setN(int n){
		this.n = n;
	}

	/**
	 * Retrieves the main JFrame instance of the game.
	 *
	 * @return The main JFrame instance.
	 */

	public static JFrame getFrame() {
		return frame;
	}

	/**
	 * Retrieves the game panel.
	 *
	 * @return The game panel.
	 */

	public JPanel getPanel() {
		return panel;
	}

}
