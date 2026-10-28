/*   MIT License
 *   Copyright (c) [2026] [Base 10 Assets, LLC]
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:

 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.

 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 */
/* ETHAN DONT CHANGE THIS CHUD */
/* ETHAN DONT CHANGE THIS CHUD */
/* ETHAN DONT CHANGE THIS CHUD */
/* ETHAN DONT CHANGE THIS CHUD */
/* ETHAN DONT CHANGE THIS CHUD */
/* ETHAN DONT CHANGE THIS CHUD */
/* ETHAN DONT CHANGE THIS CHUD */
/* ETHAN DONT CHANGE THIS CHUD */
/* ETHAN DONT CHANGE THIS CHUD */
/* ETHAN DONT CHANGE THIS CHUD */
/* ETHAN DONT CHANGE THIS CHUD */
/* ETHAN DONT CHANGE THIS CHUD */
/* ETHAN DONT CHANGE THIS CHUD */
/* ETHAN DONT CHANGE THIS CHUD */
/* ETHAN DONT CHANGE THIS CHUD */
/* ETHAN DONT CHANGE THIS CHUD */
/* ETHAN DONT CHANGE THIS CHUD */
/* ETHAN DONT CHANGE THIS CHUD */
/* ETHAN DONT CHANGE THIS CHUD */
/* ETHAN DONT CHANGE THIS CHUD */
/* ETHAN DONT CHANGE THIS CHUD */
/* ETHAN DONT CHANGE THIS CHUD */
/* ETHAN DONT CHANGE THIS CHUD */
/* ETHAN DONT CHANGE THIS CHUD */
/* ETHAN DONT CHANGE THE CONFIG CHUD */
/* ETHAN DONT CHANGE THE CONFIG CHUD */
/* ETHAN DONT CHANGE THE CONFIG CHUD */
/* ETHAN DONT CHANGE THE CONFIG CHUD */
/* ETHAN DONT CHANGE THE CONFIG CHUD */
/* ETHAN DONT CHANGE THE CONFIG CHUD */
/* ETHAN DONT CHANGE THE CONFIG CHUD */
/* ETHAN DONT CHANGE THE CONFIG CHUD */





package org.firstinspires.ftc.teamcode;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import android.content.Context;
import android.content.SharedPreferences;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.ValueProvider;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.ftccommon.external.OnCreate;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.VoltageSensor;

/*
 * This file includes a teleop (driver-controlled) file for the goBILDA® StarterBot with Mecanum
 * Wheels for the 2026-2027 FIRST® Tech Challenge. On top of a mecanum wheel drivetrain, it uses
 * one motor driving an intake roller, two servos which pull elements out of corners, and a high-speed
 * launcher motor.
 *
 * Likely the most niche concept we'll use in this example is closed-loop motor velocity control.
 * This control method reads the current speed as reported by the motor's encoder and applies a varying
 * amount of power to reach, and then hold a target velocity. The FTC SDK calls this control method
 * "RUN_USING_ENCODER". This contrasts to the default "RUN_WITHOUT_ENCODER" where you control the power
 * applied to the motor directly.
 * Since the dynamics of a launcher wheel system varies greatly from those of most other FTC mechanisms,
 * we will also need to adjust the "PIDF" coefficients with some that are a better fit for our application.
 */

@TeleOp(name = "Mec BioBuzz StarterBot Teleop", group = "StarterBot")
//@Disabled
public class BioBuzzStarterbotTeleopMecanum extends OpMode {

    // Declare OpMode members.
    private DcMotor leftFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor rightBackDrive = null;
    private DcMotorEx launcher = null;
    private DcMotor intake = null;
    private CRServo leftIntakeServo = null;
    private CRServo rightIntakeServo = null;
    private CRServo windmillServo = null;
    private VoltageSensor voltageSensor = null;


