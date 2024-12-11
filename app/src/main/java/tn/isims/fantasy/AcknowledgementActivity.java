package tn.isims.fantasy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AcknowledgementActivity extends AppCompatActivity {

    // Declare buttons for navigating to different activities
    Button acknowledgebackButton, stayintouchButton;

    TextView t1, t2, t3;
    String name, amount, contactnumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_acknowledgement);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referring the Views
        t1 = findViewById(R.id.textView12);
        t2 = findViewById(R.id.textView17);
        //t3 = findViewById(R.id.textView3);

        // Getting the Intent
        Intent i = getIntent();

        // Getting the Values from First Activity using the Intent
        name = i.getStringExtra("name");
        amount = i.getStringExtra("amount");
        //contactnumber = i.getStringExtra("contactnumber");

        // Setting the Values to TextViews
        t1.setText(name);
        t2.setText(amount + " DT");

        //linking to their XML IDs
        acknowledgebackButton = (Button) findViewById(R.id.acknowledgebackbtn);
        stayintouchButton = (Button) findViewById(R.id.stayintouchbtn);

        // Set up an OnClickListener for the 'Back' button
        acknowledgebackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to navigate back to the HomeActivity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent); // Start the HomeActivity
            }
        });

        // Set up an OnClickListener for the 'Stay in Touch' button
        stayintouchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create an Intent to navigate to the StayInTouchActivity
                Intent intent = new Intent(getApplicationContext(), StayInTouchActivity.class);
                startActivity(intent); // Start the StayInTouchActivity
            }
        });
    }
}