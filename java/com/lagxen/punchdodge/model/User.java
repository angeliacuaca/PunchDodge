package com.lagxen.punchdodge.model;

import android.content.Context;

import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by angeliacuaca on 10/10/16.
 */
public class User {

    private String id;
    private String name;
    private String image;
    private String roomId;
    private long score;
    private long hit;


    public User() {}

    ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getScore() {return this.score;}

    public void setScore(long score) {this.score = score;}

    public long getHit() {return this.hit;}

    public void setHit(long hit) {this.hit = hit;}


    public void setRoomId(String roomId) {this.roomId = roomId;}

    public String getRoomId() {return this.roomId;}

    public void queue() {

        Firebase roomRef;
        Firebase waitRef = new Firebase("https://lagxen-punchdodge.firebaseio" + "" +
                ".com/waitingRoom");

        roomRef = waitRef.push();
        this.roomId = roomRef.getKey();

        waitRef = waitRef.child(roomId).child("player1");
        waitRef.setValue(this);
    }

    public void move(String roomID, String playerType) {

        Firebase waitRef = new Firebase("https://lagxen-punchdodge.firebaseio" + "" +
                ".com/waitingRoom");

        waitRef = waitRef.child(roomID).child(playerType);
        waitRef.setValue(this);
    }

    //this is for presistent
    public void save() {

        Firebase userRef = new Firebase("https://lagxen-punchdodge.firebaseio" + "" +
                ".com/users");
        userRef = userRef.child(getId());
        userRef.setValue(this);
    }


}
