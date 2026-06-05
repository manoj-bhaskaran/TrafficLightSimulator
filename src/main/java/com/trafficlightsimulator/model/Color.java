package com.trafficlightsimulator.model;

/**
 * Signal colour displayed by a traffic or pedestrian light.
 *
 * <p>Pedestrian lights do not use {@link #AMBER}; that constraint is enforced
 * in {@link TrafficLight#setColor(Color)}.
 */
public enum Color {
    RED, AMBER, GREEN
}
