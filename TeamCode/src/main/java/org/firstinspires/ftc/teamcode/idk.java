package org.firstinspires.ftc.teamcode;

import com.bylazar.configurables.PanelsConfigurables;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
@Configurable
public class idk extends OpMode {

    private Servo flowerIntake;
    private TelemetryManager panelsTelemetry;
    public static double Position = 0.0;
    @Override
    public void init() {
        PanelsConfigurables.INSTANCE.refreshClass(this);
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();
        flowerIntake = hardwareMap.get(Servo.class, "flower_intake");
        flowerIntake.setDirection(Servo.Direction.FORWARD);
    }

    @Override
    public void loop() {

        flowerIntake.setPosition(Position);
        panelsTelemetry.addData("Flower Intake Position", flowerIntake.getPosition());
        panelsTelemetry.update(telemetry);

    }
}
