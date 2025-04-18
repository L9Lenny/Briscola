package briscola;


import log.ColorLogger;
import org.jetbrains.annotations.NotNull;
import utils.ImageLoader;
import utils.SelectCardManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Mazzo class represents a deck of cards used in the game.
 * It manages the distribution, rotation, and comparison of cards during gameplay.
 */

public class Mazzo {

    private final ColorLogger log = new ColorLogger(Mazzo.class);

    private final MyArrayList<Carta> mazzo = new MyArrayList<>();
    private JLabel carta;
    private final JPanel panel;
    private int indice = 1; // indice carte da pescare
    private final ArrayList<JButton> backs = new ArrayList<>(); // carte avversario
    private String briscola;
    private final CerchioLabel carteRimanenti;
    private final Animation animation = new Animation();

    private final AtomicInteger punteggio = new AtomicInteger(0);
    private final AtomicInteger punteggio2 = new AtomicInteger(0);
    private final CerchioLabel punti1 = new CerchioLabel();
    private final CerchioLabel punti2 = new CerchioLabel();
    private final boolean[] flags = new boolean[3];
    private int r = new Random().nextInt(2);
    private final String cartaType;
    private final MazzoManager setup = new MazzoManager();
    private final JButton [] cardsButton = new JButton[3];
    private GameOverPanel g;
    private int turno = 0;
    private final Database db;
    private final Briscola b;
    private Timer timer;
    private final ImageLoader imgLoad = new ImageLoader();
    private SelectCardManager scm;

    /**
     * Constructs a new Mazzo object.
     * @param panel The JPanel where the deck will be displayed.
     * @param cartaType The type of cards used in the deck.
     * @param carteRimanenti The label indicating the number of remaining cards.
     * @param db The database instance used for game data storage.
     * @param b The Briscola instance associated with the game.
     */

    public Mazzo(JPanel panel, String cartaType, CerchioLabel carteRimanenti, Database db, Briscola b) {


        this.panel = panel;
        this.carteRimanenti = carteRimanenti;
        this.cartaType = cartaType;
        this.db = db;
        this.b = b;
        
        addCarte(cartaType);
        mescola();
        briscola(panel, cartaType);

        log.info("\n" + mazzo);

    }

    /**
     * Adds cards to the deck based on the specified type.
     * @param str The type of cards to add to the deck.
     */

    private void addCarte(String str) {

        setup.setup(str, mazzo);
        log.info("The cards have been added to the deck");
    }

    /**
     * Shuffles the deck.
     */

    public void mescola() {
        Collections.shuffle(this.mazzo);
        log.info("The cards have been shuffled");
    }

    /**
     * Sets the Briscola card from the deck.
     * @param panel The JPanel where the Briscola card will be displayed.
     * @param cartaType The type of cards used in the deck.
     * @return The Briscola card.
     */

    public Carta briscola(@NotNull JPanel panel, String cartaType) {
        Collections.swap(mazzo, 0, 39);
        carta = new JLabel(rotateCarta(this.mazzo.get(0), cartaType).getImg());
        carta.setBounds(105, 155, 200, 168);
        panel.add(carta);
        panel.setComponentZOrder(carta, 3);
        this.briscola = this.mazzo.get(0).getSeme();

        log.info("The briscola of this match has been decided : " + this.mazzo.get(0).toString());

        return this.mazzo.get(0);
    }

    /**
     * Rotates a card based on its type.
     * @param carta The card to rotate.
     * @param cartaType The type of cards used.
     * @return The rotated card.
     */

    public Carta rotateCarta(Carta carta, String cartaType) {

        return setup.rotateCarta(carta, cartaType);

    }

    /**
     * Rotates the briscola card.
     * @param carta The card to rotate.
     * @param cartaType The type of cards used.
     * @return The rotated card.
     */

    private Carta rotateBriscola(Carta carta, String cartaType) {

        return setup.rotateBriscola(carta, cartaType);

    }

    /**
     * Distributes cards to players.
     * @param g1 Player 1.
     * @param g2 Player 2.
     */

