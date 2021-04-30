/**
 * Instances of class Simulator equates to the given parameters and states of a
 * simulation. It basically sets up and runs this simulation.
 */
public class Simulator {

    private Graph graph;
    private Ant[] ants;
    private int sugarCapacity;
    private int droppedPheromones;

    /**
     * Constructor creates a new simulation with the related parameters.
     */
    public Simulator(Graph graph, Ant[] ants, int sugar, int pheromones) {
	this.graph = graph;
	this.ants = ants;
	this.sugarCapacity = sugar;
	this.droppedPheromones = pheromones;
    }

    /**
     * The order of the commands for the tick method (which runs this simulation for
     * one unit of time) must be:
     *
     * 1. move() 2. dropSugar() 3. eatSugar()
     *
     * If eatSugar() is applied after move(), the ants die when they return to the
     * colony. If eatSugar() is applied before move(), all the ants die immediately.
     */
    public void tick() {
	graph.tick();
	move();
	dropSugar();
	eatSugar();
    }

    /**
     * Uses for loop to iterate through instances of Ant in ants array. Calling the
     * dropSugar method on each Ant in ants array.
     */
    private void dropSugar() {
	for (Ant ant : ants) {
	    dropSugar(ant);
	}
    }

    /**
     * Loops through Ant instances in ants array. Checks if Ant is dead or "null",
     * and then moves it.
     */
    private void move() {
	for (Ant ant : ants) {
	    if (ant != null) {
		move(ant);
	    }
	}
    }

    /**
     * Loops through ants. Decreases sugar amount by one for each Ant instance.
     */
    private void eatSugar() {
	for (int i = 0; i < ants.length; i = i + 1) {
	    eatSugar(i, ants);
	}
    }

    /**
     * Checks if Ant instance is in its home Colony. Checks if Ant is carrying
     * sugar. If Ant is home and carrying sugar, the ant.dropSugar method is called.
     */
    public void dropSugar(Ant ant) {
	if (ant != null) {
	    Colony home = ant.home();
	    if (ant.isAtHome() && ant.carrying()) {
		ant.dropSugar();
		home.topUp(home.sugar() + sugarCapacity);
	    }
	}
    }

    /**
     * Checks if Ant is not null or dead. Sets ant's home Colony. If home has no
     * sugar, the Ant dies or is set to null. If the home colony has sugar, one unit
     * of sugar is subtracted from Colony.
     */
    public void eatSugar(int i, Ant[] ants) {
	Ant ant = ants[i];
	if (ant != null) {
	    Colony home = ant.home();
	    if (ant.isAtHome()) {
		if (!home.hasStock()) {
		    ants[i] = null;
		    // System.out.println(String.format("Ant: %s died from starvation", ant));
		} else {
		    home.consume();
		    /*
		     * System.out.println(String.format("Ant: %s ate sugar %d left", ant,
		     * home.sugar()));
		     */
		}
	    }
	}
    }

    /**
     * Records the location of Ant instance as well as a new target Node. Once
     * found, the pheromone level of the Edge (which the the Ant traverse) is
     * increased, and the Ant is moved to the new Node.
     */
    public void move(Ant ant) {
	Node current = ant.current();
	Node nextNode = findNextNode(ant);

	/*
	 * int pheromoneLevel = graph.pheromoneLevel(current, nextNode)
	 * System.out.println(String.format("Ant: %s, Raising pheromoneLevel from: %d
	 * to: %d, between nodes: %s <-> %s", ant, pheromoneLevel, pheromoneLevel +
	 * droppedPheromones, current, nextNode) );
	 */
	if (nextNode == null) {
	    throw new RuntimeException(
		    String.format("Ant: %s, Graph returned null adjacent node from current node: %s", ant, current));
	}
	graph.raisePheromones(current, nextNode, droppedPheromones);
	ant.move(nextNode);
	/*
	 * System.out.println(String.format("Ant %s, new pheromoneLevel: %d", ant,
	 * graph.pheromoneLevel(current, nextNode)));
	 */
    }

    /**
     * Finds nodes for ants to travel to, using the ant's current and previous
     * locations. Checks whether the current node has sugar. If so, that sugar is
     * picked up (if the Ant is not already carrying). One unit of sugar is also
     * subtracted from the node, and the Ant moves to its previous location. If
     * there is no sugar in the current node, the findAdjacent method is called to
     * find a new node (that is connected to the current one by an Edge).
     */
    private Node findNextNode(Ant ant) {
	Node current = ant.current();
	Node previous = ant.previous();

	if (shouldPickUpSugar(current, ant)) {
	    // System.out.println(String.format("Ant: %s, is picking up sugar", ant));
	    current.decreaseSugar();
	    ant.pickUpSugar();
	    return previous;
	}
	Node[] adjacent = findAdjacent(current, previous);
	return findNextNode(adjacent, current);
    }

