package com.lagxen.punchdodge.controller;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.lagxen.punchdodge.R;
import com.lagxen.punchdodge.controller.cloud.UserAsyncTask;
import com.lagxen.punchdodge.model.Singleton;
import com.lagxen.punchdodge.model.User;

public class ResultActivity extends AppCompatActivity {

    private String playerType, enemyType, roomId;
    private TextView pScoreTxt, pHitTxt, eScoreTxt, eHitTxt;
    private TextView winnerName, pName, eName;
    private ImageView yourImg, enemyImg;
    private Button nextBtn;
    private Drawable defaultImg;
    private int pScore, pHit, eScore, eHit;
    private String pRes, eRes,wRes;

    private User player, enemy;
    Singleton model = Singleton.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        defaultImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable
                .defaultimg);

        playerType = getIntent().getExtras().getString("player_type");
        enemyType = getIntent().getExtras().getString("enemy_type");
        enemyType = getIntent().getExtras().getString("roomId");

        pScore = getIntent().getIntExtra("player_score",0);
        pHit = getIntent().getIntExtra("player_hit",0);
        eScore = getIntent().getIntExtra("enemy_score",0);
        eHit = getIntent().getIntExtra("enemy_hit",0);

        pScoreTxt = (TextView) findViewById(R.id.p1Score);
        eScoreTxt = (TextView) findViewById(R.id.p2Score);

        pRes = "Score:\t" + pScore + "\nHit:\t\t\t" + pHit;
        eRes = "Score:\t" + eScore + "\nHit:\t\t\t" + eHit;


        pScoreTxt.setText(""+pRes);
        eScoreTxt.setText(""+eRes);

        player = model.getPlayer();
        enemy = model.getEnemy();

        winnerName = (TextView) findViewById(R.id.whoWon);
        pName = (TextView) findViewById(R.id.pName);
        eName = (TextView) findViewById(R.id.eName);

        pName.setText(player.getName());
        eName.setText(enemy.getName());

        if(pScore==eScore)
            wRes="DRAW";
        else if (pScore>eScore)
            wRes=player.getName()+" WON!";
        else if(eScore>pScore)
            wRes= enemy.getName()+" WON!";

        winnerName.setText(wRes);

        yourImg = (ImageView) findViewById(R.id.yourImg);
        enemyImg = (ImageView) findViewById(R.id.enemyImg);

        if (player.getImage() == null) yourImg.setImageDrawable(defaultImg);
        else new ImageLoadTask(player.getImage(), yourImg).execute();
        if (enemy.getImage() == null) enemyImg.setImageDrawable(defaultImg);
        else new ImageLoadTask(enemy.getImage(), enemyImg).execute();

        nextBtn = (Button) findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               gotoUser();
            }
        });

        saveToFB();

    }

    private void saveToFB(){

        Firebase userRef = new Firebase("https://lagxen-punchdodge.firebaseio" + "" +
                ".com/users");

        player=model.getPlayer();

        player.setScore(player.getScore()+pScore);
        player.setHit(player.getHit()+pHit);
        userRef.child(player.getId()).setValue(player);

        userRef.child(player.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                player.setId((String) dataSnapshot.child("id").getValue());
                player.setName((String) dataSnapshot.child("name").getValue());
                player.setImage((String) dataSnapshot.child("image").getValue());
                player.setScore((long) dataSnapshot.child("score").getValue());
                player.setHit((long) dataSnapshot.child("hit").getValue());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        enemy.setScore(enemy.getScore()+eScore);
        enemy.setHit(enemy.getHit()+eHit);
        userRef.child(enemy.getId()).setValue(enemy);

        model.setPlayer(player);


    }
    private void gotoUser(){
//        new UserAsyncTask(getApplicationContext(), player.getId(), "update")
//                .execute();


        Intent intent = new Intent(getApplicationContext(),UserActivity.class);
        intent.putExtra("from","result");

        startActivity(intent);
    }

}
