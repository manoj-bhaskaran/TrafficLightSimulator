package com.trafficlightsimulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A group of {@link TrafficLight} instances that are controlled together and
 * share safety incompatibility rules.
 *
 * <p>Lights are compared by identity (not by value) throughout, which allows
 * the same value-equal light configuration to appear at multiple physical
 * locations without unintended interference.
 *
 * <p><strong>Incompatibility rules.</strong> Pairs of lights can be declared
 * mutually incompatible via {@link #addIncompatibleLights(TrafficLight, TrafficLight)}.
 * A light is considered active when its colour is {@link Color#GREEN} and its
 * state is {@link State#ON}. Whenever a managed light is about to be activated
 * through {@link #setLightColor(TrafficLight, Color)} or
 * {@link #setLightState(TrafficLight, State)}, the group checks all registered
 * incompatibilities and throws {@link IllegalStateException} if a conflicting
 * light is already active.
 */
public class TrafficLightGroup {
    private static final Logger logger = Logger.getLogger(TrafficLightGroup.class.getName());
    private final List<TrafficLight> lights;
    private final Map<TrafficLight, Set<TrafficLight>> incompatibleLights;

    /**
     * Creates an empty traffic-light group with no lights and no
     * incompatibility rules.
     */
    public TrafficLightGroup() {
        this.lights = new ArrayList<>();
        this.incompatibleLights = new IdentityHashMap<>();
    }

    /**
     * Adds a light to this group. Null values are silently ignored.
     *
     * @param light light to add; ignored if null
     */
    public void addTrafficLight(TrafficLight light) {
        if (light != null) {
            lights.add(light);
            incompatibleLights.computeIfAbsent(light, ignored -> newIdentitySet());
            logger.log(Level.FINE, "Traffic light added: {0}", light);
        }
    }

    /**
     * Removes a light from this group and clears any incompatibility rules
     * that reference it.
     *
     * @param light light to remove; no-op if null or not in this group
     */
    public void removeTrafficLight(TrafficLight light) {
        lights.remove(light);
        incompatibleLights.remove(light);
        for (Set<TrafficLight> incompatibleSet : incompatibleLights.values()) {
            incompatibleSet.remove(light);
        }
        logger.log(Level.FINE, "Traffic light removed: {0}", light);
    }

    /**
     * Declares two lights as mutually incompatible. Neither light may be
     * activated while the other is already active.
     *
     * <p>The lights do not need to be members of this group at the time of
     * registration; membership is only required when activating via
     * {@link #setLightColor} or {@link #setLightState}.
     *
     * @param firstLight  first light in the pair; must not be null
     * @param secondLight second light in the pair; must not be null and must
     *                    not be the same object as {@code firstLight}
     * @throws IllegalArgumentException if either argument is null, or if both
     *                                  arguments refer to the same object
     */
    public void addIncompatibleLights(TrafficLight firstLight, TrafficLight secondLight) {
        validateIncompatibilityPair(firstLight, secondLight);
        incompatibleLights.computeIfAbsent(firstLight, ignored -> newIdentitySet()).add(secondLight);
        incompatibleLights.computeIfAbsent(secondLight, ignored -> newIdentitySet()).add(firstLight);
        logger.log(Level.FINE, "Configured incompatible lights: {0} <-> {1}",
                new Object[]{firstLight, secondLight});
    }

    /**
     * Removes a previously declared incompatibility between two lights.
     *
     * @param firstLight  first light in the pair; must not be null
     * @param secondLight second light in the pair; must not be null
     * @throws IllegalArgumentException if either argument is null
     */
    public void removeIncompatibleLights(TrafficLight firstLight, TrafficLight secondLight) {
        if (firstLight == null || secondLight == null) {
            throw new IllegalArgumentException("Incompatible lights must not be null.");
        }
        removeIncompatibility(firstLight, secondLight);
        removeIncompatibility(secondLight, firstLight);
        logger.log(Level.FINE, "Removed incompatible lights: {0} <-> {1}",
                new Object[]{firstLight, secondLight});
    }

    /**
     * Returns {@code true} if the two lights have been declared incompatible
     * in this group.
     *
     * @param firstLight  first light; null returns {@code false}
     * @param secondLight second light; null returns {@code false}
     * @return {@code true} if a mutual incompatibility is registered
     */
    public boolean areIncompatible(TrafficLight firstLight, TrafficLight secondLight) {
        if (firstLight == null || secondLight == null) {
            return false;
        }
        return incompatibleLights.getOrDefault(firstLight, Collections.emptySet()).contains(secondLight);
    }

    /**
     * Returns a read-only view of the lights declared incompatible with the
     * given light in this group.
     *
     * @param light light to query; must not be null
     * @return unmodifiable set of incompatible lights; never null
     * @throws IllegalArgumentException if {@code light} is null
     */
    public Set<TrafficLight> getIncompatibleLights(TrafficLight light) {
        if (light == null) {
            throw new IllegalArgumentException("Traffic light must not be null.");
        }
        return Collections.unmodifiableSet(incompatibleLights.getOrDefault(light, Collections.emptySet()));
    }

    /**
     * Changes the colour of a managed light, first verifying that the change
     * does not activate a light that is incompatible with an already-active
     * light.
     *
     * @param light light to update; must not be null and must belong to this
     *              group
     * @param color new colour; must be valid for the light's type
     * @throws IllegalArgumentException if {@code light} is null, does not
     *                                  belong to this group, or if
     *                                  {@code color} is invalid for the type
     * @throws IllegalStateException    if applying the colour would activate
     *                                  an incompatible light pair
     */
    public void setLightColor(TrafficLight light, Color color) {
        validateManagedLight(light);
        ensureCompatibleActivation(light, color, light.getState());
        light.setColor(color);
        logger.log(Level.FINE, "Set color {0} for light: {1}", new Object[]{color, light});
    }

    /**
     * Changes the state of a managed light, first verifying that the change
     * does not activate a light that is incompatible with an already-active
     * light.
     *
     * @param light light to update; must not be null and must belong to this
     *              group
     * @param state new state; must not be null
     * @throws IllegalArgumentException if {@code light} is null, does not
     *                                  belong to this group, or if
     *                                  {@code state} is null
     * @throws IllegalStateException    if applying the state would activate an
     *                                  incompatible light pair
     */
    public void setLightState(TrafficLight light, State state) {
        validateManagedLight(light);
        ensureCompatibleActivation(light, light.getColor(), state);
        light.setState(state);
        logger.log(Level.FINE, "Set state {0} for light: {1}", new Object[]{state, light});
    }

    /**
     * Sets every light in this group to the specified colour. Lights that
     * cannot accept the colour (e.g. AMBER on a pedestrian light, or an
     * incompatibility violation) are skipped with a warning log.
     *
     * @param color colour to apply to all lights
     */
    public void setAllLightsColor(Color color) {
        for (TrafficLight light : lights) {
            try {
                setLightColor(light, color);
            } catch (IllegalArgumentException | IllegalStateException e) {
                logger.log(Level.WARNING, "Cannot set color for light: {0}", e.getMessage());
            }
        }
    }

    /**
     * Sets every light in this group to the specified state. Lights that
     * cannot accept the state (e.g. due to an incompatibility violation) are
     * skipped with a warning log.
     *
     * @param state state to apply to all lights
     */
    public void setAllLightsState(State state) {
        for (TrafficLight light : lights) {
            try {
                setLightState(light, state);
            } catch (IllegalArgumentException | IllegalStateException e) {
                logger.log(Level.WARNING, "Cannot set state for light: {0}", e.getMessage());
            }
        }
    }

    /**
     * Returns a read-only view of all lights in this group.
     *
     * @return unmodifiable list of lights; never null
     */
    public List<TrafficLight> getLights() {
        return Collections.unmodifiableList(lights);
    }

    /**
     * Logs the type, colour, and state of every light in this group for
     * diagnostic purposes.
     */
    public void displayGroupStatus() {
        logger.log(Level.FINE, "{0}", this);
        for (TrafficLight light : lights) {
            logger.log(Level.FINE, "Traffic light: {0}", light);
        }
    }

    private void validateIncompatibilityPair(TrafficLight firstLight, TrafficLight secondLight) {
        if (firstLight == null || secondLight == null) {
            throw new IllegalArgumentException("Incompatible lights must not be null.");
        }
        if (firstLight == secondLight) {
            throw new IllegalArgumentException("A traffic light cannot be incompatible with itself.");
        }
    }

    private void validateManagedLight(TrafficLight light) {
        if (light == null) {
            throw new IllegalArgumentException("Traffic light must not be null.");
        }
        if (!lights.contains(light)) {
            throw new IllegalArgumentException("Traffic light must belong to this group before it can be changed.");
        }
    }

    private void ensureCompatibleActivation(TrafficLight light, Color candidateColor, State candidateState) {
        if (!isActive(candidateColor, candidateState)) {
            return;
        }

        for (TrafficLight incompatibleLight : incompatibleLights.getOrDefault(light, Collections.emptySet())) {
            if (isActive(incompatibleLight)) {
                throw new IllegalStateException("Cannot activate " + describe(light)
                        + " because incompatible light " + describe(incompatibleLight)
                        + " is already active.");
            }
        }
    }

    private boolean isActive(TrafficLight light) {
        return light != null && isActive(light.getColor(), light.getState());
    }

    private boolean isActive(Color color, State state) {
        return color == Color.GREEN && state == State.ON;
    }

    private String describe(TrafficLight light) {
        return light.getType() + " light facing " + light.getDirection()
                + " with color " + light.getColor() + " and state " + light.getState();
    }

    private Set<TrafficLight> newIdentitySet() {
        return Collections.newSetFromMap(new IdentityHashMap<>());
    }

    private void removeIncompatibility(TrafficLight firstLight, TrafficLight secondLight) {
        Set<TrafficLight> incompatibleSet = incompatibleLights.get(firstLight);
        if (incompatibleSet != null) {
            incompatibleSet.remove(secondLight);
        }
    }

    /**
     * Returns a compact diagnostic representation of this group.
     *
     * @return readable traffic-light-group summary
     */
    @Override
    public String toString() {
        int incompatibilityCount = incompatibleLights.values().stream()
                .mapToInt(Set::size)
                .sum() / 2;
        return "TrafficLightGroup{"
                + "lightCount=" + lights.size()
                + ", incompatibilityCount=" + incompatibilityCount
                + '}';
    }

    /**
     * Traffic-light groups model physical controller group instances, so
     * equality is intentionally based on object identity.
     *
     * @param obj object to compare
     * @return {@code true} only when both references point to the same group
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    /**
     * Returns an identity-based hash code consistent with {@link #equals(Object)}.
     *
     * @return identity hash code
     */
    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

}
