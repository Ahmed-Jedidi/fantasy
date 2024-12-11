package tn.isims.fantasy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StayInTouchActivity extends AppCompatActivity {


    // Declare EditText fields for user input (name and email)
    EditText sitnameedit, sitemailedit;
    //Declare Button for the subscription action
    Button subscribebutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stay_in_touch);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        // linking to their XML IDs
        sitnameedit = (EditText) findViewById(R.id.sitnameedt);
        sitemailedit = (EditText) findViewById(R.id.editTextTextEmailAddress);

        // linking to its XML ID
        subscribebutton = (Button) findViewById(R.id.subscribebtn);

        // Set an OnClickListener for the Join button
        subscribebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = sitnameedit.getText().toString().trim();
                String email = sitemailedit.getText().toString().trim();
                subscribeToNewsletter(name, email);
            }
        });

        subscribebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Show a toast message to notify the user of successful subscription
                Toast.makeText(StayInTouchActivity.this, "Subscription successful! Thank you for staying in touch.", Toast.LENGTH_SHORT).show();

                // Create an Intent to navigate to the HomeActivity after subscription
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void subscribeToNewsletter(String name, String email) {
        // Validate name and email
        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Name and Email are required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress (if applicable)
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Subscribing...");
        progressDialog.show();

        // Replace with your backend API URL
        String apiUrl = "https://example.com/api/newsletter/subscribe";

        // Create a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Prepare parameters for the POST request
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("email", email);

        // Create a POST request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, apiUrl, new JSONObject(params),
                response -> {
                    // Handle success
                    progressDialog.dismiss();
                    // Show a Toast message thanking the user for joining and letting them know about qualification updates
                    Toast.makeText(this, "Subscribed successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                },
                error -> {
                    // Handle error
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed to subscribe. Please try again.", Toast.LENGTH_SHORT).show();
                });

        // Add the request to the request queue
        requestQueue.add(request);
    }
}