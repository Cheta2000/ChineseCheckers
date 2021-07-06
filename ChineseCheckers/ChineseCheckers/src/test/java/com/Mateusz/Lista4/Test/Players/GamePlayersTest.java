package com.Mateusz.Lista4.Test.Players;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.awt.Color;

import org.junit.Test;

import com.Mateusz.Lista4.Client.Design.Board;
import com.Mateusz.Lista4.Server.Game;
import com.Mateusz.Lista4.Server.Game.Player;

public class GamePlayersTest {
	Board board = new Board(3);
	Game game = new Game(3);
	Player player = game.new Player(0);

	@Test
	public void WhiteField() {
		Color expected = Color.WHITE;
		assertEquals(expected, board.getColorElement(20));
	}

	@Test
	public void ColorField() {
		Color expected = Color.RED;
		assertEquals(expected, board.getColorElement(100));
	}

	@Test
	public void ColorName() {
		String expected = "red";
		String[] s = board.getAllColorsNames();
		game.setColorName(s);
		assertEquals(expected, game.getColorName(1));
	}

	@Test
	public void Color() {
		Color expected = Color.green;
		String[] s = board.getAllColorsNames();
		game.setColorName(s);
		assertEquals(expected, game.getColor(2));
	}

	@Test
	public void FieldID() {
		int expected = 0;
		assertEquals(expected, game.getFieldIDDueToXY(20, 450));
	}

	@Test
	public void WinZoneTrue() {
		boolean expected = true;
		player.setWinZone();
		assertEquals(expected, player.isInWinZone(111));
	}

	@Test
	public void WinZoneFalse() {
		boolean expected = false;
		player.setWinZone();
		assertEquals(expected, player.isInWinZone(110));
	}
}
