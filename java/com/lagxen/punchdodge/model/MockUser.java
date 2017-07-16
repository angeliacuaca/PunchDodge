package com.lagxen.punchdodge.model;

import com.facebook.Profile;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by angeliacuaca on 11/10/16.
 */
public class MockUser {

    private final Firebase waitRef = new Firebase("https://lagxen-punchdodge" +
            ".firebaseio" + ".com/waitingRoom");

    public void asEnemy(final String userId, final String roomId) {

        final Firebase userRef = new Firebase("https://lagxen-punchdodge.firebaseio" +
                ".com/users");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator<DataSnapshot> children = dataSnapshot.getChildren().iterator();

                while (children.hasNext()) {
                    DataSnapshot snapChild = (DataSnapshot) children.next();
//                    HashMap<String,Object>
                    if (((String) snapChild.child("id").getValue()).equals((String)
                            userId)) {
                        waitRef.child(roomId).child("player2").setValue(snapChild
                                .getValue());
                        waitRef.child(roomId).child("player2").child("accepted")
                                .setValue("wait");
                        waitRef.child(roomId).child("player1").child("accepted")
                                .setValue("wait");
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }

    //generate mock user
    public void generate() {

        //push them to Waitingroom
        Firebase roomRef;
        String mockRoom;

        User u01 = new User();
        u01.setName("Fred");
        u01.setId("mockId01");

        roomRef = waitRef.push();
        mockRoom = roomRef.getKey();
        u01.setRoomId(mockRoom);
        waitRef.child(mockRoom).child("player1").setValue(u01);
        waitRef.child(mockRoom).child("player1").child("accepted").setValue("");

        User u02 = new User();
        u02.setName("Annie");
        u02.setId("mockId02");

        roomRef = waitRef.push();
        mockRoom = roomRef.getKey();
        u02.setRoomId(mockRoom);
        waitRef.child(mockRoom).child("player1").setValue(u02);

        User u03 = new User();
        u03.setName("zai-zai");
        u03.setId("mockId03");
        u03.setImage("https://scontent.fmel5-1.fna.fbcdn.net/v/t1" +
                ".0-9/12670624_10153623759962862_182016100718178192_n" +
                ".jpg?oh=2f5119e812ed7142ab096d981d26dd54&oe=59C830DB");

        roomRef = waitRef.push();
        mockRoom = roomRef.getKey();
        u03.setRoomId(mockRoom);
        waitRef.child(mockRoom).child("player1").setValue(u03);

        User u04 = new User();
        u04.setName("Winnie");
        u04.setId("mockId04");
        u04.setImage("https://scontent.fmel5-1.fna.fbcdn.net/v/t1" +
                ".0-1/p160x160/15241963_1277577948951785_257818222728596456_n" +
                ".jpg?oh=5af8cce9f483b5c864766e67ef417e7f&oe=5A109175");

        roomRef = waitRef.push();
        mockRoom = roomRef.getKey();
        u04.setRoomId(mockRoom);
        waitRef.child(mockRoom).child("player1").setValue(u04);

        User u05 = new User();
        u05.setName("Stanley");
        u05.setId("mockId05");

        roomRef = waitRef.push();
        mockRoom = roomRef.getKey();
        u05.setRoomId(mockRoom);
        waitRef.child(mockRoom).child("player1").setValue(u05);

        User u06 = new User();
        u06.setName("Vann Law");
        u06.setId("mockId06");
        u06.setImage("https://scontent.fmel5-1.fna.fbcdn.net/v/t1" +
                ".0-1/p160x160/10534182_10152386092458036_8990660869187214113_n" +
                ".jpg?oh=563681127a1fe6d443514a60fb3b6c7a&oe=59F57E71");

        roomRef = waitRef.push();
        mockRoom = roomRef.getKey();
        u06.setRoomId(mockRoom);
        waitRef.child(mockRoom).child("player1").setValue(u06);

        User u07 = new User();
        u07.setName("Eva");
        u07.setId("mockId07");
        u07.setImage("https://scontent.fmel5-1.fna.fbcdn.net/v/t1.0-1/c54.40.504" +
                ".504/s160x160/542286_10151015766097755_1841802617_n" +
                ".jpg?oh=20c782b3d3c45beb5cbb49ae3f758c7c&oe=5A0BF66C");

        roomRef = waitRef.push();
        mockRoom = roomRef.getKey();
        u07.setRoomId(mockRoom);
        waitRef.child(mockRoom).child("player1").setValue(u07);


        //push them to users
        Firebase users = new Firebase("https://lagxen-punchdodge.firebaseio" + "" +
                ".com/users");

        users.child(u01.getId()).setValue(u01);
        users.child(u02.getId()).setValue(u02);
        users.child(u03.getId()).setValue(u03);
        users.child(u04.getId()).setValue(u04);
        users.child(u05.getId()).setValue(u05);
        users.child(u06.getId()).setValue(u06);
        users.child(u07.getId()).setValue(u07);

    }
}
