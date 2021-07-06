/**
 * 
 */
package com.Mateusz.Lista4.Client;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.Mateusz.Lista4.Client.Design.Board;

/**
 * KLIENT
 */

public class ChineseCheckersClient extends JFrame {
	// okno i plansza gry
	private Board board;
	private JFrame frame;
	// okno dialogowe z wyborem opcji
	private JDialog dialog;
	// napisy powiadomien i bledow
	private JLabel text, warning, name1, name2, fail;
	// pole tekstowe
	private JTextField textField;
	// przycisk
	private JButton button1, button2, button3;
	// rozwijany wybor
	private JComboBox comboBox;
	private Socket socket;
	public Scanner input;
	public PrintWriter output;
	// indeks dotknietego pola
	int indexTouch;
	// indeks pola na ktore ruszamy
	int indexMove;
	// kolor pola
	Color myColor;
	// 0 jesli dotykamy, 1 jesli ruszamy
	int counter;
	// 0 jesli nie bylo skoku, 1 jesli byl
	int jumpFlag;
	// tryb: ogladanie, granie
	String mode;

	/**
	 * konstruktor ustawia socket, input i output
	 */
	public ChineseCheckersClient() throws Exception {
		socket = new Socket("localhost", 10000);
		input = new Scanner(socket.getInputStream());
		output = new PrintWriter(socket.getOutputStream(), true);
		chooseMode();
	}

	/**
	 * wybor trybu
	 */
	private void chooseMode() {
		// licznik do tablicy
		int i = 0;
		// uzupelniamy store nazwami 10(lub mniej jesli tyle nie bylo) ostatnich gier
		String[] store = new String[10];
		String receivedMessage = input.nextLine();
		while (!(receivedMessage.equals("END TABLES"))) {
			store[i] = receivedMessage;
			i++;
			receivedMessage = input.nextLine();
		}
		// tworzymy okno dialogowe
		dialog = new JDialog(this, "mode", true);
		dialog.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		dialog.setSize(500, 500);
		dialog.setLayout(null);
		dialog.setBackground(Color.LIGHT_GRAY);
		dialog.setResizable(false);
		// tworzymy comboboxa z danymi ze store
		comboBox = new JComboBox(store);
		comboBox.setBounds(100, 220, 300, 50);
		// tworzymy pole tekstowe na wpisanie nazwy gry
		textField = new JTextField();
		textField.setBounds(100, 300, 300, 50);
		// tworzymy napisy
		name1 = new JLabel("Or insert name of the game");
		name1.setBounds(100, 260, 300, 50);
		name2 = new JLabel("Choose one of last 10 games");
		name2.setBounds(100, 180, 300, 50);
		fail = new JLabel();
		fail.setBounds(100, 120, 300, 50);
		// tworzymy przyciski
		button2 = new JButton("Play");
		button2.setBounds(100, 0, 300, 100);
		button2.setBackground(Color.GREEN);
		button2.addActionListener(new ButtonListener());
		button3 = new JButton("Watch");
		button3.setBounds(100, 363, 300, 100);
		button3.setBackground(Color.CYAN);
		button3.addActionListener(new ButtonListener());
		// dodajemy wszystko do okna dialogowego i ustawiamy je jako widoczne
		dialog.add(comboBox);
		dialog.add(button2);
		dialog.add(button3);
		dialog.add(textField);
		dialog.add(name1);
		dialog.add(name2);
		dialog.add(fail);
		dialog.setVisible(true);
	}

