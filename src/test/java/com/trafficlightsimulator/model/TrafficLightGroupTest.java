package com.trafficlightsimulator.model;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TrafficLightGroupTest {

    @Test
    void addIncompatibleLights_configuresSymmetricIdentityRelationship() {
        TrafficLightGroup group = new TrafficLightGroup();
        TrafficLight northbound = trafficLight(Direction.STRAIGHT, Color.RED, State.ON);
        TrafficLight eastbound = trafficLight(Direction.LEFT, Color.RED, State.ON);
        group.addTrafficLight(northbound);
        group.addTrafficLight(eastbound);

        group.addIncompatibleLights(northbound, eastbound);

        assertTrue(group.areIncompatible(northbound, eastbound));
        assertTrue(group.areIncompatible(eastbound, northbound));
        assertEquals(Set.of(eastbound), group.getIncompatibleLights(northbound));
    }

    @Test
    void setLightColor_preventsGreenWhenIncompatibleLightAlreadyActive() {
        TrafficLightGroup group = new TrafficLightGroup();
        TrafficLight active = trafficLight(Direction.STRAIGHT, Color.GREEN, State.ON);
        TrafficLight conflicting = trafficLight(Direction.LEFT, Color.RED, State.ON);
        group.addTrafficLight(active);
        group.addTrafficLight(conflicting);
        group.addIncompatibleLights(active, conflicting);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> group.setLightColor(conflicting, Color.GREEN));

        assertTrue(exception.getMessage().contains("Cannot activate"));
        assertTrue(exception.getMessage().contains("already active"));
        assertEquals(Color.RED, conflicting.getColor());
    }

    @Test
    void setLightState_preventsOnWhenIncompatibleGreenLightAlreadyActive() {
        TrafficLightGroup group = new TrafficLightGroup();
        TrafficLight active = trafficLight(Direction.STRAIGHT, Color.GREEN, State.ON);
        TrafficLight conflicting = trafficLight(Direction.RIGHT, Color.GREEN, State.OFF);
        group.addTrafficLight(active);
        group.addTrafficLight(conflicting);
        group.addIncompatibleLights(active, conflicting);

        assertThrows(IllegalStateException.class, () -> group.setLightState(conflicting, State.ON));

        assertEquals(State.OFF, conflicting.getState());
    }

    @Test
    void setLightColor_allowsGreenWhenIncompatibleLightIsNotActive() {
        TrafficLightGroup group = new TrafficLightGroup();
        TrafficLight inactive = trafficLight(Direction.STRAIGHT, Color.GREEN, State.OFF);
        TrafficLight candidate = trafficLight(Direction.LEFT, Color.RED, State.ON);
        group.addTrafficLight(inactive);
        group.addTrafficLight(candidate);
        group.addIncompatibleLights(inactive, candidate);

        group.setLightColor(candidate, Color.GREEN);

        assertEquals(Color.GREEN, candidate.getColor());
    }

    @Test
    void setLightColor_requiresLightToBelongToGroup() {
        TrafficLightGroup group = new TrafficLightGroup();
        TrafficLight ungrouped = trafficLight(Direction.NONE, Color.RED, State.ON);

        assertThrows(IllegalArgumentException.class, () -> group.setLightColor(ungrouped, Color.GREEN));
    }

    private TrafficLight trafficLight(Direction direction, Color color, State state) {
        return new TrafficLight(color, state, TrafficLight.Type.TRAFFIC, direction, true);
    }
}
