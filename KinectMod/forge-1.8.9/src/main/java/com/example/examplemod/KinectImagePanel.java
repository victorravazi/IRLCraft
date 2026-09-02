package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class KinectImagePanel {

    private final ResourceLocation texture;
    private final double x, y, z;
    private final float width, height;
    private final float yaw; // orientação do quadro (0 = virado pro sul, 90 = virado pro leste, etc.)

    public KinectImagePanel(
            ResourceLocation texture,
            double x, double y, double z,
            float width, float height,
            float yaw) {

        this.texture = texture;
        this.x = x; this.y = y; this.z = z;
        this.width = width; this.height = height;
        this.yaw = yaw;
    }

    public void render() {

        Minecraft mc = Minecraft.getMinecraft();

        double renderX = x - mc.getRenderManager().viewerPosX;
        double renderY = y - mc.getRenderManager().viewerPosY;
        double renderZ = z - mc.getRenderManager().viewerPosZ;

        GlStateManager.pushMatrix();

        GlStateManager.translate(renderX, renderY, renderZ);
        GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);

        mc.getTextureManager().bindTexture(texture);

        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        float halfWidth = width / 2.0F;

        GL11.glBegin(GL11.GL_QUADS);

        // frente
        GL11.glTexCoord2f(0.0F, 1.0F); GL11.glVertex3f(-halfWidth, 0.0F, 0.0F);
        GL11.glTexCoord2f(1.0F, 1.0F); GL11.glVertex3f(halfWidth, 0.0F, 0.0F);
        GL11.glTexCoord2f(1.0F, 0.0F); GL11.glVertex3f(halfWidth, height, 0.0F);
        GL11.glTexCoord2f(0.0F, 0.0F); GL11.glVertex3f(-halfWidth, height, 0.0F);

        // fundo, pra dar pra ver dos dois lados
        GL11.glTexCoord2f(1.0F, 1.0F); GL11.glVertex3f(-halfWidth, 0.0F, -0.02F);
        GL11.glTexCoord2f(0.0F, 1.0F); GL11.glVertex3f(halfWidth, 0.0F, -0.02F);
        GL11.glTexCoord2f(0.0F, 0.0F); GL11.glVertex3f(halfWidth, height, -0.02F);
        GL11.glTexCoord2f(1.0F, 0.0F); GL11.glVertex3f(-halfWidth, height, -0.02F);

        GL11.glEnd();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableTexture2D();

        GlStateManager.popMatrix();
    }
}