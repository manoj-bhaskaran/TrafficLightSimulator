package com.trafficlightsimulator.config;

/**
 * Validation limits for intersection topology configuration.
 */
public final class IntersectionLimits {
    /** Minimum roads required for an intersection. */
    public static final int MIN_ROADS = 2;

    /** Maximum roads that can connect to an intersection. */
    public static final int MAX_ROADS = 8;

    /**
     * Minimum allowed angular separation, in degrees, between two roads at an
     * intersection.
     */
    public static final double MIN_ANGLE_BETWEEN_ROADS = 30.0;

    private IntersectionLimits() {
        // Utility class: prevent instantiation.
    }
}
