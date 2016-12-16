package com.example.deansponholz.fish_game;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;



/**
 * Created by deansponholz on 12/10/16.
 */

public class InsertTask extends AsyncTask {
    private StringBuilder sb;
    private ProgressDialog pr;
    private HttpResponse req;
    private InputStream is;
    String line=null;
    String result=null;
    int code;
    String user_name, user_score;
    HUDFragment hudFragment = null;

    public InsertTask(){
        hudFragment = new HUDFragment();
        user_name = hudFragment.user_name;
        user_score = Integer.toString(hudFragment.user_score);
        Log.d("inputname", user_score);

    }

    @Override
    protected Object doInBackground(Object[] params) {
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("user_name", user_name));
        nameValuePairs.add(new BasicNameValuePair("user_score", user_score));



        try
        {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://cs-linuxlab-30/db_insert.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            Log.e("pass 1", "connection success ");
        }
        catch(Exception e)
        {
            Log.e("Fail 1", e.toString());

        }
        try
        {

            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                sb.append(line);
            }
            is.close();
            result = sb.toString();
            //"connectionsuccess"
            Log.e("pass 2", "connection success ");
        }
        catch(Exception e)
        {
            Log.e("Fail 2", e.toString());
        }
        try
        {
            JSONObject json_data = new JSONObject(result);
            code=(json_data.getInt("code"));

            if(code==1)
            {
                Log.d("Inserted Successfully","woot!");
            }
            else
            {
                Log.d("FAILURE INSERTING","dammit");
            }
        }
        catch(Exception e)
        {
            Log.e("Fail 3", e.toString());
        }
    return result;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }
}
