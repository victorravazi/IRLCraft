using OpenTK;
using OpenTK.Graphics;
using OpenTK.Graphics.OpenGL;
using OpenTK.Input;
using Microsoft.Kinect;
using System;

public class SkeletonWindow : GameWindow
{
    private KinectManager kinect;
    private SkeletonRecorder recorder;

    public SkeletonWindow(
        KinectManager kinect,
        SkeletonRecorder recorder)
        : base(
            1000,
            700,
            GraphicsMode.Default,
            "Kinect 3D")
    {
        this.kinect = kinect;
        this.recorder = recorder;
    }

    protected override void OnLoad(EventArgs e)
    {
        base.OnLoad(e);

        GL.ClearColor(0.05f, 0.05f, 0.05f, 1.0f);

        GL.Enable(EnableCap.DepthTest);

        GL.Enable(EnableCap.CullFace);

        GL.Enable(EnableCap.Lighting);
        GL.Enable(EnableCap.Light0);

        GL.Enable(EnableCap.ColorMaterial);

        GL.ColorMaterial(
            MaterialFace.Front,
            ColorMaterialParameter.AmbientAndDiffuse
        );

        GL.Light(
            LightName.Light0,
            LightParameter.Position,
            new float[] { 2f, 3f, -4f, 1f }
        );

        GL.PointSize(10f);
    }

    protected override void OnResize(EventArgs e)
    {
        base.OnResize(e);

        GL.Viewport(
            0,
            0,
            Width,
            Height
        );

        OpenTK.Matrix4 projection =
            OpenTK.Matrix4.CreatePerspectiveFieldOfView(
                OpenTK.MathHelper.DegreesToRadians(60f),
                Width / (float)Height,
                0.1f,
                100f
            );

        GL.MatrixMode(MatrixMode.Projection);

        GL.LoadMatrix(ref projection);
    }

    protected override void OnRenderFrame(
        FrameEventArgs e)
    {
        base.OnRenderFrame(e);

        GL.Clear(
            ClearBufferMask.ColorBufferBit |
            ClearBufferMask.DepthBufferBit
        );

        OpenTK.Matrix4 camera =
            OpenTK.Matrix4.LookAt(
                new OpenTK.Vector3(0, 1.2f, -4f),
                new OpenTK.Vector3(0, 1.2f, 0),
                OpenTK.Vector3.UnitY
            );

        GL.MatrixMode(MatrixMode.Modelview);

        GL.LoadMatrix(ref camera);

        Skeleton skeleton = kinect.CurrentSkeleton;

        if (skeleton != null)
        {
            DrawSkeleton(skeleton);
        }

        SwapBuffers();
    }

