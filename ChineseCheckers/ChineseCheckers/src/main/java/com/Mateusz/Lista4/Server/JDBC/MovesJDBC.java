/**
 * 
 */
package com.Mateusz.Lista4.Server.JDBC;

import javax.sql.DataSource;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * klasa obslugujaca baze danych
 */
public class MovesJDBC implements GameDB {

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplateObject;

	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplateObject = new JdbcTemplate(dataSource);
	}

	@Override
	public void createTable(String table) {
		// tworzymy tabele o nazwie table z polami ID, Color, Touch, Move
		String SQL = "CREATE TABLE `" + table
				+ "`(`ID` INT NOT NULL AUTO_INCREMENT PRIMARY KEY, `Color` VARCHAR(10), `Touch` INT,`Move` INT)";
		jdbcTemplateObject.update(SQL);
	}

	@Override
	public void insert(String table, String color, int indexTouch, int indexMove) {
		// dodajemy to tabeli ruch: color,indexTouch, indexMove, ID jest
		// autoinkrementowane
		String SQL = "INSERT INTO `" + table + "`(`Color`,`Touch`,`Move`) VALUES(?,?,?);";
		jdbcTemplateObject.update(SQL, color, indexTouch, indexMove);
	}

	@Override
	public List<Move> getMoves(String table) {
		// wybieramy wszytskie ruchy z tabeli table
		String SQL = "SELECT * FROM `" + table + "`";
		List<Move> move = jdbcTemplateObject.query(SQL, new MoveMapper());
		return move;
	}

	public int getPlayers(String table) {
		// liczymy rozne kolory aby wiedziec ilu gralo graczy
		String SQL = "SELECT COUNT(DISTINCT Color) FROM `" + table + "`";
		int players = jdbcTemplateObject.queryForObject(SQL, Integer.class);
		return players;
	}

	@Override
	public List<String> getGames() {
		// wybieramy 10 ostatnich tabel gier
		String SQL = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='ChineseCheckers' ORDER BY TABLE_NAME DESC LIMIT 10";
		List<String> tableName = jdbcTemplateObject.queryForList(SQL, String.class);
		return tableName;
	}

	@Override
	public Boolean tableExists(String table) {
		// liczymy ile jest tabel o podanej nazwie, jesli jakies sa zwracamy true, else
		// false
		String SQL = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME='" + table + "'";
		int exists = jdbcTemplateObject.queryForObject(SQL, Integer.class);
		if (exists > 0) {
			return true;
		} else {
			return false;
		}
	}
}
