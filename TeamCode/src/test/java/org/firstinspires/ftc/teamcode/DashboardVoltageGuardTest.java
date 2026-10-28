package org.firstinspires.ftc.teamcode;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

// Live dashboard voltage edits must land with the same semantics restoreDouble
// uses when loading saved settings: in range -> the value, anything else -> default.
// Exercises the REAL static method on the REAL class.
public class DashboardVoltageGuardTest {

    @Test
    public void outOfRangeAndNonFiniteFallBackToDefault() {
        assertEquals(12.5, BioBuzzStarterbotTeleopMecanum.inRangeOrDefault(Double.NaN, 1, 24, 12.5), 0.0);
        assertEquals(12.5, BioBuzzStarterbotTeleopMecanum.inRangeOrDefault(Double.POSITIVE_INFINITY, 1, 24, 12.5), 0.0);
        assertEquals(12.5, BioBuzzStarterbotTeleopMecanum.inRangeOrDefault(Double.NEGATIVE_INFINITY, 1, 24, 12.5), 0.0);
        assertEquals(12.5, BioBuzzStarterbotTeleopMecanum.inRangeOrDefault(0, 1, 24, 12.5), 0.0);
        assertEquals(12.5, BioBuzzStarterbotTeleopMecanum.inRangeOrDefault(-3, 1, 24, 12.5), 0.0);
        assertEquals(12.5, BioBuzzStarterbotTeleopMecanum.inRangeOrDefault(30, 1, 24, 12.5), 0.0);
        assertEquals(12.5, BioBuzzStarterbotTeleopMecanum.inRangeOrDefault(12500, 1, 24, 12.5), 0.0);
    }

    @Test
    public void inRangePassesThroughUnchanged() {
        assertEquals(1, BioBuzzStarterbotTeleopMecanum.inRangeOrDefault(1, 1, 24, 12.5), 0.0);
        assertEquals(24, BioBuzzStarterbotTeleopMecanum.inRangeOrDefault(24, 1, 24, 12.5), 0.0);
        assertEquals(12.5, BioBuzzStarterbotTeleopMecanum.inRangeOrDefault(12.5, 1, 24, 12.5), 0.0);
        assertEquals(13.97, BioBuzzStarterbotTeleopMecanum.inRangeOrDefault(13.97, 1, 24, 12.5), 0.0);
    }
}
