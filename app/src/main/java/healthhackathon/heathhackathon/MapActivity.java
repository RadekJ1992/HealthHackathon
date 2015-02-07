package healthhackathon.heathhackathon;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
//import android.os.AsyncTask;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;


import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class MapActivity extends FragmentActivity implements LocationListener{

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    String SENDER_ID = "278851512630";

    static final String TAG = "GCMDemo";

    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;

    String regid;

    final Context context = this;

    private Double currentLatitude;
    private Double currentLongitude;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager locationManager;
    private Button helpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setUpMapIfNeeded();
    Log.d("TESTT", "Test logu");
        helpButton = (Button) findViewById(R.id.helpButton);

        regid = getRegistrationId(context);

        if (regid.isEmpty()) {
            registerInBackground();
        } else {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }
        }

        helpButton.setOnClickListener(new View.OnClickListener() {
            /* (non-Javadoc)
             * @see android.view.View.OnClickListener#onClick(android.view.View)
             */
            @Override
            public void onClick(View arg0) {
                Dialog dialog = new Dialog(MapActivity.this);
                dialog.setContentView(R.layout.popup_layout);
                dialog.setTitle("Wybierz typ zgłoszenia");

                Button loseButton = (Button) dialog.findViewById(R.id.loseButton);
                Button seizureButton = (Button) dialog.findViewById(R.id.seizureButton);
                Button accidentButton = (Button) dialog.findViewById(R.id.accidentButton);
                Button asthmaButton = (Button) dialog.findViewById(R.id.asthmaButton);

                loseButton.setOnClickListener(new View.OnClickListener() {
                    /* (non-Javadoc)
                     * @see android.view.View.OnClickListener#onClick(android.view.View)
                     */
                    @Override
                    public void onClick(View arg0) {
                        new AsyncTask<Void, Void, String>() {
                            @Override
                            protected String doInBackground(Void... params) {
                                String msg = "";
                                try {
                                    Bundle data = new Bundle();
                                    data.putString("my_message", "Hello World");
                                    // data.putString("my_message", "Hello World");
                                    // data.putString("my_action",
                                    //"com.google.android.gcm.demo.app.ECHO_NOW");
                                    String id = Integer.toString(msgId.incrementAndGet());
                                    gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
                                    msg = "Sent message";
                                    Log.d("TEST", "SendMessage " + id + " : " + data);

                                } catch (IOException ex) {
                                    msg = "Error :" + ex.getMessage();
                                }
                                return msg;
                            }

                            @Override
                            protected void onPostExecute(String msg) {
                                //Toast.makeText(MapActivity.this, "Test wysyłania", Toast.LENGTH_SHORT);
                            }
                        }.execute(null, null, null);
                    }
                });

                seizureButton.setOnClickListener(new View.OnClickListener() {
                    /* (non-Javadoc)
                     * @see android.view.View.OnClickListener#onClick(android.view.View)
                     */
                    @Override
                    public void onClick(View arg0) {
                        //TODO implementacja
                    }
                });

                accidentButton.setOnClickListener(new View.OnClickListener() {
                    /* (non-Javadoc)
                     * @see android.view.View.OnClickListener#onClick(android.view.View)
                     */
                    @Override
                    public void onClick(View arg0) {
                        //TODO implementacja
                    }
                });

                asthmaButton.setOnClickListener(new View.OnClickListener() {
                    /* (non-Javadoc)
                     * @see android.view.View.OnClickListener#onClick(android.view.View)
                     */
                    @Override
                    public void onClick(View arg0) {
                        //TODO implementacja
                    }
                });

                dialog.show();
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (LocationManager.NETWORK_PROVIDER != null) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);           
        }
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MapActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        double lat = 52.2297700;
        double lng = 21.0117800;
        CameraUpdate center= CameraUpdateFactory.newLatLng(new LatLng(lat, lng));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(14);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = (location.getLatitude());
        currentLongitude = (location.getLongitude());

        if (!mMap.isMyLocationEnabled()) { 
            mMap.setMyLocationEnabled(true);
        }
        mMap.clear();
        CameraUpdate center= CameraUpdateFactory.newLatLng(new LatLng(currentLatitude, currentLongitude));
        mMap.moveCamera(center);

        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .position(new LatLng(currentLatitude,currentLongitude)));
        //Toast.makeText(this, "Location update: " + lat + ", " + lng, Toast.LENGTH_SHORT).show();
    }

    /* (non-Javadoc)
     * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
     */
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
     */
    @Override
    public void onProviderEnabled(String provider) {
       // Toast.makeText(this, "Enabled new provider " + provider,Toast.LENGTH_SHORT).show();

    }

    /* (non-Javadoc)
     * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
     */
    @Override
    public void onProviderDisabled(String provider) {
     //   Toast.makeText(this, "Disabled provider " + provider,Toast.LENGTH_SHORT).show();
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                    Log.d("TEST", msg);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String s) {
                //super.onPostExecute(s);
            }
        }.execute(null, null, null);
    }

    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    public Double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public Double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(Double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }
}
