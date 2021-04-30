import java.io.File;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

/**
 * The Graph class creates and fills out a grid of Node, Edge and Colony
 * instances. Additionally, Graph handles the pheromone levels of Edge
 * instances, the amount of sugar in Node and Colony instances, as well as the
 * periodic reduction of pheromones in Edges.
 */
public class Graph {
    private double sugarProbability;
    private int sugarAverage;
    private Edge[] edges;

    /**
     * The first constructor creates a 2D array of Node and Colony instances with
     * user-specified variables for the probability and the amount of sugar being
     * added to Nodes randomly over time.
     */
    public Graph(int width, int depth, Colony[] colonies, double sugarProbability, int sugarAverage) {
        this.sugarProbability = sugarProbability;
        this.sugarAverage = sugarAverage;

        Node[][] nodeGrid = fillingInNodesTo2dArray(width, depth);
        insertingColoniesToMap(nodeGrid, colonies, width, depth);
        this.edges = createEdgesBetweenNodes(width, depth, nodeGrid);
    }

    /**
     * The second constructor creates a 2D array of Node and Colony instances from a
     * user-specified text file, while handling possible exception that might be
     * thrown by a malformed text file from the user.
     */
    public Graph(String filename, Colony[] homes, double sugarProbability, int sugarAverage) {
        this.sugarProbability = sugarProbability;
        this.sugarAverage = sugarAverage;

        try {
            Scanner filescanner = new Scanner(new File(filename));

            int amountOfNodes = filescanner.nextInt();
            filescanner.nextLine();
            Node[] nodeLocation = new Node[amountOfNodes];

            String[] colonyInformation = filescanner.nextLine().trim().split(" ");

            addColoniesToNodeArray(colonyInformation, nodeLocation, homes);
            addNewNodesToEmptyIndexes(nodeLocation);
            createEdges(filescanner, nodeLocation);
            addSugarToNodes(nodeLocation);
            filescanner.close();
        } catch (Throwable t) {
            // Use in case of debugging
            // t.printStackTrace();
            System.err.println("File is not well-formed, error:  " + t.getMessage());
            System.exit(-1);
        }
    }

    /**
     * Gets the amount of pheromones in a given Node instance.
     */
    public int pheromoneLevel(Node source, Node target) {
        for (Edge edge : edges) {
            if (edge.source() == source && edge.target() == target) {
                return edge.pheromones();
            }
            if (edge.target() == source && edge.source() == target) {
                return edge.pheromones();
            }
        }
        return 0;
    }

    /**
     * Increases the amount of pheromones in a given Node instance.
     */
    public void raisePheromones(Node source, Node target, int amount) {
        for (Edge edge : edges) {
            if (edge.source() == source && edge.target() == target) {
                edge.raisePheromones(amount);
            }
            if (edge.target() == source && edge.source() == target) {
                edge.raisePheromones(amount);
            }
        }
    }

