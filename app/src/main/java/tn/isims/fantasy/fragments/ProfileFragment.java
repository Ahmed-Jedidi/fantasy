package tn.isims.fantasy.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import tn.isims.fantasy.BmiActivity;
import tn.isims.fantasy.EditProfileActivity;
import tn.isims.fantasy.LocationActivity;
import tn.isims.fantasy.LoginActivity;
import tn.isims.fantasy.notification.NotifyActivity;
import tn.isims.fantasy.R;
import tn.isims.fantasy.databinding.FragmentProfileBinding;

import android.net.Uri;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
//
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public ProfileFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment ProfilFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static ProfileFragment newInstance(String param1, String param2) {
//        ProfileFragment fragment = new ProfileFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

    private FirebaseAuth auth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FragmentProfileBinding binding;

    private static final String CHANNEL_ID = "my_channel_id"; // Notification channel ID


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_profile, container, false);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        editProfile();
        notifyMe();
        trackBmi();
        emergency();
        reclamation();
        logout();

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

        getEmail();
        return binding.getRoot();
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



    private void getEmail() {
        // Set up the Firebase authentication instance
        auth = FirebaseAuth.getInstance();
        binding.emailText.setText(Objects.requireNonNull(auth.getCurrentUser()).getEmail().toString());
    }


    private void editProfile() {
        binding.editProfile.setOnClickListener(v -> startActivity(new Intent(getContext(), EditProfileActivity.class)));
    }

    private void notifyMe() {
        binding.notificationSwitch.setOnClickListener(v -> startActivity(new Intent(getContext(), NotifyActivity.class)));
    }
    private void trackBmi() {
        binding.trackBMI.setOnClickListener(v -> startActivity(new Intent(getContext(), BmiActivity.class)));
    }

    public Intent sendReclamationEmail(String userEmail, String userMessage) {
        // Define the recipient email address
        String recipient = "support@company.com"; // Replace with the actual recipient email

        // Define the subject of the email
        String subject = "Reclamation Request";

        // Compose the email body
        String emailBody = "User Email: " + userEmail + "\n\nMessage:\n" + userMessage;

        // Create an intent to send an email
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:")); // Only email apps should handle this
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);

        // Check if there's an email client available to handle the intent
        //if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(emailIntent, "Send email using..."));
        //} else {
            // Notify the user that no email client is available
            //Toast.makeText(this, "No email client found!", Toast.LENGTH_SHORT).show();
        //}
        return emailIntent;
    }


    private void reclamation() {
        binding.reclamation.setOnClickListener(v -> startActivity( sendReclamationEmail("ahmedjedidi16@gmail.com", "Reclamation")));
    }
    private void emergency() {
        binding.emergency.setOnClickListener(v -> startActivity(new Intent(getContext(), LocationActivity.class)));
    }

    private void logout() {
        binding.logoutAccount.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(requireContext())
                    .setTitle("Logout Confirmation")
                    .setMessage("Are you sure you want to log out? All unsaved changes will be lost.")
                    .setPositiveButton("Logout", (dialog12, which) -> {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.signOut();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    })
                    .setNegativeButton("No", (dialog1, which) -> dialog1.dismiss())
                    .setCancelable(false)
                    .show();

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.red));




        });
    }
}