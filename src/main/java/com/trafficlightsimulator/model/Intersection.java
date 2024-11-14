package com.trafficlightsimulator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Intersection {
    private static final Logger logger = Logger.getLogger(Intersection.class.getName());
    
    private static final int MIN_ROADS = 2; // Minimum roads required for an intersection
    private static final int MAX_ROADS = 8; // Maximum roads that can connect to an intersection
    private int numberOfRoads;
    private List<Road> roads;

    // Constructor with road count parameter
    public Intersection(int numberOfRoads) {
        this.roads = new ArrayList<>();
        setNumberOfRoads(numberOfRoads);
    }

    // Method to set the number of roads in the intersection with validation
    public void setNumberOfRoads(int numberOfRoads) {
        if (numberOfRoads < MIN_ROADS || numberOfRoads > MAX_ROADS) {
            throw new IllegalArgumentException("Number of roads must be between " + MIN_ROADS + " and " + MAX_ROADS);
        }
        this.numberOfRoads = numberOfRoads;
        logger.log(Level.INFO, "Number of roads set to: {0}", numberOfRoads);
    }

    // Method to add a road to the intersection with validation
    public void addRoad(Road road) {
        if (road == null) {
            throw new IllegalArgumentException("Road cannot be null");
        }
        if (roads.size() >= numberOfRoads) {
            throw new IllegalStateException("Cannot add more roads than the specified number: " + numberOfRoads);
        }
        roads.add(road);
        logger.log(Level.INFO, "Added road to intersection. Total roads now: {0}", roads.size());
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

    // Method to check if the intersection setup is complete
    public boolean isIntersectionSetupComplete() {
        boolean isComplete = roads.size() == numberOfRoads;
        if (isComplete) {
            logger.log(Level.INFO, "Intersection setup is complete with {0} roads.", roads.size());
        } else {
            logger.log(Level.WARNING, "Intersection setup is incomplete. Only {0} of {1} roads added.", new Object[]{roads.size(), numberOfRoads});
        }
        return isComplete;
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
