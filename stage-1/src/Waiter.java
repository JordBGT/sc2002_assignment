public class Waiter extends Staff
{
    // Constructor
    public Waiter(int id)
    {
        super(id);
    }


    // Methods
    public boolean seatCustomer(Customer customer, Table table)
    {
        // If notActed and notTabletOccupied and notCustomerNull
        if(!hasActedThisTurn && !table.isOccupied() && customer !=null)
        {
            table.seatCustomer(customer);
            setActed(true);
            return true;
        }
        return false;
    }
    public Order takeOrder(Table table, MenuItem item, int orderId)
    {
        // If notActed and TabletOccupied and notItemNull
        if(!hasActedThisTurn && table.isOccupied() && item !=null)
        {
            Order newOrder = new Order(orderId, item);
            table.assignOrder(newOrder);
            setActed(true);
            return newOrder;
        }
        return null;
    }
    public void serveOrder(Table table)
    {
        // If notActed and TableOccupied
        if(!hasActedThisTurn && table.isOccupied())
        {
            Order order = table.getCurrentOrder();
            Customer customer = table.getCurrentCustomer();

            if(order.isReady() && order!= null && customer != null)
            {
                order.setStatus(OrderStatus.SERVED);
                customer.setStatus(CustomerStatus.SERVED);
                setActed(true);
            }
        }
    }



}
