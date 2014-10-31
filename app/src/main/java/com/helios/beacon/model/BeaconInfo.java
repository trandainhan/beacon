package com.helios.beacon.model;

/**
 * Created by nhantran on 10/24/14.
 */
public class BeaconInfo {

    private String uuid;
    private String major;
    private String minor;

    public BeaconInfo(){};

    public BeaconInfo(String uuid, String major, String minor){
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }
}
