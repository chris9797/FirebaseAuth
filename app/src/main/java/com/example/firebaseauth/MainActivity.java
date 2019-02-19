package com.example.firebaseauth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText   editTextPassword;
    private TextView textViewSignin;

    private DatabaseReference databaseReference;



    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    RadioGroup radioGroup;
    RadioButton radioButton;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        radioGroup = findViewById(R.id.id_radioGroup);

        textView = findViewById(R.id.id_Select);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() !=null){
            //profile activity here
            usertyp();
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("UserType");



        progressDialog = new ProgressDialog(this);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);

        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        textViewSignin = (TextView) findViewById(R.id.textViewSignin);

        buttonRegister.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);
    }

    private void registerUser() {
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

        progressDialog.setMessage("Registering user...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            saveusertype();
                            //user is successfully registered and logged in
                            //we will start profile acticity here
                            //only toast
                            if (firebaseAuth.getCurrentUser() != null) {
                                usertyp();
                            }
                        }else {
                            Toast.makeText(MainActivity.this, "Registeration Failed", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                }});

    }

    private void saveusertype(){
        int radioId = radioGroup.getCheckedRadioButtonId();

        radioButton = findViewById(radioId);

        String type = radioButton.getText().toString().trim();

        usertype usertype1 = new usertype(type);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        databaseReference.child(user.getUid()).setValue(usertype1);

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
        if(view == buttonRegister) {
            registerUser();

        }

        if(view == textViewSignin) {
            //will open login activity
            startActivity(new Intent(this,LoginActivity.class));
        }

    }
}

