package com.lagxen.punchdodge.controller.cloud;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.lagxen.punchdodge.model.Singleton;
import com.lagxen.punchdodgeg.backend.userApi.UserApi;
import com.lagxen.punchdodgeg.backend.userApi.model.User;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class UserListAsyncTask extends AsyncTask<Void, Void, List<com.lagxen
        .punchdodgeg.backend.userApi.model.User>> {
    private static UserApi myApiService = null;
    private Context context;

    private com.lagxen.punchdodgeg.backend.userApi.model.User user;
    private com.lagxen.punchdodge.model.User clientUser;

    private String userId, req;
    private List<User> res;

    Singleton model = Singleton.getInstance();

    public UserListAsyncTask(Context context, String userId, String req) {
        this.context = context;
        this.userId = userId;
        this.req = req;
    }


    @Override
    protected List<com.lagxen.punchdodgeg.backend.userApi.model.User> doInBackground
            (Void... params) {
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

        try {
            return myApiService.listUser().execute().getItems();


        } catch (IOException e) {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    protected void onPostExecute(List<com.lagxen.punchdodgeg.backend.userApi.model
            .User> result) {

        res = result;
        boolean exists = false;
        for (com.lagxen.punchdodgeg.backend.userApi.model.User q : result) {

            Toast.makeText(context, q.getName() + ":"+q.getScore(), Toast.LENGTH_LONG).
                    show();

            if (q.getId().equals(userId)) {

                clientUser = new com.lagxen.punchdodge.model.User();
                clientUser.setId(q.getId());
                clientUser.setName(q.getName());
                clientUser.setImage(q.getImage());
                clientUser.setScore(q.getScore());
                clientUser.setHit(q.getHit());

                model.setPlayer(clientUser);
                exists = true;
                break;

            }

        }

        if (exists == false) {
            Toast.makeText(context, "Create new user", Toast.LENGTH_LONG).show();

//            clientUser = model.getPlayer();
//            user = new User();
//            user.setId(clientUser.getId());
//            user.setName(clientUser.getName());
//            user.setImage(clientUser.getImage());
//            user.setScore(clientUser.getScore());
//            user.setHit(clientUser.getHit());
//
//            try {
//                myApiService.insertUser(user).execute();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }

    }

    private void findUser(String id) {
        for (com.lagxen.punchdodgeg.backend.userApi.model.User q : res) {
//            Toast.makeText(context, q.getName() + ":"+q.getScore(), Toast.LENGTH_LONG).
//                    show();

            if (q.getId().equals(userId)) {

                clientUser = new com.lagxen.punchdodge.model.User();
                clientUser.setId(q.getId());
                clientUser.setName(q.getName());
                clientUser.setImage(q.getImage());
                clientUser.setScore(q.getScore());
                clientUser.setHit(q.getHit());

                model.setPlayer(clientUser);

                break;
            }

        }

    }
}