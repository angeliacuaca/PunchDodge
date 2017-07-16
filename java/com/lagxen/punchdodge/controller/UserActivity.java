package com.lagxen.punchdodge.controller;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;


import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.lagxen.punchdodge.R;
import com.lagxen.punchdodge.controller.cloud.UserAsyncTask;
import com.lagxen.punchdodge.model.Singleton;
import com.lagxen.punchdodge.model.User;
import com.lagxen.punchdodge.controller.cloud.UserListAsyncTask;

public class UserActivity extends AppCompatActivity {

    private TextView nameTxt, scoreTxt, hitTxt;
    private ImageView userImg;
    private Button btnStart;
    private Drawable defaultImg;

    private User player;
    private String userId;

    Singleton model = Singleton.getInstance();
    final Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        nameTxt = (TextView) findViewById(R.id.nameTxt);
        scoreTxt = (TextView) findViewById(R.id.scoreTxt);
        hitTxt = (TextView) findViewById(R.id.hitTxt);

        btnStart = (Button) findViewById(R.id.btnStart);
        userImg = (ImageView) findViewById(R.id.userImg);

        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                new UserListAsyncTask(getApplicationContext(), userId, "").execute();
                gotoMain();
            }
        });

        player = model.getPlayer();

        defaultImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable
                .defaultimg);
        userImg.setImageDrawable(defaultImg);
        nameTxt.setText("Boo");


        userId = getIntent().getExtras().getString("player_id");

        String from = getIntent().getExtras().getString("from");

        //BELOW IF STATEMENT IS USED TO CALL DATASTORE WHICH ALREADY DISABLED NOW
//        if (from.equals("login")) {
//            if (!player.getName().equals("Anonymous User"))
////                new UserAsyncTask(getApplicationContext(), player.getId(), "insert")
////                        .execute();
//        } else if (from.equals("result")) {
//
//            if (!player.getName().equals("Anonymous User")) {
////                model.getFromFirebase(this);
//                player=model.getPlayer();
////                new UserAsyncTask(getApplicationContext(), player.getId(), "update")
////                        .execute();
//            }
//        }

        Firebase.setAndroidContext(this);


        loadUser();
        model.login(getApplicationContext(),model.getPlayer());

        player = model.getPlayer();
        nameTxt.setText(player.getName());
        scoreTxt.setText("You punched " + player.getScore() + " times");
        hitTxt.setText("You got punched " + player.getHit() + " times");
    }

    private void loadUser() {
        Firebase userRef = new Firebase("https://lagxen-punchdodge.firebaseio" + "" +
                ".com/users");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                player = model.getPlayer();
                nameTxt.setText(player.getName());
                scoreTxt.setText("You punched " + player.getScore() + " times");
                hitTxt.setText("You got punched " + player.getHit() + " times");

                if (player.getImage() != null)
                    new ImageLoadTask(player.getImage(), userImg).execute();
                else userImg.setImageDrawable(defaultImg);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void gotoMain() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        player.setScore(0);
        player.setHit(0);
//        player.save();
        player.queue();
        model.setPlayer(player);
        intent.putExtra("player_type", "player1");
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        model.logout(getApplicationContext(),model.getPlayer());

    }

    @Override
    protected void onStop() {
        super.onStop();
        model.logout(getApplicationContext(),model.getPlayer());

    }

    @Override
    protected void onStart() {
        super.onStart();
        model.login(getApplicationContext(),model.getPlayer());

    }
    @Override
    protected void onResume() {
        super.onResume();
        model.login(getApplicationContext(), model.getPlayer());

    }
}