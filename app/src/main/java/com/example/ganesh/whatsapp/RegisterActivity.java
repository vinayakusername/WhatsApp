package com.example.ganesh.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private Button CreateAccountButton;
    private EditText UserEmail, UserPassword;
    private TextView AlreadyHaveAccountLink;

    private FirebaseAuth mAuth;
    private ProgressDialog LoadingBar;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        RootRef=FirebaseDatabase.getInstance().getReference();

        InitializedFields();

        AlreadyHaveAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });
        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });
    }

    private void CreateNewAccount() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(RegisterActivity.this, "Please Enter EmailId.....", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "Please Enter Password....", Toast.LENGTH_SHORT).show();
        }
        else
        {
              LoadingBar.setTitle("Creating New Account");
              LoadingBar.setMessage("Please Wait,while we are creating new account for you....");
              LoadingBar.setCanceledOnTouchOutside(true);
              LoadingBar.show();
              mAuth.createUserWithEmailAndPassword(email,password)
                      .addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    String currentUserID=mAuth.getCurrentUser().getUid();
                    RootRef.child("Users").child(currentUserID).setValue("");

                    SendUserToMainActivity();
                    Toast.makeText(RegisterActivity.this, "Account created Successfully....", Toast.LENGTH_SHORT).show();
                    LoadingBar.dismiss();
                    UserEmail.setText("");
                    UserPassword.setText("");
                }
                else
                    {
                           String message=task.getException().toString();
                           Toast.makeText(RegisterActivity.this,"Error.."+message,Toast.LENGTH_SHORT).show();
                           LoadingBar.dismiss();
                    }


            }
             });

        }
    }




    private void InitializedFields() {
        CreateAccountButton=(Button)findViewById(R.id.register_button);
        UserEmail=(EditText)findViewById(R.id.register_email);
        UserPassword=(EditText)findViewById(R.id.register_password);
        AlreadyHaveAccountLink=(TextView)findViewById(R.id.Already_have_account_link);
        LoadingBar=new ProgressDialog(this);
    }
    private void SendUserToLoginActivity() {
        Intent loginIntent=new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(loginIntent);
    }

    private void SendUserToMainActivity() {
        Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);//for validation
        startActivity(mainIntent);
        finish();
    }
}
