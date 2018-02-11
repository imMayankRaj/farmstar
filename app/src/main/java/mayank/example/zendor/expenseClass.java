package mayank.example.zendor;

/**
 * Created by mayan on 1/8/2018.
 */

public class expenseClass{
    private String amount;
    private String rid;
    private String details;

    public expenseClass(String amount, String rid, String details){
        this.details = details;
        this.amount = amount;
        this.rid = rid;
    }

    public String getAmount() {
        return amount;
    }

    public String getDetails() {
        return details;
    }

    public String getRid() {
        return rid;
    }
}