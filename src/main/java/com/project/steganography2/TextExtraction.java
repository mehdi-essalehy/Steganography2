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
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TextExtraction extends AppCompatActivity {

    String txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_extraction);
        // get Image Uri String and get Image Uri from it
        String imgUriStr = getIntent().getStringExtra("IMAGE");
        Uri myUri = Uri.parse(imgUriStr);
        // create bitmap from Image Uri
        Bitmap mBitmap = null;
        try {
            mBitmap = MediaStore.Images.Media.getBitmap(this. getContentResolver(), myUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // extract text from bitmap and set it to TextView
        txt = extractText(mBitmap);
        TextView present = findViewById(R.id.textView);
        present.setText(txt);
    }

    public void saveText(View view) {
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        TextView FileName_TV = findViewById(R.id.FileNameForText);
        String filename = FileName_TV.getText().toString() + ".txt";
        File file = new File(root, filename);
        if (file.exists()) file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            Context context = getApplicationContext();
            CharSequence text = e.toString();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            e.printStackTrace();
        }
        try {
            PrintWriter pw = new PrintWriter(file);
            pw.write(txt);
            pw.close();
            if (file.exists()) {
                Context context = getApplicationContext();
                CharSequence toastText = "text successfully saved!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, toastText, duration);
                toast.show();
            }
        } catch (FileNotFoundException e) {
            Context context = getApplicationContext();
            CharSequence text = e.toString();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            e.printStackTrace();
        }
    }

    public String extractText(Bitmap img) {
        if (img == null) return "";
        // declare color, and argb variables
        int color, R, G, B;
        // get height and width
        int height = img.getHeight();
        int width  = img.getWidth();
        // create StringBuilder for binarySizeString
        StringBuilder binarySizeBuilder = new StringBuilder();
        // first loop gets size from first 11 pixels
        int c = 0;
        for (int i=0; i<11; i++) {
            color = img.getPixel(i, 0);
            R = Color.red(color); binarySizeBuilder.append(getLSB(R)); c++; if (c >= 32) break;
            G = Color.green(color); binarySizeBuilder.append(getLSB(G)); c++; if (c >= 32) break;
            B = Color.blue(color); binarySizeBuilder.append(getLSB(B)); c++; if (c >= 32) break;
        }
        // get size from binarySizeBuilder
        String binarySize = binarySizeBuilder.toString(); //return binarySize;
        int size = Integer.parseInt(binarySize, 2); //return "" + size;
        // create StringBuilder for binary
        StringBuilder binaryBuilder = new StringBuilder();
        // declare and initiate count and start main loop
        int count = 0;
        for (int i=0; i<height; i++) {
            for (int j=0; j<width; j++) {
                // ignore first 11 pixels
                if (j<11 && i==0) continue;
                // get pixel
                color = img.getPixel(j, i);
                // get least significant bits of the pixel, add it to the binaryBuilder, and if count reaches size then retrieve text from binaryBuilder and return it
                R = Color.red(color); binaryBuilder.append(getLSB(R)); count++; if (count >= size) return binToText(binaryBuilder.toString());
                G = Color.green(color); binaryBuilder.append(getLSB(G)); count++; if (count >= size) return binToText(binaryBuilder.toString());
                B = Color.blue(color); binaryBuilder.append(getLSB(B)); count++; if (count >= size) return binToText(binaryBuilder.toString());
            }
        }
        return binToText(binaryBuilder.toString());
    }

    public String getLSB(int number) {
        int LSB = number%2; // get LSB
        return ""+LSB; // cast to string and return it
    }

    public String binToText(String bin) {
        // create a list of bytes from binary string
        List<String> binList = new ArrayList<>();
        for (int i=0; i<bin.length(); i += 8) {
            binList.add(bin.substring(i, i+8));
        }
        // create string by transforming each byte into character
        StringBuilder sb = new StringBuilder();
        for (String s : binList) {
            Character c = binToChar(s);
            sb.append(c);
        }
        return sb.toString();
    }

    public char binToChar(String bin) {
        // create int from byte and then cast it to char and return it
        int i = Integer.parseInt(bin, 2);
        return (char) i;
    }

}