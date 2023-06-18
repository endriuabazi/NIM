import java.util.Scanner;

public class NimGame {
    static int[] pile;
    static boolean regularNim;
    static String playerName;
    static int[] isMoveValidRange;

    public static void main(String[] args) {

        getGameParameters();
        boolean playerTurn = true;
        while (true) {
            System.out.println(
                    "Current player: \u001B[34m" + (playerTurn ? playerName : "\u001B[31mComputer") + "\u001B[0m");

            printBoard();
            if (isGameOver()) {
                if (playerTurn) {
                    System.out.println("\u001B[31m╔══════════════════╗");
                    System.out.println("║   \u001B[31mYou lost XD!   \u001B[0m║");
                    System.out.println("╚══════════════════╝\u001B[0m");

                } else {
                    System.out.println("\u001B[32m╔══════════════╗");
                    System.out.println("║   \u001B[32mYou won!   \u001B[0m║");
                    System.out.println("╚══════════════╝\u001B[0m");

                }
                break;
            }

            int[] move = new int[2];
            if (playerTurn) {
                move = getPlayerMove();
            } else {
                move = getComputerMove();
                System.out.println("The computer takes " + move[1] + " matches from heap " + (move[0] + 1));
            }

            if (isMoveValid(move)) {
                pile[move[0]] -= move[1];
                playerTurn = !playerTurn;
            } else {
                System.out.println("\u001B[31mInvalid move, please try again.\u001B[0m");

            }
        }
    }

    public static void printBoard() {
        for (int i = 0; i < pile.length; i++) {
            System.out.println("Pile " + (i + 1) + ": " + (pile[i] > 0 ? pile[i] : "Empty"));
        }
    }

    public static boolean isGameOver() {
        int totalMatches = 0;
        for (int p : pile) {
            totalMatches += p;
        }
        if (regularNim) {
            return totalMatches == 0;
        } else {
            if (totalMatches == 1) {
                return true;
            }
            boolean allHeapsEmpty = true;
            boolean onlyOneMatchLeft = false;
            int nonEmptyHeapsCount = 0;
            for (int i = 0; i < pile.length; i++) {
                if (pile[i] > 0) {
                    allHeapsEmpty = false;
                    nonEmptyHeapsCount++;
                    if (pile[i] == 1) {
                        onlyOneMatchLeft = true;
                    } else {
                        onlyOneMatchLeft = false;
                        break;
                    }
                }
            }

            boolean sumIsEven = totalMatches % 2 == 0;
            return allHeapsEmpty || (onlyOneMatchLeft && sumIsEven && nonEmptyHeapsCount % 2 == 0);
        }
    }

    public static boolean isMoveValid(int[] move) {
        int numPiles = pile.length;
        int minVal = 1;
        int maxVal = numPiles;
        if (isMoveValidRange != null) {
            minVal = isMoveValidRange[0];
            maxVal = isMoveValidRange[1];
        }
        if (move[0] >= 0 && move[0] < numPiles && move[1] >= 1 && move[1] <= pile[move[0]] && move[0] + 1 >= minVal
                && move[0] + 1 <= maxVal) {
            return true;
        } else {
            return false;
        }
    }

    public static int[] getPlayerMove() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the pile number (1-3) and the number of matches to take:");
        int heap = scanner.nextInt() - 1;
        int matches = scanner.nextInt();
        return new int[] { heap, matches };
    }

    public static int[] getComputerMove() {
        int[] move = new int[2];

        if (regularNim) {
            // Calculate the nim-sum of all piles
            int nimSum = 0;
            for (int p : pile) {
                nimSum ^= p;
            }

            if (nimSum == 0) {
                // If the nim-sum is zero, perform a random move
                int nonEmptyHeapIndex = -1;
                for (int i = 0; i < pile.length; i++) {
                    if (pile[i] > 0) {
                        nonEmptyHeapIndex = i;
                        break;
                    }
                }

                if (nonEmptyHeapIndex != -1) {
                    move[0] = nonEmptyHeapIndex;
                    move[1] = 1; // Take one match from the non-empty heap
                    return move;
                }
            } else {
                // Perform a winning move based on the nim-sum
                for (int i = 0; i < pile.length; i++) {
                    int xor = pile[i] ^ nimSum;
                    if (xor < pile[i]) {
                        move[0] = i;
                        move[1] = pile[i] - xor;
                        return move;
                    }
                }
            }
        } else {
            // Calculate the total number of matches in the game
            int totalMatches = pile[0] + pile[1] + pile[2];

            if (totalMatches == 1) {
                // If there is only one match left, take it from any non-empty heap
                for (int i = 0; i < pile.length; i++) {
                    if (pile[i] > 0) {
                        move[0] = i;
                        move[1] = 1;
                        return move;
                    }
                }
            } else {
                // Perform a winning move based on the total number of matches
                for (int i = 0; i < pile.length; i++) {
                    for (int j = 1; j <= pile[i]; j++) {
                        int newTotalMatches = totalMatches - j;
                        int xor = pile[i] ^ j ^ newTotalMatches;

                        if (xor == 0) {
                            move[0] = i;
                            move[1] = j;
                            return move;
                        }
                    }
                }
            }
        }

        // If no winning move is found, choose a random move
        for (int i = 0; i < pile.length; i++) {
            if (pile[i] > 0) {
                move[0] = i;
                move[1] = 1; // Take one match from a non-empty heap
                return move;
            }
        }

        return move;
    }

    public static void getGameParameters() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your name: ");
        playerName = scanner.nextLine();

        while (true) {
            System.out.println("Enter the number of piles (3, 4, or 5):");
            int numberOfPiles = scanner.nextInt();

            if (numberOfPiles >= 3 && numberOfPiles <= 5) {
                pile = new int[numberOfPiles];
                break;
            } else {
                System.out.println("Invalid number of piles. Please enter a value between 3 and 5.");
            }
        }

        System.out.println("Enter the range of acceptable values when selecting a pile (1-" + pile.length + "):");
        int minVal = scanner.nextInt();
        int maxVal = scanner.nextInt();

        if (minVal >= 1 && maxVal <= pile.length && minVal <= maxVal) {
            isMoveValidRange = new int[] { minVal, maxVal };
        } else {
            System.out.println("Invalid range of values. Please enter a range between 1-" + pile.length + ".");
        }

        System.out.println("Enter the number of elements in each pile (separated by spaces):");
        for (int i = 0; i < pile.length; i++) {
            pile[i] = scanner.nextInt();
        }

        scanner.nextLine(); // Consume the newline character

        while (true) {
            System.out.println("Enter the game type (Regular or Misere):");
            String gameType = scanner.nextLine().toLowerCase();
            if (gameType.equals("regular") || gameType.equals("misere")) {
                regularNim = gameType.equals("regular");
                break;
            } else {
                System.out.println("Invalid game type. Please enter either 'regular' or 'misere'.");
            }
        }

        while (true) {
            System.out.println("Enter who plays first (user or computer):");
            String firstPlayer = scanner.nextLine().toLowerCase();
            if (firstPlayer.equals("user") || firstPlayer.equals("computer")) {
                boolean playerStarts = firstPlayer.equals("user");
                if (!playerStarts) {
                    // Generate a random move for the computer
                    int heap = (int) (Math.random() * pile.length);
                    int matches = (int) (Math.random() * pile[heap]) + 1;
                    pile[heap] -= matches;
                }
                break;
            } else {
                System.out.println("Invalid player. Please enter either 'user' or 'computer'.");
            }
        }
    }

}
