package com.lagxen.punchdodge.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.lagxen.punchdodge.R;
import com.lagxen.punchdodge.model.Mock;
import com.lagxen.punchdodge.model.Request;
import com.lagxen.punchdodge.model.Singleton;
import com.lagxen.punchdodge.model.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;


public class PunchActivity extends AppCompatActivity {

    private Button epBtn, edBtn;

    private Button pBtn, dBtn;
    private TextView rndTxt, reqTxt, resTxt, pScore, eScore, askRotate;
    private String timeout;
    final Handler handler = new Handler();

    private String playerType, roomId;
    private String enemyType;
    private char playerReq, enemyReq;

    static Logger Log = Logger.getLogger("ANGELIA READ THIS:");

    private ArrayList<String> roundList = new ArrayList<>();

    private User player, enemy, winner;
    Firebase playRoom, rounds;

    private VideoView videoPlayer;
    private ImageView imagePlayer;
    private Uri video;
    private DisplayMetrics dm;


    private String currRound;
    private int playerIndex, enemyIndex;
    private long roundCount;

    private int playerScore, playerHit;
    private int enemyScore, enemyHit;

    Singleton model = Singleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_punch);

        playerType = getIntent().getExtras().getString("player_type");
        roomId=getIntent().getExtras().getString("room_id");

        player=model.getPlayer();
        enemy=model.getEnemy();

        checkPlayers();
        dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;

        imagePlayer = (ImageView) findViewById(R.id.placeholder);
        imagePlayer.setMinimumWidth(width);
        imagePlayer.setMinimumHeight(height);
        imagePlayer.setVisibility(View.VISIBLE);
        videoPlayer = (VideoView) findViewById(R.id.video_player_view);
        getVideo(R.drawable.ready);

        pBtn = (Button) findViewById(R.id.punchBtn);
        dBtn = (Button) findViewById(R.id.dodgeBtn);

        reqTxt = (TextView) findViewById(R.id.requestTxt);
        resTxt = (TextView) findViewById(R.id.resultTxt);

        pScore = (TextView) findViewById(R.id.p1Score);
        eScore = (TextView) findViewById(R.id.p2Score);

        askRotate = (TextView) findViewById(R.id.askRotate);

        pBtn.setEnabled(false);
        dBtn.setEnabled(false);

        pBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!hasResult) {

                    rounds.child(currRound).child("respond").child(playerType).child
                            ("resp").setValue("p");
                }
                pBtn.setEnabled(false);
                dBtn.setEnabled(false);

            }

        });

        dBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!hasResult) {

                    rounds.child(currRound).child("respond").child(playerType).child
                            ("resp").setValue("d");

                }
                pBtn.setEnabled(false);
                dBtn.setEnabled(false);
            }
        });


        Firebase.setAndroidContext(this);

        playRoom = new Firebase("https://lagxen-punchdodge.firebaseio.com/playRoom")
                .child(roomId);

        rounds = playRoom.child("rounds");

        //delete this generator
        new Mock(roomId, 15).generate();

        playRoom.child(playerType).child("ready").addValueEventListener(readyListener);

        //below is enemy buttons for test purposes, you can delete these
        epBtn = (Button) findViewById(R.id.EpunchBtn);
        edBtn = (Button) findViewById(R.id.EdodgeBtn);
        epBtn.setEnabled(false);
        edBtn.setEnabled(false);

        epBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!hasResult) {

                    rounds.child(currRound).child("respond").child(enemyType).child
                            ("resp").setValue("p");

                }
                epBtn.setEnabled(false);
                edBtn.setEnabled(false);

            }

        });

        edBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!hasResult) {

                    rounds.child(currRound).child("respond").child(enemyType).child
                            ("resp").setValue("d");

                }
                epBtn.setEnabled(false);
                edBtn.setEnabled(false);
            }
        });

        model.login(getApplicationContext(),model.getPlayer());

    }//end onCreate

    private boolean isWait;
    //get DONE signal from server put on playerRoom -> player -> ready
    private ValueEventListener readyListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()) {
                assignPlayers();
                getVideo(R.drawable.go);
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };

    //get signal if round change rounds ->current
    private ValueEventListener roundListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if (currRound != null) {
                rounds.child(currRound).child("respond").removeEventListener
                        (respListener);
                rounds.child(currRound).child("request").removeEventListener
                        (reqsListener);
            }

            isWait = false;
            hasResult = false;
            winner = null;
            playerResp = null;
            enemyResp = null;

            if (dataSnapshot.exists()) {

                currRound = (String) dataSnapshot.getValue();
                rounds.child(currRound).child("request").addListenerForSingleValueEvent
                        (reqsListener);

            }

            rounds.child("current").removeEventListener(this);

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };

    //get request from server rounds -> currRound -> request
    private ValueEventListener reqsListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                currReq = (String) dataSnapshot.getValue();

                playerReq = currReq.charAt(playerIndex);

                enemyReq = currReq.charAt(enemyIndex);

                drawRequest = new Request().play(currReq);
