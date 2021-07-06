package com.Mateusz.Lista4.Server.JDBC;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 * klasa mapujaca dane z bazy danych na obiekt
 */
public class MoveMapper implements RowMapper<Move> {
	/**
	 * metoda mapujaca wyniki z bazy na obiekt klasy move
	 */
	public Move mapRow(ResultSet rs, int rowNum) throws SQLException {
		// tworzymy nowy ruch
		Move move = new Move();
		// ustawiamy jego ID,kolor i indexy
		move.setID(rs.getInt("ID"));
		move.setColor(rs.getString("Color"));
		move.setIndexTouch(rs.getInt("Touch"));
		move.setIndexMove(rs.getInt("Move"));

		return move;
	}
}