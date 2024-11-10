package com.trafficlightsimulator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TrafficLightGroup {
    private static final Logger logger = Logger.getLogger(TrafficLightGroup.class.getName());
    private List<TrafficLight> lights;

    // Constructor
    public TrafficLightGroup() {
        this.lights = new ArrayList<>();
    }

    // Method to add a traffic light to the group
    public void addTrafficLight(TrafficLight light) {
        if (light != null) {
            lights.add(light);
            logger.log(Level.INFO, "Traffic light added: {0}", light);
        }
    }

    // Method to remove a traffic light from the group
    public void removeTrafficLight(TrafficLight light) {
        lights.remove(light);
        logger.log(Level.INFO, "Traffic light removed: {0}", light);
    }

    // Method to set all lights in the group to a specified color (if they support color change)
    public void setAllLightsColor(Color color) {
        for (TrafficLight light : lights) {
            try {
                light.setColor(color);
                logger.log(Level.INFO, "Set color {0} for light: {1}", new Object[]{color, light});
            } catch (IllegalArgumentException e) {
                logger.log(Level.WARNING, "Cannot set color for light: {0}", e.getMessage());
            }
        }
    }

    // Method to set all lights in the group to a specified state
    public void setAllLightsState(State state) {
        for (TrafficLight light : lights) {
            light.setState(state);
            logger.log(Level.INFO, "Set state {0} for light: {1}", new Object[]{state, light});
        }
    }

    // Getter for the list of traffic lights
    public List<TrafficLight> getLights() {
        return lights;
    }

    // Method to display the current state of each light in the group (for debugging)
    public void displayGroupStatus() {
        for (TrafficLight light : lights) {
            logger.log(Level.INFO, "Light Type: {0}, Color: {1}, State: {2}", 
                       new Object[]{light.getType(), light.getColor(), light.getState()});
        }
    }
}
