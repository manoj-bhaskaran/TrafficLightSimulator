package com.trafficlightsimulator.model;

import com.trafficlightsimulator.config.IntersectionLimits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A road intersection that connects two to eight {@link Road} instances.
 *
 * <p>The intersection enforces a declared capacity: callers first set the
 * expected road count and then add roads one by one. Each added road must be
 * at least {@link #MIN_ANGLE_BETWEEN_ROADS} degrees away from every existing
 * road, measured by the shortest circular angle between their headings. Once
 * the intersection reaches capacity, {@link #isIntersectionSetupComplete()}
 * returns {@code true} and no additional roads can be added.
 *
 * <p>Safety invariants for incompatible traffic lights are registered at the
 * intersection level through
 * {@link #configureIncompatibleTrafficLights(TrafficLight, TrafficLight)},
 * which locates the owning {@link TrafficLightGroup} for each light and
 * records the mutual incompatibility in both groups.
 */
public class Intersection {
    private static final Logger logger = Logger.getLogger(Intersection.class.getName());

    /**
     * Minimum allowed angular separation, in degrees, between any two roads
     * connected to this intersection.
     */
    public static final double MIN_ANGLE_BETWEEN_ROADS = IntersectionLimits.MIN_ANGLE_BETWEEN_ROADS;

    private int numberOfRoads;
    private final List<Road> roads;

    /**
     * Creates an intersection with the given road capacity.
     *
     * @param numberOfRoads expected number of roads; must be between
     *                      {@link IntersectionLimits#MIN_ROADS} and
     *                      {@link IntersectionLimits#MAX_ROADS} inclusive
     * @throws IllegalArgumentException if {@code numberOfRoads} is out of range
     */
    public Intersection(int numberOfRoads) {
        this.roads = new ArrayList<>();
        setNumberOfRoads(numberOfRoads);
    }

    /**
     * Updates the declared road capacity for this intersection.
     *
     * <p>The new value must not be less than the number of roads already added,
     * and must remain within the allowed range.
     *
     * @param numberOfRoads new capacity; must be between
     *                      {@link IntersectionLimits#MIN_ROADS} and
     *                      {@link IntersectionLimits#MAX_ROADS} inclusive, and
     *                      must not be less than the roads already added
     * @throws IllegalArgumentException if the value is out of range or would
     *                                  shrink below the current road count
     */
    public void setNumberOfRoads(int numberOfRoads) {
        if (numberOfRoads < IntersectionLimits.MIN_ROADS || numberOfRoads > IntersectionLimits.MAX_ROADS) {
            throw new IllegalArgumentException("Number of roads must be between " + IntersectionLimits.MIN_ROADS + " and " + IntersectionLimits.MAX_ROADS);
        }
        if (roads != null && numberOfRoads < roads.size()) {
            throw new IllegalArgumentException(
                    "Number of roads cannot be less than the number of roads already added: " + roads.size());
        }
        this.numberOfRoads = numberOfRoads;
        logger.log(Level.FINE, "Number of roads set to: {0}", numberOfRoads);
    }

    /**
     * Adds a road to this intersection.
     *
     * @param road road to add; must not be null
     * @throws IllegalArgumentException if {@code road} is null or is closer
     *                                  than {@link #MIN_ANGLE_BETWEEN_ROADS}
     *                                  degrees to an existing road
     * @throws IllegalStateException    if the intersection already contains the
     *                                  declared number of roads
     */
    public void addRoad(Road road) {
        if (road == null) {
            throw new IllegalArgumentException("Road cannot be null");
        }
        if (roads.size() >= numberOfRoads) {
            throw new IllegalStateException("Cannot add more roads than the specified number: " + numberOfRoads);
        }
        validateMinimumAngleBetweenRoads(road);
        roads.add(road);
        logger.log(Level.FINE, "Added road to intersection. Total roads now: {0}", roads.size());
    }

    /**
     * Returns a read-only view of all roads in this intersection.
     *
     * @return unmodifiable list of roads; never null
     */
    public List<Road> getRoads() {
        return Collections.unmodifiableList(roads);
    }

    /**
     * Returns all lanes across every road in this intersection, incoming and
     * outgoing combined.
     *
     * @return new mutable list containing all lanes; never null
     */
    public List<Lane> getAllLanes() {
        List<Lane> allLanes = new ArrayList<>();
        for (Road road : roads) {
            allLanes.addAll(road.getIncomingLanes());
            allLanes.addAll(road.getOutgoingLanes());
        }
        return allLanes;
    }

    /**
     * Returns all pedestrian crossings attached to roads in this intersection.
     *
     * @return new mutable list of crossings; never null
     */
    public List<PedestrianCrossing> getAllPedestrianCrossings() {
        List<PedestrianCrossing> pedestrianCrossings = new ArrayList<>();
        for (Road road : roads) {
            if (road.hasPedestrianCrossing()) {
                pedestrianCrossings.add(road.getPedestrianCrossing());
            }
        }
        return pedestrianCrossings;
    }

    /**
     * Registers two traffic lights in this intersection as mutually
     * incompatible so neither can be set to GREEN+ON while the other is
     * already active.
     *
     * <p>Both lights must already belong to a group managed by this
     * intersection. The incompatibility is recorded in each light's owning
     * group, so activation checks in either group will catch the conflict.
     *
     * @param firstLight  first light in the incompatible pair; must not be null
     *                    and must belong to this intersection
     * @param secondLight second light in the incompatible pair; must not be
     *                    null and must belong to this intersection
     * @throws IllegalArgumentException if either light is null or does not
     *                                  belong to this intersection
     */
    public void configureIncompatibleTrafficLights(TrafficLight firstLight, TrafficLight secondLight) {
        TrafficLightGroup firstGroup = findTrafficLightGroup(firstLight);
        TrafficLightGroup secondGroup = findTrafficLightGroup(secondLight);

        if (firstGroup == null || secondGroup == null) {
            throw new IllegalArgumentException("Both traffic lights must belong to this intersection before incompatibilities can be configured.");
        }

        firstGroup.addIncompatibleLights(firstLight, secondLight);
        if (secondGroup != firstGroup) {
            secondGroup.addIncompatibleLights(secondLight, firstLight);
        }
        logger.log(Level.FINE, "Configured incompatible traffic lights for intersection: {0} <-> {1}",
                new Object[]{firstLight, secondLight});
    }

    /**
     * Returns all {@link TrafficLightGroup} instances managed by this
     * intersection, covering both vehicle and pedestrian signals.
     *
     * @return new mutable list of groups; never null
     */
    public List<TrafficLightGroup> getAllTrafficLightGroups() {
        List<TrafficLightGroup> trafficLightGroups = new ArrayList<>();
        for (Road road : roads) {
            TrafficLightGroup trafficLightGroup = road.getIncomingLanesTrafficLightGroup();
            if (trafficLightGroup != null) {
                trafficLightGroups.add(trafficLightGroup);
            }
            if (road.hasPedestrianCrossing()) {
                TrafficLightGroup pedestrianLightGroup = road.getPedestrianCrossing().getPedestrianLightGroup();
                if (pedestrianLightGroup != null) {
                    trafficLightGroups.add(pedestrianLightGroup);
                }
            }
        }
        return trafficLightGroups;
    }

    /**
     * Sets all vehicle traffic-light groups across every road to the
     * {@link State#OFF} state, establishing a safe initial condition before
     * the simulation begins.
     */
    public void initializeTrafficLightGroups() {
        for (Road road : roads) {
            TrafficLightGroup lightGroup = road.getIncomingLanesTrafficLightGroup();
            if (lightGroup != null) {
                lightGroup.setAllLightsState(State.OFF);
                logger.log(Level.FINE, "Initialized traffic light group for road: {0}", road);
            }
        }
    }

    /**
     * Sets all pedestrian traffic-light groups across every crossing to the
     * {@link State#OFF} state, establishing a safe initial condition before
     * the simulation begins.
     */
    public void initializePedestrianLightGroups() {
        for (PedestrianCrossing crossing : getAllPedestrianCrossings()) {
            TrafficLightGroup pedestrianLightGroup = crossing.getPedestrianLightGroup();
            if (pedestrianLightGroup != null) {
                pedestrianLightGroup.setAllLightsState(State.OFF);
                logger.log(Level.FINE, "Initialized pedestrian light group for crossing: {0}", crossing);
            }
        }
    }

    /**
     * Returns {@code true} when the number of roads added equals the declared
     * road capacity.
     *
     * @return {@code true} if setup is complete
     */
    public boolean isIntersectionSetupComplete() {
        boolean isComplete = roads.size() == numberOfRoads;
        if (isComplete) {
            logger.log(Level.FINE, "Intersection setup is complete with {0} roads.", roads.size());
        } else {
            logger.log(Level.WARNING, "Intersection setup is incomplete. Only {0} of {1} roads added.", new Object[]{roads.size(), numberOfRoads});
        }
        return isComplete;
    }

    /**
     * Logs the current status of every road and pedestrian crossing in this
     * intersection for diagnostic purposes.
     */
    public void displayIntersectionStatus() {
        logger.log(Level.FINE, "{0}", this);
        for (Road road : roads) {
            road.displayLaneInfo();

            if (road.hasPedestrianCrossing()) {
                road.getPedestrianCrossing().displayCrossingStatus();
            }
        }
    }

    private void validateMinimumAngleBetweenRoads(Road candidateRoad) {
        for (Road existingRoad : roads) {
            double angleDifference = calculateShortestAngleDifference(candidateRoad.getAngle(),
                    existingRoad.getAngle());
            if (angleDifference < MIN_ANGLE_BETWEEN_ROADS) {
                throw new IllegalArgumentException("Road angle must be at least " + MIN_ANGLE_BETWEEN_ROADS
                        + " degrees from every existing road. Candidate angle " + candidateRoad.getAngle()
                        + " is " + angleDifference + " degrees from existing road angle "
                        + existingRoad.getAngle() + ".");
            }
        }
    }

    private double calculateShortestAngleDifference(double firstAngle, double secondAngle) {
        double absoluteDifference = Math.abs(firstAngle - secondAngle);
        return Math.min(absoluteDifference, 360.0 - absoluteDifference);
    }

    private TrafficLightGroup findTrafficLightGroup(TrafficLight light) {
        if (light == null) {
            throw new IllegalArgumentException("Traffic light must not be null.");
        }
        for (TrafficLightGroup trafficLightGroup : getAllTrafficLightGroups()) {
            if (trafficLightGroup.getLights().contains(light)) {
                return trafficLightGroup;
            }
        }
        return null;
    }

    /**
     * Returns a compact diagnostic representation of this intersection.
     *
     * @return readable intersection summary
     */
    @Override
    public String toString() {
        return "Intersection{"
                + "configuredRoadCapacity=" + numberOfRoads
                + ", roadCount=" + roads.size()
                + ", setupComplete=" + (roads.size() == numberOfRoads)
                + '}';
    }

    /**
     * Intersections model physical junction instances, so equality is
     * intentionally based on object identity rather than matching road lists.
     *
     * @param obj object to compare
     * @return {@code true} only when both references point to the same intersection
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    /**
     * Returns an identity-based hash code consistent with {@link #equals(Object)}.
     *
     * @return identity hash code
     */
    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

}
