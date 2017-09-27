package com.android.mycamera.cameraapplication;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.test.AndroidTestCase;
import android.util.Log;

import com.android.mycamera.cameraapplication.dataobjects.MyDocument;
import com.android.mycamera.cameraapplication.util.MyCameraApplicationUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by vk on 8/4/2016.
 */
public class MainActivityTest extends AndroidTestCase {

    public void testDeleteDocument()
    {
        String documentName="Doc2016080731085854";
        assertNotNull(documentName);
        assertNotNull(getContext());
        assertNotNull(getContext().getApplicationContext());
        assertNotNull(getContext().getApplicationContext().getExternalCacheDir());
        File documentDir = new File(getContext().getApplicationContext().getExternalCacheDir(),documentName);
    assertNotNull(documentDir);

    }

    public void testDisplayDocuments()
    {
        ArrayList<MyDocument> documents = new ArrayList<MyDocument>();
        File sourceDirectory = getContext().getApplicationContext().getExternalCacheDir();
        assertNotNull(sourceDirectory);
    }
    public void testLaunchCamera()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File cacheDir = getContext().getApplicationContext().getCacheDir();
        assertNotNull(intent);
        assertNotNull(cacheDir);

    }

    public void testLaunchGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");

        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        assertNotNull(galleryIntent);
    }
}