    /*
     * These two variables are used to control the velocity of the launcher motor.
     * They are both in encoder ticks per second. The motors we use in the FIRST Tech Challenge
     * have encoders with a resolution of 28 ticks per revolution. We can convert this to RPM
     * by dividing the value by 28, to get to revolutions per second, before multiplying by 60
     * to get revolutions per minute.
     * We pass the target velocity variable to our motor to set the goal. The min velocity was
     * going to gate the windmill until the launcher is up to speed, but that check is off
     * right now.
     */
    // code defaults for the dashboard tuning, so we can tell a fresh default from a dashboard edit
    private static final double DEF_DRIVE_POWER_SCALE = 1.0;
    private static final double DEF_DRIVE_DEADZONE = 0.1;
    private static final int DEF_LAUNCHER_TARGET_VELOCITY = 1225;
    private static final double DEF_WINDMILL_POWER = 1.0;
    private static final long DEF_WINDMILL_180_DEG_MS = 500;
    private static final double DEF_NOMINAL_VOLTAGE = 12.5;
    private static final long DEF_WINDMILL_PAUSE_MS = 100;

    public static int LAUNCHER_TARGET_VELOCITY = DEF_LAUNCHER_TARGET_VELOCITY; // ~2625 RPM
    public final int LAUNCHER_MIN_VELOCITY = 1200; //idk RPM
    // Overall drive speed cap, 1.0 = full speed. Tune live from the dashboard.
    public static double DRIVE_POWER_SCALE = DEF_DRIVE_POWER_SCALE;

    // deadzone for the drive sticks, tunable in the dashboard. stick movement smaller
    // than this counts as zero. 0.1 is a normal value, not too much.
    public static double DRIVE_DEADZONE = DEF_DRIVE_DEADZONE;

    // Windmill continuous servo 180-degree increment tuning and state
    public static double WINDMILL_POWER = DEF_WINDMILL_POWER;
    public static long WINDMILL_180_DEG_MS = DEF_WINDMILL_180_DEG_MS; // Milliseconds for 180-degree rotation at nominal voltage (12.5V)
    public static double NOMINAL_VOLTAGE = DEF_NOMINAL_VOLTAGE;  // Benchmark battery voltage for automatic time compensation
    public static long WINDMILL_PAUSE_MS = DEF_WINDMILL_PAUSE_MS;   // Milliseconds pause between continuous 180° increments

    private boolean windmillIsRotating = false;
    private boolean windmillIsPausing = false;
    private long windmillStartTime = 0;


    /*
     * These four variables store the power we need to apply to the motors. In other cases, we may
     * choose to declare these variables inside the mecanumDrive() function, instead we declare them
     * here so that we can access them in our main loop  for telemetry.
     */
    double leftFrontPower;
    double rightFrontPower;
    double leftBackPower;
    double rightBackPower;

    // Create a variable to set to the intake.
    double intakePower;

    // dpad drive modes: 0 = normal, 1 = always inverted, 2 = inverted while the intake runs.
    // one single value, so two modes can never be on at the same time.
    int driveMode = 0;
    boolean dpadUpWasPressed = false;
    boolean dpadDownWasPressed = false;
    SharedPreferences prefs;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {

        // bring back the drive mode from last time. anything weird in storage means normal.
        prefs = hardwareMap.appContext.getSharedPreferences("biobuzz", Context.MODE_PRIVATE);
        int savedMode;
        try {
            savedMode = prefs.getInt("driveMode", 0);
        } catch (ClassCastException e) {
            savedMode = 0;
        }
        driveMode = (savedMode == 1 || savedMode == 2) ? savedMode : 0;

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        /*
         * Initialize the hardware variables. Note that the strings used here as parameters
         * to 'get' must correspond to the names assigned during the robot configuration
         * step.
         */
        leftFrontDrive = hardwareMap.get(DcMotor.class, "left_front_drive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "right_front_drive");
        leftBackDrive = hardwareMap.get(DcMotor.class, "left_back_drive");
        rightBackDrive = hardwareMap.get(DcMotor.class, "right_back_drive");
        intake = hardwareMap.get(DcMotor.class, "intake");
        launcher = hardwareMap.get(DcMotorEx.class, "launcher");
        windmillServo = hardwareMap.get(CRServo.class, "windmillServo");
        voltageSensor = hardwareMap.voltageSensor.iterator().next();
        leftIntakeServo = hardwareMap.get(CRServo.class, "left_intake_servo");
        rightIntakeServo = hardwareMap.get(CRServo.class, "right_intake_servo");

        /*
         * To drive forward, most robots need the motor on one side to be reversed,
         * because the axles point in opposite directions. Pushing the left stick forward
         * MUST make robot go forward. So adjust these two lines based on your first test drive.
         * Note: The settings here assume direct drive on left and right wheels. Gear
         * Reduction or 90 Deg drives may require direction flips
         */
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);

        // The intake motor is wired opposite its servos, so flip the motor only.
        // Positive intake power (right trigger) pulls game elements in.
        intake.setDirection(DcMotor.Direction.REVERSE);

        /*
         * Setting zeroPowerBehavior to BRAKE enables a "brake mode". This causes the motor to
         * slow down much faster when it is coasting. This creates a much more controllable
         * drivetrain. As the robot stops much quicker.
         */
        leftFrontDrive.setZeroPowerBehavior(BRAKE);
        rightFrontDrive.setZeroPowerBehavior(BRAKE);
        leftBackDrive.setZeroPowerBehavior(BRAKE);
        rightBackDrive.setZeroPowerBehavior(BRAKE);
        intake.setZeroPowerBehavior(BRAKE);

        /*
         * Here we set our launcher to the RUN_USING_ENCODER runmode.
         * If you notice that you have no control over the velocity of the motor, it just jumps
         * right to a number much higher than your set point, make sure that your encoders are plugged
         * into the port right beside the motor itself. And that the motors polarity is consistent
         * through any wiring.
         */

        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        launcher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(40, 0, 0, 12.5));

