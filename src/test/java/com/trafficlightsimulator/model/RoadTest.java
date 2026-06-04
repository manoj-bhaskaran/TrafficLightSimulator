package com.trafficlightsimulator.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
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

    @Test
    void constructor_rejectsZeroIncomingLaneCount() {
        assertThrows(IllegalArgumentException.class, () -> new Road(0.0, 0, 1));
    }

    @Test
    void constructor_rejectsNegativeIncomingLaneCount() {
        assertThrows(IllegalArgumentException.class, () -> new Road(0.0, -1, 1));
    }

    @Test
    void constructor_rejectsZeroOutgoingLaneCount() {
        assertThrows(IllegalArgumentException.class, () -> new Road(0.0, 1, 0));
    }

    @Test
    void constructor_rejectsNegativeOutgoingLaneCount() {
        assertThrows(IllegalArgumentException.class, () -> new Road(0.0, 1, -1));
    }

    @Test
    void getIncomingLanes_returnsUnmodifiableView() {
        Road road = new Road(0.0, 1, 1);
        Lane originalLane = road.getIncomingLanes().get(0);

        assertThrows(UnsupportedOperationException.class,
                () -> road.getIncomingLanes().add(new Lane(Lane.Direction.INCOMING)));

        assertEquals(1, road.getIncomingLanes().size());
        assertSame(originalLane, road.getIncomingLanes().get(0));
    }

    @Test
    void getOutgoingLanes_returnsUnmodifiableView() {
        Road road = new Road(0.0, 1, 1);
        Lane originalLane = road.getOutgoingLanes().get(0);

        assertThrows(UnsupportedOperationException.class,
                () -> road.getOutgoingLanes().clear());

        assertEquals(1, road.getOutgoingLanes().size());
        assertSame(originalLane, road.getOutgoingLanes().get(0));
    }

    @Test
    void setNumIncomingLanes_growthPreservesExistingLaneObjects() {
        Road road = new Road(0.0, 2, 1);
        Lane originalFirst = road.getIncomingLanes().get(0);
        Lane originalSecond = road.getIncomingLanes().get(1);

        road.setNumIncomingLanes(4);

        assertSame(originalFirst, road.getIncomingLanes().get(0));
        assertSame(originalSecond, road.getIncomingLanes().get(1));
        assertEquals(4, road.getIncomingLanes().size());
    }

    @Test
    void setNumIncomingLanes_shrinkRemovesFromTailAndPreservesHead() {
        Road road = new Road(0.0, 3, 1);
        Lane originalFirst = road.getIncomingLanes().get(0);

        road.setNumIncomingLanes(1);

        assertEquals(1, road.getIncomingLanes().size());
        assertSame(originalFirst, road.getIncomingLanes().get(0));
    }

    @Test
    void setNumOutgoingLanes_growthPreservesExistingLaneObjects() {
        Road road = new Road(0.0, 1, 2);
        Lane originalFirst = road.getOutgoingLanes().get(0);
        Lane originalSecond = road.getOutgoingLanes().get(1);

        road.setNumOutgoingLanes(4);

        assertSame(originalFirst, road.getOutgoingLanes().get(0));
        assertSame(originalSecond, road.getOutgoingLanes().get(1));
        assertEquals(4, road.getOutgoingLanes().size());
    }

    @Test
    void setNumOutgoingLanes_shrinkRemovesFromTailAndPreservesHead() {
        Road road = new Road(0.0, 1, 3);
        Lane originalFirst = road.getOutgoingLanes().get(0);

        road.setNumOutgoingLanes(1);

        assertEquals(1, road.getOutgoingLanes().size());
        assertSame(originalFirst, road.getOutgoingLanes().get(0));
    }
}
