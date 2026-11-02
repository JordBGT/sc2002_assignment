import java.util.Arrays;

/** Offline regression checks; no test framework required. */
public class Stage3Test {
    private static int checks;
    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
    private static void end(RestaurantController game) {
        game.executeTurn();
    }
    public static void main(String[] args) {
        MenuItem salad = new MenuItem("Salad", 900, 1);
        MenuItem pasta = new MenuItem("Pasta", 1800, 2);
        MenuItem burger = new MenuItem("Burger", 1500, 1);
        ComboMeal combo = new ComboMeal(salad, pasta);
        check(combo.getPriceCents() == 2430 && combo.getPrepUnits() == 3, "Combo example");
        check(combo.getFirst() == salad && combo.getSecond() == pasta, "Combo composition");
        check(new ComboMeal(new MenuItem("A", 1, 1), new MenuItem("B", 4, 1)).getPriceCents() == 5,
                "Combo rounds half a cent up");
        check(new Customer(3, 7).orderFood(Arrays.asList(salad, pasta, combo)) == combo, "Third arrival selects combo");
        check(new CriticCustomer(3, 7).orderFood(Arrays.asList(salad, pasta, combo)) == combo, "Critic supports combo rule");
        check(new CriticCustomer(1, 1).orderFood(Arrays.asList(salad, pasta)).getPriceCents() > 1000, "Critic premium preference");

        HappyHourPolicy policy = new HappyHourPolicy();
        check(policy.activate(), "Activate once");
        check(policy.priceFor(new VIPCustomer(1, 1), burger) == 1080, "VIP then Happy Hour example");
        check(policy.priceFor(new Customer(1, 1), combo) == 1944, "Combo then Happy Hour");
        check(policy.priceFor(new VIPCustomer(1, 1), combo) == 1750, "VIP combo rounding then Happy Hour rounding");
        policy.endTurn();
        check(policy.getRemainingTurns() == 1, "Second active turn");
        policy.endTurn();
        check(!policy.isActive() && !policy.activate(), "Expires and cannot reactivate");

        RestaurantController game = new RestaurantController();
        Customer first = game.getWaitingQueue().get(0);
        first.reduceSatisfaction(30);
        check(game.startTurn(true) && first.getSatisfaction() == 85, "Activation turn recovery");
        check(game.startTurn(false) && first.getSatisfaction() == 85, "No repeated recovery");
        check(!game.executeHostTask(9) && !game.getHost().hasActed(), "Invalid Host action preserves task");
        check(game.executeHostTask(1), "Host seats");
        check(!game.executeHostTask(2), "Host cannot act twice");
        check(!game.executeChefTask(1) && !game.getChef().hasActed(), "Chef waits for Waiter choice");
        check(!game.executeWaiterTask(2, 2) && !game.getWaiter().hasActed(), "Invalid Waiter action preserves task");
        check(game.executeWaiterTask(2, 1), "Waiter takes order after Host seats same turn");
        Order order = game.getTableById(1).getCurrentOrder();
        int locked = order.getPrice();
        check(locked == (int) Math.round(first.getDiscountedPrice(order.getItem().getPriceCents()) * 0.8), "Controller locks discounts");
        check(game.executeChefTask(order.getOrderId()), "Chef cooks new order same turn");
        check(!game.executeWaiterTask(3, 1), "Cannot serve after Chef this turn");
        check(!game.startTurn(true), "Cannot activate after staff actions");
        end(game);
        int afterDecay = first.getSatisfaction();
        game.startTurn(false);
        check(first.getSatisfaction() == Math.min(100, afterDecay + 15), "Second turn recovery");
        check(game.getHappyHourRemainingTurns() == 1, "Display remaining duration");
        // Finish food, preserving the Waiter-before-Chef sequence.
        if (!order.isReady()) {
            game.executeWaiterTask(0, 0);
            game.executeChefTask(order.getOrderId());
            end(game);
            game.startTurn(false);
        }
        check(game.executeWaiterTask(3, 1), "Serve READY order on a later turn");
        int servedTurn = game.getCurrentTurn();
        int servedSatisfaction = first.getSatisfaction();
        end(game);
        check(first.getStatus() == CustomerStatus.EATING && game.getRevenueCents() == 0, "No payment on serving turn");
        end(game);
        check(first.getStatus() == CustomerStatus.READY_TO_PAY && game.getRevenueCents() == 0, "Full eating turn before ready");
        end(game);
        check(first.getStatus() == CustomerStatus.PAID && game.getRevenueCents() == locked, "Cashier collects locked bill without discounting twice");
        check(first.getSatisfaction() == servedSatisfaction, "Served customer no longer decays");
        check(game.getCurrentTurn() == servedTurn + 3 && !game.getTableById(1).isOccupied(), "Payment frees table at correct time");
        check(!game.startTurn(true), "Cannot reuse expired Happy Hour");
        check(order.getPrice() == locked, "Price unchanged after expiry");

        RestaurantController existing = new RestaurantController();
        existing.executeHostTask(1);
        existing.executeWaiterTask(2, 1);
        Order oldOrder = existing.getTableById(1).getCurrentOrder();
        int oldPrice = oldOrder.getPrice();
        end(existing);
        existing.startTurn(true);
        check(oldOrder.getPrice() == oldPrice, "Activation leaves existing order unchanged");
        Customer queued = new Customer(99, 1);
        queued.reduceSatisfaction(5);
        queued.recoverSatisfaction(15);
        check(queued.getSatisfaction() == 100, "Recovery capped at 100");
        queued.reduceSatisfaction(100);
        queued.recoverSatisfaction(15);
        check(queued.getSatisfaction() == 0 && queued.isAbandoned(), "Departed customers never recover");
        Customer served = new Customer(100, 1);
        served.reduceSatisfaction(20);
        served.setStatus(CustomerStatus.SERVED);
        served.recoverSatisfaction(15);
        check(served.getSatisfaction() == 80, "Recovery excludes served customers");

        RestaurantController both = new RestaurantController();
        both.executeWaiterTask(1, 1);
        end(both); end(both); end(both);
        check(both.executeHostTask(2), "Host seats second arrival");
        check(both.executeWaiterTask(2, 1), "Host and Waiter have independent tasks");
        end(both); end(both); end(both);
        check(!both.executeHostTask(1) && !both.getHost().hasActed(), "Occupied table rejected");

        RestaurantController abandoned = new RestaurantController();
        abandoned.executeWaiterTask(1, 1);
        end(abandoned);
        abandoned.executeWaiterTask(2, 1);
        Customer leaving = abandoned.getTableById(1).getCurrentCustomer();
        Order cancelled = abandoned.getTableById(1).getCurrentOrder();
        leaving.reduceSatisfaction(leaving.getSatisfaction() - 1);
        end(abandoned);
        check(cancelled.getStatus() == OrderStatus.CANCELLED && !abandoned.getTableById(1).isOccupied(),
                "Abandonment cancels unfinished order and frees table");
        check(abandoned.getRevenueCents() == 0 && abandoned.getUnhappyDepartures() == 1,
                "Abandoned order never contributes revenue");

        RestaurantController payment = new RestaurantController();
        Customer younger = new Customer(80, 4);
        Customer older = new Customer(81, 1);
        for (int i = 0; i < 2; i++) {
            Table table = payment.getTables().get(i);
            Customer customer = i == 0 ? younger : older;
            table.seatCustomer(customer);
            table.assignOrder(new Order(i + 1, burger, 1234));
            customer.setStatus(CustomerStatus.READY_TO_PAY);
        }
        end(payment);
        check(older.getStatus() == CustomerStatus.PAID && younger.getStatus() == CustomerStatus.READY_TO_PAY,
                "Cashier handles only oldest ready customer");
        check(payment.getRevenueCents() == 1234 && payment.getPaidCustomersCount() == 1,
                "Cashier pays at most one bill per turn");

        RestaurantController defeat = new RestaurantController();
        for (int turn = 1; turn <= 18; turn++) {
            check(!defeat.isGameOver(), "No early ending");
            end(defeat);
        }
        check(defeat.checkGameResult() == GameResult.DEFEAT && defeat.getRevenueCents() == 0, "18-turn defeat");
        check(defeat.getUnhappyDepartures() > 0, "Abandonment counted");
        int finishedTurn = defeat.getCurrentTurn();
        end(defeat);
        check(defeat.getCurrentTurn() == finishedTurn && !defeat.executeHostTask(1), "Game stops after 18 turns");

        RestaurantController victory = new RestaurantController();
        while (!victory.isGameOver()) {
            victory.startTurn(victory.getCurrentTurn() == 7);
            int hostTable = 0;
            if (!victory.getWaitingQueue().isEmpty()) {
                for (Table table : victory.getTables()) if (!table.isOccupied()) { hostTable = table.getTableId(); break; }
            }
            victory.executeHostTask(hostTable);
            int action = 0, target = 0;
            for (Table table : victory.getTables()) {
                if (table.getCurrentOrder() != null && table.getCurrentOrder().getStatus() == OrderStatus.READY) {
                    action = 3; target = table.getTableId(); break;
                }
            }
            if (action == 0) for (Table table : victory.getTables()) {
                if (table.isOccupied() && table.getCurrentOrder() == null) { action = 2; target = table.getTableId(); break; }
            }
            victory.executeWaiterTask(action, target);
            int chefOrder = 0;
            for (Table table : victory.getTables()) {
                Order candidate = table.getCurrentOrder();
                if (candidate != null && (candidate.getStatus() == OrderStatus.ORDERED || candidate.getStatus() == OrderStatus.PREPARING)) {
                    chefOrder = candidate.getOrderId(); break;
                }
            }
            victory.executeChefTask(chefOrder);
            System.out.printf("Replay turn %d: Host %d, Waiter %d/%d, Chef %d%n", victory.getCurrentTurn(), hostTable, action, target, chefOrder);
            end(victory);
        }
        System.out.printf("Replay: %s, paid %d, revenue %d cents, satisfaction %.1f%n", victory.checkGameResult(), victory.getPaidCustomersCount(), victory.getRevenueCents(), victory.getAverageSatisfaction());
        check(victory.checkGameResult() == GameResult.VICTORY, "Reproducible Stage 3 victory");
        System.out.println("Passed " + checks + " checks.");
    }
}
