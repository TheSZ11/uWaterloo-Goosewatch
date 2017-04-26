package com.szaidi.goosewatch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.szaidi.goosewatch.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private OkHttpClient client = new OkHttpClient();
    private String url = "https://api.uwaterloo.ca/v2/resources/goosewatch.json?key=8e05f5225cf1b7cd2a0259afa51b6f59";
    private GoogleMap map;
    private SupportMapFragment mMapFragment;
    private final LatLng uWaterloo = new LatLng(43.47, -80.54);
    private Markers mMarkers;
    private ArrayList<Markers> arrayMarkers = new ArrayList<>();
    private ArrayList<MarkerOptions> markerOptionsArray = new ArrayList<>();
    private Double longitude;
    private Double latitude;
    private String location = "";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<String> locationArray = new ArrayList<>();


    @Override
         protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.tutorialMessage).create().show();

        try {
            post(url);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        final Button button = (Button) findViewById(R.id.button);
        button.setText("SHOW ME THE GEESE");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < markerOptionsArray.size(); i++) {
                    map.addMarker(markerOptionsArray.get(i));
                }
                mAdapter = new MyAdapter(locationArray);
                mRecyclerView.setAdapter(mAdapter);
                button.setVisibility(View.GONE);
            }

        });

        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.moveCamera((CameraUpdateFactory.newLatLngZoom(uWaterloo, 14)));
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    Call post(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        ResponseBody body = response.body();
                        String jsonData = body.string();
                        try {
                            JSONObject jsonObject = new JSONObject(jsonData);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            parseJsonArray(jsonArray);
                            fillMarkerOptions();
                        } catch (JSONException ex) {
                            //DO SOMETHING
                            ex.printStackTrace();
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        return call;
    }

    private void parseJsonArray(JSONArray jsonArray) {
        JSONObject object;
        Double longitude;
        Double latitude;
        String location;
        Markers newMarker;

        if (jsonArray.length() != 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    object = jsonArray.getJSONObject(i);
                    longitude = object.getDouble("longitude");
                    latitude = object.getDouble("latitude");
                    location = object.get("location").toString();

                    if(!TextUtils.isEmpty(location)){
                        newMarker = new Markers(longitude, latitude, location);
                        arrayMarkers.add(newMarker);
                    }
                } catch (JSONException e) {
                    //DO SOMETHING
                    e.printStackTrace();
                }
            }
        }
    }

    private void fillMarkerOptions(){
        for (int i = 0; i < arrayMarkers.size(); i++) {
            mMarkers = arrayMarkers.get(i);
            latitude = mMarkers.getLatitude();
            location = mMarkers.getLocation();
            longitude = mMarkers.getLongitude();
            MarkerOptions mMarkerOptions = new MarkerOptions().title(location).position(new LatLng(latitude, longitude));
            markerOptionsArray.add(mMarkerOptions);
            locationArray.add(location);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about) {
            new AlertDialog.Builder(this).setTitle("About").setMessage("This app was made using the University of Waterloo Open Data Api.").setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Do Something
                }
            }).show();
            return true;
        }
        if (id == R.id.settings) {
            //TODO SZ: put in Settings page
        }

        return super.onOptionsItemSelected(item);
    }
}
