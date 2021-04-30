/**
 * This class represents nodes of ant colonies, extends the class Node and
 * manages the colonies' sugar stock.
 */

public class Colony extends Node {

    /**
     * Default constructor creates a new colony with an empty sugar stock.
     */
    public Colony() {
    }

    /**
     * Increases the colony's sugar stock by a given amount.
     */
    public void topUp(int sugar) {
        setSugar(sugar() + sugar);
    }

    /**
     * Decreases the colony's sugar stock by one unit.
     */
    public void consume() {
        decreaseSugar();
    }

    /**
     * Returns a non-empty sugar stock in the colony.
     */
    public boolean hasStock() {
        return sugar() > 0;
    }
}