/** Seats one queued customer independently of the Waiter each turn. */
public class Host extends Staff {
    public Host(int id) { super(id); }

    public boolean seatCustomer(Customer customer, Table table) {
        if (hasActed() || customer == null || table == null || table.isOccupied()
                || customer.getStatus() != CustomerStatus.QUEUED) return false;
        table.seatCustomer(customer);
        setActed(true);
        return true;
    }
}
