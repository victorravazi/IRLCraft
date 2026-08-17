using Microsoft.Kinect;

public class SkeletonFrameData
{
    public long Timestamp;

    public SkeletonPoint[] Joints;

    public SkeletonFrameData()
    {
        Joints = new SkeletonPoint[20];
    }

    public SkeletonFrameData(Skeleton skeleton)
    {
        Timestamp = System.Environment.TickCount;

        Joints = new SkeletonPoint[20];

        foreach (Joint joint in skeleton.Joints)
        {
            int index = (int)joint.JointType;

            if (index >= 0 && index < Joints.Length)
            {
                Joints[index] = joint.Position;
            }
        }
    }
}