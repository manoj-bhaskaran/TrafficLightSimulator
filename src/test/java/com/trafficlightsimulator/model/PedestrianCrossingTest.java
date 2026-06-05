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
    void buttonConstructor_rejectsButtonAlreadyAttachedToAnotherCrossing() {
        PedestrianButton sharedButton = new PedestrianButton(new TrafficLightGroup());
        PedestrianCrossing firstCrossing = new PedestrianCrossing(sharedButton, null);
        TrafficLightGroup firstCrossingLightGroup = firstCrossing.getPedestrianLightGroup();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new PedestrianCrossing(sharedButton, null));

        assertTrue(exception.getMessage().contains("already attached"));
        assertSame(firstCrossingLightGroup, sharedButton.getPedestrianLightGroup());
        assertSame(sharedButton, firstCrossing.getButtonAtStart().orElseThrow());
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

    @Test
    void noButtonConstructor_isAutomatedControlType() {
        PedestrianCrossing crossing = new PedestrianCrossing();

        assertEquals(PedestrianCrossing.ControlType.AUTOMATED, crossing.getControlType());
    }

    @Test
    void buttonConstructor_withAtLeastOneButton_isButtonControlledType() {
        PedestrianButton startButton = new PedestrianButton(new TrafficLightGroup());
        PedestrianCrossing crossing = new PedestrianCrossing(startButton, null);

        assertEquals(PedestrianCrossing.ControlType.BUTTON_CONTROLLED, crossing.getControlType());
    }

    @Test
    void buttonConstructor_withBothNullButtons_isAutomatedControlType() {
        PedestrianCrossing crossing = new PedestrianCrossing(null, null);

        assertEquals(PedestrianCrossing.ControlType.AUTOMATED, crossing.getControlType());
    }

    @Test
    void isActive_returnsFalseWhenNoLightsAdded() {
        PedestrianCrossing crossing = new PedestrianCrossing();

        assertFalse(crossing.isActive());
    }

    @Test
    void isActive_returnsFalseWhenLightIsRedAndOn() {
        PedestrianCrossing crossing = new PedestrianCrossing();
        crossing.addPedestrianLight(new TrafficLight(Color.RED, State.ON,
                TrafficLight.Type.PEDESTRIAN, Direction.NONE, false));

        assertFalse(crossing.isActive());
    }

    @Test
    void activate_setsAllPedestrianLightsToGreenOn() {
        PedestrianCrossing crossing = new PedestrianCrossing();
        TrafficLight light = new TrafficLight(Color.RED, State.ON,
                TrafficLight.Type.PEDESTRIAN, Direction.NONE, false);
        crossing.addPedestrianLight(light);

        crossing.activate();

        assertTrue(crossing.isActive());
        assertEquals(Color.GREEN, light.getColor());
        assertEquals(State.ON, light.getState());
    }

    @Test
    void deactivate_setsAllPedestrianLightsToRedOn() {
        PedestrianCrossing crossing = new PedestrianCrossing();
        TrafficLight light = new TrafficLight(Color.RED, State.ON,
                TrafficLight.Type.PEDESTRIAN, Direction.NONE, false);
        crossing.addPedestrianLight(light);
        crossing.activate();

        crossing.deactivate();

        assertFalse(crossing.isActive());
        assertEquals(Color.RED, light.getColor());
        assertEquals(State.ON, light.getState());
    }

    @Test
    void activateAndDeactivate_toggleStatusCorrectly() {
        PedestrianCrossing crossing = new PedestrianCrossing();
        crossing.addPedestrianLight(new TrafficLight(Color.RED, State.ON,
                TrafficLight.Type.PEDESTRIAN, Direction.NONE, false));

        assertFalse(crossing.isActive());

        crossing.activate();
        assertTrue(crossing.isActive());

        crossing.deactivate();
        assertFalse(crossing.isActive());
    }

    @Test
    void equality_usesIdentityAndToStringIsReadable() {
        PedestrianCrossing crossing = new PedestrianCrossing();
        PedestrianCrossing sameValueCrossing = new PedestrianCrossing();

        assertEquals(crossing, crossing);
        assertNotEquals(crossing, sameValueCrossing);
        assertEquals(System.identityHashCode(crossing), crossing.hashCode());
        assertTrue(crossing.toString().contains("controlType=AUTOMATED"));
        assertFalse(crossing.toString().contains("@"));
    }

}