    /**
     * Checks for Node instances next to the one calling this method.
     */
    public Node[] adjacentTo(Node node) {
        ArrayList<Node> nodes = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.source() == node) {
                nodes.add(edge.target());
            } else if (edge.target() == node) {
                nodes.add(edge.source());
            }
        }
        return nodes.toArray(new Node[0]);
    }

    /**
     * Decreases the amount pheromones in edges and randomly decides whether to add
     * sugar.
     */
    public void tick() {
        for (Edge edge : edges) {
            edge.decreasePheromones();
        }
        if (RandomUtils.coinFlip(sugarProbability)) {
            spawnSugar();
        }
    }

    /**
     * Creates a 2D array to represent the position of Node instances relative to
     * each other.
     */
    private Node[][] fillingInNodesTo2dArray(int width, int depth) {
        Node[][] nodeGrid = new Node[width][depth];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < depth; j++) {
                if (RandomUtils.coinFlip(sugarProbability)) {
                    nodeGrid[i][j] = new Node(RandomUtils.randomPoisson(sugarAverage));
                } else {
                    nodeGrid[i][j] = new Node();
                }
            }
        }
        return nodeGrid;
    }

    /**
     * Takes 2D array and converts a given amount of them from Nodes to Colony
     * instances.
     */
    private void insertingColoniesToMap(Node[][] nodeGrid, Colony[] colonies, int width, int depth) {
        for (int i = 0; i < colonies.length;) {
            final int widthPostion = RandomUtils.randomInt(width);
            final int depthPosition = RandomUtils.randomInt(depth);
            if (!(isPositionColony(nodeGrid, widthPostion, depthPosition))) {
                nodeGrid[widthPostion][depthPosition] = colonies[i];
                i = i + 1;
            }
        }
    }

    /**
     * Creates connecting Edge instance between Node instances with source and
     * targets.
     */
    private Edge[] createEdgesBetweenNodes(int width, int depth, Node[][] nodeGrid) {
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < depth; j++) {
                if (i + 1 < width) {
                    edges.add(new Edge(nodeGrid[i][j], nodeGrid[i + 1][j]));
                }
                if (j + 1 < depth) {
                    edges.add(new Edge(nodeGrid[i][j], nodeGrid[i][j + 1]));
                }
            }
        }
        return edges.toArray(new Edge[edges.size()]);
    }

    /**
     * Randomly decides whether to increase the amount of sugar in a Node by a
     * random amount.
     */
    private void spawnSugar() {
        int randomEdgeIndex = RandomUtils.randomInt(edges.length);
        Edge randomEdge = edges[randomEdgeIndex];
        if (RandomUtils.coinFlip(0.50)) {
            randomEdge.source().setSugar(RandomUtils.randomPoisson(sugarAverage));
        } else {
            randomEdge.target().setSugar(RandomUtils.randomPoisson(sugarAverage));
        }
    }

    /**
     * Checks if the instance calling this method is currently in an instance of
     * Colony.
     */
    private boolean isPositionColony(Node[][] map, int widthPostion, int depthPosition) {
        return map[widthPostion][depthPosition] instanceof Colony;
    }

    /**
     * Adds Colony instances to Node array.
     */
    private void addColoniesToNodeArray(String[] colonyInformation, Node[] nodeLocation, Colony[] homes) {
        int colonyCounter = 0;
        for (String colony : colonyInformation) {
            int colonyIndex = Integer.parseInt(colony) - 1;
            nodeLocation[colonyIndex] = homes[colonyCounter];
            colonyCounter = colonyCounter + 1;
        }
    }

    /**
     * Fills out empty slot in Nodes array.
     */
    private void addNewNodesToEmptyIndexes(Node[] nodeLocation) {
        int i = 0;
        while (i < nodeLocation.length) {
            if (nodeLocation[i] == null) {
                Node node = new Node();
                nodeLocation[i] = node;
            }
            i = i + 1;
        }
    }

    /**
     * Creates a connecting Edge instance between two Node instances.
     */
    private void createEdges(Scanner filescanner, Node[] nodeLocation) {
        List<Edge> edges = new ArrayList<>();
        while (filescanner.hasNextLine()) {
            int nodeA = filescanner.nextInt() - 1;
            int nodeB = filescanner.nextInt() - 1;
            filescanner.nextLine();
            Edge edge = new Edge(nodeLocation[nodeA], nodeLocation[nodeB]);
            checkDuplicateEdge(edge, edges);
            edges.add(edge);
        }
        this.edges = edges.toArray(new Edge[edges.size()]);
    }

    /**
     * Checks whether multiple Edge instances target the same Node in the same
     * direction.
     */
    private void checkDuplicateEdge(Edge edge, List<Edge> edges) {
        for (Edge other : edges) {
            boolean isSameWay = other.source() == edge.source() && other.target() == edge.target();
            boolean isOtherWay = other.source() == edge.target() && other.target() == edge.source();
            if (isSameWay || isOtherWay) {
                throw new RuntimeException("Duplicate edges");
            }
        }
    }

    /**
     * Increases the amount of sugar in a given Node instance.
     */
    private void addSugarToNodes(Node[] nodeLocation) {
        for (Node node : nodeLocation) {
            if (RandomUtils.coinFlip(sugarProbability)) {
                node.setSugar(RandomUtils.randomPoisson(sugarAverage));
            }
        }
    }
}