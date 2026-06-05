package com.trafficlightsimulator.model;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        List<Lane> incomingLanes = road.getIncomingLanes();
        Lane originalLane = incomingLanes.get(0);
        Lane anotherIncoming = new Lane(Lane.Direction.INCOMING);

        assertThrows(UnsupportedOperationException.class, () -> incomingLanes.add(anotherIncoming));

        assertEquals(1, road.getIncomingLanes().size());
        assertSame(originalLane, road.getIncomingLanes().get(0));
    }

    @Test
    void getOutgoingLanes_returnsUnmodifiableView() {
        Road road = new Road(0.0, 1, 1);
        List<Lane> outgoingLanes = road.getOutgoingLanes();
        Lane originalLane = outgoingLanes.get(0);

        assertThrows(UnsupportedOperationException.class, outgoingLanes::clear);

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

    @Test
    void addPedestrianCrossing_associatesCrossingWithRoad() {
        Road road = new Road(0.0, 1, 1);
        PedestrianCrossing crossing = new PedestrianCrossing();

        road.addPedestrianCrossing(crossing);

        assertTrue(road.hasPedestrianCrossing());
        assertSame(crossing, road.getPedestrianCrossing());
    }

    @Test
    void addPedestrianCrossing_rejectsNull() {
        Road road = new Road(0.0, 1, 1);

        assertThrows(IllegalArgumentException.class, () -> road.addPedestrianCrossing(null));
    }

    @Test
    void addPedestrianCrossing_rejectsSecondCrossing() {
        Road road = new Road(0.0, 1, 1);
        road.addPedestrianCrossing(new PedestrianCrossing());
        PedestrianCrossing anotherCrossing = new PedestrianCrossing();

        assertThrows(IllegalStateException.class, () -> road.addPedestrianCrossing(anotherCrossing));
    }

    @Test
    void removePedestrianCrossing_disassociatesCrossingFromRoad() {
        Road road = new Road(0.0, 1, 1);
        road.addPedestrianCrossing(new PedestrianCrossing());

        road.removePedestrianCrossing();

        assertFalse(road.hasPedestrianCrossing());
        assertNull(road.getPedestrianCrossing());
    }

    @Test
    void removePedestrianCrossing_throwsWhenNoCrossingPresent() {
        Road road = new Road(0.0, 1, 1);

        assertThrows(IllegalStateException.class, road::removePedestrianCrossing);
    }

    @Test
    void addThenRemoveThenAddCrossing_succeeds() {
        Road road = new Road(0.0, 1, 1);
        PedestrianCrossing first = new PedestrianCrossing();
        PedestrianCrossing second = new PedestrianCrossing();

        road.addPedestrianCrossing(first);
        road.removePedestrianCrossing();
        road.addPedestrianCrossing(second);

        assertSame(second, road.getPedestrianCrossing());
    }


    @Test
    void addAllowedTurnByLane_configuresInboundToOutboundRestriction() {
        Road road = new Road(0.0, 2, 2);
        Lane inbound = road.getIncomingLanes().get(0);
        Lane outbound = road.getOutgoingLanes().get(1);

        road.addAllowedTurn(inbound, outbound);

        assertTrue(road.isTurnAllowed(inbound, outbound));
        assertSame(outbound, road.getAllowedTurns(inbound).get(0));
    }

    @Test
    void addAllowedTurnByIndex_configuresInboundToSameRoadOutboundRestriction() {
        Road road = new Road(0.0, 2, 2);

        road.addAllowedTurn(1, 0);

        assertTrue(road.isTurnAllowed(road.getIncomingLanes().get(1), road.getOutgoingLanes().get(0)));
    }

    @Test
    void setAllowedTurns_replacesPermittedOutboundLanesForInboundLane() {
        Road road = new Road(0.0, 1, 2);
        Lane inbound = road.getIncomingLanes().get(0);
        Lane removedOutbound = road.getOutgoingLanes().get(0);
        Lane allowedOutbound = road.getOutgoingLanes().get(1);
        road.addAllowedTurn(inbound, removedOutbound);

        road.setAllowedTurns(inbound, List.of(allowedOutbound));

        assertFalse(road.isTurnAllowed(inbound, removedOutbound));
        assertTrue(road.isTurnAllowed(inbound, allowedOutbound));
    }

    @Test
    void validateTurnAllowed_allowsPermittedTurn() {
        Road road = new Road(0.0, 1, 1);
        Lane inbound = road.getIncomingLanes().get(0);
        Lane outbound = road.getOutgoingLanes().get(0);
        road.addAllowedTurn(inbound, outbound);

        assertDoesNotThrow(() -> road.validateTurnAllowed(inbound, outbound));
    }

    @Test
    void validateTurnAllowed_preventsIllegalTurnWithClearFeedback() {
        Road road = new Road(0.0, 1, 2);
        Lane inbound = road.getIncomingLanes().get(0);
        Lane outbound = road.getOutgoingLanes().get(1);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> road.validateTurnAllowed(inbound, outbound));

        assertTrue(exception.getMessage().contains("Illegal turn"));
    }

    @Test
    void addAllowedTurn_rejectsInboundLaneFromAnotherRoad() {
        Road road = new Road(0.0, 1, 1);
        Road otherRoad = new Road(90.0, 1, 1);
        Lane foreignInboundLane = otherRoad.getIncomingLanes().get(0);
        Lane ownedOutboundLane = road.getOutgoingLanes().get(0);

        assertThrows(IllegalArgumentException.class,
                () -> road.addAllowedTurn(foreignInboundLane, ownedOutboundLane));
    }

    @Test
    void addAllowedTurn_rejectsOutboundLaneFromAnotherRoad() {
        Road road = new Road(0.0, 1, 1);
        Road otherRoad = new Road(90.0, 1, 1);
        Lane ownedInboundLane = road.getIncomingLanes().get(0);
        Lane foreignOutboundLane = otherRoad.getOutgoingLanes().get(0);

        assertThrows(IllegalArgumentException.class,
                () -> road.addAllowedTurn(ownedInboundLane, foreignOutboundLane));
    }

    @Test
    void addAllowedTurn_rejectsNonOutboundTarget() {
        Road road = new Road(0.0, 2, 1);
        Lane ownedInboundLane = road.getIncomingLanes().get(0);
        Lane nonOutboundLane = road.getIncomingLanes().get(1);

        assertThrows(IllegalArgumentException.class,
                () -> road.addAllowedTurn(ownedInboundLane, nonOutboundLane));
    }

    @Test
    void addAllowedTurnByIndex_rejectsOutOfRangeIndexes() {
        Road road = new Road(0.0, 1, 1);

        assertThrows(IndexOutOfBoundsException.class, () -> road.addAllowedTurn(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> road.addAllowedTurn(0, 1));
    }


    @Test
    void equality_usesIdentityAndToStringIsReadable() {
        Road road = new Road(90.0, 1, 2);
        Road sameValueRoad = new Road(90.0, 1, 2);

        assertEquals(road, road);
        assertNotEquals(road, sameValueRoad);
        assertEquals(System.identityHashCode(road), road.hashCode());
        assertTrue(road.toString().contains("angle=90.0"));
        assertTrue(road.toString().contains("incomingLaneCount=1"));
        assertFalse(road.toString().contains("@"));
    }

}
