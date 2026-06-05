package com.trafficlightsimulator.model;

import com.trafficlightsimulator.config.ValidationConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A road that connects to an {@link Intersection}.
 *
 * <p>A road has an angle in degrees (measured clockwise from north), a set of
 * incoming lanes (towards the intersection), a set of outgoing lanes (away
 * from the intersection), an optional {@link PedestrianCrossing}, and a
 * {@link TrafficLightGroup} that controls the incoming lanes.
 *
 * <p>Turn restrictions between incoming and outgoing lanes are managed through
 * the {@code addAllowedTurn}, {@code setAllowedTurns}, {@code getAllowedTurns},
 * {@code isTurnAllowed}, and {@code validateTurnAllowed} methods. All of these
 * validate that both lanes belong to this road before delegating to
 * {@link Lane}.
 *
 * <p>At most one {@link PedestrianCrossing} may be associated with a road at
 * any time. Use {@link #addPedestrianCrossing(PedestrianCrossing)} to attach
 * one and {@link #removePedestrianCrossing()} to detach it.
 */
public class Road {
    private static final Logger logger = Logger.getLogger(Road.class.getName());

    private final List<Lane> incomingLanes;
    private final List<Lane> outgoingLanes;
    private PedestrianCrossing pedestrianCrossing;
    private TrafficLightGroup incomingLanesTrafficLightGroup;
    private Intersection intersection;
    private double angle;
    private int numIncomingLanes;
    private int numOutgoingLanes;

    /**
     * Creates a road with the given angle and initial lane counts.
     *
     * @param angle            road angle in degrees, measured clockwise from
     *                         north; must be in
     *                         [{@link ValidationConstants#MIN_ANGLE_DEGREES},
     *                         {@link ValidationConstants#MAX_ANGLE_DEGREES_EXCLUSIVE})
     * @param numIncomingLanes number of incoming lanes; must be at least
     *                         {@link ValidationConstants#MIN_LANES}
     * @param numOutgoingLanes number of outgoing lanes; must be at least
     *                         {@link ValidationConstants#MIN_LANES}
     * @throws IllegalArgumentException if any argument is out of range
     */
    public Road(double angle, int numIncomingLanes, int numOutgoingLanes) {
        this.incomingLanes = new ArrayList<>();
        this.outgoingLanes = new ArrayList<>();
        this.pedestrianCrossing = null;
        this.incomingLanesTrafficLightGroup = new TrafficLightGroup();
        this.intersection = null;
        setAngle(angle);
        setNumIncomingLanes(numIncomingLanes);
        setNumOutgoingLanes(numOutgoingLanes);
    }

    /**
     * Sets the road angle.
     *
     * @param angle angle in degrees; must be in
     *              [{@link ValidationConstants#MIN_ANGLE_DEGREES},
     *              {@link ValidationConstants#MAX_ANGLE_DEGREES_EXCLUSIVE})
     * @throws IllegalArgumentException if the angle is out of range or would
     *                                  violate the minimum road-angle spacing
     *                                  required by the owning intersection
     */
    public void setAngle(double angle) {
        validateAngleRange(angle);
        if (intersection != null) {
            intersection.validateRoadAngleChange(this, angle);
        }
        this.angle = angle;
        logger.log(Level.FINE, "Angle for road set to: {0} degrees", angle);
    }

    /**
     * Returns the road angle in degrees.
     *
     * @return angle in [{@link ValidationConstants#MIN_ANGLE_DEGREES},
     *         {@link ValidationConstants#MAX_ANGLE_DEGREES_EXCLUSIVE})
     */
    public double getAngle() {
        return angle;
    }

    /**
     * Adjusts the number of incoming lanes, creating or removing
     * {@link Lane} instances as needed.
     *
     * @param numIncomingLanes desired lane count; must be at least
     *                         {@link ValidationConstants#MIN_LANES}
     * @throws IllegalArgumentException if the count is less than the minimum
     */
    public void setNumIncomingLanes(int numIncomingLanes) {
        if (numIncomingLanes < ValidationConstants.MIN_LANES) {
            throw new IllegalArgumentException("Number of incoming lanes must be at least " + ValidationConstants.MIN_LANES + ".");
        }
        int current = this.incomingLanes.size();
        if (numIncomingLanes > current) {
            for (int i = current; i < numIncomingLanes; i++) {
                this.incomingLanes.add(new Lane(Lane.Direction.INCOMING));
            }
        } else if (numIncomingLanes < current) {
            this.incomingLanes.subList(numIncomingLanes, current).clear();
        }
        this.numIncomingLanes = numIncomingLanes;
        logger.log(Level.FINE, "Number of incoming lanes set to: {0}", numIncomingLanes);
    }

    /**
     * Returns the current number of incoming lanes.
     *
     * @return incoming lane count
     */
    public int getNumIncomingLanes() {
        return numIncomingLanes;
    }

    /**
     * Adjusts the number of outgoing lanes, creating or removing
     * {@link Lane} instances as needed.
     *
     * @param numOutgoingLanes desired lane count; must be at least
     *                         {@link ValidationConstants#MIN_LANES}
     * @throws IllegalArgumentException if the count is less than the minimum
     */
    public void setNumOutgoingLanes(int numOutgoingLanes) {
        if (numOutgoingLanes < ValidationConstants.MIN_LANES) {
            throw new IllegalArgumentException("Number of outgoing lanes must be at least " + ValidationConstants.MIN_LANES + ".");
        }
        int current = this.outgoingLanes.size();
        if (numOutgoingLanes > current) {
            for (int i = current; i < numOutgoingLanes; i++) {
                this.outgoingLanes.add(new Lane(Lane.Direction.OUTGOING));
            }
        } else if (numOutgoingLanes < current) {
            this.outgoingLanes.subList(numOutgoingLanes, current).clear();
        }
        this.numOutgoingLanes = numOutgoingLanes;
        logger.log(Level.FINE, "Number of outgoing lanes set to: {0}", numOutgoingLanes);
    }

    /**
     * Returns the current number of outgoing lanes.
     *
     * @return outgoing lane count
     */
    public int getNumOutgoingLanes() {
        return numOutgoingLanes;
    }

    /**
     * Adds a traffic light to the group that controls this road's incoming
     * lanes. Null values are silently ignored.
     *
     * @param light light to add; ignored if null
     */
    public void addTrafficLightToIncomingGroup(TrafficLight light) {
        if (light != null) {
            incomingLanesTrafficLightGroup.addTrafficLight(light);
        }
    }

    /**
     * Returns the {@link TrafficLightGroup} that controls this road's
     * incoming lanes.
     *
     * @return incoming-lanes light group; never null
     */
    public TrafficLightGroup getIncomingLanesTrafficLightGroup() {
        return incomingLanesTrafficLightGroup;
    }

    /**
     * Replaces the pedestrian crossing associated with this road, or clears
     * it when {@code null} is passed.
     *
     * <p>Prefer {@link #addPedestrianCrossing(PedestrianCrossing)} when the
     * invariant that at most one crossing is present must be enforced.
     *
     * @param pedestrianCrossing crossing to associate, or null to clear
     */
    public void setPedestrianCrossing(PedestrianCrossing pedestrianCrossing) {
        this.pedestrianCrossing = pedestrianCrossing;
        logger.log(Level.FINE, "Pedestrian crossing set for the road: {0}", pedestrianCrossing);
    }

    /**
     * Associates a pedestrian crossing with this road.
     *
     * @param pedestrianCrossing crossing to associate; must not be null
     * @throws IllegalArgumentException if {@code pedestrianCrossing} is null
     * @throws IllegalStateException    if this road already has a crossing
     */
    public void addPedestrianCrossing(PedestrianCrossing pedestrianCrossing) {
        if (pedestrianCrossing == null) {
            throw new IllegalArgumentException("Pedestrian crossing cannot be null.");
        }
        if (this.pedestrianCrossing != null) {
            throw new IllegalStateException("A pedestrian crossing is already associated with this road.");
        }
        this.pedestrianCrossing = pedestrianCrossing;
        logger.log(Level.FINE, "Pedestrian crossing added to road: {0}", pedestrianCrossing);
    }

    /**
     * Removes the pedestrian crossing from this road.
     *
     * @throws IllegalStateException if this road has no crossing to remove
     */
    public void removePedestrianCrossing() {
        if (this.pedestrianCrossing == null) {
            throw new IllegalStateException("No pedestrian crossing is associated with this road.");
        }
        logger.log(Level.FINE, "Pedestrian crossing removed from road: {0}", this.pedestrianCrossing);
        this.pedestrianCrossing = null;
    }

    /**
     * Returns a read-only view of the incoming lanes on this road.
     *
     * @return unmodifiable list of incoming lanes; never null
     */
    public List<Lane> getIncomingLanes() {
        return Collections.unmodifiableList(incomingLanes);
    }

    /**
     * Returns a read-only view of the outgoing lanes on this road.
     *
     * @return unmodifiable list of outgoing lanes; never null
     */
    public List<Lane> getOutgoingLanes() {
        return Collections.unmodifiableList(outgoingLanes);
    }

    /**
     * Permits traffic to turn from an inbound lane to an outbound lane.
     * Both lanes must belong to this road.
     *
     * @param inboundLane  incoming lane; must not be null and must belong to
     *                     this road
     * @param outboundLane outgoing lane to permit; must not be null and must
     *                     belong to this road
     * @throws IllegalArgumentException if either argument is null or does not
     *                                  belong to this road
     */
    public void addAllowedTurn(Lane inboundLane, Lane outboundLane) {
        validateInboundLaneMembership(inboundLane);
        validateOutboundLane(outboundLane);
        inboundLane.addAllowedOutgoingLane(outboundLane);
        logger.log(Level.FINE, "Allowed turn configured from inbound lane {0} to outbound lane {1}",
                new Object[] { inboundLane, outboundLane });
    }

    /**
     * Atomically replaces all permitted outbound lanes for an inbound lane.
     * Both the inbound lane and every outbound lane must belong to this road.
     *
     * @param inboundLane  incoming lane; must not be null and must belong to
     *                     this road
     * @param outboundLanes new set of permitted outbound lanes; must not be
     *                      null, and each element must belong to this road
     * @throws IllegalArgumentException if any argument is invalid
     */
    public void setAllowedTurns(Lane inboundLane, Collection<Lane> outboundLanes) {
        validateInboundLaneMembership(inboundLane);
        if (outboundLanes == null) {
            throw new IllegalArgumentException("Allowed outbound lanes must not be null.");
        }
        for (Lane outboundLane : outboundLanes) {
            validateOutboundLane(outboundLane);
        }
        inboundLane.setAllowedOutgoingLanes(outboundLanes);
        logger.log(Level.FINE, "Allowed turns replaced for inbound lane {0}", inboundLane);
    }

    /**
     * Permits a turn from an inbound lane by zero-based index to an outbound
     * lane by zero-based index.
     *
     * @param inboundLaneIndex  zero-based index into the incoming lane list
     * @param outboundLaneIndex zero-based index into the outgoing lane list
     * @throws IndexOutOfBoundsException if either index is out of range
     */
    public void addAllowedTurn(int inboundLaneIndex, int outboundLaneIndex) {
        addAllowedTurn(getIncomingLaneAt(inboundLaneIndex), getOutgoingLaneAt(outboundLaneIndex));
    }

    /**
     * Returns the read-only list of outbound lanes permitted for an inbound
     * lane.
     *
     * @param inboundLane incoming lane; must not be null and must belong to
     *                    this road
     * @return permitted outbound lanes; never null
     * @throws IllegalArgumentException if {@code inboundLane} is null or does
     *                                  not belong to this road
     */
    public List<Lane> getAllowedTurns(Lane inboundLane) {
        validateInboundLaneMembership(inboundLane);
        return inboundLane.getAllowedOutgoingLanes();
    }

    /**
     * Returns {@code true} if the configured restrictions permit the turn from
     * {@code inboundLane} to {@code outboundLane}.
     *
     * @param inboundLane  incoming lane; must not be null and must belong to
     *                     this road
     * @param outboundLane outgoing lane; must not be null and must belong to
     *                     this road
     * @return {@code true} if the turn is permitted
     * @throws IllegalArgumentException if either lane is null or does not
     *                                  belong to this road
     */
    public boolean isTurnAllowed(Lane inboundLane, Lane outboundLane) {
        validateInboundLaneMembership(inboundLane);
        validateOutboundLane(outboundLane);
        return inboundLane.isAllowedOutgoingLane(outboundLane);
    }

    /**
     * Enforces the configured turn restrictions, throwing if the turn is not
     * permitted.
     *
     * @param inboundLane  incoming lane; must not be null and must belong to
     *                     this road
     * @param outboundLane outgoing lane; must not be null and must belong to
     *                     this road
     * @throws IllegalArgumentException if either lane is null or does not
     *                                  belong to this road
     * @throws IllegalStateException    if the turn is not in the permitted set
     */
    public void validateTurnAllowed(Lane inboundLane, Lane outboundLane) {
        validateInboundLaneMembership(inboundLane);
        validateOutboundLane(outboundLane);
        inboundLane.validateOutgoingLaneAllowed(outboundLane);
    }

    /**
     * Returns the pedestrian crossing associated with this road, or
     * {@code null} if none is present.
     *
     * @return pedestrian crossing, or null
     */
    public PedestrianCrossing getPedestrianCrossing() {
        return pedestrianCrossing;
    }

    /**
     * Returns {@code true} if a pedestrian crossing is currently associated
     * with this road.
     *
     * @return {@code true} if a crossing is present
     */
    public boolean hasPedestrianCrossing() {
        return pedestrianCrossing != null;
    }

    /**
     * Logs the lane counts and pedestrian crossing presence for diagnostic
     * purposes.
     */
    public void displayLaneInfo() {
        logger.log(Level.FINE, "{0}", this);
        for (Lane lane : incomingLanes) {
            logger.log(Level.FINE, "Incoming lane: {0}", lane);
        }
        for (Lane lane : outgoingLanes) {
            logger.log(Level.FINE, "Outgoing lane: {0}", lane);
        }
        if (hasPedestrianCrossing()) {
            logger.log(Level.FINE, "Pedestrian crossing: {0}", pedestrianCrossing);
        }
    }

    boolean isConnectedToDifferentIntersection(Intersection intersection) {
        return this.intersection != null && this.intersection != intersection;
    }

    void attachToIntersection(Intersection intersection) {
        if (isConnectedToDifferentIntersection(intersection)) {
            throw new IllegalStateException("Road already belongs to another intersection.");
        }
        this.intersection = intersection;
    }

    private void validateAngleRange(double angle) {
        if (angle < ValidationConstants.MIN_ANGLE_DEGREES || angle >= ValidationConstants.MAX_ANGLE_DEGREES_EXCLUSIVE) {
            throw new IllegalArgumentException("Angle must be between " + ValidationConstants.MIN_ANGLE_DEGREES
                    + " and " + ValidationConstants.MAX_ANGLE_DEGREES_EXCLUSIVE + " degrees.");
        }
    }

    private Lane getIncomingLaneAt(int inboundLaneIndex) {
        if (inboundLaneIndex < 0 || inboundLaneIndex >= incomingLanes.size()) {
            throw new IndexOutOfBoundsException("Inbound lane index is outside this road's incoming lane range.");
        }
        return incomingLanes.get(inboundLaneIndex);
    }

    private Lane getOutgoingLaneAt(int outboundLaneIndex) {
        if (outboundLaneIndex < 0 || outboundLaneIndex >= outgoingLanes.size()) {
            throw new IndexOutOfBoundsException("Outbound lane index is outside this road's outgoing lane range.");
        }
        return outgoingLanes.get(outboundLaneIndex);
    }

    private void validateInboundLaneMembership(Lane inboundLane) {
        if (inboundLane == null) {
            throw new IllegalArgumentException("Inbound lane must not be null.");
        }
        if (inboundLane.getDirection() != Lane.Direction.INCOMING) {
            throw new IllegalArgumentException("Turn restrictions must start from an inbound lane.");
        }
        if (!incomingLanes.contains(inboundLane)) {
            throw new IllegalArgumentException("Inbound lane must belong to this road.");
        }
    }

    private void validateOutboundLane(Lane outboundLane) {
        if (outboundLane == null) {
            throw new IllegalArgumentException("Outbound lane must not be null.");
        }
        if (outboundLane.getDirection() != Lane.Direction.OUTGOING) {
            throw new IllegalArgumentException("Turn restrictions must target an outbound lane.");
        }
        if (!outgoingLanes.contains(outboundLane)) {
            throw new IllegalArgumentException("Outbound lane must belong to this road.");
        }
    }

    /**
     * Returns a compact diagnostic representation of this road.
     *
     * @return readable road summary
     */
    @Override
    public String toString() {
        return "Road{"
                + "angle=" + angle
                + ", incomingLaneCount=" + incomingLanes.size()
                + ", outgoingLaneCount=" + outgoingLanes.size()
                + ", hasPedestrianCrossing=" + hasPedestrianCrossing()
                + ", trafficLightCount=" + incomingLanesTrafficLightGroup.getLights().size()
                + '}';
    }

    /**
     * Roads model physical road approaches, so equality is intentionally based
     * on object identity rather than matching angle or lane counts.
     *
     * @param obj object to compare
     * @return {@code true} only when both references point to the same road
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
