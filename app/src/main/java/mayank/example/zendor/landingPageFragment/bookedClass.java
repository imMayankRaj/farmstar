package mayank.example.zendor.landingPageFragment;

/**
 * Created by mayank on 12/2/2017.
 */

public class bookedClass {

    private String pid;
    private String sellerName;
    private String zone;
    private String est_weight;
    private String rate;
    private String booked_ts;
    private String commodity;

    public bookedClass(String commodity, String pid, String sellerName, String zone, String est_weight, String rate, String booked_ts ){
        this.commodity = commodity;
        this.pid = pid;
        this.sellerName = sellerName;
        this.zone = zone;
        this.est_weight = est_weight;
        this.rate = rate;
        this.booked_ts = booked_ts;
        this.commodity = commodity;
    }

    public String getBooked_ts() {
        return booked_ts;
    }

    public String getEst_weight() {
        return est_weight;
    }

    public String getPid() {
        return pid;
    }

    public String getRate() {
        return rate;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getZone() {
        return zone;
    }

    public String getCommodity() {
        return commodity;
    }
}

