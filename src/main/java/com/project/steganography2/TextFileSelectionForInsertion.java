package com.project.steganography2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TextFileSelectionForInsertion extends AppCompatActivity {

    static final int REQUEST_IMAGE = 1;
    static final int REQUEST_TEXT  = 2;
    static Uri imageUri = null;
    static Uri textUri  = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_file_selection_for_insertion);
    }

    public void sendMessage(View view) {
        try {
            // create intent to move data from FourthActivity to FifthActivity
            Intent intent = new Intent(this, TextInsertion.class);
            // add Image Uri String and Text to intent
            intent.putExtra("IMAGE", imageUri.toString());
            intent.putExtra("TEXT", getTextContent());
            // start the intent(go to FifthActivity)
            startActivity(intent);
        } catch (Exception e) {
            Context context = getApplicationContext();
            CharSequence toastText = e.toString();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, toastText, duration);
            toast.show();
            e.printStackTrace();
        }
    }

    public void showImageChooser(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        // Update with mime types
        intent.setType("image/jpeg");

        // Only pick openable and local files
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        // start intent
        //noinspection deprecation
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    public void showTextChooser(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        // Update with mime types
        intent.setType("text/plain");

        // Only pick openable and local files
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        // start intent
        //noinspection deprecation
        startActivityForResult(intent, REQUEST_TEXT);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the user doesn't pick a file just return
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode != REQUEST_IMAGE && requestCode != REQUEST_TEXT) || resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_IMAGE) {
            imageUri = data.getData();
        }
        if (requestCode == REQUEST_TEXT){
            textUri = data.getData();
        }
    }

    public String getTextContent() {
        String res = null;
        // if text file has been selected (textUri != null) get file content and set it to result
        if (textUri != null) {
            try {
                InputStream in = getContentResolver().openInputStream(textUri);

                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                StringBuilder total = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    total.append(line).append('\n');
                }
                res = total.toString();
            } catch (IOException e) {
                Context context = getApplicationContext();
                CharSequence toastText = e.toString();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, toastText, duration);
                toast.show();
                e.printStackTrace();
            }
        }
        // if text file has not been selected (textUri == null) get EditText content and set it to result
        else {
            EditText input = findViewById(R.id.EditText);
            res = input.getText().toString();
        }
        // return result
        return res;
    }

}