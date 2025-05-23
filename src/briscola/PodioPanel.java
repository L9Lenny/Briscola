package briscola;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 * The PodioPanel class represents a panel displaying top players' statistics in a leaderboard format.
 */

public class PodioPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private final Database db;
	private int y = 200;
	private final JPanel panel;
	private final BackgroundPanel bp;

	/**
	 * Constructs a PodioPanel with the specified database and parent panel.
	 *
	 * @param db    the database containing player statistics
	 * @param panel the parent panel to which this PodioPanel belongs
	 */

	public PodioPanel(Database db, JPanel panel) {
		
		this.db = db;
		this.panel = panel;
		
		setSize(1178, 861);
		setLayout(null);
		
		bp = new BackgroundPanel("res/Background/background4.png");
		add(bp, BorderLayout.CENTER);
		
		players();
		keyBindings();
		
	}

	/**
	 * Displays the top players and their statistics on the leaderboard.
	 */

	private void players() {
	    HashMap<String, PlayerStats> playerStats = db.topPlayers();
	    
	    List<Map.Entry<String, PlayerStats>> sortedPlayers = new ArrayList<>(playerStats.entrySet());
	    sortedPlayers.sort((entry1, entry2) -> {
	        int gamesWonComparison = Integer.compare(entry2.getValue().getGamesWon(), entry1.getValue().getGamesWon());
	        if (gamesWonComparison != 0) {
	            return gamesWonComparison;
	        } else {
	            return Integer.compare(entry1.getValue().getGamesLost(), entry2.getValue().getGamesLost());
	        }
	    });

	    int limit = Math.min(sortedPlayers.size(), 5);
	    List<Map.Entry<String, PlayerStats>> top5Players = sortedPlayers.subList(0, limit);

	    for (Map.Entry<String, PlayerStats> entry : top5Players) {
	        String nome = entry.getKey();
	        int gamesWon = entry.getValue().getGamesWon();
	  //      int gamesLost = entry.getValue().getGamesLost();

	        JLabel label = new JLabel(nome + " - " + gamesWon);
	        label.setFont(new Font("Tahoma", Font.PLAIN, 50));
	        label.setBounds(430, y, 500 ,55);
	        y += 100;
	        bp.add(label);
	    }
	}

	/**
	 * Registers the key bindings for keyboard shortcuts,
	 * allowing the user to exit the PodioPanel by pressing the Escape key.
	 */

	private void keyBindings() {
		InputMap inputMap = this.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = this.getActionMap();

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "esc");

		actionMap.put("esc", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				panel.setVisible(true);
			}
		});
	}
	
}
