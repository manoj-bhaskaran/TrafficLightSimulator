package com.trafficlightsimulator.model;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PedestrianCrossing {
    private static final Logger logger = Logger.getLogger(PedestrianCrossing.class.getName());
    
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
            logger.log(Level.INFO, "Added pedestrian light: {0}", light);
        }
    }

    // Method to check if either button has been pressed
    public boolean isCrossingRequested() {
        return buttonAtStart.map(PedestrianButton::isPressed).orElse(false) ||
               buttonAtEnd.map(PedestrianButton::isPressed).orElse(false);
    }

    // Method to reset buttons after the crossing has been completed
    public void resetButtons() {
        buttonAtStart.ifPresent(button -> {
            button.reset();
            logger.log(Level.INFO, "Reset button at start of crossing.");
        });
        buttonAtEnd.ifPresent(button -> {
            button.reset();
            logger.log(Level.INFO, "Reset button at end of crossing.");
        });
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
        logger.log(Level.INFO, "Pedestrian Crossing Status:");
        pedestrianLightGroup.displayGroupStatus();
        buttonAtStart.ifPresent(button -> 
            logger.log(Level.INFO, "Button at Start: {0}", button.isPressed() ? "Pressed" : "Not Pressed"));
        buttonAtEnd.ifPresent(button -> 
            logger.log(Level.INFO, "Button at End: {0}", button.isPressed() ? "Pressed" : "Not Pressed"));
    }
}
