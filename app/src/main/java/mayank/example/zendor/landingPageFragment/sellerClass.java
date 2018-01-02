package mayank.example.zendor.landingPageFragment;

/**
 * Created by mayan on 10/31/2017.
 */

public class sellerClass {

    private String sellerId;
    private String name;
    private String address;
    private String last_purchase;
    private String number;

    public sellerClass(String sellerId, String name, String address, String last_purchase, String number){
        this.sellerId = sellerId;
        this.name = name;
        this.address = address;
        this.number = number;
        this.last_purchase = last_purchase;
    }

    public String getAddress() {
        return address;
    }

    public String getLast_purchase() {
        return last_purchase;
    }

    public String getName() {
        return name;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getNumber() {
        return number;
    }
}

