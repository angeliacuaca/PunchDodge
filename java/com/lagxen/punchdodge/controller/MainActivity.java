package com.lagxen.punchdodge.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.lagxen.punchdodge.R;
import com.lagxen.punchdodge.controller.cloud.GenerateRequestAsyncTask;
import com.lagxen.punchdodge.model.Mock;
import com.lagxen.punchdodge.model.MockUser;
import com.lagxen.punchdodge.model.Singleton;
import com.lagxen.punchdodge.model.User;

import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private Button accBtn, decBtn;
    private TextView statTxt, yourNameTxt, enemyNameTxt;
    private ImageView yourImg, enemyImg;
    private Drawable defaultImg;

    private String playerId, playerName, playerType, imageUrl, roomId;
    private String enemyId, enemyName, enemyType, enemyImage, enemyRoom;
    private String status = "Waiting...";
    private boolean isFull = false;
    private boolean playerAccepted = false;
    private boolean enemyAccepted = false;

    private User player, enemy;

    private Firebase waitingRoom, playRoom, poolRoom;
    private ValueEventListener listener;

    private TextView test1, test2;
    private Button addBtn, okBtn;

    Singleton model = Singleton.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        defaultImg = ContextCompat.getDrawable(getApplicationContext(), R.drawable
                .defaultimg);

        playerType = getIntent().getExtras().getString("player_type");

        initialiseViews();

