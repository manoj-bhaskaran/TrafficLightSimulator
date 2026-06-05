package com.trafficlightsimulator.model;

/**
 * A single traffic or pedestrian signal light.
 *
 * <p>Instances intentionally rely on Java object identity for equality so that
 * {@link TrafficLightGroup} incompatibility rules can target specific physical
 * lights rather than value-equal copies.
 */
public class TrafficLight {

    /** Distinguishes vehicle-facing signals from pedestrian-facing signals. */
    public enum Type {
        TRAFFIC,
        PEDESTRIAN
    }

    private Color color;
    private State state;
    private final Type type;
    private Direction direction;
    private final boolean isMultiColor;

    /**
     * Creates a traffic light with the supplied initial properties.
     *
     * @param color        initial signal colour; must not be null, and must not
     *                     be {@link Color#AMBER} when {@code type} is
     *                     {@link Type#PEDESTRIAN}
     * @param state        initial operational state; must not be null
     * @param type         signal type (traffic or pedestrian); must not be null
     * @param direction    directional arrow shown on this light face; must not
     *                     be null
     * @param isMultiColor {@code true} if the physical light can display more
     *                     than one colour
     * @throws IllegalArgumentException if {@code type} or {@code direction} is
     *                                  null, or if an {@link Color#AMBER} colour
     *                                  is supplied for a pedestrian light
     */
    public TrafficLight(Color color, State state, Type type, Direction direction, boolean isMultiColor) {
        if (type == null) {
            throw new IllegalArgumentException("Traffic light type must not be null.");
        }
        if (direction == null) {
            throw new IllegalArgumentException("Direction must not be null.");
        }
        this.type = type;
        this.direction = direction;
        this.isMultiColor = isMultiColor;
        setColor(color);
        setState(state);
    }

    /**
     * Returns the current signal colour.
     *
     * @return current colour; never null after construction
     */
    public Color getColor() {
        return color;
    }

    /**
     * Changes the signal colour.
     *
     * @param color new colour; must not be null, and must not be
     *              {@link Color#AMBER} for a {@link Type#PEDESTRIAN} light
     * @throws IllegalArgumentException if {@code color} is null or if AMBER is
     *                                  supplied for a pedestrian light
     */
    public void setColor(Color color) {
        if (color == null) {
            throw new IllegalArgumentException("Color must not be null.");
        }
        if (type == Type.PEDESTRIAN && color == Color.AMBER) {
            throw new IllegalArgumentException("Pedestrian lights do not support AMBER.");
        }
        this.color = color;
    }

    /**
     * Returns the current operational state.
     *
     * @return current state; never null after construction
     */
    public State getState() {
        return state;
    }

    /**
     * Changes the operational state.
     *
     * @param state new state; must not be null
     * @throws IllegalArgumentException if {@code state} is null
     */
    public void setState(State state) {
        if (state == null) {
            throw new IllegalArgumentException("State must not be null.");
        }
        this.state = state;
    }

    /**
     * Returns whether this is a vehicle-facing or pedestrian-facing signal.
     *
     * @return signal type; never null
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the directional arrow currently shown on this light face.
     *
     * @return directional arrow; never null
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Changes the directional arrow shown on this light face.
     *
     * @param direction new direction; must not be null
     * @throws IllegalArgumentException if {@code direction} is null
     */
    public void setDirection(Direction direction) {
        if (direction == null) {
            throw new IllegalArgumentException("Direction must not be null.");
        }
        this.direction = direction;
    }

    /**
     * Returns whether the physical light can display more than one colour.
     *
     * @return {@code true} if the light is multi-colour
     */
    public boolean isMultiColor() {
        return isMultiColor;
    }
}
