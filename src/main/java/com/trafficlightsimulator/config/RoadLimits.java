package com.trafficlightsimulator.config;

/**
 * Validation limits for road geometry and lane configuration.
 *
 * @deprecated use {@link ValidationConstants}; retained as a compatibility
 *             facade for callers that referenced the former road-specific
 *             constants holder.
 */
@Deprecated(since = "0.15.0", forRemoval = false)
public final class RoadLimits {
    /** Inclusive lower bound for a road angle in degrees. */
    public static final double MIN_ANGLE_DEGREES = ValidationConstants.MIN_ANGLE_DEGREES;

    /** Exclusive upper bound for a road angle in degrees. */
    public static final double MAX_ANGLE_DEGREES_EXCLUSIVE = ValidationConstants.MAX_ANGLE_DEGREES_EXCLUSIVE;

    /** Minimum number of incoming or outgoing lanes on a road. */
    public static final int MIN_LANES = ValidationConstants.MIN_LANES;

    private RoadLimits() {
        // Utility class: prevent instantiation.
    }
}
