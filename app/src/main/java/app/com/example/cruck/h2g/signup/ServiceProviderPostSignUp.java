package app.com.example.cruck.h2g.signup;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.HashMap;
import java.util.Map;

import app.com.example.cruck.h2g.R;
import app.com.example.cruck.h2g.firebaseconfig.Config;
import app.com.example.cruck.h2g.serviceprovider.ServiceProviderLanding;

public class ServiceProviderPostSignUp extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    final private int PERMISSION_ACCESS_COARSE_LOCATION = 123;
    protected EditText usernameEditText;
    protected EditText fullnameEditText;
    protected EditText servicenameEditText;
    protected EditText servicedescriptionEditText;
    protected EditText servicephoneEditText;
    protected Spinner categorySpinner;
    protected NumberPicker highwayNum;
    protected Button createButton;
    protected String latitudeSS;
    protected String longitudeSS;
    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_data);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.ACCESS_COARSE_LOCATION },
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        usernameEditText = (EditText) findViewById(R.id.usernamefield);
        fullnameEditText = (EditText) findViewById(R.id.fullnamefield);
        servicenameEditText = (EditText) findViewById(R.id.servicename);
        servicedescriptionEditText = (EditText) findViewById(R.id.servicedescription);
        servicephoneEditText = (EditText) findViewById(R.id.servicephone);
        categorySpinner = (Spinner) findViewById(R.id.Spinner01);
        highwayNum = (NumberPicker) findViewById(R.id.numberPicker);
        createButton = (Button) findViewById(R.id.createbutton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ServiceProviderPostSignUp.this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        highwayNum.setMinValue(1);
        highwayNum.setMaxValue(100);
        highwayNum.setWrapSelectorWheel(false);

        final Firebase ref = new Firebase(Config.FIREBASE_URL);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = usernameEditText.getText().toString().trim();
                final String fullname = fullnameEditText.getText().toString().trim();
                final String servicename = servicenameEditText.getText().toString().trim();
                final String servicedescription = servicedescriptionEditText.getText().toString().trim();
                final String servicephone = servicephoneEditText.getText().toString().trim();
                final String category = categorySpinner.getSelectedItem().toString();
                final String highwayNumber = Integer.toString(highwayNum.getValue());
                if(username.isEmpty() || fullname.isEmpty() || servicename.isEmpty() || servicedescription.isEmpty() || servicephone.isEmpty() || category.isEmpty() || highwayNumber.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ServiceProviderPostSignUp.this);
                    builder.setMessage(R.string.empty_message)
                            .setTitle(R.string.signup_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    String email = getIntent().getExtras().getString("email");
                    String password = getIntent().getExtras().getString("password");

                    final String emailAddress = email;
                    ref.authWithPassword(email, password, new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticated(AuthData authData) {
                            // Authenticated successfully with payload authData
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("email", emailAddress);
                            map.put("username", username);
                            map.put("name", fullname);
                            map.put("serviceName", servicename);
                            map.put("serviceDescription", servicedescription);
                            map.put("servicePhone", servicephone);
                            map.put("category", category);
                            map.put("highwayNumber", highwayNumber);
                            map.put("latitude", latitudeSS);
                            map.put("longitude", longitudeSS);
                            ref.child("services").child(authData.getUid()).setValue(map);

                            Intent intent = new Intent(ServiceProviderPostSignUp.this, ServiceProviderLanding.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            // Authenticated failed with error firebaseError
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ServiceProviderPostSignUp.this);
                            builder.setMessage(firebaseError.getMessage())
                                    .setTitle(R.string.login_error_title)
                                    .setPositiveButton(android.R.string.ok, null);
                            android.app.AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });

                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("location failed", "we entered this shit");
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("location", "we Connected!");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Log.d("location", "we are in!!!");
            latitudeSS = Double.toString(lastLocation.getLatitude());
            longitudeSS = Double.toString(lastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onConnectionSuspended(int i) {}


}

