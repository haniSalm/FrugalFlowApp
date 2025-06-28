package com.sdaproject.frugalflowapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class ActivityNotifications extends AppCompatActivity {

    private LinearLayout notificationsContainer;
    private DatabaseReference databaseReference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        Button backButton = findViewById(R.id.back_button);
        notificationsContainer = findViewById(R.id.notification_container);

        backButton.setOnClickListener(v -> finish());

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(userId).child("notifications");

        fetchNotifications();
    }

    private void fetchNotifications() {
        databaseReference.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                notificationsContainer.removeAllViews();

                for (DataSnapshot notifSnap : snapshot.getChildren()) {
                    String category = notifSnap.child("category").getValue(String.class);
                    String description = notifSnap.child("description").getValue(String.class);
                    Double amount = notifSnap.child("amount").getValue(Double.class);
                    Double remaining = notifSnap.child("remainingBalance").getValue(Double.class);

                    TextView notifText = new TextView(ActivityNotifications.this);

                    if ("Income".equalsIgnoreCase(category)) {
                        notifText.setText(
                                "Income Notification:\n" +
                                        description + "\n" +
                                        "Amount: ₹" + String.format(Locale.getDefault(), "%.2f", amount)
                        );
                    } else {
                        notifText.setText(
                                "Transaction in " + category + ":\n" +
                                        description + "\n" +
                                        "Amount: ₹" + amount + "\n" +
                                        "Remaining Balance: ₹" + remaining
                        );
                    }

                    notifText.setPadding(32, 24, 32, 24);
                    if ("Income".equalsIgnoreCase(category)) {
                        notifText.setBackgroundResource(R.drawable.notification_box_background_green); // green for income
                    } else {
                        notifText.setBackgroundResource(R.drawable.notification_box_background_red); // red for transactions
                    }

                    notifText.setTextColor(getResources().getColor(android.R.color.white));
                    notifText.setTextSize(16);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0, 0, 0, 24);
                    notifText.setLayoutParams(params);

                    notificationsContainer.addView(notifText);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Optional: Handle error
            }
        });
    }

}
