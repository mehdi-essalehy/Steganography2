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
import java.util.ArrayList;
import java.util.List;

public class ImageExtraction extends AppCompatActivity {

    Bitmap img = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_extraction);
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
        img = extractImage(mBitmap);
        ImageView present = findViewById(R.id.imageView);
        present.setImageBitmap(img);
    }

    public void saveImage(View view) {
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        TextView FileName_TV = findViewById(R.id.FileNameForImage2);
        String filename = FileName_TV.getText().toString() + ".jpg";
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
            img.compress(Bitmap.CompressFormat.JPEG, 90, out);
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

    public Bitmap extractImage(Bitmap img){
        List<Integer> l = getPixelList(img);
        int h = getHeight(l);
        int w = getWidth(l);

        Bitmap res = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        int c = 22, pixel;
        for (int y = 0; y < h; y++){
            for (int x = 0; x < w; x++){
                List<Integer> sub_l = getNextEightPixels(l, c);
                pixel = getPixelFromList(sub_l);
                res.setPixel(x,y,pixel);
                c+=8;
            }
        }
        return res;
    }

    public int getPixelFromList(List<Integer> sub_l) {
        int color, A, R, G, B;
        StringBuilder binaryBuilder = new StringBuilder();
        for(int i = 0; i < 8; i++){
            color = sub_l.get(i);
            R = Color.red(color); binaryBuilder.append(getLSB(R));
            G = Color.green(color); binaryBuilder.append(getLSB(G));
            B = Color.blue(color); binaryBuilder.append(getLSB(B));
        }
        String binary = binaryBuilder.toString();
        String bin_A = binary.substring(0, 6)   + "00"; A = Integer.parseInt(bin_A, 2);
        String bin_R = binary.substring(6, 12)  + "00"; R = Integer.parseInt(bin_R, 2);
        String bin_G = binary.substring(12, 18) + "00"; G = Integer.parseInt(bin_G, 2);
        String bin_B = binary.substring(18, 24) + "00"; B = Integer.parseInt(bin_B, 2);
        return Color.argb(A, R, G, B);
    }

    public String completeBin(String bin) {
        // add a padding of ones to a binary string to complete its size to 32
        StringBuilder binBuilder = new StringBuilder(bin);
        while(binBuilder.length()<32){
            binBuilder.insert(0, "0");
        }
        bin = binBuilder.toString();
        return bin;
    }

    private int getWidth(List<Integer> l) {
        int color, R, G, B;
        StringBuilder binaryWidthBuilder = new StringBuilder();
        int c = 0;
        for (int i = 11; i < 22; i++){
            color = l.get(i);
            R = Color.red(color); binaryWidthBuilder.append(getLSB(R)); c++; if (c >= 32) break;
            G = Color.green(color); binaryWidthBuilder.append(getLSB(G)); c++; if (c >= 32) break;
            B = Color.blue(color); binaryWidthBuilder.append(getLSB(B)); c++; if (c >= 32) break;
        }
        String binaryWidth = binaryWidthBuilder.toString();
        return Integer.parseInt(binaryWidth, 2);
    }

    private int getHeight(List<Integer> l) {
        int color, R, G, B;
        StringBuilder binaryHeightBuilder = new StringBuilder();
        int c = 0;
        for (int i = 0; i < 11; i++){
            color = l.get(i);
            R = Color.red(color); binaryHeightBuilder.append(getLSB(R)); c++; if (c >= 32) break;
            G = Color.green(color); binaryHeightBuilder.append(getLSB(G)); c++; if (c >= 32) break;
            B = Color.blue(color); binaryHeightBuilder.append(getLSB(B)); c++; if (c >= 32) break;
        }
        String binaryHeight = binaryHeightBuilder.toString();
        return Integer.parseInt(binaryHeight, 2);
    }

    public List<Integer> getPixelList(Bitmap img) {
        int h = img.getHeight();
        int w = img.getWidth();

        List<Integer> l = new ArrayList<>();
        int pixel;
        for (int y = 0; y < h; y++){
            for (int x = 0; x < w; x++){
                pixel = img.getPixel(x,y);
                l.add(pixel);
            }
        }
        return l;
    }

    public List<Integer> getNextEightPixels(List<Integer> l, int c1) {
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < 8; i++){
            res.add(l.get(c1+i));
        }
        return res;
    }

    public String getLSB(int number) {
        int LSB = number%2; // get LSB
        return ""+LSB; // cast to string and return it
    }
}