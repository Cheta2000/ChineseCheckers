package com.Mateusz.Lista4.Server;

import java.net.ServerSocket;

import java.util.concurrent.Executors;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.Mateusz.Lista4.Server.Game.Player;

/**
 * SERWER
 */
public class ChineseCheckersServer {
	// gra
	Game game;

	/**
	 * w konstruktorze ustawiamy serwer i tworzymy watki dla 10 osob
	 */
	public ChineseCheckersServer(int players, Game game) {
		try (var listener = new ServerSocket(10000)) {
			System.out.println("Chinese checkers server is running...");
			if (players < 0 || players > 6 || players == 1 || players == 5) {
				System.out.println("Insert valid number of players");
				return;
			} else {
				var pool = Executors.newFixedThreadPool(10);
				this.game = game;
				for (int i = 0; i < 10; i++) {
					Player player = game.new Player(listener.accept());
					pool.execute(player);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * pobieramy liczbe graczy i tworzymy serwer
	 */
	public static void main(String args[]) throws Exception {
		// tworzymy server za pomoca BeansServer.xml
		ApplicationContext context = new ClassPathXmlApplicationContext("BeansServer.xml");
		ChineseCheckersServer server = (ChineseCheckersServer) context.getBean("chineseServer");

	}
}
