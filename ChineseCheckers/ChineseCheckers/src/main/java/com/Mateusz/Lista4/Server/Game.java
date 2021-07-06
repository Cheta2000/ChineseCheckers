package com.Mateusz.Lista4.Server;

import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.Mateusz.Lista4.Client.Design.Board;
import com.Mateusz.Lista4.Client.Design.Ellipse;
import com.Mateusz.Lista4.Server.Game.Player;
import com.Mateusz.Lista4.Server.Exception.IllegalMoveException;
import com.Mateusz.Lista4.Server.JDBC.Move;
import com.Mateusz.Lista4.Server.JDBC.MovesJDBC;

/**
 * interfejs z zasadami gry
 */
interface Rules {
	public void move(int location, Player player) throws IllegalMoveException;

	public void touch(int location, Player player) throws IllegalMoveException;

	public void endMove(Player player) throws IllegalMoveException;
}

/**
 * klasa odpowiedzialna za gre
 */

public class Game {
	// plansza gry
	private Board board;
	// schemat planszy gry
	private Color[] boardColors;
	// liczba graczy
	int players;
	// liczba graczy ktora wyszla
	int left;
	// indeks dotknietego i ruszonego pola
	int indexTouch;
	int indexMove;
	// aktualny gracz
	Player currentPlayer;
	// ID gracza ktory ruszal sie poprzednio i ktory rusza sie aktualnie
	int previousMoveID;
	int moveID;
	// ID gracza ktory zaczyna
	int startID;
	// 0 gdy ruch nie byl skokiem, 1 gdy byl
	int jumpFlag;
	// licznik remisu
	int deuce;
	// tablica graczy
	Player[] playersTable;
	// kolory graczy
	String[] colorsNames;
	Color[] colors;
	// klasa ruchow z ktorej aktualnie korzysta gra
	Rules rules;
	// mediator uczestniczacy przy wysylaniu wiadomosci
	GameMediator mediator;
	// posrednik do bazy danych
	MovesJDBC movesJDBC;
	// nazwa tabeli w bazie danych
	String table;
	// tryb: ogladanie,granie
	String mode;
	// liczba polaczonych GRAJACYCH
	int connected;

	/**
	 * NOWY KONSTRUKTOR ustawiamy ktore zasady uzywamy
	 */
	public Game() {
		rules = new NormalMoves();

	}

	/**
	 * STARY KONSTRUKTOR potrzebny do testow
	 * 
	 * @param playersliczba graczy
	 */
	public Game(int players) {
		this.players = players;
		rules = new NormalMoves();
		mediator = new GameMediator();
		playersTable = new Player[players];
		colorsNames = new String[players];
		colors = new Color[players];
		board = new Board(players);
		boardColors = board.getBoardColors();
		// pobieramy wszytskie dostepne kolory i dostosowujemy do naszej gry
		String[] possibleColorsNames = board.getAllColorsNames();
		setColorName(possibleColorsNames);
		Color[] possibleColors = board.getAllColors();
		setColor(possibleColors);
		// losowanie kto zaczyna
		Random random = new Random();
		startID = random.nextInt(players);
		moveID = startID;
		previousMoveID = -1;
	}

	/**
	 * init-method z beans
	 */
	public void setup() {
		playersTable = new Player[players];
		colorsNames = new String[players];
		colors = new Color[players];
		boardColors = board.getBoardColors();
		// pobieramy wszytskie dostepne kolory i dostosowujemy do naszej gry
		String[] possibleColorsNames = board.getAllColorsNames();
		setColorName(possibleColorsNames);
		Color[] possibleColors = board.getAllColors();
		setColor(possibleColors);
		// losowanie kto zaczyna
		Random random = new Random();
		startID = random.nextInt(players);
		moveID = startID;
	}

	/**
	 * getery i setery
	 */

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void setConnected(int connected) {
		this.connected = connected;
	}

	public void setBoardColors(Color[] boardColors) {
		this.boardColors = boardColors;
	}

	public void setColorsNames(String[] colorsNames) {
		this.colorsNames = colorsNames;
	}

	public void setColors(Color[] colors) {
		this.colors = colors;
	}

	public void setPlayers(int players) {
		this.players = players;
	}

