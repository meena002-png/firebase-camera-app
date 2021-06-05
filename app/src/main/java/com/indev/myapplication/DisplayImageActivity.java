package com.indev.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplayImageActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference mDatabaseReference;
    RecyclerviewAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<Member> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(DisplayImageActivity.this));

        list = new ArrayList<>();
        adapter = new RecyclerviewAdapter(list,this);
        recyclerView.setAdapter(adapter);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = firebaseDatabase.getReference();

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//
                    String name = postSnapshot.child("name").getValue().toString();
                    String link = postSnapshot.child("link").getValue().toString();
                    String cdate = postSnapshot.child("date").getValue().toString();

                    Member imgmember = new Member(name,link,cdate);
                    list.add(imgmember);
                    adapter.notifyDataSetChanged();
                    Log.d("result","retrive: "+link);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.d("result","failed");

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}