package com.trafficlightsimulator.config;

import com.trafficlightsimulator.model.Intersection;
import com.trafficlightsimulator.model.Road;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Fluent factory for validated {@link Intersection} instances.
 *
 * <p>Road-count bounds are checked when the capacity is configured, and road
 * capacity plus angle-spacing rules are validated before {@link #build()} adds
 * any collected road to the intersection.
 */
public final class IntersectionBuilder {
    private Integer roadCapacity;
    private final List<Road> roads = new ArrayList<>();

    private IntersectionBuilder() {
        // Use factory methods.
    }

    /**
     * Starts an intersection builder with no roads configured yet.
     *
     * @return new intersection builder
     */
    public static IntersectionBuilder intersection() {
        return new IntersectionBuilder();
    }

    /**
     * Starts an intersection builder with the declared road capacity.
     *
     * @param roadCapacity expected number of roads
     * @return new intersection builder
     */
    public static IntersectionBuilder withRoadCapacity(int roadCapacity) {
        return intersection().roadCapacity(roadCapacity);
    }

    /**
     * Sets the declared road capacity.
     *
     * @param roadCapacity expected number of roads; must be within shared bounds
     * @return this builder
     * @throws IllegalArgumentException if the capacity is out of range or lower
     *                                  than roads already added to this builder
     */
    public IntersectionBuilder roadCapacity(int roadCapacity) {
        validateRoadCapacity(roadCapacity);
        if (roadCapacity < roads.size()) {
            throw new IllegalArgumentException(
                    "Road capacity cannot be less than the number of roads already added: " + roads.size());
        }
        this.roadCapacity = roadCapacity;
        return this;
    }

    /**
     * Adds a road that should belong to the built intersection.
     *
     * @param road road to add; must not be null
     * @return this builder
     * @throws IllegalArgumentException if the road is null or already added to
     *                                  this builder
     * @throws IllegalStateException    if the configured capacity is already full
     */
    public IntersectionBuilder addRoad(Road road) {
        if (road == null) {
            throw new IllegalArgumentException("Road cannot be null.");
        }
        if (roads.contains(road)) {
            throw new IllegalArgumentException("Road is already added to this builder.");
        }
        if (roadCapacity != null && roads.size() >= roadCapacity) {
            throw new IllegalStateException("Cannot add more roads than the specified number: " + roadCapacity);
        }
        roads.add(road);
        return this;
    }

    /**
     * Adds all roads from the supplied collection in iteration order.
     *
     * @param roads roads to add; must not be null and must not contain nulls
     * @return this builder
     */
    public IntersectionBuilder roads(Collection<Road> roads) {
        if (roads == null) {
            throw new IllegalArgumentException("Road collection cannot be null.");
        }
        for (Road road : roads) {
            addRoad(road);
        }
        return this;
    }

    /**
     * Builds an intersection and attaches the configured roads.
     *
     * <p>If no capacity was configured, the builder infers capacity from the
     * supplied road count, while still respecting the minimum road count. This
     * supports both {@code withRoadCapacity(4).build()} for an empty intersection
     * shell and {@code intersection().addRoad(...).addRoad(...).build()} for a
     * fully assembled intersection.
     *
     * @return configured intersection
     * @throws IllegalArgumentException if inferred capacity is out of range, a
     *                                  road is already attached to an intersection,
     *                                  or configured roads violate angle spacing
     * @throws IllegalStateException    if adding roads exceeds capacity
     */
    public Intersection build() {
        int capacity = roadCapacity != null ? roadCapacity : inferRoadCapacity();
        validateRoadCapacity(capacity);
        validateRoadsBeforeAttachment();
        Intersection intersection = new Intersection(capacity);
        for (Road road : roads) {
            intersection.addRoad(road);
        }
        return intersection;
    }

    private int inferRoadCapacity() {
        if (roads.isEmpty()) {
            return ValidationConstants.MIN_ROADS;
        }
        return roads.size();
    }

    private void validateRoadCapacity(int roadCapacity) {
        if (roadCapacity < ValidationConstants.MIN_ROADS || roadCapacity > ValidationConstants.MAX_ROADS) {
            throw new IllegalArgumentException("Number of roads must be between "
                    + ValidationConstants.MIN_ROADS + " and " + ValidationConstants.MAX_ROADS);
        }
    }

    private void validateRoadsBeforeAttachment() {
        for (Road road : roads) {
            if (road.isConnectedToIntersection()) {
                throw new IllegalArgumentException("Road is already connected to an intersection.");
            }
        }
        validateMinimumAngleBetweenConfiguredRoads();
    }

    private void validateMinimumAngleBetweenConfiguredRoads() {
        for (int candidateIndex = 0; candidateIndex < roads.size(); candidateIndex++) {
            Road candidateRoad = roads.get(candidateIndex);
            for (int existingIndex = 0; existingIndex < candidateIndex; existingIndex++) {
                Road existingRoad = roads.get(existingIndex);
                double angleDifference = calculateShortestAngleDifference(candidateRoad.getAngle(),
                        existingRoad.getAngle());
                if (angleDifference < ValidationConstants.MIN_ANGLE_BETWEEN_ROADS) {
                    throw new IllegalArgumentException("Road angle must be at least "
                            + ValidationConstants.MIN_ANGLE_BETWEEN_ROADS
                            + " degrees from every existing road. Candidate angle "
                            + candidateRoad.getAngle() + " is " + angleDifference
                            + " degrees from existing road angle " + existingRoad.getAngle() + ".");
                }
            }
        }
    }

    private double calculateShortestAngleDifference(double firstAngle, double secondAngle) {
        double absoluteDifference = Math.abs(firstAngle - secondAngle);
        return Math.min(absoluteDifference,
                ValidationConstants.MAX_ANGLE_DEGREES_EXCLUSIVE - absoluteDifference);
    }
}
