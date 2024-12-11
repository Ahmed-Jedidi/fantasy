package tn.isims.fantasy;


import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;

public class LocationActivity extends AppCompatActivity {
    Button buttonSend;
    EditText textSMS;
    EditText textlon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_location);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Referencing the Views
        buttonSend = findViewById(R.id.buttonSend);
        textSMS = findViewById(R.id.editTextSMS);
        textlon = findViewById(R.id.textlon);

        // Check for location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not already granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        // Initialize Location Manager and Listener
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener mlocListener = new MyLocationListener();

        // Request location updates from GPS
        if (mlocManager != null) {
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

        }

        // Set up button to send SMS
        buttonSend.setOnClickListener(v -> sendEmergencySMS());
    }

    // Function to send emergency SMS with location
    private void sendEmergencySMS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 2);
            return;
        }

        String phoneNumber = "112"; // Replace with an actual emergency contact number
        String latitude = textSMS.getText().toString();
        String longitude = textlon.getText().toString();

        if (latitude.isEmpty() || longitude.isEmpty()) {
            Toast.makeText(this, "Location not available yet", Toast.LENGTH_SHORT).show();
            return;
        }

        String message = "Emergency! My current location is:\nLatitude: " + latitude + "\nLongitude: " + longitude;

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "Emergency SMS sent successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, re-request location updates
                LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                LocationListener mlocListener = new MyLocationListener();

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
                }
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Location permission is required for this app", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Inner class for handling location updates
    public class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            // Getting the latitude and longitude
            Double lat = loc.getLatitude();
            Double lon = loc.getLongitude();

            // Updating the EditText fields with the new location
            textSMS.setText(lat.toString());
            textlon.setText(lon.toString());
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Notify user if GPS is disabled
            Toast.makeText(getApplicationContext(), "GPS Disabled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Notify user if GPS is enabled
            Toast.makeText(getApplicationContext(), "GPS Enabled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // You can handle the provider status change here if needed
        }
    }
}