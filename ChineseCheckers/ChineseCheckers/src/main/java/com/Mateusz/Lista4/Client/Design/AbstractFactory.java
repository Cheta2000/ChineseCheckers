/**
 * 
 */
package com.Mateusz.Lista4.Client.Design;

import java.awt.Color;
import java.util.ArrayList;

/**
 * interfejs z metoda tworzaca liste pol
 */
interface FieldMethod {
	public ArrayList<Ellipse> CreateList(BoardDesign boardDesign);
}

/**
 * interfejs z metoda tworzaca tablice kolorow
 */
interface ColorMethod {
	public Color[] CreateColor(BoardDesign boardDesign, int players);
}

/**
 * fabryka abstrakcyjna skladajaca sie z fabryki pol i fabryki kolorow
 */
public abstract class AbstractFactory {
	public abstract ColorMethod getColorMethod(String colorType);

	public abstract FieldMethod getFieldListMethod(String fieldType);
}

/**
 * fabryka wybiera sposob tworzenia pol
 */
class FieldListMethodFactory extends AbstractFactory {
	@Override
	public FieldMethod getFieldListMethod(String fieldType) {
		if (fieldType.equals("Normal")) {
			return new NormalFieldList();
		} else {
			return null;
		}
	}

	@Override
	public ColorMethod getColorMethod(String colorType) {
		return null;
	}
}

/**
 * fabryka wybiera sposob ukladania kolorow
 */
class ColorMethodFactory extends AbstractFactory {

	@Override
	public ColorMethod getColorMethod(String colorType) {
		if (colorType.equals("Normal")) {
			return new NormalColor();
		} else {
			return null;
		}
	}

	@Override
	public FieldMethod getFieldListMethod(String fieldType) {
		return null;
	}

}

/**
 * klasa tworzaca pola w sposob "normalny"
 */
class NormalFieldList implements FieldMethod {

	/**
	 * @return najwieksza ilosc pol w kolumnie lub w wierszu potrzebne do rysowania
	 */
	private int getMax(BoardDesign boardDesign) {
		int max = boardDesign.getBounds(0);
		for (int i = 1; i < boardDesign.getBoundsSize(); i++) {
			if (boardDesign.getBounds(i) > max) {
				max = boardDesign.getBounds(i);
			}
		}
		if (max > boardDesign.getSize()) {
			return max;
		} else {
			return boardDesign.getSize();
		}
	}

	/**
	 * @return lista pol
	 */
	@Override
	public ArrayList<Ellipse> CreateList(BoardDesign boardDesign) {
		int size = boardDesign.getSize();
		ArrayList<Ellipse> fields = new ArrayList<>();
		// zakladamy ze nasza plansza ma wymiary 1000x1000
		// zaczynamy w punkcie (20,450)
		double x = 20;
		double y = 450;
		// przyrost x i y
		double d = Math.floor(960 / size);
		// rozmiar okregu
		double w = Math.floor(960 / getMax(boardDesign));
		// petla po kolumnach planszy
		for (int i = 0; i < size; i++) {
			// w zaleznosci od kolumny ustawiamy y
			if (i == 0)
				y = 450;
			if (i > 0 && i <= 3)
				y = y - (i + 0.5) * d;
			if (i == 4)
				y = y - 8.5 * d;
			if (i > 4 && i <= 8)
				y = y - (17.5 - i) * d;
			if (i == 9)
				y = y - 9.5 * d;
			if (i > 9 && i <= 12)
				y = y - (i + 0.5) * d;
			if (i == 13)
				y = y - 8.5 * d;
			if (i > 13 && i <= 16)
				y = y - (17.5 - i) * d;
			// jesli jestesmy w lewej polowie planszy
			if (i <= size / 2) {
				// petla po wierszach
				for (int j = 0; j < boardDesign.getBounds(i); j++) {
					// tworzymy pole (okrag) i dodajemy do listy
					Ellipse circle = new Ellipse(x, y, w, w);
					fields.add(circle);
					y = y + d;
				}
			} else {
				for (int j = 0; j < (boardDesign.getBounds(size - i - 1)); j++) {

					Ellipse circle = new Ellipse(x, y, w, w);
					fields.add(circle);
					y = y + d;
				}
			}
			x = x + d;
		}
		return fields;
	}
}

/**
 * klasa rozmieszczajaca kolory w sposob "normalny"
 */
class NormalColor implements ColorMethod {

	/**
	 * @return tablica kolorow color[i] to kolor pola (okregu) na liscie w miejscu o
	 *         indeksie i
	 */
	@Override
	public Color[] CreateColor(BoardDesign boardDesign, int players) {
		Color[] colors = new Color[200];
		// petla po tablicy kolorow
		for (int i = 0; i < colors.length; i++) {
			// w zaleznosci od i i graczy ustawiamy kolor
			if (i < 10)
				colors[i] = boardDesign.getColor(0);
			else if (players == 6 && ((i >= 10 && i < 14) || (i >= 23 && i < 26) || (i >= 35 && i < 37) || i == 46))
				colors[i] = boardDesign.getColor(1);
			else if (players >= 4 && ((i >= 19 && i < 23) || (i >= 32 && i < 35) || (i >= 44 && i < 46) || i == 55))
				colors[i] = boardDesign.getColor(2);
			else if (players >= 3 && (i == 65 || (i >= 75 && i < 77) || (i >= 86 && i < 89) || (i >= 98 && i < 102)))
				colors[i] = boardDesign.getColor(3);
			else if (players >= 3 && players != 4
					&& (i == 74 || (i >= 84 && i < 86) || (i >= 95 && i < 98) || (i >= 107 && i < 111)))
				colors[i] = boardDesign.getColor(4);
			else if (players != 3 && i >= 111)
				colors[i] = boardDesign.getColor(5);
			// pola ktore nie sa pionkami sa biale
			else
				colors[i] = Color.white;
		}
		return colors;

	}
}

/**
 * klasa produkujaca fabryki w zaleznosci od wyboru
 */
class FactoryProducer {

	/**
	 * @param choice wybor fabryki
	 * @return fabryka
	 */
	public static AbstractFactory getFactory(String choice) {

		if (choice.equals("Field")) {
			return new FieldListMethodFactory();

		} else if (choice.equals("Color")) {
			return new ColorMethodFactory();
		}

		return null;
	}
}
