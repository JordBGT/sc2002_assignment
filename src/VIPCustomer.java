public class VIPCustomer extends Customer{
    public VIPCustomer(int customerId, int arrivalTurn){
        super(customerId, arrivalTurn);
    }

    @Override public int getSatisfactionLoss(){
        return 5;
    }

    @Override
    public int getDiscountedPrice(int priceCents) {
        return (int) Math.round(priceCents * 0.9);
    }

    @Override
    public String getCustomerType(){
        return "VIP";
    }
}
