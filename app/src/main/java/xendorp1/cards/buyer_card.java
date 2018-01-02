package xendorp1.cards;

/**
 * Created by GOTHAM on 02-11-2017.
 */

public class buyer_card {
    String name,buyer_id;
    private String number;
    public buyer_card()
    {

    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setBuyer_id(String buyer_id) {
        this.buyer_id = buyer_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public String getBuyer_id() {
        return buyer_id;
    }

}
