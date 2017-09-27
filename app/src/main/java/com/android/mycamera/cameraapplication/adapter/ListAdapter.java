package com.android.mycamera.cameraapplication.adapter;



import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;



import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

;import com.android.mycamera.cameraapplication.R;
import com.android.mycamera.cameraapplication.dataobjects.MyDocument;
import com.android.mycamera.cameraapplication.util.MyCameraApplicationUtil;

public class ListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<MyDocument> documentList  = new ArrayList<MyDocument>();
    public ArrayList<MyDocument> getDocumentList() {
        return documentList;
    }
    public void setDocumentList(ArrayList<MyDocument> documentList) {
        this.documentList = documentList;
    }
    public ListAdapter(Context context){
        mContext = context;
    }
    @Override
    public int getCount() {
        return documentList.size();
    }

    @Override
    public MyDocument getItem(int arg0) {

        return  documentList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {

        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_item, null);

        }
        TextView documentNameView = (TextView) rowView.findViewById(R.id.documentName);
        TextView documentDate = (TextView) rowView.findViewById(R.id.modifiedDate);
        ImageView documentThumbnail = (ImageView) rowView.findViewById(R.id.documentThumbnail);
        TextView pageFormatView = (TextView) rowView.findViewById(R.id.pageFormat);

        MyDocument document = documentList.get(position);
        documentNameView.setText(document.getDocumentName());
        Log.e(MyCameraApplicationUtil.LOG_TAG, "document.getModifiedDate( :" + document.getModifiedDate());
        documentDate.setText(document.getModifiedDate());
        String pageFormatText = "Pages: "+document.getNumberOfPages();

        pageFormatView.setText(pageFormatText);
        if((document.getThumbnailPath() != null)&&(document.getThumbnailPath()!= "")) {
            Bitmap thumbnail = MyCameraApplicationUtil.getThumbnailFromImage(document.getThumbnailPath(),getPixelValue(40),getPixelValue(40));//getThumbnailFromImagePath(document.getThumbnailPath());
            documentThumbnail.setImageBitmap(thumbnail);

        }
        else{
            documentThumbnail.setImageResource(R.drawable.empty_document);

        }

        rowView.setTag(document.getDocumentName());
        return rowView;
    }
    private int getPixelValue(int dpValue){
        final float scale = mContext.getResources().getDisplayMetrics().density;
        int pixels = (int) (dpValue * scale + 0.5f);
        return pixels;
    }
}

