package com.project.steganography2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ImageInsertionExtraction extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_insertion_extraction);
    }

    public void goToExtraction(View view) {
        // go to SecondActivity
        Intent intent = new Intent(this, ImageFileSelectionForExtraction.class);
        startActivity(intent);
    }

    public void goToInsertion(View view) {
        // go to FourthActivity
        Intent intent = new Intent(this, ImageFileSelectionForInsertion.class);
        startActivity(intent);
    }
}
