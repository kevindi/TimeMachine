package com.di.kevin.timemachine.object;

/**
 * Created by dike on 20/5/2015.
 */
public class MyLocation {
    public String getLocationName() {
        return LocationName;
    }

    public void setLocationName(String locationName) {
        LocationName = locationName;
    }

    String LocationName;

    public double getLocationLng() {
        return LocationLng;
    }

    public void setLocationLng(double locationLng) {
        LocationLng = locationLng;
    }

    public double getLocationLat() {
        return LocationLat;
    }

    public void setLocationLat(double locationLat) {
        LocationLat = locationLat;
    }

    double LocationLat;
    double LocationLng;

    public long getLocationId() {
        return LocationId;
    }

    public void setLocationId(long locationId) {
        LocationId = locationId;
    }

    long LocationId;

    public float getDistanceToMe() {
        return distanceToMe;
    }

    public void setDistanceToMe(float distanceToMe) {
        this.distanceToMe = distanceToMe;
    }

    float distanceToMe;
}
