package com.example.examplemod;

import com.example.examplemod.PlayerSkeleton;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

public class KinectReceiver extends Thread {

    private final int port;

    private DatagramSocket socket;

    private volatile List<PlayerSkeleton> players = new ArrayList<PlayerSkeleton>();

    public KinectReceiver(int port) {
        this.port = port;
    }

    @Override
    public void run() {

        try {

            socket = new DatagramSocket(port);

            System.out.println("[Kinect] Receiver iniciado na porta " + port);

            byte[] buffer = new byte[4096];

            while (!isInterrupted()) {

                DatagramPacket packet = new DatagramPacket(buffer,buffer.length);

                socket.receive(packet);

                readPacket(packet.getData(),packet.getLength());
            }

        } catch (IOException e) {

            if (!isInterrupted()) {
                e.printStackTrace();
            }
        }
    }

    private void readPacket(byte[] data,int length) {
        try {
            ByteArrayInputStream input = new ByteArrayInputStream(data,0,length);

            DataInputStream stream = new DataInputStream(input);

            int playerCount = readIntLE(stream);

            if (playerCount < 0 || playerCount > 2) {

                System.out.println("[Kinect] Quantidade inválida: " + playerCount );

                return;
            }

            List<PlayerSkeleton> newPlayers = new ArrayList<PlayerSkeleton>();

            for (int p = 0; p < playerCount; p++) {


                long trackingId = readLongLE(stream);

                int jointCount = readIntLE(stream);


                if (jointCount != 20) {
                    System.out.println("[Kinect] Joint count inválido: "+ jointCount );
                    return;
                }

                float[][] joints = new float[20][3];

                for (int i = 0; i < 20; i++) {

                    joints[i][0] = readFloatLE(stream);


                    joints[i][1] = readFloatLE(stream);


                    joints[i][2] = readFloatLE(stream);

                }

                newPlayers.add(new PlayerSkeleton(trackingId,joints));

            }

            players = newPlayers;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<PlayerSkeleton> getPlayers() {
        return players;
    }

    private int readIntLE(DataInputStream stream)throws IOException {

        int b1 = stream.readUnsignedByte();
        int b2 = stream.readUnsignedByte();
        int b3 = stream.readUnsignedByte();
        int b4 = stream.readUnsignedByte();

        return (b1) | (b2 << 8) | (b3 << 16) | (b4 << 24);
    }

    private long readLongLE(DataInputStream stream) throws IOException {
        long result = 0;

        for (int i = 0; i < 8; i++) {

            result |= ((long) stream.readUnsignedByte()) << (8 * i);

        }
        return result;
    }

    private float readFloatLE(DataInputStream stream) throws IOException {
        return Float.intBitsToFloat(readIntLE(stream));

    }

    public void stopReceiver() {

        interrupt();

        if (socket != null) {
            socket.close();
        }
    }
}