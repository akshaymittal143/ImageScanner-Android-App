package com.android.mycamera.cameraapplication.adapter;


import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.mycamera.cameraapplication.CacheImageLoader;
import com.android.mycamera.cameraapplication.ImageGridViewActivity;
import com.android.mycamera.cameraapplication.R;
import com.android.mycamera.cameraapplication.dataobjects.GridObject;

import java.util.ArrayList;


/**
 * The Class MyAdapter.
 */
public class ImageAdapter extends BaseAdapter {

    /** The m context. */
    private Context mContext;
    private View grid;
    private GridView gridView ;
    /** The image list. */
    private ArrayList<GridObject> imageList = new ArrayList<GridObject>();
    SparseBooleanArray mSparseBooleanArray;

    public static final String LOG_TAG = "MyCameraAppLog";
    /**
     * Instantiates a new my adapter.
     *
     * @param c the c
     */

    public ImageAdapter(Context c) {
        mContext = c;
        mSparseBooleanArray = new SparseBooleanArray();
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return getImageList().size();
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int arg0) {
        return  imageList.get(arg0);
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    /**
     * Gets the image list.
     *
     * @return the image list
     */
    public ArrayList<GridObject> getImageList() {
        return imageList;
    }

    /**
     * Sets the image list.
     *
     * @param imageList the new image list
     */
    public void setImageList(ArrayList<GridObject> imageList) {
        this.imageList = imageList;

    }
    public ArrayList<String> getCheckedItems() {
        ArrayList<String> mTempArry = new ArrayList<String>();

        for(int i=0;i<imageList.size();i++) {
            if(mSparseBooleanArray.get(i)) {
                Log.i("Delete", "imageList.get(i).getState() " + imageList.get(i).getState());
                mTempArry.add(imageList.get(i).getPath());
            }
        }

        return mTempArry;
    }
    public void unCheckedItems() {
        for(int i=0;i<imageList.size();i++) {
            if(mSparseBooleanArray.get(i)) {
                mSparseBooleanArray.put(i, false);
            }
        }


    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {




        final GridObject gridObject = imageList.get(position);
        if (convertView == null) {
            grid = new View(mContext);
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = inflater.inflate(R.layout.mygrid_layout, parent, false);
        } else {
            grid = (View) convertView;
        }

        gridView = (GridView) parent;

        int width = (Integer) gridView.getTag();

        grid.findViewById(R.id.checkBox1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.i(LOG_TAG,"on Item click ::"+mSparseBooleanArray.get(position));
                ((CompoundButton) v).setChecked((boolean)!mSparseBooleanArray.get(position));
                if(((CompoundButton) v).isChecked()){
                    mSparseBooleanArray.put(position, true);
                }
                else{
                    mSparseBooleanArray.put(position, false);
                }
            }
        });


        final CheckBox mCheckBox = (CheckBox)  grid.findViewById(R.id.checkBox1);
        ImageView imageView = (ImageView) grid.findViewById(R.id.image);
        RelativeLayout imageLayout = (RelativeLayout) grid.findViewById(R.id.imageLayout);

        if (gridObject.getPath().contains(".PDF") || gridObject.getPath().contains("pdf")) {
            Log.d(LOG_TAG,"pathe"+gridObject.getPath());
            imageView.setImageResource(R.drawable.icon);


        }

        imageLayout.setTag(position);
        mCheckBox.setChecked(mSparseBooleanArray.get(position));
        mCheckBox.setTag(gridObject.getPath());


        loadBitmap(gridObject.getPath(), imageView,width);
        return grid;
    }

    /** The m thumb ids. */
    private Integer[] mThumbIds = { R.drawable.icon,
            R.drawable.icon, R.drawable.icon,
            R.drawable.icon, R.drawable.icon,
            R.drawable.icon, R.drawable.icon,
            R.drawable.icon, R.drawable.icon,
            R.drawable.icon, R.drawable.icon,
            R.drawable.icon, R.drawable.icon,
            R.drawable.icon, R.drawable.icon };


    /**
     * Load bitmap.
     *
     * @param fileid the fileid
     * @param imageView the image view
     */
    public void loadBitmap(String fileid, ImageView imageView,int requiredWidth) {



        final Bitmap bitmap = ImageGridViewActivity.getBitmapFromMemCache(fileid);

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
        else {

            CacheImageLoader imageLoader = new CacheImageLoader(mContext, imageView);
            try{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    imageLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                            fileid,String.valueOf(requiredWidth));
                } else {
                    imageLoader.execute(fileid,String.valueOf(requiredWidth));
                }
            }
            catch(Exception e){
               // Log.i(Util.LOG_TAG,"Exception in executeThread");
                e.printStackTrace();
            }
        }



    }
}
