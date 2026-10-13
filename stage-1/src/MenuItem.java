public class MenuItem
{
    private String name;
    private int priceCents;
    private int prepUnits;

    // Constructor
    public MenuItem(String name, int priceCents, int prepUnits)
    {
        this.name = name;
        this.priceCents = priceCents;
        this.prepUnits = prepUnits;
    }

    // Getters
    public String getName()
    {
        return this.name;
    }
    public int getPriceCents()
    {
        return this.priceCents;
    }
    public int getPrepUnits()
    {
        return this.prepUnits;
    }
}
