package com.geeky7.rohit.location.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.geeky7.rohit.location.Main;
import com.geeky7.rohit.location.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BackgroundService extends Service implements GoogleApiClient.OnConnectionFailedListener,
GoogleApiClient.ConnectionCallbacks,LocationListener{
    public static final String TAG = "Location";
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS/2;

    private static final int GOOGLE_API_CLIENT_ID = 0;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mlocationRequest;
    private Location mCurrentLocation;
    private boolean mRequestingLocationUpdates;
    private String mLastUpdateTime;

    static Context context;

    // current == 1 minute
    private int mInterval = 5000; // 5 seconds by default, can be changed later
    private Handler mHandler;

    public BackgroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Main.showToast(getApplicationContext(), "BackgroundService Created");
//        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        if(mGoogleApiClient.isConnected()&&mRequestingLocationUpdates)
            startLocationupdates();

        mHandler = new Handler();
        startRepeatingTask();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Main.showToast(getApplicationContext(), "BackgroundServiceDestroyed");

        stopSelf();
        stopService(new Intent(BackgroundService.this, Automatic.class));
        stopService(new Intent(BackgroundService.this,SemiAutomatic.class));
        stopService(new Intent(BackgroundService.this,Manual.class));

        if (mGoogleApiClient.isConnected())
            stopLocationupdates();
        mGoogleApiClient.disconnect();
        stopRepeatingTask();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(BackgroundService.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    private void createLocationRequest() {
        mlocationRequest = new LocationRequest();
        mlocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mlocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }
    protected void updateToast(){
        Main.showToast(getApplicationContext(), "New Coordinates: " + mCurrentLocation.getLatitude() + "\n" + mCurrentLocation.getLongitude());
    }
    protected void startLocationupdates(){
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mlocationRequest, this);
    }
    protected void stopLocationupdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mCurrentLocation==null){
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateToast();
        }
        if (mRequestingLocationUpdates)
            startLocationupdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateToast();
    }
    //Code for google places begins here:
    public StringBuilder sbMethod()
    {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean restaurant = sharedPrefs.getBoolean(getResources().getString(R.string.restaurant), false);
        boolean religious_place = sharedPrefs.getBoolean(getResources().getString(R.string.religious_place), false);
        boolean movie_theatre = sharedPrefs.getBoolean(getResources().getString(R.string.movie_theatre), false);
        String type = "";
        if (restaurant) type += "restaurant";
        type+="|";
        if (religious_place) type += "place_of_worship";
        type+="|";
        if (movie_theatre) type += "movie_theater";

        //-35.0161877,138.5439102 Westfield marion 50m
        double mLatitude = -34.923792;
        double mLongitude = 138.6047722;
        int mRadius = 50;
/*        mLatitude = mCurrentLocation.getLatitude();
        mLongitude = mCurrentLocation.getLongitude();*/
        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("location=" + mLatitude + "," + mLongitude);
        sb.append("&radius="+mRadius);
        sb.append("&types=" + type);
        sb.append("&sensor=true");
        sb.append("&key=AIzaSyC0ZdWHP1aun8cfHq9aXzOOztUaD1Fmw_I");
        Log.v("Places", sb.toString());
        return sb;
    }
    private String downloadUrl(String strUrl) throws IOException
    {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();

        } catch (Exception e) {
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        Context mContext;
        public ParserTask(Context context){
            mContext = context;
        }
        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            Place_JSON placeJson = new Place_JSON();

            try {
                jObject = new JSONObject(jsonData[0]);

                places = placeJson.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {

            for (int i = 0; i < list.size(); i++) {
                HashMap<String, String> hmPlace = list.get(0);
//                double lat = Double.parseDouble(hmPlace.get("lat"));
//                double lng = Double.parseDouble(hmPlace.get("lng"));
                final String name = hmPlace.get("place_name");
                String vicinity = hmPlace.get("vicinity");
            }
            HashMap<String, String> hmPlace = new HashMap<>();
            String name = "" ;
            if (list.size()>0){
                hmPlace = list.get(0);
                name = hmPlace.get("place_name");
            }
            else name = "";

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if (!name.equals("")){
                String rule = "AA";
                rule = sharedPrefs.getString("ThisIsARule", rule);
//                boolean restaurant = sharedPrefs.getBoolean(getResources().getString(R.string.restaurant), false);
//                Main.showToast(getApplicationContext(), "Restaurant: " + restaurant);
                Main.showToast(getApplicationContext(), "RuleName: " + rule);

                if (rule.equalsIgnoreCase("Automatic"))
                    startService(new Intent(BackgroundService.this,Automatic.class));
                if (rule.equalsIgnoreCase("SemiAutomatic"))
                    startService(new Intent(BackgroundService.this,SemiAutomatic.class));
                if (rule.equalsIgnoreCase("Manual"))
                    startService(new Intent(BackgroundService.this,Manual.class));
            }
            /*else
                stopService(new Intent(BackgroundService.this, Automatic.class));
                stopService(new Intent(BackgroundService.this,SemiAutomatic.class));
                stopService(new Intent(BackgroundService.this,Manual.class));*/

//            Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
        }
    }// end of the parserTask class

    public class Place_JSON {

        /**
         * Receives a JSONObject and returns a list
         */
        public List<HashMap<String, String>> parse(JSONObject jObject) {

            JSONArray jPlaces = null;
            try {
                /** Retrieves all the elements in the 'places' array */
                jPlaces = jObject.getJSONArray("results");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            /** Invoking getPlaces with the array of json object
             * where each json object represent a place
             */
            return getPlaces(jPlaces);
        }

        private List<HashMap<String, String>> getPlaces(JSONArray jPlaces) {
            int placesCount = jPlaces.length();
            List<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> place = null;

            /** Taking each place, parses and adds to list object */
            for (int i = 0; i < placesCount; i++) {
                try {
                    /** Call getPlace with place JSON object to parse the place */
                    place = getPlace((JSONObject) jPlaces.get(i));
                    placesList.add(place);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return placesList;
        }

        /**
         * Parsing the Place JSON object
         */
        private HashMap<String, String> getPlace(JSONObject jPlace)
        {

            HashMap<String, String> place = new HashMap<String, String>();
            String placeName = "-NA-";
            String vicinity = "-NA-";
            String latitude = "";
            String longitude = "";
            String reference = "";

            try {
                // Extracting Place name, if available
                if (!jPlace.isNull("name")) {
                    placeName = jPlace.getString("name");
                }

                // Extracting Place Vicinity, if available
                if (!jPlace.isNull("vicinity")) {
                    vicinity = jPlace.getString("vicinity");
                }

                latitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lat");
                longitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lng");
                reference = jPlace.getString("reference");

                place.put("place_name", placeName);
                place.put("vicinity", vicinity);
                place.put("lat", latitude);
                place.put("lng", longitude);
                place.put("reference", reference);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return place;
        }
    }

    public class PlacesTask extends AsyncTask<String, Integer, String>

    {
        String data = null;
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }
        @Override
        protected void onPostExecute(String result) {
            ParserTask parserTask = new ParserTask(context);
            parserTask.execute(result);
            Log.i("PlacesTask", result);
        }
    }
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                String sb = sbMethod().toString();
                new PlacesTask().execute(sb);
            } finally {
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }
}
