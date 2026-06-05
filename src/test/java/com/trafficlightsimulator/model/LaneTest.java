package com.trafficlightsimulator.model;

import java.util.List;

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
    void getAllowedOutgoingLanes_returnsUnmodifiableView() {
        Lane incoming = new Lane(Lane.Direction.INCOMING);
        Lane outgoing = new Lane(Lane.Direction.OUTGOING);
        incoming.addAllowedOutgoingLane(outgoing);

        List<Lane> allowedOutgoingLanes = incoming.getAllowedOutgoingLanes();
        Lane anotherOutgoing = new Lane(Lane.Direction.OUTGOING);

        assertThrows(UnsupportedOperationException.class, () -> allowedOutgoingLanes.add(anotherOutgoing));

        assertEquals(1, incoming.getAllowedOutgoingLanes().size());
        assertSame(outgoing, incoming.getAllowedOutgoingLanes().get(0));
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


    @Test
    void addAllowedOutgoingLane_ignoresDuplicateLane() {
        Lane incoming = new Lane(Lane.Direction.INCOMING);
        Lane outgoing = new Lane(Lane.Direction.OUTGOING);

        incoming.addAllowedOutgoingLane(outgoing);
        incoming.addAllowedOutgoingLane(outgoing);

        assertEquals(1, incoming.getAllowedOutgoingLanes().size());
        assertSame(outgoing, incoming.getAllowedOutgoingLanes().get(0));
    }

    @Test
    void setAllowedOutgoingLanes_replacesExistingRestrictionsAndDeduplicates() {
        Lane incoming = new Lane(Lane.Direction.INCOMING);
        Lane removedOutgoing = new Lane(Lane.Direction.OUTGOING);
        Lane allowedOutgoing = new Lane(Lane.Direction.OUTGOING);
        incoming.addAllowedOutgoingLane(removedOutgoing);

        incoming.setAllowedOutgoingLanes(List.of(allowedOutgoing, allowedOutgoing));

        assertEquals(1, incoming.getAllowedOutgoingLanes().size());
        assertSame(allowedOutgoing, incoming.getAllowedOutgoingLanes().get(0));
        assertTrue(incoming.isAllowedOutgoingLane(allowedOutgoing));
        assertFalse(incoming.isAllowedOutgoingLane(removedOutgoing));
    }

    @Test
    void setAllowedOutgoingLanes_rejectsInvalidCollectionWithoutChangingExistingRestrictions() {
        Lane incoming = new Lane(Lane.Direction.INCOMING);
        Lane existingOutgoing = new Lane(Lane.Direction.OUTGOING);
        Lane invalidIncoming = new Lane(Lane.Direction.INCOMING);
        incoming.addAllowedOutgoingLane(existingOutgoing);

        assertThrows(IllegalArgumentException.class,
                () -> incoming.setAllowedOutgoingLanes(List.of(invalidIncoming)));

        assertEquals(1, incoming.getAllowedOutgoingLanes().size());
        assertSame(existingOutgoing, incoming.getAllowedOutgoingLanes().get(0));
    }

    @Test
    void setAllowedOutgoingLanes_rejectsNullCollection() {
        Lane incoming = new Lane(Lane.Direction.INCOMING);

        assertThrows(IllegalArgumentException.class, () -> incoming.setAllowedOutgoingLanes(null));
    }

    @Test
    void validateOutgoingLaneAllowed_allowsPermittedTurn() {
        Lane incoming = new Lane(Lane.Direction.INCOMING);
        Lane outgoing = new Lane(Lane.Direction.OUTGOING);
        incoming.addAllowedOutgoingLane(outgoing);

        assertDoesNotThrow(() -> incoming.validateOutgoingLaneAllowed(outgoing));
    }

    @Test
    void validateOutgoingLaneAllowed_preventsIllegalTurnWithClearFeedback() {
        Lane incoming = new Lane(Lane.Direction.INCOMING);
        Lane outgoing = new Lane(Lane.Direction.OUTGOING);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> incoming.validateOutgoingLaneAllowed(outgoing));

        assertTrue(exception.getMessage().contains("Illegal turn"));
    }

}
