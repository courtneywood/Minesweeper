import java.util.ArrayList;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class Minesweeper extends PApplet {
	int STATE = 0;
	int difficultyLevel = 0;
	PFont f;
	Cell[][] cells = new Cell[10][10];
	boolean update = true;
	Random r = new Random();
	boolean gameover = false;
	
	PImage flag;
	PImage bomb;
	
	public void draw() {
		//will say game over until they click a starting square again
		if (STATE == 0) {
			homeScreen(difficultyLevel);
			
		}
		else if (STATE == 1) {
			if (!gameover) {
				background(255, 255, 255);
				menu();
				board();
			}
			else {
				board();
				//fill(0, 102, 204);
				//rect(200, 400, 200, 100);
				fill(51, 204, 255);
				textFont(f, 50);
				text("You lost!", 110, 400);
				textFont(f, 28);
				text("Click Restart to Play Again!", 30, 450);
			}
		}
	}
	
	public void homeScreen(int difficulty) {
		background(100, 255, 250);
		textFont(f, 50);
		fill(0, 0, 0);
		text("Minesweeper!", 40, 100);
		//easy, medium, hard buttons
		//easy button
		fill(0, 255, 0);
		rect(20, 200, 100, 50);
		//medium button
		fill(255, 255, 0);
		rect(150, 200, 100, 50);
		//hard button
		fill(255, 0, 0);
		rect(280, 200, 100, 50);
		//easy text
		textFont(f, 25);
		if (difficulty == 0) fill(255, 255, 255);
		else fill(0, 0, 0);
		text("Easy", 40, 230);
		//medium text
		if (difficulty == 1) fill(255, 255, 255);
		else fill(0, 0, 0);
		text("Medium", 150, 230);
		if (difficulty == 2) fill(255, 255, 255);
		else fill(0, 0, 0);
		//hard text
		text("Hard", 300, 230);
		
		//play button
		fill(0, 255, 0);
		rect(120, 500, 200, 100);
		//play text
		textFont(f, 72);
		fill(0, 0, 0);
		text("PLAY!", 130, 580);
	}
	
	public void restart() {
		cells = new Cell[10][10];
		setup();
		gameover = false;
	}
	
	public void setup() {
		flag  = loadImage("flag.png");
		bomb = loadImage("bomb.png");
		ArrayList<Integer> mines = new ArrayList<>();
		int count = 0;
		int mineCount = 0;
		if (difficultyLevel == 0) mineCount = 5;
		if (difficultyLevel == 1) mineCount = 10;
		else mineCount = 20;
		for (int i = 0; i < mineCount; i++) {
			mines.add(r.nextInt(100));
		}
		f = createFont("Comic Sans", 50, true);
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				cells[i][j] = new Cell(i, j);
				if (mines.contains(count)) {
					cells[i][j].placeMine();
				}
				count++;
			}
		}
	}
	
	public void settings() {
		size(420, 620);
	}
	
	public static void main(String[] args) {
		PApplet.main("Minesweeper");
	}
	
	public void board() {
		fill(0, 0, 0);
		rect(10, 210, 400, 400);
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (cells[i][j].flagged) {
					cell(i, j, 2);
				}
				else if (cells[i][j].numbered) cell(i, j, 3);
				//else if (cells[i][j].opened) cell(i, j, 1);
				else if (cells[i][j].bomb) {
					cell(i, j, 4);
				}
				else {
					cell(i, j, 0);
				}
			}
		}
	}
	
	public void menu() {
		//restart button
		textFont(f, 14);
		fill(0, 204, 204);
		rect(10, 10, 100, 30);
		fill(0, 0, 0);
		text("Restart", 35, 32);
		//minesweeper logo
		textFont(f, 50);
		fill(0, 0, 0);
		text("Minesweeper!", 40, 100);
		
		//high score
		
		//time
	}
	
	public void cell(int row, int col, int type) {
		//0 - unopened, blue
		//1 - opened, white
		//2 - flagged, red
		//3, numbered, a number
		//4 - bomb
		int xoffset = 10;
		int yoffset = 210;
		if (type == 0) fill(0, 102, 204);
		else if (type == 1 || type == 3) fill(255, 255, 255);
		else if (type == 2) {
			fill(0, 102, 204);
			//image(flag, row*40 + 5 + xoffset, col*40 + 5 + yoffset, 30, 30);
		}
		else if (type == 4) {
			fill(255, 0, 0);
		}
		
		rect(row*40 + 5 + xoffset, col*40 + 5 + yoffset, 30, 30);
		if (type == 2) {
			image(flag, row*40 + 10 + xoffset, col*40 + 10 + yoffset, 22, 22);
		}
		else if (type == 4) {
			image(bomb, row*40 + 10 + xoffset, col*40 + 10 + yoffset, 22, 22);
		}
		else if (type == 3) {
			Cell c = cells[row][col];
			int num = c.num;
			placeNumber(num, row, col);
		}
	}
	
	
	public void mousePressed() {
		if (STATE == 1) {
			//clicked a cell
			if (mouseY > 220 && mouseY <= 600 && mouseX > 10 && mouseX < 410) {
				int[] loc = findCellClicked(mouseX, mouseY);
				Cell clickedCell = cells[loc[0]][loc[1]];
				if (mouseButton == LEFT) {
					clickedCell.open();
					if (clickedCell.hasMine()) {
						gameover = true;
						clickedCell.showBomb();
					}
					else {
						//calculate bombs around
						int num = calculateNumber(loc[0], loc[1]);
						clickedCell.setNum(num);
						if (num == 0) {
							//starting explosion
							explode(clickedCell);
						}
					}
				}
				else if (mouseButton == RIGHT) {
					update = true;
					clickedCell.flag();
				}
				cells[loc[0]][loc[1]] = clickedCell;
			}
			
			else if (mouseY > 10 && mouseY < 30 && mouseX > 10 && mouseX < 100) {
				//restart button
				restart();
			}
		}
		else if (STATE == 0) {
			if (mouseX > 20 && mouseY > 200 && mouseX < 120 && mouseY < 250) {
				difficultyLevel = 0;
			}
			else if (mouseX > 150 && mouseY > 200 && mouseX < 250 && mouseY < 250) {
				difficultyLevel = 1;
			}
			else if (mouseX > 280 && mouseY > 200 && mouseX < 380 && mouseY < 250) {
				difficultyLevel = 2;
			}
			
			else if (mouseX > 120 && mouseY > 500 && mouseX < 320 && mouseY < 600) {
				STATE = 1;
				setup();
			}
		}
	}
	
	public void explode(Cell clickedCell) {
		int x = clickedCell.x;
		int y = clickedCell.y;
		//calculate numbers for all the ones around, if it's 0, open and call explode
		Cell[] neighbors = getNeighbors(clickedCell);
		for (int i = 0; i < neighbors.length; i++) {
			int num = calculateNumber(neighbors[i].x, neighbors[i].y);
			neighbors[i].setNum(num);
			int x1 = neighbors[i].x;
			int y1 = neighbors[i].y;
			cells[x1][y1] = neighbors[i];
			/*
			if (num == 0) {
				//if there are no bombs around it, explode its neighbors
				explode(cells[neighbors[i].x][neighbors[i].y]);
			}
			else {
				//if there are bombs around it, show the number
				neighbors[i].setNum(num);
				int x1 = neighbors[i].x;
				int y1 = neighbors[i].y;
				cells[x1][y1] = neighbors[i];
			}
			*/
		}
	}
	
	public Cell[] getNeighbors(Cell c) {
		ArrayList<Cell> neighbors = new ArrayList<>();
		int x = c.x;
		int y = c.y;
		if (x != 0) {
			neighbors.add(cells[x - 1][y]);
			
			if (y != 0) {
				//left upwards diagonal
				neighbors.add(cells[x - 1][y - 1]);
			}
		}
		
		
		if (x != 9) {
			neighbors.add(cells[x + 1][y]);
			//bottom right diagonal
			if (y != 9) {
				neighbors.add(cells[x + 1][y + 1]);
			}
		}
		
		
		if (y != 0) {
			neighbors.add(cells[x][y - 1]);
			if (x != 9) {
				//right upwards diagonal
				neighbors.add(cells[x + 1][y - 1]);
			}
		}
		
		
		if (y != 9) {
			neighbors.add(cells[x][y + 1]);
			//bottom left diagonal
			if (x != 0) {
				neighbors.add(cells[x - 1][y + 1]);
			}
			
		}
		Cell neighborsArray[] = new Cell[neighbors.size()];
		for (int i = 0; i < neighbors.size(); i++) {
			neighborsArray[i] = neighbors.get(i);
		}
		return neighborsArray;
	}
	
	public int calculateNumber(int x, int y) {
		int bombCount = 0;
		if (x != 0) {
			if (cells[x - 1][y].hasMine()) {
				bombCount++;
			}
			
			if (y != 0) {
				//left upwards diagonal
				if (cells[x - 1][y - 1].hasMine()) {
					bombCount++;
				}
			}
		}
		
		
		if (x != 9) {
			if (cells[x + 1][y].hasMine()) {
				bombCount++;
			}
			//bottom right diagonal
			if (y != 9) {
				if (cells[x + 1][y + 1].hasMine()) {
					bombCount++;
				}
			}
		}
		
		
		if (y != 0) {
			if (cells[x][y - 1].hasMine()) {
				bombCount++;
			}
			if (x != 9) {
				//right upwards diagonal
				if (cells[x + 1][y - 1].hasMine()) {
					bombCount++;
				}
			}
		}
		
		
		if (y != 9) {
			if (cells[x][y + 1].hasMine()) {
				bombCount++;
			}
			//bottom left diagonal
			if (x != 0) {
				if (cells[x - 1][y + 1].hasMine()) {
					bombCount++;
				}
			}
			
		}
		
		return bombCount;
	}
	
	public void placeNumber(int number, int x, int y) {
		int roughX = x * 40;
		int roughY = y * 40;
		int xoffset = 25;
		int yoffset = 235;
		y = y + 200;
		textFont(f, 18);
		fill(0, 0, 0);		
		text("" + number, roughX + 3 + xoffset, roughY + 3 + yoffset);
	}
	
	public int[] findCellClicked(int x, int y) {
		int[] loc = new int[2];
		y = y - 200;
		int roughX = x / 40;
		int roughY = y / 40;
		if (roughX == 10) roughX = 9;
		if (roughY == 10) roughY = 9;
		loc[0] = roughX;
		loc[1] = roughY;
		return loc;
	}
	
	public class Cell {
		int x;
		int y;
		boolean mine = false;
		boolean opened = false;
		boolean flagged = false;
		boolean numbered = false;
		boolean bomb = false;
		int num;
		public Cell(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public void placeMine() {
			mine = true;
		}
		
		public boolean hasMine() {
			return mine;
		}
		
		public void open() {
			opened = true;
			cell(x, y, 1);
		}
		
		public void flag() {
			if (flagged) {
				flagged = false;
			}
			else flagged = true;
		}
		
		public void setNum(int n){
			num = n;
			numbered = true;
		}
		
		public void showBomb() {
			bomb = true;
			flagged = false;
			numbered = false;
		}
	}
}

