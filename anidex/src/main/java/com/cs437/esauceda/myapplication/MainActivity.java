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

import cz.msebera.android.httpclient.Header;

public class MainActivity extends Activity {
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AnimalDbHelper mydb = new AnimalDbHelper(this);
        mydb.clearDB(mydb.getWritableDatabase());
        mydb.insertAnimal("Dog", "Canis lupus familiaris",
                "Although initially thought to have originated as a manmade variant of an extant " +
                "canid species (variously supposed as being the dhole, golden jackal,or gray wolf), " +
                "extensive genetic studies undertaken during the 2010s indicate that dogs diverged from an " +
                "extinct wolf-like canid in Eurasia 40,000 years ago.",
                "The domestic dog is a domesticated canid which has been selectively bred for " +
                "millennia for various behaviors, sensory capabilities, and physical attributes.",
                "http://images5.fanpop.com/image/photos/25600000/DOG-ssssss-dogs-25606625-1024-768.jpg");
        mydb.insertAnimal("Cat", "Felis catus", "Since cats were venerated in ancient Egypt, they were " +
                "commonly believed to have been domesticated there, but there may have been instances " +
                "of domestication as early as the Neolithic from around 9,500 years ago (7,500 BCE). " +
                "A genetic study in 2007 concluded that domestic cats are descended from Near Eastern " +
                "wildcats, having diverged around 8,000 BCE in West Asia.", "The domesticated cat or the undomesticated cat" +
                " is a small, typically furry, carnivorous mammal. They are often called house cats " +
                "when kept as indoor pets or simply cats when there is no need to distinguish them " +
                "from other felids and felines.", "http://static3.shop033.com/resources/18/160536/picture/16/85402902.jpg");
        mydb.insertAnimal("Fish", "Goldus Fishus", "Starting in ancient China, various species of carp (collectively known as" +
                " Asian carp) have been domesticated and reared as food fish for thousands of years. Some of these normally gray" +
                " or silver species have a tendency to produce red, orange or yellow colour mutations; this was first recorded " +
                "during the Jin dynasty (265–420).", "The goldfish is a freshwater fish in the family Cyprinidae of " +
                "order Cypriniformes. It was one of the earliest fish to be domesticated, and is one of the most commonly kept " +
                "aquarium fish.", "http://img14.deviantart.net/8959/i/2007/338/8/d/goldfish_1600x1200_by_kira_r.jpg");
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