    public void distribuisci(Giocatore g1, Giocatore g2) {

        for (int i = 0; i < 3; i++) {
            g1.getMano().add(this.mazzo.get(this.indice++));
            g2.getMano().add(this.mazzo.get(this.indice++));
        }

        JButton card0 = new JButton(imgLoad.loadImage("res/Cards/back.png"));
        JButton card1 = new JButton(imgLoad.loadImage("res/Cards/back.png"));
        JButton card2 = new JButton(imgLoad.loadImage("res/Cards/back.png"));

        JButton back0 = new JButton(imgLoad.loadImage("res/Cards/back.png"));
        JButton back1 = new JButton(imgLoad.loadImage("res/Cards/back.png"));
        JButton back2 = new JButton(imgLoad.loadImage("res/Cards/back.png"));

        animation.distribuisciAnimation(card0, g1.getMano().get(0).getImg(), 418);
        animation.distribuisciAnimation(card1, g1.getMano().get(1).getImg(), 548);
        animation.distribuisciAnimation(card2, g1.getMano().get(2).getImg(), 678);

        animation.distribuisciAnimationBack(back0, 418);
        animation.distribuisciAnimationBack(back1, 548);
        animation.distribuisciAnimationBack(back2, 678);

        setUpButton(card0);
        setUpButton(card1);
        setUpButton(card2);

        setUpButton(back0);
        setUpButton(back1);
        setUpButton(back2);

        backs.add(back0);
        backs.add(back1);
        backs.add(back2);


        cardActionListner(card0, g1, g2, 0);
        cardActionListner(card1, g1, g2, 1);
        cardActionListner(card2, g1, g2, 2);
        
        cardsButton[0] = card0;
        cardsButton[1] = card1;
        cardsButton[2] = card2;
        
    }

    /**
     * Initiates the animation for drawing a card from the deck for the Player.
     * This method controls the timing of the animation and invokes methods to handle the drawn card.
     * @param card The button representing the drawn card.
     * @param g1 Player 1.
     * @param r The index of the opponent's card.
     * @param g2 Player 2.
     * @param n The index of the drawn card.
     */

