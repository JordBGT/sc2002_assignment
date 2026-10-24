
# Restaurant Rush – Stage 2

Stage 2 extends the Restaurant Rush simulation by introducing two additional customer types, **VIP** and **Critic**. Each customer type has different satisfaction loss rates, ordering preferences, and pricing behaviour.

## 1. Changes Made to UML Diagram

### Changes to Existing Classes

The `Customer` class was extended with three new methods: `getSatisfactionLoss()`, `getDiscountedPrice()`, and `getCustomerType()`. These methods support customer-specific satisfaction loss rates, pricing adjustments, and customer type identification.

The `RestaurantController` class was updated to include the `generateCustomerTypes()` method, which generates customer types using seeded randomness.

### New Classes Added

Two new subclasses, `VIPCustomer` and `CriticCustomer`, were introduced to extend the existing `Customer` class.

The `VIPCustomer` class overrides the relevant methods to implement a satisfaction loss of **5 points per waiting turn** and a **10% discount** on food orders.

The `CriticCustomer` class overrides the relevant methods to implement a satisfaction loss of **12 points per waiting turn** and a distinct food-ordering preference.

### Relationships Added

Two generalization (inheritance) relationships were added to the UML diagram, connecting `VIPCustomer` and `CriticCustomer` to their superclass, `Customer`. These relationships demonstrate the use of inheritance and method overriding to implement customer-specific behaviours.

## 2. Changes Made to Code

### RestaurantCLI

The `RestaurantCLI` class was modified by updating the `promptChefAction()` and `displayTurnStart()` methods to display customer types. This allows players to distinguish between Regular, VIP, and Critic customers during gameplay.

### RestaurantController

The `RestaurantController` class was extended with the `generateCustomerTypes()` method to generate customer types using seeded randomness. The `checkAndProcessArrival()` and `executeTurn()` methods were also modified to support the arrival and processing of different customer types, including their respective satisfaction loss rates.

### Customer

The `Customer` class was updated with three additional methods: `getSatisfactionLoss()`, `getCustomerType()`, and `getDiscountedPrice()`. These methods provide default behaviour for Regular customers and can be overridden by subclasses to implement specialised behaviours.

## 3. Critic Customer Behaviour

Critic customers have a distinct food-ordering preference, prioritising menu items priced above **$10.00**.

Unlike Regular customers, Critic customers have a higher satisfaction loss rate of **12 points per waiting turn**.

## 4. Customer Type Generation

Customer types are generated using seeded randomness. A fixed random seed of `631` was selected to produce a reproducible customer arrival sequence:

**Regular → VIP → Regular → Critic → Regular → Regular**

### Customer Type Mapping for Reproducible Run

| Customer | Arrival Turn | Customer Type | Satisfaction Loss per Waiting Turn | Discount |
|----------|--------------|---------------|------------------------------------|----------|
| Customer #1 | 1 | Regular | 8 points | 0% |
| Customer #2 | 4 | VIP | 5 points | 10% |
| Customer #3 | 7 | Regular | 8 points | 0% |
| Customer #4 | 10 | Critic | 12 points | 0% |
| Customer #5 | 13 | Regular | 8 points | 0% |
| Customer #6 | 16 | Regular | 8 points | 0% |


## 5. Testing and Reproducible Runs



### Run 1: Victory Run

- **Objective:** Fulfill all targets (>= 4 paid customers, >= $45.00 revenue, >= 60.0 average satisfaction).
- **Customer Type Sequence (Seed 631):** Regular, VIP, Regular, Critic, Regular, Regular.
- **Special Behaviours:** VIP customers receive a 10% discount and lose 5 satisfaction points per waiting turn. Critic customers prioritise food priced above $10 and lose 12 satisfaction points per waiting turn.

| **Turn** | **Waiter Choice** | **Target Table** | **Chef Choice** | **Description** |
| -------- | ----------------- | ---------------- | --------------- | --------------- |
| **1** | `1` | `1` | (Auto-waits) | Seats Customer #1 (Regular) at Table 1. |
| **2** | `2` | `1` | `1` | Takes Order #1 (Pasta). Chef cooks 1/2 units. |
| **3** | `0` | — | `1` | Waiter skips. Chef finishes Order #1 (2/2 units). |
| **4** | `3` | `1` | (Auto-waits) | Serves Order #1 (Pasta). Customer #2 (VIP) arrives. |
| **5** | `1` | `2` | (Auto-waits) | Seats Customer #2 (VIP) at Table 2. |
| **6** | `2` | `2` | `2` | Takes Order #2 (Salad). Chef finishes cooking. Cashier collects $18.00 from Customer #1. |
| **7** | `3` | `2` | (Auto-waits) | Serves Order #2 to Customer #2 (VIP). Customer #3 (Regular) arrives. |
| **8** | `1` | `1` | (Auto-waits) | Seats Customer #3 (Regular) at Table 1. |
| **9** | `2` | `1` | `3` | Takes Order #3 (Salad). Chef finishes cooking. Cashier collects $8.10 from VIP Customer #2 (10% discount). |
| **10** | `3` | `1` | (Auto-waits) | Serves Order #3 to Customer #3. Customer #4 (Critic) arrives. |
| **11** | `1` | `2` | (Auto-waits) | Seats Customer #4 (Critic) at Table 2. |
| **12** | `2` | `2` | `4` | Takes Order #4 (Pasta) from Critic Customer #4. Chef cooks 1/2 units. Cashier collects $9.00 from Customer #3. |
| **13** | `1` | `1` | `4` | Seats Customer #5 (Regular) at Table 1. Chef finishes Order #4 (2/2 units). |
| **14** | `3` | `2` | (Auto-waits) | Serves Order #4 (Pasta) to Critic Customer #4. |
| **15** | `2` | `1` | `5` | Takes Order #5 (Pasta). Chef cooks 1/2 units. |
| **16** | `0` | — | `5` | Waiter skips. Chef finishes Order #5 (2/2 units). Cashier collects $18.00 from Critic Customer #4. Victory targets achieved. |
| **17** | `3` | `1` | (Auto-waits) | Serves Order #5 to Customer #5. |
| **18** | `1` | `2` | (Auto-waits) | Seats Customer #6 (Regular) at Table 2. Game ends with four paid customers. |

