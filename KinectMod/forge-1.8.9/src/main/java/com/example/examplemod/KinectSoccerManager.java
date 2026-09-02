package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KinectSoccerManager {

    // Tempo total da partida
    private static final long GAME_DURATION_MS = 30000L;

    // Tempo que a mensagem "Player X marcou!" fica na tela
    private static final long GOAL_MESSAGE_MS = 2000L;

    // Velocidade da bola
    private static final double BALL_SPEED = 0.65D;

    // Distância mínima entre o pé e o gol para considerar que a bola chegou
    private static final double GOAL_DEPTH = 0.8D;

    // Tamanho do gol
    private static final double GOAL_WIDTH = 3.0D;
    private static final double GOAL_HEIGHT = 3.0D;

    // Altura em que a bola é criada
    private static final double BALL_HEIGHT = 0.35D;

    /*
     * POSIÇÃO DO GOL
     *
     * ALTERE ESTES 3 VALORES PARA O LOCAL DO SEU GOL.
     */
    private static final double GOAL_X = 270.0D;
    private static final double GOAL_Y = 4.0D;
    private static final double GOAL_Z = 1778.0D;

    private static final double BALL_OFFSET_X = 0.40D;

    /*
     * Distância mínima para considerar que houve um chute.
     *
     * Quanto maior:
     *  - mais difícil detectar o chute
     *
     * Quanto menor:
     *  - mais fácil detectar
     */
    private static final double KICK_THRESHOLD = 0.10D;

    /*
     * Tempo mínimo entre dois chutes do mesmo jogador.
     */
    private static final long KICK_COOLDOWN_MS = 800L;

    private boolean running = false;

    private long startTime;

    private int goalsPlayer1 = 0;
    private int goalsPlayer2 = 0;

    private boolean startKeyWasDown = false;

    /*
     * Última posição dos pés.
     */
    private float previousRightFootX1;
    private float previousRightFootY1;
    private float previousRightFootZ1;

    private float previousLeftFootX1;
    private float previousLeftFootY1;
    private float previousLeftFootZ1;

    private float previousRightFootX2;
    private float previousRightFootY2;
    private float previousRightFootZ2;

    private float previousLeftFootX2;
    private float previousLeftFootY2;
    private float previousLeftFootZ2;

    /*
     * Evita detectar vários chutes seguidos.
     */
    private long lastKickPlayer1 = 0L;
    private long lastKickPlayer2 = 0L;

    /*
     * Bolas atualmente no campo.
     */
    private final List<BallData> balls = new ArrayList<>();

    /*
     * Mensagem de gol.
     */
    private boolean goalMessageShowing = false;
    private long goalMessageTime = 0L;
    private String goalMessage = "";

    /*
     * Resultado final.
     */
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

        /*
         * Inicializa os pés para evitar que o primeiro movimento
         * seja interpretado como chute.
         */
        initializeFootPositions();

        System.out.println("[KinectSoccer] =====================");
        System.out.println("[KinectSoccer] JOGO INICIADO!");
        System.out.println("[KinectSoccer] =====================");
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.END)
            return;

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer == null || mc.theWorld == null)
            return;

        /*
         * Tecla [ para iniciar.
         */
        if (mc.currentScreen == null) {

            boolean startDown =
                    Keyboard.isKeyDown(Keyboard.KEY_RBRACKET);

            if (startDown && !startKeyWasDown && !running) {
                startGame();
            }

            startKeyWasDown = startDown;
        }

        /*
         * Atualiza o jogo.
         */
        if (running) {

            updateGame(mc);

        } else {

            /*
             * Mesmo fora do jogo atualizamos as posições dos pés.
             * Isso evita um salto grande quando a partida começar.
             */
            updatePreviousFootPositions();
        }
    }

    private void updateGame(Minecraft mc) {

        long now = System.currentTimeMillis();

        /*
         * Detecta chute dos dois jogadores.
         */
        checkKick(
                ModKinect.playerManager.getPlayer1(),
                1
        );

        checkKick(
                ModKinect.playerManager.getPlayer2(),
                2
        );

        /*
         * Move as bolas.
         */
        moveBalls();

        /*
         * Verifica gols.
         */
        checkGoals();

        /*
         * Final da partida.
         */
        if (now - startTime >= GAME_DURATION_MS) {
            endGame();
        }

        /*
         * Mensagem de gol.
         */
        if (goalMessageShowing &&
                now - goalMessageTime >= GOAL_MESSAGE_MS) {

            goalMessageShowing = false;
        }
    }

    private void checkKick(
            KinectPlayer player,
            int playerNumber
    ) {

        if (player == null)
            return;

        float[][] joints = player.getJoints();

        if (joints == null)
            return;

        float[] leftFoot = joints[14];
        float[] rightFoot = joints[18];

        if (leftFoot == null || rightFoot == null)
            return;

        long now = System.currentTimeMillis();

        long lastKick =
                playerNumber == 1
                        ? lastKickPlayer1
                        : lastKickPlayer2;

        /*
         * Evita vários chutes seguidos.
         */
        if (now - lastKick < KICK_COOLDOWN_MS) {

            saveFootPosition(
                    leftFoot,
                    rightFoot,
                    playerNumber
            );

            return;
        }

        /*
         * Movimento do pé esquerdo.
         */
        double leftMovementX;
        double leftMovementY;
        double leftMovementZ;

        /*
         * Movimento do pé direito.
         */
        double rightMovementX;
        double rightMovementY;
        double rightMovementZ;

        if (playerNumber == 1) {

            leftMovementX =
                    leftFoot[0] - previousLeftFootX1;

            leftMovementY =
                    leftFoot[1] - previousLeftFootY1;

            leftMovementZ =
                    leftFoot[2] - previousLeftFootZ1;

            rightMovementX =
                    rightFoot[0] - previousRightFootX1;

            rightMovementY =
                    rightFoot[1] - previousRightFootY1;

            rightMovementZ =
                    rightFoot[2] - previousRightFootZ1;

        } else {

            leftMovementX =
                    leftFoot[0] - previousLeftFootX2;

            leftMovementY =
                    leftFoot[1] - previousLeftFootY2;

            leftMovementZ =
                    leftFoot[2] - previousLeftFootZ2;

            rightMovementX =
                    rightFoot[0] - previousRightFootX2;

            rightMovementY =
                    rightFoot[1] - previousRightFootY2;

            rightMovementZ =
                    rightFoot[2] - previousRightFootZ2;
        }

        /*
         * Calcula a força do movimento de cada pé.
         */
        double leftSpeed =
                Math.sqrt(
                        leftMovementX * leftMovementX +
                                leftMovementY * leftMovementY +
                                leftMovementZ * leftMovementZ
                );

        double rightSpeed =
                Math.sqrt(
                        rightMovementX * rightMovementX +
                                rightMovementY * rightMovementY +
                                rightMovementZ * rightMovementZ
                );

        /*
         * Se qualquer pé se movimentou bastante,
         * considera que houve um chute.
         */
        if (leftSpeed > KICK_THRESHOLD ||
                rightSpeed > KICK_THRESHOLD) {

            /*
             * Usa o pé que teve o maior movimento.
             */
            if (rightSpeed >= leftSpeed) {

                shootBall(
                        player,
                        playerNumber,
                        rightMovementX,
                        rightMovementY,
                        rightMovementZ
                );

            } else {

                shootBall(
                        player,
                        playerNumber,
                        leftMovementX,
                        leftMovementY,
                        leftMovementZ
                );
            }

            if (playerNumber == 1)
                lastKickPlayer1 = now;
            else
                lastKickPlayer2 = now;
        }

        /*
         * Salva as posições atuais.
         */
        saveFootPosition(
                leftFoot,
                rightFoot,
                playerNumber
        );
    }

    private double calculateForwardMovement(
            float[] foot,
            int playerNumber,
            boolean rightFoot
    ) {

        float previousX;
        float previousZ;

        if (playerNumber == 1) {

            if (rightFoot) {

                previousX = previousRightFootX1;
                previousZ = previousRightFootZ1;

            } else {

                previousX = previousLeftFootX1;
                previousZ = previousLeftFootZ1;
            }

        } else {

            if (rightFoot) {

                previousX = previousRightFootX2;
                previousZ = previousRightFootZ2;

            } else {

                previousX = previousLeftFootX2;
                previousZ = previousLeftFootZ2;
            }
        }

        /*
         * Direção do jogador até o gol.
         */
        Minecraft mc = Minecraft.getMinecraft();

        double playerX = mc.thePlayer.posX;
        double playerZ = mc.thePlayer.posZ;

        /*
         * Pequeno deslocamento lateral para separar
         * os dois jogadores.
         *
         * Isso não altera o renderer.
         */
        if (playerNumber == 1)
            playerX -= 2.0D;
        else
            playerX += 2.0D;

        double directionX = GOAL_X - playerX;
        double directionZ = GOAL_Z - playerZ;

        double length =
                Math.sqrt(
                        directionX * directionX +
                                directionZ * directionZ
                );

        if (length < 0.001D)
            return 0.0D;

        directionX /= length;
        directionZ /= length;

        /*
         * Movimento do pé.
         */
        double movementX = foot[0] - previousX;
        double movementZ = foot[2] - previousZ;

        /*
         * Produto escalar:
         *
         * mede quanto o pé avançou na direção do gol.
         */
        return movementX * directionX +
                movementZ * directionZ;
    }

    private void saveFootPosition(
            float[] leftFoot,
            float[] rightFoot,
            int playerNumber
    ) {

        if (playerNumber == 1) {

            previousLeftFootX1 = leftFoot[0];
            previousLeftFootY1 = leftFoot[1];
            previousLeftFootZ1 = leftFoot[2];

            previousRightFootX1 = rightFoot[0];
            previousRightFootY1 = rightFoot[1];
            previousRightFootZ1 = rightFoot[2];

        } else {

            previousLeftFootX2 = leftFoot[0];
            previousLeftFootY2 = leftFoot[1];
            previousLeftFootZ2 = leftFoot[2];

            previousRightFootX2 = rightFoot[0];
            previousRightFootY2 = rightFoot[1];
            previousRightFootZ2 = rightFoot[2];
        }
    }


    private void initializeFootPositions() {

        KinectPlayer player1 =
                ModKinect.playerManager.getPlayer1();

        KinectPlayer player2 =
                ModKinect.playerManager.getPlayer2();

        if (player1 != null &&
                player1.getJoints() != null) {

            float[][] joints = player1.getJoints();

            saveFootPosition(
                    joints[14],
                    joints[18],
                    1
            );
        }

        if (player2 != null &&
                player2.getJoints() != null) {

            float[][] joints = player2.getJoints();

            saveFootPosition(
                    joints[14],
                    joints[18],
                    2
            );
        }
    }


    private void updatePreviousFootPositions() {

        initializeFootPositions();
    }

    private void shootBall(
            KinectPlayer player,
            int playerNumber,
            double movementX,
            double movementY,
            double movementZ
    ) {

        Minecraft mc = Minecraft.getMinecraft();

        float[][] joints = player.getJoints();

        if (joints == null)
            return;

        /*
         * Usa o pé direito como posição inicial da bola.
         */
        float[] foot = joints[18];

        if (foot == null)
            return;

        double playerX = mc.thePlayer.posX;
        double playerY = mc.thePlayer.posY;
        double playerZ = mc.thePlayer.posZ;

        /*
         * Separa os dois jogadores.
         */
        if (playerNumber == 1)
            playerX -= 2.0D;
        else
            playerX += 2.0D;

        /*
         * Posição inicial da bola.
         */
        double ballX = playerX + foot[0] + BALL_OFFSET_X;

        double ballY =
                playerY +
                        KinectConstants.VERTICAL_OFFSET +
                        foot[1] +
                        BALL_HEIGHT;

        double ballZ = playerZ + foot[2];

        /*
         * Direção do chute = movimento do pé.
         */
        double directionX = movementX;
        double directionY = movementY;
        double directionZ = movementZ;

        /*
         * Normaliza a direção.
         */
        double length =
                Math.sqrt(
                        directionX * directionX +
                                directionY * directionY +
                                directionZ * directionZ
                );

        if (length < 0.001D)
            return;

        directionX /= length;
        directionY /= length;
        directionZ /= length;

        /*
         * Cria a bolinha de neve.
         */
        EntitySnowball ball =
                new EntitySnowball(
                        mc.theWorld,
                        ballX,
                        ballY,
                        ballZ
                );

        ball.setPosition(
                ballX,
                ballY,
                ballZ
        );

        /*
         * A bola segue exatamente a direção
         * em que o pé foi chutado.
         */
        ball.motionX = directionX * BALL_SPEED;
        ball.motionY = directionY * BALL_SPEED;
        ball.motionZ = directionZ * BALL_SPEED;

        mc.theWorld.spawnEntityInWorld(ball);

        balls.add(
                new BallData(
                        ball,
                        playerNumber
                )
        );

        System.out.println(
                "[KinectSoccer] Player " +
                        playerNumber +
                        " chutou!"
        );

        System.out.println(
                "[KinectSoccer] Direção: " +
                        directionX + ", " +
                        directionY + ", " +
                        directionZ
        );
    }

    private void moveBalls() {

        Iterator<BallData> iterator =
                balls.iterator();

        while (iterator.hasNext()) {

            BallData data = iterator.next();

            if (data.entity.isDead) {

                iterator.remove();
                continue;
            }

            EntitySnowball ball = data.entity;

            /*
             * Movimentação manual.
             */
            ball.posX += ball.motionX;
            ball.posY += ball.motionY;
            ball.posZ += ball.motionZ;

            ball.setPosition(
                    ball.posX,
                    ball.posY,
                    ball.posZ
            );

            /*
             * Remove se passou muito longe.
             */
            double distance =
                    ball.getDistance(
                            GOAL_X,
                            GOAL_Y,
                            GOAL_Z
                    );

            if (distance > 30.0D) {

                ball.setDead();
                iterator.remove();
            }
        }
    }

    private void checkGoals() {

        Iterator<BallData> iterator =
                balls.iterator();

        while (iterator.hasNext()) {

            BallData data = iterator.next();

            EntitySnowball ball = data.entity;

            if (ball.isDead) {

                iterator.remove();
                continue;
            }

            /*
             * Distância no eixo Z até o gol.
             */
            double depth =
                    Math.abs(ball.posZ - GOAL_Z);

            if (depth > GOAL_DEPTH)
                continue;

            /*
             * Verifica largura.
             */
            double horizontalDistance =
                    Math.abs(ball.posX - GOAL_X);

            /*
             * Verifica altura.
             */
            double verticalDistance =
                    ball.posY - GOAL_Y;

            if (horizontalDistance <= GOAL_WIDTH / 2.0D &&
                    verticalDistance >= 0.0D &&
                    verticalDistance <= GOAL_HEIGHT) {

                registerGoal(
                        data.playerNumber
                );

                ball.setDead();
                iterator.remove();
            }
        }
    }

    private void registerGoal(int playerNumber) {

        if (playerNumber == 1)
            goalsPlayer1++;
        else
            goalsPlayer2++;

        goalMessage =
                "Player " +
                        playerNumber +
                        ": marcou!";

        goalMessageShowing = true;

        goalMessageTime =
                System.currentTimeMillis();

        System.out.println(
                "[KinectSoccer] " +
                        goalMessage
        );

        System.out.println(
                "[KinectSoccer] Placar: " +
                        goalsPlayer1 +
                        " x " +
                        goalsPlayer2
        );
    }

    private void endGame() {

        running = false;

        clearBalls();

        if (goalsPlayer1 > goalsPlayer2) {

            resultMessage =
                    "Fim de jogo! Player 1 venceu! " +
                            "P1: " + goalsPlayer1 +
                            " | P2: " + goalsPlayer2;

        } else if (goalsPlayer2 > goalsPlayer1) {

            resultMessage =
                    "Fim de jogo! Player 2 venceu! " +
                            "P1: " + goalsPlayer1 +
                            " | P2: " + goalsPlayer2;

        } else {

            resultMessage =
                    "Fim de jogo! Empate! " +
                            "P1: " + goalsPlayer1 +
                            " | P2: " + goalsPlayer2;
        }

        resultShowing = true;

        resultShownAt =
                System.currentTimeMillis();

        System.out.println(
                "[KinectSoccer] " +
                        resultMessage
        );
    }

    private void clearBalls() {

        for (BallData data : balls) {

            if (!data.entity.isDead)
                data.entity.setDead();
        }

        balls.clear();
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isGoalMessageShowing() {

        if (!goalMessageShowing)
            return false;

        if (System.currentTimeMillis() -
                goalMessageTime >= GOAL_MESSAGE_MS) {

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

        if (System.currentTimeMillis() -
                resultShownAt >= 5000L) {

            resultShowing = false;

            return false;
        }

        return true;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    private static class BallData {

        final EntitySnowball entity;
        final int playerNumber;

        BallData(
                EntitySnowball entity,
                int playerNumber
        ) {

            this.entity = entity;
            this.playerNumber = playerNumber;
        }
    }
}