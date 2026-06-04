package com.trafficlightsimulator.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntersectionTest {

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

        assertThrows(IllegalStateException.class,
                () -> eastRoad.getIncomingLanesTrafficLightGroup().setLightColor(eastLight, Color.GREEN));
        assertEquals(Color.RED, eastLight.getColor());
    }

    private TrafficLight trafficLight(Direction direction, Color color, State state) {
        return new TrafficLight(color, state, TrafficLight.Type.TRAFFIC, direction, true);
    }
}
