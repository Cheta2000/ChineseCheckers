/**
 * 
 */
package com.Mateusz.Lista4.Server.JDBC;

/**
 * klasa ruchu
 */
public class Move {
	private int ID;
	private String color;
	private int indexTouch;
	private int indexMove;

	/**
	 * getery i setery
	 */
	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getIndexTouch() {
		return indexTouch;
	}

	public void setIndexTouch(int indexTouch) {
		this.indexTouch = indexTouch;
	}

	public int getIndexMove() {
		return indexMove;
	}

	public void setIndexMove(int indexMove) {
		this.indexMove = indexMove;
	}

}
