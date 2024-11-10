package com.trafficlightsimulator.model;

import java.util.ArrayList;
import java.util.List;

public class Intersection {
    private List<Road> roads;

    // Constructor
    public Intersection() {
        this.roads = new ArrayList<>();
    }

    // Method to add a road to the intersection
    public void addRoad(Road road) {
        if (road != null) {
            roads.add(road);
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
                // Set default state or perform any necessary initialization on the light group
                lightGroup.setAllLightsState(State.OFF);  // Example default state
            }
        }
    }

    // Method to initialize all pedestrian light groups in the intersection
    public void initializePedestrianLightGroups() {
        for (PedestrianCrossing crossing : getAllPedestrianCrossings()) {
            TrafficLightGroup pedestrianLightGroup = crossing.getPedestrianLightGroup();
            if (pedestrianLightGroup != null) {
                // Set default state or perform any necessary initialization on the pedestrian light group
                pedestrianLightGroup.setAllLightsState(State.OFF);  // Example default state
            }
        }
    }

    // Method to display the status of the intersection (for debugging purposes)
    public void displayIntersectionStatus() {
        System.out.println("Intersection Status:");
        for (Road road : roads) {
            System.out.println("Road:");
            road.displayLaneInfo();

            if (road.hasPedestrianCrossing()) {
                System.out.println("Pedestrian Crossing:");
                road.getPedestrianCrossing().displayCrossingStatus();
            }
        }
    }
}
