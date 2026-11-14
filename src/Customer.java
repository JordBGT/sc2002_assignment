import java.util.List;
import java.util.Random;

public class Customer
{
    private int id;
    private int satisfaction;
    private int arrivalTurn;
    private CustomerStatus status;
    private static final Random rng = new Random(42);      // FIXED SEED HERE, CHANGE THIS FOR NEW SEED

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
    public MenuItem orderFood(List<MenuItem> menu) {
        if (menu == null || menu.isEmpty()) return null;
        int index = rng.nextInt(menu.size());
        return menu.get(index);
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
    public int getSatisfactionLoss() {
        return 8;
    }

    public int getDiscountedPrice(int priceCents){
        return priceCents;
    }

    public String getCustomerType(){
        return "Regular";
    }

}
