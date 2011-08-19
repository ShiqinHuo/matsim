/* *********************************************************************** *
 * project: org.matsim.*
 * Controler.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2011 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package playground.sergioo.NetworksMatcher.gui;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Link;

import playground.sergioo.NetworksMatcher.gui.DoubleNetworkWindow.Label;
import playground.sergioo.NetworksMatcher.gui.DoubleNetworkWindow.Option;
import playground.sergioo.Visualizer2D.Layer;
import playground.sergioo.Visualizer2D.LayersPanel;
import playground.sergioo.Visualizer2D.NetworkVisualizer.NetworkPainters.NetworkManager;
import playground.sergioo.Visualizer2D.NetworkVisualizer.NetworkPainters.NetworkPainter;
import playground.sergioo.Visualizer2D.NetworkVisualizer.NetworkPainters.SimpleNetworkPainter;


public class NetworkPanel extends LayersPanel implements MouseListener, MouseMotionListener, KeyListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//Attributes
	private final DoubleNetworkWindow doubleNetworkWindow;
	private int iniX;
	private int iniY;
	private boolean withNetwork = true;
	
	//Methods
	public NetworkPanel(DoubleNetworkWindow doubleNetworkWindow, NetworkPainter networkPainter) {
		super();
		this.doubleNetworkWindow = doubleNetworkWindow;
		layers.add(new Layer(networkPainter));
		this.setBackground(backgroundColor);
		calculateBoundaries();
		super.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize().width,Toolkit.getDefaultToolkit().getScreenSize().height);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		setFocusable(true);
	}
	private void calculateBoundaries() {
		Collection<Coord> coords = new ArrayList<Coord>();
		for(Link link:((NetworkPainter)layers.get(0).getPainter()).getNetworkManager().getNetworkLinks()) {
			if(link!=null) {
				coords.add(link.getFromNode().getCoord());
				coords.add(link.getToNode().getCoord());
			}
		}
		super.calculateBoundaries(coords);
	}
	public String getLabelText(Label label) {
		try {
			return (String) NetworkManager.class.getMethod("refresh"+label.getText(), new Class[0]).invoke(((NetworkPainter)layers.get(0).getPainter()).getNetworkManager(), new Object[0]);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return "";
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		doubleNetworkWindow.setActivePanel(this);
		if(e.getClickCount()==2 && e.getButton()==MouseEvent.BUTTON3)
			camera.centerCamera(getWorldX(e.getX()), getWorldY(e.getY()));
		else {
			if(doubleNetworkWindow.getOption().equals(Option.SELECT_LINK) && e.getButton()==MouseEvent.BUTTON1) {
				((NetworkPainter)layers.get(0).getPainter()).getNetworkManager().selectLink(getWorldX(e.getX()),getWorldY(e.getY()));
				doubleNetworkWindow.refreshLabel(Label.LINK);
			}
			else if(doubleNetworkWindow.getOption().equals(Option.SELECT_LINK) && e.getButton()==MouseEvent.BUTTON3) {
				((NetworkPainter)layers.get(0).getPainter()).getNetworkManager().unselectLink();
				doubleNetworkWindow.refreshLabel(Label.LINK);
			}
			else if(doubleNetworkWindow.getOption().equals(Option.SELECT_NODE) && e.getButton()==MouseEvent.BUTTON1) {
				((NetworkPainter)layers.get(0).getPainter()).getNetworkManager().selectNode(getWorldX(e.getX()),getWorldY(e.getY()));
				doubleNetworkWindow.refreshLabel(Label.NODE);
			}
			else if(doubleNetworkWindow.getOption().equals(Option.SELECT_NODE) && e.getButton()==MouseEvent.BUTTON3) {
				((NetworkPainter)layers.get(0).getPainter()).getNetworkManager().unselectNode();
				doubleNetworkWindow.refreshLabel(Label.NODE);
			}
			else if(doubleNetworkWindow.getOption().equals(Option.ZOOM) && e.getButton()==MouseEvent.BUTTON1) {
				camera.zoomIn(getWorldX(e.getX()), getWorldY(e.getY()));
				doubleNetworkWindow.cameraChange(camera);
			}
			else if(doubleNetworkWindow.getOption().equals(Option.ZOOM) && e.getButton()==MouseEvent.BUTTON3) {
				camera.zoomOut(getWorldX(e.getX()), getWorldY(e.getY()));
				doubleNetworkWindow.cameraChange(camera);
			}
		}
		repaint();
	}
	@Override
	public void mousePressed(MouseEvent e) {
		this.requestFocus();
		doubleNetworkWindow.setActivePanel(this);
		iniX = e.getX();
		iniY = e.getY();
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		doubleNetworkWindow.setActivePanel(this);
		camera.move(getWorldX(e.getX()),getWorldX(iniX),getWorldY(e.getY()),getWorldY(iniY));
		iniX = e.getX();
		iniY = e.getY();
		doubleNetworkWindow.cameraChange(camera);
		repaint();
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		doubleNetworkWindow.setCoords(getWorldX(e.getX()),getWorldY(e.getY()));
	}
	@Override
	public void keyTyped(KeyEvent e) {
		switch(e.getKeyChar()) {
		case 'n':
			withNetwork  = !withNetwork;
			break;
		case 's':
			((SimpleNetworkPainter)layers.get(0).getPainter()).changeSelected();
			break;
		case 'o':
			((NetworkPainter)layers.get(0).getPainter()).getNetworkManager().selectOppositeLink();
			doubleNetworkWindow.refreshLabel(Label.LINK);
			break;
		case 'v':
			viewAll();
			doubleNetworkWindow.cameraChange(camera);
			break;
		case 'm':
			doubleNetworkWindow.setNetworksSeparated();
			break;	
		}
		repaint();
	}
	@Override
	public void keyPressed(KeyEvent e) {
		
	}
	@Override
	public void keyReleased(KeyEvent e) {
		
	}
	
}
