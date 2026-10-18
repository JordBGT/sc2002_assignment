# SC2002 Restaurant Rush - Group Assignment

**Group ID:** SCEA_Grp5  
**Repository URL:** [Insert GitHub Repository URL here]  

---

## 1. Build and Run Instructions

This project is written in standard Java (JDK 17 or later) without external dependencies or internet access requirements.

### Using the Terminal
1. Open a terminal or command prompt and navigate to the directory containing the `.java` files.
2. Compile all source files:
   ```
   javac *.java
   ```

3.  Run the application: 
    ```
    java RestaurantCLI.java
    ```

### Using an IDE (IntelliJ IDEA / Eclipse / VS Code)

1.  Open or Import the project folder in your IDE.
2.  Locate to the main class `RestaurantCLI.java`
3.  Right-click RestaurantCLI.java and select Run 'RestaurantCLI.main()'.



## 2. Customer Food Ordering

- Implementation: Customer orders are determined using seeded randomness
### Order Mapping for Reproducible Run

| Customer | Ordered Item | Preparation Units | Price |
| :---: | :--- | :---: | :---: |
| **Customer #1** | Pasta | 2 units | $18.00 |
| **Customer #2** | Salad | 1 unit | $9.00 |
| **Customer #3** | Salad | 1 unit | $9.00 |
| **Customer #4** | Pasta | 2 units | $18.00 |
| **Customer #5** | Salad | 1 unit | $9.00 |
| **Customer #6** | Burger | 1 unit | $15.00 |



## 3. Stage 1 Walkthrough

### Run 1: Victory Run
- Objective: Fulfill all targets (>= 4 paid customers, >= $45.00 revenue, >= 60.0 average satisfaction).

| Turn   | Waiter Choice | Target Table | Chef Choice    | Description                                                                                                                           |
| :----: | :-----------: | :----------: | :------------: | :-------------------------------------------------------------------------------------------------------------------------------------|
| **1**  | `1`           | `1`          | (Auto-waits)   | Waiter seats Customer #1 at Table 1                                                                                                   |
| **2**  | `2`           | `1`          | `1`            | Waiter takes Order #1 (Pasta) at Table 1. Chef cooks Order #1 (1/2)                                                                   |
| **3**  | `0`           | —            | `1`            | Waiter waits. Chef finishes Order #1 (2/2 -> READY)                                                                                   |
| **4**  | `3`           | `1`          | (Auto-waits)   | Waiter serves Order #1 at Table 1 (SERVED)                                                                                            |
| **5**  | `1`           | `2`          | (Auto-waits)   | Waiter seats Customer #2 at Table 2. (Customer #1 finishes eating)                                                                    |
| **6**  | `2`           | `2`          | `2`            | Waiter takes Order #2 (Salad) at Table 2. Chef cooks Order #2. (Cashier collects $18.00 from Customer #1. Table 1 becomes vacant)     |
| **7**  | `3`           | `2`          | (Auto-waits)   | Waiter serves Order #2 at Table 2 (SERVED)                                                                                            |
| **8**  | `1`           | `1`          | (Auto-waits)   | Waiter seats Customer #3 at Table 1. (Customer #2 finishes eating)                                                                    |
| **9**  | `2`           | `1`          | `3`            | Waiter takes Order #3 (Salad) at Table 1. Chef cooks Order #3. (Cashier collects $9.00 from Customer #2. Table 2 becomes vacant)      |
| **10** | `3`           | `1`          | (Auto-waits)   | Waiter serves Order #3 at Table 1 (SERVED)                                                                                            |
| **11** | `1`           | `2`          | (Auto-waits)   | Waiter seats Customer #4 at Table 2. (Customer #3 finishes eating)                                                                    |
| **12** | `2`           | `2`          | `4`            | Waiter takes Order #4 (Pasta) at Table 2. Chef cooks Order #4 (1/2). (Cashier collects $9.00 from Customer #3. Table 1 becomes vacant)|
| **13** | `1`           | `1`          | `4`            | Waiter seats Customer #5 at Table 1. Chef finishes Order #4 (2/2 -\> READY).                                                          |
| **14** | `3`           | `2`          | (Auto-waits)   | Waiter serves Order #4 at Table 2 (SERVED)                                                                                            |
| **15** | `2`           | `1`          | `5`            | Waiter takes Order #5 (Salad) at Table 1. Chef cooks Order #5. (Cashier collects $18.00 from Customer #4. Table 2 becomes vacant)     |
| **16** | `3`           | `1`          | (Auto-waits)   | Waiter serves Order #5 at Table 1 (SERVED)                                                                                            |
| **17** | `1`           | `2`          | (Auto-waits)   | Waiter seats Customer #6 at Table 2 (Cashier collects $9.00 from Customer #5. Table 1 becomes vacant)                                 |
| **18** | `2`           | `2`          | `6`            | Waiter takes Order #6 (Burger) at Table 2. Chef cooks Order #6.                                                                       |

**Victory Output at Game Over:**
```
========================================================
                       GAME OVER                        
========================================================
Final Result           : VICTORY
Total Revenue          : $63.00 (Target: >= $45.00)
Paid Customers Count   : 5 (Target: >= 4)
Average Satisfaction   : 74.4 (Target: >= 60.0)
========================================================
Congratulations! You met all manager targets!
```

### Run 2: Defeat Run

  - Objective: Demonstrate a defeat outcome caused by player inaction leading to
    customer abandonment and zero revenue.


For all Turns 1 to 18, enter 0 (Wait / Skip) at every prompt:

  - Turns 1 - 18: Waiter Choice: 0 | Chef Choice: (Auto-waits or input 0)

**Defeat Behaviour:**

1.  Customers arrive at turns 1, 4, 7, 10, 13, and 16, joining the waiting
    queue.
2.  Because no customers are seated or served, every waiting customer suffers
    satisfaction decay (-8 points per turn).
3.  Starting at turn 14, customers reach 0 satisfaction and abandon the
    restaurant without paying.
4.  No orders are placed, no food is cooked, and no payments are collected.

**Defeat Output at Game Over:**
```
========================================================
                       GAME OVER                        
========================================================
Final Result           : DEFEAT
Total Revenue          : $0.00 (Target: >= $45.00)
Paid Customers Count   : 0 (Target: >= 4)
Average Satisfaction   : 0.0 (Target: >= 60.0)
========================================================
Defeat! You did not meet all manager objectives.
```


## 4. Stage 1 Class and Objects

  - Domain Entities:
      - Customer.java and CustomerStatus.java: Manages customer satisfaction,
        status transitions, and ordering
      - MenuItem.java: Immutable menu representation containing name, prep
        units, and price in integer cents
      - Order.java and OrderStatus.java: Tracks preparation progress and status
        transitions
      - Table.java: Manages table occupancy and holds active Customer and Order
        references
  - Staff Hierarchy:
      - Staff.java (abstract): Common parent, tracking employee ID and
        single-action-per-turn limit
      - Waiter.java: Dispatches seating, order-taking, and serving tasks
      - Chef.java: Advances preparation units on pending orders
      - Cashier.java: Automatically processes bill payments and frees tables
  - Control & Interface:
      - RestaurantController.java and GameResult.java: Controller managing the turn
        lifecycle (Steps 1-6), validation, and win/loss rules
      - RestaurantCLI.java: Text interface handling game rendering and user
        inputs 





