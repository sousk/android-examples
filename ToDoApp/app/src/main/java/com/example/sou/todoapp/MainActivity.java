package com.example.sou.todoapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Get ListView object from xml
        final ListView listView = (ListView) findViewById(R.id.listView);

        // Create a new Adapter
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        final EditText text = (EditText) findViewById(R.id.todoText);
        final Button button = (Button) findViewById((R.id.addButton));

        // Use Firebase to populate the list
        Firebase.setAndroidContext(this);
        final Firebase fb = new Firebase("https://mercari-p-sou-todoapp.firebaseio.com/todoItems");

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fb.push().child("text").setValue(text.getText().toString());
            }
        });

        fb
                .addChildEventListener(new ChildEventListener() {
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        adapter.add((String)dataSnapshot.child("text").getValue());
                    }

                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        adapter.remove((String)dataSnapshot.child("text").getValue());
                    }

                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               fb.orderByChild("text")
                       .equalTo((String) listView.getItemAtPosition(position))
                       .addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(DataSnapshot dataSnapshot) {
                               if (dataSnapshot.hasChildren()) {
                                   DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                                   firstChild.getRef().removeValue();
                               }
                           }

                           @Override
                           public void onCancelled(FirebaseError firebaseError) {

                           }
                       });
           }
        });

    }
}
