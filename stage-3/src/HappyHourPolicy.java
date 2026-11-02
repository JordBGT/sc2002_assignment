/** Once-per-game pricing policy; its duration advances only at turn end. */
public class HappyHourPolicy {
    private boolean used;
    private int remainingTurns;

    public boolean activate() {
        if (used) return false;
        used = true;
        remainingTurns = 2;
        return true;
    }

    public boolean hasBeenUsed() { return used; }
    public boolean isActive() { return remainingTurns > 0; }
    public int getRemainingTurns() { return remainingTurns; }

    public int priceFor(Customer customer, MenuItem item) {
        int customerPrice = customer.getDiscountedPrice(item.getPriceCents());
        return isActive() ? (int) Math.round(customerPrice * 0.8) : customerPrice;
    }

    public void endTurn() {
        if (isActive()) remainingTurns--;
    }
}
