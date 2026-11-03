package org.firstinspires.ftc.teamcode.autos.Blue;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.StaticData;
import org.firstinspires.ftc.teamcode.autos.FullAutoMain;

@Autonomous(name = "FullAutoBlue", group = "Autonomous")
public class FullAuto extends FullAutoMain {

    private final PoseFactory poseFactory = PoseFactory.degrees();
    private DcMotor leftFrontDrive;

    @Override
    public Pose GetStartPath(){
        return poseFactory.of(107, 9, 90);
    }

    @Override
    public Path StartToShootPath(){
        Pose start = poseFactory.of(107, 9, 90);
        Pose controlPoint1 = poseFactory.of(123.8659, 104.2135, 0);
        Pose controlPoint2 = poseFactory.of(100.6193, 109.1605, 0);
        Pose point = poseFactory.of(84.0936, 107.4248, 270);

        return Paths.curve(start, controlPoint1, controlPoint2, point).linear(start, point);
    }

    @Override
    public Path ShootToFlowerPath1(){
        Pose start = poseFactory.of(84.0936, 107.4248, 270);
        Pose controlPoint1 = poseFactory.of(89.901, 109.4047, 0);
        Pose point = poseFactory.of(131.6788, 94.533, 180);

        return Paths.curve(start, controlPoint1, point).linear(start, point).linear(start, point);

    }

    @Override
    public Path FlowerToShootPath1(){
        Pose start = poseFactory.of(131.6788, 94.533, 180);
        Pose controlPoint1 = poseFactory.of(121.4172, 35.7943, 0);
        Pose point = poseFactory.of(84.7623, 33.1418, 90);

        return Paths.curve(start, controlPoint1, point).linear(start, point);
    }

    @Override
    public Path ShootToFlowerPath2(){
        Pose start = poseFactory.of(84.7623, 33.1418, 90);
        Pose point = poseFactory.of(94.6615, 10.7596, 90);

        return Paths.line(start, point).linear(start, point);
    }

    @Override
    public Path FlowerToShootPath2(){
        Pose start = poseFactory.of(94.6615, 10.7596, 90);
        Pose point = poseFactory.of(85.2035, 33.1674, 90);

        return Paths.line(start, point).linear(start, point);
    }

    @Override
    public Path ShootToPark(){
        Pose start = poseFactory.of(85.2035, 33.1674, 90);
        Pose point = poseFactory.of(131.3038, 34.2737, 90);

        return Paths.line(start, point).linear(start, point);
    }
}
