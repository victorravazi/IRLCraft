using Microsoft.Kinect;
using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;

public class KinectUdpSender
{
    private UdpClient client;
    private IPEndPoint endpoint;

    public KinectUdpSender(string ip, int port)
    {
        client = new UdpClient();
        endpoint = new IPEndPoint(IPAddress.Parse(ip), port);
    }

    public void Send(List<Skeleton> skeletons)
    {
        if (skeletons == null)
            return;

        int personSize = 8 + 4 + (20 * 12);
        int packetSize = 4 + (skeletons.Count * personSize);

        byte[] data = new byte[packetSize];

        int offset = 0;
        WriteInt(data, offset, skeletons.Count);
        offset += 4;

        foreach (Skeleton skeleton in skeletons)
        {
            WriteLong(data, offset, skeleton.TrackingId);
            offset += 8;
            WriteInt(data,offset, 20);
            offset += 4;

            
            for (int i = 0; i < 20; i++)
            {
                JointType type = (JointType)i;
                Joint joint = skeleton.Joints[type];
                SkeletonPoint p = joint.Position;
                WriteFloat(data, offset, p.X);
                offset += 4;
                WriteFloat(data, offset, p.Y);
                offset += 4;
                WriteFloat(data, offset, p.Z);
                offset += 4;
            }
        }

        client.Send(data, data.Length, endpoint);
    }

    private void WriteInt(byte[] data, int offset, int value)
    {
        data[offset] = (byte)(value & 0xFF);
        data[offset + 1] = (byte)((value >> 8) & 0xFF);
        data[offset + 2] = (byte)((value >> 16) & 0xFF);
        data[offset + 3] = (byte)((value >> 24) & 0xFF);
    }

    private void WriteLong(byte[] data, int offset, long value)
    {
        byte[] bytes = BitConverter.GetBytes(value);
        Buffer.BlockCopy(bytes, 0, data, offset, 8);
    }

    private void WriteFloat(byte[] data, int offset, float value)
    {
        byte[] bytes = BitConverter.GetBytes(value);
        Buffer.BlockCopy(bytes, 0, data, offset, 4);
    }

    public void Close()
    {
        client.Close();
    }
}