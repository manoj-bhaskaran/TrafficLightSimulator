package com.trafficlightsimulator.config;

/**
 * Validation limits for intersection topology configuration.
 */
public final class IntersectionLimits {
    /** Minimum roads required for an intersection. */
    public static final int MIN_ROADS = 2;

    /** Maximum roads that can connect to an intersection. */
    public static final int MAX_ROADS = 8;

    private IntersectionLimits() {
        // Utility class: prevent instantiation.
    }
}
