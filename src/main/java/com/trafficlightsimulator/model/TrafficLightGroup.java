package com.trafficlightsimulator.model;

import java.util.ArrayList;
import java.util.List;

public class TrafficLightGroup {
    private List<TrafficLight> lights;

    // Constructor
    public TrafficLightGroup() {
        this.lights = new ArrayList<>();
    }

    // Method to add a traffic light to the group
    public void addTrafficLight(TrafficLight light) {
        if (light != null) {
            lights.add(light);
        }
    }

    // Method to remove a traffic light from the group
    public void removeTrafficLight(TrafficLight light) {
        lights.remove(light);
    }

    // Method to set all lights in the group to a specified color (if they support color change)
    public void setAllLightsColor(Color color) {
        for (TrafficLight light : lights) {
            try {
                light.setColor(color);
            } catch (IllegalArgumentException e) {
                System.out.println("Cannot set color for light: " + e.getMessage());
            }
        }
    }

    // Method to set all lights in the group to a specified state
    public void setAllLightsState(State state) {
        for (TrafficLight light : lights) {
            light.setState(state);
        }
    }

    // Getter for the list of traffic lights
    public List<TrafficLight> getLights() {
        return lights;
    }

    // Method to display the current state of each light in the group (for debugging)
    public void displayGroupStatus() {
        for (TrafficLight light : lights) {
            System.out.println("Light Type: " + light.getType() + ", Color: " + light.getColor() + ", State: " + light.getState());
        }
    }
}
