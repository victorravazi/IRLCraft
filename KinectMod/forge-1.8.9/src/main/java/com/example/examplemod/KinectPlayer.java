package com.example.examplemod;

import net.minecraft.util.ResourceLocation;

public class KinectPlayer {

    private final int id;

    private long trackingId = -1;

    private String nickname;

    private SkinType skin;

    private ResourceLocation resolvedSkinTexture;

    private boolean resolvedSlim;

    private float[][] joints;

    public KinectPlayer(int id) {

        this.id = id;

        this.nickname = "Player " + id;

        this.skin = SkinType.STEVE;
    }

    public ResourceLocation getResolvedSkinTexture() {
        return resolvedSkinTexture;
    }

    public boolean isResolvedSlim() {
        return resolvedSlim;
    }

    public void setResolvedSkin(ResourceLocation texture, boolean slim) {
        this.resolvedSkinTexture = texture;
        this.resolvedSlim = slim;
    }

    /*
     * Limpa a skin customizada resolvida, voltando
     * a usar Steve/Alex fixos (SkinType).
     */
    public void clearResolvedSkin() {
        this.resolvedSkinTexture = null;
        this.resolvedSlim = false;
    }

    public int getId() {
        return id;
    }

    public long getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(long trackingId) {
        this.trackingId = trackingId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {

        if (nickname == null || nickname.trim().isEmpty()) {
            this.nickname = "Player " + id;
            return;
        }

        this.nickname = nickname;
    }

    public SkinType getSkin() {
        return skin;
    }

    public void setSkin(SkinType skin) {
        this.skin = skin;
    }

    public float[][] getJoints() {
        return joints;
    }

    public void setJoints(float[][] joints) {
        this.joints = joints;
    }
}