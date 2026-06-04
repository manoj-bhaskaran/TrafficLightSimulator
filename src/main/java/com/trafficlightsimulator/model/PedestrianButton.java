package com.trafficlightsimulator.model;

public class PedestrianButton {
    private TrafficLightGroup pedestrianLightGroup;
    private Object crossingAttachment;
    private boolean pressed;

    // Constructor
    public PedestrianButton(TrafficLightGroup pedestrianLightGroup) {
        if (pedestrianLightGroup == null) {
            throw new IllegalArgumentException("Pedestrian light group must not be null.");
        }
        this.pedestrianLightGroup = pedestrianLightGroup;
        this.crossingAttachment = null;
        this.pressed = false;
    }

    // Method to press the button
    public void press() {
        pressed = true;
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

    void attachToCrossing(Object crossingAttachment, TrafficLightGroup pedestrianLightGroup) {
        if (crossingAttachment == null) {
            throw new IllegalArgumentException("Crossing attachment must not be null.");
        }
        if (pedestrianLightGroup == null) {
            throw new IllegalArgumentException("Pedestrian light group must not be null.");
        }
        if (this.crossingAttachment != null && this.crossingAttachment != crossingAttachment) {
            throw new IllegalArgumentException("Pedestrian button is already attached to another crossing.");
        }
        this.crossingAttachment = crossingAttachment;
        this.pedestrianLightGroup = pedestrianLightGroup;
    }
}
