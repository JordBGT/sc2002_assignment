# Stage 3 UML diagrams

These diagrams describe the current `stage-3/src` implementation and cover Section 5 of the assignment. They are diagram assets to insert into the group report.

| Diagram | Report copy | Vector image | Editable source |
| --- | --- | --- | --- |
| Final UML class diagram | [PDF](stage-3-class-diagram.pdf) | [SVG](stage-3-class-diagram.svg) | [Graphviz DOT](stage-3-class-diagram.dot) |
| Complete UML sequence diagram | [PDF, three continued sheets](stage-3-sequence-diagram.pdf) | [SVG, continuous view](stage-3-sequence-diagram.svg) | [PlantUML](stage-3-sequence-diagram.puml) |

The PDFs use large vector pages to preserve readable labels when zoomed. Insert SVGs when your report editor supports them, or insert PDF pages. Avoid squeezing the entire sequence onto a small portrait page. The three sequence sheets form one continued scenario, with the same lifeline order throughout.

## Class diagram coverage

- All 15 principal classes and the three enums, including CLI and controller.
- Selected key attributes, constructors and operations, with visibility and types. Routine controller getters and repeated inherited members are omitted for readability.
- Reference fields are represented by named association ends instead of duplicating them in attribute compartments.
- Staff and customer inheritance, and ComboMeal inheritance from MenuItem.
- Base-setting multiplicities: two tables, one of each staff role, one policy, six menu entries and up to six queued customers.
- Table customer/order associations and the order's single menu item.
- Two shared combo components, represented by a hollow aggregation diamond. These menu items exist independently of the combo, so exclusive lifetime composition would be misleading.
- Filled composition diamonds for controller-created staff, tables and policy.
- Notes on encapsulation, polymorphism, composition and notation.

## Sequence diagram scenario

The sequence follows customer 3 in `../tests/victory-input.txt`:

1. At the end of turn 6, `executeTurn()` advances the counter and processes the turn-7 arrival. Customer 3 is a Regular customer with satisfaction 100.
2. The manager activates Happy Hour on turn 7, uses Host to seat at Table 1, and uses Waiter to take the order in that same turn.
3. Customer 3 chooses Burger + Pasta Combo: 2970 cents and three preparation units. The policy calls the customer discount method first (Regular returns full price), then applies Happy Hour: 2376 cents. Order 3 locks this bill.
4. Chef prepares one unit on each of turns 7, 8 and 9. Happy Hour expires after turn 8. Satisfaction after turns 7, 8 and 9 is 92, 92 and 84 respectively; turn-8 recovery restores 92 to 100 before its decay.
5. On turn 10, Waiter serves the already READY order. Customer becomes EATING at turn end, READY_TO_PAY at the end of turn 11, and pays 2376 cents on turn 12. Satisfaction 84 is recorded and Table 1 is freed.
6. The game continues through turn 18, then checks all three objectives. The supplied replay ends in VICTORY: five paid customers, $79.26 revenue and 83.8 average satisfaction.

The manager decision is explicit: Host seating preserves the Waiter's task for same-turn ordering; assigning seating to Waiter would delay the order. Activating Happy Hour on turn 7 also changes the locked bill from $29.70 to $23.76 and enables recovery on turns 7 and 8.

Guards and combined fragments cover invalid-choice retries, active-turn recovery, preparation, oldest-first payment and the final result. Other customers' concurrent interactions, repeated getters and redundant PAID/clearTable assignments are intentionally abbreviated and identified in the diagram. The four staff reset messages are shown explicitly during the preparation loop; subsequent routine resets are abbreviated.

## Editing

Edit the `.dot` class source in a Graphviz-compatible editor and the `.puml` sequence source in a PlantUML-compatible editor. Both SVGs are also editable vector images. PlantUML may choose a different visual layout from the supplied PDF; the message source describes the same scenario. Update the diagrams whenever the Java design changes.
