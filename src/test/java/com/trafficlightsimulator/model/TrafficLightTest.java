package com.trafficlightsimulator.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TrafficLightTest {

    private TrafficLight trafficLight(TrafficLight.Type type, boolean multiColor) {
        return new TrafficLight(Color.RED, State.ON, type, Direction.NONE, multiColor);
    }

    // --- TRAFFIC type ---

    @Test
    void trafficMultiColor_acceptsRed() {
        TrafficLight light = trafficLight(TrafficLight.Type.TRAFFIC, true);
        light.setColor(Color.RED);
        assertEquals(Color.RED, light.getColor());
    }

    @Test
    void trafficMultiColor_acceptsAmber() {
        TrafficLight light = trafficLight(TrafficLight.Type.TRAFFIC, true);
        light.setColor(Color.AMBER);
        assertEquals(Color.AMBER, light.getColor());
    }

    @Test
    void trafficMultiColor_acceptsGreen() {
        TrafficLight light = trafficLight(TrafficLight.Type.TRAFFIC, true);
        light.setColor(Color.GREEN);
        assertEquals(Color.GREEN, light.getColor());
    }

    @Test
    void trafficSingleColor_acceptsRed() {
        TrafficLight light = trafficLight(TrafficLight.Type.TRAFFIC, false);
        light.setColor(Color.RED);
        assertEquals(Color.RED, light.getColor());
    }

    @Test
    void trafficSingleColor_acceptsAmber() {
        TrafficLight light = trafficLight(TrafficLight.Type.TRAFFIC, false);
        light.setColor(Color.AMBER);
        assertEquals(Color.AMBER, light.getColor());
    }

    @Test
    void trafficSingleColor_acceptsGreen() {
        TrafficLight light = trafficLight(TrafficLight.Type.TRAFFIC, false);
        light.setColor(Color.GREEN);
        assertEquals(Color.GREEN, light.getColor());
    }

    // --- PEDESTRIAN type ---

    @Test
    void pedestrianMultiColor_acceptsRed() {
        TrafficLight light = trafficLight(TrafficLight.Type.PEDESTRIAN, true);
        light.setColor(Color.RED);
        assertEquals(Color.RED, light.getColor());
    }

    @Test
    void pedestrianMultiColor_acceptsGreen() {
        TrafficLight light = trafficLight(TrafficLight.Type.PEDESTRIAN, true);
        light.setColor(Color.GREEN);
        assertEquals(Color.GREEN, light.getColor());
    }

    @Test
    void pedestrianMultiColor_rejectsAmber() {
        TrafficLight light = trafficLight(TrafficLight.Type.PEDESTRIAN, true);
        assertThrows(IllegalArgumentException.class, () -> light.setColor(Color.AMBER));
    }

    @Test
    void pedestrianSingleColor_acceptsRed() {
        TrafficLight light = trafficLight(TrafficLight.Type.PEDESTRIAN, false);
        light.setColor(Color.RED);
        assertEquals(Color.RED, light.getColor());
    }

    @Test
    void pedestrianSingleColor_acceptsGreen() {
        TrafficLight light = trafficLight(TrafficLight.Type.PEDESTRIAN, false);
        light.setColor(Color.GREEN);
        assertEquals(Color.GREEN, light.getColor());
    }

    @Test
    void pedestrianSingleColor_rejectsAmber() {
        TrafficLight light = trafficLight(TrafficLight.Type.PEDESTRIAN, false);
        assertThrows(IllegalArgumentException.class, () -> light.setColor(Color.AMBER));
    }

    // --- Null guard ---

    @Test
    void setColor_rejectsNull() {
        TrafficLight light = trafficLight(TrafficLight.Type.TRAFFIC, true);
        assertThrows(IllegalArgumentException.class, () -> light.setColor(null));
    }
}
