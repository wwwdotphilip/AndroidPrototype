package co.familyplay.androidprototype.graphics;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/*This class handles all bitmap manipulations such as rotation and resizing
The methods are all static so no need to instantiate.
simply call for example

Bmp.scaleDownBitmap(Bitmap photo, int newHeight, Context context)

*/

public class Bmp {

//    Convert drawable resouces into bitmap
    public static Bitmap decodeSampledBitmapFromResource(String resId,
                                                         int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(resId, options);
    }

    //    Get the matrix rotate if the image.
    public static Matrix rotateMatrix(String imageurl) {
        Matrix matrix = new Matrix();
        matrix.postRotate(getImageOrientation(imageurl));
        return matrix;
    }

    //    Get the bitmap folder path and return it as string.
    public static String getPath(Uri uri, Context context) {
        String res;
        Cursor cursor = context.getContentResolver().query(uri, null, null,
                null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        res = cursor.getString(idx);
        cursor.close();
        return res;
    }

//    Get the bitmap folder path and return it as string.
    public static String getSelectedPath(Uri uri, Activity activity) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = activity
                .managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    //Cut the bitmap into square shape.
    public static Bitmap cutImage(Bitmap source) {
        Bitmap result;
        if (source.getWidth() >= source.getHeight()) {

            result = Bitmap.createBitmap(source,
                    source.getWidth() - source.getHeight(), 0,
                    source.getHeight(), source.getHeight());

        } else {
            result = Bitmap.createBitmap(source, 0, source.getHeight()
                            - source.getWidth(), source.getWidth(),
                    source.getWidth());
        }
        return result;
    }

    // Resize bitmap accordingly.
    public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h = (int) (newHeight * densityMultiplier);
        int w = (int) (h * photo.getWidth() / ((double) photo.getHeight()));

        photo = Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
    }

    // Get the orientation of the image
    private static int getImageOrientation(String imagePath) {
        int rotate = 0;
        try {

            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }

    // Calculate bitmap size.
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 2;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    // check if file is GIF
    public static boolean isGif(File file) {
        if (file.getName().toLowerCase().endsWith("gif")) {
            return true;
        }
        return false;
    }

    // returns input stream into byte array
    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

}
