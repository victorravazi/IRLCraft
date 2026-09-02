package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class KinectMinigameManager {

    private static final long GAME_DURATION_MS = 25000L;
    private static final long SPAWN_INTERVAL_MS = 1500L;
    private static final double OBSTACLE_SPEED = 0.30D;
    private static final float HIT_RADIUS = 0.5F;
    private static final double SPAWN_DISTANCE = 12.0D;
    private static final long RESULT_DISPLAY_MS = 5000L;
    private static final float ARRIVAL_YAW_DEGREES = 0.0F; // direção fixa de onde os carrinhos vêm (0 = norte, ajuste conforme seu mapa)
    private static final int LANE_COUNT = 4;
    private static final double LANE_WIDTH = 1.2D;

    private static final int[] BODY_JOINTS = {
            0, 1, 2, 3,
            4, 8,
            6, 7, 10, 11,
            12, 16,
            13, 14, 17, 18
    };

    private boolean running = false;
    private long startTime;
    private long lastSpawnTime;
    private boolean startKeyWasDown = false;

    private int hitCount1 = 0;
    private int hitCount2 = 0;

    private double anchorX, anchorY, anchorZ;

    private final List<ObstacleData> obstacles = new ArrayList<>();

    private boolean resultShowing = false;
    private long resultShownAt;
    private String resultMessage = "";

    public void startGame() {

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer == null || mc.theWorld == null)
            return;

        running = true;
        resultShowing = false;
        startTime = System.currentTimeMillis();
        lastSpawnTime = 0L;

        hitCount1 = 0;
        hitCount2 = 0;

        clearObstacles();

        anchorX = mc.thePlayer.posX;
        anchorY = mc.thePlayer.posY;
        anchorZ = mc.thePlayer.posZ;

        System.out.println("[KinectMinigame] Minigame iniciado!");
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.END)
            return;

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer == null || mc.theWorld == null)
            return;

        if (running) {
            updateRunningGame(mc);
        }

        if (mc.currentScreen == null) {

            boolean startDown = org.lwjgl.input.Keyboard.isKeyDown(org.lwjgl.input.Keyboard.KEY_LBRACKET);

            if (startDown && !startKeyWasDown && !running) {
                startGame();
            }

            startKeyWasDown = startDown;
        }
    }

    private void updateRunningGame(Minecraft mc) {

        long now = System.currentTimeMillis();
        long elapsed = now - startTime;

        if (now - lastSpawnTime > SPAWN_INTERVAL_MS) {
            spawnObstacle(mc);
            lastSpawnTime = now;
        }

        moveObstacles();
        checkHits();

        if (elapsed >= GAME_DURATION_MS) {
            endGame();
        }
    }

    private void spawnObstacle(Minecraft mc) {

        double yawRad = Math.toRadians(ARRIVAL_YAW_DEGREES);

        // vetor de direção (de onde nasce -> na direção do jogador)
        double dirX = -Math.sin(yawRad);
        double dirZ = Math.cos(yawRad);

        // vetor perpendicular, usado pra separar as faixas
        double perpX = -dirZ;
        double perpZ = dirX;

        int laneIndex = (int) (Math.random() * LANE_COUNT);

        // centraliza as faixas em volta do eixo central (ex: 4 faixas -> -1.5, -0.5, 0.5, 1.5)
        double laneOffset = (laneIndex - (LANE_COUNT - 1) / 2.0D) * LANE_WIDTH;

        double spawnX = anchorX - dirX * SPAWN_DISTANCE + perpX * laneOffset;
        double spawnZ = anchorZ - dirZ * SPAWN_DISTANCE + perpZ * laneOffset;
        double spawnY = anchorY + 0.10D;

        EntityMinecartEmpty cart = new EntityMinecartEmpty(mc.theWorld);
        cart.setPosition(spawnX, spawnY, spawnZ);

        cart.motionX = dirX * OBSTACLE_SPEED;
        cart.motionZ = dirZ * OBSTACLE_SPEED;

        mc.theWorld.spawnEntityInWorld(cart);

        obstacles.add(new ObstacleData(cart, new HashSet<Integer>()));

        System.out.println("[KinectMinigame] Carrinho na faixa " + laneIndex);
    }

    private void moveObstacles() {

        Iterator<ObstacleData> iterator = obstacles.iterator();

        while (iterator.hasNext()) {

            ObstacleData data = iterator.next();

            if (data.entity.isDead) {
                iterator.remove();
                continue;
            }

            data.entity.posX += data.entity.motionX;
            data.entity.posZ += data.entity.motionZ;
            data.entity.setPosition(data.entity.posX, data.entity.posY, data.entity.posZ);

            double dx = data.entity.posX - anchorX;
            double dz = data.entity.posZ - anchorZ;
            double distFromAnchor = Math.sqrt(dx * dx + dz * dz);

            if (distFromAnchor > SPAWN_DISTANCE + 5.0D) {
                data.entity.setDead();
                iterator.remove();
            }
        }
    }

    private void checkHits() {
        checkHitsForPlayer(ModKinect.playerManager.getPlayer1(), 1);
        checkHitsForPlayer(ModKinect.playerManager.getPlayer2(), 2);
    }

    private void checkHitsForPlayer(KinectPlayer kinectPlayer, int playerNumber) {

        if (kinectPlayer == null)
            return;

        float[][] joints = kinectPlayer.getJoints();

        if (joints == null)
            return;

        EntityPlayer minecraftPlayer = Minecraft.getMinecraft().thePlayer;

        for (ObstacleData data : obstacles) {

            if (data.hitPlayers.contains(playerNumber))
                continue;

            for (int jointIndex : BODY_JOINTS) {

                float[] joint = joints[jointIndex];

                double jointWorldX = minecraftPlayer.posX + joint[0];
                double jointWorldY = minecraftPlayer.posY + KinectConstants.VERTICAL_OFFSET + joint[1];
                double jointWorldZ = minecraftPlayer.posZ + joint[2];

                double dx = jointWorldX - data.entity.posX;
                double dy = jointWorldY - data.entity.posY;
                double dz = jointWorldZ - data.entity.posZ;

                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

                if (distance < HIT_RADIUS) {

                    data.hitPlayers.add(playerNumber);

                    if (playerNumber == 1) hitCount1++;
                    else hitCount2++;

                    System.out.println("[KinectMinigame] Player " + playerNumber + " atingido! Total: " +
                            (playerNumber == 1 ? hitCount1 : hitCount2));

                    break;
                }
            }
        }
    }

    private void endGame() {

        running = false;

        clearObstacles();

        resultMessage = "Fim de jogo! Player 1: " + hitCount1 + " acertos | Player 2: " + hitCount2 + " acertos";

        resultShowing = true;
        resultShownAt = System.currentTimeMillis();

        System.out.println("[KinectMinigame] " + resultMessage);
    }

    private void clearObstacles() {

        for (ObstacleData data : obstacles) {
            if (!data.entity.isDead) {
                data.entity.setDead();
            }
        }

        obstacles.clear();
    }

    public boolean isResultShowing() {

        if (!resultShowing)
            return false;

        if (System.currentTimeMillis() - resultShownAt > RESULT_DISPLAY_MS) {
            resultShowing = false;
            return false;
        }

        return true;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    private static class ObstacleData {

        final EntityMinecartEmpty entity;
        final Set<Integer> hitPlayers;

        ObstacleData(EntityMinecartEmpty entity, Set<Integer> hitPlayers) {
            this.entity = entity;
            this.hitPlayers = hitPlayers;
        }
    }
}