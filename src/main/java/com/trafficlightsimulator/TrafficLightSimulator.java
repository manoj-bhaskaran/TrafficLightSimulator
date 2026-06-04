package com.trafficlightsimulator;

import com.trafficlightsimulator.model.Color;
import com.trafficlightsimulator.model.Direction;
import com.trafficlightsimulator.model.Intersection;
import com.trafficlightsimulator.model.Road;
import com.trafficlightsimulator.model.State;
import com.trafficlightsimulator.model.TrafficLight;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application entry point for the Traffic Light Simulator.
 */
public final class TrafficLightSimulator {
    private static final Logger logger = Logger.getLogger(TrafficLightSimulator.class.getName());

    private TrafficLightSimulator() {
        // Utility class: prevent instantiation.
    }

    /**
     * Bootstraps a minimal sample intersection so the packaged application can be run.
     *
     * @param args command-line arguments; currently unused
     */
    public static void main(String[] args) {
        Intersection intersection = createSampleIntersection();
        intersection.initializeTrafficLightGroups();

        logger.log(Level.INFO, "Traffic Light Simulator started with {0} roads and {1} total lanes.",
                new Object[]{intersection.getRoads().size(), intersection.getAllLanes().size()});
        intersection.displayIntersectionStatus();
    }

    private static Intersection createSampleIntersection() {
        Intersection intersection = new Intersection(2);
        Road northboundRoad = createRoad(0.0, Color.GREEN, Direction.STRAIGHT);
        Road eastboundRoad = createRoad(90.0, Color.RED, Direction.LEFT);

        intersection.addRoad(northboundRoad);
        intersection.addRoad(eastboundRoad);

        return intersection;
    }

    private static Road createRoad(double angle, Color initialColor, Direction direction) {
        Road road = new Road(angle, 1, 1);
        road.addTrafficLightToIncomingGroup(new TrafficLight(
                initialColor,
                State.ON,
                TrafficLight.Type.TRAFFIC,
                direction,
                true));
        return road;
    }
}
