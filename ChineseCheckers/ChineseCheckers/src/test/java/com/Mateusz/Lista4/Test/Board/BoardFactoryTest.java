package com.Mateusz.Lista4.Test.Board;

import static org.junit.Assert.assertEquals;

import java.awt.Color;

import org.junit.Test;

import com.Mateusz.Lista4.Client.Design.Board;
import com.Mateusz.Lista4.Client.Design.Ellipse;

/**
 * test fabryki
 */
public class BoardFactoryTest {
	Board board = new Board(6);

	@Test
	public void checkListSize() {
		int expected = 121;
		assertEquals(121, board.getListSize());
	}

	@Test
	public void checkListElement() {
		Ellipse expected = new Ellipse(20, 450, Math.floor(960 / 17), Math.floor(960 / 17));
		assertEquals(expected, board.getListElement(0));
	}

	@Test
	public void checkColorElement() {
		Color expected = Color.YELLOW;
		assertEquals(expected, board.getColorElement(20));

	}
}
