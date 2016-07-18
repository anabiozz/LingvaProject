package com.almexe.lingvaproject.utils;

import android.os.AsyncTask;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

public class VkResponse extends AsyncTask<Void, Void, Void> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //linlaHeaderProgress.setVisibility(View.VISIBLE);

    }

    @Override
    protected Void doInBackground(Void... voids) {

        VKRequest request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "photo_200"));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                    /*user = ((VKList<VKApiUser>) response.parsedModel).get(0);

                    Driver.headerName.setText(user.first_name);
                    Driver.headerLastName.setText(user.last_name);

                    Picasso.with(getActivity()).load(user.photo_200).
                            transform(new CircleTransform()).into(Driver.image);

                    Driver.image.setVisibility(View.VISIBLE);

                    if(!userDb.isRowExists(user.id)){
                        UserDb.user_id = user.id;
                        userDb.write();

                        Tables.setTableMain("user" + "_" + user.id);

                        mainDbForUser.createTable(Tables.getTableMain());
                        mainDbForUser.insert(Tables.getTableMain());

                        if (mainDbForUser.getCountLessonWordsFromTen(Tables.getTableMain(), MainDbForUser.TEN) != 10) {

                            for (int i = 0; i < 10; i++) {

                                int result = mainDbForUser.getNumber(i, Tables.getTableMain());

                                mainDbForUser.update(Tables.getTableMain(), MainDbForUser.TEN, mainDb.getIdForeginWord(mainDb.getWord(result, 2)));
                            }
                        }
                        Driver.numberlLearnedWords.setText(String.valueOf(mainDbForUser.getCountLessonWordsFromTen(Tables.getTableMain(), MainDbForUser.LEARNED)));
                    }else {

                        Tables.setTableMain("user" + "_" + user.id);
                        Driver.numberlLearnedWords.setText(String.valueOf(mainDbForUser.getCountLessonWordsFromTen(Tables.getTableMain(), MainDbForUser.LEARNED)));
                    }*/
            }
        });
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //linlaHeaderProgress.setVisibility(View.GONE);
    }
}
