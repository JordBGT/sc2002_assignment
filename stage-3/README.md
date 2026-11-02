# Stage 3 Restaurant Rush

Standalone Java implementation of Section 4.3, extending the Stage 2 source snapshot. Requires JDK 17 or later; no dependencies or network access.

## Build and play

Run from the repository root. Compile each stage separately because the stages intentionally contain classes with the same names.

```sh
mkdir -p stage-3/out
javac --release 17 -d stage-3/out stage-3/src/*.java
java -cp stage-3/out RestaurantCLI
```

At turn start, choose whether to activate Happy Hour (1 activates, 0 saves it). The state then displays its remaining active turns after applying any recovery. Next choose the Host's table (0 waits), the Waiter's task and table, and the Chef's order (0 waits). The Chef automatically waits when there are no cookable orders. Invalid choices explain the failure and preserve the staff task. Food choice belongs to the customer.

## Section 4.3 behaviour

| Requirement | Implementation |
| --- | --- |
| Host seats at most one queued customer in addition to Waiter | `Host extends Staff`, with its own action flag reset each turn; seats the first queued customer at the selected free table. Waiter seating remains available. |
| Combo consists of two existing items | `ComboMeal extends MenuItem`, retaining references to its two components; usable anywhere a menu item is accepted. Nested combos are rejected. |
| Combo preparation and price | Preparation is the sum; price is 90% of combined integer cents, rounded to the nearest cent (half cents round up). |
| Customer chooses combo | Every third arrival (customer IDs 3 and 6) chooses a combo; other arrivals choose individual dishes. See the deterministic rule below. |
| Happy Hour once at turn start for two turns | `HappyHourPolicy` records whether activation was used and the remaining duration. `startTurn` accepts activation only before that turn's staff actions; `endTurn` reduces duration. |
| Recovery on both active turns | After arrivals and activation, before staff choices, queued and seated unserved customers recover 15, capped at 100. This includes customers waiting on READY food. Served, eating, ready-to-pay, paid and abandoned customers receive no recovery. Normal end-of-turn decay still applies. |
| 20% off new orders, VIP first | The customer computes its discount, then the policy applies 20% off. Each percentage calculation rounds to the nearest cent. `Order` stores the final bill in an immutable field. |
| Existing orders unaffected | Cashier collects the stored bill. Neither activation nor expiry recalculates an order's price. |

The menu has the original Salad ($9, 1 unit), Burger ($15, 1 unit) and Pasta ($18, 2 units), plus:

| Combo | Price | Preparation units |
| --- | ---: | ---: |
| Salad + Burger | $21.60 | 2 |
| Salad + Pasta | $24.30 | 3 |
| Burger + Pasta | $29.70 | 3 |

A VIP Burger during Happy Hour costs $15 × 0.9 × 0.8 = $10.80. A VIP Salad + Pasta combo during Happy Hour costs $24.30 × 0.9 = $21.87, then $21.87 × 0.8 = $17.496, rounded to $17.50.

### Reproducible customer choice

Stage 2's seeded customer-type generator (seed 631) and six scheduled arrivals are retained. Customer behaviour remains polymorphic: Regular loses 8 per waiting turn, VIP loses 5 and receives 10% off, Critic loses 12 and prefers items priced above $10.

For food selection, Critic first filters the menu to items above $10 (falling back to the entire menu if necessary). Each customer then filters its eligible menu to combos if its ID is divisible by 3, otherwise individual dishes. If that group is empty, it uses all eligible dishes. It selects index `(id - 1) % eligibleGroupSize` in menu insertion order. This replaces shared/random food selection with a documented deterministic rule, including removing the Critic's previously unseeded randomness. It is reproducible across games in the same JVM as well as separate launches.

## Design and changes from Stage 2

Stage 1 and Stage 2 source files are preserved. Stage 3 copies the Stage 2 classes into its own source directory and makes these changes:

