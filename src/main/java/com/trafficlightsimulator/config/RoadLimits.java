package com.trafficlightsimulator.config;

/**
 * Validation limits for road geometry and lane configuration.
 */
public final class RoadLimits {
    /** Inclusive lower bound for a road angle in degrees. */
    public static final double MIN_ANGLE_DEGREES = 0.0;

    /** Exclusive upper bound for a road angle in degrees. */
    public static final double MAX_ANGLE_DEGREES_EXCLUSIVE = 360.0;

    /** Minimum number of incoming or outgoing lanes on a road. */
    public static final int MIN_LANES = 1;

    private RoadLimits() {
        // Utility class: prevent instantiation.
    }
}
