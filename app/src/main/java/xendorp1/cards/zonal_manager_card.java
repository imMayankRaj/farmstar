package xendorp1.cards;

/**
 * Created by GOTHAM on 02-11-2017.
 */

public class zonal_manager_card {
    String name,username,password,zone_name,id,zid;
    int status;
    public zonal_manager_card()
    {}


    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setZone_name(String zone_name) {
        this.zone_name = zone_name;
    }

    public int getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getZone_name() {
        return zone_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setZid(String zid) {
        this.zid = zid;
    }

    public String getZid() {
        return zid;
    }
}
