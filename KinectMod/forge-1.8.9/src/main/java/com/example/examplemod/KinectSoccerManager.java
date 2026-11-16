package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class KinectSoccerManager {

    private static final long GAME_DURATION_MS = 30000L;
    private static final long GOAL_MESSAGE_MS = 2000L;

    private static final double IDLE_SPEED_THRESHOLD = 0.20D;
    private static final long IDLE_RESPAWN_MS = 2000L;

    // Tamanho do gol
    private static final double GOAL_WIDTH = 8.0D;
    private static final double GOAL_HEIGHT = 4.0D;
    private static final double GOAL_DEPTH = 0.8D;

    private static final double GOAL_X = 270.0D;
    private static final double GOAL_Y = 4.0D;
    private static final double GOAL_Z = 1781.0D;

    // As duas posições onde as bolas nascem/reaparecem
    private static final double BALL1_SPAWN_X = 270.5D;
    private static final double BALL1_SPAWN_Y = 4.0D;
    private static final double BALL1_SPAWN_Z = 1793.5D;

    private static final double BALL2_SPAWN_X = 10.5D;
    private static final double BALL2_SPAWN_Y = 4.0D;
    private static final double BALL2_SPAWN_Z = 10.5D;

    private static final double KICK_RADIUS = 1.2D;
    private static final double KICK_MIN_FOOT_SPEED = 0.08D;
    private static final double KICK_POWER = 17.0D;
    private static final double MIN_FORWARD_COMPONENT = 0.15D;
    private static final long KICK_COOLDOWN_MS = 1300L;
    private static final double KICK_LIFT_BASE = 0.12D;    // impulso mínimo de altura, mesmo num chute raso
    private static final double KICK_LIFT_SCALE = 3.5D;    // o quanto o movimento vertical do pé influencia a altura
    private static final double KICK_LIFT_MAX = 5.0D;     // limite máximo de altura do chute

    private boolean running = false;
    private long startTime;

    private int goalsPlayer1 = 0;
    private int goalsPlayer2 = 0;

    private boolean startKeyWasDown = false;

    private BallState ball1;
    private BallState ball2;

    // posições anteriores dos pés (pra medir velocidade/direção do chute)
    private float previousRightFootX1, previousRightFootY1, previousRightFootZ1;
    private float previousLeftFootX1, previousLeftFootY1, previousLeftFootZ1;
    private float previousRightFootX2, previousRightFootY2, previousRightFootZ2;
    private float previousLeftFootX2, previousLeftFootY2, previousLeftFootZ2;

    private boolean goalMessageShowing = false;
    private long goalMessageTime = 0L;
    private String goalMessage = "";

    private boolean resultShowing = false;
    private long resultShownAt = 0L;
    private String resultMessage = "";

    public void startGame() {

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer == null || mc.theWorld == null)
            return;

        running = true;
        goalsPlayer1 = 0;
        goalsPlayer2 = 0;
        resultShowing = false;
        goalMessageShowing = false;
        startTime = System.currentTimeMillis();

        clearBalls();

        ball1 = spawnBall(mc, BALL1_SPAWN_X, BALL1_SPAWN_Y, BALL1_SPAWN_Z);
        ball2 = spawnBall(mc, BALL2_SPAWN_X, BALL2_SPAWN_Y, BALL2_SPAWN_Z);

        initializeFootPositions();

        System.out.println("[KinectSoccer] JOGO INICIADO!");
    }

    private void checkIdleRespawn(BallState ball) {

        if (ball == null || ball.entity.isDead)
            return;

        double speed = Math.sqrt(
                ball.entity.motionX * ball.entity.motionX +
                        ball.entity.motionZ * ball.entity.motionZ
        );

        boolean isIdle = ball.entity.onGround && speed < IDLE_SPEED_THRESHOLD;

        if (isIdle) {

            if (ball.idleSince < 0L) {
                ball.idleSince = System.currentTimeMillis();
            } else if (System.currentTimeMillis() - ball.idleSince >= IDLE_RESPAWN_MS) {
                respawnBall(ball);
                ball.idleSince = -1L;
            }

        } else {

            ball.idleSince = -1L;
        }
    }

    private BallState spawnBall(Minecraft mc, double x, double y, double z) {

        ItemStack stack = new ItemStack(Items.fire_charge);

        EntityItem item = new EntityItem(mc.theWorld, x, y, z, stack);

        item.motionX = 0.0D;
        item.motionY = 0.0D;
        item.motionZ = 0.0D;

        item.setPickupDelay(32767); // efetivamente nunca pode ser "pego" pelo jogador

        mc.theWorld.spawnEntityInWorld(item);

        double forwardX = GOAL_X - x;
        double forwardZ = GOAL_Z - z;
        double length = Math.sqrt(forwardX * forwardX + forwardZ * forwardZ);

        forwardX /= length;
        forwardZ /= length;

        return new BallState(item, x, y, z, forwardX, forwardZ);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.END)
            return;

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer == null || mc.theWorld == null)
            return;

        if (mc.currentScreen == null) {

            boolean startDown = Keyboard.isKeyDown(Keyboard.KEY_RBRACKET);

            if (startDown && !startKeyWasDown && !running) {
                startGame();
            }

            startKeyWasDown = startDown;
        }

        if (running) {
            updateGame();
        } else {
            updatePreviousFootPositions();
        }
    }

    private void updateGame() {

        long now = System.currentTimeMillis();

        checkKicksForPlayer(ModKinect.playerManager.getPlayer1(), 1);
        checkKicksForPlayer(ModKinect.playerManager.getPlayer2(), 2);

        checkGoal(ball1);
        checkGoal(ball2);

        checkIdleRespawn(ball1); // <- novo
        checkIdleRespawn(ball2); // <- novo

        if (now - startTime >= GAME_DURATION_MS) {
            endGame();
        }

        if (goalMessageShowing && now - goalMessageTime >= GOAL_MESSAGE_MS) {
            goalMessageShowing = false;
        }
    }

    private void checkKicksForPlayer(KinectPlayer player, int playerNumber) {

        if (player == null)
            return;

        float[][] joints = player.getJoints();

        if (joints == null)
            return;

        float[] leftFoot = joints[14];
        float[] rightFoot = joints[18];

        if (leftFoot == null || rightFoot == null)
            return;

        Minecraft mc = Minecraft.getMinecraft();

        double playerX = mc.thePlayer.posX;
        double playerY = mc.thePlayer.posY;
        double playerZ = mc.thePlayer.posZ;

        tryKick(playerNumber, playerX, playerY, playerZ, rightFoot, true);
        tryKick(playerNumber, playerX, playerY, playerZ, leftFoot, false);

        saveFootPosition(leftFoot, rightFoot, playerNumber);
    }

    private void tryKick(
            int playerNumber,
            double playerX, double playerY, double playerZ,
            float[] foot, boolean rightFoot) {

        float previousX, previousY, previousZ;

        if (playerNumber == 1) {
            previousX = rightFoot ? previousRightFootX1 : previousLeftFootX1;
            previousY = rightFoot ? previousRightFootY1 : previousLeftFootY1;
            previousZ = rightFoot ? previousRightFootZ1 : previousLeftFootZ1;
        } else {
            previousX = rightFoot ? previousRightFootX2 : previousLeftFootX2;
            previousY = rightFoot ? previousRightFootY2 : previousLeftFootY2;
            previousZ = rightFoot ? previousRightFootZ2 : previousLeftFootZ2;
        }

        double movementX = foot[0] - previousX;
        double movementY = foot[1] - previousY;
        double movementZ = foot[2] - previousZ;

        double footSpeed = Math.sqrt(movementX * movementX + movementZ * movementZ);

        if (footSpeed < KICK_MIN_FOOT_SPEED)
            return;

        double footWorldX = playerX + foot[0];
        double footWorldY = playerY + KinectConstants.VERTICAL_OFFSET + foot[1];
        double footWorldZ = playerZ + foot[2];

        tryKickBall(ball1, footWorldX, footWorldY, footWorldZ, movementX, movementY, movementZ);
        tryKickBall(ball2, footWorldX, footWorldY, footWorldZ, movementX, movementY, movementZ);
    }

    private void tryKickBall(
            BallState ball,
            double footX, double footY, double footZ,
            double movementX, double movementY, double movementZ) {

        if (ball == null || ball.entity.isDead)
            return;

        long now = System.currentTimeMillis();

        if (now - ball.lastKickTime < KICK_COOLDOWN_MS)
            return;

        double dx = footX - ball.entity.posX;
        double dy = footY - ball.entity.posY;
        double dz = footZ - ball.entity.posZ;

        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (distance > KICK_RADIUS)
            return;

        double rawLength = Math.sqrt(movementX * movementX + movementZ * movementZ);

        if (rawLength < 0.001D)
            return;

        double rawDirX = movementX / rawLength;
        double rawDirZ = movementZ / rawLength;

        double lateralX = -ball.forwardZ;
        double lateralZ = ball.forwardX;

        double forwardComponent = rawDirX * ball.forwardX + rawDirZ * ball.forwardZ;
        double lateralComponent = rawDirX * lateralX + rawDirZ * lateralZ;

        if (forwardComponent < MIN_FORWARD_COMPONENT) {
            forwardComponent = MIN_FORWARD_COMPONENT;
        }

        double finalDirX = ball.forwardX * forwardComponent + lateralX * lateralComponent;
        double finalDirZ = ball.forwardZ * forwardComponent + lateralZ * lateralComponent;

        double finalLength = Math.sqrt(finalDirX * finalDirX + finalDirZ * finalDirZ);

        finalDirX /= finalLength;
        finalDirZ /= finalLength;

        ball.entity.motionX = finalDirX * KICK_POWER * 0.1D;
        ball.entity.motionZ = finalDirZ * KICK_POWER * 0.1D;

        // impulso vertical: base mínima + proporcional ao quanto o pé subiu
        double verticalImpulse = KICK_LIFT_BASE + Math.max(0.0D, movementY) * KICK_LIFT_SCALE;

        if (verticalImpulse > KICK_LIFT_MAX) {
            verticalImpulse = KICK_LIFT_MAX;
        }

        ball.entity.motionY = verticalImpulse;

        ball.lastKickTime = now;

        ball.idleSince = -1L; // garante que o respawn por "parada" não conte enquanto ela ainda está no ar

        System.out.println("[KinectSoccer] Chute! dir=(" + finalDirX + ", " + finalDirZ + ") altura=" + verticalImpulse);
    }

    private void checkGoal(BallState ball) {

        if (ball == null || ball.entity.isDead)
            return;

        EntityItem entity = ball.entity;

        double depth = Math.abs(entity.posZ - GOAL_Z);

        if (depth > GOAL_DEPTH)
            return;

        double horizontalDistance = Math.abs(entity.posX - GOAL_X);
        double verticalDistance = entity.posY - GOAL_Y;

        if (horizontalDistance <= GOAL_WIDTH / 2.0D &&
                verticalDistance >= 0.0D &&
                verticalDistance <= GOAL_HEIGHT) {

            registerGoal();
            respawnBall(ball);
            return;
        }

        // se a bola foi longe demais do lugar dela (saiu do campo), reposiciona
        double distFromSpawn = entity.getDistance(ball.spawnX, ball.spawnY, ball.spawnZ);

        if (distFromSpawn > 20.0D) {
            respawnBall(ball);
        }
    }

    private void respawnBall(BallState ball) {

        ball.entity.setPosition(ball.spawnX, ball.spawnY, ball.spawnZ);
        ball.entity.motionX = 0.0D;
        ball.entity.motionY = 0.0D;
        ball.entity.motionZ = 0.0D;
    }

    private void registerGoal() {

        // simplificado: como as duas bolas podem ser chutadas por qualquer jogador,
        // aqui contamos como ponto geral. Se quiser saber QUEM chutou por último,
        // dá pra guardar isso no BallState também (me avisa se quiser essa versão).
        goalMessage = "GOL!";
        goalMessageShowing = true;
        goalMessageTime = System.currentTimeMillis();

        System.out.println("[KinectSoccer] " + goalMessage);
    }

    private void saveFootPosition(float[] leftFoot, float[] rightFoot, int playerNumber) {

        if (playerNumber == 1) {
            previousLeftFootX1 = leftFoot[0]; previousLeftFootY1 = leftFoot[1]; previousLeftFootZ1 = leftFoot[2];
            previousRightFootX1 = rightFoot[0]; previousRightFootY1 = rightFoot[1]; previousRightFootZ1 = rightFoot[2];
        } else {
            previousLeftFootX2 = leftFoot[0]; previousLeftFootY2 = leftFoot[1]; previousLeftFootZ2 = leftFoot[2];
            previousRightFootX2 = rightFoot[0]; previousRightFootY2 = rightFoot[1]; previousRightFootZ2 = rightFoot[2];
        }
    }

    private void initializeFootPositions() {

        KinectPlayer player1 = ModKinect.playerManager.getPlayer1();
        KinectPlayer player2 = ModKinect.playerManager.getPlayer2();

        if (player1 != null && player1.getJoints() != null) {
            float[][] joints = player1.getJoints();
            saveFootPosition(joints[14], joints[18], 1);
        }

        if (player2 != null && player2.getJoints() != null) {
            float[][] joints = player2.getJoints();
            saveFootPosition(joints[14], joints[18], 2);
        }
    }

    private void updatePreviousFootPositions() {
        initializeFootPositions();
    }

    private void endGame() {

        running = false;

        clearBalls();

        resultMessage = "Fim de jogo!";

        resultShowing = true;
        resultShownAt = System.currentTimeMillis();

        System.out.println("[KinectSoccer] " + resultMessage);
    }

    private void clearBalls() {

        if (ball1 != null && !ball1.entity.isDead) ball1.entity.setDead();
        if (ball2 != null && !ball2.entity.isDead) ball2.entity.setDead();

        ball1 = null;
        ball2 = null;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isGoalMessageShowing() {

        if (!goalMessageShowing)
            return false;

        if (System.currentTimeMillis() - goalMessageTime >= GOAL_MESSAGE_MS) {
            goalMessageShowing = false;
            return false;
        }

        return true;
    }

    public String getGoalMessage() {
        return goalMessage;
    }

    public boolean isResultShowing() {

        if (!resultShowing)
            return false;

        if (System.currentTimeMillis() - resultShownAt >= 5000L) {
            resultShowing = false;
            return false;
        }

        return true;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    private static class BallState {

        final EntityItem entity;
        final double spawnX, spawnY, spawnZ;
        final double forwardX, forwardZ;
        long lastKickTime = 0L;
        long idleSince = -1L;


        BallState(EntityItem entity, double spawnX, double spawnY, double spawnZ, double forwardX, double forwardZ) {
            this.entity = entity;
            this.spawnX = spawnX;
            this.spawnY = spawnY;
            this.spawnZ = spawnZ;
            this.forwardX = forwardX;
            this.forwardZ = forwardZ;
        }
    }
}