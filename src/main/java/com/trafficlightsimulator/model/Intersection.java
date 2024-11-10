package com.trafficlightsimulator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Intersection {
    private static final Logger logger = Logger.getLogger(Intersection.class.getName());
    
    private List<Road> roads;

    // Constructor
    public Intersection() {
        this.roads = new ArrayList<>();
    }

    // Method to add a road to the intersection
    public void addRoad(Road road) {
        if (road != null) {
            roads.add(road);
            logger.log(Level.INFO, "Added road to intersection: {0}", road);
        }
    }

    // Method to retrieve all roads in the intersection
    public List<Road> getRoads() {
        return roads;
    }

    // Method to retrieve all lanes in the intersection (both incoming and outgoing)
    public List<Lane> getAllLanes() {
        List<Lane> allLanes = new ArrayList<>();
        for (Road road : roads) {
            allLanes.addAll(road.getIncomingLanes());
            allLanes.addAll(road.getOutgoingLanes());
        }
        return allLanes;
    }

    // Method to retrieve all pedestrian crossings in the intersection
    public List<PedestrianCrossing> getAllPedestrianCrossings() {
        List<PedestrianCrossing> pedestrianCrossings = new ArrayList<>();
        for (Road road : roads) {
            if (road.hasPedestrianCrossing()) {
                pedestrianCrossings.add(road.getPedestrianCrossing());
            }
        }
        return pedestrianCrossings;
    }

    // Method to initialize all traffic light groups in the intersection
    public void initializeTrafficLightGroups() {
        for (Road road : roads) {
            TrafficLightGroup lightGroup = road.getIncomingLanesTrafficLightGroup();
            if (lightGroup != null) {
                lightGroup.setAllLightsState(State.OFF);  // Example default state
                logger.log(Level.INFO, "Initialized traffic light group for road: {0}", road);
            }
        }
    }

    // Method to initialize all pedestrian light groups in the intersection
    public void initializePedestrianLightGroups() {
        for (PedestrianCrossing crossing : getAllPedestrianCrossings()) {
            TrafficLightGroup pedestrianLightGroup = crossing.getPedestrianLightGroup();
            if (pedestrianLightGroup != null) {
                pedestrianLightGroup.setAllLightsState(State.OFF);  // Example default state
                logger.log(Level.INFO, "Initialized pedestrian light group for crossing: {0}", crossing);
            }
        }
    }

    // Method to display the status of the intersection (for debugging purposes)
    public void displayIntersectionStatus() {
        logger.log(Level.INFO, "Intersection Status:");
        for (Road road : roads) {
            logger.log(Level.INFO, "Road:");
            road.displayLaneInfo();

            if (road.hasPedestrianCrossing()) {
                logger.log(Level.INFO, "Pedestrian Crossing:");
                road.getPedestrianCrossing().displayCrossingStatus();
            }
        }
    }
}
