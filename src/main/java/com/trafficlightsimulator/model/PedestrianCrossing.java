package com.trafficlightsimulator.model;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A pedestrian crossing associated with a {@link Road}.
 *
 * <p>A crossing owns the {@link TrafficLightGroup} that controls its
 * pedestrian signals and manages zero, one, or two {@link PedestrianButton}
 * instances positioned at each end. When constructed with at least one
 * button, the crossing connects those buttons to its owned light group and is
 * classified as {@link ControlType#BUTTON_CONTROLLED}; a no-argument
 * constructor creates an {@link ControlType#AUTOMATED} crossing.
 *
 * <p>The {@link #activate()} and {@link #deactivate()} methods grant or
 * revoke the pedestrian right of way by setting all lights in the owned group
 * to GREEN+ON or RED+ON respectively.
 */
public class PedestrianCrossing {
    private static final Logger logger = Logger.getLogger(PedestrianCrossing.class.getName());

    /**
     * Distinguishes how a crossing receives its activation signal.
     */
    public enum ControlType {
        /** Crossing activates when a {@link PedestrianButton} is pressed. */
        BUTTON_CONTROLLED,
        /** Crossing activation is driven entirely by the simulation engine. */
        AUTOMATED
    }

    private final TrafficLightGroup pedestrianLightGroup;
    private final Object buttonAttachment;
    private final ControlType controlType;
    private PedestrianButton buttonAtStart;
    private PedestrianButton buttonAtEnd;

    /**
     * Creates an automated pedestrian crossing with no buttons.
     */
    public PedestrianCrossing() {
        this(null, null);
    }

    /**
     * Creates a pedestrian crossing, optionally with buttons at each end.
     *
     * <p>If at least one button is non-null the crossing is classified as
     * {@link ControlType#BUTTON_CONTROLLED}; otherwise it is
     * {@link ControlType#AUTOMATED}. Each non-null button is bound to this
     * crossing's owned light group.
     *
     * @param buttonAtStart button at the start of the crossing, or null if
     *                      absent
     * @param buttonAtEnd   button at the end of the crossing, or null if
     *                      absent
     * @throws IllegalArgumentException if a button is already attached to a
     *                                  different crossing
     */
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

    /**
     * Adds a pedestrian signal light to this crossing's light group. Null
     * values and non-pedestrian lights are silently ignored.
     *
     * @param light pedestrian light to add; ignored if null or not
     *              {@link TrafficLight.Type#PEDESTRIAN}
     */
    public void addPedestrianLight(TrafficLight light) {
        if (light != null && light.getType() == TrafficLight.Type.PEDESTRIAN) {
            pedestrianLightGroup.addTrafficLight(light);
            logger.log(Level.FINE, "Added pedestrian light: {0}", light);
        }
    }

    /**
     * Returns {@code true} if at least one button has been pressed and not
     * yet reset.
     *
     * @return {@code true} if a crossing has been requested
     */
    public boolean isCrossingRequested() {
        return isButtonPressed(buttonAtStart) || isButtonPressed(buttonAtEnd);
    }

    /**
     * Resets the pressed state of all buttons after the crossing has been
     * completed.
     */
    public void resetButtons() {
        resetButton(buttonAtStart, "start");
        resetButton(buttonAtEnd, "end");
    }

    /**
     * Returns how this crossing receives its activation signal.
     *
     * @return control type; never null
     */
    public ControlType getControlType() {
        return controlType;
    }

    /**
     * Returns {@code true} if at least one pedestrian light in this crossing
     * is currently GREEN and ON.
     *
     * @return {@code true} if the crossing is active
     */
    public boolean isActive() {
        return pedestrianLightGroup.getLights().stream()
                .anyMatch(l -> l.getColor() == Color.GREEN && l.getState() == State.ON);
    }

    /**
     * Grants pedestrians the right of way by setting all lights in this
     * crossing's group to GREEN and ON.
     */
    public void activate() {
        pedestrianLightGroup.setAllLightsState(State.ON);
        pedestrianLightGroup.setAllLightsColor(Color.GREEN);
        logger.log(Level.FINE, "Pedestrian crossing activated.");
    }

    /**
     * Revokes the pedestrian right of way by setting all lights in this
     * crossing's group to RED and ON.
     */
    public void deactivate() {
        pedestrianLightGroup.setAllLightsColor(Color.RED);
        pedestrianLightGroup.setAllLightsState(State.ON);
        logger.log(Level.FINE, "Pedestrian crossing deactivated.");
    }

    /**
     * Returns the {@link TrafficLightGroup} that controls all pedestrian
     * signals at this crossing.
     *
     * @return pedestrian light group; never null
     */
    public TrafficLightGroup getPedestrianLightGroup() {
        return pedestrianLightGroup;
    }

    /**
     * Returns the button at the start of this crossing, if present.
     *
     * @return an {@link Optional} containing the start button, or empty if
     *         this crossing has no start button
     */
    public Optional<PedestrianButton> getButtonAtStart() {
        return Optional.ofNullable(buttonAtStart);
    }

    /**
     * Returns the button at the end of this crossing, if present.
     *
     * @return an {@link Optional} containing the end button, or empty if this
     *         crossing has no end button
     */
    public Optional<PedestrianButton> getButtonAtEnd() {
        return Optional.ofNullable(buttonAtEnd);
    }

    /**
     * Logs the light states and button pressed status for diagnostic purposes.
     */
    public void displayCrossingStatus() {
        logger.log(Level.FINE, "{0}", this);
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
            logger.log(Level.FINE, "Reset button at {0} of crossing.", location);
        }
    }

    private void logButtonStatus(PedestrianButton button, String location) {
        if (button != null) {
            logger.log(Level.FINE, "Button at {0}: {1}", new Object[]{location, button});
        }
    }

    /**
     * Returns a compact diagnostic representation of this crossing.
     *
     * @return readable pedestrian-crossing summary
     */
    @Override
    public String toString() {
        return "PedestrianCrossing{"
                + "controlType=" + controlType
                + ", lightCount=" + pedestrianLightGroup.getLights().size()
                + ", crossingRequested=" + isCrossingRequested()
                + ", active=" + isActive()
                + '}';
    }

    /**
     * Pedestrian crossings model physical crossing instances, so equality is
     * intentionally based on object identity.
     *
     * @param obj object to compare
     * @return {@code true} only when both references point to the same crossing
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    /**
     * Returns an identity-based hash code consistent with {@link #equals(Object)}.
     *
     * @return identity hash code
     */
    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

}
