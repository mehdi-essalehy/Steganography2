package com.project.steganography2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TextInsertion extends AppCompatActivity {
    Bitmap img = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_insertion);
        String imgUriStr = getIntent().getStringExtra("IMAGE");
        String text = getIntent().getStringExtra("TEXT");
        // create Image Uri from String
        Uri myUri = Uri.parse(imgUriStr);
        // make bitmap from Image Uri
        Bitmap mBitmap = null;
        try {
            mBitmap = MediaStore.Images.Media.getBitmap(this. getContentResolver(), myUri);
        } catch (IOException e) {
            Context context = getApplicationContext();
            CharSequence toastText = e.toString();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, toastText, duration);
            toast.show();
            e.printStackTrace();
        }
        // create copy of image with text inserted
        img = insertText(mBitmap , text);
        // get ImageView and display img in it
        ImageView present = findViewById(R.id.imageView1);
        present.setImageBitmap(img);
    }

    public void saveImage(View view) {
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        TextView FileName_TV = findViewById(R.id.FileNameForImage);
        String filename = FileName_TV.getText().toString() + ".png";
        File file = new File(root, filename);
        if (file.exists()) file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            Context context = getApplicationContext();
            CharSequence toastText = e.toString();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, toastText, duration);
            toast.show();
            e.printStackTrace();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            img.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            if (file.exists()) {
                Context context = getApplicationContext();
                CharSequence toastText = "Image successfully saved!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, toastText, duration);
                toast.show();
            }
        } catch (Exception e) {
            Context context = getApplicationContext();
            CharSequence toastText = e.toString();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, toastText, duration);
            toast.show();
            e.printStackTrace();
        }
    }

    public Bitmap insertText(Bitmap img, String txt) {
        if (img == null) return null;
        // create copy of image
        Bitmap res = img.copy(img.getConfig(), true);
        // get height and width
        int height = img.getHeight();
        int width  = img.getWidth();
        // convert the text to a binary string
        String binary = convertStringToBinary(txt);
        // get size in bits of text
        int size = binary.length();
        // convert size to binary string
        String binarySize = Integer.toBinaryString(size);
        String completeBinarySize = completeBin(binarySize);
        // declare color and substring variables
        int color;
        String substring;
        // first loop sets the 11 first pixels to code the size of the text
        int c = 0;
        for (int i = 0; i < 11 ; i++) {
            color = img.getPixel(i, 0);
            // get 3 bits from completeBinarySize, except when there are only 2 bits left, get the 2 bits and add a "0"
            if (c < 30) substring = completeBinarySize.substring(c, c+3);
            else substring = completeBinarySize.substring(c, c+2); substring = substring + "0";
            res.setPixel(i, 0, setColor(color, substring));
            c += 3;
            if (c >= 32) break;
        }
        // declare and initiate count and start main loop
        int count = 0;
        for (int i=0; i<height; i++) {
            for (int j=0; j<width; j++) {
                // ignore first 11 pixels
                if (j<11 && i==0) continue;
                // get pixel at (x=j,y=i)
                color = img.getPixel(j, i);
                // get 3 bits from binary string
                if (size - count >= 3) substring = binary.substring(count, count+3);
                else if (size%3 == 1) {substring = binary.substring(count, count+1); substring = substring + "00";}
                else if (size%3 == 2) {substring = binary.substring(count, count+2); substring = substring + "0";}
                else substring = binary.substring(count, count+3);
                // set the pixel to new values
                res.setPixel(j, i, setColor(color, substring));
                // increase count by 3
                count += 3;
                // if count goes higher than the size of the text, then stop and return the result
                if (count >= size) return res;
            }
        }
        return res;
    }

    public String convertStringToBinary(String input) {

        StringBuilder result = new StringBuilder();
        char[] chars = input.toCharArray();
        for (char aChar : chars) {
            result.append(
                    String.format("%8s", Integer.toBinaryString(aChar))   // char -> int, auto-cast
                            .replaceAll(" ", "0")                         // zero pads
            );
        }
        return result.toString();
    }

    public int setColor(int color, String subString) {
        // get argb values
        int A = Color.alpha(color);
        int R = Color.red(color);
        int G = Color.green(color);
        int B = Color.blue(color);

        // setting argb values in an array and loop through it to set LSB
        int[] values = {R, G, B};
        for (int i = 0; i < 3; i++) {
            values[i] = setLSB(values[i], subString.charAt(i));
        }
        // construct color from new argb values and return it
        return Color.argb(A, values[0], values[1], values[2]);
    }

    public int setLSB(int number, char character) {
        if      (number%2==0 && character=='0') return number;
        else if (number%2==0 && character=='1') return number+1;
        else if (number%2==1 && character=='0') return number-1;
        else                                    return number;
    }

    public String completeBin(String bin) {
        // add a padding of zeros to a binary string to complete its size to 32
        StringBuilder binBuilder = new StringBuilder(bin);
        while(binBuilder.length()<32){
            binBuilder.insert(0, "0");
        }
        bin = binBuilder.toString();
        return bin;
    }
}
