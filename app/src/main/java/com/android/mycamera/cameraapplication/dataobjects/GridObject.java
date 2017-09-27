package com.android.mycamera.cameraapplication.dataobjects;



public class GridObject{


    private String path;
    private int state;
    private boolean cleaned;
    /**
     * Instantiates a new grid object.
     *
     * @param path the path
     * @param state the state
     */
    public GridObject(){
        super();
    }

    public boolean isCleaned() {
        return cleaned;
    }

    public void setCleaned(boolean cleaned) {
        this.cleaned = cleaned;
    }

    public GridObject(String path, int state, boolean cleaned) {
        super();
        this.path = path;
        this.state = state;
        this.cleaned = cleaned;
    }

    /**
     * Gets the path.
     *
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the path.
     *
     * @param path the new path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Gets the state.
     *
     * @return the state
     */
    public int getState() {
        return state;
    }

    /**
     * Sets the state.
     *
     * @param state the new state
     */
    public void setState(int state) {
        this.state = state;
    }
    @Override
    public boolean equals(Object o) {

        GridObject fileObj = (GridObject) o;
        return state==fileObj.state;
    }

}
