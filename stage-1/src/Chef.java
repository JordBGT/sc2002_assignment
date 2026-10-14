public class Chef extends Staff
{
    // Constructor
    public Chef(int id)
    {
        super(id);
    }


    // Methods
    public void cookOrder(Order order)
    {
        // If notActed and notOrderReady
        if(!hasActedThisTurn && !order.isReady() && order != null)
        {
            order.advancePrep();
            setActed(true);
        }
    }
}
