package com.trafficlightsimulator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Road {
    private static final Logger logger = Logger.getLogger(Road.class.getName());
    
    private List<Lane> incomingLanes;
    private List<Lane> outgoingLanes;
    private PedestrianCrossing pedestrianCrossing;

    // Constructor
    public Road() {
        this.incomingLanes = new ArrayList<>();
        this.outgoingLanes = new ArrayList<>();
        this.pedestrianCrossing = null;
    }

    // Method to add an incoming lane
    public void addIncomingLane(Lane lane) {
        if (lane != null) {
            incomingLanes.add(lane);
            logger.log(Level.INFO, "Added incoming lane: {0}", lane);
        }
    }

    // Method to add an outgoing lane
    public void addOutgoingLane(Lane lane) {
        if (lane != null) {
            outgoingLanes.add(lane);
            logger.log(Level.INFO, "Added outgoing lane: {0}", lane);
        }
    }

    // Method to associate a pedestrian crossing with the road
    public void setPedestrianCrossing(PedestrianCrossing pedestrianCrossing) {
        this.pedestrianCrossing = pedestrianCrossing;
        logger.log(Level.INFO, "Pedestrian crossing set for the road: {0}", pedestrianCrossing);
    }

    // Getter for incoming lanes
    public List<Lane> getIncomingLanes() {
        return incomingLanes;
    }

    // Getter for outgoing lanes
    public List<Lane> getOutgoingLanes() {
        return outgoingLanes;
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