//check if player came from login or playerlist
        checkPlayers();

        fillViews();

        // Firebase
        Firebase.setAndroidContext(this);
        poolRoom = new Firebase("https://lagxen-punchdodge.firebaseio" + "" +
                ".com/waitingRoom");
        playRoom = new Firebase("https://lagxen-punchdodge.firebaseio.com/playRoom");


        //listen to snapshot if enemy picked you
        listen();
        checkStatus();

        fillViews();

        test1 = (TextView) findViewById(R.id.tes1);
        test2 = (TextView) findViewById(R.id.tes2);

        if (playerName != null && playerType != null)
            test1.setText(playerName + ": " + playerType);
        if (enemyName != null && enemyType != null)
            test2.setText(enemyName + ": " + enemyType);

        okBtn = (Button) findViewById(R.id.nameBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MockUser().generate();
            }
        });
        addBtn = (Button) findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleEnemy();
            }
        });

        checkListener();

    }//end of OnCreate


    private void enemyImageClicked() {
        Intent intent = new Intent(getApplicationContext(), PlayerListActivity.class);
        intent.putExtra("player_type", "player1");
        intent.putExtra("player_room", roomId);
        intent.putExtra("player_id", playerId);
        intent.putExtra("player_name", playerName);
        intent.putExtra("player_image", imageUrl);

//        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        startActivity(intent);
        finish();
    }

    private void handleEnemy() {
        //new MockUser().asEnemy("mockId07", roomId);
        waitingRoom.child(enemyType).child("accepted").setValue("yes");
//        checkStatus(roomId);
    }

    //reject when waiting for enemy respond
    private void rejectAction(View v) {

//        poolRoom = new Firebase("https://lagxen-punchdodge.firebaseio" + "" +
//                ".com/waitingRoom");

        //if reject when enemy picked you, enemy back to enemy prev room
        if (isFull && playerType.equals("player1")) {
            poolRoom.child(enemy.getRoomId()).child("player1").setValue(enemy);
            poolRoom.child(enemy.getRoomId()).child("player1").child("accepted")
                    .setValue(null);
            poolRoom.child(player.getRoomId()).child("player2").setValue(null);
            decBtn.setEnabled(false);
        }

        //if reject when you pick enemy, you go back to prev room
        else if (isFull && playerType.equals("player2")) {
            roomId = player.getRoomId();
            poolRoom.child(player.getRoomId()).child("player1").setValue(player);
            poolRoom.child(player.getRoomId()).child("player1").child("accepted")
                    .setValue(null);
            poolRoom.child(enemy.getRoomId()).child("player2").setValue(null);
            decBtn.setEnabled(false);
            playerType = "player1";
        }

        resetEnemy();
        //stop listening to other registered room
//        if(!roomId.equals(player.getRoomId())){
//            waitingRoom
//        }
        checkStatus();
        //find another guy
//        findVictim();
    }

    private void findVictim() {
//randomise waiting room and add to opponent
    }


    //if match accepted start the game
    //if match rejected, reset everything to the initial state
    private void acceptAction(View view) {
        waitingRoom = new Firebase("https://lagxen-punchdodge.firebaseio" + "" +
                ".com/waitingRoom").child(roomId);
        status = "Now wait for " + enemyName + " to accept";

        statTxt.setText(status);

        accBtn.setEnabled(false);
        decBtn.setEnabled(true);

        waitingRoom.child(playerType).child("accepted").setValue("yes");
//        waitingRoom.child(enemyType).child("accepted").setValue("wait");
        playerAccepted = true;

//        //if enemy accepted and you accepted
//        if (enemyAccepted && playerAccepted) {
//
//            //add room to playroom
//            Firebase moveRef = playRoom.child(roomId);
//
//            moveRef.child(playerType).setValue(player);
//            moveRef.child(enemyType).setValue(enemy);
//
//
//            //remove room from waiting room
//            waitingRoom.child(playerType).setValue(null);
//            waitingRoom.child(enemyType).setValue(null);
//
//            //send to EP ask for generate 5 round requests
////                        new EndpointsAsyncTask().execute(new Pair<Context, String>
////                                (getApplicationContext(), roomId));
//
//
//            new Mock(roomId, 5).generate();
//
//
//        }

    }


    private void checkPlayers() {

        player=model.getPlayer();
        playerId = getIntent().getExtras().getString("player_id");
        playerName = getIntent().getExtras().getString("player_name");
        roomId = getIntent().getExtras().getString("player_room");
        imageUrl = getIntent().getExtras().getString("player_image");

        playerId = player.getId();
        playerName = player.getName();
        roomId = player.getRoomId();
        imageUrl = player.getImage();

//        player = new User();
//        player.setId(playerId);
//        player.setName(playerName);
//        player.setImage(imageUrl);
//        player.setRoomId(roomId);



        //intent from login page
        if (playerType.equalsIgnoreCase("player1")) {

            enemyType = "player2";
            roomId = player.getRoomId();

        }

        //intent from player list, this is generate when you picked enemy
        else {

            enemyType = "player1";
            accBtn.setEnabled(true);

            enemyId = getIntent().getExtras().getString("enemy_id");
            enemyName = getIntent().getExtras().getString("enemy_name");
            enemyRoom = getIntent().getExtras().getString("enemy_room");
            enemyImage = getIntent().getExtras().getString("enemy_image");

            roomId = enemyRoom;

            enemy = new User();
            enemy.setId(enemyId);
            enemy.setName(enemyName);
            enemy.setImage(enemyImage);
            enemy.setRoomId(enemyRoom);


//            checkStatus(roomId);

        }
    }

    private void listen() {
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("player2").child("accepted").exists() &&
                        dataSnapshot.child("player1").child("accepted").exists()) {
                    if (dataSnapshot.child("player2").child("accepted").getValue()
                            .equals("yes") && dataSnapshot.child("player1").child
                            ("accepted").getValue().equals("yes")) {
                        gotoPlayRoom();
                    }
                }
                if (dataSnapshot.getChildrenCount() == 2) {
                    isFull = true;

                    if (!dataSnapshot.child("player2").exists()) {
                        playerType = "player1";
                        roomId = player.getRoomId();
                        enemyType = "player2";
                    }


                    //if enemy come in, not yet accepted
                    if (enemyAccepted == false) {

                        //player1 means came from LOGIN, enemy is player2
                        if (playerType.equalsIgnoreCase("player1")) {

                            getEnemyInfo("player2", dataSnapshot);
                            status = enemyName + " wants to Punch you!!";
                        }

                        //player2 came from player list, enemy is player1
                        else if (playerType.equalsIgnoreCase("player2")) {


                            getEnemyInfo("player1", dataSnapshot);
                            status = "You just challenge " + enemyName + "!!";

                        }

                        decBtn.setEnabled(true); //reject is possible

                    }//end of enemyAccepted
                    else {
                        status = enemyName + " has accepted";
                    }
                    String accepted = (String) dataSnapshot.child(enemyType).child
                            ("accepted").getValue();
                    String pAccepted = (String) dataSnapshot.child(playerType).child
                            ("accepted").getValue();

                    //if player accepted but enemy not yet
                    if ((accepted != null) && accepted.equals("yes")) {
                        enemyAccepted = true;
                        status = enemyName + " has accepted";
                    }
                    if ((pAccepted != null) && !pAccepted.equals("yes")){
                        playerAccepted = false;
                        accBtn.setEnabled(true);
                    }
                    if (playerAccepted) {
                        accBtn.setEnabled(false);
                        status = "Now wait for " + enemyName + " to accept";
                    }

                    //if players less than 2, reset
                } else {
                    isFull = false;

                    resetEnemy();
                    status = "Waiting for a victim....";


                }//end of children count

                fillViews();
                statTxt.setText(status);

//                if (playerName != null && playerType != null)
                test1.setText(playerName + ": " + playerType);
