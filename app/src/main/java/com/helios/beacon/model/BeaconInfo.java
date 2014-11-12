package com.helios.beacon.model;

import java.text.DecimalFormat;

/**
 * Created by nhantran on 10/24/14.
 */
public class BeaconInfo {

    private String uuid;
    private String major;
    private String minor;
    private Double distance;

    public BeaconInfo(){};

    public BeaconInfo(String uuid, String major, String minor){
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
    }

    public BeaconInfo(String uuid, String major, String minor, Double distance){
        this(uuid, major, minor);
        this.distance = distance;
    }

    public String getUuid() {
        return "UUID: " + uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMajor() {
        return "Major: " + major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return "Minor: " + minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getDistance() {
        DecimalFormat df = new DecimalFormat("#.##");
        return "Distance: " + df.format(distance) + " meter";
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BeaconInfo that = (BeaconInfo) o;

        if (major != null ? !major.equals(that.major) : that.major != null) return false;
        if (minor != null ? !minor.equals(that.minor) : that.minor != null) return false;
        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (major != null ? major.hashCode() : 0);
        result = 31 * result + (minor != null ? minor.hashCode() : 0);
        return result;
    }
}
