# Stage 3 UML class diagram

The class diagram describes the current `stage-3/src` implementation. [PDF](Stage3_UMLClassDiagram.pdf)

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
