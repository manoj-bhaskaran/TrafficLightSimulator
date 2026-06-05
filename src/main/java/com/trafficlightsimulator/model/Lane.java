package com.trafficlightsimulator.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A single lane on a road, classified as either incoming (towards the
 * intersection) or outgoing (away from the intersection).
 *
 * <p>Incoming lanes maintain a set of permitted outgoing lanes that
 * represent legal turn targets. This set starts empty; callers must
 * explicitly configure allowed turns before routing traffic. Duplicate
 * outgoing lanes are silently ignored so each permitted turn appears only
 * once. Outgoing lanes do not hold turn information.
 */
public class Lane {
    private static final Logger logger = Logger.getLogger(Lane.class.getName());

    /** Flow direction relative to the intersection. */
    public enum Direction {
        INCOMING, OUTGOING
    }

    private final Direction direction;
    private final List<Lane> allowedOutgoingLanes;

    /**
     * Creates a lane with the given flow direction.
     *
     * @param direction flow direction; must not be null
     * @throws IllegalArgumentException if {@code direction} is null
     */
    public Lane(Direction direction) {
        if (direction == null) {
            throw new IllegalArgumentException("Lane direction must not be null.");
        }
        this.direction = direction;
        this.allowedOutgoingLanes = new ArrayList<>();
    }

    /**
     * Adds an outgoing lane to the set of permitted turn targets for this
     * incoming lane. Has no effect if {@code lane} is already in the set.
     *
     * @param lane outgoing lane to permit; must not be null, and must have
     *             direction {@link Direction#OUTGOING}
     * @throws IllegalArgumentException if {@code lane} is null, if this lane
     *                                  is not {@link Direction#INCOMING}, or if
     *                                  {@code lane} is not
     *                                  {@link Direction#OUTGOING}
     */
    public void addAllowedOutgoingLane(Lane lane) {
        validateAllowedOutgoingLane(lane);
        if (!allowedOutgoingLanes.contains(lane)) {
            allowedOutgoingLanes.add(lane);
            logger.log(Level.FINE, "Added allowed outgoing lane: {0}", lane);
        }
    }

    /**
     * Atomically replaces the complete set of permitted outgoing lanes for
     * this incoming lane.
     *
     * <p>All lanes in {@code lanes} are validated before any change is made,
     * so either the full replacement succeeds or the existing set is left
     * unchanged.
     *
     * @param lanes new set of permitted outgoing lanes; must not be null, and
     *              every element must be a non-null
     *              {@link Direction#OUTGOING} lane
     * @throws IllegalArgumentException if {@code lanes} is null, or if any
     *                                  element fails validation
     */
    public void setAllowedOutgoingLanes(Collection<Lane> lanes) {
        if (lanes == null) {
            throw new IllegalArgumentException("Allowed outgoing lanes must not be null.");
        }
        List<Lane> replacementLanes = new ArrayList<>(lanes);
        for (Lane lane : replacementLanes) {
            validateAllowedOutgoingLane(lane);
        }

        allowedOutgoingLanes.clear();
        for (Lane lane : replacementLanes) {
            addAllowedOutgoingLane(lane);
        }
    }

    /**
     * Returns the flow direction of this lane.
     *
     * @return flow direction; never null
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Returns a read-only view of the outgoing lanes that are permitted for
     * this incoming lane.
     *
     * @return unmodifiable list of permitted outgoing lanes; never null
     */
    public List<Lane> getAllowedOutgoingLanes() {
        return Collections.unmodifiableList(allowedOutgoingLanes);
    }

    /**
     * Returns {@code true} if {@code lane} is in the permitted outgoing set.
     *
     * @param lane lane to test
     * @return {@code true} if the turn is permitted
     */
    public boolean isAllowedOutgoingLane(Lane lane) {
        return allowedOutgoingLanes.contains(lane);
    }

    /**
     * Enforces turn restrictions before routing traffic to an outgoing lane.
     *
     * @param lane target outgoing lane; must not be null
     * @throws IllegalArgumentException if {@code lane} is null
     * @throws IllegalStateException    if this lane is not
     *                                  {@link Direction#INCOMING}, or if
     *                                  {@code lane} is not in the permitted set
     */
    public void validateOutgoingLaneAllowed(Lane lane) {
        if (lane == null) {
            throw new IllegalArgumentException("Outgoing lane must not be null.");
        }
        if (direction != Direction.INCOMING) {
            throw new IllegalStateException("Only incoming lanes can validate outgoing turn restrictions.");
        }
        if (!isAllowedOutgoingLane(lane)) {
            throw new IllegalStateException("Illegal turn: outbound lane is not permitted for this inbound lane.");
        }
    }

    /**
     * Logs the direction and permitted outgoing lanes of this lane for
     * diagnostic purposes.
     */
    public void displayLaneInfo() {
        logger.log(Level.FINE, "{0}", this);
        if (direction == Direction.INCOMING) {
            for (Lane outgoingLane : allowedOutgoingLanes) {
                logger.log(Level.FINE, "Allowed outgoing lane: {0}", outgoingLane);
            }
        }
    }

    private void validateAllowedOutgoingLane(Lane lane) {
        if (lane == null) {
            throw new IllegalArgumentException("Allowed outgoing lane must not be null.");
        }
        if (this.direction != Direction.INCOMING || lane.direction != Direction.OUTGOING) {
            throw new IllegalArgumentException("Only incoming lanes can have allowed outgoing lanes, and only outgoing lanes can be added.");
        }
    }

    /**
     * Returns a compact diagnostic representation of this lane.
     *
     * @return readable lane summary
     */
    @Override
    public String toString() {
        return "Lane{"
                + "direction=" + direction
                + ", allowedOutgoingLaneCount=" + allowedOutgoingLanes.size()
                + '}';
    }

    /**
     * Lanes model physical lane instances, so equality is intentionally based
     * on object identity rather than direction or configured turns.
     *
     * @param obj object to compare
     * @return {@code true} only when both references point to the same lane
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
