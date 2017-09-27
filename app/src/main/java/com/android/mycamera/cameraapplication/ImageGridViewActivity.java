package com.android.mycamera.cameraapplication;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.ProgressDialog;
import android.content.Intent;

import android.database.Cursor;
import android.graphics.Bitmap;

import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;

import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.mycamera.cameraapplication.adapter.ImageAdapter;
import com.android.mycamera.cameraapplication.dataobjects.GridObject;
import com.android.mycamera.cameraapplication.util.MyCameraApplicationConstants;
import com.android.mycamera.cameraapplication.util.MyCameraApplicationUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.IOException;

import java.util.ArrayList;



public class ImageGridViewActivity extends Activity implements View.OnClickListener {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int ANDROID_GALLERY_ACTIVITY = 101;
    private static final int SHARE_ACTIVITY = 102;

    private static LruCache<String, Bitmap> mMemoryCache;

    private File cachePath;
    private ProgressBar displayProgress;
    private ImageAdapter imageAdapter;
    private GridView gridview;
    private ImageView shareButton,cameraButton,galleryButton,deleteBtn;
    private static EditText renameText;
    private TextView documentTitle;

    public static final String LOG_TAG = "MyCameraAppLog";
    ProgressDialog progress;

    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        } else {
            mMemoryCache.remove(key);
            mMemoryCache.put(key, bitmap);
        }
    }

    public static void removeBitmapToMemoryCache(String key) {
        if (getBitmapFromMemCache(key) != null) {
            Log.i(LOG_TAG, "key exists for remove ");
            mMemoryCache.remove(key);
        }

    }
    public static Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_layout);
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize);

        this.cachePath = ((Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) || !Environment
                .isExternalStorageRemovable()) ? getExternalCacheDir()
                : getCacheDir();

        this.displayProgress = (ProgressBar) findViewById(R.id.displayProgress);

        if (imageAdapter == null) {
            imageAdapter = new ImageAdapter(this);
        }
        gridview = (GridView) findViewById(R.id.gridview);
        gridview.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {


            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                imageAdapter.notifyDataSetChanged();
            }
        });
        shareButton = (ImageView) findViewById(R.id.shareButton);

        cameraButton = (ImageView) findViewById(R.id.cameraBtn);
        galleryButton = (ImageView) findViewById(R.id.galleryBtn);


        cameraButton.setOnClickListener(this);
        galleryButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);


        deleteBtn = (ImageView) findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(this);
        renameText = (EditText) findViewById(R.id.renameText);
        documentTitle = (TextView) findViewById(R.id.documentTitle);
        documentTitle.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                documentTitle.setVisibility(View.GONE);
                renameText.setVisibility(View.VISIBLE);
                CharSequence text = documentTitle.getText();
                String documentName = text.toString();
                renameText.setText(documentName);
                renameText.selectAll();


                //renameDocument();
                renameText.setFocusableInTouchMode(true);
                renameText.setFocusable(true);
                renameText.performClick();
                renameText.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) ImageGridViewActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);

                inputMethodManager.showSoftInput(renameText, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }
        });

        renameText.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                try {
                    if (event.getKeyCode() == android.view.KeyEvent.KEYCODE_BACK) {

                        hideSoftKeyboard(ImageGridViewActivity.this);
                        documentTitle.setVisibility(View.VISIBLE);
                        renameText.setVisibility(View.GONE);
                    }
                } catch (Exception e) {

                }

                return false;
            }
        });
        renameText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        renameText.setImeOptions(EditorInfo.IME_ACTION_GO);
        renameText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (event != null && event.getAction() != android.view.KeyEvent.ACTION_DOWN) {
                    return false;
                }

                if (actionId != EditorInfo.IME_ACTION_NEXT && actionId != EditorInfo.IME_NULL) {
                    return false;
                }

                if (event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER) {


                }
                return false;
            }
        });


        renameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                try {

                    if (!hasFocus) {
                        documentTitle.setVisibility(View.VISIBLE);
                        renameText.setVisibility(View.GONE);
                        hideSoftKeyboard(ImageGridViewActivity.this);
                    }
                } catch (Exception e) {

                }
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra("key")) {

            int key = intent.getIntExtra("key", 100);

            switch (key) {
                case 0:
                    Log.i(LOG_TAG, "Launching Camera");

                    launchCamera();
                    break;
                case 1:
                    Log.i(LOG_TAG, "Launch gallery");

                    launchGallery();
                    break;
                case 2:
                    Log.i(LOG_TAG, "load image in thumbnail view ");

                    showThumbnailView(intent.getStringExtra("documentPath"));
                    break;
                default:
                    Log.i(LOG_TAG, "Default");
                    break;
            }
        }


      //  documentTitle.setText(XIPSGlobalVariables.documentName);


    }


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        if (activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } else {
            inputMethodManager.hideSoftInputFromWindow(renameText.getWindowToken(), 0);
        }
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof EditText) {

            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            Log.d("Activity", "Touch event " + event.getRawX() + "," + event.getRawY() + " " + x + "," + y + " rect " + w.getLeft() + "," + w.getTop() + "," + w.getRight() + "," + w.getBottom() + " coords " + scrcoords[0] + "," + scrcoords[1]);
            try {
                if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom())) {
                    documentTitle.setVisibility(View.VISIBLE);
                    renameText.setVisibility(View.GONE);
                    hideSoftKeyboard(this);
                }
            } catch (Exception e) {

            }
        }
        return ret;
    }

    /*Save text in  file */

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.cameraBtn:
                // launchThumbnailView(0);

                launchCamera();
                break;

            case R.id.galleryBtn:
                // launchThumbnailView(1);
                launchGallery();
                break;
            case R.id.shareButton:
                v.setBackgroundColor(Color.GRAY);
                sharePdf();

                break;
            case R.id.deleteBtn:
                deleteSelectedImages();
                break;

            default:
                break;
        }

    }

    public File generatePdf(String documentName){
        progress = ProgressDialog.show(this, "Loading",
                "Generating PDF ..", true);
        File document = new File(getExternalCacheDir(),documentName);
        String filePath = document.getAbsolutePath();
        Log.d(LOG_TAG, "file path: " + filePath);
        return MyCameraApplicationUtil.convertToPdf(document);
    }
    public void deleteSelectedImages() {
        Log.i(LOG_TAG, "MyCameraApplicationConstants.documentPath " + MyCameraApplicationConstants.documentName);
        ArrayList<String> selectedItems = imageAdapter.getCheckedItems();
        Log.i("Delete", "selected items " + selectedItems);
        for (int index = selectedItems.size() - 1; index >= 0; index--) {
            Log.i("Delete", "selected Item:: " + selectedItems.get(index));
            String imagePath = selectedItems.get(index);
            if (null != imagePath) {
                Log.d(LOG_TAG,"image path: "+imagePath);
                File file = new File(imagePath);
                boolean deleteStatus = file.delete();
                Log.d(LOG_TAG,"image path delete status : "+deleteStatus);
                imageAdapter.unCheckedItems();

            }
            showThumbnailView(MyCameraApplicationConstants.documentName);

        }
    }



    public void showThumbnailView(String documentName) {
        Log.i(LOG_TAG, "documentPath in grid activity  " + documentName);
        // imageList = fetchAllImagesFromFolder(documentPath);

        ArrayList<GridObject> myObjects = new ArrayList<GridObject>();

        File cacheDir = getApplicationContext().getExternalCacheDir();

        File documentDir = new File(cacheDir,documentName);
        Log.d(LOG_TAG,"Doc path: "+documentDir.getAbsolutePath());
        Log.d(LOG_TAG,"Doc name: "+documentDir.getName());
        MyCameraApplicationConstants.documentName = documentDir.getName();
        documentTitle.setText(documentDir.getName());
        if(documentDir.exists()) {
            File[] files = documentDir.listFiles();
            int index = 0;
            for(File file: files) {
                index++;
                GridObject gridObject = new GridObject(file.getPath(), index, false);
                if (!myObjects.contains(gridObject)) {

                    myObjects.add(gridObject);
                }
            }
        }


        imageAdapter.setImageList(myObjects);

        imageAdapter.unCheckedItems();
        gridview.setAdapter(imageAdapter);

        gridview.setClickable(true);
        gridview.setFocusable(true);


        int deviceWidth = getWindowManager().getDefaultDisplay().getWidth();
        int totalImagesPerRow = 3;

        int desiredGridWidth = 75;
        totalImagesPerRow = deviceWidth / (getPixelValue(desiredGridWidth) + (2 * getPixelValue(1)));
        int requiredWidth = (deviceWidth - (2 * totalImagesPerRow * getPixelValue(1))) / totalImagesPerRow;
        float n = deviceWidth / (requiredWidth + (2 * getPixelValue(1)));
        gridview.setNumColumns(Math.round(n));
        gridview.setTag(requiredWidth - getPixelValue(15));

    }

    private int getPixelValue(int dpValue) {
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (dpValue * scale + 0.5f);
        return pixels;
    }



    private void launchCamera(){
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File cacheDir = getApplicationContext().getCacheDir();


        Uri fileUri = MyCameraApplicationUtil.getOutputMediaFileUri(MyCameraApplicationConstants.documentName,MyCameraApplicationUtil.MEDIA_TYPE_IMAGE, getApplicationContext()); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
        Log.d(LOG_TAG, "Media URI: " + fileUri);
        // start the image capture Intent
        Log.d(LOG_TAG,"Debugging Doc name: "+MyCameraApplicationConstants.documentName);
        intent.putExtra("docName", MyCameraApplicationConstants.documentName);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

    }

    public void launchGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.putExtra("docName", MyCameraApplicationConstants.documentName);
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(galleryIntent, "Select Picture"),
                ANDROID_GALLERY_ACTIVITY);

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(LOG_TAG, "activtiy result syso");
        switch (requestCode) {
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                Log.d(LOG_TAG,"Doc name: "+MyCameraApplicationConstants.documentName+" path:"+MyCameraApplicationConstants.documentPath);
                String docName = getIntent().getStringExtra("docName");
                Log.d(LOG_TAG,"docname : "+docName);
              //  MyCameraApplicationConstants.documentName = docName;
                showThumbnailView(MyCameraApplicationConstants.documentName);

                break;
            case ANDROID_GALLERY_ACTIVITY:
                File tempFile = null;
                Log.d(LOG_TAG,"In Gallery, Doc name: "+MyCameraApplicationConstants.documentName);
                docName = getIntent().getStringExtra("docName");
                Log.d(LOG_TAG,"Doc name : "+docName);
                //MyCameraApplicationConstants.documentName = docName;

                Uri imgUri = data.getData();
                Log.d(LOG_TAG, "Debugging prints img URI " + imgUri);
                String uriString = imgUri.toString();
                if (uriString.endsWith(".png")) {
                    Toast.makeText(getApplicationContext(), "Application allows only jpeg image", Toast.LENGTH_SHORT).show();
                }
                /** Handles Kitkat gallery issue*/
                else if (uriString.contains("com.android.providers.media.documents")) {

                    tempFile = MyCameraApplicationUtil.getFileFromURIKitkat(imgUri, getContentResolver());
                } else {
                    tempFile = MyCameraApplicationUtil.getFileFromURI(imgUri, getContentResolver());
                }

                FileInputStream fis = null;
             //   File cacheDir = getApplicationContext().getCacheDir();
                Log.e(LOG_TAG,"MyCameraApplicationConstants.documentName : "+MyCameraApplicationConstants.documentName);
                File imageFile = MyCameraApplicationUtil.getOutputMediaFile(MyCameraApplicationConstants.documentName,MyCameraApplicationUtil.MEDIA_TYPE_IMAGE, getApplicationContext());
                if (tempFile != null && tempFile.exists()) {

                    try {
                        imageFile.createNewFile();
                        imageFile.setReadable(true, true);
                        imageFile.setWritable(true, true);
                    } catch (IOException e) {

                        System.out
                                .println("exception in file copy to project folder ");
                        e.printStackTrace();
                    }

                    FileOutputStream fos = null;
                    try {
                        fis = new FileInputStream(tempFile);
                        fos = new FileOutputStream(imageFile);
                        Log.i(MyCameraApplicationUtil.LOG_TAG, "fis " + fis);
                    } catch (FileNotFoundException e) {

                        e.printStackTrace();
                    }
                    byte[] buffer = new byte[1024];
                    int read;
                    try {
                        while ((read = fis.read(buffer)) != -1) {
                            fos.write(buffer, 0, read);
                        }
                        fos.close();
                        fis.close();
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
                showThumbnailView(MyCameraApplicationConstants.documentName);

                break;

            default:
                break;
        }
    }


    @SuppressLint("NewApi")
    public File getFileFromURIKitkat(Uri contentUri) {
        File testFile = null;
        String wholeID = DocumentsContract.getDocumentId(contentUri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";
        System.out.println(" id " + id);
        Cursor cursor = getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);

        String filePath = "";

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }

        if (filePath != "") {
            testFile = new File(filePath);
        }
        System.out.println(" file path " + filePath);
        cursor.close();
        return testFile;
    }

    public File getFileFromURI(Uri contentUri) {

        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        int column_index = 0;
        String filePath = "";
        File testFile = null;
        try {
            cursor = getContentResolver().query(contentUri, proj,
                    null,
                    null, // WHERE clause selection arguments (none)
                    null);


            column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            Log.i(LOG_TAG, "cursor  " + cursor + " android.os.Build.VERSION " + android.os.Build.VERSION.RELEASE);
            Log.i(LOG_TAG, "cursor.getString(column_index) " + cursor.getString(column_index));
            filePath = cursor.getString(column_index);
            if (filePath != null) {
                testFile = new File(filePath);
            } else if (android.os.Build.VERSION.RELEASE.equalsIgnoreCase("4.4")) {

            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Please try again",
                    Toast.LENGTH_LONG).show();
        }

        return testFile;

    }
    public void sharePdf() {

        File pdfFile = generatePdf(MyCameraApplicationConstants.documentName);
        if(pdfFile != null) {
            progress.dismiss();
            shareButton.setBackgroundColor(Color.parseColor("#639ED8"));
            Intent shareIntent = new Intent(
                    android.content.Intent.ACTION_SEND_MULTIPLE);
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                    "My PDF");

            ArrayList<Uri> uris = new ArrayList<Uri>();
            shareIntent.setType("application/pdf");
            uris.add(Uri.fromFile(pdfFile));

            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            startActivityForResult(Intent.createChooser(shareIntent, "Share options"), SHARE_ACTIVITY);

        }

    }
}
