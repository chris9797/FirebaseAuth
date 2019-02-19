package com.example.firebaseauth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Patient extends AppCompatActivity implements View.OnClickListener {
    private String utype;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private TextView type;
    private Button buttonLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        buttonLogout = (Button) findViewById(R.id.buttonLogout);

        buttonLogout.setOnClickListener(this);
    }


    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot TypeSnapshot : dataSnapshot.getChildren()){

                    utype = TypeSnapshot.child(user.getUid()).getValue().toString();

                    utype = utype.replace("{type=","");

                    utype = utype.replace("}","");

                }

                type = (TextView) findViewById(R.id.type);
                type.setText("Welcome "+utype);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == buttonLogout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
