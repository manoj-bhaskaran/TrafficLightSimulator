package com.trafficlightsimulator.engine;

import com.trafficlightsimulator.model.Intersection;
import com.trafficlightsimulator.model.PedestrianCrossing;
import com.trafficlightsimulator.model.Road;
import com.trafficlightsimulator.model.State;
import com.trafficlightsimulator.model.TrafficLightGroup;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Engine-layer coordinator for simulation lifecycle operations on an intersection.
 */
public class TrafficLightSimulationEngine {
    private static final Logger logger = Logger.getLogger(TrafficLightSimulationEngine.class.getName());

    private final Intersection intersection;

    /**
     * Creates a simulation engine for the supplied intersection model.
     *
     * @param intersection intersection to coordinate
     */
    public TrafficLightSimulationEngine(Intersection intersection) {
        if (intersection == null) {
            throw new IllegalArgumentException("Intersection must not be null.");
        }
        this.intersection = intersection;
    }

    /**
     * Initializes all road traffic-light groups to the default inactive state.
     */
    public void initializeTrafficLightGroups() {
        for (Road road : intersection.getRoads()) {
            TrafficLightGroup lightGroup = road.getIncomingLanesTrafficLightGroup();
            if (lightGroup != null) {
                lightGroup.setAllLightsState(State.OFF);
                logger.log(Level.FINE, "Initialized traffic light group for road: {0}", road);
            }
        }
    }

    /**
     * Initializes all pedestrian traffic-light groups to the default inactive state.
     */
    public void initializePedestrianLightGroups() {
        for (PedestrianCrossing crossing : intersection.getAllPedestrianCrossings()) {
            TrafficLightGroup pedestrianLightGroup = crossing.getPedestrianLightGroup();
            if (pedestrianLightGroup != null) {
                pedestrianLightGroup.setAllLightsState(State.OFF);
                logger.log(Level.FINE, "Initialized pedestrian light group for crossing: {0}", crossing);
            }
        }
    }

    /**
     * Displays the current intersection status for diagnostics.
     */
    public void displayIntersectionStatus() {
        logger.log(Level.INFO, "Intersection status: {0}", intersection);
        for (Road road : intersection.getRoads()) {
            logger.log(Level.INFO, "Road status: {0}", road);
            road.displayLaneInfo();

            if (road.hasPedestrianCrossing()) {
                logger.log(Level.INFO, "Pedestrian crossing status: {0}", road.getPedestrianCrossing());
                road.getPedestrianCrossing().displayCrossingStatus();
            }
        }
    }
}
