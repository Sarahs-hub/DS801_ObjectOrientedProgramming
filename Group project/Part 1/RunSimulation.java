import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/*
 * This is the top-level class for the Ant Colony Simulation.
 */
public class RunSimulation {

    // call Scanner for later access
    static Scanner scanner;

    /*
     * This method executes the simulation, allowing for exceptions to be thrown.
     */
    public static void main(String[] args) throws FileNotFoundException {

        // create new instance of Scanner
        scanner = new Scanner(System.in);

        // welcome
        System.out.println("");
        System.out.println("Welcome to the Ant Colony Simulation.");
        System.out.println("");

        // get user inputs
        double sugarProbability = readValue(scanner,
                "What is the probability of sugar spawning in a node? Please enter number: ");
        System.out.println("The sugar probability is " + sugarProbability + ".");
        System.out.println("");

        int avgSugar = (int) readValue(scanner, "What is the average amount of sugar in a node? Please enter number: ");
        System.out.println("The average sugar amount is " + avgSugar + " units.");
        System.out.println("");

        int carriedSugar = (int) readValue(scanner,
                "How many units of sugar can ants carry at a time? Please enter number: ");
        System.out.println("Ants can carry " + carriedSugar + " units of sugar.");

        System.out.println("");

        int droppedPheromones = (int) readValue(scanner, "How many pheromones do ants secrete? Please enter number: ");
        System.out.println("Ants secrete " + droppedPheromones + " pheromones.");
        System.out.println("");

        String graphType = readChoice(scanner,
                "How do you wish to generate the graph? \n"
                        + "- By reading a file storing the graph, press and enter 'A'. \n"
                        + "- By creating your own grid, press and enter 'B'. \n",
                "A", "B");

        boolean isGrid = graphType.equalsIgnoreCase("B");

        // define variables due to scoping
        int colonyAmount;
        String filename = "Filename not found.";

        // manual colony user input
        if (graphType.equalsIgnoreCase("B")) {
            while (true) {
                colonyAmount = (int) readValue(scanner, "Please type the total number of colonies: ");
                if (colonyAmount > 0) {
                    System.out.println("Total number of Ant Colonies is: " + colonyAmount);
                    break;
                }
                System.out.println("The total number of colonies must be at least 1 ");
            }
        }
        // create grid and generate colonies based on file input
        else {
            filename = readUserString(scanner, "Please enter filename. Otherwise, graph1.txt is chosen.", "graph1.txt");

            // read second line from graph file
            Scanner filescanner = new Scanner(new File(filename));
            filescanner.nextLine();
            colonyAmount = filescanner.nextLine().trim().split(" ").length;
            System.out.println("The number of colonies is " + colonyAmount + ".");

        }

        Colony[] colonies = createColonies(colonyAmount);

        String antMode = readChoice(scanner,
                "How do you wish to assign the ants? \n" + "- By range of ants per colony, press and enter 'A'. \n"
                        + "- By specific number of ants per colony, press and enter 'B'. \n",
                "A", "B");
        System.out.println("");

        Ant[] ants;
        // assign ants in intervals
        if (antMode.equalsIgnoreCase("A")) {
            ants = intervalAnts(colonies);
        }
        // assign ants per colony
        else {
            ants = specificAnts(colonies);
        }

        // let user choose mode of viewing information
        String viewMode;
        if (isGrid) {
            viewMode = readChoice(scanner,
                    "Please enter the desired mode of viewing information. \n"
                            + "- For a textual summary, press and enter 'A'. \n"
                            + "- For a graphical representation, press and enter 'B'. \n",
                    "A", "B");
            System.out.println("");
        } else {
            viewMode = "A";
        }

        // declare instance of Graph in proper scope
        Graph graph;

        // read file from disk
        if (graphType.equalsIgnoreCase("A")) {
            System.out.println("The graph is generated by the file " + filename + ".");

            // create new instance of Graph instance
            graph = new Graph(filename, colonies, sugarProbability, avgSugar);

            // specify width and height of new grid based on user input
        } else {
            int[] gridSize = requestGridSize(colonyAmount);
            int width = gridSize[0];
            int depth = gridSize[1];
            graph = new Graph(width, depth, colonies, sugarProbability, avgSugar);
            System.out.println("The dimensions of your grid are " + width + " x " + depth + ".");
        }

        int tickNumber = (int) readValue(scanner, "How many simulations do wish to run? ");
        int textUpdateInterval = 1;

        if (viewMode.equalsIgnoreCase("A")) {
            textUpdateInterval = (int) readValue(scanner, "How often do you wish to receive a text update in ticks? ");
        }

        // create new instance of Simulator
        Simulator simulator = new Simulator(graph, ants, carriedSugar, droppedPheromones);

        // get start node from ant array
        Node startNode = ants[0].current();

        // create new instance of Visualizer
        Visualizer visualizer = new Visualizer(graph, isGrid, startNode, ants);

        if (viewMode.equalsIgnoreCase("B")) {
            visualizer.display();
        }

        // loop through simulation
        int totalTicks = 0;
        while (totalTicks < tickNumber) {
            if (viewMode.equalsIgnoreCase("B")) {
                visualizer.update();
            } else if (totalTicks % textUpdateInterval == 0) {
                visualizer.printStatus();
            }
            simulator.tick();
            totalTicks = totalTicks + 1;
        }

    }

    /*
     * This method converts the Ant 2D array into the Ant 1D array. Argument: Ant 2D
     * array. Returns Ant 1D array.
     */
    public static Ant[] ant2dTo1d(Ant[][] ant2d) {
        int x = 0;
        int sum = 0;
        while (x < ant2d.length) {
            sum = sum + ant2d[x].length;
            x = x + 1;
        }
        Ant[] ant1d = new Ant[sum];
        x = 0;
        int i = 0;
        while (x < ant2d.length) {
            int y = 0;
            while (y < ant2d[x].length) {
                ant1d[i] = ant2d[x][y];
                i = i + 1;
                y = y + 1;
            }
            x = x + 1;
        }
        return ant1d;
    }

