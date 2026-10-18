import java.util.InputMismatchException;
import java.util.Scanner;

public class RestaurantCLI {
    private final Scanner scanner;

    public RestaurantCLI() {
        this.scanner = new Scanner(System.in);
    }

    public void displayTurnStart(RestaurantController controller) {
        System.out.println("\n========================================================");
        System.out.printf("  TURN %d / %d\n", controller.getCurrentTurn(), controller.getMaxTurns());
        System.out.printf("  Revenue: $%.2f | Paid Customers: %d | Avg Satisfaction: %s\n",
                controller.getRevenueCents() / 100.0,
                controller.getPaidCustomersCount(),
                controller.getPaidCustomersCount() == 0 ? "N/A" : String.format("%.1f", controller.getAverageSatisfaction()));
        System.out.println("========================================================");

        // Display Waiting Queue
        System.out.print("Waiting Queue: ");
        if (controller.getWaitingQueue().isEmpty()) {
            System.out.println("Empty");
        } else {
            for (Customer c : controller.getWaitingQueue()) {
                System.out.printf("[Customer #%d (%s)| Satisfaction: %d] ", c.getId(), c.getCustomerType(), c.getSatisfaction());
            }
            System.out.println();
        }

        // Display Tables
        System.out.println("\nTables State:");
        for (Table t : controller.getTables()) {
            if (!t.isOccupied()) {
                System.out.printf("  Table %d: [AVAILABLE]\n", t.getTableId());
            } else {
                Customer c = t.getCurrentCustomer();
                Order o = t.getCurrentOrder();
                String orderInfo = (o == null) ? "No Order" :
                        String.format("Order #%d (%s, Status: %s, Remaining Prep: %d)",
                                o.getOrderId(), o.getItem().getName(), o.getStatus(), o.getRemainingUnits());

                System.out.printf("  Table %d: [OCCUPIED] Customer #%d (%s) (Satisfaction: %d, Status: %s) | %s\n",
                        t.getTableId(), c.getId(), c.getCustomerType(), c.getSatisfaction(), c.getStatus(), orderInfo);
            }
        }
        System.out.println("--------------------------------------------------------");
    }

    public int promptWaiterAction(RestaurantController controller) {
        while (true) {
            System.out.println("\n--- Waiter Actions ---");
            System.out.println("1. Seat queued customer");
            System.out.println("2. Take order from seated customer");
            System.out.println("3. Serve READY order");
            System.out.println("0. Wait (Skip)");
            System.out.print("Select Waiter action (0-3): ");

            int choice = readInt();
            if (choice == 0) {
                controller.executeWaiterTask(0, 0);
                return 0;
            } else if (choice >= 1 && choice <= 3) {
                System.out.print("Enter Target Table ID (1 or 2): ");
                int targetTableId = readInt();
                boolean success = controller.executeWaiterTask(choice, targetTableId);
                if (success) {
                    return choice;
                }
                System.out.println("Please re-select a valid action.");
            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }
    }

    public int promptChefAction(RestaurantController controller) {
        while (true) {
            System.out.println("\n--- Chef Actions ---");
            System.out.println("Active Orders to Prepare:");
            boolean hasOrders = false;
            for (Table t : controller.getTables()) {
                Order o = t.getCurrentOrder();
                if (o != null && (o.getStatus() == OrderStatus.ORDERED || o.getStatus() == OrderStatus.PREPARING)) {
                    System.out.printf("  -> Order ID: %d (%s, Remaining Units: %d)\n",
                            o.getOrderId(), o.getItem().getName(), o.getRemainingUnits());
                    hasOrders = true;
                }
            }
            if (!hasOrders) {
                System.out.println("  (No pending orders need cooking)");
            }

            System.out.print("Enter Order ID to cook, or 0 to Wait (Skip): ");
            int orderId = readInt();

            boolean success = controller.executeChefTask(orderId);
            if (success) {
                return orderId;
            }
            System.out.println("Please re-select a valid chef action.");
        }
    }

    public void displayTurnSummary(RestaurantController controller) {
        System.out.println("\n>>> Turn processing finished. <<<");
        System.out.printf("End of Turn %d State: Revenue: $%.2f | Paid Customers: %d\n",
                controller.getCurrentTurn() - 1,
                controller.getRevenueCents() / 100.0,
                controller.getPaidCustomersCount());
    }

    public void displayGameOver(GameResult result, RestaurantController controller) {
        System.out.println("\n========================================================");
        System.out.println("                       GAME OVER                        ");
        System.out.println("========================================================");
        System.out.printf("Final Result           : %s\n", result);
        System.out.printf("Total Revenue          : $%.2f (Target: >= $45.00)\n", controller.getRevenueCents() / 100.0);
        System.out.printf("Paid Customers Count   : %d (Target: >= 4)\n", controller.getPaidCustomersCount());
        System.out.printf("Average Satisfaction   : %s (Target: >= 60.0)\n",
                controller.getPaidCustomersCount() == 0 ? "0.0" : String.format("%.1f", controller.getAverageSatisfaction()));
        System.out.println("========================================================");

        if (result == GameResult.VICTORY) {
            System.out.println("Congratulations! You met all manager targets!");
        } else {
            System.out.println("Defeat! You did not meet all manager objectives.");
        }
    }

    private int readInt() {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.print("Invalid input. Please enter an integer: ");
            }
        }
    }

    // main method to launch and test Stage 1
    public static void main(String[] args) {
        RestaurantController controller = new RestaurantController();
        RestaurantCLI cli = new RestaurantCLI();

        while (!controller.isGameOver()) {
            cli.displayTurnStart(controller);
            cli.promptWaiterAction(controller);

            if (controller.hasOrdersToCook()) {
                cli.promptChefAction(controller);
            } else {
                System.out.println("\n[Chef]: No cookable orders this turn (Chef waits).");
                controller.executeChefTask(0);
            }

            controller.executeTurn();
            cli.displayTurnSummary(controller);
        }

        cli.displayGameOver(controller.checkGameResult(), controller);
    }
}
