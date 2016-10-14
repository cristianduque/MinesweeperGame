import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/*
 * This class receives the mouse events when clicking the grids in the game Minesweeper and uses the methods implemented 
 * of class MyPanel.
 * @author Cristian G. Duque Gonzalez
 * @author Rafael Cruz Candelario
 */
 
public class MyMouseAdapter extends MouseAdapter {
	
	public void mousePressed(MouseEvent e) {
		Component c = e.getComponent();
		while (!(c instanceof JFrame)) {
			c = c.getParent();
			if (c == null) {
				return;
			}
		}
		JFrame myFrame = (JFrame) c;
		MyPanel myPanel = (MyPanel) myFrame.getContentPane().getComponent(0);
		Insets myInsets = myFrame.getInsets();
		int x1 = myInsets.left;
		int y1 = myInsets.top;
		e.translatePoint(-x1, -y1);
		int x = e.getX();
		int y = e.getY();
		myPanel.x = x;
		myPanel.y = y;
		switch (e.getButton()) {
		case 1:		//Left mouse button

			myPanel.mouseDownGridX = myPanel.getGridX(x, y);
			myPanel.mouseDownGridY = myPanel.getGridY(x, y);
			myPanel.repaint();
			break;
		case 3:		//Right mouse button
			//Do nothing
			myPanel.mouseDownGridX = myPanel.getGridX(x, y);
			myPanel.mouseDownGridY = myPanel.getGridY(x, y);
			myPanel.repaint();
			break;
		default:    //Some other button (2 = Middle mouse button, etc.)
			//Do nothing
			break;
		}
	}
	
	
	public void mouseReleased(MouseEvent e) {
		Component c = e.getComponent();
		while (!(c instanceof JFrame)) {
			c = c.getParent();
			if (c == null) {
				return;
			}
		}
		JFrame myFrame = (JFrame)c;
		MyPanel myPanel = (MyPanel) myFrame.getContentPane().getComponent(0);  //Can also loop among components to find MyPanel
		Insets myInsets = myFrame.getInsets();
		int x1 = myInsets.left;
		int y1 = myInsets.top;
		e.translatePoint(-x1, -y1);
		int x = e.getX();
		int y = e.getY();
		myPanel.x = x;
		myPanel.y = y;
		Color uncoveredGrid = Color.WHITE;
		Color flag = Color.RED;
		Color bomb = Color.BLACK;
		Color coveredGrid = Color.lightGray;

		int gridX = myPanel.getGridX(x, y);
		int gridY = myPanel.getGridY(x, y);
		
		switch (e.getButton()){
		case 1:		//Left mouse button
			
			if ((myPanel.mouseDownGridX == -1) || (myPanel.mouseDownGridY == -1)) {
				//Had pressed outside
				//Do nothing
			} else {
				if ((gridX == -1) || (gridY == -1)) {
					//Is releasing outside
					//Do nothing
				} else {
					if ((myPanel.mouseDownGridX != gridX) || (myPanel.mouseDownGridY != gridY)) {
						//Released the mouse button on a different cell where it was pressed
						//Do nothing
					} else {
						//Released the mouse button on the same cell where it was pressed
						if (myPanel.cells[myPanel.mouseDownGridX][myPanel.mouseDownGridY] == flag) {
							//On the left column and on the top row... do nothing
						} 
						else {
							myPanel.cells[myPanel.mouseDownGridX][myPanel.mouseDownGridY] = uncoveredGrid;
							myPanel.cascade(myPanel.mouseDownGridX, myPanel.mouseDownGridY);
							myPanel.repaint();
							myPanel.cells[myPanel.mouseDownGridX][myPanel.mouseDownGridY] = Color.LIGHT_GRAY;
							if(myPanel.winGame()){
								//Checks if the user have win the came 
								JOptionPane.showMessageDialog(null, "YOU WIN!", "Minesweeper", JOptionPane.INFORMATION_MESSAGE);
								System.exit(0);
							}
						}
						
						
						
						if((myPanel.bombs[myPanel.mouseDownGridX][myPanel.mouseDownGridY] == 1) && 
								myPanel.cells[myPanel.mouseDownGridX][myPanel.mouseDownGridY] != flag){
							for (int x5 = 0; x5 < myPanel.getTotalColumns(); x5++){
								for (int y5 = 0; y5 < myPanel.getTotalRows(); y5++){
									if(myPanel.bombs[x5][y5] == 1){
										myPanel.cells[x5][y5] = bomb;
										myPanel.repaint();
									}
								}
							}
							JOptionPane.showMessageDialog(null, "Bomb Clicked!", "Minesweeper", JOptionPane.INFORMATION_MESSAGE);
							System.exit(0);
						}
					}
				}
			}
			
			myPanel.repaint();
			break;
		case 3:		//Right mouse button
			if ((myPanel.mouseDownGridX == -1) || (myPanel.mouseDownGridY == -1)) {
				//Had pressed outside
				//Do nothing
			} else {
				if ((gridX == -1) || (gridY == -1)) {
					//Is releasing outside
					//Do nothing
				} else {
					//Click in the grid
					if((myPanel.mouseDownGridX == gridX) && (myPanel.mouseDownGridY == gridY)){
						//White grid with right click
						if(myPanel.cells[myPanel.mouseDownGridX][myPanel.mouseDownGridY] == coveredGrid){
							//Do nothing
						}
						else {  
							//Grid gray, incorporate the flag
							if(myPanel.cells[myPanel.mouseDownGridX][myPanel.mouseDownGridY] == uncoveredGrid){
								myPanel.cells[myPanel.mouseDownGridX][myPanel.mouseDownGridY] = flag;
								myPanel.repaint();
								
							}
							else{
								//If grid is a flag, remove it
								if(myPanel.cells[myPanel.mouseDownGridX][myPanel.mouseDownGridY] == flag){
									myPanel.cells[myPanel.mouseDownGridX][myPanel.mouseDownGridY] = uncoveredGrid;
									myPanel.repaint();
								}
							}
								
						}
					}
				
				}
			}
			break;
		default:    //Some other button (2 = Middle mouse button, etc.)
			//Do nothing
			break;
		}
	}
}