package mobile.jesus.com.banklocator.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jr02815 on 7/21/2017.
 */

public class Location {
    private double lat;
    private double lng;

    public Location(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Location(JSONObject location) {
        try {
            lat = Double.parseDouble(location.get("lat").toString());
        } catch (JSONException e) {
            e.printStackTrace();
            lat = 0;
        }

        try {
            lng = Double.parseDouble(location.get("lng").toString());
        } catch (JSONException e) {
            e.printStackTrace();
            lng = 0;
        }
    }

    public Location(){
        lat = 0;
        lng = 0;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
