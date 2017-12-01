package xendorp1.cards;

/**
 * Created by GOTHAM on 29-10-2017.
 */

public class zone_card {
    private String zone_name;
    private String zone_id;
    public zone_card()
    {

    }
     public zone_card(String zone_id,String zone_name)
    {
        this.zone_id=zone_id;
        this.zone_name=zone_name;
    }

    public void setZone_id(String zone_id) {
        this.zone_id = zone_id;
    }

    public String getZone_name() {
        return zone_name;
    }

    public String getZone_id() {
        return zone_id;
    }

    public void setZone_name(String zone_name) {
        this.zone_name = zone_name;
    }
}
