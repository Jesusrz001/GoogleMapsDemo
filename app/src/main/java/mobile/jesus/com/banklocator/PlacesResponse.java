package mobile.jesus.com.banklocator;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jr02815 on 7/21/2017.
 */

class PlacesResponse {
    private ArrayList<Place> places = new ArrayList<>();

    public PlacesResponse(String s) {
        try {
            JSONObject jsonPlaces = new JSONObject(s);
            JSONArray placesArray = jsonPlaces.getJSONArray("results");
            if(placesArray != null && placesArray.length() >0){
                for(int i = 0; i < placesArray.length(); i++){
                    places.add(new Place((JSONObject) placesArray.get(i)));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Place> getPlaces() {
        return places;
    }

    public Place getPlaceByID(String id){
        for(Place place : places){
            if(place.getId().equals(id)){
                return place;
            }
        }
        return new Place();
    }
}
