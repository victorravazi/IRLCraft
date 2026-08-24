package com.example.examplemod;

public interface KinectInteractable {

    boolean isNear(float x, float y, float z);

    void interact();
}