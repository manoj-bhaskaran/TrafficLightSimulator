package com.trafficlightsimulator.model;

/**
 * Traffic-light domain object. Instances intentionally use Java object identity
 * for equality so {@link TrafficLightGroup} incompatibility rules can target
 * specific physical lights.
 */
public class TrafficLight {
    
     public enum Type {
        TRAFFIC,
        PEDESTRIAN
    }
    private Color color;
    private State state;
    private final Type type;
    private Direction direction;
    private final boolean isMultiColor;

    // Constructor
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

    // Getters and Setters
    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        if (color == null) {
            throw new IllegalArgumentException("Color must not be null.");
        }
        if (type == Type.PEDESTRIAN && color == Color.AMBER) {
            throw new IllegalArgumentException("Pedestrian lights do not support AMBER.");
        }
        this.color = color;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        if (state == null) {
            throw new IllegalArgumentException("State must not be null.");
        }
        this.state = state;
    }

    public Type getType() {
        return type;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        if (direction == null) {
            throw new IllegalArgumentException("Direction must not be null.");
        }
        this.direction = direction;
    }

    public boolean isMultiColor() {
        return isMultiColor;
    }
}