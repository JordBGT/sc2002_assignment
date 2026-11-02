/** Composition: a combo retains its two existing menu items. */
public class ComboMeal extends MenuItem {
    private final MenuItem first;
    private final MenuItem second;

    public ComboMeal(MenuItem first, MenuItem second) {
        super(nameOf(first, second),
                (int) Math.round((first.getPriceCents() + (long) second.getPriceCents()) * 0.9),
                first.getPrepUnits() + second.getPrepUnits());
        this.first = first;
        this.second = second;
    }

    private static String nameOf(MenuItem first, MenuItem second) {
        if (first == null || second == null || first instanceof ComboMeal || second instanceof ComboMeal)
            throw new IllegalArgumentException("A combo requires two existing individual menu items.");
        return first.getName() + " + " + second.getName() + " Combo";
    }

    public MenuItem getFirst() { return first; }
    public MenuItem getSecond() { return second; }
}
