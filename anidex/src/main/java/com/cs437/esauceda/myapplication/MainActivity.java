package com.cs437.esauceda.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import local.org.apache.http.Header;


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
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            uploadImage(bitmap);
        }
    }

    private void getLabel(String id) {
        String apiKey = "acc_e4fd5656fd09c82";
        String apiSecret = "03473fd5baf817098e016df08eef5518";
        String url = "https://api.imagga.com/v1/tagging";
        RequestParams params = new RequestParams();

        AsyncHttpClient client = new AsyncHttpClient();
        client.setBasicAuth(apiKey, apiSecret);
        params.put("content", id);
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                String s = new String(responseBody);
                System.out.println(s);
                JSONObject json = null;
                try {
                    String name = "";
                    json = new JSONObject(s);
                    JSONArray results = json.getJSONArray("results");
                    JSONObject img_info = results.getJSONObject(0);
                    JSONArray tags = img_info.getJSONArray("tags");
                    JSONObject first_tag = tags.getJSONObject(0);
                    name = first_tag.get("tag").toString();
                    getInfo(name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                String s = new String(responseBody);
                System.out.println("failure");
                System.out.println(s);
            }
        });
    }

    private void getInfo(final String name) {
        System.out.println(name);
        final AnimalDbHelper mydb = new AnimalDbHelper(this);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://sauceda.me:3000/" + name, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                String result;
                String name;
                String sciName;
                String origin;
                String desc;
                String path;
                try {
                    result = new String(responseBody, "UTF-8");
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
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                openDialog(name);
            }

        });
    }

    private String uploadImage(Bitmap bitmap) {
        String apiKey = "acc_e4fd5656fd09c82";
        String apiSecret = "03473fd5baf817098e016df08eef5518";
        String url = "https://api.imagga.com/v1/content";
        RequestParams params = new RequestParams();
        final String[] id = {""};

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 85, out);
        byte[] myByteArray = out.toByteArray();
        params.put("image", new ByteArrayInputStream(myByteArray), "image.png");

        AsyncHttpClient client = new AsyncHttpClient();
        client.setBasicAuth(apiKey, apiSecret);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                String s = new String(responseBody);
                JSONObject json = null;
                try {
                    json = new JSONObject(s);
                    JSONArray uploaded = json.getJSONArray("uploaded");
                    JSONObject json_id = uploaded.getJSONObject(0);
                    id[0] = json_id.get("id").toString();
                    getLabel(id[0]);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                //System.out.println("Failure on upload");

            }
        });
        return id[0];
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

    public void openDialog(String name) {
        final Dialog dialog = new Dialog(this); // Context, this, etc.
        dialog.setContentView(R.layout.dialog_demo);
        dialog.setTitle(R.string.dialog_title);
        TextView text = (TextView) dialog.findViewById(R.id.dialog_info);
        text.setText("Could not find:" + name);

        Button dialogButton = (Button) dialog.findViewById(R.id.dialog_ok);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
