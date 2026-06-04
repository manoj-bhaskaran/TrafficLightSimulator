package com.trafficlightsimulator.model;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PedestrianCrossing {
    private static final Logger logger = Logger.getLogger(PedestrianCrossing.class.getName());

    public enum ControlType {
        BUTTON_CONTROLLED,
        AUTOMATED
    }

    private final TrafficLightGroup pedestrianLightGroup;
    private final Object buttonAttachment;
    private final ControlType controlType;
    private PedestrianButton buttonAtStart;
    private PedestrianButton buttonAtEnd;

    // Constructor without buttons — creates an automated crossing
    public PedestrianCrossing() {
        this(null, null);
    }

    // Constructor with buttons at both ends — creates a button-controlled crossing
    public PedestrianCrossing(PedestrianButton buttonAtStart, PedestrianButton buttonAtEnd) {
        this.pedestrianLightGroup = new TrafficLightGroup();
        this.buttonAttachment = new Object();
        this.controlType = (buttonAtStart != null || buttonAtEnd != null)
                ? ControlType.BUTTON_CONTROLLED : ControlType.AUTOMATED;
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

    // Getter for the control type of this crossing
    public ControlType getControlType() {
        return controlType;
    }

    // Returns true if at least one pedestrian light in this crossing is currently active (GREEN + ON)
    public boolean isActive() {
        return pedestrianLightGroup.getLights().stream()
                .anyMatch(l -> l.getColor() == Color.GREEN && l.getState() == State.ON);
    }

    // Sets all pedestrian lights to GREEN and ON (grants pedestrians the right to cross)
    public void activate() {
        pedestrianLightGroup.setAllLightsState(State.ON);
        pedestrianLightGroup.setAllLightsColor(Color.GREEN);
        logger.log(Level.INFO, "Pedestrian crossing activated.");
    }

    // Sets all pedestrian lights to RED (stops pedestrian crossing)
    public void deactivate() {
        pedestrianLightGroup.setAllLightsColor(Color.RED);
        pedestrianLightGroup.setAllLightsState(State.ON);
        logger.log(Level.INFO, "Pedestrian crossing deactivated.");
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
            button.attachToCrossing(buttonAttachment, pedestrianLightGroup);
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
