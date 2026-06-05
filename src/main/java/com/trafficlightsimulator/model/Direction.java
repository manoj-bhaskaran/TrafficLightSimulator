package com.trafficlightsimulator.model;

/**
 * Directional arrow shown on a traffic light face.
 *
 * <p>Use {@link #NONE} for lights that display no directional arrow.
 */
public enum Direction {
    STRAIGHT, LEFT, RIGHT,
    /** Indicates a light with no directional arrow. */
    NONE
}
