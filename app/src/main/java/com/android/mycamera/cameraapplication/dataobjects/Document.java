package com.android.mycamera.cameraapplication.dataobjects;


import java.util.Date;


public class Document implements Comparable<Document>{

    private int id;
    private String documentName;
    private String modifiedDate;
    private String thumbnailPath;

    private int numberOfPages;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }



    @Override
    public int compareTo(Document another) {
        return another.getDocumentName().compareTo(documentName);
    }
}
