package de.peterloos.anothertodoapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, ChildEventListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseRef;
    private DatabaseReference itemsRef;
    private String userId;

    private EditText edittextNewItem;
    private Button buttonAdd;
    private ListView listviewItems;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        // retrieve UI controls
        this.edittextNewItem = (EditText) this.findViewById(R.id.todoText);
        this.buttonAdd = (Button) this.findViewById(R.id.addButton);
        this.listviewItems = (ListView) this.findViewById(R.id.listviewItems);

        // connect UI controls with event handlers / adapters
        this.buttonAdd.setOnClickListener(this);
        this.adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        this.listviewItems.setAdapter(adapter);

        // initialize Firebase Auth and RealTime Database
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseUser = this.firebaseAuth.getCurrentUser();
        this.databaseRef = FirebaseDatabase.getInstance().getReference();

        if (this.firebaseUser == null) {

            this.loadLogInView();  // not logged in, launch LogIn activity
        } else {

            // retrieve child node of realtime database
            this.userId = firebaseUser.getUid();
            this.itemsRef = this.databaseRef.child("users").child(this.userId).child("items");
            this.itemsRef.addChildEventListener(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // inflate the menu
        this.getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_logout) {
            this.firebaseAuth.signOut();
            this.loadLogInView();
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * implementation of interface 'View.OnClickListener'
     */

    @Override
    public void onClick(View view) {

        String input = this.edittextNewItem.getText().toString();
        if (input.isEmpty()) {
            Toast.makeText(MainActivity.this, R.string.error_empty_input, Toast.LENGTH_LONG).show();
            return;
        }

        DatabaseReference newChild = this.itemsRef.push().child("title");
        newChild.setValue(input);

        // clear UI
        this.edittextNewItem.setText("");
    }

    /*
     * implementation of interface 'ChildEventListener'
     */

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        DataSnapshot childSnapshot = dataSnapshot.child("title");
        String title = (String) childSnapshot.getValue();
        this.adapter.add(title);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

        DataSnapshot childSnapshot = dataSnapshot.child("title");
        String title = (String) childSnapshot.getValue();
        this.adapter.remove(title);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
    }

    // private helper methods
    private void loadLogInView() {

        Intent intent = new Intent(this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(intent);
    }
}
