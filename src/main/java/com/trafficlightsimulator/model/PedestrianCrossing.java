package com.trafficlightsimulator.model;

import java.util.Optional;

public class PedestrianCrossing {
    private TrafficLightGroup pedestrianLightGroup;
    private Optional<PedestrianButton> buttonAtStart;
    private Optional<PedestrianButton> buttonAtEnd;

    // Constructor without buttons
    public PedestrianCrossing() {
        this.pedestrianLightGroup = new TrafficLightGroup();
        this.buttonAtStart = Optional.empty();
        this.buttonAtEnd = Optional.empty();
    }

    // Constructor with buttons at both ends
    public PedestrianCrossing(PedestrianButton buttonAtStart, PedestrianButton buttonAtEnd) {
        this.pedestrianLightGroup = new TrafficLightGroup();
        this.buttonAtStart = Optional.ofNullable(buttonAtStart);
        this.buttonAtEnd = Optional.ofNullable(buttonAtEnd);
    }

    // Method to initialize the pedestrian light group
    public void addPedestrianLight(TrafficLight light) {
        if (light != null && light.getType() == TrafficLight.Type.PEDESTRIAN) {
            pedestrianLightGroup.addTrafficLight(light);
        }
    }

    // Method to check if either button has been pressed
    public boolean isCrossingRequested() {
        return buttonAtStart.map(PedestrianButton::isPressed).orElse(false) ||
               buttonAtEnd.map(PedestrianButton::isPressed).orElse(false);
    }

    // Method to reset buttons after the crossing has been completed
    public void resetButtons() {
        buttonAtStart.ifPresent(PedestrianButton::reset);
        buttonAtEnd.ifPresent(PedestrianButton::reset);
    }

    // Getter for the pedestrian light group
    public TrafficLightGroup getPedestrianLightGroup() {
        return pedestrianLightGroup;
    }

    // Getter for the button at the start of the crossing
    public Optional<PedestrianButton> getButtonAtStart() {
        return buttonAtStart;
    }

    // Getter for the button at the end of the crossing
    public Optional<PedestrianButton> getButtonAtEnd() {
        return buttonAtEnd;
    }

    // Method to display the status of the pedestrian crossing (for debugging)
    public void displayCrossingStatus() {
        System.out.println("Pedestrian Crossing Status:");
        pedestrianLightGroup.displayGroupStatus();
        buttonAtStart.ifPresent(button -> System.out.println("Button at Start: " + (button.isPressed() ? "Pressed" : "Not Pressed")));
        buttonAtEnd.ifPresent(button -> System.out.println("Button at End: " + (button.isPressed() ? "Pressed" : "Not Pressed")));
    }
}