    private void DrawSkeleton(Skeleton skeleton)
    {
        // Cabeça
        DrawJoint(
            skeleton,
            JointType.Head,
            0.13f
        );

        // Ombros
        DrawJoint(skeleton, JointType.ShoulderLeft, 0.08f);
        DrawJoint(skeleton, JointType.ShoulderRight, 0.08f);

        // Cotovelos
        DrawJoint(skeleton, JointType.ElbowLeft, 0.07f);
        DrawJoint(skeleton, JointType.ElbowRight, 0.07f);

        // Mãos
        DrawJoint(skeleton, JointType.HandLeft, 0.08f);
        DrawJoint(skeleton, JointType.HandRight, 0.08f);

        // Quadril
        DrawJoint(skeleton, JointType.HipLeft, 0.09f);
        DrawJoint(skeleton, JointType.HipRight, 0.09f);

        // Joelhos
        DrawJoint(skeleton, JointType.KneeLeft, 0.08f);
        DrawJoint(skeleton, JointType.KneeRight, 0.08f);

        // Pés
        DrawJoint(skeleton, JointType.FootLeft, 0.08f);
        DrawJoint(skeleton, JointType.FootRight, 0.08f);


        // Braço esquerdo
        DrawBone(
            skeleton,
            JointType.ShoulderLeft,
            JointType.ElbowLeft,
            0.06f
        );

        DrawBone(
            skeleton,
            JointType.ElbowLeft,
            JointType.WristLeft,
            0.055f
        );

        DrawBone(
            skeleton,
            JointType.WristLeft,
            JointType.HandLeft,
            0.05f
        );


        // Braço direito
        DrawBone(
            skeleton,
            JointType.ShoulderRight,
            JointType.ElbowRight,
            0.06f
        );

        DrawBone(
            skeleton,
            JointType.ElbowRight,
            JointType.WristRight,
            0.055f
        );

        DrawBone(
            skeleton,
            JointType.WristRight,
            JointType.HandRight,
            0.05f
        );


        // Tronco
        DrawBone(
            skeleton,
            JointType.ShoulderCenter,
            JointType.Spine,
            0.16f
        );

        DrawBone(
            skeleton,
            JointType.Spine,
            JointType.HipCenter,
            0.18f
        );


        // Perna esquerda
        DrawBone(
            skeleton,
            JointType.HipLeft,
            JointType.KneeLeft,
            0.09f
        );

        DrawBone(
            skeleton,
            JointType.KneeLeft,
            JointType.AnkleLeft,
            0.075f
        );

        DrawBone(
            skeleton,
            JointType.AnkleLeft,
            JointType.FootLeft,
            0.06f
        );


        // Perna direita
        DrawBone(
            skeleton,
            JointType.HipRight,
            JointType.KneeRight,
            0.09f
        );

        DrawBone(
            skeleton,
            JointType.KneeRight,
            JointType.AnkleRight,
            0.075f
        );

        DrawBone(
            skeleton,
            JointType.AnkleRight,
            JointType.FootRight,
            0.06f
        );
    }


    private void DrawJoint(
        Skeleton skeleton,
        JointType type,
        float radius)
    {
        Joint joint = skeleton.Joints[type];

        if (joint.TrackingState ==
            JointTrackingState.NotTracked)
            return;

        SkeletonPoint p = joint.Position;

        DrawSphere(
            new OpenTK.Vector3(
                p.X,
                p.Y,
                p.Z
            ),
            radius
        );
    }


    private void DrawBone(
        Skeleton skeleton,
        JointType joint1,
        JointType joint2,
        float radius)
    {
        Joint a = skeleton.Joints[joint1];
        Joint b = skeleton.Joints[joint2];

        if (a.TrackingState ==
            JointTrackingState.NotTracked)
            return;

        if (b.TrackingState ==
            JointTrackingState.NotTracked)
            return;

        SkeletonPoint p1 = a.Position;
        SkeletonPoint p2 = b.Position;

        OpenTK.Vector3 start =
            new OpenTK.Vector3(
                p1.X,
                p1.Y,
                p1.Z
            );

        OpenTK.Vector3 end =
            new OpenTK.Vector3(
                p2.X,
                p2.Y,
                p2.Z
            );

        DrawCylinderBetween(
            start,
            end,
            radius
        );
    }


    private void DrawSphere(
        OpenTK.Vector3 position,
        float radius)
    {
        GL.PushMatrix();

        GL.Translate(
            position.X,
            position.Y,
            position.Z
        );

        int slices = 12;
        int stacks = 8;

        for (int i = 0; i < stacks; i++)
        {
            double lat0 =
                Math.PI *
                (-0.5 + (double)i / stacks);

            double z0 =
                Math.Sin(lat0);

            double zr0 =
                Math.Cos(lat0);

            double lat1 =
                Math.PI *
                (-0.5 + (double)(i + 1) / stacks);

            double z1 =
                Math.Sin(lat1);

            double zr1 =
                Math.Cos(lat1);

            GL.Begin(PrimitiveType.QuadStrip);

            for (int j = 0; j <= slices; j++)
            {
                double lng =
                    2 * Math.PI * j / slices;

                double x =
                    Math.Cos(lng);

                double y =
                    Math.Sin(lng);

                GL.Normal3(
                    x * zr0,
                    y * zr0,
                    z0
                );

                GL.Vertex3(
                    radius * x * zr0,
                    radius * y * zr0,
                    radius * z0
                );

                GL.Normal3(
                    x * zr1,
                    y * zr1,
                    z1
                );

                GL.Vertex3(
                    radius * x * zr1,
                    radius * y * zr1,
                    radius * z1
                );
            }

            GL.End();
        }

        GL.PopMatrix();
    }


