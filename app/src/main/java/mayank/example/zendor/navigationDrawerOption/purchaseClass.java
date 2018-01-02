package mayank.example.zendor.navigationDrawerOption;

/**
 * Created by mayank on 12/7/2017.
 */

public class purchaseClass {

    private String commodities;
    private String purchase_id;
    private String estimated_weight;
    private String actual_weight;
    private String rate;
    private String booked_ts;
    private String sellerName;
    private String booker;
    private String picker;
    private String picked;
    private String roc_b;
    private String roc_p;
    private String collected_ts;
    private String cancelled_ts;
    private String collected_weight;
    private String collected_by;
    private String cancelled_by;
    private String flag;

    public purchaseClass(String commodities, String purchase_id, String estimated_weight, String actual_weight, String rate, String booked_ts,
                         String sellerName, String booker, String picker, String picked, String roc_b, String roc_p, String collected_ts,
                         String cancelled_ts, String collected_weight, String collected_by, String cancelled_by, String flag){

        this.commodities = commodities;
        this.purchase_id = purchase_id;
        this.estimated_weight = estimated_weight;
        this.actual_weight = actual_weight;
        this.rate = rate;
        this.booked_ts = booked_ts;
        this.sellerName = sellerName;
        this.booker = booker;
        this.picker = picker;
        this.picked = picked;
        this.roc_b = roc_b;
        this.roc_p = roc_p;
        this.collected_ts = collected_ts;
        this.cancelled_by = cancelled_by;
        this.cancelled_ts = cancelled_ts;
        this.collected_weight = collected_weight;
        this.collected_by = collected_by;
        this.flag = flag;
    }

    public String getFlag() {
        return flag;
    }

    public String getRate() {
        return rate;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getBooked_ts() {
        return booked_ts;
    }

    public String getActual_weight() {
        return actual_weight;
    }

    public String getBooker() {
        return booker;
    }

    public String getCancelled_by() {
        return cancelled_by;
    }

    public String getCancelled_ts() {
        return cancelled_ts;
    }

    public String getCollected_by() {
        return collected_by;
    }

    public String getCollected_ts() {
        return collected_ts;
    }

    public String getCollected_weight() {
        return collected_weight;
    }

    public String getCommodities() {
        return commodities;
    }

    public String getEstimated_weight() {
        return estimated_weight;
    }

    public String getPicked() {
        return picked;
    }

    public String getPicker() {
        return picker;
    }

    public String getPurchase_id() {
        return purchase_id;
    }

    public String getRoc_b() {
        return roc_b;
    }

    public String getRoc_p() {
        return roc_p;
    }
}
