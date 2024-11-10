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
        if (isMultiColor && (type == Type.TRAFFIC || (type == Type.PEDESTRIAN && (color == Color.RED || color == Color.GREEN)))) {
            this.color = color;
        } else {
            throw new IllegalArgumentException("This traffic light cannot change to the specified color.");
        }
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