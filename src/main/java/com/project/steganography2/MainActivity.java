package com.project.steganography2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToImage(View view) {
        // go to SecondActivity
        Intent intent = new Intent(this, ImageInsertionExtraction.class);
        startActivity(intent);
    }

    public void goToText(View view) {
        // go to FourthActivity
        Intent intent = new Intent(this, TextInsertionExtraction.class);
        startActivity(intent);
    }
}