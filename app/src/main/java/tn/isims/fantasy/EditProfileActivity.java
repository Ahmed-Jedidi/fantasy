package tn.isims.fantasy;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import tn.isims.fantasy.databinding.ActivityEditProfileBinding;
import tn.isims.fantasy.databinding.FragmentProfileBinding;

public class EditProfileActivity extends AppCompatActivity {


    private ActivityEditProfileBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        //setContentView(R.layout.activity_edit_profile);
        //ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
        //    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        //    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        //    return insets;
        //});
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backIcon.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        getEmail();


/////////////////////////////////////////////// Firestore username
        db.collection("users").document(getUidFireAuth()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            Log.d("Firestore", "Document data: " + document.getData());
                            if (document.contains("username")) {
                                getName(document.getString("username"));
                            }
                        } else {
                            Log.d("Firestore", "No such document");
                        }
                    } else {
                        Log.e("Firestore", "Error fetching document", task.getException());
                    }
                });
        ///////////////////////////////////////////////

    }

    private void getEmail() {
        // Set up the Firebase authentication instance
        auth = FirebaseAuth.getInstance();
        binding.emailText.setText(Objects.requireNonNull(auth.getCurrentUser()).getEmail().toString());
    }

    private void getName(String name) {
        // Set up the Firebase authentication instance
        auth = FirebaseAuth.getInstance();
        binding.nameText.setText(Objects.requireNonNull(name));
    }

    private String getUidFireAuth() {
        // Set up the Firebase authentication instance
        auth = FirebaseAuth.getInstance();
        return Objects.requireNonNull(auth.getCurrentUser()).getUid();
    }
}