    /**
     * Called when Ant encounters a node with sugar in it. Checks if Ant is already
     * carrying sugar. If the Ant is not carrying sugar, it picks up a unit. If the
     * Ant is carrying and is not at home, it is instructed to go to its home
     * Colony.
     */
    private boolean shouldPickUpSugar(Node current, Ant ant) {
	boolean decision = !ant.carrying() && current.sugar() > 0 && !ant.isAtHome();
	return decision;
    }

    /**
     * Called by ants when trying to find a new Node to travel to. Checks the ant's
     * current node, and finds any nodes connected to it (but not the ant's previous
     * node).
     */
    private Node[] findAdjacent(Node current, Node previous) {
	Node[] adjacent = graph.adjacentTo(current);
	for (int i = 0; i < adjacent.length; i = i + 1) {
	    if (adjacent[i] == null) {
		throw new RuntimeException(
			String.format("Graph returned null adjacent node from current node: %s", current));
	    }
	}
	if (current == previous || previous == null || adjacent.length == 1) {
	    return adjacent;
	}
	return adjacentWithoutPrevious(adjacent, previous);
    }

    /**
     * Checks if the node found by the findAdjacent method is one the Ant instance
     * has visited before its current node.
     */
    private Node[] adjacentWithoutPrevious(Node[] adjacent, Node previous) {
	/*
	 * Find how many nodes are not a previous node. Hack to avoid faulty
	 * graph.adjacentTo giving multiple of the same entry.
	 */
	int newLength = 0;
	for (Node node : adjacent) {
	    if (node != previous) {
		newLength = newLength + 1;
	    }
	}
	Node[] newAdjacent = new Node[newLength];
	int inputIndex = 0;
	for (Node node : adjacent) {
	    if (node != previous) {
		newAdjacent[inputIndex] = node;
		inputIndex = inputIndex + 1;
	    }
	}

	for (int i = 0; i < newAdjacent.length; i = i + 1) {
	    if (newAdjacent[i] == null) {
		throw new RuntimeException(String.format("Graph returned null adjacent"));
	    }
	}
	return newAdjacent;
    }

    /**
     * Determines which node an instance of Ant should go to next on its way to its
     * intended location. Checks for dead-ends, in which case the Ant is instructed
     * to return to its previous Node. If more than one Edge is connected to the
     * current Node, the decision (about which Edge instance to traverse) is
     * calculated using the weightedProbability method – and by comparing the amount
     * of Pheromones in each connected Edge.
     */
    private Node findNextNode(Node[] adjacent, Node current) {
	if (adjacent.length == 1) {
	    /*
	     * System.out.println(String.format("Only way Moving %s -> %s", current,
	     * adjacent[0]));
	     */
	    return adjacent[0];
	}
	int[] nodeProbabilities = new int[adjacent.length];
	int totalPheromones = weightedProbabilities(adjacent, current, nodeProbabilities);
	Node nextNode = pickRandomNextNode(totalPheromones, nodeProbabilities, adjacent);
	/*
	 * System.out.println(String.format("Random Moving %s -> %s", current,
	 * nextNode));
	 */
	return nextNode;
    }

    /**
     * Loops through connected instances of Edge, comparing the amount of pheromones
     * in each.
     */
    private int weightedProbabilities(Node[] adjacent, Node current, int[] nodeProbabilities) {
	int totalPheromones = 0;
	for (int i = 0; i < nodeProbabilities.length; i = i + 1) {
	    int pheromones = graph.pheromoneLevel(current, adjacent[i]);
	    totalPheromones += pheromones + 1;
	    nodeProbabilities[i] = totalPheromones;
	}
	return totalPheromones;
    }

    /**
     * In case of a stalemate between the pheromone amounts of two or more instances
     * of Edge, a random Edge instance is selected for the Ant to traverse.
     */
    private Node pickRandomNextNode(int totalPheromones, int[] nodeProbabilities, Node[] adjacent) {
	for (int i = 0; i < adjacent.length; i = i + 1) {
	    if (adjacent[i] == null) {
		throw new RuntimeException(String.format("Graph returned null adjacent"));
	    }
	}
	int randomNumber = RandomUtils.randomInt(totalPheromones);
	for (int i = 0; i < nodeProbabilities.length - 1; i = i + 1) {
	    if (randomNumber < nodeProbabilities[i]) {
		return adjacent[i];
	    }
	}
	return adjacent[adjacent.length - 1];
    }
}