package com.lagxen.punchdodge.controller;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.client.Firebase;
//import com.google.android.gms.appindexing.Action;
//import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lagxen.punchdodge.R;
import com.lagxen.punchdodge.controller.cloud.UserAsyncTask;
import com.lagxen.punchdodge.controller.cloud.UserListAsyncTask;
import com.lagxen.punchdodge.model.Singleton;
import com.lagxen.punchdodge.model.User;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.logging.Logger;

public class LoginActivity extends AppCompatActivity {

    private static Logger Log = Logger.getLogger("Angelia ");

    private EditText email;
    private EditText password;
    private ProgressBar progressBar;

    //FaceBook
    private CallbackManager callbackManager;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    Singleton model = Singleton.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //FaceBook
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new
                FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.info("facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.info("facebook:onCancelled: ");
            }

            @Override
            public void onError(FacebookException error) {
                Log.info("facebook:onError:" + error);
                Toast.makeText(getApplicationContext(), "" + error.getMessage(), Toast
                        .LENGTH_LONG).show();
            }
        });
        //

        //Firebase
        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();

        checkUserLogin();

    }


    @Override
    protected void onStart() {
        super.onStart();

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_login);

        mAuth.addAuthStateListener(mAuthListener);

        model.login(getApplicationContext(), model.getPlayer());

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

        model.logout(getApplicationContext(), model.getPlayer());

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        model.logout(getApplicationContext(), model.getPlayer());

    }

    //FaceBook
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    //

    //FaceBook
    public void onFacebookLogInClicked(View view) {
        progressBar.setVisibility(View.VISIBLE);

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList
                ("public_profile", "user_friends", "email"));
    }
    //

    public void onAnonymousLoginClicked(View view) {
        mAuth.signInAnonymously().addOnCompleteListener(this, new
                OnCompleteListener<AuthResult>() {


            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Log.info("signInWithCredential" + task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast
                            .LENGTH_SHORT).show();
                } else {

                    // If sign in fails, display a message to the user. If sign in
                    // succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {

                        String uid = firebaseUser.getUid();

                        //save to Firebase
                        User user = new User();
                        user.setId(uid);
                        user.setName("Anonymous User");
                        user.setImage(null);
                        user.setHit(0);
                        user.setScore(0);
                        user.save();

                        model.setPlayer(user);

                        Intent intent = new Intent(getApplicationContext(),
                                UserActivity.class);

                        intent.putExtra("player_id", uid);
                        intent.putExtra("from", "login");
                        model.login(getApplicationContext(), model.getPlayer());


                        if (mAuthListener != null) {
                            mAuth.removeAuthStateListener(mAuthListener);
                        }


                        FirebaseAuth.getInstance().signOut();
                        startActivity(intent);
                        finish();
                    }
                }

            }
        });
    }

    private void checkUserLogin() {

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
//user is signed in
                    Log.info("Signed in: " + firebaseUser.getUid());
                    model.login(getApplicationContext(), model.getPlayer());
                } else {
                    //user is signed out
                    FirebaseAuth.getInstance().signOut();
//                    Log.info("Logged out: " + firebaseUser.getUid());

                }
            }
        };


    }


    //
    private void handleFacebookAccessToken(final AccessToken token) {
        Log.info("handleFacebookAccessToken:" + token);


        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new
                OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.info("signInWithCredential:onComplete:" + task.isSuccessful());


                if (!task.isSuccessful()) {
                    Log.info("signInWithCredential" + task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast
                            .LENGTH_SHORT).show();
                } else {

                    // If sign in fails, display a message to the user. If sign in
                    // succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {

                        String uid = firebaseUser.getUid();
                        String name = firebaseUser.getDisplayName();
//                        String image = firebaseUser.getPhotoUrl().toString();

                        String image = Profile.getCurrentProfile().getProfilePictureUri
                                (200,200).toString();

                                                //save to Firebase
                        User user = new User();
                        user.setId(uid);
                        user.setName(name);
                        user.setImage(image);
                        user.setHit(0);
                        user.setScore(0);
                        user.save();

                        model.setPlayer(user);
                        model.login(getApplicationContext(), model.getPlayer());


                        Intent intent = new Intent(getApplicationContext(),
                                UserActivity.class);
                        intent.putExtra("player_id", uid);
                        intent.putExtra("from", "login");

                        if (mAuthListener != null) {
                            mAuth.removeAuthStateListener(mAuthListener);
                        }


                        FirebaseAuth.getInstance().signOut();
                        startActivity(intent);
//                        finishActivity();
                        finish();
                    }
                }


            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        model.login(getApplicationContext(), model.getPlayer());

    }
}