**Victory Output at Game Over:**

```text
========================================================
                       GAME OVER
========================================================
Final Result           : VICTORY
Total Revenue          : $53.10 (Target: >= $45.00)
Paid Customers Count   : 4 (Target: >= 4)
Average Satisfaction   : 72.3 (Target: >= 60.0)
========================================================
Congratulations! You met all manager targets!
```

### Run 2: Defeat Run

- **Objective:** Demonstrate a defeat outcome caused by delayed customer service, resulting in insufficient revenue and paid customers.
- **Customer Type Sequence (Seed 631):** Regular, VIP, Regular, Critic, Regular, Regular.
- **Defeat Conditions:** Revenue below $45.00 and fewer than 4 paid customers, despite maintaining an average satisfaction above 60.0.

| **Turn** | **Waiter Choice** | **Target Table** | **Chef Choice** | **Description** |
| -------- | ----------------- | ---------------- | --------------- | --------------- |
| **1** | `1` | `1` | (Auto-waits) | Seats Customer #1 (Regular) at Table 1. |
| **2** | `2` | `1` | `1` | Takes Order #1 (Pasta). Chef cooks 1/2 units. |
| **3** | `0` | — | `1` | Waiter skips. Chef finishes Order #1. |
| **4** | `3` | `1` | (Auto-waits) | Serves Order #1. Customer #2 (VIP) arrives. |
| **5** | `1` | `2` | (Auto-waits) | Seats Customer #2 (VIP) at Table 2. |
| **6** | `0` | — | (Auto-waits) | Waiter skips. Cashier collects $18.00 from Customer #1. |
| **7** | `0` | — | (Auto-waits) | Waiter skips. Customer #3 (Regular) arrives. |
| **8** | `1` | `1` | (Auto-waits) | Seats Customer #3 (Regular) at Table 1. |
| **9** | `0` | — | (Auto-waits) | Waiter skips. VIP Customer #2 continues waiting for an order. |
| **10** | `0` | — | (Auto-waits) | Waiter skips. Customer #4 (Critic) arrives. |
| **11** | `2` | `1` | `2` | Takes Order #2 (Salad) from Customer #3. Chef finishes cooking. |
| **12** | `3` | `1` | (Auto-waits) | Serves Order #2 to Customer #3. Critic Customer #4 continues waiting in the queue. |
| **13** | `0` | — | (Auto-waits) | Waiter skips. Customer #5 (Regular) arrives. |
| **14** | `0` | — | (Auto-waits) | Waiter skips. Cashier collects $9.00 from Customer #3. |
| **15** | `2` | `2` | `3` | Takes Order #3 (Salad) from VIP Customer #2. Chef finishes cooking. |
| **16** | `0` | — | (Auto-waits) | Waiter skips. Customer #6 (Regular) arrives. |
| **17** | `3` | `2` | (Auto-waits) | Serves Order #3 to VIP Customer #2. |
| **18** | `0` | — | (Auto-waits) | Waiter skips. VIP Customer #2 is still eating, so payment is not collected before the game ends. |

**Defeat Behaviour:**

1. All six customers arrive at their scheduled turns: 1, 4, 7, 10, 13, and 16.
2. Frequent skipped waiter actions delay order-taking and food service.
3. Different customer types experience different satisfaction losses:
    - Regular: 8 points per waiting turn.
    - VIP: 5 points per waiting turn.
    - Critic: 12 points per waiting turn.
4. Customer #1 pays \$18.00 at Turn 6, while Customer #3 pays $9.00 at Turn 14.
5. VIP Customer #2 receives food at Turn 17 but does not reach payment before the game ends.
6. Critic Customer #4 remains in the waiting queue and has not placed an order.
7. The restaurant earns only $27.00 from 2 paid customers, failing the revenue and customer-count targets.

**Defeat Output at Game Over:**

```text
========================================================
                       GAME OVER
========================================================
Final Result           : DEFEAT
Total Revenue          : $27.00 (Target: >= $45.00)
Paid Customers Count   : 2 (Target: >= 4)
Average Satisfaction   : 68.0 (Target: >= 60.0)
========================================================
Defeat! You did not meet all manager objectives.
```
