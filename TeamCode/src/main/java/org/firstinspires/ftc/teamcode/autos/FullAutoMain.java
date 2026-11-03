package org.firstinspires.ftc.teamcode.autos;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.StaticData;
import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous(name = "donrt run ", group = "Autonomous")
@Configurable
public abstract class FullAutoMain extends OpMode {

    // motors and servos
    private DcMotorEx launcher;
    private DcMotor intake;
    private CRServo windmillServo;
    private CRServo leftIntakeServo;
    private CRServo rightIntakeServo;

    // telemetry
    private TelemetryManager panelsTelemetry;
    public Follower follower;

    // pathing stuff


    // states
    private ActionStates state = ActionStates.Parked;
    private Paths currentPath = Paths.StartToShootPath;

    // timers
    final ElapsedTime intakeTimer = new ElapsedTime();
    final ElapsedTime shootTimer = new ElapsedTime();

    // abstract methods
    public abstract Pose GetStartPath();
    public abstract Path StartToShootPath();
    public abstract Path ShootToFlowerPath1();
    public abstract Path FlowerToShootPath1();
    public abstract Path ShootToFlowerPath2();
    public abstract Path FlowerToShootPath2();
    public abstract Path ShootToPark();


    // setup
    private void init_launcher(){
        launcher = hardwareMap.get(DcMotorEx.class, StaticData.LAUNCHER_NAME);
        launcher.setMode(StaticData.LAUNCHER_RUN_MODE);
        launcher.setPIDFCoefficients(StaticData.LAUNCHER_RUN_MODE, new PIDFCoefficients(StaticData.P, StaticData.I, StaticData.D, StaticData.F));

        windmillServo = hardwareMap.get(CRServo.class, StaticData.WINDMILL_SERVO_NAME);

    }

    private void init_intake(){
        intake = hardwareMap.get(DcMotor.class, StaticData.INTAKE_NAME);
        leftIntakeServo = hardwareMap.get(CRServo.class, StaticData.LEFT_INTAKE_SERVO_NAME);
        rightIntakeServo = hardwareMap.get(CRServo.class, StaticData.RIGHT_INTAKE_SERVO_NAME);

        intake.setZeroPowerBehavior(StaticData.INTAKE_ZERO_POWER_BEHAVIOR);
    }

    @Override
    public void init(){
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();
        init_launcher();
        init_intake();
        follower = Constants.create(hardwareMap);

        follower.setPose(GetStartPath());

    }

    // main loop
    @Override
    public void loop(){
        follower.update();
        updateMove();
        StaticData.currentPose = follower.pose();
        panelsTelemetry.update(telemetry);
    }

    // moving
    public void updateMove(){

        switch (currentPath){
            case StartToShootPath:
                follower.follow(StartToShootPath());
                if(follower.atParametricEnd()){
                    switch (state){
                        case Shooting:
                            updateShoot();
                            break;
                        case Moving:
                            shootTimer.reset();
                            state = ActionStates.Shooting;
                            break;
                        case Parked:
                            state = ActionStates.Moving;
                            currentPath = Paths.ShootToFlowerPath1;
                            break;
                    }
                }
                break;
            case ShootToFlowerPath1:
                follower.follow(ShootToFlowerPath1());
                if(follower.remainingDistance() <= 10 || follower.atParametricEnd()){
                    switch (state){
                        case Intaking:
                            updateIntake();
                            break;
                        case Moving:
                            intakeTimer.reset();
                            state = ActionStates.Intaking;
                            break;
                        case Parked:
                            state = ActionStates.Moving;
                            currentPath = Paths.FlowerToShootPath1;
                            break;
                    }
                }
                break;
            case FlowerToShootPath1:
                follower.follow(FlowerToShootPath1());
                if(follower.atParametricEnd()){
                    switch (state){
                        case Shooting:
                            updateShoot();
                            break;
                        case Moving:
                            shootTimer.reset();
                            state = ActionStates.Shooting;
                            break;
                        case Parked:
                            state = ActionStates.Moving;
                            currentPath = Paths.ShootToFlowerPath2;
                            break;
                    }
                }
                break;
            case ShootToFlowerPath2:
                follower.follow(ShootToFlowerPath2());
                if(follower.remainingDistance() <= 10 || follower.atParametricEnd()){
                    switch (state){
                        case Intaking:
                            updateIntake();
                            break;
                        case Moving:
                            intakeTimer.reset();
                            state = ActionStates.Intaking;
                            break;
                        case Parked:
                            state = ActionStates.Moving;
                            currentPath = Paths.FlowerToShootPath2;
                            break;
                    }
                }
                break;
            case FlowerToShootPath2:
                follower.follow(FlowerToShootPath2());
                if(follower.atParametricEnd()){
                    switch (state){
                        case Shooting:
                            updateShoot();
                            break;
                        case Moving:
                            shootTimer.reset();
                            state = ActionStates.Shooting;
                            break;
                        case Parked:
                            state = ActionStates.Moving;
                            currentPath = Paths.ShootToPark;
                            break;
                    }
                }
                break;
            case ShootToPark:
                follower.follow(ShootToPark());
                if(follower.atParametricEnd()){
                    state = ActionStates.Parked;
                    currentPath = Paths.Done;
                }
                break;
            case Done:
                break;
        }
    }


    // shooting
    public void updateShoot(){
        launcher.setVelocity(1225);
        windmillServo.setPower(1);
        if (shootTimer.seconds() >= 0.5){
            launcher.setVelocity(0);
            windmillServo.setPower(0);
            state = ActionStates.Parked;
        }
    }

    // intake
    public void updateIntake(){
        intake.setPower(1);
        leftIntakeServo.setPower(1);
        rightIntakeServo.setPower(1);
        if (intakeTimer.seconds() >= 0.5){
            intake.setPower(0);
            leftIntakeServo.setPower(0);
            rightIntakeServo.setPower(0);
            state = ActionStates.Parked;
        }
    }

    // enums
    private enum Paths{
        StartToShootPath,
        ShootToFlowerPath1,
        FlowerToShootPath1,
        ShootToFlowerPath2,
        FlowerToShootPath2,
        ShootToPark,
        Done
    }

    private enum ActionStates{
        Intaking,
        Shooting,
        Moving,
        Parked

    }
}
