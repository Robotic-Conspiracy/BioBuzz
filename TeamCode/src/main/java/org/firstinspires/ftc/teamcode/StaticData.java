package org.firstinspires.ftc.teamcode;

import com.pedropathing.math.Pose;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class StaticData {
    // launcher stuff
    public static final int LAUNCHER_MIN_VELOCITY = 1200;
    public static final double P = 0;
    public static final double I = 0;
    public static final double D = 0;
    public static final double F = 0;
    public static final String LAUNCHER_NAME = "launcher";
    public static final DcMotor.RunMode LAUNCHER_RUN_MODE = DcMotor.RunMode.RUN_USING_ENCODER;


    // wheel stuff
    public static final String LEFT_FRONT_NAME = "left_front_drive";
    public static final String RIGHT_FRONT_NAME = "right_front_drive";
    public static final String LEFT_BACK_NAME = "left_back_drive";
    public static final String RIGHT_BACK_NAME = "right_back_drive";
    public static final DcMotorSimple.Direction LEFT_FRONT_DIRECTION = DcMotorSimple.Direction.REVERSE;
    public static final DcMotorSimple.Direction RIGHT_FRONT_DIRECTION = DcMotorSimple.Direction.FORWARD;
    public static final DcMotorSimple.Direction LEFT_BACK_DIRECTION = DcMotorSimple.Direction.REVERSE;
    public static final DcMotorSimple.Direction RIGHT_BACK_DIRECTION = DcMotorSimple.Direction.FORWARD;

    // intake stuff
    public static final String INTAKE_NAME = "intake";
    public static final String WINDMILL_SERVO_NAME = "windmillServo";
    public static final String FLOWER_INTAKE_SERVO_NAME = "flower_intake";
//    public static final String LEFT_INTAKE_SERVO_NAME = "left_intake_servo";
//    public static final String RIGHT_INTAKE_SERVO_NAME = "right_intake_servo";
    public static final DcMotorSimple.Direction INTAKE_DIRECTION = DcMotorSimple.Direction.REVERSE;
    public static final DcMotor.ZeroPowerBehavior INTAKE_ZERO_POWER_BEHAVIOR = DcMotor.ZeroPowerBehavior.BRAKE;


    // pinpoint stuff

    public static final String PINPOINT_NAME = "pinpoint";
    public static final double FORWARD_OFFSET = 158.5;
    public static final double LATERAL_OFFSET = 110.5;

    public static final GoBildaPinpointDriver.GoBildaOdometryPods ENCODER_RESOLUTION = GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD;
    public static final GoBildaPinpointDriver.EncoderDirection FORWARD_DIRECTION = GoBildaPinpointDriver.EncoderDirection.FORWARD;
    public static final GoBildaPinpointDriver.EncoderDirection LATERAL_DIRECTION = GoBildaPinpointDriver.EncoderDirection.FORWARD;

    // stuff to be used between files
    public static Pose currentPose;


}
