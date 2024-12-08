package tn.isims.fantasy;

import static com.google.android.gms.auth.api.signin.GoogleSignIn.getClient;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import tn.isims.fantasy.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private FirebaseAuth auth;
    private GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN = 123;
    // ActivityResultLauncher for Google Sign-In
    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    handleGoogleSignInResult(result.getData());
                }
            });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Initialize view binding
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Hiding the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance();

        // Initialize Google Sign-In
        initGoogleSignIn();

        // Set up click listener for sign up button
        binding.signUpButton.setOnClickListener(view -> SignUpWithEmailPassword());

        if (auth.getCurrentUser() != null) {
            // User is already signed in, start the Main Activity
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            finish(); // close the SignUp Activity
        }

        // Set up click listener for already signed up text view
        binding.alreadySignedUpTextview.setOnClickListener(view -> navigateToLogin());

        // Handle Google Sign-Up
        binding.btnGoogle.setOnClickListener(v -> loginWithGoogle());
    }

    private void navigateToLogin() {
            // Go to login activity
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
    }

    private void SignUpWithEmailPassword() {
        {
            // Get email and password from edit texts
            String email = binding.emailEditText.getText().toString();
            String password = binding.passwordEditText.getText().toString();

            // Check if email and password are not empty
            if (TextUtils.isEmpty(email)) {
                binding.emailEditText.setError(getString(R.string.error_empty_email));
                return;
            }
            if (TextUtils.isEmpty(password)) {
                binding.passwordEditText.setError(getString(R.string.error_empty_password));
                return;
            }
            if (password.length() < 6) {
                binding.passwordEditText.setError(getString(R.string.error_short_password));
                return;
            }
            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && password.length() >= 6) {
                // Create a new user with email and password
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, R.string.sign_up_success, Toast.LENGTH_SHORT).show();
                                // Sign up successful, go to main activity
                                navigateToMain();
                            } else {
                                // Sign up failed, display error message
                                Toast.makeText(SignUpActivity.this, R.string.sign_up_failed, Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                // Email or password is empty, display error message
                Toast.makeText(SignUpActivity.this, R.string.sign_up_enter_mail_password, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateToMain() {
        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
        finish();
    }

    private void initGoogleSignIn() {
        // Initialize Google SignIn
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = getClient(this, gso);
    }

    private void loginWithGoogle() {
        Intent loginIntent = new Intent(mGoogleSignInClient.getSignInIntent());
        //startActivityForResult(LoginIntent, RC_SIGN_IN);
        googleSignInLauncher.launch(loginIntent);
    }

    private void handleGoogleSignInResult(Intent data) {
        try {
            GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data)
                    .getResult(ApiException.class);
            if (account != null) {
                firebaseAuthWithGoogle(account.getIdToken());
            }
        } catch (ApiException e) {
            Toast.makeText(this, R.string.google_sign_in_failed, Toast.LENGTH_SHORT).show();
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w("TAG", "Google Sign in failed", e);
            }
        }
    }*/


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "Login with Google successful", Toast.LENGTH_SHORT).show();
                        navigateToMain();
                        Toast.makeText(this, R.string.google_sign_in_success, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, R.string.google_sign_in_failed, Toast.LENGTH_SHORT).show();
                        //Log.w("TAG", "LoginWithCredential:failure", task.getException());
                    }

                });
    }
}