	/**
	 * init-method z beans
	 */
	private void init() {
		// jesli gramy
		if (mode.equals("PLAY")) {
			try {
				play();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// jesi ogladamy
		else {
			watch();
		}
	}

	/**
	 * setery
	 */

	public void setBoard(Board board) {
		this.board = board;
	}

	public void setIndexMove(int indexMove) {
		this.indexMove = indexMove;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public void setJumpFlag(int jumpFlag) {
		this.jumpFlag = jumpFlag;
	}

	public void setIndexTouch(int indexTouch) {
		this.indexTouch = indexTouch;
	}

	/**
	 * przygotowanie planszy przy graniu w zaleznosci od liczby graczy
	 */
	private void setupPlay(int players, String colorPlayer) {
		// tworzymy okno, plansze i napisy
		frame = new JFrame("(PLAY) Chinese Checkers: " + colorPlayer);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setSize(1000, 1000);
		frame.setLayout(new BorderLayout());
		frame.setResizable(false);
		board.setPlayers(players);
		board.setup();
		// dodajemy sluchacza myszki
		board.addMouseMotionListener(new Mouse());
		board.addMouseListener(new Mouse());
		myColor = board.stringToColor(colorPlayer);
		// tworzymy napisy
		text = new JLabel();
		text.setFont(new Font("Verdana", Font.PLAIN, 25));
		warning = new JLabel();
		warning.setFont(new Font("Verdana", Font.PLAIN, 25));
		warning.setForeground(Color.RED);
		// tworzymy przycisk
		button1 = new JButton("End move");
		button1.addActionListener(new ButtonListener());
		// dodajemy wszystko do panelu i okna i ustawiamy je jako widoczne
		board.add(text, BorderLayout.NORTH, SwingConstants.CENTER);
		board.add(warning, BorderLayout.SOUTH, SwingConstants.CENTER);
		frame.add(board, BorderLayout.CENTER);
		frame.add(button1, BorderLayout.SOUTH);
		frame.setVisible(true);
	}

	/**
	 * przygotowanie planszy przy ogladaniu w zaleznosci od liczby graczy
	 */
	private void setupWatch(int players) {
		// tworzymy okno i plansze
		frame = new JFrame("(WATCH) Chinese Checkers");
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setSize(1000, 1000);
		frame.setLayout(new BorderLayout());
		frame.setResizable(false);
		text = new JLabel("WATCHING...");
		text.setFont(new Font("Verdana", Font.PLAIN, 25));
		text.setForeground(Color.MAGENTA);
		board.setPlayers(players);
		board.setup();
		board.add(text, BorderLayout.NORTH, SwingConstants.CENTER);
		frame.add(board, BorderLayout.CENTER);
		frame.setVisible(true);
	}

	/**
	 * gra
	 */
	public void play() throws Exception {
		// wiadomosc otrzymana z serwera
		String receivedMessage = "";
		// kolor ktorym ruszyl sie przeciwnik
		Color enemyColor;
		try {
			// pobieramy informacje o ilosci graczy i kolorze pionkow
			receivedMessage = input.nextLine();
			int players = 0;
			try {
				players = Integer.parseInt(receivedMessage);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			String colorPlayer = input.nextLine();
			String ending = "";
			// ustawienia okna
			setupPlay(players, colorPlayer);
			// petla w ktorej pobieramy wiadomosci wyslane przez serwer
			// gra konczy sie gdy otrzymamy wiadomosc END
			while (!(receivedMessage.contains("Win"))) {
				// jesli serwer cos wyslal
				if (input.hasNextLine()) {
					receivedMessage = input.nextLine();
					// wykonalismy prawdlowe dotkniecie
					if (receivedMessage.contains("You touched")) {
						// nie bylo skoku
						jumpFlag = 0;
						// odpowiednio ustawiamy plansze i wiadomosci
						board.setTouch(indexTouch);
						text.setText(receivedMessage);
						warning.setText("");
						// nastepne klikniecie to ruch
						counter = 1;
					}
					// wykonalismy prawidlowy ruch
					else if (receivedMessage.contains("You moved")) {
						jumpFlag = 0;
						board.setMove(indexTouch, indexMove, myColor);
						text.setText(receivedMessage);
						warning.setText("");
						// nastepne klikniecie to dotkniecie
						counter = 0;
					}
					// wykonalismy skok
					else if (receivedMessage.contains("You jumped")) {
						// byl skok
						jumpFlag = 1;
						board.setMove(indexTouch, indexMove, myColor);
						// mozemy sie ruszyc tylko pionkiem ktorym ruszylismy sie poprzednio
						indexTouch = indexMove;
						board.setTouch(indexTouch);
						text.setText(receivedMessage);
						warning.setText("");
						// nastepne klikniecie to ruch
						counter = 1;
					}
					// jesli konczymy ruch
					else if (receivedMessage.contains("You ended move")) {
						jumpFlag = 0;
						// anulowanie dotkniecia
						indexTouch = -1;
						board.setMove(indexTouch, indexTouch, myColor);
						text.setText(receivedMessage);
						// nastepne klikniecie to dotkiecie
						counter = 0;
					}
					// jezeli przeciwnik wyszedl
					else if (receivedMessage.contains("Opponent left")) {
						indexTouch = -1;
						indexMove = -1;
						text.setText(receivedMessage);
						ending = receivedMessage;
						String[] fragments = receivedMessage.split(" ");
						Color deleteColor = board.stringToColor(fragments[4]);
						board.deleteColor(deleteColor);
					}
					// jesli wygralismy
					else if (receivedMessage.equals("You win!")) {
						board.setMove(indexTouch, indexMove, myColor);
						text.setForeground(Color.MAGENTA);
						text.setText(receivedMessage);
						warning.setText(ending);
						break;
					}
					// jeslli jest remis
					else if (receivedMessage.equals("Draw!")) {
						text.setForeground(Color.MAGENTA);
						text.setText(receivedMessage);
						warning.setText("");
						break;
					}
					// jesli kliknelismy pole a nie jest nasza kolejka
					// jesli kliknelismy nie swoje pole
					else if (receivedMessage.contains("It is not")) {
						indexTouch = -1;
						warning.setText(receivedMessage);
					}
					// jesli chcemy sie ruszyc na zajete pole
					// jesli pole nie jest w zasiegu
					// jesli po skoku chcemy wykonac normalny ruch
					// jesli chcemy wyjsc ze zwycieskiego trojkata
					else if (receivedMessage.contains("Field") || receivedMessage.contains("After jump")
							|| receivedMessage.contains("You cannot")) {
						warning.setText(receivedMessage);
						counter = 1;
					}
					// jesli ktorys z przeciwnikow wykonal ruch
					else if (receivedMessage.contains("MOVED")) {
						// dzielimy otrzymana wiadomosc
						String[] fragments = receivedMessage.split(" ");
						// wybieramy potrzebne fragmenty
						enemyColor = board.stringToColor(fragments[0]);
						indexTouch = Integer.parseInt(fragments[3]);
						indexMove = Integer.parseInt(fragments[5]);
						// ustawiamy plansze
						board.setMove(indexTouch, indexMove, enemyColor);
					}
					// jesli ktos z przeciwnikow wygral
					else if (receivedMessage.contains("Winner")) {
						warning.setText("");
						text.setForeground(Color.RED);
						text.setText(receivedMessage);
						break;
					}
					// inne wiadomosci np. kto wykonal ruch i kto teraz
					else {
						text.setText(receivedMessage);
						warning.setText("");
					}
				}
				// gdy nic nie otrzymamy wychodzimy z petli
				else {
					break;
				}
				// aktualizujemy plansze
				board.repaint();
			}
			board.repaint();
			output.println("QUIT");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			socket.close();
		}
	}

	/**
	 * ogladanie
	 */
	public void watch() {
		// wiadomosc otrzymana z serwera
		String receivedMessage = input.nextLine();
		// liczba graczy w ogladanej grze
		int players = Integer.parseInt(receivedMessage);
		setupWatch(players);
		receivedMessage = input.nextLine();
		String colorName;
		Color color;
		int indexTouch;
		int indexMove;
		// pobieramy ruchu dopoki nie otrzymamy informacji o koncu
		while (!(receivedMessage.equals("END"))) {
			colorName = receivedMessage;
			color = board.stringToColor(colorName);
			receivedMessage = input.nextLine();
			indexTouch = Integer.parseInt(receivedMessage);
			receivedMessage = input.nextLine();
			indexMove = Integer.parseInt(receivedMessage);
			receivedMessage = input.nextLine();
			// jesli ruch to wyjscie gracza
			if (indexMove == -1 && indexTouch == -1) {
				// usuwamy kolor
				board.deleteColor(color);
				// jesli jest koniec gry bo ktos wygral
			} else if (indexMove == -3 && indexTouch == -3) {
				text.setText("Winner is "+colorName);
			}
			// jesli jest koniec gry no jest remis
			else if (indexMove == -4 && indexTouch == -4) {
				text.setText("Deuce");
			}
			// jesli ruch to nie wyjscie i nie skonczenie ruchu
			else if (indexMove != -2 && indexTouch != -2) {
				// ustawiamy ruch
				board.setMove(indexTouch, indexMove, color);
			}

			// rysujemy plansze na nowo
			board.repaint();
		}
	}

	/**
	 * odpalamy klienta i gre
	 */
	public static void main(String[] args) {
		// tworzymy client za pomoca BeansClient.xml
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext("BeansClient.xml");
			ChineseCheckersClient client = (ChineseCheckersClient) context.getBean("chineseClient");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * klasa zajmujaca sie przyciskami
	 */
	class ButtonListener implements ActionListener {

		/**
		 * akcja przycisku
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			// konczymy ruch
			if (e.getActionCommand().equals("End move")) {
				output.println("END MOVE");
			}
			// wybieramy tryb gry
			if (e.getActionCommand().equals("Play")) {
				output.println("PLAY");
				String receivedMessage = input.nextLine();
				// jesli otrzymamy komunikat o bledzie
				if (receivedMessage.contains("Another") || receivedMessage.contains("Game")) {
					fail.setForeground(Color.RED);
					fail.setText(receivedMessage);
					textField.setText("");
				}
				// jesli nie otrzymamy komunikatu o bledzie
				else {
					mode = "PLAY";
					dialog.setVisible(false);
				}
			}
			// wybieramy tryb ogladania
			if (e.getActionCommand().equals("Watch")) {
				// jesli pole tekstowe jest puste, pobieramy tabele z comboboxa
				if (textField.getText().equals("")) {
					output.println("WATCH: " + comboBox.getSelectedItem());
					String receivedMessage = input.nextLine();
					// jesli otrzymamy komunikat o bledzie
					if (receivedMessage.contains("Another")) {
						fail.setForeground(Color.RED);
						fail.setText(receivedMessage);
						textField.setText("");
					}
					// jesli nie otrzymamy komunikatu o bledzie
					else {
						mode = "WATCH";
						dialog.setVisible(false);
					}
				}
				// bierzemy nazwe tabeli z pola tekstowego
				else {
					output.println("WATCH: " + textField.getText());
					String receivedMessage = input.nextLine();
					if (receivedMessage.contains("Game")) {
						fail.setForeground(Color.RED);
						fail.setText(receivedMessage);
						textField.setText("");
					} else if (receivedMessage.contains("Another")) {
						fail.setForeground(Color.RED);
						fail.setText(receivedMessage);
						textField.setText("");
					}

					else {
						mode = "WATCH";
						dialog.setVisible(false);
					}
				}
			}
		}

	}

	/**
	 * klasa zajmujaca sie kliknieciami myszki
	 */
	class Mouse extends MouseAdapter {
		// wspolrzedne klikniecia
		double x, y;

		/**
		 * gdy myszka zostanie kliknieta
		 */
		public void mouseClicked(MouseEvent e) {
			x = e.getX();
			y = e.getY();
			// jesli to dotkniecie
			if (counter == 0) {
				// pobieramy indeks dotknietego pola
				// -1 jesli kliniemy cos innego
				indexTouch = board.getIndex(x, y);
				// jesli wybralismy pole wysylamy na serwer
				if (indexTouch >= 0) {
					output.println("TOUCHED " + indexTouch);
				}
			}
			// jesli to ruch
			else if (counter == 1) {
				indexMove = board.getIndex(x, y);
				// jesli wybralismy pole
				if (indexMove >= 0) {
					// jesli wybralismy to samo pole do ruszenia co klikniete i nie skaczemy
					if (indexMove == indexTouch && jumpFlag == 0) {
						// cofamy dotkniecie
						text.setText("You undo the touch");
						counter = 0;
						board.setMove(indexTouch, indexTouch, myColor);
						board.repaint();
					}
					// wysylamy ruch na serwer
					else {
						output.println("MOVED " + indexMove);
					}
				}
			}
		}
	}

}