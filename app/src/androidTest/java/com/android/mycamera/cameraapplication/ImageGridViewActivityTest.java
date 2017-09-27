package com.android.mycamera.cameraapplication;

import android.content.Intent;
import android.provider.MediaStore;
import android.test.AndroidTestCase;

import java.io.File;

/**
 * Created by vk on 8/4/2016.
 */
public class ImageGridViewActivityTest extends AndroidTestCase{

    public void testShowThumbnails()
    {
        String documentName = "Doc20160808105350";
        File cacheDir = getContext().getApplicationContext().getExternalCacheDir();

        File documentDir = new File(cacheDir,documentName);
        assertNotNull(documentDir);
    }
    public void testLaunchCamera()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File cacheDir = getContext().getApplicationContext().getCacheDir();
        assertNotNull(intent);
        assertNotNull(cacheDir);

    }
    public void testLaunchGallary()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");

        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        assertNotNull(galleryIntent);
    }

}
