package com.Mateusz.Lista4.Server;

import java.util.ArrayList;

import com.Mateusz.Lista4.Server.Game.Player;

interface Mediator {
	public void sendMessageExcept(String message, Player p1, Player p2);

	public void sendMessageOnly(String message, Player p1);

	public void addPlayer(Player player);
}

/**
 * mediator klasa posredniczaca wysylaniu wiadomosci do klienta
 */
public class GameMediator implements Mediator {
	// lista graczy
	private ArrayList<Player> playersList;

	public GameMediator() {
		playersList = new ArrayList<>();
	}

	/**
	 * wysylanie wiadomosci do jednego gracza
	 */
	@Override
	public void sendMessageOnly(String message, Player p1) {
		p1.output.println(message);
	}

	/**
	 * wysylanie wiadomosci do wszytskich oprocz dwoch graczy
	 */
	@Override
	public void sendMessageExcept(String message, Player p1, Player p2) {
		for (Player p : playersList) {
			if (p != p1 && p != p2) {
				p.output.println(message);
			}
		}
	}

	/**
	 * dodawanie gracza do listy
	 */
	@Override
	public void addPlayer(Player player) {
		playersList.add(player);

	}

}
