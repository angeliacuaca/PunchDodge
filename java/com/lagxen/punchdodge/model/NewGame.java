package com.lagxen.punchdodge.model;

import java.util.Random;

/**
 * Created by angeliacuaca on 19/09/16.
 * This Class generate 10(or depend on round requested) punch and dodge and submit it to Firebase
 */
public class NewGame {

    private String room;
    private Player p1 = new Player("A");
    private Player p2 = new Player("B");

    private int round = 10;

    Random random = new Random();
    boolean rand, randReqs;
    boolean start = true;


    public NewGame(String room,Player p1, Player p2){
        //check how many room available first before input above parameter
        //in main activity ask to accept connection, retrive player A and B

        this.room=room;

    }

    public void gameStart(){
        int currRound = 0;

        while(start == true){

            rand = random.nextBoolean();

            if(rand == true){
                //generate pd
            }else{
                //generate dp
            }



        }


    }

}