//            getVideo(R.drawable.def);

                getVideo(drawRequest);

                showRequest.start();

                rounds.child(currRound).child("respond").runTransaction(new Transaction.Handler() {

                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {


                        return null;
                    }

                    @Override
                    public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });
                rounds.child(currRound).child("respond").addValueEventListener
                        (respListener);


                rounds.child(currRound).child("request").removeEventListener
                        (reqsListener);
                rounds.child("current").removeEventListener(roundListener);

                pBtn.setEnabled(true);
                dBtn.setEnabled(true);
                epBtn.setEnabled(true);
                edBtn.setEnabled(true);
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };


    //respond and result rounds -> currRound -> responds
    private ValueEventListener respListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if (dataSnapshot.child(dataSnapshot.getKey()).child("result").exists()) {
                rounds.child(currRound).child("respond").removeEventListener
                        (respListener);
                resTxt.setText(currRound + " resssssss");
            } else {

                if (dataSnapshot.getChildrenCount()==1) {

                    if (dataSnapshot.child(playerType).child("resp").exists())
                        playerResp = (String) dataSnapshot.child(playerType).child("resp").getValue();
                    else    rounds.child(currRound).child("respond").child(playerType).child
                            ("resp").setValue("o");

                    if (dataSnapshot.child(enemyType).child("resp").exists())
                        enemyResp = (String) dataSnapshot.child(enemyType).child("resp").getValue();
                    else   rounds.child(currRound).child("respond").child(enemyType).child
                            ("resp").setValue("o");


                    if (enemyResp == null) enemyResp = "o";
                    if (playerResp == null) playerResp = "o";

                }else if(dataSnapshot.getChildrenCount()==2){

                    if (dataSnapshot.child(playerType).child("resp").exists())
                        playerResp = (String) dataSnapshot.child(playerType).child("resp").getValue();
                    else playerResp = "o";


                    if (dataSnapshot.child(enemyType).child("resp").exists())
                        enemyResp = (String) dataSnapshot.child(enemyType).child("resp").getValue();
                    else enemyResp = "o";

                    if (enemyResp == null) enemyResp = "o";
                    if (playerResp == null) playerResp = "o";

                    int draw = new Request().check(currReq, playerResp.charAt(0),
                            enemyResp.charAt(0));

                    getVideo(draw);

                    switch (draw) {
                        case R.raw.oxres:
                        case R.raw.xores:
                            pBtn.setEnabled(true);
                            dBtn.setEnabled(true);
                            edBtn.setEnabled(true);
                            epBtn.setEnabled(true);
                            break;

                        case R.raw.pores:
                        case R.raw.pxres:
                            winner = player;
                            player.setScore(player.getScore() + 1);
                            enemy.setHit(enemy.getHit() + 1);

                            playerScore = playerScore + 1;
                            enemyHit = enemyHit + 1;
                            timeout = "somebody responded";
                            break;

                        case R.raw.opres:
                        case R.raw.xpres:
                            winner = enemy;
                            enemy.setScore(enemy.getScore() + 1);
                            player.setHit(player.getHit() + 1);

                            enemyScore = enemyScore + 1;
                            playerHit = playerHit + 1;
                            timeout = "somebody responded";
                            break;

                        case R.raw.ppres:
                            player.setScore(player.getScore() + 1);
                            enemy.setHit(enemy.getHit() + 1);

                            enemy.setScore(enemy.getScore() + 1);
                            player.setHit(player.getHit() + 1);
                            winner = null;

                            playerScore = playerScore + 1;
                            playerHit = playerHit + 1;
                            enemyHit = enemyHit + 1;
                            enemyScore = enemyScore + 1;
                            timeout = "somebody responded";
                            break;

                        case R.raw.pdres:
                        case R.raw.dpres:
                        case R.raw.pdreqddres:
                        case R.raw.ppreqddres:
                        case R.raw.dpreqddres:
                        case R.raw.dxres:
                        case R.raw.xdres:
                        case R.raw.dores:
                        case R.raw.odres:
                            playerScore = playerScore + 0;
                            playerHit = playerHit + 0;
                            enemyHit = enemyHit + 0;
                            enemyScore = enemyScore + 0;
                            timeout = "somebody responded";
                            break;
                        default:
                            playerScore = playerScore + 0;
                            playerHit = playerHit + 0;
                            enemyHit = enemyHit + 0;
                            enemyScore = enemyScore + 0;
                            timeout = "no respond";
                            break;
                    }

                    pScore.setText("" + playerScore);
                    eScore.setText("" + enemyScore);


                } else {

                    timeout = "no respond";

                }
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };



    private void checkPlayers() {
        if (playerType.equals("player1")) {
            enemyType = "player2";
//            roomId = player.getRoomId();
            playerIndex = 0;
        } else {
            enemyType = "player1";
            enemyIndex = 1;
//            roomId=enemy.getRoomId();
        }


    }


    private void assignPlayers() {
        getVideo(R.drawable.ready);

        playRoom.addListenerForSingleValueEvent(

                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        player = new User();
                        player.setId((String) dataSnapshot.child(playerType).child
                                ("id").getValue());
                        player.setName((String) dataSnapshot.child(playerType).child
                                ("name").getValue());
                        player.setImage((String) dataSnapshot.child(playerType).child
                                ("image").getValue());
                        player.setScore((long) dataSnapshot.child(playerType).child
                                ("score").getValue());
                        player.setHit((long) dataSnapshot.child(playerType).child
                                ("hit").getValue());
                        player.setRoomId((String) dataSnapshot.child(playerType).child
                                ("roomId").getValue());

                        enemy = new User();
                        enemy.setId((String) dataSnapshot.child(enemyType).child("id")
                                .getValue());
                        enemy.setName((String) dataSnapshot.child(enemyType).child
                                ("name").getValue());
                        enemy.setImage((String) dataSnapshot.child(enemyType).child
                                ("image").getValue());
                        enemy.setScore((long) dataSnapshot.child(enemyType).child
                                ("score").getValue());
                        enemy.setHit((long) dataSnapshot.child(enemyType).child("hit")
                                .getValue());
                        enemy.setRoomId((String) dataSnapshot.child(enemyType).child
                                ("roomId").getValue());

                        roundCount = dataSnapshot.child("rounds").getChildrenCount();
                        Iterator<DataSnapshot> children = dataSnapshot.child("rounds")
                                .getChildren().iterator();

                        while (children.hasNext()) {
                            DataSnapshot snapChild = (DataSnapshot) children.next();

                            if (!snapChild.getKey().equals("current")) {
                                roundList.add(snapChild.getKey());
                            }

                        }

                        if (roundList.size() > 0) {

                            currRound = roundList.get(0);
                            rounds.child("current").setValue(currRound);
                            playRoom.child(playerType).child("ready")
                                    .removeEventListener(readyListener);
                            rounds.child("current").addListenerForSingleValueEvent
                                    (roundListener);

                        }

                        model.setPlayer(player);
                        model.setEnemy(enemy);
                    }


                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }

        );

    }//end assignPlayers


    private String currReq;

    private int drawResult;
    private int drawRequest;


    CountDownTimer showRequest = new CountDownTimer(5000, 1000) {
        @Override
        public void onTick(long l) {

            reqTxt.setText(timeout + " " + (int) (l / 1000));

            if (timeout != null && timeout.equals("somebody responded")) {
                this.onFinish();
                timeout = null;
                rounds.child(currRound).child("respond").removeEventListener
                        (respListener);


            }
        }

        @Override
        public void onFinish() {

            pBtn.setEnabled(false);
            dBtn.setEnabled(false);
            epBtn.setEnabled(false);
            edBtn.setEnabled(false);

            reqTxt.setText(timeout + " xx");
            if (timeout != null && timeout.equals("no respond")) {
                getVideo(R.raw.xxres);
//                count = 0;
            }


            showResult.start();
            timeout = null;
        }
    };


    private String playerResp;
    private String enemyResp;
    private boolean hasResult = false;


    CountDownTimer showResult = new CountDownTimer(1000, 1000) {


        @Override
        public void onTick(long l) {
            reqTxt.setText("result");

        }

        //where animation should be
        @Override
        public void onFinish() {
//            showRequest.cancel();

            if (winner != null) {
                playRoom.child("completed").child(currRound).setValue(winner.getName());
                rounds.child(currRound).child("result").setValue(winner.getName());
            } else {
                playRoom.child("completed").child(currRound).setValue("draw");
                rounds.child(currRound).child("result").setValue("draw");
            }

            hasResult = true;
            pBtn.setEnabled(false);
            dBtn.setEnabled(false);
            epBtn.setEnabled(false);
            edBtn.setEnabled(false);

//            rounds.child(currRound).child("request").removeEventListener(reqsListener);
//            rounds.child(currRound).child("respond").removeEventListener(respListener);
//            rounds.child("current").removeEventListener(roundListener);


            if (roundList.size() > 1) {
                roundList.remove(0);
                rounds.child("current").setValue(roundList.get(0));
                rounds.child("current").addListenerForSingleValueEvent(roundListener);

            } else {

                getVideo(R.raw.over);
                rounds.child("current").removeEventListener(roundListener);

                playRoom.child(playerType).child("score").setValue(playerScore);
                playRoom.child(playerType).child("hit").setValue(playerHit);

                playRoom.child(enemyType).child("score").setValue(enemyScore);
                playRoom.child(enemyType).child("hit").setValue(enemyHit);

                        lastShow.start();

//                playRoom.setValue(null);

            }


        }
    };

    private CountDownTimer lastShow = new CountDownTimer(1000, 1000) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {

            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);

            intent.putExtra("player_type", playerType);
            intent.putExtra("enemy_type", playerType);
            intent.putExtra("room_id", roomId);


            intent.putExtra("player_score", playerScore);
            intent.putExtra("player_hit", playerHit);
            intent.putExtra("enemy_score", enemyScore);
            intent.putExtra("enemy_hit", enemyHit);

            startActivity(intent);

            lastShow.cancel();
            finish();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