| Files | Responsibility and reason |
| --- | --- |
| New `Host.java` | Independent single-task seating role, reusing `Staff` turn tracking. |
| New `ComboMeal.java` | Menu subtype that composes two existing items and calculates its own price and preparation units. |
| New `HappyHourPolicy.java` | Owns activation, duration and pricing; avoids adding temporary discounts to mutable menu prices. |
| `RestaurantController.java` | Registers combos and Host; coordinates start-of-turn recovery and activation, Host tasks, policy expiry and locked billing; enforces Waiter before Chef, stops actions after turn 18, tracks served/departure totals. Customer-specific discounts and satisfaction loss remain dispatched through customer methods. |
| `Order.java` | Adds final bill cents and an order-time bill constructor; preserves the original constructor for ordinary undiscounted orders. |
| `Waiter.java` | Adds order-time bill overload and checks for a non-null READY order before serving. |
| `Customer.java` | Adds capped recovery limited to waiting statuses and deterministic combo selection. |
| `CriticCustomer.java` | Retains premium preference, then delegates deterministic selection to Customer. |
| `VIPCustomer.java` | Rounds the percentage discount to cents, including fractional-cent combo bills. |
| `MenuItem.java` | Makes item data immutable so combo component values remain stable. |
| `RestaurantCLI.java` | Collects activation and Host choices, shows menu, locked bills, remaining duration and required totals; handles invalid numeric input and end of input. No pricing or recovery rules in the UI. |
| Unchanged domain files | `Staff`, `Chef`, `Cashier`, `Table`, `CustomerStatus`, `OrderStatus`, `GameResult` retain the existing flow. |

The original preparation, serving, eating, automatic oldest-first payment, abandonment, six arrivals and 18-turn result rules remain. A serving turn ends in EATING, the following turn ends in READY_TO_PAY, and payment occurs on the next turn. A dish finished by Chef cannot be served until a later Waiter action next turn. The Cashier collects at most one bill per turn.

The Host acts before the Waiter in the CLI, allowing a newly seated customer to order in the same turn. This makes the new role useful while retaining the Waiter's single-task limit. Pricing uses one concrete policy object: an interface would add no useful variation for the one required policy. Order-time billing was chosen over recalculating discounts at payment because the latter would incorrectly change existing orders.

New support files: this README, `tests/Stage3Test.java`, and the two replay input files. Repository root README now points to this stage. Publishing a runnable commit/tag and inserting a real Stage 2 → 3 compare link remain repository submission steps; no commit identifiers or tags are fabricated here.

## Verification and reproducible runs

```sh
javac --release 17 -Xlint:all -d stage-3/out stage-3/src/*.java stage-3/tests/*.java
java -cp stage-3/out Stage3Test
java -cp stage-3/out RestaurantCLI < stage-3/tests/victory-input.txt
java -cp stage-3/out RestaurantCLI < stage-3/tests/defeat-input.txt
```

The regression runner performs 67 checks: combo composition, sums and rounding; customer/critic choice; independent Host and Waiter tasks; invalid actions; turn sequencing; both recovery turns, cap and eligibility; one-time activation and duration; existing-order protection; VIP-before-Happy-Hour pricing; locked payment after expiry; eating/payment timing; oldest-first single payment; cancellation and unpaid revenue exclusion; all 18 turns; and victory/defeat.

The victory input activates Happy Hour on turn 7. It seats with Host whenever a table is free, prioritizes READY serving over new orders, and cooks the first pending order. Its full action sequence is printed by the regression runner.

| Replay | Paid customers | Revenue | Average paid satisfaction | Served | Unhappy departures | Turns |
| --- | ---: | ---: | ---: | ---: | ---: | ---: |
| Victory | 5 | $79.26 | 83.8 | 5 | 0 | 18 |
| Defeat (all choices Wait, never activate) | 0 | $0.00 | N/A | 0 | 2 | 18 |

## UML diagrams

The [final class diagram and complete sequence diagram](diagrams/README.md) are available as PDF and SVG, with editable Graphviz and PlantUML sources. The sequence follows customer 3 through the supplied Happy Hour victory replay.
