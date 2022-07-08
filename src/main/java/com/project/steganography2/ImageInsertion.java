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

public class ImageInsertion extends AppCompatActivity {

    Bitmap img = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_insertion);
        String imgUriStr1 = getIntent().getStringExtra("IMAGE1");
        String imgUriStr2 = getIntent().getStringExtra("IMAGE2");
        // create Image Uri from String
        Uri myUri1 = Uri.parse(imgUriStr1);
        Uri myUri2 = Uri.parse(imgUriStr2);
        // make bitmap from Image Uri
        Bitmap mBitmap1 = null;
        Bitmap mBitmap2 = null;
        try {
            mBitmap1 = MediaStore.Images.Media.getBitmap(this. getContentResolver(), myUri1);
            mBitmap2 = MediaStore.Images.Media.getBitmap(this. getContentResolver(), myUri2);
        } catch (IOException e) {
            Context context = getApplicationContext();
            CharSequence toastText = e.toString();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, toastText, duration);
            toast.show();
            e.printStackTrace();
        }
        // create copy of image with text inserted
        img = InsertImage(mBitmap1 , mBitmap2);
        // get ImageView and display img in it
        ImageView present = findViewById(R.id.imageView3);
        present.setImageBitmap(img);
    }

    public void saveImage(View view) {
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        TextView FileName_TV = findViewById(R.id.fileNameForImage3);
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

    public Bitmap InsertImage(Bitmap img1, Bitmap img2) {
        Bitmap res = img1.copy(img1.getConfig(), true);

        int h1 = img1.getHeight();
        int w1 = img1.getWidth();
        int h2 = img2.getHeight();
        int w2 = img2.getWidth();

        int size1 = h1*w1;
        int size2 = h2*w2;

        if (size1 - 22 < size2 * 8) return null;

        List<Integer> l1 = getPixelList(img1);

        String binaryHeight = Integer.toBinaryString(h2);
        String completeBinaryHeight = completeBin(binaryHeight);

        String binaryWidth = Integer.toBinaryString(w2);
        String completeBinaryWidth = completeBin(binaryWidth);

        int c = 0;
        String substring;
        for (int i = 0; i < 11; i++){
            if (c < 30) substring = completeBinaryHeight.substring(c, c+3);
            else substring = completeBinaryHeight.substring(c, c+2); substring = substring + "0";
            l1.set(i, setColor(l1.get(i), substring));
            c += 3;
        }

        c = 0;
        for (int i = 11; i < 22; i++){
            if (c < 30) substring = completeBinaryWidth.substring(c, c+3);
            else substring = completeBinaryWidth.substring(c, c+2); substring = substring + "0";
            l1.set(i, setColor(l1.get(i), substring));
            c += 3;
        }

        int c1=22, pixel;
        for (int y = 0; y < h2; y++){
            for (int x = 0; x < w2; x++){
                pixel = img2.getPixel(x, y);
                List<Integer> sub_l = getNextEightPixels(l1, c1);
                insertPixel(sub_l, pixel);
                insertPixelList(l1, sub_l, c1);
                c1 += 8;
            }
        }
        createBitmapFromList(res, l1);
        return res;
    }

    public void createBitmapFromList(Bitmap res, List<Integer> l) {
        int c = 0;
        for (int y = 0; y < res.getHeight(); y++){
            for (int x = 0; x < res.getWidth(); x++){
                res.setPixel(x, y, l.get(c));
                c++;
            }
        }
    }

    public void insertPixelList(List<Integer> l, List<Integer> sub_l, int c1) {
        for (int i = 0; i < 8; i++){
            l.set(c1+i, sub_l.get(i));
        }
    }

    public List<Integer> getNextEightPixels(List<Integer> l, int c) {
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < 8; i++){
            res.add(l.get(c+i));
        }
        return res;
    }

    public void insertPixel(List<Integer> l, int pixel) {
        String binaryPixel = Integer.toBinaryString(pixel);
        String completeBinaryPixel = completeBin(binaryPixel);
        String A = completeBinaryPixel.substring(0, 8).substring(0, 6);
        String R = completeBinaryPixel.substring(8, 16).substring(0, 6);
        String G = completeBinaryPixel.substring(16, 24).substring(0, 6);
        String B = completeBinaryPixel.substring(24, 32).substring(0, 6);
        String ARGB = A + R + G + B;
        String substring;
        int c = 0;
        for (int i = 0; i < 8; i++){
            substring = ARGB.substring(c, c+3);
            l.set(i, setColor(l.get(i), substring));
            c += 3;
        }
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


