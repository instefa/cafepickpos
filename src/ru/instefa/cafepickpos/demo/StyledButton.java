package ru.instefa.cafepickpos.demo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import ru.instefa.cafepickpos.swing.PosButton;

public class StyledButton extends PosButton implements MouseListener, MouseMotionListener {
	public static final int TYPE_ROUNDED_RECTANGLE = 1;
	public static final int RECTANGLE = 2;

	private int shape;
	private boolean buttonSelected;
	private Color selectedBackgoundColor;
	private Color selectedForegroundColor;
	private Color originalBackground;
	private Color originalForeground;
	private Color selectedBorderColor;
	private int gapTop = 2;
	private int gapLeft = 5;
	private int arcWidth = 10;
	private int arcHeight = 10;
	private boolean cellButton = true;

	public StyledButton(String text) {
		this(text, TYPE_ROUNDED_RECTANGLE, Color.white, Color.black);
		selectedBackgoundColor = new Color(203, 33, 55);
		selectedForegroundColor = Color.WHITE;
		setCellButton(false);
		setGap(2, 2);
	}

	public StyledButton(String text, int shape, Color bg, Color fg) {
		super(text);
		this.shape = shape;
		this.originalBackground = bg;
		originalForeground = fg;
		setOpaque(false);
		setFocusPainted(false);
		setContentAreaFilled(false);
		setFocusable(false);
		setBackground(bg);
		setForeground(fg);
		setBorderPainted(false);
		addMouseListener(this);
	}

	public void setCellButton(boolean b) {
		this.cellButton = b;
	}

	public void setGap(int gapTop, int gapLeft) {
		this.gapTop = gapTop;
		this.gapLeft = gapLeft;
	}

	public void setRoundCorner(int arcWidth, int arcHeight) {
		this.arcWidth = arcWidth;
		this.arcHeight = arcHeight;
	}

	public void setSelectedBackground(Color selectedBackground) {
		this.selectedBackgoundColor = selectedBackground;
	}

	public void setSelectedBForeground(Color foregroundColor) {
		this.selectedForegroundColor = foregroundColor;
	}

	public StyledButton() {
		super();
	}

	public void setSelected(boolean b) {
		this.buttonSelected = b;
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (selectedBorderColor != null) {
			g.setColor(selectedBorderColor);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		if (buttonSelected) {
			g.setColor(selectedBackgoundColor);
			if (shape == TYPE_ROUNDED_RECTANGLE)
				g.fillRoundRect(gapLeft, gapTop, getWidth() - gapLeft * 2, getHeight() - gapTop * 2, arcWidth, arcHeight);
			else
				g.fillRect(0, 0, getWidth(), getHeight());

			setForeground(Color.WHITE);
		}
		else {
			g.setColor(originalBackground);
			if (shape == TYPE_ROUNDED_RECTANGLE)
				g.fillRoundRect(gapLeft, gapTop, getWidth() - gapLeft * 2, getHeight() - gapTop * 2, arcWidth, arcHeight);
			else
				g.fillRect(0, 0, getWidth(), getHeight());
			setForeground(originalForeground);
		}
		super.paintComponent(g);
	}

	public void setSelectedBorderColor(Color color) {
		this.selectedBorderColor = color;
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!cellButton)
			setSelected(true);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!cellButton) {
			setSelected(false);
			repaint();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

}
