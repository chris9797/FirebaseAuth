package com.example.firebaseauth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignUp;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() !=null){
            //profile activity here
            finish();
            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        }

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        textViewSignUp = (TextView) findViewById(R.id.textViewSignUp);

        progressDialog = new ProgressDialog(this);

        buttonSignIn.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);



    }


    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();


        if (TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            //password is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            //stopping execution from going further
            return;
        }

        //iif validation ok
        //show progress bar

        progressDialog.setMessage("Logging in user...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {

                            //start profile activity
                            finish();
                            usertyp();
                            Toast.makeText(LoginActivity.this,"Login Successful...", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(LoginActivity.this,"Login Failed...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void usertyp() {
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot TypeSnapshot : dataSnapshot.getChildren()){

                    String utype = TypeSnapshot.child(user.getUid()).getValue().toString();

                    utype = utype.replace("{type=","");

                    utype = utype.replace("}","");

                    if(utype.equals("Patient")) {
                        startActivity(new Intent(getApplicationContext(), Patient.class));
                    }
                    else {
                        startActivity(new Intent(getApplicationContext(), Doctor.class));
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onClick(View view) {
        if(view == buttonSignIn){
            userLogin();

        }

        if(view == textViewSignUp){
            finish();
            startActivity(new Intent(this,MainActivity.class));
        }
    }
}
