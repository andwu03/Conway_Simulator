//package LifeDemo;

//import
import java.awt.*;
import javax.swing.*;
import java.awt.event.*; // Needed for ActionListener
import java.io.File;
import java.io.PrintWriter;
import java.util.Random;
import javax.swing.event.*; // Needed for ActionListener
import java.util.Scanner;

class Conway_Simulator extends JFrame implements ActionListener, ChangeListener {

	// variables
	static Colony colony;
	static JSlider speedSldr = new JSlider();
	static Timer t;
	private JComboBox life = new JComboBox();
	private JComboBox xStart = new JComboBox();
	private JComboBox yStart = new JComboBox();
	private JComboBox length = new JComboBox();
	private JComboBox width = new JComboBox();

	// ======================================================== constructor
	public Conway_Simulator() {
		// 1... Create/initialize components
		colony = new Colony(this, 0.1);
        speedSldr.addChangeListener(this);

		// Buttons for various ac
		JButton simulateBtn = new JButton("Start Simulation");
		simulateBtn.addActionListener(this);

		JButton stopBtn = new JButton("Stop");
		stopBtn.addActionListener(this);

		JButton restartBtn = new JButton("Restart");
		restartBtn.addActionListener(this);

		JButton clearBtn = new JButton("Clear");
		clearBtn.addActionListener(this);

		JButton populateEradicateBtn = new JButton("P or E");
		populateEradicateBtn.addActionListener(this);

		JButton saveBtn = new JButton("Save");
		saveBtn.addActionListener(this);

		JButton loadBtn = new JButton("Load");
		loadBtn.addActionListener(this);

		// 2... Create content pane, set layout
		JPanel content = new JPanel(); // Create a content pane
		content.setLayout(new BorderLayout()); // Use BorderLayout for panel
		JPanel north = new JPanel();
		north.setLayout(new FlowLayout()); // Use FlowLayout for input area
		JPanel west = new JPanel();
		west.setLayout(new FlowLayout());

		DrawArea board = new DrawArea(500, 500);

		// 3... Add the components to the input area.

		// Adding buttons
		north.add(simulateBtn);
		north.add(stopBtn);
		north.add(restartBtn);
		north.add(clearBtn);
		north.add(speedSldr);

		north.add(populateEradicateBtn);
		north.add(life);
		life.addItem("Populate");
		life.addItem("Eradicate");
		north.add(xStart);
		for (int k = 0; k < 100; k++) { // add options for xStart
			xStart.addItem(k);
		}
		north.add(yStart);
		for (int k = 0; k < 100; k++) { // add options for yStart
			yStart.addItem(k);
		}
		north.add(length);
		for (int k = 0; k <= 100; k++) { // add options for length
			length.addItem(k);
		}
		north.add(width);
		for (int k = 0; k <= 100; k++) { // add options for width
			width.addItem(k);
		}
		west.add(saveBtn);
		west.add(loadBtn);

		content.add(north, "North"); // Input area
		content.add(west, "West"); // Save and Load Button
		content.add(board, "South"); // Output area

		// 4... Set this window's attributes.
		setContentPane(content);
		pack();
		setTitle("Life Simulation Demo");
		setSize(1200, 700);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null); // Center window.
	}

	public void stateChanged(ChangeEvent e) {
		if (t != null)
			t.setDelay(400 - 4 * speedSldr.getValue()); // 0 to 400 ms
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Start Simulation")) {
			Movement moveColony = new Movement(colony); // ActionListener
			t = new Timer(200, moveColony); // set up timer
			t.start(); // start simulation
		}
		if (e.getActionCommand().equals("Stop")) {
			t.stop(); // stop simulation
		}
		if (e.getActionCommand().equals("Restart")) {
			colony = new Colony(this, 0.1); // reset to inital condition
		}
		if (e.getActionCommand().equals("Clear")) {
			colony.clear(); // clears the field
		}
		if (e.getActionCommand().equals("P or E")) {
			colony.populateEradicate((String) life.getSelectedItem(), xStart.getSelectedIndex(),
					yStart.getSelectedIndex(), length.getSelectedIndex(), width.getSelectedIndex()); // populate or
																										// eradicate
		}
		if (e.getActionCommand().equals("Save")) {
			colony.save();// save
		}
		if (e.getActionCommand().equals("Load")) {
			colony.load();// load
		}
		repaint();
	}

	class DrawArea extends JPanel {
		public DrawArea(int width, int height) {
			this.setPreferredSize(new Dimension(width, height)); // size
		}

		public void paintComponent(Graphics g) {
			colony.show(g);
		}
	}

	class Movement implements ActionListener {
		private Colony colony;

		public Movement(Colony col) {
			colony = col;
		}

		public void actionPerformed(ActionEvent event) {
			colony.advance();
			repaint();
		}
	}

	// ======================================================== method main
	public static void main(String[] args) {
		Conway_Simulator window = new Conway_Simulator();
		window.setVisible(true);
	}
}

