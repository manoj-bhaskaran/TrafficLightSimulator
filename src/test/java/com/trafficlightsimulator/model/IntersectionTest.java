package com.trafficlightsimulator.model;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

class IntersectionTest {

    @Test
    void constructor_acceptsMinimumAndMaximumRoadCounts() {
        assertEquals(0, new Intersection(2).getRoads().size());
        assertEquals(0, new Intersection(8).getRoads().size());
    }

    @Test
    void constructor_rejectsRoadCountBelowMinimum() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Intersection(1));

        assertTrue(exception.getMessage().contains("between 2 and 8"));
    }

    @Test
    void constructor_rejectsRoadCountAboveMaximum() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Intersection(9));

        assertTrue(exception.getMessage().contains("between 2 and 8"));
    }

    @Test
    void addRoad_rejectsNullRoad() {
        Intersection intersection = new Intersection(2);

        assertThrows(IllegalArgumentException.class, () -> intersection.addRoad(null));
    }

    @Test
    void addRoad_rejectsRoadsBeyondConfiguredCapacity() {
        Intersection intersection = new Intersection(2);
        intersection.addRoad(new Road(0.0, 1, 1));
        intersection.addRoad(new Road(90.0, 1, 1));
        Road extraRoad = new Road(180.0, 1, 1);

        Executable addExtraRoad = () -> intersection.addRoad(extraRoad);

        IllegalStateException exception = assertThrows(IllegalStateException.class, addExtraRoad);

        assertTrue(exception.getMessage().contains("Cannot add more roads"));
        assertEquals(2, intersection.getRoads().size());
    }

    @Test
    void addRoad_rejectsRoadAlreadyConnectedToIntersection() {
        Intersection intersection = new Intersection(3);
        Road road = new Road(0.0, 1, 1);
        intersection.addRoad(road);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> intersection.addRoad(road));

        assertTrue(exception.getMessage().contains("already connected"));
        assertEquals(1, intersection.getRoads().size());
    }

    @Test
    void addRoad_acceptsRoadAtMinimumAngleFromExistingRoad() {
        Intersection intersection = new Intersection(2);
        intersection.addRoad(new Road(0.0, 1, 1));
        Road roadAtMinimumAngle = new Road(Intersection.MIN_ANGLE_BETWEEN_ROADS, 1, 1);

        assertDoesNotThrow(() -> intersection.addRoad(roadAtMinimumAngle));

        assertEquals(2, intersection.getRoads().size());
        assertSame(roadAtMinimumAngle, intersection.getRoads().get(1));
    }

    @Test
    void addRoad_rejectsRoadBelowMinimumAngleFromExistingRoad() {
        Intersection intersection = new Intersection(2);
        Road existingRoad = new Road(90.0, 1, 1);
        intersection.addRoad(existingRoad);
        Road tooCloseRoad = new Road(90.0 + Intersection.MIN_ANGLE_BETWEEN_ROADS - 0.5, 1, 1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> intersection.addRoad(tooCloseRoad));

        assertTrue(exception.getMessage().contains("at least " + Intersection.MIN_ANGLE_BETWEEN_ROADS));
        assertEquals(1, intersection.getRoads().size());
        assertSame(existingRoad, intersection.getRoads().get(0));
    }

    @Test
    void addRoad_rejectsRoadBelowMinimumAngleAcrossZeroDegreeBoundary() {
        Intersection intersection = new Intersection(2);
        intersection.addRoad(new Road(350.0, 1, 1));
        Road tooCloseRoad = new Road(5.0, 1, 1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> intersection.addRoad(tooCloseRoad));

        assertTrue(exception.getMessage().contains("350.0"));
        assertEquals(1, intersection.getRoads().size());
    }

    @Test
    void addRoad_acceptsRoadAtMinimumAngleAcrossZeroDegreeBoundary() {
        Intersection intersection = new Intersection(2);
        intersection.addRoad(new Road(345.0, 1, 1));
        Road roadAtMinimumAngle = new Road(15.0, 1, 1);

        assertDoesNotThrow(() -> intersection.addRoad(roadAtMinimumAngle));

        assertEquals(2, intersection.getRoads().size());
    }

    @Test
    void roadSetAngleRejectsMutationThatWouldBreakIntersectionSpacing() {
        Intersection intersection = new Intersection(2);
        Road firstRoad = new Road(0.0, 1, 1);
        Road secondRoad = new Road(90.0, 1, 1);
        intersection.addRoad(firstRoad);
        intersection.addRoad(secondRoad);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> secondRoad.setAngle(5.0));

        assertTrue(exception.getMessage().contains("at least " + Intersection.MIN_ANGLE_BETWEEN_ROADS));
        assertEquals(90.0, secondRoad.getAngle());
        assertTrue(intersection.isIntersectionSetupComplete());
    }

    @Test
    void roadSetAngleAcceptsMutationThatPreservesIntersectionSpacing() {
        Intersection intersection = new Intersection(2);
        Road firstRoad = new Road(0.0, 1, 1);
        Road secondRoad = new Road(90.0, 1, 1);
        intersection.addRoad(firstRoad);
        intersection.addRoad(secondRoad);

        assertDoesNotThrow(() -> secondRoad.setAngle(180.0));

        assertEquals(180.0, secondRoad.getAngle());
    }

    @Test
    void getRoads_returnsUnmodifiableViewBackedByInternalRoads() {
        Intersection intersection = new Intersection(2);
        Road road = new Road(0.0, 1, 1);
        intersection.addRoad(road);

        List<Road> roads = intersection.getRoads();
        Road anotherRoad = new Road(90.0, 1, 1);

        assertThrows(UnsupportedOperationException.class, () -> roads.add(anotherRoad));

        assertEquals(1, intersection.getRoads().size());
        assertSame(road, intersection.getRoads().get(0));
    }

    @Test
    void setNumberOfRoads_rejectsValueBelowCurrentRoadCount() {
        Intersection intersection = new Intersection(4);
        intersection.addRoad(new Road(0.0, 1, 1));
        intersection.addRoad(new Road(90.0, 1, 1));
        intersection.addRoad(new Road(180.0, 1, 1));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> intersection.setNumberOfRoads(2));

        assertTrue(exception.getMessage().contains("already added"));
    }

    @Test
    void setNumberOfRoads_acceptsValueEqualToCurrentRoadCount() {
        Intersection intersection = new Intersection(4);
        intersection.addRoad(new Road(0.0, 1, 1));
        intersection.addRoad(new Road(90.0, 1, 1));
        intersection.addRoad(new Road(180.0, 1, 1));

        assertDoesNotThrow(() -> intersection.setNumberOfRoads(3));
    }

    @Test
    void configureIncompatibleTrafficLights_enforcesConflictsAcrossRoadGroups() {
        Intersection intersection = new Intersection(2);
        Road northRoad = new Road(0.0, 1, 1);
        Road eastRoad = new Road(90.0, 1, 1);
        TrafficLight northLight = trafficLight(Direction.STRAIGHT, Color.GREEN, State.ON);
        TrafficLight eastLight = trafficLight(Direction.LEFT, Color.RED, State.ON);
        northRoad.addTrafficLightToIncomingGroup(northLight);
        eastRoad.addTrafficLightToIncomingGroup(eastLight);
        intersection.addRoad(northRoad);
        intersection.addRoad(eastRoad);

        intersection.configureIncompatibleTrafficLights(northLight, eastLight);

        TrafficLightGroup eastLightGroup = eastRoad.getIncomingLanesTrafficLightGroup();

        Executable activateEastLight = () -> eastLightGroup.setLightColor(eastLight, Color.GREEN);

        assertThrows(IllegalStateException.class, activateEastLight);
        assertEquals(Color.RED, eastLight.getColor());
    }

    private TrafficLight trafficLight(Direction direction, Color color, State state) {
        return new TrafficLight(color, state, TrafficLight.Type.TRAFFIC, direction, true);
    }

    @Test
    void equality_usesIdentityAndToStringIsReadable() {
        Intersection intersection = new Intersection(2);
        Intersection sameValueIntersection = new Intersection(2);

        assertEquals(intersection, intersection);
        assertNotEquals(intersection, sameValueIntersection);
        assertEquals(System.identityHashCode(intersection), intersection.hashCode());
        assertTrue(intersection.toString().contains("configuredRoadCapacity=2"));
        assertTrue(intersection.toString().contains("roadCount=0"));
        assertFalse(intersection.toString().contains("@"));
    }

}
