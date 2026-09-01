package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class KinectTeleportManager {

    private static final String SAVE_FILE_NAME = "kinect_teleports.txt";

    private final List<TeleportPoint> points = new ArrayList<>();

    private boolean nextKeyWasDown = false;
    private boolean prevKeyWasDown = false;
    private boolean firstKeyWasDown = false;
    private boolean lastKeyWasDown = false;

    private int currentIndex = -1;

    public KinectTeleportManager() {
        loadFromFile();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.END)
            return;

        boolean nextDown = Keyboard.isKeyDown(Keyboard.KEY_H);
        boolean prevDown = Keyboard.isKeyDown(Keyboard.KEY_J);
        boolean firstDown = Keyboard.isKeyDown(Keyboard.KEY_B);
        boolean lastDown = Keyboard.isKeyDown(Keyboard.KEY_N);

        if (nextDown && !nextKeyWasDown)
            teleportNext();

        if (prevDown && !prevKeyWasDown)
            teleportPrevious();

        if (firstDown && !firstKeyWasDown)
            teleportFirst();

        if (lastDown && !lastKeyWasDown)
            teleportLast();

        nextKeyWasDown = nextDown;
        prevKeyWasDown = prevDown;
        firstKeyWasDown = firstDown;
        lastKeyWasDown = lastDown;
    }

    public void addPoint(double x, double y, double z, float yaw, float pitch) {

        String name = "Ponto " + (points.size() + 1);

        points.add(new TeleportPoint(x, y, z, yaw, pitch, name));

        saveToFile();

        System.out.println("[KinectTeleport] " + name + " salvo: " + x + ", " + y + ", " + z);
    }


    public void teleportTo(int index) {

        if (index < 0 || index >= points.size())
            return;

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer == null)
            return;

        TeleportPoint point = points.get(index);

        EntityPlayer player = mc.thePlayer;

        player.setPosition(point.x, point.y, point.z);
        player.rotationYaw = point.yaw;
        player.rotationPitch = point.pitch;

        currentIndex = index;

        if (ModKinect.cameraManager != null) {
            ModKinect.cameraManager.syncFrozenPlayerToCurrentPlayer();
        }

        System.out.println("[KinectTeleport] Teleportado para " + point.name);
    }

    public void teleportNext() {
        System.out.println(
                "[KinectTeleport] H/NEXT pressionado. Pontos disponíveis: "
                        + points.size()
        );
        if (points.isEmpty()) {
            System.out.println("[KinectTeleport] Nenhum ponto de teleporte!");
            return;
        }
        int next = currentIndex < 0 ? 0 : (currentIndex + 1) % points.size();
        teleportTo(next);
    }

    public void teleportPrevious() {
        if (points.isEmpty()) return;
        int prev = currentIndex <= 0 ? points.size() - 1 : currentIndex - 1;
        teleportTo(prev);
    }

    public void teleportFirst() {
        teleportTo(0);
    }

    public void teleportLast() {
        teleportTo(points.size() - 1);
    }

    private java.io.File getSaveFile() {
        return new java.io.File(Minecraft.getMinecraft().mcDataDir, SAVE_FILE_NAME);
    }

    private void saveToFile() {

        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(getSaveFile()))) {

            for (TeleportPoint point : points) {

                writer.println(
                        point.name + "|" +
                                point.x + "|" + point.y + "|" + point.z + "|" +
                                point.yaw + "|" + point.pitch
                );
            }

        } catch (Exception e) {
            System.out.println("[KinectTeleport] Erro ao salvar: " + e.getMessage());
        }
    }

    private void loadFromFile() {

        java.io.File file = getSaveFile();

        if (!file.exists())
            return;

        points.clear();

        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty())
                    continue;

                String[] parts = line.split("\\|");

                if (parts.length < 6)
                    continue;

                TeleportPoint point = new TeleportPoint(
                        Double.parseDouble(parts[1]),
                        Double.parseDouble(parts[2]),
                        Double.parseDouble(parts[3]),
                        Float.parseFloat(parts[4]),
                        Float.parseFloat(parts[5]),
                        parts[0]
                );

                points.add(point);
            }

            System.out.println("[KinectTeleport] " + points.size() + " pontos carregados.");

        } catch (Exception e) {
            System.out.println("[KinectTeleport] Erro ao carregar: " + e.getMessage());
        }
    }

    public static class TeleportPoint {

        public final double x, y, z;
        public final float yaw, pitch;
        public final String name;

        public TeleportPoint(double x, double y, double z, float yaw, float pitch, String name) {
            this.x = x; this.y = y; this.z = z;
            this.yaw = yaw; this.pitch = pitch;
            this.name = name;
        }
    }
}