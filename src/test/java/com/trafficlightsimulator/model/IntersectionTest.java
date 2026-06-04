package com.trafficlightsimulator.model;

import org.junit.jupiter.api.Test;

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

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> intersection.addRoad(extraRoad));

        assertTrue(exception.getMessage().contains("Cannot add more roads"));
        assertEquals(2, intersection.getRoads().size());
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

        assertThrows(IllegalStateException.class,
                () -> eastLightGroup.setLightColor(eastLight, Color.GREEN));
        assertEquals(Color.RED, eastLight.getColor());
    }

    private TrafficLight trafficLight(Direction direction, Color color, State state) {
        return new TrafficLight(color, state, TrafficLight.Type.TRAFFIC, direction, true);
    }
}
