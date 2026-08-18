using Microsoft.Kinect;
using System;
using System.Collections.Generic;

public class KinectManager
{
    private KinectSensor sensor;

    public List<Skeleton> CurrentSkeletons { get; private set; }

    public event Action<List<Skeleton>> SkeletonsUpdated;

    public bool IsRunning
    {
        get { return sensor != null; }
    }

    public KinectManager()
    {
        if (KinectSensor.KinectSensors.Count == 0)
            throw new Exception("Kinect não encontrado.");

        sensor = KinectSensor.KinectSensors[0];
        sensor.SkeletonStream.Enable();
        sensor.SkeletonFrameReady += OnSkeletonFrameReady;


        CurrentSkeletons = new List<Skeleton>();
        sensor.Start();
    }

    private void OnSkeletonFrameReady(
        object sender,
        SkeletonFrameReadyEventArgs e)
    {
        using (SkeletonFrame frame = e.OpenSkeletonFrame())
        {
            if (frame == null)
                return;

            Skeleton[] skeletons = new Skeleton[frame.SkeletonArrayLength];

            frame.CopySkeletonDataTo(skeletons);

            List<Skeleton> trackedSkeletons = new List<Skeleton>();

            foreach (Skeleton skeleton in skeletons)
            {
                if (skeleton == null)
                    continue;

                if (skeleton.TrackingState == SkeletonTrackingState.Tracked)
                {
                    trackedSkeletons.Add(skeleton);
                    if (trackedSkeletons.Count >= 2)
                        break;
                }
            }

            CurrentSkeletons = trackedSkeletons;

            Console.WriteLine("Pessoas detectadas: " + trackedSkeletons.Count);

            if (SkeletonsUpdated != null)
            {
                SkeletonsUpdated(trackedSkeletons);
            }
        }
    }

    public void Stop()
    {
        if (sensor != null)
        {
            sensor.Stop();
            sensor = null;
        }
    }
}