package tn.isims.fantasy;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NotifyActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "my_channel_id"; // Notification channel ID
    Button notify; // Button for notifications
    EditText e; // EditText for input

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notify);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        // Initialize UI components
        notify = findViewById(R.id.button); // Find the button by ID
        e = findViewById(R.id.editText); // Find the EditText by ID

        // Create Notification Channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "My Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel); // Create the notification channel
        }

        // Request notification permission for Android 13 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1); // Request permission
            }
        }

        // Set OnClickListener for the notification button
        notify.setOnClickListener(v -> {
            Intent intent = new Intent(NotifyActivity.this, Notify2.class); // Create intent for SecondActivity
            PendingIntent pending = PendingIntent.getActivity(
                    NotifyActivity.this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE // Use FLAG_IMMUTABLE
            );

            // Build the notification
            Notification notification = new Notification.Builder(NotifyActivity.this, CHANNEL_ID)
                    .setContentTitle("New Message")
                    .setContentText(e.getText().toString()) // Get text from EditText
                    .setSmallIcon(R.mipmap.ic_launcher) // Set small icon for notification
                    .setContentIntent(pending) // Set the intent to be fired on click
                    .setAutoCancel(true) // Dismiss the notification on click
                    .build();

            // Get the NotificationManager and notify
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(0, notification); // Notify with ID 0
        });
    }
}