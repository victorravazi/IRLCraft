package com.example.examplemod;

public class KinectNoOpTrigger implements KinectInteractable {

    private final float x;
    private final float y;
    private final float z;
    private final float interactionRadius;
    private final String label;

    public KinectNoOpTrigger(float x, float y, float z, float interactionRadius, String label) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.interactionRadius = interactionRadius;
        this.label = label;
    }

    @Override
    public boolean isNear(float handX, float handY, float handZ) {

        float dx = handX - x;
        float dy = handY - y;
        float dz = handZ - z;

        float distance = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);

        return distance < interactionRadius;
    }

    @Override
    public void interact() {
        System.out.println("[KinectNoOpTrigger] Tocado: " + label);
    }
}