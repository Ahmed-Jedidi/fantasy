package tn.isims.fantasy;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import tn.isims.fantasy.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    // To bind the activity_main.xml layout to the code via View Binding
    // binding: A reference to the View Binding class for activity_main.xml
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inflate the layout using view binding
        // To return a binding object that represents the root view and its child views
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        // Sets the activity's layout to the root view of the binding object
        setContentView(binding.getRoot());

        // Set the Toolbar as the ActionBar
        setSupportActionBar(binding.toolbar);

        // Set up the Firebase authentication instance
        auth = FirebaseAuth.getInstance();

        // get the reference to the ActionBar
        ActionBar actionBar = getSupportActionBar();

        // set the title of the ActionBar
        assert actionBar != null;
        actionBar.setTitle(R.string.bar_homepage);
    }


    // Inflate the menu layout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // Handle the back button press and logout button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Jedidi required more adjustements
        /*if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }*/
        if (item.getItemId() == R.id.action_logout) {
            performLogout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void performLogout() {
        auth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
}