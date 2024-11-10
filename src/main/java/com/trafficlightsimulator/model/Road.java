package com.trafficlightsimulator.model;

import java.util.ArrayList;
import java.util.List;

public class Road {
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
        }
    }

    // Method to add an outgoing lane
    public void addOutgoingLane(Lane lane) {
        if (lane != null) {
            outgoingLanes.add(lane);
        }
    }

    // Method to associate a pedestrian crossing with the road
    public void setPedestrianCrossing(PedestrianCrossing pedestrianCrossing) {
        this.pedestrianCrossing = pedestrianCrossing;
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
        System.out.println("Incoming Lanes: " + incomingLanes.size());
        for (Lane lane : incomingLanes) {
            System.out.println("Incoming Lane: " + lane.getDirection());
        }

        System.out.println("Outgoing Lanes: " + outgoingLanes.size());
        for (Lane lane : outgoingLanes) {
            System.out.println("Outgoing Lane: " + lane.getDirection());
        }

        if (hasPedestrianCrossing()) {
            System.out.println("Pedestrian Crossing is present on this road.");
        } else {
            System.out.println("No Pedestrian Crossing on this road.");
        }
    }
}
