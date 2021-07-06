package com.Mateusz.Lista4.Test.Moves;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.Mateusz.Lista4.Client.Design.Board;
import com.Mateusz.Lista4.Server.Game;
import com.Mateusz.Lista4.Server.Exception.IllegalMoveException;
import com.Mateusz.Lista4.Server.Game.Player;
import com.Mateusz.Lista4.Server.Game.NormalMoves;

public class MovesTest {
	Game game = new Game(2);
	Player player = game.new Player(0);
	NormalMoves rules = game.new NormalMoves();
	Board board = new Board(2);

	public void setFirst() {
		game.setStartID(0);
		game.insertIntoTable(0, player);
		game.setCurrentPlayer();
		player.setWinZone();
	}

	@Test
	public void currentPlayerStart() {
		int startID = game.getStartID();
		Player expected = game.new Player(startID);
		game.insertIntoTable(startID, expected);
		game.setCurrentPlayer();
		assertEquals(expected, game.getCurrentPlayer());
	}

	@Test
	public void currentPlayerNext() {
		setFirst();
		Player expected = game.new Player(1);
		game.insertIntoTable(1, expected);
		game.goNext();
		assertEquals(expected, game.getCurrentPlayer());
	}

	@Test
	public void ValidMove() throws IllegalMoveException {
		setFirst();
		rules.touch(9, player);
		rules.move(17, player);
		int expected = 17;
		assertEquals(expected, game.getIndexMove());
	}

	@Test
	public void ValidJump() throws IllegalMoveException {
		setFirst();
		rules.touch(3, player);
		rules.move(14, player);
		int expected = 14;
		assertEquals(expected, game.getIndexMove());
	}

	@Test
	public void ValidMultiJump() throws IllegalMoveException {
		setFirst();
		rules.touch(3, player);
		rules.move(14, player);
		game.setIndexTouch(14);
		rules.move(3, player);
		int expected = 3;
		assertEquals(expected, game.getIndexMove());
	}

	@Test(expected = IllegalMoveException.class)
	public void IllegalPlayer() throws IllegalMoveException {
		setFirst();
		Player player1 = game.new Player(1);
		player1.setWinZone();
		rules.move(19, player1);
		rules.touch(19, player1);
	}

	@Test(expected = IllegalMoveException.class)
	public void FieldOccupied() throws IllegalMoveException {
		setFirst();
		rules.touch(9, player);
		rules.move(120, player);
	}

	@Test(expected = IllegalMoveException.class)
	public void NotInRange() throws IllegalMoveException {
		setFirst();
		rules.touch(9, player);
		rules.move(16, player);
	}

	@Test(expected = IllegalMoveException.class)
	public void IllegalMutliJump() throws IllegalMoveException {
		setFirst();
		rules.touch(3, player);
		rules.move(14, player);
		game.setIndexTouch(14);
		rules.move(15, player);
	}

	@Test(expected = IllegalMoveException.class)
	public void IllegalTouch() throws IllegalMoveException {
		setFirst();
		rules.touch(120, player);
	}
}
