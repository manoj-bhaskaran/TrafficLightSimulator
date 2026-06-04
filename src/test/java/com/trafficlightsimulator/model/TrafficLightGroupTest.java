package com.trafficlightsimulator.model;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TrafficLightGroupTest {

    @Test
    void addTrafficLight_tracksNonNullLightsAndIgnoresNulls() {
        TrafficLightGroup group = new TrafficLightGroup();
        TrafficLight light = trafficLight(Direction.STRAIGHT, Color.RED, State.ON);

        group.addTrafficLight(light);
        group.addTrafficLight(null);

        assertEquals(1, group.getLights().size());
        assertSame(light, group.getLights().get(0));
        assertTrue(group.getIncompatibleLights(light).isEmpty());
    }

    @Test
    void removeTrafficLight_removesMembershipAndConfiguredIncompatibilities() {
        TrafficLightGroup group = new TrafficLightGroup();
        TrafficLight northbound = trafficLight(Direction.STRAIGHT, Color.RED, State.ON);
        TrafficLight eastbound = trafficLight(Direction.LEFT, Color.RED, State.ON);
        group.addTrafficLight(northbound);
        group.addTrafficLight(eastbound);
        group.addIncompatibleLights(northbound, eastbound);

        group.removeTrafficLight(eastbound);

        assertFalse(group.getLights().contains(eastbound));
        assertFalse(group.areIncompatible(northbound, eastbound));
        assertTrue(group.getIncompatibleLights(northbound).isEmpty());
    }

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
    void addIncompatibleLights_rejectsNullsAndSelfRelationship() {
        TrafficLightGroup group = new TrafficLightGroup();
        TrafficLight light = trafficLight(Direction.STRAIGHT, Color.RED, State.ON);

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> group.addIncompatibleLights(null, light)),
                () -> assertThrows(IllegalArgumentException.class, () -> group.addIncompatibleLights(light, null)),
                () -> assertThrows(IllegalArgumentException.class, () -> group.addIncompatibleLights(light, light))
        );
    }

    @Test
    void removeIncompatibleLights_removesSymmetricRelationshipAndRejectsNulls() {
        TrafficLightGroup group = new TrafficLightGroup();
        TrafficLight northbound = trafficLight(Direction.STRAIGHT, Color.RED, State.ON);
        TrafficLight eastbound = trafficLight(Direction.LEFT, Color.RED, State.ON);
        group.addIncompatibleLights(northbound, eastbound);

        group.removeIncompatibleLights(northbound, eastbound);

        assertFalse(group.areIncompatible(northbound, eastbound));
        assertFalse(group.areIncompatible(eastbound, northbound));
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> group.removeIncompatibleLights(null, eastbound)),
                () -> assertThrows(IllegalArgumentException.class, () -> group.removeIncompatibleLights(northbound, null))
        );
    }

    @Test
    void getIncompatibleLights_rejectsNullAndExposesReadOnlyView() {
        TrafficLightGroup group = new TrafficLightGroup();
        TrafficLight northbound = trafficLight(Direction.STRAIGHT, Color.RED, State.ON);
        TrafficLight eastbound = trafficLight(Direction.LEFT, Color.RED, State.ON);
        group.addIncompatibleLights(northbound, eastbound);

        Set<TrafficLight> incompatibleLights = group.getIncompatibleLights(northbound);

        assertThrows(UnsupportedOperationException.class, () -> incompatibleLights.add(trafficLight(Direction.RIGHT, Color.RED, State.ON)));
        assertThrows(IllegalArgumentException.class, () -> group.getIncompatibleLights(null));
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

    @Test
    void setLightState_requiresLightToBelongToGroup() {
        TrafficLightGroup group = new TrafficLightGroup();
        TrafficLight ungrouped = trafficLight(Direction.NONE, Color.RED, State.ON);

        assertThrows(IllegalArgumentException.class, () -> group.setLightState(ungrouped, State.OFF));
    }

    @Test
    void setAllLightsState_updatesEveryCompatibleLight() {
        TrafficLightGroup group = new TrafficLightGroup();
        TrafficLight straight = trafficLight(Direction.STRAIGHT, Color.RED, State.ON);
        TrafficLight left = trafficLight(Direction.LEFT, Color.GREEN, State.ON);
        group.addTrafficLight(straight);
        group.addTrafficLight(left);

        group.setAllLightsState(State.OFF);

        assertEquals(State.OFF, straight.getState());
        assertEquals(State.OFF, left.getState());
    }

    @Test
    void setAllLightsState_continuesAfterConflictWarningPath() {
        TrafficLightGroup group = new TrafficLightGroup();
        TrafficLight active = trafficLight(Direction.STRAIGHT, Color.GREEN, State.ON);
        TrafficLight conflicting = trafficLight(Direction.LEFT, Color.GREEN, State.OFF);
        TrafficLight amber = trafficLight(Direction.RIGHT, Color.AMBER, State.OFF);
        group.addTrafficLight(active);
        group.addTrafficLight(conflicting);
        group.addTrafficLight(amber);
        group.addIncompatibleLights(active, conflicting);

        group.setAllLightsState(State.ON);

        assertEquals(State.ON, active.getState());
        assertEquals(State.OFF, conflicting.getState());
        assertEquals(State.ON, amber.getState());
    }

    @Test
    void setAllLightsColor_updatesSupportedLightsAndContinuesAfterValidationWarning() {
        TrafficLightGroup group = new TrafficLightGroup();
        TrafficLight trafficLight = trafficLight(Direction.STRAIGHT, Color.RED, State.OFF);
        TrafficLight pedestrianLight = pedestrianLight(Color.RED, State.OFF);
        group.addTrafficLight(trafficLight);
        group.addTrafficLight(pedestrianLight);

        group.setAllLightsColor(Color.AMBER);

        assertEquals(Color.AMBER, trafficLight.getColor());
        assertEquals(Color.RED, pedestrianLight.getColor());
    }

    private TrafficLight trafficLight(Direction direction, Color color, State state) {
        return new TrafficLight(color, state, TrafficLight.Type.TRAFFIC, direction, true);
    }

    private TrafficLight pedestrianLight(Color color, State state) {
        return new TrafficLight(color, state, TrafficLight.Type.PEDESTRIAN, Direction.NONE, false);
    }
}
