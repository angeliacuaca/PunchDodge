package com.lagxen.punchdodge.controller;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lagxen.punchdodge.R;
import com.lagxen.punchdodge.model.User;

import java.util.ArrayList;

/**
 * Created by angeliacuaca on 10/10/16.
 */
public class PlayerListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private Drawable defaultImg;

    private ArrayList<User> playerList;

    public PlayerListAdapter(Context c, ArrayList<User> users) {
        this.playerList = users;
        layoutInflater = LayoutInflater.from(c);
        this.context = c;
        defaultImg = ContextCompat.getDrawable(c, R.drawable.defaultimg);
    }


    @Override
    public int getCount() {
        return playerList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = layoutInflater.inflate(R.layout.row_player,viewGroup,false);

        TextView name = (TextView) view.findViewById(R.id.playerName);
        ImageView image = (ImageView) view.findViewById(R.id.playerImg);

        name.setText(playerList.get(i).getName());
        String imageUrl = playerList.get(i).getImage();

        if (imageUrl != null) new ImageLoadTask(imageUrl, image).execute();
        else image.setImageDrawable(defaultImg);

        return view;
    }

    public void updateList(ArrayList<User> users){
        playerList=users;
        notifyDataSetChanged();
    }
}
