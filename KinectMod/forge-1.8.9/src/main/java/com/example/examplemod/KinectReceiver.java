package com.example.examplemod;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class KinectReceiver {

    private DatagramSocket socket;

    private volatile float[][] joints;

    private volatile boolean running;

    public KinectReceiver(int port) throws Exception {

        joints = new float[20][3];

        socket = new DatagramSocket(port);

        running = true;

        Thread thread = new Thread(
                this::receiveLoop,
                "Kinect-UDP"
        );

        thread.setDaemon(true);

        thread.start();
    }

    private void receiveLoop() {

        byte[] buffer =
                new byte[244];

        while (running) {

            try {

                DatagramPacket packet =
                        new DatagramPacket(
                                buffer,
                                buffer.length
                        );

                socket.receive(packet);

                ByteBuffer data =
                        ByteBuffer.wrap(
                                packet.getData(),
                                0,
                                packet.getLength()
                        );

                data.order(
                        ByteOrder.LITTLE_ENDIAN
                );

                int count =
                        data.getInt();

                if (count != 20)
                    continue;

                float[][] newJoints =
                        new float[20][3];

                for (int i = 0; i < 20; i++) {

                    newJoints[i][0] =
                            data.getFloat();

                    newJoints[i][1] =
                            data.getFloat();

                    newJoints[i][2] =
                            data.getFloat();
                }

                joints = newJoints;


            } catch (Exception e) {

                if (running) {
                    e.printStackTrace();
                }
            }
        }
    }

    public float[][] getJoints() {
        return joints;
    }

    public void stop() {

        running = false;

        if (socket != null) {
            socket.close();
        }
    }
}
