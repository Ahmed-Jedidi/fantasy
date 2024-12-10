package tn.isims.fantasy;

import static com.google.android.gms.auth.api.signin.GoogleSignIn.getClient;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import tn.isims.fantasy.databinding.ActivitySignUpBinding;
import tn.isims.fantasy.utilities.Constants;
import tn.isims.fantasy.utilities.ReadWriteUser;

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
            String username = binding.inputUsername.getText().toString();
            String email = binding.emailEditText.getText().toString();
            String password = binding.passwordEditText.getText().toString();

            if (TextUtils.isEmpty(username)) {
                binding.inputUsername.setError(getString(R.string.error_empty_username));
                showToast(getString(R.string.error_empty_username));
            }
            // Check if email and password are not empty
            else if (TextUtils.isEmpty(email)) {
                binding.emailEditText.setError(getString(R.string.error_empty_email));
                showToast(getString(R.string.error_empty_email));
            }
            else if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailEditText.getText().toString()).matches()) {
                binding.emailEditText.setError(getString(R.string.error_invalid_email));
                showToast(getString(R.string.error_invalid_email));
            }
            else if (TextUtils.isEmpty(password)) {
                binding.passwordEditText.setError(getString(R.string.error_empty_password));
                showToast(getString(R.string.error_empty_password));
            }
            else if (password.length() < 6) {
                binding.passwordEditText.setError(getString(R.string.error_short_password));
                showToast(getString(R.string.error_short_password));
            }
            else if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && password.length() >= 6
                    && Patterns.EMAIL_ADDRESS.matcher(binding.emailEditText.getText().toString()).matches()) {
                isLoading(true);
                // Create a new user with email and password
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                hideTheKeyboard();
                                /////////////////////////////////////////////// Realtime DB
                                FirebaseUser firebaseUser = auth.getCurrentUser();

                                //Store userdata to firebase realtime database
                                ReadWriteUser writeUserDetails = new ReadWriteUser(binding.inputUsername.getText().toString());

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constants.USER_COLLECTION);
                                assert firebaseUser != null;
                                databaseReference.child(firebaseUser.getUid()).setValue(writeUserDetails)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()){
                                                isLoading(false);
                                                // showToast("Profile created successfully, please verify your email");

                                                // Send verification link
                                                firebaseUser.sendEmailVerification();
                                                showToast("Verification link send to your entered email address");
                                                isLoading(false);
                                                Toast.makeText(SignUpActivity.this, R.string.sign_up_success, Toast.LENGTH_SHORT).show();
                                                // Sign up successful, go to main activity
                                                navigateToMain();

                                            }else{
                                                isLoading(false);
                                                showToast("Something error occurred, please try again later");
                                            }
                                        });
                                /////////////////////////////////////////////// Firestore

                                FirebaseFirestore db = FirebaseFirestore.getInstance();

                                // Create a collection and add a document
                                Map<String, Object> user = new HashMap<>();
                                user.put("uid", firebaseUser.getUid().trim());
                                user.put("username", binding.inputUsername.getText().toString().trim());
                                user.put("email", binding.emailEditText.getText().toString().trim());
                                user.put("password", binding.passwordEditText.getText().toString());
                                user.put("role", "user");


                                //db.collection("users").add(user)
                                db.collection("users").document(firebaseUser.getUid().trim()).set(user)
                                        .addOnSuccessListener(documentReference -> {
                                            //Log.d("Firestore", "Document added with ID: " + documentReference.getId());
                                        })
                                        .addOnFailureListener(e -> {
                                            //Log.e("Firestore", "Error adding document", e);
                                        });


                                //////////////////////////////////////////////


                            } else {
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                }catch (FirebaseAuthUserCollisionException e){
                                    isLoading(false);
                                    showToast("Email is already in use, try again or re-enter");
                                } catch (FirebaseAuthWeakPasswordException e){
                                    isLoading(false);
                                    showToast("Password is too weak, try mixing of alphabets");
                                }catch (FirebaseAuthInvalidCredentialsException e){
                                    showToast("Email is invalid or already in use");
                                    isLoading(false);
                                } catch (Exception e){
                                    showToast(e.getMessage());
                                }
                                // Sign up failed, display error message
                                //Toast.makeText(SignUpActivity.this, R.string.sign_up_failed, Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                // Email or password is empty, display error message
                Toast.makeText(SignUpActivity.this, R.string.sign_up_enter_mail_password, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateToMain() {
        //Open main activity after successful register
        Intent iMainActivity = new Intent(SignUpActivity.this, MainActivity.class);
        iMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(iMainActivity);
        //startActivity(new Intent(SignUpActivity.this, MainActivity.class));
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


    private void hideTheKeyboard() {
        //Ensures the keyboard doesn’t block the screen or interfere
        View view = this.getCurrentFocus();
        InputMethodManager manager = (InputMethodManager) getSystemService(LoginActivity.INPUT_METHOD_SERVICE);
        assert view != null;
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void isLoading(boolean isLoading) {
        // Reference the ProgressBar
        ProgressBar progressBar = findViewById(R.id.progress_bar);

        if (isLoading) {
            binding.signUpButton.setVisibility(View.INVISIBLE);
            //binding.progressBar.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.signUpButton.setVisibility(View.VISIBLE);
            //binding.progressBar.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}