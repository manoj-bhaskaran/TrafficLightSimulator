package com.trafficlightsimulator.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Lane {
    private static final Logger logger = Logger.getLogger(Lane.class.getName());

    public enum Direction {
        INCOMING, OUTGOING
    }

    private final Direction direction;
    private final List<Lane> allowedOutgoingLanes;

    // Constructor
    public Lane(Direction direction) {
        if (direction == null) {
            throw new IllegalArgumentException("Lane direction must not be null.");
        }
        this.direction = direction;
        this.allowedOutgoingLanes = new ArrayList<>();
    }

    // Method to add an allowed outgoing lane (only applicable for incoming lanes)
    public void addAllowedOutgoingLane(Lane lane) {
        validateAllowedOutgoingLane(lane);
        if (!allowedOutgoingLanes.contains(lane)) {
            allowedOutgoingLanes.add(lane);
            logger.log(Level.INFO, "Added allowed outgoing lane: {0}", lane);
        }
    }

    // Method to replace all allowed outgoing lanes for this inbound lane
    public void setAllowedOutgoingLanes(Collection<Lane> lanes) {
        if (lanes == null) {
            throw new IllegalArgumentException("Allowed outgoing lanes must not be null.");
        }
        List<Lane> replacementLanes = new ArrayList<>(lanes);
        for (Lane lane : replacementLanes) {
            validateAllowedOutgoingLane(lane);
        }

        allowedOutgoingLanes.clear();
        for (Lane lane : replacementLanes) {
            addAllowedOutgoingLane(lane);
        }
    }

    // Getter for direction
    public Direction getDirection() {
        return direction;
    }

    // Getter for allowed outgoing lanes
    public List<Lane> getAllowedOutgoingLanes() {
        return Collections.unmodifiableList(allowedOutgoingLanes);
    }

    // Utility method to check if a lane is an allowed outgoing lane
    public boolean isAllowedOutgoingLane(Lane lane) {
        return allowedOutgoingLanes.contains(lane);
    }

    // Method to enforce turn restrictions before traffic is routed to an outgoing lane
    public void validateOutgoingLaneAllowed(Lane lane) {
        if (lane == null) {
            throw new IllegalArgumentException("Outgoing lane must not be null.");
        }
        if (direction != Direction.INCOMING) {
            throw new IllegalStateException("Only incoming lanes can validate outgoing turn restrictions.");
        }
        if (!isAllowedOutgoingLane(lane)) {
            throw new IllegalStateException("Illegal turn: outbound lane is not permitted for this inbound lane.");
        }
    }

    private void validateAllowedOutgoingLane(Lane lane) {
        if (lane == null) {
            throw new IllegalArgumentException("Allowed outgoing lane must not be null.");
        }
        if (this.direction != Direction.INCOMING || lane.direction != Direction.OUTGOING) {
            throw new IllegalArgumentException("Only incoming lanes can have allowed outgoing lanes, and only outgoing lanes can be added.");
        }
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
