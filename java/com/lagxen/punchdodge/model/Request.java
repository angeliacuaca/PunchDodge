package com.lagxen.punchdodge.model;

import com.lagxen.punchdodge.R;


import java.util.HashMap;
import java.util.Random;

/**
 * Created by angeliacuaca on 19/09/16.
 * This Class take request from Firebase and match it with respond from user
 */
public class Request {

    private String request;
    private char p1, p2, r1, r2;

    private int dpreq = R.raw.dpreq;
    private int pdreq = R.raw.pdreq;
    private int ppreq = R.raw.ppreq;

    private int dpres = R.raw.dpres;
    private int pdres = R.raw.pdres;

    private int opres = R.raw.opres;
    private int pores = R.raw.pores;

    private int xpres = R.raw.xpres;
    private int pxres = R.raw.pxres;

    private int ppres = R.raw.ppres;
    private int xxres = R.raw.xxres;

    private int pdreqddres = R.raw.pdreqddres;
    private int dpreqddres = R.raw.dpreqddres;
    private int ppreqddres = R.raw.ppreqddres;

    private int odres = R.raw.odres; //when enemy punch but dodge instead
    private int dores = R.raw.dores; //when you punch but dodge instead

    private int oxres = R.raw.oxres;
    private int xores = R.raw.xores;

    private int dxres = R.raw.dxres;
    private int xdres = R.raw.xdres;

    private int ready = R.drawable.ready;
    private int def = R.drawable.def;
    private int go = R.drawable.go;
    private int over = R.raw.over;

    public Request() {

    }

    //play request
    public int play(String req) {
        int output;

        switch (req) {

            case "ready":
                output = ready;
                break;

            case "go":
                output = go;
                break;

            case "pd":
                output = pdreq;
                break;

            case "dp":
                output = dpreq;
                break;

            case "pp":
                output = ppreq;
                break;

            case "xx":
                output = xxres;
                break;

            case "over":
                output = over;
                break;

            default:
                output = def;

        }


        return output;
    }

    //calculate and play result
    public int check(String request, char p1, char p2) {
        this.request = request;
        this.p1 = p1;
        this.p2 = p2;
        this.r1 = request.charAt(0);
        this.r2 = request.charAt(1);

        int draw = def;

        //if request dp
        if (r1 == 'p' && r2 == 'd') {

            if (p1 == 'p' && p2 == 'd') draw = pdres; // 1p 2d  no penalty
            else if (p1 == 'd' && p2 == 'p') draw = dxres; //1d 2p no penalty

            else if (p1 == 'p' && p2 == 'o') draw = pores;  // 1p 2o 1 scored
            else if (p1 == 'o' && p2 == 'p') draw = oxres;  // 1o 2p nothing happened

            else if (p1 == 'p' && p2 == 'p') draw = pxres; //1p 2x 1 scored
            else if (p1 == 'd' && p2 == 'd') draw = pdreqddres; //both dodge no penalty

            else if (p1 == 'd' && p2 == 'o') draw = dores; // 1d 2o
            else if (p1 == 'o' && p2 == 'd') draw = odres;  //1o 2d
        }

        //if request dp
        if (r1 == 'd' && r2 == 'p') {

            if (p1 == 'd' && p2 == 'p') draw = dpres;     // 1d 2p  no penalty
            else if (p1 == 'p' && p2 == 'd') draw = xdres; //1p 2d no penalty


            else if (p1 == 'o' && p2 == 'p') draw = opres;   // 1o 2p  2scored
            else if (p1 == 'p' && p2 == 'o') draw = xores;  //1p 2o nothing happened

            else if (p1 == 'p' && p2 == 'p') draw = xpres; //1x 2p 2 scored
            else if (p1 == 'd' && p2 == 'd') draw = dpreqddres; //both dodge no penalty

            else if (p1 == 'o' && p2 == 'd') draw = odres;  // 1o 2d 1 no penalty
            else if (p1 == 'd' && p2 == 'o') draw = dores;  // 1d 2o no penalty

        }

        //if request pp
        if (r1 == 'p' && r2 == 'p') {

            if (p1 == r1 && p2 == r2) draw = ppres;   // 1p 2p  both scored

            else if (p1 == 'd' && p2 == 'd') draw = ppreqddres;

            else if (p1 == 'p' && p2 == 'd') draw = pores; //1p 2o 1 scored //can't dodge
            else if (p1 == 'd' && p2 == 'p') draw = opres; //1o 2p 2 scored //can't dodge

            else if (p1 == 'p' && p2 == 'o') draw = pores;
            else if (p1 == 'o' && p2 == 'p') draw = opres;

            else if (p1 == 'd' && p2 == 'o') draw = dores;
            else if (p1 == 'o' && p2 == 'd') draw = odres;
        }

        return draw;
    }
}