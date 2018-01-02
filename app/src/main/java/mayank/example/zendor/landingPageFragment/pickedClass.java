package mayank.example.zendor.landingPageFragment;

/**
 * Created by mayank on 12/2/2017.
 */

public class pickedClass {

    private String commodity;
    private String pid;
    private String sellerName;
    private String zone;
    private String weight;
    private String rate;
    private String picked_ts;

    public pickedClass(String commodity, String pid, String sellerName, String zone, String weight, String rate, String picked_ts){

        this.commodity = commodity;
        this.pid = pid;
        this.sellerName = sellerName;
        this.zone = zone;
        this.weight = weight;
        this.rate = rate;
        this.picked_ts = picked_ts;
    }

    public String getCommodity() {
        return commodity;
    }

    public String getZone() {
        return zone;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getRate() {
        return rate;
    }

    public String getPid() {
        return pid;
    }

    public String getPicked_ts() {
        return picked_ts;
    }

    public String getWeight() {
        return weight;
    }
}
