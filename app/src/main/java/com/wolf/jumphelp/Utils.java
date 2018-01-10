package com.wolf.jumphelp;

import android.arch.core.executor.TaskExecutor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;

import static java.lang.Runtime.getRuntime;

/**
 * Created by Roye on 2018/1/9.
 */

public class Utils {


    //按图片坐标像素点识别指定图片
    public static List<ImagePosColor> getImageGRB(Bitmap bitmap1) {
        //#3A3852 (58,56,82)    #363C66 (54,60,102)
//        File file = new File(filePath);
        List<ImagePosColor> result = null;
//        android.util.Log.d("www", "file.exists()=== " + file.exists());
        Bitmap bitmap = bitmap1;
        try {
            //bitmap = BitmapFactory.decodeFile(filePath);
            int height = bitmap.getHeight();
            int width = bitmap.getWidth();
            result = new ArrayList<>(100);
            android.util.Log.d("www", "rgb=== " + width + " " + height);
            int color;
            for (int i = width / 5; i < width / 2; i++) {
                for (int j = height / 2; j < height / 1.5; j++) {
                    color = bitmap.getPixel(i, j) & 0xFFFFFF;
                    int r = (color & 0xFF0000) >> 16;
                    int g = (color & 0x00FF00) >> 8;
                    int b = color & 0x0000FF;
                    if (Math.abs(r - 58) < 10 && Math.abs(g - 56) < 10 && Math.abs(b - 82) < 10) {
                        result.add(new ImagePosColor(i, j, color));
                        android.util.Log.d("www", String.format("rgb=== r%d g%d b%d (x%d,y%d)", r, g, b, i, j));
                    }
                }
            }
        } catch (Throwable e) {
            android.util.Log.d("www", "Throwable=== " + e.toString());
            e.printStackTrace();
        } finally {
            if (bitmap != null) {
                bitmap.recycle();
            }
        }

        return result;
    }

    public static void checkImageGRB(Bitmap bitmap, final TiTView view) {
        new AsyncTask<Bitmap, Integer, List<ImagePosColor>>() {

            @Override
            protected List<ImagePosColor> doInBackground(Bitmap... strings) {
                return getImageGRB(strings[0]);
            }

            @Override
            protected void onPostExecute(List<ImagePosColor> posColors) {
                if (posColors != null) {
                    android.util.Log.d("www", "integer=== " + posColors);
                }

            }
        }.execute(bitmap);

    }

    public static int[] getRandomCoordinate(int[] from, int[] to){
        return new int[]{from[0]+new Random().nextInt(to[0]-from[0]),from[1]+new Random().nextInt(to[1]-from[1])};
    }

}
