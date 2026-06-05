package com.trafficlightsimulator.config;

import com.trafficlightsimulator.model.PedestrianCrossing;
import com.trafficlightsimulator.model.Road;
import com.trafficlightsimulator.model.TrafficLight;

import java.util.ArrayList;
import java.util.List;

/**
 * Fluent factory for validated {@link Road} instances.
 *
 * <p>The builder mirrors {@link Road}'s construction-time validation while
 * keeping road assembly readable when optional crossings or traffic lights are
 * configured alongside geometry and lane counts.
 */
public final class RoadBuilder {
    private double angle = ValidationConstants.MIN_ANGLE_DEGREES;
    private int incomingLaneCount = ValidationConstants.MIN_LANES;
    private int outgoingLaneCount = ValidationConstants.MIN_LANES;
    private PedestrianCrossing pedestrianCrossing;
    private final List<TrafficLight> incomingTrafficLights = new ArrayList<>();

    private RoadBuilder() {
        // Use factory methods.
    }

    /**
     * Starts a road builder using valid defaults: angle {@code 0.0}, one
     * incoming lane, and one outgoing lane.
     *
     * @return new road builder
     */
    public static RoadBuilder road() {
        return new RoadBuilder();
    }

    /**
     * Starts a road builder with the required geometry supplied up front.
     *
     * @param angle road angle in degrees
     * @return new road builder
     */
    public static RoadBuilder atAngle(double angle) {
        return road().angle(angle);
    }

    /**
     * Sets the road angle in degrees.
     *
     * @param angle angle in [{@link ValidationConstants#MIN_ANGLE_DEGREES},
     *              {@link ValidationConstants#MAX_ANGLE_DEGREES_EXCLUSIVE})
     * @return this builder
     * @throws IllegalArgumentException if the angle is out of range
     */
    public RoadBuilder angle(double angle) {
        validateAngle(angle);
        this.angle = angle;
        return this;
    }

    /**
     * Sets the number of incoming lanes.
     *
     * @param incomingLaneCount incoming lane count
     * @return this builder
     * @throws IllegalArgumentException if the count is below the minimum
     */
    public RoadBuilder incomingLanes(int incomingLaneCount) {
        validateLaneCount(incomingLaneCount, "incoming");
        this.incomingLaneCount = incomingLaneCount;
        return this;
    }

    /**
     * Sets the number of outgoing lanes.
     *
     * @param outgoingLaneCount outgoing lane count
     * @return this builder
     * @throws IllegalArgumentException if the count is below the minimum
     */
    public RoadBuilder outgoingLanes(int outgoingLaneCount) {
        validateLaneCount(outgoingLaneCount, "outgoing");
        this.outgoingLaneCount = outgoingLaneCount;
        return this;
    }

    /**
     * Sets incoming and outgoing lane counts together.
     *
     * @param incomingLaneCount incoming lane count
     * @param outgoingLaneCount outgoing lane count
     * @return this builder
     */
    public RoadBuilder lanes(int incomingLaneCount, int outgoingLaneCount) {
        return incomingLanes(incomingLaneCount).outgoingLanes(outgoingLaneCount);
    }

    /**
     * Associates a pedestrian crossing with the road being built.
     *
     * @param pedestrianCrossing crossing to attach; must not be null
     * @return this builder
     * @throws IllegalArgumentException if the crossing is null
     */
    public RoadBuilder pedestrianCrossing(PedestrianCrossing pedestrianCrossing) {
        if (pedestrianCrossing == null) {
            throw new IllegalArgumentException("Pedestrian crossing cannot be null.");
        }
        this.pedestrianCrossing = pedestrianCrossing;
        return this;
    }

    /**
     * Adds a traffic light to the road's incoming-lane light group.
     *
     * @param trafficLight traffic light to add; must not be null
     * @return this builder
     * @throws IllegalArgumentException if the light is null
     */
    public RoadBuilder addIncomingTrafficLight(TrafficLight trafficLight) {
        if (trafficLight == null) {
            throw new IllegalArgumentException("Traffic light cannot be null.");
        }
        incomingTrafficLights.add(trafficLight);
        return this;
    }

    /**
     * Builds a validated road and applies optional crossing/light configuration.
     *
     * @return configured road
     */
    public Road build() {
        Road road = new Road(angle, incomingLaneCount, outgoingLaneCount);
        if (pedestrianCrossing != null) {
            road.addPedestrianCrossing(pedestrianCrossing);
        }
        for (TrafficLight trafficLight : incomingTrafficLights) {
            road.addTrafficLightToIncomingGroup(trafficLight);
        }
        return road;
    }

    private void validateAngle(double angle) {
        if (angle < ValidationConstants.MIN_ANGLE_DEGREES
                || angle >= ValidationConstants.MAX_ANGLE_DEGREES_EXCLUSIVE) {
            throw new IllegalArgumentException("Angle must be between "
                    + ValidationConstants.MIN_ANGLE_DEGREES + " and "
                    + ValidationConstants.MAX_ANGLE_DEGREES_EXCLUSIVE + " degrees.");
        }
    }

    private void validateLaneCount(int laneCount, String laneType) {
        if (laneCount < ValidationConstants.MIN_LANES) {
            throw new IllegalArgumentException("Number of " + laneType + " lanes must be at least "
                    + ValidationConstants.MIN_LANES + ".");
        }
    }
}
