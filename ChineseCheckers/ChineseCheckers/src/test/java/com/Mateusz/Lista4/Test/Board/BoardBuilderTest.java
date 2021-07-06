package com.Mateusz.Lista4.Test.Board;

import static org.junit.Assert.assertEquals;

import java.awt.Color;

import org.junit.Test;

import com.Mateusz.Lista4.Client.Design.Board;

/**
 * test sprawdzajacy dzialanie buildera
 */
public class BoardBuilderTest {
	Board board = new Board(6);

	@Test
	public void checkSize() {
		int expected = 17;
		assertEquals(expected, board.getBoardDesign().getSize());
	}

	@Test
	public void checkBounds() {
		int expected = 13;
		assertEquals(expected, board.getBoardDesign().getBounds(4));
	}

	@Test
	public void checkColor() {
		Color expected = Color.BLACK;
		assertEquals(expected, board.getBoardDesign().getColor(0));
	}

	@Test
	public void checkAmount() {
		int expected = 10;
		assertEquals(expected, board.getBoardDesign().getAmount());
	}

	@Test
	public void checkColorName() {
		String expected = "black";
		assertEquals(expected, board.getBoardDesign().getColorName(0));
	}

	@Test
	public void checkAllColorNames() {
		String expected = "black";
		String[] colorsNames = board.getAllColorsNames();
		assertEquals(expected, colorsNames[0]);
	}
}
