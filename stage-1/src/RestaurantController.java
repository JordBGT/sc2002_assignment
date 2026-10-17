import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestaurantController {
    private int currentTurn = 1;
    private final int maxTurns = 18;
    private int revenueCents = 0;
    private int paidCustomersCount = 0;
    private int totalPaidSatisfaction = 0;

    private final List<Table> tables;
    private final List<Customer> waitingQueue;
    private final List<MenuItem> menu;
    private final Waiter waiter;
    private final Chef chef;
    private final Cashier cashier;

    private int nextCustomerId = 1;
    private int nextOrderId = 1;
    private final List<Integer> arrivalTurns = Arrays.asList(1, 4, 7, 10, 13, 16);

    // Helper query methods for CLI display
    public int getCurrentTurn() { return currentTurn; }
    public int getMaxTurns() { return maxTurns; }
    public int getRevenueCents() { return revenueCents; }
    public int getPaidCustomersCount() { return paidCustomersCount; }
    public List<Table> getTables() { return tables; }
    public List<Customer> getWaitingQueue() { return waitingQueue; }
    public Waiter getWaiter() { return waiter; }
    public Chef getChef() { return chef; }

    public Table getTableById(int tableId) {
        for (Table t : tables) {
            if (t.getTableId() == tableId) return t;
        }
        return null;
    }

    public Order getOrderById(int orderId) {
        for (Table t : tables) {
            if (t.getCurrentOrder() != null && t.getCurrentOrder().getOrderId() == orderId) {
                return t.getCurrentOrder();
            }
        }
        return null;
    }

    public boolean hasOrdersToCook() {
        for (Table t : tables) {
            Order o = t.getCurrentOrder();
            if (o != null && (o.getStatus() == OrderStatus.ORDERED || o.getStatus() == OrderStatus.PREPARING)) {
                return true;
            }
        }
        return false;
    }

    public RestaurantController(){
        this.tables = new ArrayList<>();
        this.tables.add(new Table(1));
        this.tables.add(new Table(2));
        this.waitingQueue = new ArrayList<>();

        this.menu = new ArrayList<>();
        this.menu.add(new MenuItem("Salad", 900, 1));
        this.menu.add(new MenuItem("Burger", 1500, 1));
        this.menu.add(new MenuItem("Pasta", 1800, 2));

        this.waiter = new Waiter(1);
        this.chef = new Chef(1);
        this.cashier = new Cashier(1);

        checkAndProcessArrival();

    }

    // 3.2 Step 1: Arrivals
    // The scheduled customer joins the waiting queue with satisfaction 100.
    private void checkAndProcessArrival() {
        if (arrivalTurns.contains(currentTurn)) {
            Customer newCustomer = new Customer(nextCustomerId++, currentTurn);
            newCustomer.setStatus(CustomerStatus.QUEUED);
            waitingQueue.add(newCustomer);
        }
    }

    public boolean executeWaiterTask(int choice, int target) {
        if(waiter.hasActed()) {
            System.out.println("Waiter has already acted this turn.");
            return false;
        }

        Table table = getTableById(target);
        switch (choice) {
            case 1: // Seat queued customer
                if (waitingQueue.isEmpty()) {
                    System.out.println("Action failed: Waiting queue is empty.");
                    return false;
                }
                if (table == null || table.isOccupied()) {
                    System.out.println("Action failed: Selected table is not available or occupied.");
                    return false;
                }
                Customer customerToSeat = waitingQueue.remove(0);
                boolean seated = waiter.seatCustomer(customerToSeat, table);
                if (seated) {
                    waiter.setActed(true);
                    return true;
                }
                return false;

            case 2: // Take order
                if (table == null || !table.isOccupied()) {
                    System.out.println("Action failed: Table is empty.");
                    return false;
                }
                Customer seatedCustomer = table.getCurrentCustomer();
                if (seatedCustomer == null || seatedCustomer.getStatus() != CustomerStatus.SEATED) {
                    System.out.println("Action failed: Customer cannot order right now.");
                    return false;
                }
                if (table.getCurrentOrder() != null) {
                    System.out.println("Action failed: Order already taken for this table.");
                    return false;
                }
                // Customer picks food (random generated)
                MenuItem item = seatedCustomer.orderFood(menu);
                Order order = waiter.takeOrder(table, item, nextOrderId++);
                if (order != null) {
                    waiter.setActed(true);
                    return true;
                }
                return false;

            case 3: // Serve order
                if (table == null || !table.isOccupied()) {
                    System.out.println("Action failed: Table is empty.");
                    return false;
                }
                Order tableOrder = table.getCurrentOrder();
                if (tableOrder == null || tableOrder.getStatus() != OrderStatus.READY) {
                    System.out.println("Action failed: Dish is not READY to be served.");
                    return false;
                }
                waiter.serveOrder(table);
                waiter.setActed(true);
                return true;

            case 0: // Wait / Skip
                waiter.setActed(true);
                return true;

            default:
                System.out.println("Action failed: Invalid choice.");
                return false;
        }
    }

    // 3.2 - Kitchen and serving
    public boolean executeChefTask(int orderId) {
        if (chef.hasActed()) {
            System.out.println("Chef has already acted this turn.");
            return false;
        }

        if (orderId == 0) {
            chef.setActed(true);
            return true;
        }

        Order targetOrder = getOrderById(orderId);
        if (targetOrder == null) {
            System.out.println("Action failed: Order not found.");
            return false;
        }
        if (targetOrder.getStatus() != OrderStatus.ORDERED && targetOrder.getStatus() != OrderStatus.PREPARING) {
            System.out.println("Action failed: Order cannot be prepared (current status: " + targetOrder.getStatus() + ").");
            return false;
        }

        chef.cookOrder(targetOrder);
        chef.setActed(true);
        return true;
    }

    public void executeTurn() {
        // Step 4: Eating & Automatic Cashier Payment
        // Handle customers eating -> ready to pay
        for (Table table : tables) {
            if (table.isOccupied()) {
                Customer c = table.getCurrentCustomer();
                if (c != null && c.getStatus() == CustomerStatus.SERVED) {
                    // Transition to eating -> ready to pay after 1 full turn
                    c.setStatus(CustomerStatus.READY_TO_PAY);
                }
            }
        }

        // Cashier collects payment from oldest READY_TO_PAY customer
        Table oldestReadyTable = null;
        int earliestArrival = Integer.MAX_VALUE;
        for (Table table : tables) {
            if (table.isOccupied() && table.getCurrentCustomer() != null) {
                Customer c = table.getCurrentCustomer();
                // A customer who became READY_TO_PAY this turn pays next turn at earliest
                if (c.getStatus() == CustomerStatus.READY_TO_PAY && c.getArrivalTurn() < currentTurn) {
                    if (c.getArrivalTurn() < earliestArrival) {
                        earliestArrival = c.getArrivalTurn();
                        oldestReadyTable = table;
                    }
                }
            }
        }

        if (oldestReadyTable != null) {
            Customer payingCustomer = oldestReadyTable.getCurrentCustomer();
            int amount = cashier.processPayment(oldestReadyTable);
            revenueCents += amount;
            paidCustomersCount++;
            totalPaidSatisfaction += payingCustomer.getSatisfaction();
            payingCustomer.setStatus(CustomerStatus.PAID);
            oldestReadyTable.clearTable();
        }

        // Step 5: Waiting & Abandonment
        // Queue customers lose satisfaction
        List<Customer> abandonedInQueue = new ArrayList<>();
        for (Customer c : waitingQueue) {
            c.reduceSatisfaction(8);
            if (c.isAbandoned()) {
                c.setStatus(CustomerStatus.ABANDONED);
                abandonedInQueue.add(c);
            }
        }
        waitingQueue.removeAll(abandonedInQueue);

        // Seated unserved customers lose satisfaction
        for (Table table : tables) {
            if (table.isOccupied()) {
                Customer c = table.getCurrentCustomer();
                if (c != null && (c.getStatus() == CustomerStatus.SEATED)) {
                    c.reduceSatisfaction(8);
                    if (c.isAbandoned()) {
                        c.setStatus(CustomerStatus.ABANDONED);
                        Order ord = table.getCurrentOrder();
                        if (ord != null) {
                            ord.setStatus(OrderStatus.CANCELLED);
                        }
                        table.clearTable();
                    }
                }
            }
        }

        // Reset staff actions for the next turn
        waiter.resetTurn();
        chef.resetTurn();
        cashier.resetTurn();

        // Advance turn counter
        currentTurn++;

        // Process scheduled customer arrival for the new turn
        if (!isGameOver()) {
            checkAndProcessArrival();
        }
    }

    public double getAverageSatisfaction() {
        if (paidCustomersCount == 0) return 0.0;
        return (double) totalPaidSatisfaction / paidCustomersCount;
    }

    public GameResult checkGameResult() {
        if (currentTurn <= maxTurns) {
            return GameResult.IN_PROGRESS;
        }
        // Base setting win criteria: >= 4 paid customers, >= $45 revenue (4500 cents), avg satisfaction >= 60
        if (paidCustomersCount >= 4 && revenueCents >= 4500 && getAverageSatisfaction() >= 60.0) {
            return GameResult.VICTORY;
        } else {
            return GameResult.DEFEAT;
        }
    }

    public boolean isGameOver() {
        return currentTurn > maxTurns;
    }

}


