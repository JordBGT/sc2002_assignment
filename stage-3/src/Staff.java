public abstract class Staff
{
    protected int id;
    protected boolean hasActedThisTurn;

    // Constructor
    public Staff(int id)
    {
        this.id = id;
        this.hasActedThisTurn = false;
    }


    // Getters
    public int getId()
    {
        return this.id;
    }


    // Setters
    public void setActed(boolean acted)
    {
        this.hasActedThisTurn = acted;
    }


    // Methods
    public boolean hasActed()
    {
        return this.hasActedThisTurn;
    }
    public void resetTurn()
    {
        this.hasActedThisTurn = false;
    }
    @Override
    public String toString()
    {
        // Usage: System.out.println(waiter) -> Output: Waiter #1  //Class Name + Id
        return getClass().getSimpleName() + " #" + id;
    }


}