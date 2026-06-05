package com.trafficlightsimulator.model;

/**
 * A physical push-button at one end of a {@link PedestrianCrossing} that
 * pedestrians use to request the right of way.
 *
 * <p>A button is created with a reference to the {@link TrafficLightGroup}
 * it should influence, and then attached to exactly one crossing via
 * {@link PedestrianCrossing}'s constructor. A button that is already attached
 * to one crossing cannot be reused by another crossing; this invariant
 * prevents an existing crossing from losing its button-to-light-group link.
 */
public class PedestrianButton {
    private TrafficLightGroup pedestrianLightGroup;
    private Object crossingAttachment;
    private boolean pressed;

    /**
     * Creates an unpressed button linked to the given pedestrian light group.
     *
     * @param pedestrianLightGroup light group that this button controls; must
     *                             not be null
     * @throws IllegalArgumentException if {@code pedestrianLightGroup} is null
     */
    public PedestrianButton(TrafficLightGroup pedestrianLightGroup) {
        if (pedestrianLightGroup == null) {
            throw new IllegalArgumentException("Pedestrian light group must not be null.");
        }
        this.pedestrianLightGroup = pedestrianLightGroup;
        this.crossingAttachment = null;
        this.pressed = false;
    }

    /**
     * Records that the button has been pressed by a pedestrian.
     */
    public void press() {
        pressed = true;
    }

    /**
     * Clears the pressed state once the crossing request has been processed.
     */
    public void reset() {
        pressed = false;
    }

    /**
     * Returns {@code true} if the button has been pressed and not yet reset.
     *
     * @return {@code true} if the button is pressed
     */
    public boolean isPressed() {
        return pressed;
    }

    /**
     * Returns the pedestrian light group that this button controls.
     *
     * @return associated light group; never null
     */
    public TrafficLightGroup getPedestrianLightGroup() {
        return pedestrianLightGroup;
    }

    /**
     * Binds this button to a crossing so the crossing owns the relationship.
     * Package-private: only {@link PedestrianCrossing} should call this.
     *
     * @param crossingAttachment    opaque token identifying the owning crossing
     * @param pedestrianLightGroup  light group owned by the crossing
     * @throws IllegalArgumentException if either argument is null, or if this
     *                                  button is already bound to a different
     *                                  crossing
     */
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

    /**
     * Returns a compact diagnostic representation of this button.
     *
     * @return readable pedestrian-button summary
     */
    @Override
    public String toString() {
        return "PedestrianButton{"
                + "pressed=" + pressed
                + ", attached=" + (crossingAttachment != null)
                + '}';
    }

    /**
     * Pedestrian buttons model physical push-button instances, so equality is
     * intentionally based on object identity.
     *
     * @param obj object to compare
     * @return {@code true} only when both references point to the same button
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
