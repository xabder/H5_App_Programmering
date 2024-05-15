package com.example.h5appprogrammering;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.h5appprogrammering.Models.Chat;
import com.example.h5appprogrammering.Models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatsActivity extends AppCompatActivity{

    AutoCompleteTextView searchField;
    private TextView loggedInAsTextView;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_chats);

        searchField = findViewById(R.id.searchField);

        loggedInAsTextView = findViewById(R.id.loggedInAsTextView);

        Button updateButton = findViewById(R.id.updateButton);

        updateButton.setOnClickListener(view -> getName());

        findViewById(R.id.newChatButton).setOnClickListener(view -> {
            String name = searchField.getText().toString();

            if (!name.isEmpty()) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users")
                        .whereEqualTo("name", name)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String id = document.getId();
                                    // Add chat here with the id
                                    Intent intent = new Intent(ChatsActivity.this, ChatActivity.class);
                                    startActivity(intent);

                                }
                            }
                        });
            }
        });

        getName();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        searchField.setAdapter(adapter);

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("Search", "Searching for: " + s.toString());
                updateList(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    public void getName() {
        String name = User.getInstance().getName();
        loggedInAsTextView.setText("Logged in as: " + name);
    }
    private void updateList(String text) {
        String currentUserUsername = User.getInstance().getName();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .orderBy("name")
                .startAt(text)
                .endAt(text + "\uf8ff")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> names = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            if (name != null && !name.equals(currentUserUsername)) {
                                names.add(name);
                            }
                        }
                        adapter.clear();
                        adapter.addAll(names);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

}

