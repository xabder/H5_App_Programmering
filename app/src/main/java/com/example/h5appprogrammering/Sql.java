package com.example.h5appprogrammering;

import android.os.Build;

import com.example.h5appprogrammering.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;


public class Sql {

    private FirebaseFirestore db;

    public Sql() {
        initFirestore();
    }

    void initFirestore() {
        db = FirebaseFirestore.getInstance();
    }

    public void addEmail(String mail, String username) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        User user = User.getInstance();
        user.setEmail(mail);
        user.setName(username);

        db.collection("users")
                .document(uid)
                .set(user);
    }
    public void getUser(String mail) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        User user = task.getResult().toObject(User.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            User.getInstance().setName(user.getName());
                            User.getInstance().setEmail(user.getEmail());
                        }
                    }
                });
    }
}