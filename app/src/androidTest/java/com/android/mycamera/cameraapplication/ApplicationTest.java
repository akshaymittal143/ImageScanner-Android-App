package com.android.mycamera.cameraapplication;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.test.ActivityInstrumentationTestCase;

import com.android.mycamera.cameraapplication.util.MyCameraApplicationUtil;

import java.io.File;


public class ApplicationTest extends ActivityInstrumentationTestCase<MainActivity> {

    private Activity activity;

    public ApplicationTest() {

        super("com.android,mycamera.cameraapplication",MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.activity = this.getActivity();
    }

        public void testStart() throws Exception{
            assertNotNull(this.activity);
            assertNotNull(this.activity.getApplication());
        }

        public void testCamScannerFromCamera() throws Exception{
            //To check whether the files retrieved are not empty
            assertNotNull(this.activity.getApplication().getExternalCacheDir());
        }

    public void testCameraLaunch() throws Exception{
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File cacheDir = this.getActivity().getApplication().getApplicationContext().getCacheDir();

        Uri fileUri = MyCameraApplicationUtil.getOutputMediaFileUri(MyCameraApplicationUtil.getDirectoryName(), MyCameraApplicationUtil.MEDIA_TYPE_IMAGE, this.getActivity().getApplicationContext()); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        assertNotNull(this.activity.getApplication());
        assertNotNull(this.activity.getApplication().getApplicationContext());
        assertNotNull(this.activity.getApplication().getApplicationContext().getCacheDir());
    assertNotNull(cacheDir);
        assertNotNull(intent);
        assertNotNull(fileUri);
    }

    public void testGallaryLaunch() throws Exception{
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        int ANDROID_GALLERY_ACTIVITY = 101;
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        assertNotNull(galleryIntent);


    }
}
