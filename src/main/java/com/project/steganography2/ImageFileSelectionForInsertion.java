package com.project.steganography2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class ImageFileSelectionForInsertion extends AppCompatActivity {

    static final int REQUEST_IMAGE1 = 1;
    static final int REQUEST_IMAGE2  = 2;
    static Uri imageUri1 = null;
    static Uri imageUri2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_file_selection_for_insertion);
    }

    public void sendMessage(View view) {
        try {
            // create intent to move data from FourthActivity to FifthActivity
            Intent intent = new Intent(this, ImageInsertion.class);
            // add Image Uri String and Text to intent
            intent.putExtra("IMAGE1", imageUri1.toString());
            intent.putExtra("IMAGE2", imageUri2.toString());
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
        intent.setType("image/*");

        // Only pick openable and local files
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        // start intent
        //noinspection deprecation
        startActivityForResult(intent, REQUEST_IMAGE1);
    }

    public void showTextChooser(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        // Update with mime types
        intent.setType("image/*");

        // Only pick openable and local files
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        // start intent
        //noinspection deprecation
        startActivityForResult(intent, REQUEST_IMAGE2);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the user doesn't pick a file just return
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode != REQUEST_IMAGE1 && requestCode != REQUEST_IMAGE2) || resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_IMAGE1) {
            imageUri1 = data.getData();
        }
        if (requestCode == REQUEST_IMAGE2){
            imageUri2 = data.getData();
        }
    }

}