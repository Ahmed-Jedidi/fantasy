package tn.isims.fantasy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.toolbox.JsonObjectRequest;

public class JoinActivity extends AppCompatActivity {

    // Declare EditText fields for name, email, and location
    EditText jnameedit, jemailedit, jlocationedit;

    // Declare CheckBox fields for different environmental issues and volunteer activities
    CheckBox climatechangecheckbox, plasticpollutioncheckbox, deforestationcheckbox, volunteercheckbox, attendcheckbox, hostcheckbox;

    // Declare Button for joining the organization
    Button joinbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //linking them to XML IDs
        jnameedit = (EditText) findViewById(R.id.jnameedt);
        jemailedit = (EditText) findViewById(R.id.jemailedt);
        jlocationedit = (EditText) findViewById(R.id.jlocationedt);

        //linking them to XML IDs
        climatechangecheckbox = (CheckBox) findViewById(R.id.climatechangeckb);
        plasticpollutioncheckbox = (CheckBox) findViewById(R.id.plasticpollutionckb);
        deforestationcheckbox = (CheckBox) findViewById(R.id.deforestationckb);

        //linking them to XML IDs
        volunteercheckbox = (CheckBox) findViewById(R.id.volunteerckb);
        attendcheckbox = (CheckBox) findViewById(R.id.attendckb);
        hostcheckbox = (CheckBox) findViewById(R.id.hostckb);

        //linking them to XML IDs
        joinbutton = (Button) findViewById(R.id.joinbtn1);

        // Set an OnClickListener for the Join button
        joinbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a Toast message thanking the user for joining and letting them know about qualification updates
                Toast.makeText(JoinActivity.this, "Thank you for joining! Stay tuned for updates on your qualification", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }


}