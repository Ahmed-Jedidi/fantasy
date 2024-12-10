package tn.isims.fantasy;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

import tn.isims.fantasy.databinding.ActivityMainBinding;
import tn.isims.fantasy.fragments.CartFragment;
import tn.isims.fantasy.fragments.HomeFragment;
import tn.isims.fantasy.fragments.ProfileFragment;
import tn.isims.fantasy.fragments.SearchFragment;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    // To bind the activity_main.xml layout to the code via View Binding
    // binding: A reference to the View Binding class for activity_main.xml
    private ActivityMainBinding binding;

    private Fragment currentFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        //setContentView(R.layout.activity_main);
        //ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
        //    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        //    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        //    return insets;
        //});



        // Inflate the layout using view binding
        // To return a binding object that represents the root view and its child views
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        // Sets the activity's layout to the root view of the binding object
        setContentView(binding.getRoot());

//        //// Set the Toolbar as the ActionBar
//        setSupportActionBar(binding.toolbar);
//
//        //// get the reference to the ActionBar
//        ActionBar actionBar = getSupportActionBar();
//
//        //// set the title of the ActionBar
//        assert actionBar != null;
//        actionBar.setTitle(R.string.bar_homepage);


        if (savedInstanceState == null) {
            currentFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_view, currentFragment, HomeFragment.class.getSimpleName()).commit();
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.homeFragment) {
                replaceFragment(new HomeFragment());
            } else if (id == R.id.searchFragment) {
                replaceFragment(new SearchFragment());
            } else if (id == R.id.cartFragment) {
                replaceFragment(new CartFragment());
            } else if (id == R.id.profileFragment) {
                replaceFragment(new ProfileFragment());
            }
            return true;
        });

        // Set up the Firebase authentication instance
        //auth = FirebaseAuth.getInstance();
    }


//    // Inflate the menu layout
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }
//
//    // Handle the back button press and logout button
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Jedidi required more adjustements
//        /*if (item.getItemId() == android.R.id.home) {
//            onBackPressed();
//            return true;
//        }*/
//        if (item.getItemId() == R.id.action_logout) {
//            performLogout();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void performLogout() {
//        auth.signOut();
//        startActivity(new Intent(MainActivity.this, LoginActivity.class));
//        finish();
//    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        // Hide the current fragment if it exists
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }

        // Check if the fragment is already added
        String fragmentTag = fragment.getClass().getSimpleName();
        Fragment existingFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);

        if (existingFragment == null) {
            fragmentTransaction.add(R.id.fragment_view, fragment, fragmentTag);
        } else {
            fragmentTransaction.show(existingFragment);
            fragment = existingFragment;
        }

        currentFragment = fragment;
        fragmentTransaction.commit();
    }
}