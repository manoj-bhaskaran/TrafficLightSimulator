package com.trafficlightsimulator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Lane {
    private static final Logger logger = Logger.getLogger(Lane.class.getName());

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
            logger.log(Level.INFO, "Added allowed outgoing lane: {0}", lane);
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
        logger.log(Level.INFO, "Lane Direction: {0}", direction);
        if (direction == Direction.INCOMING) {
            logger.log(Level.INFO, "Allowed Outgoing Lanes: {0}", allowedOutgoingLanes.size());
            for (Lane outgoingLane : allowedOutgoingLanes) {
                logger.log(Level.INFO, "Allowed Outgoing Lane: {0}", outgoingLane.getDirection());
            }
        }
    }
}
