# Restaurant Rush - Stage 3

Stage 3 extends Stage 2 with **Host**, **ComboMeal** and **Happy Hour**. It retains Regular, VIP and Critic customers, the original restaurant rules and the same victory conditions. Each stage remains independently playable in its own directory.

**Group ID:** SCEA_Grp5

**Repository URL:** [JordBGT/sc2002_assignment](https://github.com/JordBGT/sc2002_assignment)

## 1. Changes Made to UML Diagram

### New Classes Added

- **Host** extends `Staff` and seats at most one queued customer per turn. Its task is independent of the Waiter's task.
- **ComboMeal** extends `MenuItem` and retains references to two existing individual menu items. It calculates its own combined preparation units and discounted price.
- **HappyHourPolicy** tracks one-time activation, the two-turn duration and the additional order discount.

### Changes to Existing Classes

`RestaurantController` now coordinates the Host, Happy Hour activation and recovery, and the expanded menu. `Order` has a final `billCents` field to preserve the price calculated when the order is taken. `Waiter` accepts this locked bill through an additional `takeOrder(...)` overload. `Customer` supports capped satisfaction recovery, and `RestaurantCLI` collects the new manager choices and displays their effects.

### Relationships Added

- Generalization: `Host` inherits from `Staff`; `ComboMeal` inherits from `MenuItem`.
- Composition: `RestaurantController` creates and owns its Host and Happy Hour policy, alongside the existing staff and tables.
- Shared aggregation: `ComboMeal` references two `MenuItem` components. The components remain independent menu entries and can be shared by several combos; the diagram therefore uses a hollow aggregation diamond rather than exclusive lifetime composition.
- The controller has four staff objects in total: one Host, one Waiter, one Chef and one Cashier.

The [final class diagram](diagrams/stage-3-class-diagram.pdf) includes Stage 1's core classes, Stage 2's customer subclasses and Stage 3's additions. The [sequence diagram](diagrams/stage-3-sequence-diagram.pdf) follows Customer #3 through Happy Hour ordering, preparation, serving, eating, payment and the final result check. It is one continued scenario across three sheets. [SVG images, editable sources and diagram notes](diagrams/README.md) are also included.

## 2. Changes Made to Code

Stage 3 copies the Stage 2 source into `stage-3/src` and extends those classes. It does not edit the Stage 1 or Stage 2 source directories.

### New Source Files

| File | Responsibility |
| --- | --- |
| `Host.java` | Seats one queued customer at a selected free table, using its own inherited staff action flag. |
| `ComboMeal.java` | Stores two component references; calculates the combo's price and preparation units. |
| `HappyHourPolicy.java` | Owns activation state, remaining duration and pricing after the customer's discount. |

### Existing Source Files Changed

| File | Change and reason |
| --- | --- |
| `RestaurantController.java` | Registers three combos and the Host; adds `startTurn(...)` and `executeHostTask(...)`; coordinates recovery and expiry; locks prices when ordering; removes the payment-time customer discount to prevent double discounting. Also enforces Waiter-before-Chef actions, blocks actions after game over, and counts served customers and unhappy departures. |
| `RestaurantCLI.java` | Adds Happy Hour and Host prompts; displays duration, available staff tasks, chosen food, locked bills and required end-of-turn statistics. Handles invalid numeric input and closes cleanly when input ends. |
| `Order.java` | Adds final `billCents` and an additional constructor accepting the locked bill. The original constructor remains available for undiscounted orders. |
| `Waiter.java` | Adds a `takeOrder(...)` overload receiving bill cents and checks that a non-null order has status READY before serving. |
| `Customer.java` | Adds `recoverSatisfaction(...)`, limited to QUEUED or SEATED customers and capped at 100; replaces shared random food selection with the deterministic combo-selection rule in Section 4. |
| `CriticCustomer.java` | Retains its preference for dishes above $10, then delegates to deterministic selection. This removes Stage 2's unseeded Critic randomness. |
| `VIPCustomer.java` | Rounds its 10% discount to the nearest cent, including fractional-cent bills introduced by combos. |
| `MenuItem.java` | Makes the name, price and preparation fields final so component values remain stable. |

Unchanged source files: `Staff.java`, `Chef.java`, `Cashier.java`, `Table.java`, `CustomerStatus.java`, `OrderStatus.java` and `GameResult.java`. No source class from Stage 2 was deleted from this version.

Supporting documentation includes this README and the diagram assets. The preparation, serving, eating, payment and abandonment flow is retained. New checks make the existing sequencing rules explicit in the controller, rather than relying only on CLI prompt order.

### Stage 2 to Stage 3 Git Evidence

- [Stage 3 implementation commit 52d3e9a](https://github.com/JordBGT/sc2002_assignment/commit/52d3e9aa2a6d0c99a020ce6f40194f5f70561f5c)
- [Compare Stage 2 snapshot with the Stage 3 implementation](https://github.com/JordBGT/sc2002_assignment/compare/b1499c3a3f66dad50f2f0d4fb858de7e402e7819...52d3e9aa2a6d0c99a020ce6f40194f5f70561f5c)

The assignment also requires the `stage-3` Git tag on a runnable commit. The implementation commit and comparison above do not replace that tag.

## 3. New Role and Menu Policy

### Host

The manager may assign the Host to seat the first queued customer at Table 1 or Table 2, or choose Wait. The Host acts before the Waiter in the CLI and can seat at most one customer per turn. Invalid targets do not consume its task.

The Waiter can still seat customers. Using the Host for seating leaves the Waiter free to take the newly seated customer's order in the same turn. The Chef may then prepare one unit of that new order.

### ComboMeal

Each combo contains two existing individual menu items:

- **Preparation units:** sum of the components' preparation units.
- **Price:** 90% of their combined price, rounded to the nearest cent; half cents round up.

| Menu Item | List Price | Preparation Units |
| --- | ---: | ---: |
| Salad | $9.00 | 1 |
| Burger | $15.00 | 1 |
| Pasta | $18.00 | 2 |
| Salad + Burger Combo | $21.60 | 2 |
| Salad + Pasta Combo | $24.30 | 3 |
| Burger + Pasta Combo | $29.70 | 3 |

For example, Salad + Pasta costs `($9.00 + $18.00) × 0.9 = $24.30` and requires `1 + 2 = 3` preparation units. Customers choose their own dishes; the manager does not select their food.

### Happy Hour

- May be activated **once per game**, at the start of a turn before staff actions.
- Lasts for the activation turn and the following turn; the display shows the remaining active turns. Activation on turn 18 only has an effect during turn 18 because the game then ends.
- At the start of both active turns, customers still waiting for a table or food recover **15 satisfaction**, capped at **100**. This includes customers waiting on READY food. Normal end-of-turn waiting decay still applies.
- Served, eating, ready-to-pay, paid and abandoned customers do not recover satisfaction.
- Orders taken during Happy Hour receive **20% off**, after any VIP discount.
- The price is locked in the order. Existing orders are unaffected by later activation, and discounted orders keep their price after Happy Hour expires.

Money is stored in integer cents and displayed with two decimal places. Customer and Happy Hour discounts are applied in order, each rounded to the nearest cent.

**VIP example:** Burger during Happy Hour costs `$15.00 × 0.9 × 0.8 = $10.80`.

**VIP combo example:** Salad + Pasta costs `$24.30 × 0.9 = $21.87`, then `$21.87 × 0.8 = $17.496`, rounded to **$17.50**.

## 4. Customer Behaviour and Food Ordering

Stage 2's customer-type generation uses seed `631` and retains this arrival sequence:

**Regular → VIP → Regular → Critic → Regular → Regular**

| Customer | Arrival Turn | Type | Satisfaction Loss per Waiting Turn | Customer Discount |
| --- | ---: | --- | ---: | ---: |
| #1 | 1 | Regular | 8 | 0% |
| #2 | 4 | VIP | 5 | 10% |
| #3 | 7 | Regular | 8 | 0% |
| #4 | 10 | Critic | 12 | 0% |
| #5 | 13 | Regular | 8 | 0% |
| #6 | 16 | Regular | 8 | 0% |

### Deterministic Combo Selection Rule

1. A Critic first filters the menu to items costing more than $10. If none qualify, it uses the full menu. Other customer types begin with the full menu.
2. Customers whose IDs are divisible by 3 choose from eligible combos. Other customers choose from eligible individual dishes.
3. If the requested group is empty, the customer falls back to all eligible items.
4. Within the chosen group, select index `(customerId - 1) % groupSize`, using menu insertion order.

This rule supports reproducible food choices in separate launches and multiple games in the same JVM. It replaces food-selection randomness while retaining the Critic's premium preference and polymorphic customer behaviour.

### Order Mapping for the Victory Run

| Customer | Chosen Item | Preparation Units | List Price | Locked Bill | Payment Turn |
| --- | --- | ---: | ---: | ---: | ---: |
| #1 Regular | Salad | 1 | $9.00 | $9.00 | 4 |
| #2 VIP | Burger | 1 | $15.00 | $13.50 | 7 |
| #3 Regular | Burger + Pasta Combo | 3 | $29.70 | $23.76 (Happy Hour) | 12 |
| #4 Critic | Pasta | 2 | $18.00 | $18.00 | 15 |
| #5 Regular | Burger | 1 | $15.00 | $15.00 | 17 |
| #6 Regular | Burger + Pasta Combo | 3 | $29.70 | $29.70 | Not paid within 18 turns |

## 5. Testing and Reproducible Runs

Both runs use the base setting: 18 turns, two tables and six scheduled arrivals. Victory requires **at least four paid customers, at least $45.00 revenue, and average paid-customer satisfaction of at least 60.0**. All three conditions must hold after turn 18; there is no early ending.

### Run 1: Victory Run

**Objective:** Meet all three targets while demonstrating Host seating, a combo, VIP pricing and Happy Hour.

**Strategy:** Activate Happy Hour on turn 7. Use Host to seat whenever a table is free, prioritise READY orders through Waiter, and cook the first pending order. Happy Hour is active on turns 7 and 8.

For the table below:

- **Host:** `1` or `2` is the table to seat at; `0` means Wait.
- **Waiter:** `1` seats, `2` takes an order, `3` serves, `0` waits.
- **Chef:** the order ID; when no order is cookable, the CLI automatically waits without requesting input.
- At the Happy Hour prompt, enter `0` on turns 1-6 and `1` on turn 7. No activation prompt appears after use.

| Turn | Host Table | Waiter Choice | Target Table | Chef Choice | Description |
| --- | --- | --- | --- | --- | --- |
| 1 | `1` | `2` | `1` | `1` | Host seats #1; Waiter takes Salad order; Chef finishes Order #1, making it READY. |
| 2 | `0` | `3` | `1` | Auto-waits | Serves #1; customer begins eating at turn end. |
| 3 | `0` | `0` | — | Auto-waits | #1 finishes eating and becomes READY_TO_PAY. |
| 4 | `2` | `2` | `2` | `2` | Host seats VIP #2; takes Burger order at $13.50; Chef finishes it. Cashier collects $9.00 from #1. |
| 5 | `0` | `3` | `2` | Auto-waits | Serves VIP #2; customer begins eating. |
| 6 | `0` | `0` | — | Auto-waits | VIP #2 becomes READY_TO_PAY. |
| 7 | `1` | `2` | `1` | `3` | Activates Happy Hour. Host seats #3; combo bill locks at $23.76. Chef cooks unit 1/3; Cashier collects $13.50 from VIP #2. |
| 8 | `0` | `0` | — | `3` | Second active turn: #3 recovers from 92 to 100 before decay. Chef cooks unit 2/3; Happy Hour expires at turn end. |
| 9 | `0` | `0` | — | `3` | Chef completes unit 3/3; Order #3 becomes READY. It cannot be served this turn. |
| 10 | `2` | `3` | `1` | Auto-waits | Host seats Critic #4; Waiter prioritises serving #3's combo. |
| 11 | `0` | `2` | `2` | `4` | Critic #4 chooses Pasta; Chef cooks unit 1/2. #3 becomes READY_TO_PAY. |
| 12 | `0` | `0` | — | `4` | Chef finishes Pasta. Cashier collects #3's locked $23.76 bill after Happy Hour has expired. |
| 13 | `1` | `3` | `2` | Auto-waits | Host seats #5 while Waiter serves Critic #4. |
| 14 | `0` | `2` | `1` | `5` | Takes #5's Burger order; Chef finishes it. Critic #4 becomes READY_TO_PAY. |
| 15 | `0` | `3` | `1` | Auto-waits | Serves #5. Cashier collects $18.00 from Critic #4; four customers have paid. |
| 16 | `2` | `2` | `2` | `6` | Host seats #6; takes a combo order; Chef cooks unit 1/3. #5 becomes READY_TO_PAY. |
| 17 | `0` | `0` | — | `6` | Chef cooks unit 2/3. Cashier collects $15.00 from #5. |
| 18 | `0` | `0` | — | `6` | Chef completes #6's combo; it is still unpaid. The game ends and checks the targets. |

**End-of-turn 18 summary and final output:**

```text
Served: 5 | Unhappy departures: 0 | Turns used: 18 | Paid avg: 83.8
End of Turn 18 State: Revenue: $79.26 | Paid Customers: 5

========================================================
                       GAME OVER
========================================================
Final Result           : VICTORY
Total Revenue          : $79.26 (Target: >= $45.00)
Paid Customers Count   : 5 (Target: >= 4)
Average Satisfaction   : 83.8 (Target: >= 60.0)
========================================================
Congratulations! You met all manager targets!
```

The paid satisfaction scores are 92, 95, 84, 64 and 84: `(92 + 95 + 84 + 64 + 84) / 5 = 83.8`. Revenue is `$9.00 + $13.50 + $23.76 + $18.00 + $15.00 = $79.26`.

### Run 2: Defeat Run

**Objective:** Demonstrate abandonment and defeat when the manager does not serve anyone.

For all turns 1-18, enter `0` at the Happy Hour, Host and Waiter prompts. Happy Hour is never activated, nobody is seated, and Chef automatically waits. This is three zero inputs per turn.

| Turn | Happy Hour Choice | Host Table | Waiter Choice | Target Table | Chef Choice | Description |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | `0` | `0` | `0` | — | Auto-waits | Regular #1 arrives. All staff wait; satisfaction falls from 100 to 92. |
| 2 | `0` | `0` | `0` | — | Auto-waits | All remaining queued customers lose satisfaction. No orders, service or payments occur. |
| 3 | `0` | `0` | `0` | — | Auto-waits | All remaining queued customers lose satisfaction. No orders, service or payments occur. |
| 4 | `0` | `0` | `0` | — | Auto-waits | VIP #2 arrives. Both customers remain queued. |
| 5 | `0` | `0` | `0` | — | Auto-waits | All remaining queued customers lose satisfaction. No orders, service or payments occur. |
| 6 | `0` | `0` | `0` | — | Auto-waits | All remaining queued customers lose satisfaction. No orders, service or payments occur. |
| 7 | `0` | `0` | `0` | — | Auto-waits | Regular #3 arrives. Nobody is seated or served. |
| 8 | `0` | `0` | `0` | — | Auto-waits | All remaining queued customers lose satisfaction. No orders, service or payments occur. |
| 9 | `0` | `0` | `0` | — | Auto-waits | All remaining queued customers lose satisfaction. No orders, service or payments occur. |
| 10 | `0` | `0` | `0` | — | Auto-waits | Critic #4 arrives. Its satisfaction decreases by 12 per waiting turn. |
| 11 | `0` | `0` | `0` | — | Auto-waits | All remaining queued customers lose satisfaction. No orders, service or payments occur. |
| 12 | `0` | `0` | `0` | — | Auto-waits | All remaining queued customers lose satisfaction. No orders, service or payments occur. |
| 13 | `0` | `0` | `0` | — | Auto-waits | Regular #5 arrives. #1 reaches 0 satisfaction and abandons at turn end. |
| 14 | `0` | `0` | `0` | — | Auto-waits | All remaining queued customers lose satisfaction. No orders, service or payments occur. |
| 15 | `0` | `0` | `0` | — | Auto-waits | All remaining queued customers lose satisfaction. No orders, service or payments occur. |
| 16 | `0` | `0` | `0` | — | Auto-waits | Regular #6 arrives. All customers remain unserved. |
| 17 | `0` | `0` | `0` | — | Auto-waits | All remaining queued customers lose satisfaction. No orders, service or payments occur. |
| 18 | `0` | `0` | `0` | — | Auto-waits | Critic #4 reaches 0 and abandons at turn end. The game ends in DEFEAT with two unhappy departures. |

**Defeat behaviour:**

1. All six customers arrive on schedule. Regular, VIP and Critic customers lose 8, 5 and 12 satisfaction per waiting turn respectively.
2. Regular Customer #1 abandons at the end of turn 13. Critic Customer #4 abandons at the end of turn 18. Customer #3 is still waiting with satisfaction 4 when the game ends.
3. The two actual departures are #1 and #4. No orders or payments occur, so revenue and paid-customer count remain zero.
4. The average is displayed as `N/A`, because no customer has paid.

**End-of-turn 18 summary and final output:**

```text
Served: 0 | Unhappy departures: 2 | Turns used: 18 | Paid avg: N/A
End of Turn 18 State: Revenue: $0.00 | Paid Customers: 0

========================================================
                       GAME OVER
========================================================
Final Result           : DEFEAT
Total Revenue          : $0.00 (Target: >= $45.00)
Paid Customers Count   : 0 (Target: >= 4)
Average Satisfaction   : N/A (Target: >= 60.0)
========================================================
Defeat! You did not meet all manager objectives.
```

## 6. Build and Run Instructions

Requires **JDK 17 or later**. No external libraries or network access are needed. Compile stages separately because their classes intentionally have the same names.

### Using the Terminal

1. Open a terminal and navigate to the Stage 3 source folder, `stage-3/src`, which contains the `.java` files. From the repository root:

   ```sh
   cd stage-3/src
   ```

2. Compile all source files:

   ```sh
   javac *.java
   ```

3. Run the application:

   ```sh
   java RestaurantCLI
   ```

The launch command runs the compiled `RestaurantCLI` class. Enter the manager choices from the walkthrough tables above to reproduce either ending.

### Using an IDE (IntelliJ IDEA / Eclipse / VS Code)

1. Open or import the project folder.
2. Configure `stage-3/src` as the source directory. Keep other stages out of the same source set to avoid duplicate class names.
3. Locate `RestaurantCLI.java` and run `RestaurantCLI.main()`.

## 7. Design Discussion

- **Encapsulation:** Customer owns satisfaction changes, HappyHourPolicy owns its duration and discount calculation, and Order keeps its final bill private and final. Controller collection getters return unmodifiable views.
- **Inheritance and polymorphism:** Host reuses Staff's task tracking. ComboMeal can be used wherever MenuItem is accepted. Customer-specific losses, discounts and preferences are dispatched through overridden methods.
- **Responsibilities:** CLI collects decisions and displays state; controller coordinates turns; staff perform tasks; customer and policy objects supply their own behaviour.
- **Alternative considered:** Recalculating discounts at payment would avoid storing a bill, but would incorrectly change orders placed before activation or paid after expiry. Order-time billing preserves the required price lock.
- **Policy trade-off:** A separate pricing interface was not introduced because only one optional pricing policy is required. The concrete policy keeps its rules separate without adding an unused abstraction.
- **Manager impact:** Host seating can allow ordering and cooking on the arrival turn. Waiter and Chef priorities determine how long customers wait. Happy Hour timing changes both waiting satisfaction and the bills of newly placed orders.
