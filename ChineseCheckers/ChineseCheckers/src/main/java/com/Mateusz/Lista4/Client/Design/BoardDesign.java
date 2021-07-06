/**
 * 
 */
package com.Mateusz.Lista4.Client.Design;

import java.awt.Color;

/**
 * klasa odpowiedzialna za opcje planszy gry
 */
public class BoardDesign {
	// szerokosc planszy
	int size;
	// ilosc pionkow
	int amount;
	// ilosc pol w kazdym rzedzie
	int bounds[] = new int[50];
	// kolory pionkow
	Color[] colors = new Color[6];
	// nazwy kolorow
	String[] colorsNames = new String[6];

	/**
	 * getery i setery
	 */
	public void setSize(int size) {
		this.size = size;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public void setBounds(int[] bounds) {
		this.bounds = bounds;
	}

	public void setColors(Color[] colors) {
		this.colors = colors;
	}

	public void setColorsNames(String[] colorsNames) {
		this.colorsNames = colorsNames;
	}

	public int getSize() {
		return size;
	}

	public int getAmount() {
		return amount;
	}

	public int getBounds(int i) {
		return bounds[i];
	}

	public Color getColor(int i) {
		return colors[i];
	}

	public String getColorName(int i) {
		return colorsNames[i];
	}

	public int getBoundsSize() {
		return bounds.length;
	}

	public int getColorsSize() {
		return colors.length;
	}

	public Color[] getAllColors() {
		return colors;
	}

	public String[] getAllColorsNames() {
		return colorsNames;
	}
}