    /*
     * This method allows the user to assign ants automatically in a lower and upper
     * range. Argument: Colony 1D array. Returns Ant 1D array in desired interval.
     * In case of wrong input, the user must enter a new argument.
     */
    public static Ant[] intervalAnts(Colony[] colonies) {
        int lowerRange = (int) readValue(scanner, "Please enter lower range: ");
        System.out.println("");
        int upperRange = (int) readValue(scanner, "Please enter upper range: ");
        while (upperRange < lowerRange) {
            System.out.println("Upper range must be larger than or equal to lower range! ");
            upperRange = (int) readValue(scanner, "Please enter upper range: ");
        }
        int n = upperRange - lowerRange;
        int x = 0;
        Ant[][] ants = new Ant[colonies.length][];
        // generate random number to ant array per colony
        while (x < colonies.length) {
            int randomRange = RandomUtils.randomInt(n) + lowerRange;
            Ant[] antsArr = new Ant[randomRange];
            int y = 0;
            // generate ants
            while (y < antsArr.length) {
                Ant ant = new Ant(colonies[x]);
                antsArr[y] = ant;
                y = y + 1;
            }
            ants[x] = antsArr;
            x = x + 1;
        }
        return ant2dTo1d(ants);
    }

    /*
     * This method allows the user to assign ants specifically for each colony.
     * Argument: Colony 1D array. Returns Ant 1D array.
     */
    public static Ant[] specificAnts(Colony[] colonies) {
        System.out.println("Assign ants for each of the " + colonies.length + " colonies:");
        int x = 0;
        Ant[][] ants = new Ant[colonies.length][];
        System.out.println("");
        while (x < colonies.length) {
            int antsAmount = (int) readValue(scanner, "Enter ants for colony: " + (x + 1));
            Ant[] antsArr = new Ant[antsAmount];
            int y = 0;

            while (y < antsArr.length) {
                Ant ant = new Ant(colonies[x]);
                antsArr[y] = ant;
                y = y + 1;

            }
            ants[x] = antsArr;
            x = x + 1;
        }
        return ant2dTo1d(ants);
    }

    /*
     * This method generates colonies and assigns it to the colony array. Argument:
     * int colony amount. Return Colony 1D array.
     */
    public static Colony[] createColonies(int colonyAmount) {
        Colony[] colonies = new Colony[colonyAmount];
        int i = 0;
        while (i < colonies.length) {
            Colony colony = new Colony();
            colonies[i] = colony;
            i = i + 1;
        }
        return colonies;
    }

    /*
     * This method allows the user to assign grid arguments. Argument: int colony
     * amount. Returns int 1D array with grid arguments. In case of invalid input
     * (i.e. < 3*3), the user must enter a new argument.
     */
    public static int[] requestGridSize(int colonyAmount) {
        while (true) {
            System.out.println("Please define grid size (the dimensions must be at least 3x3). ");
            int width = (int) readValue(scanner, "Enter grid width: ");
            int depth = (int) readValue(scanner, "Enter grid height: ");
            int gridSize = width * depth;
            if (width < 3 || depth < 3) {
                continue;
            } else if (gridSize < colonyAmount) {
                System.out.println("The amount of colonies is too large for grid! ");
            } else {
                return new int[] { width, depth };
            }
        }
    }

    /*
     * This method reads and checks values from the user. Arguments: Scanner and
     * string message. Returns a message and default value to the user.
     */
    private static double readValue(Scanner scanner, String message) {
        return readValue(scanner, message, 0.0);
    }

    /*
     * This method reads and checks inputs from the user. Arguments: Scanner, string
     * message and number value. Returns input from user to a variable. If the input
     * is invalid, an error message is shown.
     */
    private static double readValue(Scanner scanner, String message, double definition) {
        System.out.println(message);
        String input = scanner.nextLine();
        while (input != null) {
            Double value = null;

            if (input.equalsIgnoreCase("default")) {
                break;
            }

            if (input.trim().isEmpty() || (value = getDouble(input)) == null) {
                System.out.println(input + " is not a valid input. Try again!");
                System.out.println(message);
            }

            if (value != null) {
                return value;
            }

            if (scanner.hasNextLine()) {
                input = scanner.nextLine();
            } else {
                input = null;
            }

        }
        System.out.println("Using default value of " + definition);
        return definition;
    }

    /*
     * This method converts string input to double. Argument: String input from
     * user. Returns double.
     */
    private static Double getDouble(String input) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    /*
     * This method checks if the user input is empty. Arguments: scanner, string
     * message and default string. Returns string input or default input. It is used
     * to read file input from the user.
     */
    public static String readUserString(Scanner scanner, String message, String defaultValue) {
        // prompt user
        System.out.print(message);
        // save input
        String strInput = scanner.nextLine();
        if (strInput.trim().isEmpty()) {
            return defaultValue;
        }
        return strInput;
    }

    /*
     * This method checks if the user is choosing a valid choice. Arguments:
     * Scanner, String message and choices. Returns valid choice or keeps asking
     * user for new input.
     */
    public static String readChoice(Scanner scanner, String message, String... choices) {
        while (true) {
            System.out.print(message);
            String choice = scanner.nextLine().trim();
            int i = 0;
            while (i < choices.length) {
                if (choice.equalsIgnoreCase(choices[i])) {
                    return choice;
                }
                i = i + 1;
            }
            System.out.println("Invalid choice: " + choice);
            System.out.println("Valid choices: " + String.join(",", choices));
        }
    }

}