import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

public class Main {
	private static int SIDE;
	private static int LEVEL;
	private static int MIN_SIDE = 1;
	private static int MAX_SIDE = 30;
	private static int MIN_LEVEL = 1;
	private static int MAX_LEVEL = 10;
	private static String SEPARATOR = " ";
	private static char FIELD = '#';
	private static char MINE = 'x';
	private static byte OK = 0;
	private static byte WIN = -1;
	private static byte LOSE = -2;
	private static byte PRESSED = -3;


	private static byte minesFiled[][];
	private static char gameField[][];

	private static void fillMinesField(){
		for(byte i=0; i<SIDE; i++)
			for(byte j=0; j<SIDE; j++)
				if( random(0, LEVEL) < 1 )
					minesFiled[i][j] = 0;
				else
					minesFiled[i][j] = 1;
	}
	private static void fillGameField(){
		for(byte i=0; i<SIDE; i++)
			for(byte j=0; j<SIDE; j++)
				gameField[i][j] = FIELD;
	}
	private static void printGameField(){
		System.out.print("  ");
		for (byte i=0; i<SIDE; i++)
			System.out.print( (i < 10 ? SEPARATOR : "") + i + SEPARATOR);
		
		System.out.println();
		
		for(byte i=0; i<SIDE; i++){
			System.out.print(i + (i < 10 ? SEPARATOR + " " : SEPARATOR));
			
			for(byte j=0; j<SIDE; j++){
				System.out.print(gameField[i][j] + SEPARATOR + " ");
			}
			System.out.println();
		}
	}
	

	private static boolean isWinner(){
		for(int i=0; i < SIDE; i++)
			for(int j=0; j < SIDE; j++)
				if(minesFiled[i][j] == 0)
					continue;
				else if(gameField[i][j] == FIELD)
					return false;
		return true;
	}

	private static int random(int start, int end){
		return (int)( start + Math.random() * (end - start) );
	}

	private static int neigbours(int row, int col, int field){
		int count = 0;
		if(minesFiled[row][col] == 0)
			return LOSE;
		else if (gameField[row][col] != FIELD)
			return PRESSED;
		
		for (int i = row - 1; i <= row + 1; i++)
			for(int j = col - 1; j <= col + 1; j++)
				try{
					if (i == row && j == col)
						continue;
					else if (minesFiled[i][j] == field)
						count ++;
				}catch(Exception e){
					continue;
				}

			return count;
	}

	private static List<List<Integer>> neigboursList(int row, int col, int field){
		List<List<Integer>> emptysList = new ArrayList<>();
		
		for (int i=row - 1; i <= row + 1; i++)
			for(int j=col - 1; j <= col + 1; j++)
				try{
					if (i == row && j == col)
						continue;
					else if (minesFiled[i][j] == field){
						List<Integer> indexes = new ArrayList<>();
						indexes.add(i);
						indexes.add(j);
						emptysList.add(indexes);

					}

				}catch(Exception e){
					continue;
				}
		return emptysList;
	}

	private static void openEmptyNeighbours(int row, int col){
		List<List<Integer>> emptysList = neigboursList(row, col, 1);
		int emptyNeighboursCount = emptysList.size(); // count of empty neighbors

		if(emptyNeighboursCount == 0)
			return;
		
		for(int i = 0; i < emptyNeighboursCount; i++){
			row = emptysList.get(i).get(0);
			col = emptysList.get(i).get(1);
			int count = neigbours(row, col, 0);
			if(count == PRESSED)
				continue;

			gameField[row][col] = Character.forDigit(
				count, 10
				);

			if(gameField[row][col] == '0')
				gameField[row][col] = ' ';

			if(gameField[row][col] == ' ' )
				openEmptyNeighbours(row, col);
		}
	}

	private static byte game(int row, int col){
		int neighboursCount = neigbours(row, col, 0);//count of neighbor Mines
		if(neighboursCount == LOSE)
			return LOSE; // if lose
		if(neighboursCount == PRESSED)
			return PRESSED; // if already pressed

		if(neighboursCount != 0)
			gameField[row][col] = Character.forDigit(neighboursCount, 10);
		else{
			gameField[row][col] = ' ';
			openEmptyNeighbours(row, col);
		}

		if(isWinner())
			return WIN; // if win

		return OK; // all is ok
	}
 	
	private static void cleanScreen() {
		try {
	        if (System.getProperty("os.name").contains("Windows"))
	            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
	        else
	            Runtime.getRuntime().exec("clear");
    	} catch (IOException | InterruptedException ex) {}
	}
	
	private static void setGameFieldMines(){
		for(int i=0; i < SIDE; i++)
			for(int j=0; j < SIDE; j++)
				if(minesFiled[i][j] == 0)
					gameField[i][j] = MINE;
	}

	private static int swap(int c, int a, int b){
        return b-Math.abs(c - a);// a = 2, b = 10, c = 3; result is 9
    }

	private static void playGame(){
		int row, col;
		byte gameState;

		cleanScreen();
		Scanner input = new Scanner(System.in);
		do{
			System.out.print(
				String.format("Game Area Size (%d - %d): ", MIN_SIDE, MAX_SIDE)
				);
			SIDE = input.nextInt();
		}while( SIDE < MIN_SIDE || SIDE > MAX_SIDE );

		do{
			System.out.print(
				String.format("\nGame Complexity Level (%d - %d): ", MIN_LEVEL, MAX_LEVEL)
				);
			LEVEL = input.nextInt();
			LEVEL = swap(LEVEL, MIN_LEVEL, MAX_LEVEL) + 1; // because 1 is invalid, random function from argument 0, 1 always returns 0. And all area fill mines
		}while( LEVEL < MIN_LEVEL || LEVEL > MAX_SIDE );
		

		minesFiled = new byte[SIDE][SIDE];
		gameField = new char[SIDE][SIDE];

		fillMinesField();
		fillGameField();

		do{
			do{
				cleanScreen();
				printGameField();
				System.out.print("\nROW: ");
				row = input.nextInt();
				System.out.print("COLOUMN: ");
				col = input.nextInt();
			}while( 
				row >= SIDE || row < 0 ||
				col >= SIDE || col < 0
				);

			gameState = game(row, col);
			cleanScreen();
			printGameField();

			if(gameState == LOSE){
				cleanScreen();
				setGameFieldMines();
				printGameField();
				System.out.println(	"\n" + 
									row + " : " + col + " is Mine\n" +
									"You lose (((\n" + 
									MINE + " is Mines"
								  );
				break;
			}
			else if(gameState == WIN){
				cleanScreen();
				setGameFieldMines();
				printGameField();
				System.out.println("\nYou Win )))");
				break;
			}
			else if(gameState == PRESSED){
				System.out.print("\nAlready pressed field!!!\nPress Enter to continue");
				new Scanner(System.in).nextLine();
			}
				
		}while(true);
		
	}

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		do{
			playGame();
			System.out.print("\nPlay Again(y,n): ");
		}while(input.nextLine().equals("y"));
	}
	

}
