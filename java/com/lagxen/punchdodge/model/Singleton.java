package com.lagxen.punchdodge.model;

import android.content.Context;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.lagxen.punchdodge.controller.cloud.UserAsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angeliacuaca on 16/10/16.
 */
public class Singleton {
    private static Singleton ourInstance = null;

    public static Singleton getInstance() {

        if (ourInstance == null) {
            ourInstance = new Singleton();
        }
        return ourInstance;
    }

    private Singleton() {
    }

    private User player, enemy;
    private List<User> userList;

    public User getPlayer() {return this.player;}

    public User getEnemy() {return this.enemy;}

    public void setPlayer(User player) {this.player = player;}

    public void setEnemy(User enemy) {this.enemy = enemy;}

    public List<User> getUserList() {return this.userList;}

    public void setUserList(List<User> list) {this.userList = list;}

    public void logout(Context context,User user) {
        Firebase.setAndroidContext(context);
        Firebase userRef = new Firebase("https://lagxen-punchdodge.firebaseio" + "" +
                ".com/active");

        if(user!=null)
        userRef.child(user.getId()).setValue(null);

//        getFromFirebase(context);
//        new UserAsyncTask(context, player.getId(), "update").execute();

    }

    public void login(Context context, User user) {
        Firebase.setAndroidContext(context);
        Firebase userRef = new Firebase("https://lagxen-punchdodge.firebaseio" + "" +
                ".com/active");
        if (user != null) userRef.child(player.getId()).setValue(user);

    }

    public void getFromFirebase(Context context) {
        Firebase.setAndroidContext(context);
        Firebase userRef = new Firebase("https://lagxen-punchdodge.firebaseio" + "" +
                ".com/users");

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
    }
}
