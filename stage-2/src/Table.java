public class Table
{
    private int tableId;
    private boolean isOccupied;

    // Association relationship variables
    private Customer currentCustomer;
    private Order currentOrder;

    // Constructor
    public Table(int tableId)
    {
        this.tableId = tableId;
        this.isOccupied = false;
        this.currentCustomer = null;
        this.currentOrder = null;
    }


    // Getters
    public int getTableId()
    {
        return this.tableId;
    }
    public boolean isOccupied()
    {
        return this.isOccupied;
    }
    public Customer getCurrentCustomer()
    {
        return this.currentCustomer;
    }
    public Order getCurrentOrder()
    {
        return this.currentOrder;
    }


    // Methods
    public void assignOrder(Order order)
    {
        this.currentOrder = order;
    }
    public void seatCustomer(Customer customer)
    {
        if(customer != null)
        {
            this.currentCustomer = customer;
            this.isOccupied = true;
            customer.setStatus(CustomerStatus.SEATED);
        }
    }
    public void clearTable()
    {
        this.isOccupied = false;
        this.currentCustomer = null;
        this.currentOrder = null;
    }
}