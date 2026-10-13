import java.util.List;

public class Customer
{
    private int id;
    private int satisfaction;
    private int arrivalTurn;
    private CustomerStatus status;

    // Constructor
    public Customer(int id, int arrivalTurn)
    {
        this.id = id;
        this.arrivalTurn = arrivalTurn;
        this.satisfaction = 100;
        this.status = CustomerStatus.QUEUED;
    }


    // Getters
    public int getId()
    {
        return this.id;
    }
    public int getSatisfaction()
    {
        return this.satisfaction;
    }
    public int getArrivalTurn()
    {
        return this.arrivalTurn;
    }
    public CustomerStatus getStatus()
    {
        return this.status;
    }


    // Setters
    public void setStatus(CustomerStatus status)
    {
        this.status = status;
    }


    // Methods
    public MenuItem orderFood(List<MenuItem> menu)
    {
        // Pre-Set Customer Menu Choice to index 0, edit here if want to change
        return menu.get(0);
    }
    public void reduceSatisfaction(int amount)
    {
        this.satisfaction -= amount;
        if(this.satisfaction <= 0)
        {
            this.satisfaction = 0;
            this.status = CustomerStatus.ABANDONED;
        }
    }
    public boolean isAbandoned()
    {
        return this.satisfaction<=0 || this.status==CustomerStatus.ABANDONED;
    }

}
