package com.example.examplemod;

public class KinectTeleportTrigger implements KinectInteractable {

    public enum Action {
        NEXT,
        PREVIOUS,
        FIRST,
        LAST
    }

    private final float x;
    private final float y;
    private final float z;
    private final float interactionRadius;
    private final Action action;

    private static final long INTERACT_COOLDOWN_MS = 1000L;
    private long lastInteractTime = 0L;

    public KinectTeleportTrigger(float x, float y, float z, float interactionRadius, Action action) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.interactionRadius = interactionRadius;
        this.action = action;
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

        long now = System.currentTimeMillis();

        if (now - lastInteractTime < INTERACT_COOLDOWN_MS)
            return;

        lastInteractTime = now;

        if (ModKinect.teleportManager == null)
            return;

        switch (action) {
            case NEXT:
                ModKinect.teleportManager.teleportNext();
                break;

            case PREVIOUS:
                ModKinect.teleportManager.teleportPrevious();
                break;

            case FIRST:
                ModKinect.teleportManager.teleportFirst();
                break;

            case LAST:
                ModKinect.teleportManager.teleportLast();
                break;
        }
    }
}