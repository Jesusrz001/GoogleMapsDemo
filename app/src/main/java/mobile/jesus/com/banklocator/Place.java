package mobile.jesus.com.banklocator;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jr02815 on 7/21/2017.
 */

class Place {
    private Location location;
    private String id;
    private String address;
    private String name;
    private double rating;
    private boolean isOpen = false;
    public Place(JSONObject place) {
            try {
                id = place.getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
                id = "0";
            }
        try {
            location = new Location(place.getJSONObject("geometry").getJSONObject("location"));
        } catch (JSONException e) {
            e.printStackTrace();
            location = new Location();
        }

        try {
            address = place.getString("formatted_address");
        } catch (JSONException e) {
            e.printStackTrace();
            address = "";
        }
        try {
            name = place.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
            name = "";
        }
        try {
            rating = place.getDouble("rating");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            isOpen = place.getJSONObject("opening_hours").getBoolean("open_now");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Place() {
        this.location = new Location();
        this.id = "";
        this.address = "";
        this.name = "";
        this.rating = 0;
        this.isOpen = false;
    }

    public Location getLocation() {
        return location;
    }

    public String getAddress() {
        return address;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getRating() {
        return rating;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public boolean isValid() {
        if (id.equalsIgnoreCase("")) {
            return false;
        }else {
            return true;
        }
    }
}