    private void DrawCylinderBetween(
        OpenTK.Vector3 start,
        OpenTK.Vector3 end,
        float radius)
    {
        OpenTK.Vector3 direction =
            end - start;

        float length =
            direction.Length;

        if (length <= 0.0001f)
            return;

        direction.Normalize();

        GL.PushMatrix();

        GL.Translate(
            start.X,
            start.Y,
            start.Z
        );

        // O cilindro é criado apontando para +Z.
        // Aqui calculamos a rotação para fazê-lo
        // apontar na direção do segundo joint.

        OpenTK.Vector3 zAxis =
            OpenTK.Vector3.UnitZ;

        float dot =
            OpenTK.Vector3.Dot(
                zAxis,
                direction
            );

        dot =
            Math.Max(
                -1f,
                Math.Min(1f, dot)
            );

        float angle =
            (float)Math.Acos(dot);

        OpenTK.Vector3 axis =
            OpenTK.Vector3.Cross(
                zAxis,
                direction
            );

        if (axis.Length > 0.0001f)
        {
            axis.Normalize();

            GL.Rotate(
                OpenTK.MathHelper.RadiansToDegrees(angle),
                axis.X,
                axis.Y,
                axis.Z
            );
        }
        else if (dot < 0)
        {
            GL.Rotate(
                180,
                1,
                0,
                0
            );
        }

        DrawCylinder(
            radius,
            length
        );

        GL.PopMatrix();
    }


    private void DrawCylinder(
        float radius,
        float height)
    {
        int slices = 12;

        GL.Begin(PrimitiveType.QuadStrip);

        for (int i = 0; i <= slices; i++)
        {
            double angle =
                2 * Math.PI * i / slices;

            float x =
                (float)Math.Cos(angle) * radius;

            float y =
                (float)Math.Sin(angle) * radius;

            GL.Normal3(
                Math.Cos(angle),
                Math.Sin(angle),
                0
            );

            GL.Vertex3(
                x,
                y,
                0
            );

            GL.Vertex3(
                x,
                y,
                height
            );
        }

        GL.End();


        // Tampa inferior
        GL.Begin(PrimitiveType.TriangleFan);

        GL.Normal3(0, 0, -1);

        GL.Vertex3(0, 0, 0);

        for (int i = 0; i <= slices; i++)
        {
            double angle =
                2 * Math.PI * i / slices;

            GL.Vertex3(
                Math.Cos(angle) * radius,
                Math.Sin(angle) * radius,
                0
            );
        }

        GL.End();


        // Tampa superior
        GL.Begin(PrimitiveType.TriangleFan);

        GL.Normal3(0, 0, 1);

        GL.Vertex3(0, 0, height);

        for (int i = 0; i <= slices; i++)
        {
            double angle =
                2 * Math.PI * i / slices;

            GL.Vertex3(
                Math.Cos(angle) * radius,
                Math.Sin(angle) * radius,
                height
            );
        }

        GL.End();
    }


    protected override void OnUpdateFrame(FrameEventArgs e)
    {
        base.OnUpdateFrame(e);

        KeyboardState keyboard = Keyboard.GetState();

        // ESC
        if (keyboard.IsKeyDown(Key.Escape))
        {
            Exit();
            return;
        }

        // Começar gravação
        if (keyboard.IsKeyDown(Key.R))
        {
            if (!recorder.IsRecording)
            {
                recorder.Start();

                Title = "Kinect 3D - GRAVANDO";
            }
        }

        // Parar gravação
        if (keyboard.IsKeyDown(Key.T))
        {
            if (recorder.IsRecording)
            {
                recorder.Stop();

                string path =
                    System.IO.Path.Combine(
                        AppDomain.CurrentDomain.BaseDirectory,
                        "movement.knt"
                    );

                recorder.Save(path);

                Title = "Kinect 3D";
            }
        }
    }


    protected override void OnUnload(EventArgs e)
    {
        kinect.Stop();

        base.OnUnload(e);
    }
}