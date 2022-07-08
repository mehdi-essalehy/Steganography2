package com.project.steganography2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TextInsertionExtraction extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_insertion_extraction);
    }

    public void goToExtraction(View view) {
        // go to SecondActivity
        Intent intent = new Intent(this, TextFileSelectionForExtraction.class);
        startActivity(intent);
    }

    public void goToInsertion(View view) {
        // go to FourthActivity
        Intent intent = new Intent(this, TextFileSelectionForInsertion.class);
        startActivity(intent);
    }
}