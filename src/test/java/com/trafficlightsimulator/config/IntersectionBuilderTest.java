package com.trafficlightsimulator.config;

import com.trafficlightsimulator.model.Intersection;
import com.trafficlightsimulator.model.Road;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntersectionBuilderTest {

    @Test
    void build_createsIntersectionWithConfiguredRoads() {
        Road north = RoadBuilder.atAngle(0.0).build();
        Road east = RoadBuilder.atAngle(90.0).lanes(2, 2).build();

        Intersection intersection = IntersectionBuilder.withRoadCapacity(2)
                .addRoad(north)
                .addRoad(east)
                .build();

        assertEquals(2, intersection.getRoads().size());
        assertSame(north, intersection.getRoads().get(0));
        assertSame(east, intersection.getRoads().get(1));
        assertTrue(intersection.isIntersectionSetupComplete());
    }

    @Test
    void build_infersCapacityFromRoadCountWhenCapacityIsNotConfigured() {
        Road north = RoadBuilder.atAngle(0.0).build();
        Road east = RoadBuilder.atAngle(90.0).build();

        Intersection intersection = IntersectionBuilder.intersection()
                .addRoad(north)
                .addRoad(east)
                .build();

        assertEquals(2, intersection.getRoads().size());
        assertTrue(intersection.isIntersectionSetupComplete());
    }

    @Test
    void roadCapacity_rejectsValuesOutsideSharedBounds() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> IntersectionBuilder.withRoadCapacity(ValidationConstants.MIN_ROADS - 1));

        assertTrue(exception.getMessage().contains(String.valueOf(ValidationConstants.MIN_ROADS)));
    }

    @Test
    void addRoad_rejectsNullAndDuplicateRoads() {
        Road road = RoadBuilder.atAngle(0.0).build();
        IntersectionBuilder builder = IntersectionBuilder.withRoadCapacity(2).addRoad(road);

        assertThrows(IllegalArgumentException.class, () -> builder.addRoad(null));
        assertThrows(IllegalArgumentException.class, () -> builder.addRoad(road));
    }

    @Test
    void build_enforcesIntersectionAngleValidation() {
        Road first = RoadBuilder.atAngle(0.0).build();
        Road tooClose = RoadBuilder.atAngle(ValidationConstants.MIN_ANGLE_BETWEEN_ROADS - 1.0).build();

        IntersectionBuilder builder = IntersectionBuilder.withRoadCapacity(2)
                .addRoad(first)
                .addRoad(tooClose);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertTrue(exception.getMessage().contains("at least " + ValidationConstants.MIN_ANGLE_BETWEEN_ROADS));
    }
}
