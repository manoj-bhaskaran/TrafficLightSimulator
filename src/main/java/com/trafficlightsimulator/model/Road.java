package com.trafficlightsimulator.model;

import com.trafficlightsimulator.config.RoadLimits;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Road {
    private static final Logger logger = Logger.getLogger(Road.class.getName());
    
    private final List<Lane> incomingLanes;
    private final List<Lane> outgoingLanes;
    private PedestrianCrossing pedestrianCrossing;
    private TrafficLightGroup incomingLanesTrafficLightGroup;
    private double angle;  // Attribute for road angle
    private int numIncomingLanes; // New attribute for tracking number of incoming lanes
    private int numOutgoingLanes; // New attribute for tracking number of outgoing lanes

    // Constructor with angle and lane counts
    public Road(double angle, int numIncomingLanes, int numOutgoingLanes) {
        this.incomingLanes = new ArrayList<>();
        this.outgoingLanes = new ArrayList<>();
        this.pedestrianCrossing = null;
        this.incomingLanesTrafficLightGroup = new TrafficLightGroup();
        setAngle(angle); // Set and validate the angle
        setNumIncomingLanes(numIncomingLanes); // Initialize incoming lanes
        setNumOutgoingLanes(numOutgoingLanes); // Initialize outgoing lanes
    }

    // Method to set the angle with validation
    public void setAngle(double angle) {
        if (angle < RoadLimits.MIN_ANGLE_DEGREES || angle >= RoadLimits.MAX_ANGLE_DEGREES_EXCLUSIVE) {
            throw new IllegalArgumentException("Angle must be between 0 and 360 degrees.");
        }
        this.angle = angle;
        logger.log(Level.INFO, "Angle for road set to: {0} degrees", angle);
    }

    // Getter for angle
    public double getAngle() {
        return angle;
    }

    // Setter for number of incoming lanes
    public void setNumIncomingLanes(int numIncomingLanes) {
        if (numIncomingLanes < RoadLimits.MIN_LANES) {
            throw new IllegalArgumentException("Number of incoming lanes must be at least 1.");
        }
        int current = this.incomingLanes.size();
        if (numIncomingLanes > current) {
            for (int i = current; i < numIncomingLanes; i++) {
                this.incomingLanes.add(new Lane(Lane.Direction.INCOMING));
            }
        } else if (numIncomingLanes < current) {
            this.incomingLanes.subList(numIncomingLanes, current).clear();
        }
        this.numIncomingLanes = numIncomingLanes;
        logger.log(Level.INFO, "Number of incoming lanes set to: {0}", numIncomingLanes);
    }

    // Getter for number of incoming lanes
    public int getNumIncomingLanes() {
        return numIncomingLanes;
    }

    // Setter for number of outgoing lanes
    public void setNumOutgoingLanes(int numOutgoingLanes) {
        if (numOutgoingLanes < RoadLimits.MIN_LANES) {
            throw new IllegalArgumentException("Number of outgoing lanes must be at least 1.");
        }
        int current = this.outgoingLanes.size();
        if (numOutgoingLanes > current) {
            for (int i = current; i < numOutgoingLanes; i++) {
                this.outgoingLanes.add(new Lane(Lane.Direction.OUTGOING));
            }
        } else if (numOutgoingLanes < current) {
            this.outgoingLanes.subList(numOutgoingLanes, current).clear();
        }
        this.numOutgoingLanes = numOutgoingLanes;
        logger.log(Level.INFO, "Number of outgoing lanes set to: {0}", numOutgoingLanes);
    }

    // Getter for number of outgoing lanes
    public int getNumOutgoingLanes() {
        return numOutgoingLanes;
    }

    // Method to add a traffic light to the group for incoming lanes
    public void addTrafficLightToIncomingGroup(TrafficLight light) {
        if (light != null) {
            incomingLanesTrafficLightGroup.addTrafficLight(light);
        }
    }

    // Getter for the traffic light group controlling incoming lanes
    public TrafficLightGroup getIncomingLanesTrafficLightGroup() {
        return incomingLanesTrafficLightGroup;
    }

    // Method to associate a pedestrian crossing with the road
    public void setPedestrianCrossing(PedestrianCrossing pedestrianCrossing) {
        this.pedestrianCrossing = pedestrianCrossing;
        logger.log(Level.INFO, "Pedestrian crossing set for the road: {0}", pedestrianCrossing);
    }

    // Adds a pedestrian crossing to the road; throws if one is already present
    public void addPedestrianCrossing(PedestrianCrossing pedestrianCrossing) {
        if (pedestrianCrossing == null) {
            throw new IllegalArgumentException("Pedestrian crossing cannot be null.");
        }
        if (this.pedestrianCrossing != null) {
            throw new IllegalStateException("A pedestrian crossing is already associated with this road.");
        }
        this.pedestrianCrossing = pedestrianCrossing;
        logger.log(Level.INFO, "Pedestrian crossing added to road: {0}", pedestrianCrossing);
    }

    // Removes the pedestrian crossing from the road; throws if none is present
    public void removePedestrianCrossing() {
        if (this.pedestrianCrossing == null) {
            throw new IllegalStateException("No pedestrian crossing is associated with this road.");
        }
        logger.log(Level.INFO, "Pedestrian crossing removed from road: {0}", this.pedestrianCrossing);
        this.pedestrianCrossing = null;
    }

    // Getter for incoming lanes
    public List<Lane> getIncomingLanes() {
        return Collections.unmodifiableList(incomingLanes);
    }

    // Getter for outgoing lanes
    public List<Lane> getOutgoingLanes() {
        return Collections.unmodifiableList(outgoingLanes);
    }


    // Associate one inbound lane with an allowable outbound lane.
    public void addAllowedTurn(Lane inboundLane, Lane outboundLane) {
        validateInboundLaneMembership(inboundLane);
        validateOutboundLane(outboundLane);
        inboundLane.addAllowedOutgoingLane(outboundLane);
        logger.log(Level.INFO, "Allowed turn configured from inbound lane {0} to outbound lane {1}",
                new Object[] { inboundLane, outboundLane });
    }

    // Replace all allowable outbound lanes for an inbound lane.
    public void setAllowedTurns(Lane inboundLane, Collection<Lane> outboundLanes) {
        validateInboundLaneMembership(inboundLane);
        if (outboundLanes == null) {
            throw new IllegalArgumentException("Allowed outbound lanes must not be null.");
        }
        for (Lane outboundLane : outboundLanes) {
            validateOutboundLane(outboundLane);
        }
        inboundLane.setAllowedOutgoingLanes(outboundLanes);
        logger.log(Level.INFO, "Allowed turns replaced for inbound lane {0}", inboundLane);
    }

    // Associate one inbound lane index with one of this road's outbound lane indexes.
    public void addAllowedTurn(int inboundLaneIndex, int outboundLaneIndex) {
        addAllowedTurn(getIncomingLaneAt(inboundLaneIndex), getOutgoingLaneAt(outboundLaneIndex));
    }

    // Retrieve allowable outbound lanes for a road-owned inbound lane.
    public List<Lane> getAllowedTurns(Lane inboundLane) {
        validateInboundLaneMembership(inboundLane);
        return inboundLane.getAllowedOutgoingLanes();
    }

    // Check whether the configured restrictions permit a turn.
    public boolean isTurnAllowed(Lane inboundLane, Lane outboundLane) {
        validateInboundLaneMembership(inboundLane);
        validateOutboundLane(outboundLane);
        return inboundLane.isAllowedOutgoingLane(outboundLane);
    }

    // Enforce configured restrictions before routing traffic to an outbound lane.
    public void validateTurnAllowed(Lane inboundLane, Lane outboundLane) {
        validateInboundLaneMembership(inboundLane);
        validateOutboundLane(outboundLane);
        inboundLane.validateOutgoingLaneAllowed(outboundLane);
    }

    private Lane getIncomingLaneAt(int inboundLaneIndex) {
        if (inboundLaneIndex < 0 || inboundLaneIndex >= incomingLanes.size()) {
            throw new IndexOutOfBoundsException("Inbound lane index is outside this road's incoming lane range.");
        }
        return incomingLanes.get(inboundLaneIndex);
    }

    private Lane getOutgoingLaneAt(int outboundLaneIndex) {
        if (outboundLaneIndex < 0 || outboundLaneIndex >= outgoingLanes.size()) {
            throw new IndexOutOfBoundsException("Outbound lane index is outside this road's outgoing lane range.");
        }
        return outgoingLanes.get(outboundLaneIndex);
    }

    private void validateInboundLaneMembership(Lane inboundLane) {
        if (inboundLane == null) {
            throw new IllegalArgumentException("Inbound lane must not be null.");
        }
        if (inboundLane.getDirection() != Lane.Direction.INCOMING) {
            throw new IllegalArgumentException("Turn restrictions must start from an inbound lane.");
        }
        if (!incomingLanes.contains(inboundLane)) {
            throw new IllegalArgumentException("Inbound lane must belong to this road.");
        }
    }

    private void validateOutboundLane(Lane outboundLane) {
        if (outboundLane == null) {
            throw new IllegalArgumentException("Outbound lane must not be null.");
        }
        if (outboundLane.getDirection() != Lane.Direction.OUTGOING) {
            throw new IllegalArgumentException("Turn restrictions must target an outbound lane.");
        }
    }

    // Getter for pedestrian crossing
    public PedestrianCrossing getPedestrianCrossing() {
        return pedestrianCrossing;
    }

    // Method to check if the road has a pedestrian crossing
    public boolean hasPedestrianCrossing() {
        return pedestrianCrossing != null;
    }

    // Method to display lane information for traffic flow management (for debugging purposes)
    public void displayLaneInfo() {
        logger.log(Level.INFO, "Incoming Lanes: {0}", incomingLanes.size());
        for (Lane lane : incomingLanes) {
            logger.log(Level.INFO, "Incoming Lane: {0}", lane.getDirection());
        }

        logger.log(Level.INFO, "Outgoing Lanes: {0}", outgoingLanes.size());
        for (Lane lane : outgoingLanes) {
            logger.log(Level.INFO, "Outgoing Lane: {0}", lane.getDirection());
        }

        if (hasPedestrianCrossing()) {
            logger.log(Level.INFO, "Pedestrian Crossing is present on this road.");
        } else {
            logger.log(Level.INFO, "No Pedestrian Crossing on this road.");
        }
    }
}
