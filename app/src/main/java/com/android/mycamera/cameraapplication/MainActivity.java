package com.android.mycamera.cameraapplication;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import android.widget.ListView;
import android.widget.Toast;

import com.android.mycamera.cameraapplication.adapter.ListAdapter;
import com.android.mycamera.cameraapplication.dataobjects.MyDocument;
import com.android.mycamera.cameraapplication.util.MyCameraApplicationUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;




public class MainActivity extends Activity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int ANDROID_GALLERY_ACTIVITY = 101;
    public static final int LAUNCH_THUMBNAIL = 102;
    public static final String LOG_TAG = "MyCameraAppLog";

    private ListView documentListView;
    private ListAdapter listAdapter;

    private ArrayList<MyDocument> documents = new ArrayList<MyDocument>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        documentListView = (ListView) findViewById(R.id.documentListView);
        listAdapter = new ListAdapter(this);

        getAllStoredDocuments();

        findViewById(R.id.cameraButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                launchCamera();
            }
        });

        findViewById(R.id.galleryButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGallery();
            }
        });


        documentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                                    long arg3) {

                Log.i(LOG_TAG, "clicked tag -- > " + view.getTag());
                String path = getExternalCacheDir().getPath();
                String documentName = (String)view.getTag();

                String fullDocPath = path+"/"+documentName;
                Log.i(LOG_TAG, "Full Document Path:  " + fullDocPath);


                launchThumbnailView(2, documentName);
            }
        });

        registerForContextMenu(documentListView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllStoredDocuments();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete_context, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        MyDocument document = (MyDocument) listAdapter.getItem((int) info.id);
        String documentName = document.getDocumentName();


        switch (item.getItemId()) {

            case R.id.deleteDoc:
                deleteDocument(documentName);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void deleteDocument(String documentName) {
     //   File fileDir =  getApplicationContext().getDir("files", Context.MODE_PRIVATE);
        File documentDir = new File(getExternalCacheDir(),documentName);
        Log.i(LOG_TAG,"documen dir path "+documentDir.getAbsolutePath());
        Log.i(LOG_TAG,"document Dir before "+documentDir.exists());
        deleteDirectory(documentDir);
        documentDir.delete();

        MyDocument doc = new MyDocument();
        doc.setDocumentName(documentName);
        documents.remove(doc);
        getAllStoredDocuments();
    }
    public void deleteDirectory(File directory){
        File[] files= directory.listFiles();
        for(File file: files){
            if(file.isDirectory()){
                deleteDirectory(file);
            }
            if(file.isFile()){
                file.delete();
            }
        }
        directory.delete();

    }
    private void launchThumbnailView(int keyValue, String documentPath) {

        Intent intent = new Intent(getApplicationContext(), ImageGridViewActivity.class);
        intent.putExtra("key", keyValue);
        intent.putExtra("documentPath", documentPath);
        startActivityForResult(intent, LAUNCH_THUMBNAIL);
    }

    private void getAllStoredDocuments(){

        ArrayList<MyDocument> documents = new ArrayList<MyDocument>();
        File sourceDirectory = getExternalCacheDir();
        if (!sourceDirectory.exists()) {
            Log.e(LOG_TAG,"Unable to read directory ");


        } else {
            File[] files = sourceDirectory.listFiles();
            int id = 1;


            for (File file : files) {

                Log.i(LOG_TAG, "file get path " + file.getAbsolutePath());
                if(file.isDirectory()){
                    MyDocument doc = new MyDocument();
                    doc.setDocumentName(file.getName());
                    doc.setId(id);

                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                    String modifiedDate = sdf.format(file.lastModified());
                    Log.d(LOG_TAG, "After Format : " + modifiedDate);
                    try {
                        doc.setDateModified(sdf.parse(modifiedDate));
                    }
                    catch(ParseException e){

                    }
                    doc.setModifiedDate(modifiedDate);
				/*find files count in original folder and set to document object*/
                    if(file.listFiles() != null && file.listFiles().length > 0) {
                        int filesCount = file.listFiles().length;
                        Log.i(LOG_TAG, "filesCount " + filesCount);
                        doc.setNumberOfPages(filesCount);
                        String firstFilePath = MyCameraApplicationUtil.getFirstFilePath(file.getAbsolutePath());
                        doc.setThumbnailPath(firstFilePath);
                    }
                    else{
                        doc.setNumberOfPages(0);
                    }

                    if(!documents.contains(doc)){
                        documents.add(doc);
                        id++;
                    }

                }
            }
        }
        Collections.sort(documents);

        for (MyDocument doc : documents) {
            Log.i(LOG_TAG, "doc date in order " + doc.getDocumentName());
        }

        listAdapter.setDocumentList(documents);
        documentListView.setAdapter(listAdapter);


    }
    private void launchCamera(){
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File cacheDir = getApplicationContext().getCacheDir();

            Log.e(LOG_TAG,cacheDir.getAbsolutePath()+"/"+MyCameraApplicationUtil.getDirectoryName());
        Uri fileUri = MyCameraApplicationUtil.getOutputMediaFileUri(MyCameraApplicationUtil.getDirectoryName(),MyCameraApplicationUtil.MEDIA_TYPE_IMAGE, getApplicationContext()); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
        Log.d(LOG_TAG, "Media URI: " + fileUri);
        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

    }

    public void launchGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");

        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(galleryIntent, "Select Picture"),
                ANDROID_GALLERY_ACTIVITY);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:

                getAllStoredDocuments();
                break;
            case ANDROID_GALLERY_ACTIVITY:

                File tempFile = null;
                Uri imgUri = data.getData();
                Log.d(LOG_TAG, " prints img URI " + imgUri);
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
                File cacheDir = getApplicationContext().getCacheDir();
                File imageFile = MyCameraApplicationUtil.getOutputMediaFile(MyCameraApplicationUtil.getDirectoryName(),MyCameraApplicationUtil.MEDIA_TYPE_IMAGE, getApplicationContext());
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
                    //Refresh documents list
                    getAllStoredDocuments();
                    break;

        }
                super.onActivityResult(requestCode, resultCode, data);

    }

}
