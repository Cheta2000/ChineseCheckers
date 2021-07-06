/**
 * 
 */
package com.Mateusz.Lista4.Client.Design;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JPanel;

/**
 * plansza gry
 */

public class Board extends JPanel {

	// informacje o planszy gry (rozmiar i wymiary pola oraz kolory pionkow)
	private BoardDesign boardDesign;
	// wybieracz planszy
	Chooser chooser;
	// budowniczy planszy
	BoardDesignBuilder boardDesignBuilder;
	// pola planszy
	private ArrayList<Ellipse> fields;
	// kolory pol planszy
	private Color[] colors;
	// indeks dotknietego pola
	private int indexTouch;
	private int players;

	/**
	 * konstruktor panelu planszy NOWY KONSTRUKTOR
	 */
	public Board() {
		setBackground(Color.CYAN);
		setSize(1000, 1000);
		setLayout(new BorderLayout());
	}

	/**
	 * konstruktor panelu planszy STARY KONSTRUKTOR zostal do testow
	 */
	public Board(int players) {
		setBackground(Color.CYAN);
		setSize(1000, 1000);
		setLayout(new BorderLayout());
		// builder ustawia "tryb planszy" (wyglad, kolory, itp.)
		Chooser chooser = new Chooser();
		BoardDesignBuilder normalBoard = new NormalBoard();
		chooser.setBoardDesignBuilder(normalBoard);
		chooser.constructBoardDesign();
		boardDesign = chooser.getBoardDesign();
		// abstract factory przygotowuje plansze zgodnie z wybranym trybem
		AbstractFactory fieldFactory = FactoryProducer.getFactory("Field");
		AbstractFactory colorFactory = FactoryProducer.getFactory("Color");
		FieldMethod fieldMethod = fieldFactory.getFieldListMethod("Normal");
		ColorMethod colorMethod = colorFactory.getColorMethod("Normal");
		fields = fieldMethod.CreateList(boardDesign);
		colors = colorMethod.CreateColor(boardDesign, players);
		indexTouch = -1;
	}

	/**
	 * metoda inicjalizujaca dla klasy, nie mozemy jej wywolac w beans bo czekamy na
	 * informacje z serwera o liczbe graczy
	 */
	public void setup() {
		// builder ustawia "tryb planszy" (wyglad, kolory, itp.)
		chooser.setBoardDesignBuilder(boardDesignBuilder);
		chooser.constructBoardDesign();
		boardDesign = chooser.getBoardDesign();
		// abstract factory przygotowuje plansze zgodnie z wybranym trybem
		AbstractFactory fieldFactory = FactoryProducer.getFactory("Field");
		AbstractFactory colorFactory = FactoryProducer.getFactory("Color");
		FieldMethod fieldMethod = fieldFactory.getFieldListMethod("Normal");
		ColorMethod colorMethod = colorFactory.getColorMethod("Normal");
		fields = fieldMethod.CreateList(boardDesign);
		colors = colorMethod.CreateColor(boardDesign, players);
	}

	/**
	 * getery i setery
	 */

	public void setBoardDesignBuilder(BoardDesignBuilder boardDesignBuilder) {
		this.boardDesignBuilder = boardDesignBuilder;
	}

	public void setChooser(Chooser chooser) {
		this.chooser = chooser;
	}

	public void setPlayers(int players) {
		this.players = players;
	}

	public void setIndexTouch(int indexTouch) {
		this.indexTouch = indexTouch;
	}

	public Ellipse getListElement(int i) {
		return fields.get(i);
	}

	public Color getColorElement(int i) {
		return colors[i];
	}

	public Color[] getBoardColors() {
		return colors;
	}

	public Color getColor(int i) {
		return boardDesign.getColor(i);
	}

	public Color[] getAllColors() {
		return boardDesign.getAllColors();
	}

	public String getColorName(int i) {
		return boardDesign.getColorName(i);
	}

	public String[] getAllColorsNames() {
		return boardDesign.getAllColorsNames();
	}

	public int getColorSize() {
		return boardDesign.getColorsSize();
	}

	public int getListSize() {
		return fields.size();
	}

	public ArrayList<Ellipse> getFieldList() {
		return fields;
	}

	public BoardDesign getBoardDesign() {
		return boardDesign;
	}

	/**
	 * @param name nazwa koloru
	 * @return kolor odpowiadajacy nazwie, null jesli takiego nie ma
	 */
	public Color stringToColor(String name) {
		for (int i = 0; i < getColorSize(); i++) {
			if (name.equals(getColorName(i))) {
				return getColor(i);
			}
		}
		return null;
	}

	/**
	 * @param x
	 * @param y
	 * @return indeks kliknietego pola, -1 jesli nie ma takiego
	 */
	public int getIndex(double x, double y) {
		for (Ellipse ellipse : fields) {
			if (ellipse.isHit(x, y))
				return fields.indexOf(ellipse);
		}
		return -1;
	}

	/**
	 * ustawienie indeksu dotknietego pola
	 */
	public void setTouch(int i) {
		indexTouch = i;
	}

	/**
	 * ustawienia pol o danym koloerze na biale
	 * 
	 * @param color
	 */
	public void deleteColor(Color color) {
		for (int i = 0; i < colors.length; i++) {
			if (colors[i].equals(color)) {
				colors[i] = Color.white;
			}
		}
	}

	/**
	 * @param t
	 * @param m
	 * @param color
	 * 
	 *              robimy ruch: pole nie jest juz dotkniete, nowe pole przyjmuje
	 *              odpowiedni kolor, stare staje sie biale
	 */
	public void setMove(int touch, int move, Color color) {
		indexTouch = -1;
		if (touch >= 0 && move >= 0) {
			colors[touch] = Color.white;
			colors[move] = color;
		}
	}

	/**
	 * rysowanie
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawBoard(g);
	}

	public void drawBoard(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(5));
		// petla po polach gry (okregi na liscie)
		for (int i = 0; i < fields.size(); i++) {
			Shape shape = fields.get(i);
			// ustawienie koloru danego pola
			if (i == indexTouch) {
				g2d.setColor(Color.ORANGE);
				g2d.draw(shape);
			}
			g2d.setColor(colors[i]);
			g2d.fill(shape);
		}
	}

}
