package mobile.jesus.com.banklocator.network;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jr02815 on 7/21/2017.
 */

public class QueryMapApi extends AsyncTask<String[], String, String> {
    private QueryCallback.OnTaskCompleted listener;
    public QueryMapApi(QueryCallback.OnTaskCompleted listener){
        this.listener = listener;
    }
    private HttpURLConnection httpClient;
    @Override protected String doInBackground(String[]... params) {
        String[] passed = params[0];
        StringBuilder resultResponse = new StringBuilder();
        try{
            String url  = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" +
                    passed[0] + "&location=" + passed[1] + "," + passed[2] + "&radius=10000&key=" +
                    passed[3];
            System.out.println(url);
            URL urlObj = new URL(url);


            httpClient = (HttpURLConnection) urlObj.openConnection();
            if (httpClient.getResponseCode() == HttpURLConnection.HTTP_OK) {{
                InputStream input = new BufferedInputStream(httpClient.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                String response;
                while((response = reader.readLine()) != null){
                    resultResponse.append(response);
                }
            }}

        }catch ( Exception e ){
            Log.d("BankLocator", e + "\nError searching for bank locations");
        }
        finally {
            httpClient.disconnect();
        }

        return resultResponse.toString();
    }

    @Override
    protected  void onPostExecute(String json){
        //callback to UI so we can parse
        listener.onTaskCompleted(json);
    }
}
