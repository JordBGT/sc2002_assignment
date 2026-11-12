import java.util.List;

public class CriticCustomer extends Customer{
    public CriticCustomer(int id, int arrivalTurn){
        super(id, arrivalTurn);
    }

    @Override
    public int getSatisfactionLoss(){
        return 12;
    }

    @Override
    public MenuItem orderFood(List<MenuItem> menu){
        if (menu == null || menu.isEmpty()){
            return null;
        }

        // Prefer the most expensive item above $10, keeping menu order for ties.
        MenuItem preferred = null;
        for (MenuItem item : menu) {
            if (item.getPriceCents() > 1000
                    && (preferred == null || item.getPriceCents() > preferred.getPriceCents())) {
                preferred = item;
            }
        }
        return preferred == null ? super.orderFood(menu) : preferred;
    }

    @Override
    public String getCustomerType(){
        return "Critic";
    }
}
