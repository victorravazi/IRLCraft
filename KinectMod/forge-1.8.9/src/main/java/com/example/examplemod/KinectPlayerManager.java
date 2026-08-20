package com.example.examplemod;

import java.util.List;

public class KinectPlayerManager {

    private final KinectPlayer player1;
    private final KinectPlayer player2;

    public KinectPlayerManager() {

        player1 = new KinectPlayer(1);
        player2 = new KinectPlayer(2);
    }

    public KinectPlayer getPlayer1() {
        return player1;
    }

    public KinectPlayer getPlayer2() {
        return player2;
    }

    public KinectPlayer getPlayer(int id) {

        if (id == 1)
            return player1;

        if (id == 2)
            return player2;

        return null;
    }

    public void updatePlayers(List<PlayerSkeleton> skeletons) {

        if (skeletons == null || skeletons.isEmpty()) {

            player1.setJoints(null);
            player2.setJoints(null);

            return;
        }

        /*
         * Primeiro jogador
         */
        if (skeletons.size() >= 1) {

            PlayerSkeleton skeleton =
                    skeletons.get(0);

            player1.setTrackingId(
                    skeleton.getTrackingId()
            );

            player1.setJoints(
                    skeleton.getJoints()
            );
        }

        /*
         * Segundo jogador
         */
        if (skeletons.size() >= 2) {

            PlayerSkeleton skeleton =
                    skeletons.get(1);

            player2.setTrackingId(
                    skeleton.getTrackingId()
            );

            player2.setJoints(
                    skeleton.getJoints()
            );

        } else {

            player2.setJoints(null);
        }
    }
}