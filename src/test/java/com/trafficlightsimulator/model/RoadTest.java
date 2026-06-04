package com.trafficlightsimulator.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoadTest {

    @Test
    void constructor_acceptsMinimumAngle() {
        Road road = new Road(0.0, 1, 1);

        assertEquals(0.0, road.getAngle());
    }

    @Test
    void constructor_acceptsAngleBelowFullCircle() {
        Road road = new Road(359.999, 1, 1);

        assertEquals(359.999, road.getAngle());
    }

    @Test
    void constructor_rejectsNegativeAngle() {
        assertThrows(IllegalArgumentException.class, () -> new Road(-0.1, 1, 1));
    }

    @Test
    void constructor_rejectsFullCircleAngle() {
        assertThrows(IllegalArgumentException.class, () -> new Road(360.0, 1, 1));
    }

    @Test
    void setAngle_acceptsValidUpdatedAngle() {
        Road road = new Road(90.0, 1, 1);

        road.setAngle(180.0);

        assertEquals(180.0, road.getAngle());
    }

    @Test
    void setAngle_rejectsOutOfRangeAngleAndKeepsExistingValue() {
        Road road = new Road(90.0, 1, 1);

        assertThrows(IllegalArgumentException.class, () -> road.setAngle(360.0));

        assertEquals(90.0, road.getAngle());
    }

    @Test
    void setAngle_acceptsRepresentativeCardinalAngles() {
        Road road = new Road(0.0, 1, 1);

        assertDoesNotThrow(() -> {
            road.setAngle(90.0);
            road.setAngle(180.0);
            road.setAngle(270.0);
        });

        assertEquals(270.0, road.getAngle());
    }
}
