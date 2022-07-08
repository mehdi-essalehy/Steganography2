package com.project.steganography2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class ImageFileSelectionForExtraction extends AppCompatActivity {

    static final int REQUEST_IMAGE = 1;
    static Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_file_selection_for_extraction);
    }

    public void sendMessage(View view) {
        try {
            // create intent to move data from SecondActivity to ThirdActivity
            Intent intent = new Intent(this, ImageExtraction.class);
            // add Image Uri String to intent
            intent.putExtra("IMAGE", imageUri.toString());
            // start the intent(go to ThirdActivity)
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
        intent.setType("image/png");

        // Only pick openable and local files
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        // start intent
        //noinspection deprecation
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the user doesn't pick a file just return
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_IMAGE || resultCode != RESULT_OK) {
            return;
        }
        imageUri = data.getData();
    }

}