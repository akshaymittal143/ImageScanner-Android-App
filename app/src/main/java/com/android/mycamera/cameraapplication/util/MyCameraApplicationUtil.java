package com.android.mycamera.cameraapplication.util;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Document;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


public class MyCameraApplicationUtil {

    private Uri fileUri;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    public static final String LOG_TAG = "MyCameraAppLog";
    public static String getDirectoryName(){

        return "Doc"+getCurrentTimeStamp();
    }

    public static String getImageName(){
        return "Img"+getCurrentTimeStamp()+".jpg";
    }
    public static String getCurrentTimeStamp(){
        try {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            return sdf.format(date);
        }
        catch(Exception e){
            return "YYYYMMDDHHMMSS";
        }

    }

    /** Create a file Uri for saving an image or video */
    public static Uri getOutputMediaFileUri(String documentName, int type,Context mContext){
        File f = getOutputMediaFile(documentName,type,mContext);
        return Uri.fromFile(f);
    }

    /** Create a File for saving an image or video */
    public static File getOutputMediaFile(String documentName,int type,Context mContext){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        boolean status = mContext.getExternalCacheDir().mkdirs();

        System.out.print("Main status :"+status);
        File mediaStorageDir = new File(mContext.getExternalCacheDir(), documentName);

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name

        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    MyCameraApplicationUtil.getImageName());
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ MyCameraApplicationUtil.getCurrentTimeStamp() + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
    @SuppressLint("NewApi")
    public static File getFileFromURIKitkat(Uri contentUri,ContentResolver contentResolver){
        File testFile = null;
        String wholeID = DocumentsContract.getDocumentId(contentUri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";
        Log.d(LOG_TAG, " id " + id);
        Cursor cursor = contentResolver.
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);

        String filePath = "";

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }

        if(filePath !=""){
            testFile = new File(filePath);
        }
        Log.d(LOG_TAG," file path "+filePath);
        cursor.close();
        return testFile;
    }
    public static File getFileFromURI(Uri contentUri,ContentResolver contentResolver) {

        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = null;
        int column_index = 0;
        String filePath = "";
        File testFile =null;
        try {
            cursor = contentResolver.query(contentUri, proj,
                    null,
                    null, // WHERE clause selection arguments (none)
                    null);


            column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            Log.i(LOG_TAG,"cursor  "+cursor+" android.os.Build.VERSION "+android.os.Build.VERSION.RELEASE);
            Log.i(LOG_TAG,"cursor.getString(column_index) "+cursor.getString(column_index));
            filePath = cursor.getString(column_index);
            Log.d(LOG_TAG,"Build SDK "+android.os.Build.VERSION.SDK_INT);

            if(filePath != null){
                testFile = new File(filePath);
            }

            else if (android.os.Build.VERSION.SDK_INT > 18){

            }

        } catch (Exception e) {
            e.printStackTrace();
            /*Toast.makeText(getApplicationContext(), "Please try again",
                    Toast.LENGTH_LONG).show();*/
        }


        return testFile;

    }
    public static int getCameraPhotoOrientation(String imagePath) {
        int rotate = 0;
        try {

            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            Log.i("Image orientation", "Exif orientation: " + orientation);

            Log.i(LOG_TAG,"imagePath in image Quality"+imagePath+" imageFile.getAbsolutePath() "+imageFile.getAbsolutePath());

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

            Log.i("Image orientation", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static Bitmap getThumbnailFromImage(String imageFile,int requiredWidth,int requiredHeight) {
        try {

            Log.i("Image gridview", "imageFile  in thumbnail " + imageFile);
            String path = imageFile;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap originalImage = BitmapFactory.decodeFile(path, options);
            Log.i("Image gridview", "original image at first " + originalImage);
            int rotateAngle = getCameraPhotoOrientation(path);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            //	int rotateAngle = Util.getCameraPhotoOrientation(path);
            int reqHeight = requiredWidth;
            int reqWidth = requiredHeight;
            if (imageHeight > reqHeight || imageWidth > reqWidth) {

                // Calculate ratios of height and width to requested height and
                // width
                final int heightRatio = Math.round((float) imageHeight
                        / (float) reqHeight);
                final int widthRatio = Math.round((float) imageWidth
                        / (float) reqWidth);

                int inSampleSize = heightRatio < widthRatio ? heightRatio
                        : widthRatio;
                options.inSampleSize = inSampleSize;
            }
            options.inJustDecodeBounds = false;
            originalImage = BitmapFactory.decodeFile(path, options);
            BitmapFactory.decodeFile(path, options);
            Log.i("Image gridview", "originalImage in thumbnail "
                    + originalImage);

            Bitmap resizedBitmap1 = null;
            Matrix matrix = new Matrix();
            matrix.preRotate(rotateAngle);

            Log.i("Image gridview", " width  "+requiredWidth+"originalImage.getWidth()  "+originalImage.getWidth());
            Log.i("Image gridview", " height "+requiredHeight+"originalImage.getHeight()  "+originalImage.getHeight());
            int xCoordinate = Math.abs(requiredWidth-originalImage.getWidth())/2;
            int yCoordinate = Math.abs(requiredHeight-originalImage.getHeight())/2;
            if(requiredWidth > originalImage.getWidth()){
                requiredWidth = originalImage.getWidth();
                xCoordinate = 0;
            }
            if(requiredHeight > originalImage.getHeight()){
                requiredHeight = originalImage.getHeight();
                yCoordinate = 0;
            }
            if (originalImage != null) {
                resizedBitmap1 = Bitmap.createBitmap(originalImage, xCoordinate,
                        yCoordinate, requiredWidth, requiredHeight, matrix, true);
                // bitmap2 = Bitmap.createBitmap(resizedBitmap1,50,50,reqWidth-100,reqHeight-100);
            }
            return resizedBitmap1;
        } catch (Exception e) {
            Log.i("Image gridview", "Error in thumbnail " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static File convertToPdf(File folder) {
        Document document = new Document(PageSize.A2);
        String output ="capture.pdf";
        File outputFile = new File(folder,output);

        List<String> inputFiles = getImageNames(folder);
        try {
            FileOutputStream fos = new FileOutputStream(outputFile.getAbsolutePath());
            PdfWriter writer = PdfWriter.getInstance(document, fos);
            writer.open();
            document.open();
            for (String s : inputFiles) {
                Log.d(LOG_TAG,"image instance: "+folder.getAbsolutePath() +"/"+ s);
                document.add(Image.getInstance(folder.getAbsolutePath() + "/" + s));
            }
            document.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputFile;
    }

    public static List<String> getImageNames(File folder) {
        File[] listOfFiles = folder.listFiles();
        List<String> nameList = new ArrayList<String>();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                String fileName = file.getName();
                Log.d(LOG_TAG,"File name :"+fileName);
                if (fileName.contains(".JPG") || fileName.contains("jpg")) {
                    nameList.add(fileName);
                }
            }
        }
        return nameList;
    }

    public static List<String> dateRange(File folder) {
        File[] listOfFiles = folder.listFiles();
        List<String> nameList = new ArrayList<String>();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                String fileName = file.getName();

            }
        }
        return nameList;
    }
    public static String getFirstFilePath(String folderPath){
        File folder = new File(folderPath);
        List<String> fileNames = new ArrayList<String>();
        for(File file:folder.listFiles()){
            if (file.getName().contains(".JPG") || file.getName().contains("jpg")) {
                fileNames.add(file.getName());
            }
        }
        Collections.sort(fileNames);
        return folderPath+"/"+fileNames.get(0);
    }
}
