package org.firstinspires.ftc.teamcode.autos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous
public class LeaveAuto extends OpMode {

    private DcMotor leftFrontDrive;
    private DcMotor rightFrontDrive;
    private DcMotor leftBackDrive;
    private DcMotor rightBackDrive;

    private float DriveTime = 0.5f;
    private float RunTime = 0f;
    private float StartTime = 0f;
    private final double DRIVE_POWER_SCALE = 1.0;

    double leftFrontPower;
    double rightFrontPower;
    double leftBackPower;
    double rightBackPower;

    @Override
    public void init() {
        leftFrontDrive = hardwareMap.get(DcMotor.class, "left_front_drive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "right_front_drive");
        leftBackDrive = hardwareMap.get(DcMotor.class, "left_back_drive");
        rightBackDrive = hardwareMap.get(DcMotor.class, "right_back_drive");

        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);

    }

    @Override
    public void start(){
        StartTime = System.currentTimeMillis() / 1000.0f;
    }

    @Override
    public void loop() {
        RunTime = (System.currentTimeMillis() / 1000.0f) - StartTime;
        if(RunTime < DriveTime){
            mecanumDrive(-1, 0, 0);
        } else {
            mecanumDrive(0, 0, 0);
        }
    }

    void mecanumDrive(double forward, double strafe, double rotate) {
        if (Math.abs(forward) < 0.1) {
            forward = 0;
        }
        if (Math.abs(strafe) < 0.1) {
            strafe = 0;
        }
        if (Math.abs(rotate) < 0.1) {
            rotate = 0;
        }
        leftFrontPower = forward - strafe + rotate;
        rightFrontPower = forward - strafe - rotate;
        leftBackPower = forward + strafe + rotate;
        rightBackPower = forward + strafe - rotate;

        double max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));

        if (max > 1.0) {
            leftFrontPower /= max;
            rightFrontPower /= max;
            leftBackPower /= max;
            rightBackPower /= max;
        }

        /*
         * Send calculated power to wheels, scaled by the dashboard-tunable cap
         */
        leftFrontDrive.setPower(leftFrontPower * DRIVE_POWER_SCALE);
        rightFrontDrive.setPower(rightFrontPower * DRIVE_POWER_SCALE);
        leftBackDrive.setPower(leftBackPower * DRIVE_POWER_SCALE);
        rightBackDrive.setPower(rightBackPower * DRIVE_POWER_SCALE);
    }
}
