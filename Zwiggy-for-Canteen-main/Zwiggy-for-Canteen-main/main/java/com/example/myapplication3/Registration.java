package com.example.myapplication3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;
public class Registration extends AppCompatActivity {

    private EditText sem,sps,srps,sroll;
    private Button sreg;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-application3-86e74-default-rtdb.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        auth=FirebaseAuth.getInstance();
        sem=(EditText) findViewById(R.id.sremail);
        sps=(EditText) findViewById(R.id.srpassword);
        srps=(EditText) findViewById(R.id.srrp);
        sroll=(EditText) findViewById(R.id.userroll);
        sreg=(Button) findViewById(R.id.srregister);

        sreg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String te=sem.getText().toString();
                String tp=sps.getText().toString();
                String trp=srps.getText().toString();
                String troll=sroll.getText().toString();
                if(TextUtils.isEmpty(troll) || TextUtils.isEmpty(te) || TextUtils.isEmpty(tp) || TextUtils.isEmpty(trp)){
                    Toast.makeText(Registration.this, "Credentials are Empty", Toast.LENGTH_SHORT).show();
                }
                else if(tp.length() < 6)
                {
                    Toast.makeText(Registration.this, "Password is too short", Toast.LENGTH_SHORT).show();
                }
                else if(!tp.equals(trp))
                {
                    Toast.makeText(Registration.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    String userId=user.getUid();
                    databaseReference.child("Student").child(userId).child("Email").setValue(te);
                    databaseReference.child("Student").child(userId).child("RollNumber").setValue(troll);
                    databaseReference.child("Student").child(userId).child("UId").setValue(userId);
                    regUser(te,tp);
                }
            }
        });
    }
    private void regUser(String email,String password)
    {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(Registration.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Registration.this,Student.class));

                }
                else
                {
                    Toast.makeText(Registration.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}