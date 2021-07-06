
package com.Mateusz.Lista4.Client.Design;

import java.awt.Color;

/**
 * budowniczy klasa abstrakcyjna z metodami ustawiajacymi wielkosc, rozmiar i
 * kolory planszy
 */
public abstract class BoardDesignBuilder {
	protected BoardDesign boardDesign;

	public BoardDesign getBoardDesign() {
		return boardDesign;
	}

	public void createNewBoardDesign() {
		boardDesign = new BoardDesign();
	}

	public abstract void buildSize();

	public abstract void buildBounds();

	public abstract void buildColors();

	public abstract void buildAmount();

	public abstract void buildNames();

}

/**
 * klasa ustawiajaca plansze jako "normalna" mozna dodac inne
 */
class NormalBoard extends BoardDesignBuilder {

	/**
	 * typowa plansza ma 17 kolumn
	 */
	@Override
	public void buildSize() {
		boardDesign.setSize(17);

	}

	/**
	 * typowa plansza po lewej stronie planszy ma wiersze wpisanej dlugosci prawa
	 * strona jest symetryczna
	 */
	@Override
	public void buildBounds() {
		int bounds[] = { 1, 2, 3, 4, 13, 12, 11, 10, 9 };
		boardDesign.setBounds(bounds);

	}

	/**
	 * wybor kolorow pionkow
	 */
	@Override
	public void buildColors() {
		Color[] colors = { Color.BLACK, Color.BLUE, Color.YELLOW, Color.RED, Color.GREEN, Color.MAGENTA };
		boardDesign.setColors(colors);
	}

	/**
	 * gracz ma 10 pionkow
	 */

	@Override
	public void buildAmount() {
		boardDesign.setAmount(10);
	}

	@Override
	public void buildNames() {
		String[] colorsNames = { "black", "blue", "yellow", "red", "green", "magenta" };
		boardDesign.setColorsNames(colorsNames);
	}

}

/**
 * klasa wybieracaja i tworzaca plansze danego typu
 */
class Chooser {
	private BoardDesignBuilder boardDesignBuilder;

	public void setBoardDesignBuilder(BoardDesignBuilder bdb) {
		boardDesignBuilder = bdb;
	}

	public BoardDesign getBoardDesign() {
		return boardDesignBuilder.getBoardDesign();
	}

	public void constructBoardDesign() {
		boardDesignBuilder.createNewBoardDesign();
		boardDesignBuilder.buildSize();
		boardDesignBuilder.buildBounds();
		boardDesignBuilder.buildColors();
		boardDesignBuilder.buildAmount();
		boardDesignBuilder.buildNames();
	}

}