	public void setPreviousMoveID(int previousMoveID) {
		this.previousMoveID = previousMoveID;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public void setJumpFlag(int jumpFlag) {
		this.jumpFlag = jumpFlag;
	}

	public void setDeuce(int deuce) {
		this.deuce = deuce;
	}

	public void setMediator(GameMediator mediator) {
		this.mediator = mediator;
	}

	public void setIndexMove(int indexMove) {
		this.indexMove = indexMove;
	}

	public void setIndexTouch(int i) {
		indexTouch = i;
	}

	public void setStartID(int i) {
		startID = i;
		moveID = startID;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public void setCurrentPlayer() {
		currentPlayer = playersTable[moveID];
	}

	public void setMovesJDBC(MovesJDBC movesJDBC) {
		this.movesJDBC = movesJDBC;
	}

	public int getIndexTouch() {
		return indexTouch;
	}

	public int getIndexMove() {
		return indexMove;
	}

	public String getColorName(int i) {
		return colorsNames[i];
	}

	public Color getColor(int i) {
		return colors[i];
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public int getStartID() {
		return startID;
	}

	/**
	 * @param x
	 * @param y
	 * @return id pola w ktorym zawiera sie (x,y), -1 gdy takiego nie ma
	 */
	public int getFieldIDDueToXY(double x, double y) {
		ArrayList<Ellipse> fields = board.getFieldList();
		for (Ellipse ellipse : fields) {
			if (ellipse.isHit(x, y)) {
				return fields.indexOf(ellipse);
			}
		}
		return -1;
	}

	/**
	 * dodanie gracza do tabeli graczy
	 */
	public void insertIntoTable(int i, Player player) {
		playersTable[i] = player;
	}

	/**
	 * @param touch
	 * @param move
	 * @param color
	 * 
	 *              po wykonaniu ruchu odswiezamy tablice, pole z ktorego ruszamy
	 *              robi sie biale, a pole na ktore sie ruszamy przyjmuje kolor
	 *              gracza ktory wykonal ruch
	 */
	public void refreshBoard(int touch, int move, Color color) {
		boardColors[touch] = Color.white;
		boardColors[move] = color;
	}

	/**
	 * @param ID id gracza ktory wyszedl
	 * 
	 *           pola gracza ktory wyszedl ustawiamy na bialo
	 */
	public void deletePlayer(int ID) {
		for (int i = 0; i < boardColors.length; i++) {
			if (boardColors[i].equals(colors[ID])) {
				boardColors[i] = Color.white;
			}
		}
	}

	/**
	 * @return ustawia nazwe koloru dla danego gracza
	 */
	public void setColorName(String[] possibleColorsNames) {
		if (players == 2) {
			colorsNames[0] = possibleColorsNames[0];
			colorsNames[1] = possibleColorsNames[5];
		} else if (players == 3) {
			colorsNames[0] = possibleColorsNames[0];
			colorsNames[1] = possibleColorsNames[3];
			colorsNames[2] = possibleColorsNames[4];
		} else if (players == 4) {
			colorsNames[0] = possibleColorsNames[0];
			colorsNames[1] = possibleColorsNames[3];
			colorsNames[2] = possibleColorsNames[5];
			colorsNames[3] = possibleColorsNames[2];
		} else if (players == 6) {
			colorsNames[0] = possibleColorsNames[0];
			colorsNames[1] = possibleColorsNames[1];
			colorsNames[2] = possibleColorsNames[3];
			colorsNames[3] = possibleColorsNames[5];
			colorsNames[4] = possibleColorsNames[4];
			colorsNames[4] = possibleColorsNames[2];
		}
	}

	/**
	 * @return ustawia kolor dla danego gracza
	 */
	public void setColor(Color[] possibleColors) {
		if (players == 2) {
			colors[0] = possibleColors[0];
			colors[1] = possibleColors[5];
		} else if (players == 3) {
			colors[0] = possibleColors[0];
			colors[1] = possibleColors[3];
			colors[2] = possibleColors[4];
		} else if (players == 4) {
			colors[0] = possibleColors[0];
			colors[1] = possibleColors[3];
			colors[2] = possibleColors[5];
			colors[3] = possibleColors[2];
		} else if (players == 6) {
			colors[0] = possibleColors[0];
			colors[1] = possibleColors[1];
			colors[2] = possibleColors[3];
			colors[3] = possibleColors[5];
			colors[4] = possibleColors[4];
			colors[4] = possibleColors[2];
		}
	}

	/**
	 * zmiana stanu, ustawiamy kolejnego gracza i zapamietujemy poprzedniego
	 */
	public void goNext() {
		previousMoveID = moveID;
		do {
			moveID = (moveID + 1) % players;
		} while (playersTable[moveID] == null);
		setCurrentPlayer();
	}

	/**
	 * klasa z normalnymi ruchami
	 */
	public class NormalMoves implements Rules {

		/**
		 * @return dystans przy legalnym ruchu
		 * 
		 *         wykorzystujemy wiedze ze z pola 0 mozna ruszyc sie na pole 1,
		 *         wszystko dziala na tej samej zasadzie
		 */
		public double countMoveDistance() {
			Ellipse ellipse1 = board.getListElement(0);
			Ellipse ellipse2 = board.getListElement(1);
			double distance = ellipse1.distance(ellipse2);
			return distance;
		}

		/**
		 * @return dystans przy jednym z dwoch legalnych skokow
		 * 
		 *         wykorzystujemy wiedza ze najdluzszy mozliwy skok moze odbyc sie z
		 *         pola 0 na pole 3
		 */
		public double countMaxJumpDistance() {
			Ellipse ellipse1 = board.getListElement(0);
			Ellipse ellipse2 = board.getListElement(3);
			double distance = ellipse1.distance(ellipse2);
			return distance;
		}

		/**
		 * @param touch dotkniete pole
		 * @param move  pole na ktore ruszamy
		 * @return czy gracz moze skoczyc
		 */
		public boolean canJump(Ellipse ellipse1, Ellipse ellipse2) {
			double avgX = (ellipse1.getX() + ellipse2.getX()) / 2;
			double avgY = (ellipse1.getY() + ellipse2.getY()) / 2;
			int index = getFieldIDDueToXY(avgX, avgY);
			if (index == -1)
				return false;
			else if (board.getColorElement(index) != Color.WHITE)
				return true;
			else
				return false;
		}

		/**
		 * @param location pole
		 * @param player   gracz
		 * @throws IllegalMoveException
		 * 
		 *                              rzucamy exception gdy gracz nie moze wykonac
		 *                              takiego ruchu, gdy gracz moze wykonac taki ruch
		 *                              robimy go i zmieniamy kolejke
		 * 
		 *                              sprawdzanie i ustawianie ruchu
		 */
		public synchronized void move(int location, Player player) throws IllegalMoveException {
			// 0 gdy ruch nie byl skokiem, 1 gdy byl
			int wasJump = 0;
			// jesli gracz nie jest aktualnie grajacym graczem
			if (player != currentPlayer) {
				throw new IllegalMoveException("It is not your turn");
			}
			// jesli pole jest zajete
			else if (boardColors[location] != Color.WHITE) {
				throw new IllegalMoveException("Field is already occupied");
			}
			// jesli gracz chce wyjsc ze zwycieskiego trojkata
			else if (player.isInWinZone(indexTouch) && player.isInWinZone(location) == false) {
				throw new IllegalMoveException("You cannot leave win triangle");
			} else {
				// liczymy dystanse miedzy pole dotknietym a tym na ktore chcemy sie ruszyc
				Ellipse ellipse1 = board.getListElement(indexTouch);
				Ellipse ellipse2 = board.getListElement(location);
				double distance = ellipse1.distance(ellipse2);
				// jesli dystans jest wiekszy niz dystans w mozliwym ruchu
				if (distance > countMoveDistance()) {
					// sprawdzamy czy dystans miesci sie w dopuszczalnych granicach skoku i czy
					// mozna taki skok wykonac
					if (((distance < countMaxJumpDistance() && ellipse1.getX() == ellipse2.getX())
							|| distance == countMaxJumpDistance()) && canJump(ellipse1, ellipse2)) {
						// wykonujemy skok i ustawiamy odpowiednie parametry
						jumpFlag = 1;
						wasJump = 1;
					}
					// jezeli pole nie jest w zasiegu
					else {
						throw new IllegalMoveException("Field is not in range");
					}
				}
			}
			// jesli po skoku wykonujemy normalny ruch
			if (jumpFlag == 1 && wasJump == 0) {
				throw new IllegalMoveException("After jump you can only jump");
			}
			// ustawiamy miejsce ruchu i odswiezamy plansze
			deuce = 0;
			indexMove = location;
			refreshBoard(indexTouch, indexMove, colors[player.getPlayerID()]);
			// jesli nie bylo skoku zmieniamy kolejke
			if (jumpFlag == 0) {
				goNext();
			}
		}

		/**
		 * sprawdzanie i ustawienia dotkniecia
		 */
		public synchronized void touch(int location, Player player) throws IllegalMoveException {
			if (player != currentPlayer) {
				throw new IllegalMoveException("It is not your turn");
			}
			// jesli dotkniemy pionka innego koloru niz nasz
			else if (colors[player.getPlayerID()] != board.getColorElement(location)) {
				throw new IllegalMoveException("It is not your pawn");
			}
			// ustawiamy miejsce dotkniecia
			indexTouch = location;
		}

		/**
		 * sprawdzanie i ustawienia konca ruchu
		 */
		public synchronized void endMove(Player player) throws IllegalMoveException {
			if (player != currentPlayer) {
				throw new IllegalMoveException("It is not your turn");
			}
			// konczymy skoki
			jumpFlag = 0;
			deuce++;
			goNext();
		}
	}

	/**
	 * klasa gracza
	 */
	public class Player implements Runnable {
		// ID gracza
		int playerID;
		// zwycieski trojkat gracza
		int[] winZone;
		Socket socket;
		public Scanner input;
		public PrintWriter output;

		/**
		 * STARY KONSTRUKTOR
		 */
		public Player(Socket socket, int playerID) {
			this.socket = socket;
			this.playerID = playerID;
			winZone = new int[10];
		}

		/**
		 * STARY KONSTRUKTOR potrzebny do testow
		 * 
		 * @param playerID ID gracza
		 */
		public Player(int playerID) {
			this.playerID = playerID;
			winZone = new int[10];
		}

		/**
		 * NOWY KONSTRUKTOR
		 */
		public Player(Socket socket) {
			this.socket = socket;
			winZone = new int[10];
		}

		/**
		 * ID gracza = jako ktory sie polaczyl
		 */
		public void setPlayerID() {
			playerID = connected;
		}

		public int getPlayerID() {
			return playerID;
		}

		/**
		 * ustawiamy zwycieski trojkat
		 */
		public void setWinZone() {
			int j = 0;
			// szukamy pol w kolorze gracza
			for (int i = 0; i < board.getListSize(); i++) {
				// gdy znajdziemy takie pole wykorzystujemy wiedze ze wygrywajace pole jest na
				// przeciwko
				if (board.getColorElement(i) == colors[getPlayerID()]) {
					winZone[j] = board.getListSize() - i - 1;
					j++;
				}
			}
		}

		/**
		 * @param field sprawdzane pole
		 * @return true jesli pole jest w zwycieskim trojkacie, else false
		 */
		public boolean isInWinZone(int field) {
			for (int i = 0; i < 10; i++) {
				if (field == winZone[i])
					return true;
			}
			return false;
		}

		/**
		 * @return true jesli kolor gracza zapelnil zwycieski trojkat, else false
		 */
		public boolean isWinner() {
			for (int i = 0; i < 10; i++) {
				if (boardColors[winZone[i]] != colors[getPlayerID()])
					return false;
			}
			return true;
		}

		/**
		 * metoda wykonywana przez zaczety watek
		 */
		@Override
		public void run() {
			try {
				// ustawienia poczatkowe
				setup();
				// gdy gramy
				if (mode.equals("PLAY")) {
					// przetwarzanie komend
					processCommands();
				}
				// gdy ogladamy
				else {
					processQueries();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// gdy gracz, widz wyjdzie
			finally {
				// jesli zostal jeszcze jakis gracz i jestesmy w trybie gry
				if (players - left > 1 && mode.equals("PLAY")) {
					// sprawdzamy czy wychodzacy do gracz czy widz ktory probowal "nielegalnie"
					// wejsc
					if (playerID < players) {
						// gdy gracz wyjdzie wysylamy informacje do bd z indeksami -1
						movesJDBC.insert(table, colorsNames[playerID], -1, -1);
						leave();
					}
				}
				// jesli jestesmy w trybie ogladania lub gra zostala juz wczesniej zakonczona
				if (mode.contains("WATCH") || players - left < 2) {
					// resetujemy tryb
					mode = "";
				}
			}
			try {
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * ustawienia poczatkowe
		 */
		private void setup() throws IOException {
			input = new Scanner(socket.getInputStream());
			output = new PrintWriter(socket.getOutputStream(), true);
			// wysylamy do klienta nazwy tabel z 10 ostatnimi grami
			List<String> tableName = movesJDBC.getGames();
			for (String record : tableName) {
				output.println(record);
			}
			// wysylamy komunikat ze to juz koniec przesylania tabel
			output.println("END TABLES");
			// flaga mowiaca czy jest zgodnosc trybow
			int done = 0;
			// musimy dac graczu jego ID
			setPlayerID();
			while (done == 0) {
				String tmp = input.nextLine();
				// jesli gracz wybierze tryb gry ale gra juz sie zaczela
				if (tmp.equals("PLAY") && players == connected) {
					// wysylamy informacje o bledzie i ustawiamy flage na 0
					output.println("Game has already started");
					done = 0;
				} else {
					// jesli tryb gry jeszcze nie byl ustalony
					if (mode.equals("")) {
						mode = tmp;
						done = 1;
					}
					// jesli gracz wybierze inny tryb niz jest aktualnie wybrany
					if ((mode.equals("PLAY") && tmp.contains("WATCH"))
							|| mode.contains("WATCH") && tmp.equals("PLAY")) {
						// wysylamy informacje o bledzie i ustawiamy flage na 0
						output.println("Another mode is chosen");
						done = 0;
					}
					// jesli gracz wybierze ten sam tryb
					else {
						// ustawiamy flage ze tryb zostal uzgodniony
						done = 1;
						// podstawiamy tryb jeszcze raz, poniewaz gracz moze chciec ogladac, ale inna
						// gre
						mode = tmp;
						// jesli tryb to granie
						if (mode.equals("PLAY")) {
							// dodajemy gracza do tablicy w mediatorze przesylajacym wiadomosci
							mediator.addPlayer(this);
							// gdy połączy się pierwszy gracz tworzymy tabele gry
							if (connected == 0) {
								// nazwa tabeli to Game: + aktualna data
								Date now = new Date();
								SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd  kk:mm:ss");
								table = "Game: " + ft.format(now);
								movesJDBC.createTable(table);
							}
							System.out.println("New player has connected");
							// wysylamy komunikat ze nie bylo zadnego bledu
							mediator.sendMessageOnly("OK", this);
							// zwiekszamy liczbe polaczonych graczy
							connected++;
							// ustawiamy "zwycieski trojkat"
							setWinZone();
							// serwer wysyla do kazdego liczbe graczy i jego kolor
							mediator.sendMessageOnly(players + "", this);
							mediator.sendMessageOnly(colorsNames[getPlayerID()], this);
							// wrzucamy gracza do tablicy graczy
							insertIntoTable(playerID, this);
							// jesli jeszcze nie wszyscy gracze sie polaczyli
							if (playerID < players - 1) {
								// wysylanie odpowiednich komunikatow
								mediator.sendMessageOnly(
										"Waiting for opponents... You play with: " + colorsNames[playerID], this);
							} else {
								setCurrentPlayer();
								mediator.sendMessageOnly("Your move: " + colorsNames[startID], playersTable[startID]);
								for (int i = 0; i < players; i++) {
									if (i != startID) {
										mediator.sendMessageOnly("You play with: " + colorsNames[i] + ". Move of: "
												+ colorsNames[startID], playersTable[i]);
									}
								}
							}
						}
					}
				}
			}
		}

		/**
		 * przetwarzanie ogladania
		 */
		private void processQueries() {
			// flaga mowiaca czy widz wybral istniejaca tabele
			int done = 0;
			// odpowiednio wyciagamy nazwe tabeli
			table = mode.substring(7);
			while (done == 0) {
				// jesli istnieje taka tabela
				if (movesJDBC.tableExists(table)) {
					// ustawiamy flage
					done = 1;
					// wysylamy komunikat ze nie ma bledu oraz liczbe graczy w wybranej grze
					output.println("OK");
					output.println("" + movesJDBC.getPlayers(table));
					// wysylamy ruch i czekamy 1s
					List<Move> moves = movesJDBC.getMoves(table);
					for (Move record : moves) {
						output.println(record.getColor());
						output.println("" + record.getIndexTouch());
						output.println("" + record.getIndexMove());
						if (record.getIndexTouch() != -2 && record.getIndexMove() != -2) {
							try {
								TimeUnit.SECONDS.sleep(1);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					// po zakonczeniu gry wysylamy odpowiedni komunikat
					output.println("END");
				}
				// gdy nie ma takiej tabeli
				else {
					// wysylamy komunikat o bledzie
					output.println("Game does not exist");
					// pobieramy nowa nazwe
					String receivedMessage = input.nextLine();
					while (receivedMessage.equals("PLAY")) {
						output.println("Another mode was chosen");
						receivedMessage = input.nextLine();
					}
					table = receivedMessage.substring(7);

				}
			}
		}

		/**
		 * gdy gracz wyjdzie
		 */
		private void leave() {
			// usuwamy kolor z planszy
			deletePlayer(playerID);
			// zwiekszamy ilosc graczy ktora wyszla
			left++;
			// gdy gracz ktory wyszedl byl na ruchu
			if (playerID == moveID) {
				goNext();
			}
			// gdy gra toczy sie dalej
			if ((players - left) > 1) {
				mediator.sendMessageExcept(
						"Opponent left the game: " + colorsNames[playerID] + " . Move of " + colorsNames[moveID],
						playersTable[playerID], playersTable[moveID]);
				mediator.sendMessageOnly(
						"Opponent left the game: " + colorsNames[playerID] + " . Your move: " + colorsNames[moveID],
						playersTable[moveID]);
				playersTable[playerID] = null;
			}
			// gdy zostal 1 gracz, gra zakonczona
			else {
				mediator.sendMessageExcept("Opponent left the game: " + colorsNames[playerID], playersTable[playerID],
						null);
				mediator.sendMessageExcept("You win!", playersTable[playerID], null);
				// resetujemy tryb
				mode = "";
				// informacje o zwycięstwie zapisujemy jako -3
				movesJDBC.insert(table, colorsNames[moveID], -3, -3);
			}
		}

		/**
		 * przetwarzanie komend
		 */
		private void processCommands() {
			String receivedMessage = "";
			// dopoki klient nie wyslal komunikatu QUIT
			while (!(receivedMessage.equals("QUIT"))) {
				// jesli klient cos wyslal
				if (input.hasNextLine()) {
					receivedMessage = input.nextLine();
					// jesli klient wyslal polecenie ruchu
					if (receivedMessage.contains("MOVED")) {
						// przetwarzenie komendy ruchu
						processMoveCommand(Integer.parseInt(receivedMessage.substring(6)));
					}
					// jesli klient wyslal polecenie dotkniecia
					else if (receivedMessage.contains("TOUCHED")) {
						// przetwarzenie komendy dotkniecia
						processTouchCommand(Integer.parseInt(receivedMessage.substring(8)));
					}
					// jesli klient wyslal polecenie zakonczenia ruchu
					else if (receivedMessage.equals("END MOVE")) {
						// przetwarzanie komendy zakonczenia ruchu
						processEndCommand();
					}
				} else {
					break;
				}
			}
		}

		/**
		 * @param location pole
		 * 
		 *                 przetwarzenie komendy ruchu
		 */
		private void processMoveCommand(int location) {
			try {
				// probujemy wykonac ruch
				rules.move(location, this);
				// gdy nie zostanie rzucony zaden wyjatek
				// do kazdego gracza oprocz gracza ruszajacego wysylamy komunikat kto sie ruszyl
				// i z jakiego pola na jakie
				// ten ruch jeszcze jest niezakonczony wiec gracz ruszajacy to moveID
				if (jumpFlag == 1) {
					// zapisujemy ruch do bazy danych
					movesJDBC.insert(table, colorsNames[moveID], indexTouch, location);
					mediator.sendMessageExcept(colorsNames[moveID] + " MOVED WITH " + indexTouch + " TO " + indexMove,
							playersTable[moveID], null);
				}
				// ten ruch juz jest zakonczony wiec gracz ktory wykonal ruch to previousMoveID
				else {
					// zapisujemy ruch do bazy danych
					movesJDBC.insert(table, colorsNames[previousMoveID], indexTouch, location);
					mediator.sendMessageExcept(
							colorsNames[previousMoveID] + " MOVED WITH " + indexTouch + " TO " + indexMove,
							playersTable[previousMoveID], null);
				}
				// jesli jest zwyciezca
				if (isWinner()) {
					// odpowiedni wysylamy komunikaty kto wygral
					if (jumpFlag == 1) {
						mediator.sendMessageExcept("Winner is: " + colorsNames[moveID], playersTable[moveID], null);
					} else {
						mediator.sendMessageExcept("Winner is: " + colorsNames[previousMoveID],
								playersTable[previousMoveID], null);
					}
					mediator.sendMessageOnly("You win!", this);
					mode = "";
					// informacje o zwycięstwie zapisujemy jako -3
					movesJDBC.insert(table, colorsNames[playerID], -3, -3);
				}
				// jezeli nie ma zwyciezcy
				else {
					// odpowiednie wysylamy komunikaty kto skoczyl, kto sie rusza itp
					if (jumpFlag == 0) {
						mediator.sendMessageOnly("You moved. Move of: " + colorsNames[moveID], this);
						mediator.sendMessageOnly("Opponent moved with: " + colorsNames[previousMoveID] + ". Your move: "
								+ colorsNames[moveID], playersTable[moveID]);
					} else {
						mediator.sendMessageOnly("You jumped. Your move: " + colorsNames[moveID], this);
					}
					if (jumpFlag == 1) {
						mediator.sendMessageExcept(
								"Opponent jumped with: " + colorsNames[moveID] + ". Move of: " + colorsNames[moveID],
								playersTable[moveID], null);
					} else {
						mediator.sendMessageExcept("Opponent moved with: " + colorsNames[previousMoveID] + ". Move of: "
								+ colorsNames[moveID], playersTable[moveID], playersTable[previousMoveID]);
					}
					indexTouch = indexMove;
				}
			} catch (IllegalMoveException ex) {
				mediator.sendMessageOnly(ex.getMessage(), this);
			}
		}

		/**
		 * 
		 * @param location pole
		 * 
		 *                 przetwarzenie komendy dotkniecia, komunikat wysylamy tylko do
		 *                 jednego gracza
		 */
		private void processTouchCommand(int location) {
			try {
				rules.touch(location, this);
				mediator.sendMessageOnly("You touched. Your move: " + colorsNames[moveID], this);
			} catch (IllegalMoveException ex) {
				mediator.sendMessageOnly(ex.getMessage(), this);
			}
		}

		/**
		 * przetwarzenie komendy zakonczenie/ominiecia ruchu
		 */
		private void processEndCommand() {
			try {
				rules.endMove(this);
				// gdy gracz zakonczy ruch wysylamy go do bd z indeksami -2
				movesJDBC.insert(table, colorsNames[previousMoveID], -2, -2);
				// odpowiednie wysylamy komunikaty kto zakonczyl ruch, kto sie rusza itp
				mediator.sendMessageOnly("You ended move. Move of: " + colorsNames[moveID], this);
				mediator.sendMessageExcept("Opponent ended move with: " + colorsNames[previousMoveID] + ". Move of: "
						+ colorsNames[moveID], playersTable[moveID], playersTable[previousMoveID]);
				mediator.sendMessageOnly("Opponent ended move with: " + colorsNames[previousMoveID] + ". Your move: "
						+ colorsNames[moveID], playersTable[moveID]);
			} catch (IllegalMoveException ex) {
				mediator.sendMessageOnly(ex.getMessage(), this);
			}
			// remis jest gdy kazdy gracz dwukrotnie z rzedu pominie ruch
			if (deuce == (players - left) * 2) {
				mediator.sendMessageExcept("Draw!", null, null);
				mode = "";
				// informacje o remisie zapisujemy jako -4
				// jako kolor dodajemy black gdyz jest on w kazdej grze a chcemy prawidlowo
				// liczyc graczy
				movesJDBC.insert(table, "black", -4, -4);
			}
		}

	}
}