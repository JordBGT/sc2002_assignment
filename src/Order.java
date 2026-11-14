public class Order
{
    private int orderId;
    private int remainingUnits;
    private OrderStatus status;

    // Association relationship variables
    private MenuItem item;

    // Constructor
    public Order(int orderId, MenuItem item)
    {
        this.orderId = orderId;
        this.item = item;
        this.remainingUnits = item.getPrepUnits();
        this.status = OrderStatus.ORDERED;
    }


    // Getters
    public int getOrderId()
    {
        return this.orderId;
    }
    public int getRemainingUnits()
    {
        return this.remainingUnits;
    }
    public OrderStatus getStatus()
    {
        return this.status;
    }
    public MenuItem getItem()
    {
        return this.item;
    }
    public int getPrice()
    {
        return item.getPriceCents();
    }


    // Setters
    public void setStatus(OrderStatus status)
    {
        this.status = status;
    }


    // Methods
    public void advancePrep()
    {
        if (this.remainingUnits <= 0)
        {
            return;
        }

        this.remainingUnits -= 1;
        if(this.remainingUnits <= 0)
        {
            this.status = OrderStatus.READY;
        }
        else
        {
            this.status = OrderStatus.PREPARING;
        }

    }
    public boolean isReady()
    {
        return this.remainingUnits<=0 || this.status==OrderStatus.READY;
    }

}