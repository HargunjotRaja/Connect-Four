

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

enum Colour {
    empty(0),
    red(1),
    yellow(2);

    private byte value;

    Colour(int value) {
        this.setValue((byte) value);
    }

    public char code() {
        switch (this) {
            case red:
                return 'R';

            case yellow:
                return 'Y';

            default:
                return ' ';
        }
    }

	public byte getValue() {
		return value;
	}

	public void setValue(byte value) {
		this.value = value;
	}
}

public class ConnectFourGame {

    private static final byte rows = 6;
    private static final byte columns = 7;
    private static final byte directions[][] = {
            {-1, 1},
            {0, 1},
            {1, 1},
            {1, 0}
    };

    private Colour[][] board;
    private List<byte[]> possibleDirections[][];
    private Scanner scanner;

    public ConnectFourGame(Scanner scanner) {
        this.scanner = scanner;
    }

    @SuppressWarnings("unchecked")
	public void init() {
        board = new Colour[rows][columns];
        possibleDirections = new List[rows][columns];

        for (byte r = 0; r < rows; r++) {
            for (byte c = 0; c < columns; c++) {
                board[r][c] = Colour.empty;
                possibleDirections[r][c] = new ArrayList<byte[]>();

                for (byte i = 0; i < directions.length; i++) {
                    if (c + 3 * directions[i][0] >= 0 &&
                            c + 3 * directions[i][0] < columns &&
                            r + 3 * directions[i][1] >= 0 &&
                            r + 3 * directions[i][1] < rows) {
                        possibleDirections[r][c].add(directions[i]);
                    }
                }
            }
        }
    }

    public void gameBoard() {
        System.out.println("-------------------------------------------");
        for (byte r = rows - 1; r >= 0; r--) {
            System.out.print("|");
            for (byte c = 0; c < columns; c++) {
                System.out.print(board[r][c]);
                System.out.print("|");
            }
            System.out.println();
            System.out.println("-------------------------------------------");
        }
    }

    public void play() {
        byte turn = 0;
        boolean running = true;
        Random random = new Random();

        while (turn < rows * columns && running) {
            Colour currentPlayerColour = (turn & 1) == 1 ? Colour.red : Colour.yellow;

            if (currentPlayerColour == Colour.red) {
                // User's turn
                byte col = getUserInput();
                if (insert(currentPlayerColour, (byte) (col - 1))) {
                    gameBoard();
                    if (isWinner(currentPlayerColour)) {
                        System.out.println("Congratulations! You won!");
                        running = false;
                        break;
                    }
                    turn++;
                } else {
                    System.out.println("Invalid move. Please try again.");
                }
            } else {
                // Computer's turn
                byte col;
                do {
                    col = (byte) (random.nextInt(columns));
                } while (!insert(currentPlayerColour, col));
                System.out.println("Computer played in column " + (col + 1));
                
                System.out.println("    ");
                gameBoard();
                if (isWinner(currentPlayerColour)) {
                    System.out.println("You lost :( ");
                    running = false;
                    break;
                }
                turn++;
            }
        }

        if (running) {
            System.out.println("It's a tie!");
        }
    }

    public boolean insert(Colour colour, byte col) {
        if (col < 0 || col >= columns) {
            return false;
        }

        byte r;
        for (r = (byte) (rows - 1); r >= 0; r--) {
            if (board[r][col] != Colour.empty) {
                r++;
                break;
            }
        }

        if (r == rows) {
            return false;
        } else if (r == -1) {
            r++;
        }

        board[r][col] = colour;
        return true;
    }

    private byte countConsecutive(byte r, byte c, byte[] dircs, Colour colour) {
        if (r < 0 || r >= rows || c < 0 || c >= columns) {
            return 0;
        }
        if (board[r][c] != colour) {
            return 0;
        }
        return (byte) (1 + countConsecutive((byte) (r + dircs[1]), (byte) (c + dircs[0]), dircs, colour));
    }

    public boolean isWinner(Colour colour) {
        for (byte r = 0; r < rows; r++) {
            for (byte c = 0; c < columns; c++) {
                if (board[r][c] == colour) {
                    for (byte i = 0; i < possibleDirections[r][c].size(); i++) {
                        if (countConsecutive(r, c, possibleDirections[r][c].get(i), colour) >= 4) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private byte getUserInput() {
        byte col;
        while (true) {
            System.out.print("\nPlayer Red, choose a column from 1 to 7: ");
            String input = scanner.next();
            try {
                col = Byte.parseByte(input);
                if (col >= 1 && col <= columns) {
                    return col;
                } else {
                    System.out.println("Column you place the dice in is wrong. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ConnectFourGame connectfourgame = new ConnectFourGame(scanner);
        connectfourgame.init();
        connectfourgame.play();
        
    }
}
