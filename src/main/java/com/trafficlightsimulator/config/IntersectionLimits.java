package com.trafficlightsimulator.config;

/**
 * Validation limits for intersection topology configuration.
 *
 * @deprecated use {@link ValidationConstants}; retained as a compatibility
 *             facade for callers that referenced the former intersection-specific
 *             constants holder.
 */
@Deprecated(since = "0.15.0", forRemoval = false)
public final class IntersectionLimits {
    /** Minimum roads required for an intersection. */
    public static final int MIN_ROADS = ValidationConstants.MIN_ROADS;

    /** Maximum roads that can connect to an intersection. */
    public static final int MAX_ROADS = ValidationConstants.MAX_ROADS;

    /**
     * Minimum allowed angular separation, in degrees, between two roads at an
     * intersection.
     */
    public static final double MIN_ANGLE_BETWEEN_ROADS = ValidationConstants.MIN_ANGLE_BETWEEN_ROADS;

    private IntersectionLimits() {
        // Utility class: prevent instantiation.
    }
}
