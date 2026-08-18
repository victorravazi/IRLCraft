using System;

class Program
{
    static void Main(string[] args)
    {
        try
        {
            KinectManager kinect = new KinectManager();
            KinectUdpSender sender = new KinectUdpSender("127.0.0.1", 25566);

            kinect.SkeletonsUpdated += sender.Send;
                

            Console.WriteLine(
                "Kinect iniciado."
            );

            Console.WriteLine(
                "Aguardando pessoas..."
            );

            Console.WriteLine(
                "Pressione ENTER para sair."
            );

            Console.ReadLine();
            kinect.Stop();
            sender.Close();
        }
        catch (Exception ex)
        {
            Console.WriteLine(ex);
            Console.ReadLine();
        }
    }
}