class Colony {
	private boolean grid[][];
	JFrame frame;

	public Colony(JFrame frame, double density) {
		this.frame = frame;
		grid = new boolean[100][100];
		for (int row = 0; row < grid.length; row++)
			for (int col = 0; col < grid[0].length; col++)
				grid[row][col] = Math.random() < density;
	}

	public void clear() {// clears the field
		for (int k = 0; k < grid.length; k++) {
			for (int i = 0; i < grid[0].length; i++) {
				grid[k][i] = false;// eradicates everything
			}
		}
	}

	public void show(Graphics g) {
		for (int row = 0; row < grid.length; row++)
			for (int col = 0; col < grid[0].length; col++) {
				if (grid[row][col]) // life
					g.setColor(Color.black);
				else
					g.setColor(Color.white);
				g.fillRect(col * 5 + 2, row * 5 + 2, 5, 5); // draw life form
			}
	}

	public boolean live(int row, int col) {
		boolean status = grid[row][col];// initial status of block
		int alive = 0;// number of neighbors that are alive

		if (row == grid.length - 1 && col == 0) {// corner
			if (grid[row - 1][col])
				alive++;
			if (grid[row - 1][col + 1])
				alive++;
			if (grid[row][col + 1])
				alive++;
			for(int k = row - 1; k <= row; k++) {
				if (grid[k][grid[0].length - 1])
				alive++;
			}
			for(int k = col; k <= col + 1; k++) {
				if (grid[0][k])
				alive++;
			}
			if (grid[0][grid[0].length - 1])
				alive++;
		} else if (row == 0 && col == 0) {// corner
			if (grid[row + 1][col])
				alive++;
			if (grid[row][col + 1])
				alive++;
			if (grid[row + 1][col + 1])
				alive++;
			for(int k = row; k <= row + 1; k++) {
				if (grid[k][grid[0].length - 1])
				alive++;
			}
			for(int k = col; k <= col + 1; k++) {
				if (grid[grid.length - 1][k])
				alive++;
			}
			if (grid[grid.length - 1][grid[0].length - 1])
				alive++;
		} else if (row == grid.length - 1 && col == grid[0].length - 1) {// corner
			if (grid[row - 1][col - 1])
				alive++;
			if (grid[row - 1][col])
				alive++;
			if (grid[row][col - 1])
				alive++;
			for(int k = row - 1; k <= row; k++) {
				if (grid[k][0])
				alive++;
			}
			for(int k = col - 1; k <= col; k++) {
				if (grid[0][k])
				alive++;
			}
			if (grid[0][0])
				alive++;
		} else if (row == 0 && col == grid[0].length - 1) {// corner
			if (grid[row + 1][col])
				alive++;
			if (grid[row][col - 1])
				alive++;
			if (grid[row + 1][col - 1])
				alive++;
			for(int k = row; k <= row + 1; k++) {
				if (grid[k][0])
				alive++;
			}
			for(int k = col - 1; k <= col; k++) {
				if (grid[grid.length - 1][k])
				alive++;
			}
			if (grid[grid.length - 1][0])
				alive++;
		} else if (row == 0) {// row
			for (int k = row; k <= row + 1; k++) {
				for (int i = col - 1; i <= col + 1; i++) {
					if (grid[k][i])
						alive++;
				}
			}
			if (status)
				alive--;// removes a duplicate count
			for (int k = col - 1; k <= col + 1; k++) {
				if (grid[grid.length - 1][k])
					alive++;
			}
		} else if (row == grid.length - 1) {// row
			for (int k = row - 1; k <= row; k++) {
				for (int i = col - 1; i <= col + 1; i++) {
					if (grid[k][i])
						alive++;
				}
			}
			if (status)
				alive--;
			for (int k = col - 1; k <= col + 1; k++) {
				if (grid[0][k])
					alive++;
			}
		} else if (col == 0) {// column
			for (int k = row - 1; k <= row + 1; k++) {
				for (int i = col; i <= col + 1; i++) {
					if (grid[k][i])
						alive++;
				}
			}
			if (status)
				alive--;
			for (int k = row - 1; k <= row + 1; k++) {
				if (grid[k][grid[0].length - 1])
					alive++;
			}
		} else if (col == grid[0].length - 1) {// column
			for (int k = row - 1; k <= row + 1; k++) {
				for (int i = col - 1; i <= col; i++) {
					if (grid[k][i])
						alive++;
				}
			}
			if (status)
				alive--;
			for (int k = row - 1; k <= row + 1; k++) {
				if (grid[k][0])
					alive++;
			}
		} else {// everything else
			for (int k = row - 1; k <= row + 1; k++) {
				for (int i = col - 1; i <= col + 1; i++) {
					if (grid[k][i])
						alive++;
				}
			}
			if (status)
				alive--;
		}

		boolean future = false;
		if (!status) {// based on the rules of Conway's Game of Life
			if (alive == 3)
				future = true;
		} else if (status) {
			if (alive == 2 || alive == 3)
				future = true;
		}
		return future;
	}

