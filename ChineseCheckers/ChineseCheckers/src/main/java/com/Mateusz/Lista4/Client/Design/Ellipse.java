package com.Mateusz.Lista4.Client.Design;

import java.awt.geom.Ellipse2D;

/**
 * kolo (pole)
 */

public class Ellipse extends Ellipse2D.Double {

	public Ellipse(double x, double y, double w, double h) {
		setFrame(x, y, w, h);
	}

	/*
	 * czy kliknieto pole
	 */
	public boolean isHit(double x, double y) {
		return getBounds2D().contains(x, y);
	}

	/**
	 * pobieranie x
	 */
	public double getX() {
		return x;
	}

	/**
	 * pobieranie y
	 */
	public double getY() {
		return y;
	}

	/**
	 * @param ellipse
	 * @return dystans miedzy dwoma polami
	 */
	public double distance(Ellipse ellipse) {
		double distance = Math.sqrt(((getX() - ellipse.getX()) * (getX() - ellipse.getX()))
				+ ((getY() - ellipse.getY()) * (getY() - ellipse.getY())));
		return distance;
	}
}