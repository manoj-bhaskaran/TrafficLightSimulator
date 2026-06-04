package com.trafficlightsimulator.model;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PedestrianCrossing {
    private static final Logger logger = Logger.getLogger(PedestrianCrossing.class.getName());
    
    private final TrafficLightGroup pedestrianLightGroup;
    private PedestrianButton buttonAtStart;
    private PedestrianButton buttonAtEnd;

    // Constructor without buttons
    public PedestrianCrossing() {
        this(null, null);
    }

    // Constructor with buttons at both ends
    public PedestrianCrossing(PedestrianButton buttonAtStart, PedestrianButton buttonAtEnd) {
        this.pedestrianLightGroup = new TrafficLightGroup();
        this.buttonAtStart = buttonAtStart;
        this.buttonAtEnd = buttonAtEnd;
        connectButton(buttonAtStart);
        connectButton(buttonAtEnd);
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
        return isButtonPressed(buttonAtStart) || isButtonPressed(buttonAtEnd);
    }

    // Method to reset buttons after the crossing has been completed
    public void resetButtons() {
        resetButton(buttonAtStart, "start");
        resetButton(buttonAtEnd, "end");
    }

    // Getter for the pedestrian light group
    public TrafficLightGroup getPedestrianLightGroup() {
        return pedestrianLightGroup;
    }

    // Getter for the button at the start of the crossing
    public Optional<PedestrianButton> getButtonAtStart() {
        return Optional.ofNullable(buttonAtStart);
    }

    // Getter for the button at the end of the crossing
    public Optional<PedestrianButton> getButtonAtEnd() {
        return Optional.ofNullable(buttonAtEnd);
    }

    // Method to display the status of the pedestrian crossing (for debugging)
    public void displayCrossingStatus() {
        logger.log(Level.INFO, "Pedestrian Crossing Status:");
        pedestrianLightGroup.displayGroupStatus();
        logButtonStatus(buttonAtStart, "Start");
        logButtonStatus(buttonAtEnd, "End");
    }

    private void connectButton(PedestrianButton button) {
        if (button != null) {
            button.setPedestrianLightGroup(pedestrianLightGroup);
        }
    }

    private boolean isButtonPressed(PedestrianButton button) {
        return button != null && button.isPressed();
    }

    private void resetButton(PedestrianButton button, String location) {
        if (button != null) {
            button.reset();
            logger.log(Level.INFO, "Reset button at {0} of crossing.", location);
        }
    }

    private void logButtonStatus(PedestrianButton button, String location) {
        if (button != null) {
            logger.log(Level.INFO, "Button at {0}: {1}",
                    new Object[]{location, button.isPressed() ? "Pressed" : "Not Pressed"});
        }
    }
}
