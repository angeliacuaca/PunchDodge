package com.lagxen.punchdodge.controller;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.lagxen.punchdodge.R;
import com.lagxen.punchdodge.model.Singleton;
import com.lagxen.punchdodge.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class PlayerListActivity extends AppCompatActivity {
    private ListView listView;
    private Button btnShuffle;

    private ArrayList<User> playerList, newList;
    private HashSet<String> set;

    private Firebase playerRef;
    private PlayerListAdapter playerListAdapter;

    private String playerId, playerName, playerType, imageUrl, roomId;
    private String enemyId, enemyName, enemyType, enemyImage, enemyRoom;
    private User player;
    private int limit = 3;

    Singleton model = Singleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);

//        Firebase.setAndroidContext(this);
        playerRef = new Firebase("https://lagxen-punchdodge.firebaseio.com/waitingRoom");

        player = new User();
        playerType = getIntent().getExtras().getString("player_type");

        playerId = getIntent().getExtras().getString("player_id");
        playerName = getIntent().getExtras().getString("player_name");
        roomId = getIntent().getExtras().getString("player_room");
        imageUrl = getIntent().getExtras().getString("player_image");

        player.setName(playerName);
        player.setId(playerId);
        player.setImage(imageUrl);
        player.setRoomId(roomId);

       model.setPlayer(player);

        btnShuffle = (Button) findViewById(R.id.shuffleBtn);
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shuffle(limit);
            }
        });

        Firebase.setAndroidContext(this);
        playerList = new ArrayList<User>();
        set = new HashSet<String>();

        listView = (ListView) findViewById(R.id.playerList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long
                    l) {
                Object obj = listView.getItemAtPosition(i);
                User user = newList.get(i);
                gotoWaitRoom(user);
            }
        });

        playerRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> children = dataSnapshot.getChildren().iterator();

                while (children.hasNext()) {
                    DataSnapshot snapChild = (DataSnapshot) children.next();

                    if (snapChild.getChildrenCount() == 1) {
                        String userId = (String) snapChild.child("player1").child("id")
                                .getValue();

                        String userName = (String) snapChild.child("player1").child
                                ("name").getValue();
                        String userImage = (String) snapChild.child("player1").child
                                ("image").getValue();
                        String userRoomId = (String) snapChild.child("player1").child
                                ("roomId").getValue();

                        //if set already contain
                        if (!set.contains(userId)) {
                            if (userId != null && player != null) {
                                if (!userId.equals(player.getId())) {
                                    User user = new User();
                                    user.setId(userId);
                                    user.setName(userName);
                                    user.setImage(userImage);
                                    user.setRoomId(userRoomId);
                                    playerList.add(user);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        playerListAdapter = new PlayerListAdapter(this, playerList);
        listView.setAdapter(playerListAdapter);

        shuffle(limit);

        delayShow.start();


        //listen to change always
        model.login(getApplicationContext(), model.getPlayer());

    }

    CountDownTimer delayShow = new CountDownTimer(2000, 1000) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            playerListAdapter = new PlayerListAdapter(PlayerListActivity.this,
                    playerList);
            listView.setAdapter(playerListAdapter);

            shuffle(limit);

        }

    };

    private void updateList(ArrayList<User> users) {
        playerListAdapter.updateList(users);
        listView.setAdapter(playerListAdapter);
    }

    private void gotoWaitRoom(User user) {

        model.setEnemy(user);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        intent.putExtra("player_type", "player2");
        intent.putExtra("player_room", roomId);
        intent.putExtra("player_id", playerId);
        intent.putExtra("player_name", playerName);
        intent.putExtra("player_image", imageUrl);

        intent.putExtra("enemy_room", user.getRoomId());
        intent.putExtra("enemy_id", user.getId());
        intent.putExtra("enemy_name", user.getName());
        intent.putExtra("enemy_image", user.getImage());

        //you move to enemy room as player2
        playerRef.child(user.getRoomId()).child("player2").setValue(player);
        playerRef.child(user.getRoomId()).child("player2").child("accepted").setValue
                ("wait");
        playerRef.child(user.getRoomId()).child("player1").child("accepted").setValue
                ("wait");
        playerRef.child(player.getRoomId()).setValue(null);

        startActivity(intent);
//        finish();
    }

    private void shuffle(int limit) {
        if (limit > playerList.size()) limit = playerList.size();
        long seed = System.nanoTime();
        newList = new ArrayList<User>();

        //randomise and limit
        Collections.shuffle(playerList, new Random(seed));
        for (int i = 0; i < limit; i++) {
            newList.add(playerList.get(i));
        }

        updateList(newList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        model.logout(getApplicationContext(), model.getPlayer());
    }

    @Override
    protected void onStop() {
        super.onStop();
        model.logout(getApplicationContext(), model.getPlayer());
    }

    @Override
    protected void onStart() {
        super.onStart();
        model.login(getApplicationContext(), model.getPlayer());
    }

    @Override
    protected void onResume() {
        super.onResume();
        model.login(getApplicationContext(), model.getPlayer());

    }
}