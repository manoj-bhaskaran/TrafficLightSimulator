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
        if (angle < 0 || angle >= 360) {
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
        this.numIncomingLanes = numIncomingLanes;
        this.incomingLanes.clear();
        for (int i = 0; i < numIncomingLanes; i++) {
            this.incomingLanes.add(new Lane(Lane.Direction.INCOMING));
        }
        logger.log(Level.INFO, "Number of incoming lanes set to: {0}", numIncomingLanes);
    }

    // Getter for number of incoming lanes
    public int getNumIncomingLanes() {
        return numIncomingLanes;
    }

    // Setter for number of outgoing lanes
    public void setNumOutgoingLanes(int numOutgoingLanes) {
        this.numOutgoingLanes = numOutgoingLanes;
        this.outgoingLanes.clear();
        for (int i = 0; i < numOutgoingLanes; i++) {
            this.outgoingLanes.add(new Lane(Lane.Direction.OUTGOING));
        }
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
