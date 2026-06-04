package com.trafficlightsimulator.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PedestrianCrossingTest {

    @Test
    void noButtonConstructor_initializesPedestrianLightGroupAndNoRequest() {
        PedestrianCrossing crossing = new PedestrianCrossing();

        assertNotNull(crossing.getPedestrianLightGroup());
        assertTrue(crossing.getButtonAtStart().isEmpty());
        assertTrue(crossing.getButtonAtEnd().isEmpty());
        assertFalse(crossing.isCrossingRequested());
    }

    @Test
    void buttonConstructor_wrapsNullableButtonsAndConnectsButtonsToCrossingLightGroup() {
        PedestrianButton startButton = new PedestrianButton(new TrafficLightGroup());

        PedestrianCrossing crossing = new PedestrianCrossing(startButton, null);

        assertTrue(crossing.getButtonAtStart().isPresent());
        assertSame(startButton, crossing.getButtonAtStart().orElseThrow());
        assertTrue(crossing.getButtonAtEnd().isEmpty());
        assertSame(crossing.getPedestrianLightGroup(), startButton.getPedestrianLightGroup());
    }

    @Test
    void isCrossingRequested_reflectsStartOrEndButtonPress() {
        PedestrianButton startButton = new PedestrianButton(new TrafficLightGroup());
        PedestrianButton endButton = new PedestrianButton(new TrafficLightGroup());
        PedestrianCrossing crossing = new PedestrianCrossing(startButton, endButton);

        assertFalse(crossing.isCrossingRequested());

        startButton.press();
        assertTrue(crossing.isCrossingRequested());

        startButton.reset();
        endButton.press();
        assertTrue(crossing.isCrossingRequested());
    }

    @Test
    void buttonPressTargetsCrossingPedestrianLightGroup() {
        PedestrianButton startButton = new PedestrianButton(new TrafficLightGroup());
        PedestrianButton endButton = new PedestrianButton(new TrafficLightGroup());
        PedestrianCrossing crossing = new PedestrianCrossing(startButton, endButton);
        TrafficLight pedestrianLight = new TrafficLight(Color.RED, State.ON,
                TrafficLight.Type.PEDESTRIAN, Direction.NONE, false);

        crossing.addPedestrianLight(pedestrianLight);
        startButton.press();

        assertTrue(crossing.isCrossingRequested());
        assertSame(crossing.getPedestrianLightGroup(), startButton.getPedestrianLightGroup());
        assertSame(crossing.getPedestrianLightGroup(), endButton.getPedestrianLightGroup());
        assertSame(pedestrianLight, startButton.getPedestrianLightGroup().getLights().get(0));
    }

    @Test
    void resetButtons_resetsBothButtons() {
        PedestrianButton startButton = new PedestrianButton(new TrafficLightGroup());
        PedestrianButton endButton = new PedestrianButton(new TrafficLightGroup());
        PedestrianCrossing crossing = new PedestrianCrossing(startButton, endButton);
        startButton.press();
        endButton.press();

        crossing.resetButtons();

        assertFalse(startButton.isPressed());
        assertFalse(endButton.isPressed());
        assertFalse(crossing.isCrossingRequested());
    }

    @Test
    void resetButtons_isSafeWhenButtonsAreAbsent() {
        PedestrianCrossing crossing = new PedestrianCrossing();

        assertDoesNotThrow(crossing::resetButtons);
        assertFalse(crossing.isCrossingRequested());
    }

    @Test
    void addPedestrianLight_addsOnlyPedestrianLightsAndIgnoresNulls() {
        PedestrianCrossing crossing = new PedestrianCrossing();
        TrafficLight pedestrianLight = new TrafficLight(Color.RED, State.ON,
                TrafficLight.Type.PEDESTRIAN, Direction.NONE, false);
        TrafficLight trafficLight = new TrafficLight(Color.RED, State.ON,
                TrafficLight.Type.TRAFFIC, Direction.STRAIGHT, true);

        crossing.addPedestrianLight(pedestrianLight);
        crossing.addPedestrianLight(trafficLight);
        crossing.addPedestrianLight(null);

        assertEquals(1, crossing.getPedestrianLightGroup().getLights().size());
        assertSame(pedestrianLight, crossing.getPedestrianLightGroup().getLights().get(0));
    }
}
