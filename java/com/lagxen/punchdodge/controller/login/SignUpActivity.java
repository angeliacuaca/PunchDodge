//package com.lagxen.punchdodge.controller.login;
//
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import com.firebase.client.Firebase;
//import com.firebase.client.FirebaseError;
//import com.lagxen.punchdodge.model.User;
//
//import java.util.Map;
//
///**
// * Created by angeliacuaca on 10/10/16.
// */
//public class SignUpActivity extends AppCompatActivity {
//
//    private Firebase myFirebaseRef;
//    private User user;
//    private EditText name;
//    private EditText phoneNumber;
//    private EditText email;
//    private EditText password;
//    private ProgressBar progressBar;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sign_up);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        //Creates a reference for  your Firebase database
//        //Add YOUR Firebase Reference URL instead of the following URL
//        myFirebaseRef =  new Firebase("https://lagxen-punchdodge.firebaseio" +
//                ".com/");
//
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        name = (EditText) findViewById(R.id.edit_text_username);
//        phoneNumber=(EditText) findViewById(R.id.edit_text_phone_number);
//        email = (EditText) findViewById(R.id.edit_text_new_email);
//        password = (EditText) findViewById( R.id.edit_text_new_password);
//        progressBar = (ProgressBar) findViewById(R.id.progress_bar_sign_up);
//    }
//
//
//    protected void setUpUser(){
//        user = new User();
//        user.setName(name.getText().toString());
//        user.setPhoneNumber(phoneNumber.getText().toString());
//        user.setEmail(email.getText().toString());
//        user.setPassword(password.getText().toString());
//    }
//
//    public void onSignUpClicked(View view){
//        progressBar.setVisibility(View.VISIBLE);
//        setUpUser();
//        //createUser method creates a new user account with the given email and password.
//        //Parameters are :
//        // email - The email for the account to be created
//        // password - The password for the account to be created
//        // handler - A handler which is called with the result of the operation
//        myFirebaseRef.createUser(
//                user.getEmail(),
//                user.getPassword(),
//                new Firebase.ValueResultHandler<Map<String, Object>>() {
//                    @Override
//                    public void onSuccess(Map<String, Object> stringObjectMap) {
//                        user.setId(stringObjectMap.get("uid").toString());
//                        user.saveUser();
//                        myFirebaseRef.unauth();
//                        Toast.makeText(getApplicationContext(), "Your Account has been Created", Toast.LENGTH_LONG).show();
//                        Toast.makeText(getApplicationContext(), "Please Login With your Email and Password", Toast.LENGTH_LONG).show();
//                        progressBar.setVisibility(View.GONE);
//                        finish();
//                    }
//
//                    @Override
//                    public void onError(FirebaseError firebaseError) {
//                        Toast.makeText(getApplicationContext(), "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
//                        progressBar.setVisibility(View.GONE);
//                    }
//                }
//        );
//    }
//
//
//}
//
