package com.trafficlightsimulator.model;

public class PedestrianButton {
    private final TrafficLightGroup pedestrianLightGroup;
    private boolean pressed;

    // Constructor
    public PedestrianButton(TrafficLightGroup pedestrianLightGroup) {
        this.pedestrianLightGroup = pedestrianLightGroup;
        this.pressed = false;
    }

    // Method to press the button
    public void press() {
        if (!pressed) {
            pressed = true;
        }
    }

    // Method to reset the button state after the pedestrian crossing request is processed
    public void reset() {
        pressed = false;
    }

    // Getter for the button's pressed state
    public boolean isPressed() {
        return pressed;
    }

    // Getter for the associated pedestrian light group
    public TrafficLightGroup getPedestrianLightGroup() {
        return pedestrianLightGroup;
    }
}