//        model.login(getApplicationContext(),model.getPlayer());

    }

    @Override
    protected void onStop() {
        super.onStop();
        playRoom.child(playerType).child("ready").setValue(null);
        playRoom.child(enemyType).child("ready").setValue(null);
//        playRoom.child("rounds").setValue(null);
//        model.logout(getApplicationContext(),model.getPlayer());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playRoom.child(playerType).child("ready").setValue(null);
        playRoom.child(enemyType).child("ready").setValue(null);
//        playRoom.child("rounds").setValue(null);
//        model.logout(getApplicationContext(),model.getPlayer());

    }

    public void getVideo(int res) {

        if (res == R.drawable.ready ||
                res == R.drawable.go || res == R.drawable.def) {
            imagePlayer.setVisibility(View.VISIBLE);
            imagePlayer.setImageDrawable(ContextCompat.
                    getDrawable(getApplicationContext(), res));

            imagePlayer.setImageDrawable(ContextCompat.
                    getDrawable(getApplicationContext(), res));


        } else {

            videoPlayer = (VideoView) findViewById(R.id.video_player_view);
            videoPlayer.setVisibility(View.VISIBLE);
            video = Uri.parse("android.resource://" + getPackageName() + "/" + res);

            this.getWindowManager().getDefaultDisplay().getMetrics(dm);
            int height = dm.heightPixels;
            int width = dm.widthPixels;
            videoPlayer.setMinimumWidth(width);
            videoPlayer.setMinimumHeight(height);
            videoPlayer.setVideoURI(video);
            videoPlayer.start();
            imagePlayer.setVisibility(View.GONE);

        }


    }
    @Override
    protected void onResume() {
        super.onResume();
//        model.login(getApplicationContext(), model.getPlayer());

    }
}