//                if (enemyName != null && enemyType != null)
                test2.setText(enemyName + ": " + enemyType);

            } //end of data snapshot

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
    }

    //check status and update info if there's change on snapshot
    private void checkStatus() {
        if (playerType.equals("player1")) {

            if (!roomId.equals(player.getRoomId())) {
                poolRoom.child(roomId).removeEventListener(listener);
                poolRoom.child(player.getRoomId()).addValueEventListener(listener);
            }

        } else if (playerType.equals("player2")) {

            if (!roomId.equals(enemy.getRoomId())) {
                poolRoom.child(roomId).removeEventListener(listener);
                poolRoom.child(enemy.getRoomId()).addValueEventListener(listener);
            }
        }
        poolRoom.child(roomId).addValueEventListener(listener);
    }

    private void checkListener() {
        Firebase ref = new Firebase("https://lagxen-punchdodge.firebaseio" + "" +
                ".com/waitingRoom");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (playerType.equals("player1")) {

                    if (!player.getRoomId().equals(roomId)) {
                        poolRoom.child(roomId).removeEventListener(listener);
                    }

                } else if (playerType.equals("player2")) {
                    if (!enemy.getRoomId().equals(roomId)) {
                        poolRoom.child(roomId).removeEventListener(listener);
                    }
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    private void getEnemyInfo(String type, DataSnapshot dataSnapshot) {

        enemyType = type;
        enemyId = (String) dataSnapshot.child(type).child("id").getValue();
        enemyName = (String) dataSnapshot.child(type).child("name").getValue();
        enemyImage = (String) dataSnapshot.child(type).child("image").getValue();
        enemyRoom = (String) dataSnapshot.child(type).child("roomId").getValue();

        enemy = new User();
        enemy.setId(enemyId);
        enemy.setName(enemyName);
        enemy.setImage(enemyImage);
        enemy.setRoomId(enemyRoom);

        enemyNameTxt.setText(enemyName);

//        if (enemyImage != null) new ImageLoadTask(enemyImage, enemyImg).execute();
//        else enemyImg.setImageDrawable(defaultImg);

        accBtn.setEnabled(true);

    }


    private void initialiseViews() {

        yourNameTxt = (TextView) findViewById(R.id.yourNameTxt);
        enemyNameTxt = (TextView) findViewById(R.id.enemyNameTxt);


        accBtn = (Button) findViewById(R.id.acceptBtn);
        accBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptAction(v);
            }
        });

        decBtn = (Button) findViewById(R.id.declineBtn);
        decBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectAction(v);
            }
        });

        //Display status, connected, waiting
        statTxt = (TextView) findViewById(R.id.statusTxt);
        statTxt.setText(status);

        yourImg = (ImageView) findViewById(R.id.yourImg);
        enemyImg = (ImageView) findViewById(R.id.enemyImg);

        enemyImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enemyImageClicked();
            }
        });

    }

    public void fillViews() {
        //        set player1
        yourNameTxt.setText(playerName);
        new ImageLoadTask(imageUrl, yourImg).execute();

        //set player2, if intent came from player list
        if (enemyName != null) enemyNameTxt.setText(enemyName);
        else enemyNameTxt.setText("Tap Me!");
        if (enemyImage != null) new ImageLoadTask(enemyImage, enemyImg).execute();
        else enemyImg.setImageDrawable(defaultImg);
    }

    public void resetEnemy() {
        enemy = null;
        enemyId = null;
        enemyName = null;
        enemyImage = null;
        enemyRoom = null;

        playerAccepted = false;
        enemyAccepted = false;

        enemyNameTxt.setText("Tap Me!");
        enemyImg.setImageDrawable(defaultImg);
        decBtn.setEnabled(false);
        accBtn.setEnabled(false);

        playerType = "player1";
        enemyType = "player2";

        roomId = player.getRoomId();
//        waitingRoom = new Firebase("https://lagxen-punchdodge.firebaseio" + "" +
//                ".com/waitingRoom").child(roomId);

    }

    private void gotoPlayRoom() {

        model.setPlayer(player);
        model.setEnemy(enemy);
        waitingRoom = new Firebase("https://lagxen-punchdodge.firebaseio" + "" +
                ".com/waitingRoom").child(roomId);

        Firebase moveRef = playRoom.child(roomId);

        moveRef.child(playerType).setValue(player);
        moveRef.child(enemyType).setValue(enemy);


        //remove room from waiting room

        waitingRoom.child(playerType).setValue(null);
        waitingRoom.child(enemyType).setValue(null);

        //send to EP ask for generate 5 round requests
        ////Uncomment the code below to invoke a call to Endpoint.
//        new GenerateRequestAsyncTask().execute(new Pair<Context, String>
//                (getApplicationContext(), roomId));


        Intent intent = new Intent(getApplicationContext(), PunchActivity.class);

        intent.putExtra("room_id",roomId);
        intent.putExtra("player_type", playerType);


        startActivity(intent);
//        finish();
    }

}
