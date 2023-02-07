package com.technophile.nuro.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.graphics.BitmapCompat;

import java.io.ByteArrayOutputStream;

public class ImageHelper {

    public static Bitmap compress(Bitmap bitmap) {
        int pixelSize = bitmap.getByteCount() / (bitmap.getWidth() * bitmap.getHeight());
        int ratio = 1;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        while (((width / ratio) * (height / ratio) * pixelSize) > (2 * 1024 * 1024)) {
            ratio++;
        }
        if (ratio == 1) {
            return bitmap;
        }
        return BitmapCompat.createScaledBitmap(bitmap, width / ratio, height / ratio, null, false);
    }

    public static byte[] toByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    public static Bitmap toBitmap(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
