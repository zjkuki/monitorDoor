package com.janady.services.BaiduMapSDK;

public enum BaiduMapZoom {

    //"10m", "20m", "50m", "100m", "200m",
    //"500m", "1km", "2km", "5km", "10km",
    //"20km", "25km", "50km", "100km", "200km"
    //"500km", "1000km", "2000km"
    zoom_20(20,10),
    zoom_19(19,20),
    zoom_18(18,50),
    zoom_17(17,100),
    zoom_16(16,200),

    zoom_15(14.8F,500),
    zoom_14(14,1000),
    zoom_13(13,2000),
    zoom_12(12,5000),
    zoom_11(11,10000),

    zoom_10(10,20000),
    zoom_9(9,25000),
    zoom_8(8,50000),
    zoom_7(7,100000),
    ;

    private float zoom;

    private int unitDistance;

    BaiduMapZoom(float zoom,int unitDistance){
        this.zoom = zoom;
        this.unitDistance = unitDistance;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    public int getUnitDistance() {
        return unitDistance;
    }

    public void setUnitDistance(int unitDistance) {
        this.unitDistance = unitDistance;
    }

}