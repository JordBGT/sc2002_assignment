public class Cashier extends Staff
{
    // Constructor
    public Cashier(int id)
    {
        super(id);
    }


    // Methods
    public int processPayment(Table table)
    {
        // If notActed and TableOccupied
        if(!hasActedThisTurn && table.isOccupied())
        {
            Customer customer = table.getCurrentCustomer();
            Order order = table.getCurrentOrder();

            // If customer READY TO PAY
            if(customer != null && order != null && customer.getStatus() == CustomerStatus.READY_TO_PAY) {
                // Get the Money $$$ and clear Table
                int collectedAmtCents = order.getPrice();
                customer.setStatus(CustomerStatus.PAID);
                order.setStatus(OrderStatus.PAID);
                table.clearTable();
                setActed(true);

                return collectedAmtCents;
            }
        }
        return 0;
    }
}
