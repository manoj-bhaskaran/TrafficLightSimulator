package com.trafficlightsimulator.engine;

import com.trafficlightsimulator.model.Color;
import com.trafficlightsimulator.model.Direction;
import com.trafficlightsimulator.model.Intersection;
import com.trafficlightsimulator.model.PedestrianCrossing;
import com.trafficlightsimulator.model.Road;
import com.trafficlightsimulator.model.State;
import com.trafficlightsimulator.model.TrafficLight;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TrafficLightSimulationEngineTest {
    @Test
    void constructorRejectsNullIntersection() {
        assertThrows(IllegalArgumentException.class, () -> new TrafficLightSimulationEngine(null));
    }

    @Test
    void initializesRoadTrafficLightGroupsToOff() {
        Intersection intersection = new Intersection(2);
        Road firstRoad = roadWithTrafficLight(State.ON);
        Road secondRoad = roadWithTrafficLight(State.BLINKING);
        intersection.addRoad(firstRoad);
        intersection.addRoad(secondRoad);

        new TrafficLightSimulationEngine(intersection).initializeTrafficLightGroups();

        assertEquals(State.OFF, firstRoad.getIncomingLanesTrafficLightGroup().getLights().get(0).getState());
        assertEquals(State.OFF, secondRoad.getIncomingLanesTrafficLightGroup().getLights().get(0).getState());
    }

    @Test
    void initializesPedestrianTrafficLightGroupsToOff() {
        Intersection intersection = new Intersection(2);
        Road firstRoad = new Road(0.0, 1, 1);
        Road secondRoad = new Road(90.0, 1, 1);
        PedestrianCrossing crossing = new PedestrianCrossing();
        TrafficLight pedestrianLight = new TrafficLight(
                Color.GREEN,
                State.ON,
                TrafficLight.Type.PEDESTRIAN,
                Direction.NONE,
                false);
        crossing.addPedestrianLight(pedestrianLight);
        firstRoad.setPedestrianCrossing(crossing);
        intersection.addRoad(firstRoad);
        intersection.addRoad(secondRoad);

        new TrafficLightSimulationEngine(intersection).initializePedestrianLightGroups();

        assertEquals(State.OFF, pedestrianLight.getState());
    }

    private Road roadWithTrafficLight(State initialState) {
        Road road = new Road(0.0, 1, 1);
        road.addTrafficLightToIncomingGroup(new TrafficLight(
                Color.GREEN,
                initialState,
                TrafficLight.Type.TRAFFIC,
                Direction.STRAIGHT,
                true));
        return road;
    }
}
