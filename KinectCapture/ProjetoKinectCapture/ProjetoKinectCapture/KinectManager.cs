using Microsoft.Kinect;
using System;

public class KinectManager
{
    private KinectSensor sensor;

    public Skeleton CurrentSkeleton { get; private set; }

    public event Action<Skeleton> SkeletonUpdated;

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

        sensor.SkeletonFrameReady +=
            OnSkeletonFrameReady;

        sensor.Start();
    }

    private void OnSkeletonFrameReady(
        object sender,
        SkeletonFrameReadyEventArgs e)
    {
        using (SkeletonFrame frame =
               e.OpenSkeletonFrame())
        {
            if (frame == null)
                return;

            Skeleton[] skeletons =
                new Skeleton[
                    frame.SkeletonArrayLength
                ];

            frame.CopySkeletonDataTo(
                skeletons
            );

            foreach (Skeleton skeleton in skeletons)
            {
                if (skeleton.TrackingState ==
                    SkeletonTrackingState.Tracked)
                {
                    CurrentSkeleton =
                        skeleton;

                    if (SkeletonUpdated != null)
                    {
                        SkeletonUpdated(
                            skeleton
                        );
                    }

                    return;
                }
            }

            CurrentSkeleton = null;
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