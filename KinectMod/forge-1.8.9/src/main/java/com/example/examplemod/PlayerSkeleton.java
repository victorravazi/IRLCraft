package com.example.examplemod;

public class PlayerSkeleton {

    private final long trackingId;
    private final float[][] joints;

    //teste
    public PlayerSkeleton(long trackingId, float[][] joints) {
        this.trackingId = trackingId;
        this.joints = joints;
    }

    public long getTrackingId() {
        return trackingId;
    }

    public float[][] getJoints() {
        return joints;
    }
}
