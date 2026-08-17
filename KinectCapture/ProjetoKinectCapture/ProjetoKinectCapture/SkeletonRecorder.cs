using Microsoft.Kinect;
using System;
using System.Collections.Generic;
using System.IO;

public class SkeletonRecorder
{
    private List<SkeletonFrameData> frames;

    private DateTime startTime;

    public bool IsRecording { get; private set; }

    public SkeletonRecorder()
    {
        frames = new List<SkeletonFrameData>();
    }

    public void Start()
    {
        frames.Clear();

        startTime = DateTime.Now;

        IsRecording = true;

        Console.WriteLine("GRAVAÇÃO INICIADA");
    }

    public void AddFrame(Skeleton skeleton)
    {
        if (!IsRecording)
            return;

        SkeletonFrameData frame =
            new SkeletonFrameData(skeleton);

        frame.Timestamp =
            (long)(DateTime.Now - startTime)
            .TotalMilliseconds;

        frames.Add(frame);
    }

    public void Stop()
    {
        IsRecording = false;

        Console.WriteLine(
            "GRAVAÇÃO FINALIZADA"
        );

        Console.WriteLine(
            "Frames gravados: " +
            frames.Count
        );
    }

    public void Save(string file)
    {
        using (BinaryWriter writer =
               new BinaryWriter(
                   File.Open(
                       file,
                       FileMode.Create
                   )))
        {
            // Identificação do arquivo
            writer.Write("KNT1");

            // Número de frames
            writer.Write(frames.Count);

            foreach (SkeletonFrameData frame in frames)
            {
                writer.Write(frame.Timestamp);

                for (int i = 0;
                     i < frame.Joints.Length;
                     i++)
                {
                    SkeletonPoint p =
                        frame.Joints[i];

                    writer.Write(p.X);
                    writer.Write(p.Y);
                    writer.Write(p.Z);
                }
            }
        }

        Console.WriteLine(
            "Arquivo salvo: " + file
        );
    }

    public List<SkeletonFrameData> GetFrames()
    {
        return frames;
    }
}