	public void advance() {
		boolean nextGen[][] = new boolean[grid.length][grid[0].length]; // create next generation of life forms
		for (int row = 0; row < grid.length; row++)
			for (int col = 0; col < grid[0].length; col++)
				nextGen[row][col] = live(row, col); // determine life/death status
		grid = nextGen; // update life forms
	}

	public void populateEradicate(String life, int x, int y, int length, int width) {

		try {
			boolean choice;
			if (life.equals("Populate")) {
				choice = true;// if user chose populate, will populate
			} else {
				choice = false;// if user chose eradicate, will eradicate
			}

			for (int k = x; k <= x + length - 1; k++) {
				for (int i = y; i <= y + width - 1; i++) {
					Random rand = new Random();
					int successRate = rand.nextInt(100);
					if (successRate < 90) {// 90% chance to happen
						grid[k][i] = choice;// populate or eradicate depending on choice
					}
				}
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(frame,
					"Unable to complete request.\nCheck to see if selected area is within the grid.",
					"User Input Error", JOptionPane.ERROR_MESSAGE); // display error window
		}
	}

	public void load() {
		try {
			File text = new File("Conway's Game of Life.txt");// file name, can be anything
			Scanner sc = new Scanner(text);
			char[] array;// initialize array
			int countrow = 0;// counter for row and col
			int countcol = 0;

			while (sc.hasNextLine()) {// while the next line exists
				array = sc.nextLine().toCharArray();// turn input in a char array
				for (char c : array) {
					if (c == '1') {// 1 means alive
						grid[countrow][countcol] = true;
						countcol++;// loops through the columns
					} else if (c == '0') {// 0 means dead
						grid[countrow][countcol] = false;
						countcol++;
					}
				}
				countcol = 0;// reset column value
				countrow++;// next row
			}

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(frame, "Unable to load file.", "File Not Found", JOptionPane.ERROR_MESSAGE);
			// error message
		}
	}

	public void save() {
		try {
			File text = new File("Conway's Game of Life.txt");// file name, can be anything
			PrintWriter pw = new PrintWriter(text);

			for (int k = 0; k < grid.length; k++) {
				for (int i = 0; i < grid[0].length; i++) {
					if (grid[k][i]) {
						pw.print("1");//print 1 for alive
					} else {
						pw.print("0");//print 0 for dead
					}
				}
				pw.print("\n");// print new line
			}
			pw.close();

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(frame, "Unable to save configuration", "Input Output Error",
					JOptionPane.ERROR_MESSAGE);
			// error message
		}
	}
}
