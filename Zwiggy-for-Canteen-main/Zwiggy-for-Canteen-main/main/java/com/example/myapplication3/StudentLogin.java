package com.example.myapplication3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class StudentLogin extends AppCompatActivity {
    private EditText slemail,slpassword;
    private Button slogin;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);
        auth=FirebaseAuth.getInstance();
        slemail=(EditText) findViewById(R.id.sle);
        slpassword=(EditText) findViewById(R.id.slp);
        slogin=(Button) findViewById(R.id.sllogin);
        slogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String te=slemail.getText().toString();
                String tp=slpassword.getText().toString();
                logUser(te,tp);
            }
        });
    }
    private void logUser(String email,String password)
    {
        auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(StudentLogin.this, "Login Success", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(StudentLogin.this,Items.class));
            }
        });
        auth.signInWithEmailAndPassword(email,password).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(StudentLogin.this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}