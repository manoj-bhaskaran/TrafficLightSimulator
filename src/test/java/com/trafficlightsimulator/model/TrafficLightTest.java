package com.trafficlightsimulator.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrafficLightTest {

    @Test
    void constructor_initializesAllProperties() {
        TrafficLight light = new TrafficLight(Color.GREEN, State.OFF,
                TrafficLight.Type.TRAFFIC, Direction.LEFT, true);

        assertAll(
                () -> assertEquals(Color.GREEN, light.getColor()),
                () -> assertEquals(State.OFF, light.getState()),
                () -> assertEquals(TrafficLight.Type.TRAFFIC, light.getType()),
                () -> assertEquals(Direction.LEFT, light.getDirection()),
                () -> assertTrue(light.isMultiColor())
        );
    }

    @Test
    void constructor_rejectsNullColor() {
        assertThrows(IllegalArgumentException.class,
                () -> new TrafficLight(null, State.ON, TrafficLight.Type.TRAFFIC, Direction.NONE, true));
    }

    @Test
    void constructor_rejectsNullState() {
        assertThrows(IllegalArgumentException.class,
                () -> new TrafficLight(Color.RED, null, TrafficLight.Type.TRAFFIC, Direction.NONE, true));
    }

    @Test
    void constructor_rejectsNullType() {
        assertThrows(IllegalArgumentException.class,
                () -> new TrafficLight(Color.RED, State.ON, null, Direction.NONE, true));
    }

    @Test
    void constructor_rejectsNullDirection() {
        assertThrows(IllegalArgumentException.class,
                () -> new TrafficLight(Color.RED, State.ON, TrafficLight.Type.TRAFFIC, null, true));
    }

    @Test
    void constructor_rejectsAmberPedestrianLight() {
        assertThrows(IllegalArgumentException.class,
                () -> new TrafficLight(Color.AMBER, State.ON, TrafficLight.Type.PEDESTRIAN, Direction.NONE, true));
    }

    @Test
    void trafficMultiColor_acceptsAllSignalColors() {
        TrafficLight light = trafficLight(TrafficLight.Type.TRAFFIC, true);

        light.setColor(Color.RED);
        assertEquals(Color.RED, light.getColor());

        light.setColor(Color.AMBER);
        assertEquals(Color.AMBER, light.getColor());

        light.setColor(Color.GREEN);
        assertEquals(Color.GREEN, light.getColor());
    }

    @Test
    void trafficSingleColor_acceptsAllSignalColors() {
        TrafficLight light = trafficLight(TrafficLight.Type.TRAFFIC, false);

        light.setColor(Color.RED);
        assertEquals(Color.RED, light.getColor());

        light.setColor(Color.AMBER);
        assertEquals(Color.AMBER, light.getColor());

        light.setColor(Color.GREEN);
        assertEquals(Color.GREEN, light.getColor());
    }

    @Test
    void pedestrianMultiColor_acceptsRedAndGreenButRejectsAmber() {
        TrafficLight light = trafficLight(TrafficLight.Type.PEDESTRIAN, true);

        light.setColor(Color.RED);
        assertEquals(Color.RED, light.getColor());

        light.setColor(Color.GREEN);
        assertEquals(Color.GREEN, light.getColor());

        assertThrows(IllegalArgumentException.class, () -> light.setColor(Color.AMBER));
        assertEquals(Color.GREEN, light.getColor());
    }

    @Test
    void pedestrianSingleColor_acceptsRedAndGreenButRejectsAmber() {
        TrafficLight light = trafficLight(TrafficLight.Type.PEDESTRIAN, false);

        light.setColor(Color.RED);
        assertEquals(Color.RED, light.getColor());

        light.setColor(Color.GREEN);
        assertEquals(Color.GREEN, light.getColor());

        assertThrows(IllegalArgumentException.class, () -> light.setColor(Color.AMBER));
        assertEquals(Color.GREEN, light.getColor());
    }

    @Test
    void setColor_rejectsNullAndKeepsExistingColor() {
        TrafficLight light = trafficLight(TrafficLight.Type.TRAFFIC, true);

        assertThrows(IllegalArgumentException.class, () -> light.setColor(null));

        assertEquals(Color.RED, light.getColor());
    }

    @Test
    void setState_updatesStateAndRejectsNull() {
        TrafficLight light = trafficLight(TrafficLight.Type.TRAFFIC, true);

        light.setState(State.OFF);
        assertEquals(State.OFF, light.getState());

        assertThrows(IllegalArgumentException.class, () -> light.setState(null));
        assertEquals(State.OFF, light.getState());
    }

    @Test
    void setDirection_updatesDirectionAndRejectsNull() {
        TrafficLight light = trafficLight(TrafficLight.Type.TRAFFIC, true);

        light.setDirection(Direction.RIGHT);
        assertEquals(Direction.RIGHT, light.getDirection());

        assertThrows(IllegalArgumentException.class, () -> light.setDirection(null));
        assertEquals(Direction.RIGHT, light.getDirection());
    }

    private TrafficLight trafficLight(TrafficLight.Type type, boolean multiColor) {
        return new TrafficLight(Color.RED, State.ON, type, Direction.NONE, multiColor);
    }
}
