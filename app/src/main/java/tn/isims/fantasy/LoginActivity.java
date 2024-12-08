package tn.isims.fantasy;

import static com.google.android.gms.auth.api.signin.GoogleSignIn.getClient;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.jetbrains.annotations.Nullable;

import tn.isims.fantasy.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth auth;
    private GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN = 123;

    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    handleGoogleSignInResult(result.getData());
                } // else {Log.w("LoginActivity", "Google sign-in failed.");}
            });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Initialize view binding
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Hiding ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance();

        initGoogleSignIn();

        // Setup click listener for the login button
        binding.loginButton.setOnClickListener(view -> loginWithEmailPassword());
        binding.btnGoogle.setOnClickListener(v -> loginWithGoogle());
        // Go to login activity
        //binding.signUpTextview.setPaintFlags(binding.signUpTextview.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        binding.signUpTextview.setOnClickListener(view -> navigateToSignUp());
        // User is already signed in, start the Main Activity
        if (auth.getCurrentUser() != null) { navigateToMainActivity(); }
    }

    private void navigateToSignUp() {
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        finish();
    }

    private void navigateToMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish(); // close the Login Activity
    }

    private void loginWithEmailPassword() {
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            binding.emailEditText.setError(getString(R.string.error_empty_email));
        }
        if (TextUtils.isEmpty(password)) {
            binding.passwordEditText.setError(getString(R.string.error_empty_password));
        } else {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            // Sign in success, update UI with the signed-in user's information
                            navigateToMainActivity();
                            Toast.makeText(LoginActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
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
        // creates an Intent to launch Google's Sign-In activity
        Intent loginIntent = mGoogleSignInClient.getSignInIntent();
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
            Toast.makeText(this, R.string.google_login_failed + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        //Log.d("TAG", "signInWithCredential:success ");
                        navigateToMainActivity();
                        Toast.makeText(LoginActivity.this, R.string.google_sign_in_success, Toast.LENGTH_SHORT).show();
                    } else {
                        //Log.w("TAG", "LoginWithCredential:failure", task.getException());
                        Toast.makeText(this, R.string.google_sign_in_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}