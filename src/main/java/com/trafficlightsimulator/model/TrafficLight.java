package com.trafficlightsimulator.model;

// TrafficLight class
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
        this.color = color;
        this.state = state;
        this.type = type;
        this.direction = direction;
        this.isMultiColor = isMultiColor;
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
        this.state = state;
    }

    public Type getType() {
        return type;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean isMultiColor() {
        return isMultiColor;
    }
}