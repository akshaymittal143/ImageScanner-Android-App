package com.android.mycamera.cameraapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.android.mycamera.cameraapplication.util.MyCameraApplicationUtil;


import java.lang.ref.WeakReference;


/**
 * The Class CacheImageLoader.
 */
public class CacheImageLoader extends AsyncTask<String, Integer, Bitmap> {
    ImageGridViewActivity gridviewObject=new ImageGridViewActivity();
    /** The view reference. */
    private final WeakReference<ImageView> viewReference;
    
    /** The m context. */
    private Context mContext;
    
    /**
     * Instantiates a new cache image loader.
     *
     * @param c the c
     * @param view the view
     */
    public CacheImageLoader(Context c, ImageView view) {
      viewReference = new WeakReference<ImageView>(view);
      mContext = c;
    }

    /* (non-Javadoc)
     * @see android.os.AsyncTask#onPreExecute()
     */
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /* (non-Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected Bitmap doInBackground(String... params) {
        
      
    	Log.i("gridview","params[0] in cache loader "+params[0]);
    	
    	
    	Bitmap bitmap = null;
        
       if(params[0]!=""){
        	Log.i("gridview","Bitmap in cache loader before::  "+bitmap);
        	Log.i("gridview","param "+params[0]);
        	Log.i("gridview","width "+params[1]);
        	int width = Integer.parseInt(params[1]);
        	bitmap = MyCameraApplicationUtil.getThumbnailFromImage(params[0],width,width);
        	
        	if(bitmap!=null){
        		ImageGridViewActivity.addBitmapToMemoryCache(params[0], bitmap);
        	}
       }  
        return bitmap;

    }
    
    
    /* (non-Javadoc)
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        if (result != null) {
            ImageView imageView = viewReference.get();
            if(imageView != null) {
              imageView.setImageBitmap(result);
            }
        }

    }


}
