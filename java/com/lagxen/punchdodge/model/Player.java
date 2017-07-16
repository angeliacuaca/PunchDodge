package com.lagxen.punchdodge.model;

/**
 * Created by angeliacuaca on 19/09/16.
 */
public class Player {

    private String name;
    private String type;
    private int point;
    private boolean win;

    private char reqsCmd;
    private char sentCmd;


    public Player(String type){
        this.type=type;
        this.point=100;
    }

    public String getType(){
        return this.type;
    }

    public void reqsCmd(char reqs){
        this.reqsCmd=reqs;
    }

    boolean isSuccess=false;
    boolean isPunched =false;

    private String msgOut;


    public void sendCmd(char sent){
        this.sentCmd=sent;


        if(sentCmd==reqsCmd){
            this.isSuccess=true;

            //this can be player either: (maybe move these to Request class later)

            if (reqsCmd=='p'){
                // successfully deal a punch ADD POINT!
            }
            if(reqsCmd=='d') {
                // successfully dodge a punch
            }

        }
        else{

            this.isSuccess=false;

            //if punched? or missed?

            if (reqsCmd=='p'){
                // missed the chance to punch (TOO BAD)
            }
            if(reqsCmd=='d') {
                // missed the chance to dodge (GET PUNCHED) REDUCE POINT
                punched(10);
            }


        }

    }

public void punched(int pointReduced){
    this.point = this.point-pointReduced;
}


    public String reqsPrint(){
        String print = "none";
        if (reqsCmd=='p'){
            print = "Punch";
        }
        else if(reqsCmd=='d') {
            print = "Dodge";
        }else{
            print = "Wrong respond!";
        }

        return print;

    }

}
