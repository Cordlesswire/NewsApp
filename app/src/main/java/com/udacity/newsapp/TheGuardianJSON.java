package com.udacity.newsapp;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.udacity.newsapp.dummy.DummyContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by geovani on 10/11/16.
 */

public class TheGuardianJSON {

    private final static String TAG = TheGuardianJSON.class.getSimpleName();


    /**
     * @param newsJSON
     * @return
     */
    public static List<DummyContent.DummyItem> extractFeatureFromJson(String newsJSON) {
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }
        //
        List<DummyContent.DummyItem> newses = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(newsJSON);
            JSONObject response = root.optJSONObject("response");
            int total = response.optInt("total");
            if (total == 0) {
                Log.i(TAG, "No Result Found :(");
                return null;
            }
            //
            JSONArray results = response.optJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                DummyContent.DummyItem news = createDummyItem(results, i);
                newses.add(news);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newses;
    }


    @NonNull
    private static DummyContent.DummyItem createDummyItem(JSONArray results, int i) {
        JSONObject currentResult = results.optJSONObject(i);
        //
        String webTitle           = currentResult.optString("webTitle");
        String sectionName        = currentResult.optString("sectionName");
        String webPublicationDate = currentResult.optString("webPublicationDate");
        Log.i(TAG, "webTitle: " + webTitle + " | " + "sectionName: " + sectionName + " | " + "webPublicationDate: " + webPublicationDate);
        //
        return new DummyContent.DummyItem(String.valueOf(i), webTitle, webPublicationDate, sectionName, "details");
    }


}
