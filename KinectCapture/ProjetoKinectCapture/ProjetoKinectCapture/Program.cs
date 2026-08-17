using System;

class Program
{
    static void Main(string[] args)
    {
        try
        {
            KinectManager kinect =
                new KinectManager();

            SkeletonRecorder recorder =
                new SkeletonRecorder();

            KinectUdpSender sender =
               new KinectUdpSender(
                   "127.0.0.1",
                   25566
               );

            kinect.SkeletonUpdated +=
                recorder.AddFrame;

            kinect.SkeletonUpdated +=
               sender.Send;

            using (SkeletonWindow window =
                   new SkeletonWindow(
                       kinect,
                       recorder))
            {
                window.Run(60.0);
            }
            sender.Close();
        }
        catch (Exception ex)
        {
            Console.WriteLine(ex);

            Console.ReadLine();
        }
    }
}