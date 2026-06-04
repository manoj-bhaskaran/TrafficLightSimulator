package com.trafficlightsimulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TrafficLightGroup {
    private static final Logger logger = Logger.getLogger(TrafficLightGroup.class.getName());
    private final List<TrafficLight> lights;
    private final Map<TrafficLight, Set<TrafficLight>> incompatibleLights;

    // Constructor
    public TrafficLightGroup() {
        this.lights = new ArrayList<>();
        this.incompatibleLights = new IdentityHashMap<>();
    }

    // Method to add a traffic light to the group
    public void addTrafficLight(TrafficLight light) {
        if (light != null) {
            lights.add(light);
            incompatibleLights.computeIfAbsent(light, ignored -> newIdentitySet());
            logger.log(Level.INFO, "Traffic light added: {0}", light);
        }
    }

    // Method to remove a traffic light from the group
    public void removeTrafficLight(TrafficLight light) {
        lights.remove(light);
        incompatibleLights.remove(light);
        for (Set<TrafficLight> incompatibleSet : incompatibleLights.values()) {
            incompatibleSet.remove(light);
        }
        logger.log(Level.INFO, "Traffic light removed: {0}", light);
    }

    // Method to configure two lights that cannot be active at the same time
    public void addIncompatibleLights(TrafficLight firstLight, TrafficLight secondLight) {
        validateIncompatibilityPair(firstLight, secondLight);
        incompatibleLights.computeIfAbsent(firstLight, ignored -> newIdentitySet()).add(secondLight);
        incompatibleLights.computeIfAbsent(secondLight, ignored -> newIdentitySet()).add(firstLight);
        logger.log(Level.INFO, "Configured incompatible lights: {0} <-> {1}",
                new Object[]{firstLight, secondLight});
    }

    // Method to remove a configured incompatibility between two lights
    public void removeIncompatibleLights(TrafficLight firstLight, TrafficLight secondLight) {
        if (firstLight == null || secondLight == null) {
            throw new IllegalArgumentException("Incompatible lights must not be null.");
        }
        removeIncompatibility(firstLight, secondLight);
        removeIncompatibility(secondLight, firstLight);
        logger.log(Level.INFO, "Removed incompatible lights: {0} <-> {1}",
                new Object[]{firstLight, secondLight});
    }

    // Method to check if two lights have been configured as incompatible
    public boolean areIncompatible(TrafficLight firstLight, TrafficLight secondLight) {
        if (firstLight == null || secondLight == null) {
            return false;
        }
        return incompatibleLights.getOrDefault(firstLight, Collections.emptySet()).contains(secondLight);
    }

    // Method to retrieve lights that are incompatible with the given light
    public Set<TrafficLight> getIncompatibleLights(TrafficLight light) {
        if (light == null) {
            throw new IllegalArgumentException("Traffic light must not be null.");
        }
        return Collections.unmodifiableSet(incompatibleLights.getOrDefault(light, Collections.emptySet()));
    }

    // Method to set one light to a specified color with safety checks
    public void setLightColor(TrafficLight light, Color color) {
        validateManagedLight(light);
        ensureCompatibleActivation(light, color, light.getState());
        light.setColor(color);
        logger.log(Level.INFO, "Set color {0} for light: {1}", new Object[]{color, light});
    }

    // Method to set one light to a specified state with safety checks
    public void setLightState(TrafficLight light, State state) {
        validateManagedLight(light);
        ensureCompatibleActivation(light, light.getColor(), state);
        light.setState(state);
        logger.log(Level.INFO, "Set state {0} for light: {1}", new Object[]{state, light});
    }

    // Method to set all lights in the group to a specified color (if they support color change)
    public void setAllLightsColor(Color color) {
        for (TrafficLight light : lights) {
            try {
                setLightColor(light, color);
            } catch (IllegalArgumentException | IllegalStateException e) {
                logger.log(Level.WARNING, "Cannot set color for light: {0}", e.getMessage());
            }
        }
    }

    // Method to set all lights in the group to a specified state
    public void setAllLightsState(State state) {
        for (TrafficLight light : lights) {
            try {
                setLightState(light, state);
            } catch (IllegalArgumentException | IllegalStateException e) {
                logger.log(Level.WARNING, "Cannot set state for light: {0}", e.getMessage());
            }
        }
    }

    // Getter for the list of traffic lights
    public List<TrafficLight> getLights() {
        return Collections.unmodifiableList(lights);
    }

    // Method to display the current state of each light in the group (for debugging)
    public void displayGroupStatus() {
        for (TrafficLight light : lights) {
            logger.log(Level.INFO, "Light Type: {0}, Color: {1}, State: {2}",
                       new Object[]{light.getType(), light.getColor(), light.getState()});
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
}
