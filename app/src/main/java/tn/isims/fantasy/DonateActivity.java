package tn.isims.fantasy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DonateActivity extends AppCompatActivity {

    // Declare EditText fields for user input
    EditText namedonatebtn, amountdonatebtn, contactnumberbtn;

    // Declare Button for donation action
    Button btndonate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_donate);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;

        });
            //linking to their XML IDs
            namedonatebtn = (EditText) findViewById(R.id.namedonate);
            amountdonatebtn = (EditText) findViewById(R.id.amountdonate);
            contactnumberbtn = (EditText) findViewById(R.id.contactnumberdonate);

            //linking to their XML IDs
            btndonate = (Button) findViewById(R.id.buttondonate);

            // Set an OnClickListener for the donate button
            btndonate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Retrieve user input from the EditText fields
                    String name = namedonatebtn.getText().toString();
                    String amount = amountdonatebtn.getText().toString();
                    String contactnumber = contactnumberbtn.getText().toString();

                    // Create an intent to navigate to the AcknowledgementActivity
                    Intent intent = new Intent(getApplicationContext(), AcknowledgementActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("amount", amount);
                    intent.putExtra("contactnumber", contactnumber);
                    startActivity(intent); // Start the AcknowledgementActivity

                }
        });
    }
}