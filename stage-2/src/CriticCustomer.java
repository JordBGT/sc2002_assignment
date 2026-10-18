import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

        List<MenuItem> preferredItems = new ArrayList<>();

        for(MenuItem item: menu){
            if (item.getPriceCents() > 1000){
                preferredItems.add(item);
            }
        }

        //In exception case where no items cost more than $10, use regular selection
        if(preferredItems.isEmpty()){
            return super.orderFood(menu);
        }

        Random rand = new Random();
        return preferredItems.get(rand.nextInt(preferredItems.size()));
    }

    @Override
    public String getCustomerType(){
        return "Critic";
    }
}
