package com.trafficlightsimulator.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PedestrianButtonTest {

    @Test
    void constructor_startsUnpressedAndRetainsLightGroupReference() {
        TrafficLightGroup group = new TrafficLightGroup();
        PedestrianButton button = new PedestrianButton(group);

        assertFalse(button.isPressed());
        assertSame(group, button.getPedestrianLightGroup());
    }

    @Test
    void press_setsPressedStateAndIsIdempotent() {
        PedestrianButton button = new PedestrianButton(new TrafficLightGroup());

        button.press();
        button.press();

        assertTrue(button.isPressed());
    }

    @Test
    void reset_clearsPressedStateAndIsIdempotent() {
        PedestrianButton button = new PedestrianButton(new TrafficLightGroup());
        button.press();

        button.reset();
        button.reset();

        assertFalse(button.isPressed());
    }
}
