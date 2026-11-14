import java.util.List;
import java.util.ArrayList;

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
    /** Every third arrival chooses a combo; others choose an individual dish.
     * The customer's id selects within that group, making every game repeatable.
     */
    public MenuItem orderFood(List<MenuItem> menu) {
        if (menu == null || menu.isEmpty()) return null;
        List<MenuItem> choices = new ArrayList<>();
        boolean wantsCombo = id % 3 == 0;
        for (MenuItem item : menu) {
            if ((item instanceof ComboMeal) == wantsCombo) choices.add(item);
        }
        if (choices.isEmpty()) choices.addAll(menu);
        return choices.get((id - 1) % choices.size());
    }

    public void recoverSatisfaction(int amount) {
        if (status == CustomerStatus.QUEUED || status == CustomerStatus.SEATED)
            satisfaction = Math.min(100, satisfaction + Math.max(0, amount));
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
