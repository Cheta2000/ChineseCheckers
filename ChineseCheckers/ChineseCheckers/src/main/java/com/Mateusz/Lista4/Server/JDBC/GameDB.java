package com.Mateusz.Lista4.Server.JDBC;

import java.util.List;

import javax.sql.DataSource;

/**
 * interfejs z metodami oblusgujacymi bd
 */
public interface GameDB {

	/**
	 * inicjalizajca bazy danych
	 */
	public void setDataSource(DataSource ds);

	/**
	 * tworzenie tabeli dla nowej gry
	 * 
	 * @param name nazwa tabeli
	 */
	public void createTable(String name);

	/**
	 * tworzenie rekordu w bazie
	 * 
	 * @param color      kolor gracza
	 * @param indexTouch pole z ktorego rusza sie gracz
	 * @param indexMove  pole na ktore rusza sie gracz
	 */
	public void insert(String table, String color, int indexTouch, int indexMove);

	/**
	 * metoda zwraca ruchy w danej grze
	 */
	public List<Move> getMoves(String table);

	/**
	 * metoda zwraca liczbe graczy w danej grze
	 */
	public int getPlayers(String table);

	/**
	 * metoda zwraca nazwy tabel z 10 ostatnimi grami
	 */

	public List<String> getGames();

	/**
	 * metoda sprawdza czy istnieje dana tabela
	 */
	public Boolean tableExists(String table);

}
