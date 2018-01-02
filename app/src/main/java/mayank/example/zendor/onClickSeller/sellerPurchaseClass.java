package mayank.example.zendor.onClickSeller;

/**
 * Created by mayank on 12/6/2017.
 */

public class sellerPurchaseClass {

    private String commodity;
    private String rate;
    private String weight;
    private String name;
    private String timestamp;
    private String flag;

    public sellerPurchaseClass(String commodity, String rate, String weight, String timestamp, String name, String flag){
        this.commodity = commodity;
        this.rate = rate;
        this.weight = weight;
        this.timestamp = timestamp;
        this.name = name;
        this.flag = flag;
    }

    public String getFlag() {
        return flag;
    }

    public String getWeight() {
        return weight;
    }

    public String getRate() {
        return rate;
    }

    public String getCommodity() {
        return commodity;
    }

    public String getName() {
        return name;
    }

    public String getTimestamp() {
        return timestamp;
    }

}