        /*
         * set Feeders to an initial value to initialize the servo controller
         */
        leftIntakeServo.setPower(0);
        rightIntakeServo.setPower(0);
        windmillServo.setPower(0);

        // Align windmill servo to 180 degrees at initialization (voltage compensated)
        long initRotationMs = getAdjustedRotationMs(WINDMILL_180_DEG_MS);
        windmillServo.setPower(WINDMILL_POWER);
        try {
            Thread.sleep(initRotationMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        windmillServo.setPower(0);

        // The right intake servo reverse is off for now, so both intake servos run the same way.
        // rightIntakeServo.setDirection(DcMotorSimple.Direction.REVERSE);
        // windmillServo.setDirection(DcMotorSimple.Direction.REVERSE);

        /*
         * Tell the driver that initialization is complete.
         */
        telemetry.addData("Status", "Initialized");
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit START
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits START
     */
    @Override
    public void start() {
        // seed the toggle edge detection with the buttons' current state, so a dpad button
        // already held at START doesn't count as a fresh press
        dpadUpWasPressed = gamepad1.dpad_up;
        dpadDownWasPressed = gamepad1.dpad_down;
    }

    /*
     * Code to run REPEATEDLY after the driver hits START but before they hit STOP
     */
    //TODO: reduce drive speed
    //TODO: map b to speed boost
    @Override
    public void loop() {
        /*
         * Here we call a function called mecanumDrive. The mecanumDrive function takes the input from
         * the joysticks, and applies power to the drive motors to move the robot as requested
         * by the driver. Moving the left joystick forwards/back moves all motors forwards/back,
         * moving the right joystick left/right rotates the robot clockwise or counterclockwise,
         * and moving the left joystick left moves the motors in the right way to create a sideways
         * "strafe" movement. Combinations of these inputs can be used to create more complex maneuvers.
         * Note, moving the joystick forward on most gamepads results in a negative signal, so
         * we invert it before passing it to the function.
         */
        // dpad up toggles always-inverted drive, dpad down toggles invert-while-intake.
        // one mode can't be turned on while the other is on. only a fresh press counts,
        // holding the button doesn't keep toggling.
        boolean upPressed = gamepad1.dpad_up && !dpadUpWasPressed;
        boolean downPressed = gamepad1.dpad_down && !dpadDownWasPressed;
        dpadUpWasPressed = gamepad1.dpad_up;
        dpadDownWasPressed = gamepad1.dpad_down;
        if (upPressed && !downPressed) {
            if (driveMode == 1) setDriveMode(0);      // own button turns it off
            else if (driveMode == 0) setDriveMode(1); // only turns on from normal
        } else if (downPressed && !upPressed) {
            if (driveMode == 2) setDriveMode(0);      // own button turns it off
            else if (driveMode == 0) setDriveMode(2); // only turns on from normal
        }

        double forward = -gamepad1.left_stick_y;
        double strafe = -gamepad1.left_stick_x;
        double rotate = gamepad1.right_stick_x;

        // the intake counts as active when the triggers actually command it (both pulled cancels out)
        boolean intakeActive = Math.abs(gamepad1.right_trigger - gamepad1.left_trigger) > 0.05;
        boolean driveFlipped = (driveMode == 1) || (driveMode == 2 && intakeActive);
        if (driveFlipped) {
            forward = -forward;
            strafe = -strafe;
        }

        mecanumDrive(forward, strafe, rotate);

        /*
         * Set the intake power variable to equal the right trigger, minus the left trigger.
         * Each trigger outputs a signal from 0-1, with 0 as fully released, and 1 fully depressed.
         * This gives us proportional control of the intake speed. The speed increases as we pull
         * the right trigger further. It's occasionally helpful to be able to reverse the intake,
         * so we also factor in the left trigger. If the left trigger is fully depressed,
         * the intakePower variable will be -1. If the right trigger is fully depressed, the variable
         * will be 1. If the driver pulls both triggers, the intake will remain off.
         * We use this technique (creating a variable, and setting it to our control inputs) to
         * allow us to avoid setting the same motors/servos power more than once per loop. That can
         * create erratic behavior.
         */
        intakePower = gamepad1.left_trigger - gamepad1.right_trigger;

        launch();

        /*
         * Here we set our intake motor and servos to their intake power. The order of operations
         * here is important though. The gamepad triggers define the starting point for the intake
         * power variable in each loop of our code, but inside our launch function we also sometimes
         * change the intake power. So we need to give our launch function a chance to modify the
         * variable before we write it to our motor and servos.
         */

        intake.setPower(intakePower);
        leftIntakeServo.setPower(intakePower);
        rightIntakeServo.setPower(intakePower);

        /*
         * Plain-language status for drivers and debugging.
         */
        double launcherVel = launcher.getVelocity();
        boolean launcherReady = launcherVel > LAUNCHER_MIN_VELOCITY;
        String intakeState = intakePower > 0.05 ? "intaking"
                : (intakePower < -0.05 ? "reversing" : "off");
        String intakeServoDir = intakePower > 0.05 ? "fwd"
                : (intakePower < -0.05 ? "rev" : "off");
        telemetry.addData("Launcher", "%s (goal %d, speed %.0f)",
                launcherReady ? "READY" : "spinning up",
                LAUNCHER_TARGET_VELOCITY, launcherVel);
        // Numeric copies so the dashboard Graph view can plot them
        telemetry.addData("launcherVel", launcherVel);
        telemetry.addData("launcherTarget", LAUNCHER_TARGET_VELOCITY);
        telemetry.addData("Intake", "%s, feeder %s",
                intakeState, windmillIsRotating ? "ON" : "off");
        // CRServos can't report position, only commanded power — shown as direction / state
        telemetry.addData("Servos", "feeder %s, intakes %s",
                windmillIsRotating ? "rotating 180°" : (windmillIsPausing ? "pausing" : "off"), intakeServoDir);
        telemetry.addData("Drive", "LF %.0f%% RF %.0f%% LB %.0f%% RB %.0f%% @ %.0f%%",
                leftFrontPower * 100, rightFrontPower * 100,
                leftBackPower * 100, rightBackPower * 100,
                DRIVE_POWER_SCALE * 100);
        telemetry.addData("Drive modes", "inverted %s, intake-invert %s%s",
                driveMode == 1 ? "ON" : "off",
                driveMode == 2 ? "ON" : "off",
                driveFlipped ? " (flipped now)" : "");
        telemetry.addData("batteryV", voltageSensor.getVoltage());
        telemetry.update();
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }

    // switch the drive mode and remember it for next time
    void setDriveMode(int mode) {
        driveMode = mode;
        prefs.edit().putInt("driveMode", mode).apply();
    }

    /*
     * Runs every time the robot controller app starts, before any opmode. The dashboard
     * forgets its tuning when the app restarts, so we bring back the saved values here and
     * hook up each dashboard config item ourselves. That way every edit on the dashboard
     * saves right away, even when no opmode is running.
     */
    @OnCreate
    public static void onRobotCreate(Context context) {
        final SharedPreferences p = context.getSharedPreferences("biobuzz", Context.MODE_PRIVATE);
        restoreTuning(p);
        try {
            FtcDashboard dash = FtcDashboard.getInstance();
            String cat = BioBuzzStarterbotTeleopMecanum.class.getSimpleName();
            dash.addConfigVariable(cat, "DRIVE_POWER_SCALE", new ValueProvider<Double>() {
                @Override public Double get() { return DRIVE_POWER_SCALE; }
                @Override public void set(Double value) { DRIVE_POWER_SCALE = value; saveTuning(p); }
            });
            dash.addConfigVariable(cat, "DRIVE_DEADZONE", new ValueProvider<Double>() {
                @Override public Double get() { return DRIVE_DEADZONE; }
                @Override public void set(Double value) { DRIVE_DEADZONE = value; saveTuning(p); }
            });
            dash.addConfigVariable(cat, "LAUNCHER_TARGET_VELOCITY", new ValueProvider<Integer>() {
                @Override public Integer get() { return LAUNCHER_TARGET_VELOCITY; }
                @Override public void set(Integer value) { LAUNCHER_TARGET_VELOCITY = value; saveTuning(p); }
            });
            dash.addConfigVariable(cat, "WINDMILL_POWER", new ValueProvider<Double>() {
                @Override public Double get() { return WINDMILL_POWER; }
                @Override public void set(Double value) { WINDMILL_POWER = value; saveTuning(p); }
            });
            dash.addConfigVariable(cat, "WINDMILL_180_DEG_MS", new ValueProvider<Long>() {
                @Override public Long get() { return WINDMILL_180_DEG_MS; }
                @Override public void set(Long value) { WINDMILL_180_DEG_MS = value; saveTuning(p); }
            });
            dash.addConfigVariable(cat, "NOMINAL_VOLTAGE", new ValueProvider<Double>() {
                @Override public Double get() { return NOMINAL_VOLTAGE; }
                @Override public void set(Double value) { NOMINAL_VOLTAGE = inRangeOrDefault(value, 1, 24, DEF_NOMINAL_VOLTAGE); saveTuning(p); }
            });
            dash.addConfigVariable(cat, "WINDMILL_PAUSE_MS", new ValueProvider<Long>() {
                @Override public Long get() { return WINDMILL_PAUSE_MS; }
                @Override public void set(Long value) { WINDMILL_PAUSE_MS = value; saveTuning(p); }
            });
        } catch (Exception e) {
            // honest failure mode: if this throws, the tuning items DON'T show up on the
            // dashboard at all this run. The robot still drives with the restored/code
            // values. Log it loud so it's diagnosable from the RC log.
            android.util.Log.e("BioBuzz", "dashboard tuning hookup failed - tuning items will not appear", e);
        }
    }

    /*
     * Bring back the saved dashboard tuning. Anything missing or out of range keeps the
     * code default.
     */
    static void restoreTuning(SharedPreferences p) {
        try {
            DRIVE_POWER_SCALE = restoreDouble(p, "DRIVE_POWER_SCALE", DEF_DRIVE_POWER_SCALE, 0, 1);
            DRIVE_DEADZONE = restoreDouble(p, "DRIVE_DEADZONE", DEF_DRIVE_DEADZONE, 0, 0.9);
            LAUNCHER_TARGET_VELOCITY = restoreInt(p, "LAUNCHER_TARGET_VELOCITY", DEF_LAUNCHER_TARGET_VELOCITY, 1, 6000);
            WINDMILL_POWER = restoreDouble(p, "WINDMILL_POWER", DEF_WINDMILL_POWER, -1, 1);
            WINDMILL_180_DEG_MS = restoreLong(p, "WINDMILL_180_DEG_MS", DEF_WINDMILL_180_DEG_MS, 0, 10000);
            NOMINAL_VOLTAGE = restoreDouble(p, "NOMINAL_VOLTAGE", DEF_NOMINAL_VOLTAGE, 1, 24);
            WINDMILL_PAUSE_MS = restoreLong(p, "WINDMILL_PAUSE_MS", DEF_WINDMILL_PAUSE_MS, 0, 10000);
        } catch (ClassCastException e) {
            // storage was messed with by hand - the code defaults are already in place
        }
    }

    // same bounds as restore: outside the range or not a real number, fall back to the code default
    static double inRangeOrDefault(double v, double min, double max, double def) {
        if (Double.isNaN(v) || Double.isInfinite(v) || v < min || v > max) return def;
        return v;
    }

    static double restoreDouble(SharedPreferences p, String key, double def, double min, double max) {
        double saved = Double.longBitsToDouble(p.getLong(key, Double.doubleToRawLongBits(def)));
        if (Double.isNaN(saved) || Double.isInfinite(saved) || saved < min || saved > max) return def;
        return saved;
    }

    static int restoreInt(SharedPreferences p, String key, int def, int min, int max) {
        int saved = p.getInt(key, def);
        return (saved < min || saved > max) ? def : saved;
    }

    static long restoreLong(SharedPreferences p, String key, long def, long min, long max) {
        long saved = p.getLong(key, def);
        return (saved < min || saved > max) ? def : saved;
    }

    static void saveTuning(SharedPreferences p) {
        p.edit()
                .putLong("DRIVE_POWER_SCALE", Double.doubleToRawLongBits(DRIVE_POWER_SCALE))
                .putLong("DRIVE_DEADZONE", Double.doubleToRawLongBits(DRIVE_DEADZONE))
                .putInt("LAUNCHER_TARGET_VELOCITY", LAUNCHER_TARGET_VELOCITY)
                .putLong("WINDMILL_POWER", Double.doubleToRawLongBits(WINDMILL_POWER))
                .putLong("WINDMILL_180_DEG_MS", WINDMILL_180_DEG_MS)
                .putLong("NOMINAL_VOLTAGE", Double.doubleToRawLongBits(NOMINAL_VOLTAGE))
                .putLong("WINDMILL_PAUSE_MS", WINDMILL_PAUSE_MS)
                .apply();
    }

    void mecanumDrive(double forward, double strafe, double rotate) {
        // keep the dashboard value sane: junk like NaN falls back to the default 0.1,
        // otherwise clamp it so a typo can't make the robot undrivable
        double dz = DRIVE_DEADZONE;
        if (Double.isNaN(dz) || Double.isInfinite(dz)) {
            dz = 0.1;
        }
        dz = Math.max(0, Math.min(dz, 0.9));
        if (Math.abs(forward) < dz) {
            forward = 0;
        }
        if (Math.abs(strafe) < dz) {
            strafe = 0;
        }
        if (Math.abs(rotate) < dz) {
            rotate = 0;
        }
        leftFrontPower = forward + strafe + rotate;
        rightFrontPower = forward + strafe - rotate;
        leftBackPower = forward - strafe + rotate;
        rightBackPower = forward - strafe - rotate;

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

    void launch() {
        // The launcher runs at target speed the whole teleop, bumper or not.
        launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
        /*
         * Moves the windmill continuous servo in 180-degree increments continuously while held.
         * Dynamically adjusts turn duration based on battery voltage to maintain consistent 180° rotation.
         * Whenever triggered, it completes a full 180° rotation before stopping or pausing.
         */
        if (windmillIsRotating) {
            long targetMs = getAdjustedRotationMs(WINDMILL_180_DEG_MS);
            if (System.currentTimeMillis() - windmillStartTime >= targetMs) {
                windmillServo.setPower(0);
                windmillIsRotating = false;
                if (WINDMILL_PAUSE_MS > 0) {
                    windmillIsPausing = true;
                    windmillStartTime = System.currentTimeMillis();
                }
            }
        } else if (windmillIsPausing) {
            if (System.currentTimeMillis() - windmillStartTime >= WINDMILL_PAUSE_MS) {
                windmillIsPausing = false;
            }
        } else {
            // right bumper feeds forward, left bumper reverses. the launcher speed check is off for now.
            if (gamepad1.right_bumper) { // && launcher.getVelocity() > LAUNCHER_MIN_VELOCITY) {
                windmillIsRotating = true;
                windmillStartTime = System.currentTimeMillis();
                windmillServo.setPower(WINDMILL_POWER);
            } else if (gamepad1.left_bumper){
                windmillIsRotating = true;
                windmillStartTime = System.currentTimeMillis();
                windmillServo.setPower(-WINDMILL_POWER);
            } else {
                windmillServo.setPower(0);
            }
        }
    }

    /**
     * Dynamically adjusts continuous servo rotation time based on current battery voltage.
     * Higher battery voltage -> servo spins faster -> shorter time needed.
     * Lower battery voltage -> servo spins slower -> longer time needed.
     */
    private long getAdjustedRotationMs(long baseMs) {
        if (voltageSensor == null) {
            return baseMs;
        }
        double currentVoltage = voltageSensor.getVoltage();
        if (currentVoltage < 8.0) {
            return baseMs;
        }
        return Math.round(baseMs * (NOMINAL_VOLTAGE / currentVoltage));
    }
}
