package com.trafficlightsimulator.config;

/**
 * Shared validation constants for road geometry, lane counts, and intersection
 * topology.
 *
 * <p>Model classes, builders, and compatibility limit holders should reference
 * this single source so range changes stay consistent across construction and
 * mutation paths.
 */
public final class ValidationConstants {
    /** Inclusive lower bound for a road angle in degrees. */
    public static final double MIN_ANGLE_DEGREES = 0.0;

    /** Exclusive upper bound for a road angle in degrees. */
    public static final double MAX_ANGLE_DEGREES_EXCLUSIVE = 360.0;

    /** Minimum number of incoming or outgoing lanes on a road. */
    public static final int MIN_LANES = 1;

    /** Minimum roads required for an intersection. */
    public static final int MIN_ROADS = 2;

    /** Maximum roads that can connect to an intersection. */
    public static final int MAX_ROADS = 8;

    /**
     * Minimum allowed angular separation, in degrees, between two roads at an
     * intersection.
     */
    public static final double MIN_ANGLE_BETWEEN_ROADS = 30.0;

    private ValidationConstants() {
        // Utility class: prevent instantiation.
    }
}
