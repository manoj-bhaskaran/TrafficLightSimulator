package com.trafficlightsimulator.config;

import com.trafficlightsimulator.model.Color;
import com.trafficlightsimulator.model.Direction;
import com.trafficlightsimulator.model.PedestrianCrossing;
import com.trafficlightsimulator.model.Road;
import com.trafficlightsimulator.model.State;
import com.trafficlightsimulator.model.TrafficLight;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoadBuilderTest {

    @Test
    void build_createsRoadWithConfiguredGeometryAndOptionalComponents() {
        PedestrianCrossing crossing = new PedestrianCrossing();
        TrafficLight light = new TrafficLight(Color.GREEN, State.ON, TrafficLight.Type.TRAFFIC,
                Direction.STRAIGHT, true);

        Road road = RoadBuilder.atAngle(90.0)
                .lanes(2, 3)
                .pedestrianCrossing(crossing)
                .addIncomingTrafficLight(light)
                .build();

        assertEquals(90.0, road.getAngle());
        assertEquals(2, road.getIncomingLanes().size());
        assertEquals(3, road.getOutgoingLanes().size());
        assertSame(crossing, road.getPedestrianCrossing());
        assertTrue(road.getIncomingLanesTrafficLightGroup().getLights().contains(light));
    }

    @Test
    void angle_rejectsOutOfRangeValuesBeforeBuild() {
        RoadBuilder builder = RoadBuilder.road();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.angle(ValidationConstants.MAX_ANGLE_DEGREES_EXCLUSIVE));

        assertTrue(exception.getMessage().contains("Angle must be between"));
    }

    @Test
    void incomingLanes_rejectsCountsBelowSharedMinimum() {
        RoadBuilder builder = RoadBuilder.road();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.incomingLanes(ValidationConstants.MIN_LANES - 1));

        assertTrue(exception.getMessage().contains(String.valueOf(ValidationConstants.MIN_LANES)));
    }

    @Test
    void optionalComponentsRejectNulls() {
        RoadBuilder builder = RoadBuilder.road();

        assertThrows(IllegalArgumentException.class, () -> builder.pedestrianCrossing(null));
        assertThrows(IllegalArgumentException.class, () -> builder.addIncomingTrafficLight(null));
    }
}
