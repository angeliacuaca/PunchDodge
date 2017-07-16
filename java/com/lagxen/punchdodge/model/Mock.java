package com.lagxen.punchdodge.model;

import com.firebase.client.Firebase;

import java.util.Random;

/**
 * Created by angeliacuaca on 10/10/16.
 */
public class Mock {

    private String roomId;
    private int roundCount;

    private final Firebase playRef = new Firebase("https://lagxen-punchdodge" +
            ".firebaseio" + ".com/playRoom");


    public Mock(String roomId, int count) {
        this.roomId = roomId;
        this.roundCount = count;
    }

    public void generate() {

        Firebase rounds = playRef.child(roomId).child("rounds");

        for (int i = 1; i <= roundCount; i++) {
            rounds.child("round" + i).child("request").setValue(randomize());
        }

        playRef.child(roomId).child("player1").child("ready").setValue("ready");
        playRef.child(roomId).child("player2").child("ready").setValue("ready");
    }

    public String randomize() {
        //3 requests
        String[] reqs = {"pd", "dp", "pp"};
        int rand = new Random().nextInt(3);
        return reqs[rand];
    }
}
