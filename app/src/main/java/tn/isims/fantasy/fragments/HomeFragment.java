package tn.isims.fantasy.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import tn.isims.fantasy.DonateActivity;
import tn.isims.fantasy.EditProfileActivity;
import tn.isims.fantasy.JoinActivity;
import tn.isims.fantasy.databinding.FragmentHomeBinding;
import tn.isims.fantasy.databinding.FragmentProfileBinding;
import tn.isims.fantasy.notification.NotifyActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#//newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public HomeFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment HomeFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static HomeFragment newInstance(String param1, String param2) {
//        HomeFragment fragment = new HomeFragment();
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


    // Declare buttons for donate and join actions
    Button donate, join;
    private FragmentHomeBinding binding;

    private FirebaseAuth auth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_home, container, false);
        binding = FragmentHomeBinding.inflate(inflater, container, false);


        // Set an OnClickListener for the Donate button
        binding.btndonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When the donate button is clicked, navigate to DonateActivity
                Intent intent = new Intent(getContext(), DonateActivity.class);
                startActivity(intent); // Start DonateActivity

            }
        });

        // Set an OnClickListener for the Join button
        binding.btnjoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When the join button is clicked, navigate to JoinActivity
                Intent intent = new Intent(getContext(), JoinActivity.class);
                startActivity(intent); // Start JoinActivity


            }
        });


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
        editProfile();
        notifyMe();

        return binding.getRoot();

    }
    private void editProfile() {
        binding.linearLayout.setOnClickListener(v -> startActivity(new Intent(getContext(), EditProfileActivity.class)));
    }
    private void notifyMe() {
        binding.imageView2.setOnClickListener(v -> startActivity(new Intent(getContext(), NotifyActivity.class)));
    }

    private void getName(String name) {
        // Set up the Firebase authentication instance
        auth = FirebaseAuth.getInstance();
        binding.username.setText(Objects.requireNonNull(name));
    }

    private String getUidFireAuth() {
        // Set up the Firebase authentication instance
        auth = FirebaseAuth.getInstance();
        return Objects.requireNonNull(auth.getCurrentUser()).getUid();
    }
}