package com.manager.appjava.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.manager.appjava.R;
import com.manager.appjava.adapter.UserAdapter;
import com.manager.appjava.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    UserAdapter userAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initView();
        initToolbar();
        getUserFromFire();

    }

    private void getUserFromFire() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            List<User> userList = new ArrayList<>();
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                User user = new User();
                                user.setId(documentSnapshot.getLong("id").intValue());
                                user.setUsername(documentSnapshot.getString("username"));
                                userList.add(user);

                            }
                            if(userList.size()>0) {
                                userAdapter = new UserAdapter(getApplicationContext(), userList);
                                recyclerView.setAdapter(userAdapter);

                            }
                        }
                    }
                });
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycleview_user);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);


    }
}