package org.firstinspires.ftc.teamcode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

// Drive math on the REAL mecanumDrive with the four motors as test doubles.
// Locks in what drivers rely on: full stick maps to full power, combined inputs
// normalize so nothing exceeds 100%, sub-deadzone input is zero, and the
// dashboard drive cap scales every wheel.
public class MecanumDriveMathTest {

    private BioBuzzStarterbotTeleopMecanum op;
    private final Map<String, Double> lastPower = new HashMap<>();

    private DcMotor recordingMotor(String name) {
        DcMotor m = mock(DcMotor.class);
        doAnswer(inv -> {
            lastPower.put(name, inv.getArgument(0));
            return null;
        }).when(m).setPower(anyDouble());
        return m;
    }

    private void install(String field, DcMotor motor) throws Exception {
        Field f = BioBuzzStarterbotTeleopMecanum.class.getDeclaredField(field);
        f.setAccessible(true);
        f.set(op, motor);
    }

    @Before
    public void setUp() throws Exception {
        op = new BioBuzzStarterbotTeleopMecanum();
        BioBuzzStarterbotTeleopMecanum.DRIVE_POWER_SCALE = 1.0;
        BioBuzzStarterbotTeleopMecanum.DRIVE_DEADZONE = 0.1;
        install("leftFrontDrive", recordingMotor("LF"));
        install("rightFrontDrive", recordingMotor("RF"));
        install("leftBackDrive", recordingMotor("LB"));
        install("rightBackDrive", recordingMotor("RB"));
    }

    private void assertPowers(double lf, double rf, double lb, double rb) {
        assertEquals("LF", lf, lastPower.get("LF"), 1e-9);
        assertEquals("RF", rf, lastPower.get("RF"), 1e-9);
        assertEquals("LB", lb, lastPower.get("LB"), 1e-9);
        assertEquals("RB", rb, lastPower.get("RB"), 1e-9);
    }

    @Test
    public void fullForwardIsFullPowerOnAllWheels() {
        op.mecanumDrive(1, 0, 0);
        assertPowers(1, 1, 1, 1);
    }

    @Test
    public void fullReverseIsFullNegativeOnAllWheels() {
        op.mecanumDrive(-1, 0, 0);
        assertPowers(-1, -1, -1, -1);
    }

    @Test
    public void pureRotateSpinsLeftAndRightSidesOppositely() {
        op.mecanumDrive(0, 0, 1);
        assertPowers(1, -1, 1, -1);
    }

    @Test
    public void combinedInputsNormalizeSoNoWheelExceedsFullPower() {
        op.mecanumDrive(1, 1, 1); // worst case sum
        for (Map.Entry<String, Double> e : lastPower.entrySet()) {
            assertTrue(e.getKey() + " within [-1,1], was " + e.getValue(),
                    Math.abs(e.getValue()) <= 1.0);
        }
        // raw LF=1, RF=-1, LB=3, RB=1; normalized by max=3, relative shape preserved
        assertPowers(1.0 / 3.0, -1.0 / 3.0, 1.0, 1.0 / 3.0);
    }

    @Test
    public void belowDeadzoneIsZero() {
        op.mecanumDrive(0.05, -0.05, 0.09);
        assertPowers(0, 0, 0, 0);
    }

    @Test
    public void drivePowerCapScalesEveryWheel() {
        BioBuzzStarterbotTeleopMecanum.DRIVE_POWER_SCALE = 0.5;
        op.mecanumDrive(1, 0, 0);
        assertPowers(0.5, 0.5, 0.5, 0.5);
    }
}
