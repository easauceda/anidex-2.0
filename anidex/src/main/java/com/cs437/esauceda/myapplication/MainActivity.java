package com.cs437.esauceda.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends Activity {
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AnimalDbHelper mydb = new AnimalDbHelper(this);
        mydb.clearDB(mydb.getWritableDatabase());
        mydb.insertAnimal("Dog", "Canus Lupus", "Africa", "This is a damn dog, what do you want", "http://images5.fanpop.com/image/photos/25600000/DOG-ssssss-dogs-25606625-1024-768.jpg");
        mydb.insertAnimal("Cat", "Feline", "Africa", "This is a damn dog, what do you want", "http://static3.shop033.com/resources/18/160536/picture/16/85402902.jpg");
        mydb.insertAnimal("Fish", "Goldus Fishus", "Africa", "This is a damn dog, what do you want", "http://img14.deviantart.net/8959/i/2007/338/8/d/goldfish_1600x1200_by_kira_r.jpg");
        updateView(mydb);
    }

    public void newAnimal(View v) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            final AnimalDbHelper mydb = new AnimalDbHelper(this);
            AsyncHttpClient client = new AsyncHttpClient();
            client.get("http://sauceda.me:3000/horse", new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    // called before request is started
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    String result;
                    String name;
                    String sciName;
                    String origin;
                    String desc;
                    String path;
                    try {
                        result = new String(response, "UTF-8");
                        JSONObject info = new JSONObject(result);
                        name = info.getString("name");
                        sciName = info.getString("sciName");
                        origin = info.getString("origin");
                        desc = info.getString("description");
                        path = info.getString("imageUrl");
                        mydb.insertAnimal(name, sciName, origin, desc, path);
                        saveEntry(name);
                        updateView(mydb);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                }

                @Override
                public void onRetry(int retryNo) {
                    // called when request is retried
                }
            });
        }
    }

    public void saveEntry(String name){
        Intent animalEntry = new Intent(this, ReviewEntry.class);
        animalEntry.putExtra("name", name);
        startActivity(animalEntry);
    }

    public void updateView(AnimalDbHelper db){
        final ListView lv = (ListView) findViewById(R.id.listView);
        ArrayList array_list = db.getData();
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array_list);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = lv.getItemAtPosition(position);
                saveEntry(o.toString());
            }
        });
    }
}
