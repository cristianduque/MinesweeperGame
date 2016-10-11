import java.awt.Color;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MyPanel extends JPanel {
	private static final long serialVersionUID = 3426940946811133635L;
	private static final int GRID_X = 25;
	private static final int GRID_Y = 25;
	private static final int INNER_CELL_SIZE = 29;
	private static final int TOTAL_COLUMNS = 9;
	private static final int TOTAL_ROWS = 9;   //Last row has only one cell
	private static final int AMOUNT_OF_MINES = 10;
	public int x = -1;
	public int y = -1;
	public int mouseDownGridX = 0;
	public int mouseDownGridY = 0;
	public int amountOfNearMines = 0;
	
	private Random random;
	
	private BufferedImage image;
	
	public Color[][] cells = new Color[TOTAL_COLUMNS][TOTAL_ROWS];
	public int [][] bombs = new int [TOTAL_COLUMNS][TOTAL_ROWS];
	public int [][] neighbors = new int[TOTAL_COLUMNS][TOTAL_ROWS];
	
	public MyPanel() {   //This is the constructor... this code runs first to initialize
		
		if (INNER_CELL_SIZE + (new Random()).nextInt(1) < 1) {	//Use of "random" to prevent unwanted Eclipse warning
			throw new RuntimeException("INNER_CELL_SIZE must be positive!");
		}
		if (TOTAL_COLUMNS + (new Random()).nextInt(1) < 2) {	//Use of "random" to prevent unwanted Eclipse warning
			throw new RuntimeException("TOTAL_COLUMNS must be at least 2!");
		}
		if (TOTAL_ROWS + (new Random()).nextInt(1) < 3) {	//Use of "random" to prevent unwanted Eclipse warning
			throw new RuntimeException("TOTAL_ROWS must be at least 3!");
		}

		for (int x = 0; x < TOTAL_COLUMNS; x++) {   //The rest of the grid
			for (int y = 0; y < TOTAL_ROWS; y++) {
				cells[x][y] = Color.WHITE;
			}
		}
		
		plantMines();
		//coverGrids();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		//Compute interior coordinates
		Insets myInsets = getInsets();
		int x1 = myInsets.left;
		int y1 = myInsets.top;
		int x2 = getWidth() - myInsets.right - 1;
		int y2 = getHeight() - myInsets.bottom - 1;
		int width = x2 - x1;
		int height = y2 - y1;

		//Paint the background
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(x1, y1, width + 1, height + 1);

		//Draw the grid minus the bottom row (which has only one cell)
		//By default, the grid will be 10x10 (see above: TOTAL_COLUMNS and TOTAL_ROWS) 
		g.setColor(Color.BLACK);
		for (int y = 0; y <= TOTAL_ROWS ; y++) {
			g.drawLine(x1 + GRID_X, y1 + GRID_Y + (y * (INNER_CELL_SIZE + 1)), x1 + GRID_X + ((INNER_CELL_SIZE + 1) * TOTAL_COLUMNS), y1 + GRID_Y + (y * (INNER_CELL_SIZE + 1)));
		}
		for (int x = 0; x <= TOTAL_COLUMNS; x++) {
			g.drawLine(x1 + GRID_X + (x * (INNER_CELL_SIZE + 1)), y1 + GRID_Y, x1 + GRID_X + (x * (INNER_CELL_SIZE + 1)), y1 + GRID_Y + ((INNER_CELL_SIZE + 1) * (TOTAL_ROWS)));
		}

		//Draw an additional cell at the bottom left
		//g.drawRect(x1 + GRID_X, y1 + GRID_Y + ((INNER_CELL_SIZE + 1) * (TOTAL_ROWS)), INNER_CELL_SIZE + 1, INNER_CELL_SIZE + 1)
		
		int mx,my,gx,gy; 
		
		//Paint cell colors and evaluate near bombs near the cell
		for (int x = 0; x < TOTAL_COLUMNS; x++) {
			for (int y = 0; y < TOTAL_ROWS; y++) {
				if ((x == 0) || (y != TOTAL_ROWS)) {
					Color c = cells[x][y];
					g.setColor(c);
					g.fillRect(x1 + GRID_X + (x * (INNER_CELL_SIZE + 1)) + 1, y1 + GRID_Y + (y * (INNER_CELL_SIZE + 1)) + 1, INNER_CELL_SIZE, INNER_CELL_SIZE);
					
				}
			}
		}
	}
	public int getGridX(int x, int y) {
		Insets myInsets = getInsets();
		int x1 = myInsets.left;
		int y1 = myInsets.top;
		x = x - x1 - GRID_X;
		y = y - y1 - GRID_Y;
		if (x < 0) {   //To the left of the grid
			return -1;
		}
		if (y < 0) {   //Above the grid
			return -1;
		}
		if ((x % (INNER_CELL_SIZE + 1) == 0) || (y % (INNER_CELL_SIZE + 1) == 0)) {   //Coordinate is at an edge; not inside a cell
			return -1;
		}
		x = x / (INNER_CELL_SIZE + 1);
		y = y / (INNER_CELL_SIZE + 1);
		if (x == 0 && y == TOTAL_ROWS - 1) {    //The lower left extra cell
			return x;
		}
		if (x < 0 || x > TOTAL_COLUMNS - 1 || y < 0 || y > TOTAL_ROWS - 1) {   //Outside the rest of the grid
			return -1;
		}
		return x;
	}
	public int getGridY(int x, int y) {
		Insets myInsets = getInsets();
		int x1 = myInsets.left;
		int y1 = myInsets.top;
		x = x - x1 - GRID_X;
		y = y - y1 - GRID_Y;
		if (x < 0) {   //To the left of the grid
			return -1;
		}
		if (y < 0) {   //Above the grid
			return -1;
		}
		if ((x % (INNER_CELL_SIZE + 1) == 0) || (y % (INNER_CELL_SIZE + 1) == 0)) {   //Coordinate is at an edge; not inside a cell
			return -1;
		}
		x = x / (INNER_CELL_SIZE + 1);
		y = y / (INNER_CELL_SIZE + 1);
		if (x == 0 && y == TOTAL_ROWS - 1) {    //The lower left extra cell
			return y;
		}
		if (x < 0 || x > TOTAL_COLUMNS - 1 || y < 0 || y > TOTAL_ROWS - 1) {   //Outside the rest of the grid
			return -1;
		}
		return y;
	}
	
	public void plantMines(){
		for(int i = 0; i < AMOUNT_OF_MINES;i++){
			generateMines();
		}
	}
	
	public void generateMines(){
		random = new Random();
		int xDirection = random.nextInt(TOTAL_COLUMNS);
		int yDirection = random.nextInt(TOTAL_ROWS);
		//TODO 
		bombs[xDirection][yDirection] = 1;
		
	}
	
	public void setNumbers(Graphics g){
		int mx, my, gx, gy;
		Insets myInsets = getInsets();
		int x1 = myInsets.left;
		int y1 = myInsets.top;
		for(int x = 0; x < TOTAL_COLUMNS;x++){
			for(int y = 0; y < TOTAL_ROWS; y++){
				amountOfNearMines = 0;
				
				mx = x - 1;
				gx = x + 1;
				my = y - 1;
				gy = y + 1;
				
				if(mx >= 0 && my >= 0 && bombs[mx][my] == 1){
					amountOfNearMines++;
					neighbors[mx][my] = 2;
				}
				if(mx >= 0 && bombs[mx][y] == 1){
					amountOfNearMines++;
					neighbors[mx][y] = 2;
				}
				if(mx >= 0 && gy < TOTAL_ROWS && bombs[mx][gy] == 1){
					amountOfNearMines++;
					neighbors[mx][gy] = 2;
				}
				if(gy < TOTAL_ROWS && bombs[x][gy] == 1){
					amountOfNearMines++;
					neighbors[x][gy] = 2;
				}
				if(gx < TOTAL_COLUMNS && gy < TOTAL_ROWS && bombs[gx][gy] == 1){
					amountOfNearMines++;
					neighbors[gx][gy] = 2;
				}
				if(gx < TOTAL_COLUMNS && bombs[gx][y] == 1){
					amountOfNearMines++;
					neighbors[gx][y] = 2;
				}
				if(gx < TOTAL_COLUMNS && my >= 0 && bombs[gx][my] == 1){
					amountOfNearMines++;
					neighbors[gx][my] = 2;
				}
				if(my >= 0 && bombs[x][my] == 1){
					amountOfNearMines++;
					neighbors[x][my] = 2;
				}
				
				if(amountOfNearMines > 0 && bombs[x][y] != 1){
					g.setColor(Color.BLUE);
					g.drawString("" + amountOfNearMines, x1 + GRID_X + (x * (INNER_CELL_SIZE + 1)) + 12, y1 + GRID_Y + (y * (INNER_CELL_SIZE + 1)) +12);
					
				}	
			}
		}
	}
}