package com.trafficlightsimulator.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LaneTest {

    @Test
    void constructor_initializesDirectionAndEmptyAllowedOutgoingLanes() {
        Lane lane = new Lane(Lane.Direction.INCOMING);

        assertEquals(Lane.Direction.INCOMING, lane.getDirection());
        assertTrue(lane.getAllowedOutgoingLanes().isEmpty());
    }

    @Test
    void constructor_rejectsNullDirection() {
        assertThrows(IllegalArgumentException.class, () -> new Lane(null));
    }

    @Test
    void addAllowedOutgoingLane_allowsIncomingToOutgoingLane() {
        Lane incoming = new Lane(Lane.Direction.INCOMING);
        Lane outgoing = new Lane(Lane.Direction.OUTGOING);

        incoming.addAllowedOutgoingLane(outgoing);

        assertEquals(1, incoming.getAllowedOutgoingLanes().size());
        assertSame(outgoing, incoming.getAllowedOutgoingLanes().get(0));
        assertTrue(incoming.isAllowedOutgoingLane(outgoing));
    }

    @Test
    void addAllowedOutgoingLane_rejectsIncomingToIncomingLane() {
        Lane incoming = new Lane(Lane.Direction.INCOMING);
        Lane otherIncoming = new Lane(Lane.Direction.INCOMING);

        assertThrows(IllegalArgumentException.class, () -> incoming.addAllowedOutgoingLane(otherIncoming));

        assertFalse(incoming.isAllowedOutgoingLane(otherIncoming));
    }

    @Test
    void addAllowedOutgoingLane_rejectsOutgoingLaneAsSource() {
        Lane outgoingSource = new Lane(Lane.Direction.OUTGOING);
        Lane outgoingTarget = new Lane(Lane.Direction.OUTGOING);

        assertThrows(IllegalArgumentException.class, () -> outgoingSource.addAllowedOutgoingLane(outgoingTarget));

        assertFalse(outgoingSource.isAllowedOutgoingLane(outgoingTarget));
    }

    @Test
    void addAllowedOutgoingLane_rejectsNullLane() {
        Lane incoming = new Lane(Lane.Direction.INCOMING);

        assertThrows(IllegalArgumentException.class, () -> incoming.addAllowedOutgoingLane(null));

        assertTrue(incoming.getAllowedOutgoingLanes().isEmpty());
    }

    @Test
    void isAllowedOutgoingLane_returnsFalseForUnregisteredAndNullLanes() {
        Lane incoming = new Lane(Lane.Direction.INCOMING);
        Lane outgoing = new Lane(Lane.Direction.OUTGOING);

        assertFalse(incoming.isAllowedOutgoingLane(outgoing));
        assertFalse(incoming.isAllowedOutgoingLane(null));
    }
}
