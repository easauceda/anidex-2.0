package com.cs437.esauceda.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class ReviewEntry extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_entry);
        String name = getIntent().getExtras().getString("name");

        AnimalDbHelper mydb = new AnimalDbHelper(this);

        HashMap<String, String> animal = mydb.getAnimal(name);

        TextView nameTextView = (TextView) findViewById(R.id.nameTV);
        TextView sciNameTextView = (TextView) findViewById(R.id.sciNameTV);
        TextView originTextView = (TextView) findViewById(R.id.originTV);
        TextView descTextView = (TextView) findViewById(R.id.descTV);

        // Code for image view display

        String imagePath = animal.get("image_url");
        new retreiveImage().execute(imagePath);

        // Using html to create what will be displayed on the screen
        String nameLabel = "<b>Animal Recognized: </b>" + animal.get("animal_name");
        String sciNameLabel = "<b>Animal Scientific Name: </b>" + animal.get("scientific_name");
        String originLabel = "<b>Animal Origin: </b>" + animal.get("origin");
        String descriptionLabel = "<b>Animal Description: </b>" + animal.get("description");

        // Sets the html text with the TextViews
        nameTextView.setText(Html.fromHtml(nameLabel));
        sciNameTextView.setText(Html.fromHtml(sciNameLabel));
        originTextView.setText(Html.fromHtml(originLabel));
        descTextView.setText(Html.fromHtml(descriptionLabel));

        Button closeButton = (Button) findViewById(R.id.confirm);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class retreiveImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(urls[0]);
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView imageView = (ImageView) findViewById(R.id.imgView);
            imageView.setImageBitmap(bitmap);
        }
    }

}
