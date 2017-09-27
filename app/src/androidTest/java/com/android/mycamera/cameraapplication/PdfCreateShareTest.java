package com.android.mycamera.cameraapplication;

import android.content.Intent;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.android.mycamera.cameraapplication.util.MyCameraApplicationConstants;
import com.android.mycamera.cameraapplication.util.MyCameraApplicationUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by vk on 8/4/2016.
 */
public class PdfCreateShareTest extends AndroidTestCase {

    public void testPdfCreate()
    {
        try {
            MyCameraApplicationUtil util = new MyCameraApplicationUtil();
            ImageGridViewActivity imagegvActivity = new ImageGridViewActivity();
            File pdfFile = imagegvActivity.generatePdf(MyCameraApplicationConstants.documentName);
            assertNotNull(getContext());
            assertNotNull(getContext().getApplicationContext());
            assertNotNull(getContext().getApplicationContext().getExternalCacheDir());
            File document = new File(getContext().getApplicationContext().getExternalCacheDir(), MyCameraApplicationConstants.documentName);
            String filePath = document.getAbsolutePath();
            MyCameraApplicationUtil.convertToPdf(document);
            assertNotNull(document);
            assertNotNull(filePath);
            assertNotNull(MyCameraApplicationUtil.convertToPdf(document));
            assertNotNull(pdfFile);
        }catch(Exception e)
        {
            assertTrue(false);
        }
    }
    public void testSharepdf()
    {
        try {
            ImageGridViewActivity imageActivity = new ImageGridViewActivity();
            assertNotNull(imageActivity);

            File pdfFile = imageActivity.generatePdf(MyCameraApplicationConstants.documentName);
            assertNotNull(pdfFile);

            Intent shareIntent = new Intent(
                    android.content.Intent.ACTION_SEND_MULTIPLE);

            assertNotNull(shareIntent);
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                    "My PDF");


            ArrayList<Uri> uris = new ArrayList<Uri>();
            shareIntent.setType("application/pdf");
            uris.add(Uri.fromFile(pdfFile));
            assertNotNull(uris);
        }
        catch(Exception e)
        {
            assertTrue(false);
        }
    }

}
