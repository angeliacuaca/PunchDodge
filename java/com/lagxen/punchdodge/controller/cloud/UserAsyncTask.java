package com.lagxen.punchdodge.controller.cloud;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lagxen.punchdodge.model.Singleton;
import com.lagxen.punchdodgeg.backend.userApi.UserApi;
import com.lagxen.punchdodgeg.backend.userApi.model.User;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class UserAsyncTask extends AsyncTask<Void, Void, User> {
    private static UserApi myApiService = null;
    private Context context;

    private User user;
//    private  clientUser;

    private String userId, req;
    private User res;

    Singleton model = Singleton.getInstance();

    static Logger Log = Logger.getLogger("ANGELIA READ THIS:");

    public UserAsyncTask(Context context, String userId, String req) {
        this.context = context;
        this.userId = userId;
        this.req = req;
    }


    @Override
    protected User doInBackground(Void... params) {
        if (myApiService == null) { // Only do this once
            UserApi.Builder builder = new UserApi.Builder(AndroidHttp
                    .newCompatibleTransport(), new AndroidJsonFactory(), null)

                    .setRootUrl("https://lagxen-punchdodge.appspot.com/_ah/api/")

                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?>
                                                       abstractGoogleClientRequest)
                                throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });

            myApiService = builder.build();
        }

        if (req.equals("update")) {

            try {

                com.lagxen.punchdodge.model.User clientUser = model.getPlayer();
                User user = new User();
                user.setId(clientUser.getId());
                user.setName(clientUser.getName());
                user.setImage(clientUser.getImage());
                user.setScore(clientUser.getScore());
                user.setHit(clientUser.getHit());

                return myApiService.updateUser(user).execute();
            } catch (IOException e) {
                e.printStackTrace();
                Log.info("failed update");
                return user;
            }

        } else {
            if (req.equals("insert"))

                try {

                    com.lagxen.punchdodge.model.User clientUser = model.getPlayer();
                    User user = new User();
                    user.setId(clientUser.getId());
                    user.setName(clientUser.getName());
                    user.setImage(clientUser.getImage());
                    user.setScore(clientUser.getScore());
                    user.setHit(clientUser.getHit());

                    return myApiService.insertUser(user).execute();
                } catch (IOException e1) {
                    Log.info(e1.toString());

                    try {

                        return myApiService.getUser(userId).execute();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.info("failed get");

                    }

                }
        }
        return user;

    }

    @Override
    protected void onPostExecute(User result) {


        com.lagxen.punchdodge.model.User clientUser = new com.lagxen.punchdodge.model
                .User();
        clientUser.setId(result.getId());
        clientUser.setName(result.getName());
        clientUser.setScore(result.getScore());
        clientUser.setHit(result.getHit());

        //if user change fb image
        if (result.getImage().equals(model.getPlayer().getImage()))
            clientUser.setImage(result.getImage());
        else clientUser.setImage(model.getPlayer().getImage()); //newer


        Firebase.setAndroidContext(context);
        Firebase userRef = new Firebase("https://lagxen-punchdodge.firebaseio" + "" +
                ".com/users");

        userRef.child(clientUser.getId()).setValue(clientUser);

        model.setPlayer(clientUser);
        Toast.makeText(context, result.getName(), Toast.LENGTH_SHORT).
                show();

    }


}