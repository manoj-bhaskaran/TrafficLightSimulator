package com.trafficlightsimulator.model;

import java.util.ArrayList;
import java.util.List;

public class Lane {
    public enum Direction {
        INCOMING, OUTGOING
    }

    private final Direction direction;
    private List<Lane> allowedOutgoingLanes;

    // Constructor
    public Lane(Direction direction) {
        this.direction = direction;
        this.allowedOutgoingLanes = new ArrayList<>();
    }

    // Method to add an allowed outgoing lane (only applicable for incoming lanes)
    public void addAllowedOutgoingLane(Lane lane) {
        if (this.direction == Direction.INCOMING && lane.direction == Direction.OUTGOING) {
            allowedOutgoingLanes.add(lane);
        } else {
            throw new IllegalArgumentException("Only incoming lanes can have allowed outgoing lanes, and only outgoing lanes can be added.");
        }
    }

    // Getter for direction
    public Direction getDirection() {
        return direction;
    }

    // Getter for allowed outgoing lanes
    public List<Lane> getAllowedOutgoingLanes() {
        return allowedOutgoingLanes;
    }

    // Utility method to check if a lane is an allowed outgoing lane
    public boolean isAllowedOutgoingLane(Lane lane) {
        return allowedOutgoingLanes.contains(lane);
    }

    // Method to display lane information (for debugging purposes)
    public void displayLaneInfo() {
        System.out.println("Lane Direction: " + direction);
        if (direction == Direction.INCOMING) {
            System.out.println("Allowed Outgoing Lanes: " + allowedOutgoingLanes.size());
            for (Lane outgoingLane : allowedOutgoingLanes) {
                System.out.println("Allowed Outgoing Lane: " + outgoingLane.getDirection());
            }
        }
    }
}
