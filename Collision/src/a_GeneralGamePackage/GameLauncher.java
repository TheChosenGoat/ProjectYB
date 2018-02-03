package a_GeneralGamePackage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class GameLauncher extends JFrame implements ActionListener {

	private JPanel mainPanel;
	private JButton Collision2DBtn;
	private JButton DodgeTheBallsBtn;

	private final static int xBounds = 400;
	private final static int yBounds = 100;

	public void bootUp() {
		System.out.println("bootup");

		Collision2DBtn = new JButton("Launch Collision 2D");
		Collision2DBtn.setPreferredSize(new Dimension(xBounds/2, 80));
		Collision2DBtn.addActionListener(this);
		Collision2DBtn.setActionCommand("Collision 2D");

		DodgeTheBallsBtn = new JButton("Play Dodge The Balls");
		DodgeTheBallsBtn.setPreferredSize(new Dimension(xBounds/2, yBounds));
		DodgeTheBallsBtn.addActionListener(this);
		DodgeTheBallsBtn.setActionCommand("Dodge The Balls");



		mainPanel = new JPanel();
		mainPanel.setPreferredSize(new Dimension(xBounds, yBounds));
		mainPanel.setBackground(Color.DARK_GRAY);
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(Collision2DBtn,BorderLayout.WEST);
		mainPanel.add(DodgeTheBallsBtn,BorderLayout.CENTER);

		this.setTitle("ITAY GAME LAUNCHER");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.add(mainPanel);
		this.setSize(new Dimension(xBounds, yBounds));
		this.setResizable(false);
		this.setVisible(true);
		






	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GameLauncher gl = new GameLauncher();
		gl.bootUp();


	}

	public void paint(Graphics g) {
		super.paint(g);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if ("Collision 2D".equals(e.getActionCommand())) {
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
			Collision2D.Board.main(null);
		}

		else if ("Dodge The Balls".equals(e.getActionCommand())) {
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
			DodgeTheBalls.Board.main(null);
		}

	}

}