    private void pescata(JButton card, Giocatore g1, int r, Giocatore g2, int n) {
        Timer timer = new Timer(1000, actionEvent -> {
            controllo(card, n, g1);
            controllo2(r, g2);
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Initiates the animation for drawing a card from the deck for the CPU.
     * This method controls the timing of the animation and invokes methods to handle the drawn card.
     * @param card The button representing the drawn card.
     * @param g1 Player 1.
     * @param r The index of the opponent's card.
     * @param g2 Player 2.
     * @param n The index of the drawn card.
     */

    private void pescataBack(JButton card, Giocatore g1, int r, Giocatore g2, int n) {
        Timer timer = new Timer(1000, actionEvent -> {
            controllo2(r, g2);
            controllo(card, n, g1);
        });
        timer.setRepeats(false);
        timer.start();
        
    }

    /**
     * Checks the position where the drawn card should be placed and initiates the corresponding animation.
     * @param card The button representing the drawn card.
     * @param n The index of the drawn card.
     * @param g1 Player 1.
     */

    private void controllo(JButton card, int n, Giocatore g1) {
        if (n == 0) {
            animation.pescataAnimation(card, distribuisci(g1, n), 418, g1);
        } else if (n == 1) {
            animation.pescataAnimation(card, distribuisci(g1, n), 548, g1);
        } else if (n == 2) {
            animation.pescataAnimation(card, distribuisci(g1, n), 678, g1);
        }
    }

    /**
     * Checks the position where the opponent's drawn card should be placed and initiates the corresponding animation for flipping the card back.
     * @param r The index of the opponent's drawn card.
     * @param g2 Player 2.
     */

    private void controllo2(int r, Giocatore g2) {
        if (r == 0) {
            animation.pescataAnimationBack(backs.get(r), distribuiscir(g2, r), 418);
        } else if (r == 1) {
            animation.pescataAnimationBack(backs.get(r), distribuiscir(g2, r), 548);
        } else if (r == 2) {
            animation.pescataAnimationBack(backs.get(r), distribuiscir(g2, r), 678);
        }
    }

    /**
     * Distributes the last drawn card to a player's hand.
     * This method selects the last card from the deck and assigns it to the player's hand at the specified index.
     * If the deck index is at its maximum (40), the method replaces the card with the rotated briscola card,
     * updates the index, updates the remaining cards label, and removes the deck's visual representation.
     *
     * @param g The player who receives the card.
     * @param n The index in the player's hand where the card should be placed.
     * @return The card distributed to the player.
     */

    public Carta distribuisci(Giocatore g, int n) {

        pescaUltimaCarta(g, n);

        return g.getMano().get(n);
    }

    /**
     * Distributes the last drawn card to a player's hand.
     *
     * This method selects the last card from the deck and assigns it to the player's hand at the specified index.
     * If the deck index is at its maximum (40), the method replaces the card with the rotated briscola card,
     * updates the index, updates the remaining cards label, and removes the deck's visual representation.
     *
     * @param g The player who receives the card.
     * @param r The index in the player's hand where the card should be placed.
     * @return The card distributed to the player.
     */

    public Carta distribuiscir(Giocatore g, int r) {
        pescaUltimaCarta(g, r);
        return g.getMano().get(r);
    }

    /**
     * Draws the last card from the deck and assigns it to a player's hand at the specified index.
     *
     * If the deck index is at its maximum (40), the method replaces the card with the rotated briscola card,
     * updates the index, updates the remaining cards label, and removes the deck's visual representation.
     *
     * @param g The player who receives the card.
     * @param r The index in the player's hand where the card should be placed.
     */

    private void pescaUltimaCarta(Giocatore g, int r) {
        if (indice == 40) {
            g.getMano().set(r, rotateBriscola(this.mazzo.get(0), cartaType));
            this.indice++;
            carteRimanenti.setNumero(40 - (indice - 1));
            removeMazzo();
        } else if (indice <= 39) {
            g.getMano().set(r, this.mazzo.get(this.indice++));
            carteRimanenti.setNumero(40 - (indice - 1));
        }
    }

    /**
     * Sets up the appearance of a button.
     *
     * This method configures the visual properties of a button such as removing border painting,
     * content area filling, and focus painting. It then adds the button to the panel and sets its
     * Z-order to 0.
     *
     * @param button The button to be set up.
     */

    private void setUpButton(@NotNull JButton button) {
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        panel.add(button);
        panel.setComponentZOrder(button, 0);

        scm = new SelectCardManager(briscola, cardsButton);

    }

    /**
     * Selects a random card index from a player's hand.
     * @param g2 The player whose hand will be used for selecting the card index.
     * @return A randomly selected index within the range of the player's hand size.
     */

    private int selectCard(@NotNull Giocatore g2, Giocatore g1) {
        return scm.selectCard(g2,g1);
    }

   /* private int selectCard(@NotNull Giocatore g2, Giocatore g1) {

        ArrayList<Integer> punti = new ArrayList<>();
        punti.add(0);
        punti.add(0);
        punti.add(0);

        if(g1.isLanciata()){
            for(int i = 0;i < cardsButton.length;i++){
                if(cardsButton[i].getLocation().x == 548 && cardsButton[i].getLocation().y == 300){
                    if (g1.getMano().get(i).getCarta().comparaCarte(g2.getMano().get(0).getCarta(), g1.getMano().get(i).getCarta(), briscola)){
                        punti.set(0, g2.getMano().get(0).getCarta().getValore() + g1.getMano().get(i).getCarta().getValore());
                    } else if (g1.getMano().get(i).getCarta().comparaCarte(g2.getMano().get(1).getCarta(), g1.getMano().get(i).getCarta(), briscola)) {
                        punti.set(0, g2.getMano().get(1).getCarta().getValore() + g1.getMano().get(i).getCarta().getValore());
                    } else if (g1.getMano().get(i).getCarta().comparaCarte(g2.getMano().get(2).getCarta(), g1.getMano().get(i).getCarta(), briscola)) {
                        punti.set(2, g2.getMano().get(2).getCarta().getValore() + g1.getMano().get(i).getCarta().getValore());
                    }else {
                        int min = Collections.min(punti);

                        return punti.indexOf(min);
                    }
                }
            }
        }else{
            return new Random().nextInt(g2.getMano().size());
        }

        int max = Collections.max(punti);

        return punti.indexOf(max);
    }
*/
    /**
     * Retrieves the current index of the deck.
     * @return The current index of the deck.
     */

    public int getIndice() {
        return indice;
    }

    /**
     * Sets up the action listener for a card button.
     * @param card The card button for which the action listener will be set up.
     * @param g1 The first player.
     * @param g2 The second player.
     * @param n The index of the card in the first player's hand.
     */

    private void cardActionListner(@NotNull JButton card, Giocatore g1, Giocatore g2, int n) {
        card.addActionListener(e -> {
        	if(!g1.isLanciata() || g2.getMano().size() < 3) {
        		g1.lanciaCallBack(card, g1, g1.getMano().get(n).getCarta(), () -> {
                    lancioBack(g2, g1);
                    azioniPartita(card, g1, g2, n);
                });


        	}else {
        		log.warn("Wait for the animation to end");
        	}
            
        });

    }

    /**
     * Executes the actions during the game.
     *
     * This method is called after each card action is performed by a player.
     * It checks the game state, updates scores, triggers animations, and handles the end of the game.
     *
     * @param card The card button that initiated the action.
     * @param g1 The first player.
     * @param g2 The second player.
     * @param n The index of the card in the first player's hand.
     */

	private void azioniPartita(JButton card, Giocatore g1, Giocatore g2, int n) {
		timer = new Timer(1500, actionEvent -> {

			turno++;

			if (!flags[0] && !flags[1] && !flags[2]) {
				if (g1.getMano().get(n).getCarta().comparaCarte(g1.getMano().get(n).getCarta(),
						g2.getMano().get(r).getCarta(), briscola)) {
					azioniGiocatore1(card, g1, g2, n);
				} else {
					azioniBack(card, g1, g2, n);
				}

			} else {
				g1.setLanciata(true);
				if (g2.getMano().get(r).getCarta().comparaCarte(g2.getMano().get(r).getCarta(),
						g1.getMano().get(n).getCarta(), briscola)) {
					azioniBack(card, g1, g2, n);
				} else {
					azioniGiocatore1(card, g1, g2, n);
				}
				flags[0] = false;
				flags[1] = false;
				flags[2] = false;
			}

			if (turno == 20) {
				if (punteggio.get() + punteggio2.get() == 120) {
					g = new GameOverPanel(b);
					if (punteggio.get() == punteggio2.get()) {
						g.tie();
						db.pareggio();
					} else if (punteggio.get() < punteggio2.get()) {
						g.lose();
						db.sconfitta();
					} else if (punteggio.get() > punteggio2.get()) {
						g.wins();
						db.vittoria();
					}
					panel.add(g, BorderLayout.CENTER);
					panel.setComponentZOrder(g, 0);
					panel.repaint();
					panel.revalidate();
					timer.stop();
				}
			}

		});

		timer.setRepeats(false);
		timer.start();

	}

    /**
     * Executes actions when the first player wins.
     * @param card The card button involved in the action.
     * @param g1 The first player.
     * @param g2 The second player.
     * @param n The index of the card in the first player's hand.
     */

    private void azioniGiocatore1(JButton card, Giocatore g1, Giocatore g2, int n) {
    	calcoloPunteggio(g1, g2, n, punteggio2, punteggio, punti1, punti2, true);
        animation.presaAnimation(card, backs.get(r), imgLoad.loadImage("res/Cards/Rotate/back.png"), panel);
        if(indice <= 40){
            pescata(card, g1, r, g2, n);
        }
        if(indice > 40){
            removeCard(g2);
        }
    }

    /**
     * Executes actions when the second player wins.
     * @param card The card button involved in the action.
     * @param g1 The first player.
     * @param g2 The second player.
     * @param n The index of the card in the first player's hand.
     */

	private void azioniBack(JButton card, Giocatore g1, Giocatore g2, int n) {
		calcoloPunteggio(g1, g2, n, punteggio2, punteggio, punti1, punti2, false);
		animation.presaAnimationBack(card, backs.get(r), imgLoad.loadImage("res/Cards/Rotate/back.png"), panel);
		if (indice <= 40) {
			pescataBack(card, g1, r, g2, n);
		}
		if (indice > 40) {
			removeCard(g2);
		}
		if(!g2.getMano().isEmpty()) {
			r = selectCard(g2, g1);
			Timer t = new Timer(1500, actionEvent1 -> g2.lancia(backs.get(r), g2, g2.getMano().get(r).getCarta()));
			t.setRepeats(false);
			t.start();
		}
		
	}

    /**
     * Calculates the score based on the outcome of a round.
     *
     * This method calculates the score based on the cards played in a round.
     * It updates the scores of both players and updates the display accordingly.
     *
     * @param g1 The first player.
     * @param g2 The second player.
     * @param n The index of the card in the first player's hand.
     * @param punteggio2 The score of the second player.
     * @param punteggio The score of the first player.
     * @param punti1 The label displaying the score of the first player.
     * @param punti2 The label displaying the score of the second player.
     * @param b A boolean indicating whether the first player won the round (true) or not (false).
     */

    private void calcoloPunteggio(Giocatore g1, Giocatore g2, int n, AtomicInteger punteggio2, AtomicInteger punteggio, CerchioLabel punti1, CerchioLabel punti2, boolean b) {
        if (b) {
            punteggio.addAndGet(g1.getMano().get(n).getCarta().getValore());
            punteggio.addAndGet(g2.getMano().get(r).getCarta().getValore());
            g1.setPunteggio(punteggio.get());
            punti1.setSize(60, 60);
            punti1.calcolaCentro(punti1, animation.getLabel());
            punti1.setNumero(g1.getPunteggio());
            panel.add(punti1);
            panel.setComponentZOrder(punti1, 0);
            panel.repaint();
            punti1.repaint();
            log.info("Score updated successfully [" + g1.getNickName() + ": " + punteggio.get() + "]");
        } else {
            punteggio2.addAndGet(g2.getMano().get(r).getCarta().getValore());
            punteggio2.addAndGet(g1.getMano().get(n).getCarta().getValore());
            g2.setPunteggio(punteggio2.get());
            punti2.setSize(60, 60);
            punti2.calcolaCentro(punti2, animation.getLabel2());
            punti2.setNumero(g2.getPunteggio());
            panel.add(punti2);
            panel.setComponentZOrder(punti2, 0);
            panel.repaint();
            punti2.repaint();
            log.info("Score updated successfully [" + g2.getNickName() + ": " + punteggio2.get() + "]");
        }
        
    }

    /**
     * Initiates the action of the second player.
     *
     * This method handles the action of the second player,
     * which involves selecting a card to play and updating the game accordingly.
     *
     * @param g2 The second player.
     */

    private void lancioBack(Giocatore g2, Giocatore g1) {
        int i = 0;
        int x = 0;

        // controllo se è stata gia lanciata una carta
        for (JButton card : backs) {
            if (card.getLocation().x == 598 && card.getLocation().y == 200) {
                flags[i] = true;
            }
            i++;
        }

        // se è stata gia lanciata una carta la x viene impostata su 1 cosi da non lanciarne altre
        for (boolean flag : flags) {
            if (flag) {
                x = 1;
                break;
            }
        }

        if (x == 0) {
            r = selectCard(g2, g1);
            g2.lancia(backs.get(r), g2, g2.getMano().get(r).getCarta());
        }

    }

    /**
     * Removes the deck from the panel after dealing all the cards.
     *
     * This method removes the graphical representation of the deck from the panel
     * after the cards have been dealt to the players.
     *
     */

    private void removeMazzo(){
        Component component = panel.getComponentAt(80, 155);
        Component component2 = panel.getComponentAt(119, 209);
        removeComponent(component);
        removeComponent(component2);
        removeComponent(carta);
    }

    /**
     * Removes a component from the panel.
     *
     * This method removes a specified component from the panel.
     *
     * @param component The component to be removed.
     */

    private void removeComponent(Component component){
        if (component instanceof JLabel) {
            panel.remove(component);
            panel.revalidate();
            panel.repaint();
        }
    }

    /**
     * Removes a card from the second player's hand.
     * @param g2 The second player.
     */

    private void removeCard(@NotNull Giocatore g2){
        g2.getMano().remove(r);
        backs.remove(r);
    }

    /**
     * Retrieves an array of card buttons.
     * @return An array containing all card buttons.
     */

    public JButton[] getCardButtons() {
    	return cardsButton;
    }
    
}