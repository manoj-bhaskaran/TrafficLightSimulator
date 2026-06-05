# Traffic Light Simulator — Functional Overview

This document explains, in plain language, **what the Traffic Light Simulator
does** and **what rules it enforces**. It is written for a non-technical
reader: you do not need to know Java to understand it. Engineers can use it as a
catalogue of the business rules implemented in the code.

---

## 1. What is the application?

The Traffic Light Simulator is a software model of a road intersection and its
signalling equipment. Think of it as a digital "kit of parts" for building a
junction: you can assemble roads, lanes, traffic lights, and pedestrian
crossings, wire them together, and the software makes sure the result is a
**safe and sensible layout**.

It is currently a **library and engine**, not a graphical game. There is no
on-screen animation yet. Instead, the software focuses on:

- **Modelling** the physical pieces of an intersection.
- **Validating** that those pieces are arranged legally and safely.
- **Coordinating** the start-up of the signals into a known, safe state.

A small runnable demo builds a simple two-road intersection and prints its
status, so you can see the model in action.

> **Project maturity.** The project is **pre-MVP** (early development). Its
> behaviour and interfaces may still change before a stable 1.0 release.

---

## 2. The pieces of the model (the "things" in the world)

| Piece | Plain-language meaning |
|-------|------------------------|
| **Intersection** | The junction where roads meet. It is the top-level container. |
| **Road** | A single approach into the junction (for example, the northbound road). Each road points in a compass direction. |
| **Lane** | A single lane on a road. Lanes are either **incoming** (heading towards the junction) or **outgoing** (heading away from it). |
| **Traffic Light** | A signal head facing drivers or pedestrians. It has a colour, an on/off state, and an optional directional arrow. |
| **Traffic Light Group** | A bundle of lights that are controlled together and share safety rules. |
| **Pedestrian Crossing** | A crossing attached to a road, with its own pedestrian signals. |
| **Pedestrian Button** | The push-button a pedestrian presses to request a crossing. |

### Properties a light can have

- **Colour:** Red, Amber, or Green.
- **State:** On, Off, Blinking, or Out of Order.
- **Direction (arrow):** Straight, Left, Right, or None.
- **Type:** a vehicle ("traffic") light or a pedestrian light.
- **Multi-colour:** whether the physical head can show more than one colour.

---

## 3. What you can do with it (capabilities)

1. **Build an intersection** by declaring how many roads it will have and then
   adding the roads one at a time.
2. **Configure each road** with a compass angle and a number of incoming and
   outgoing lanes.
3. **Attach traffic lights** to the lights controlling a road's incoming lanes.
4. **Define legal turns** — which incoming lane is allowed to feed into which
   outgoing lane.
5. **Add pedestrian crossings**, optionally with push-buttons at either end.
6. **Declare conflicting signals** that must never be green at the same time.
7. **Start the simulation** in a safe state, where the engine switches all
   signals off before anything runs.
8. **Inspect status** of the whole intersection for diagnostics.

Two convenient "builders" (`RoadBuilder` and `IntersectionBuilder`) let you
assemble roads and intersections in a readable, step-by-step way while every
rule below is still checked.

---

## 4. Business rules implemented

These are the rules the software enforces. If a rule is broken, the software
**refuses the change and reports an error** rather than silently building an
unsafe or invalid layout.

### 4.1 Intersection rules

- **R1 — Road count bounds.** An intersection must have between **2 and 8**
  roads. Declaring fewer or more is rejected.
- **R2 — Capacity is fixed once declared.** You cannot add more roads than the
  declared capacity. Once the declared number of roads has been added, the
  intersection is considered **complete** and is closed to further roads.
- **R3 — Capacity cannot shrink below what exists.** You may raise the declared
  road count later, but you cannot lower it below the number of roads already
  added (that would strand existing roads).
- **R4 — No duplicate roads.** The same physical road cannot be added to an
  intersection twice.
- **R5 — One owner per road.** A road that already belongs to another
  intersection cannot be added.
- **R6 — Minimum angular spacing.** Every pair of roads must be at least **30
  degrees apart**, measured by the shortest angle around the circle. This stops
  two roads from overlapping or being unrealistically close. The check correctly
  handles the wrap-around at the 0°/360° boundary (for example, 350° and 10° are
  only 20° apart and would be rejected).

### 4.2 Road rules

- **R7 — Valid compass angle.** A road's angle must be at least **0° and less
  than 360°**.
- **R8 — Angle changes stay legal.** If a road is already part of an
  intersection, changing its angle is re-checked against the 30° spacing rule
  (R6), so a road already in a junction cannot be quietly moved into an illegal
  position.
- **R9 — Minimum lanes.** Each road must have **at least one** incoming lane and
  **at least one** outgoing lane. Zero or negative counts are rejected.
- **R10 — Non-destructive lane resizing.** Increasing or decreasing the lane
  count adds or removes lanes **without discarding** the configuration (such as
  turn permissions) of the lanes that remain. Growing appends new lanes; only
  surplus lanes at the end are removed when shrinking.
- **R11 — At most one pedestrian crossing per road.** A road can have a single
  crossing. Adding a second one is rejected, and you cannot remove a crossing
  that is not there.

### 4.3 Lane and turning rules

- **R12 — Turns must be explicitly allowed.** By default an incoming lane
  permits **no turns**. You must explicitly list which outgoing lanes it may
  feed into before traffic can be routed.
- **R13 — Turns go incoming → outgoing only.** Only an incoming lane can hold
  turn permissions, and a permitted target must be an outgoing lane.
- **R14 — Same-road turns only.** A turn can only be configured between an
  incoming lane and an outgoing lane that **both belong to the same road**.
  Cross-road targets are rejected.
- **R15 — Duplicates ignored.** Listing the same permitted turn twice has no
  extra effect; each appears once.
- **R16 — Illegal turns are blocked.** Asking the model to route a turn that was
  never permitted is refused with a clear error.

### 4.4 Traffic-light rules

- **R17 — A light always has a valid colour and state.** A light cannot be
  created or changed to have a missing (null) colour, state, type, or direction.
- **R18 — Pedestrian lights never show Amber.** Amber is a vehicle-only colour;
  setting a pedestrian light to Amber is rejected.
- **R19 — "Active" means Green + On.** Throughout the system, a light counts as
  *active* (giving right of way) only when it is **Green and switched On**. Any
  other combination is treated as not giving right of way.

### 4.5 Safety: conflicting signals

- **R20 — Conflicting greens are forbidden.** Two lights can be declared
  **incompatible** (for example, signals for crossing traffic streams). Once
  declared, the software will **refuse to turn one green-and-on while the other
  is already green-and-on.** This is the core safety rule that prevents the
  model from green-lighting two conflicting movements simultaneously.
- **R21 — Incompatibility is mutual.** Declaring A incompatible with B
  automatically makes B incompatible with A.
- **R22 — A light cannot conflict with itself.** Declaring a light incompatible
  with itself is rejected.
- **R23 — Conflicts checked only on activation.** The safety check fires only
  when a light is about to become active (Green + On). Switching lights to red,
  off, or any non-active combination is always allowed, which keeps ordinary
  signal cycling simple.
- **R24 — Cross-group conflicts are supported.** Conflicting lights that are
  controlled by different groups (for example a vehicle group and a pedestrian
  group at the same junction) can be linked at the intersection level, and
  either group will then block an unsafe activation.
- **R25 — Bulk changes skip unsafe lights.** When setting a whole group of
  lights at once, any individual light that cannot legally accept the change
  (for example, it would breach a conflict rule, or it is an Amber request to a
  pedestrian light) is **skipped with a warning** rather than aborting the whole
  operation.

### 4.6 Pedestrian crossing and button rules

- **R26 — A crossing owns its pedestrian signals.** Each crossing manages its
  own group of pedestrian lights.
- **R27 — Control type is automatic from buttons.** A crossing created **with**
  at least one button is **button-controlled**; a crossing created **without**
  buttons is **automated** (driven by the engine).
- **R28 — Buttons belong to one crossing.** A button already attached to one
  crossing cannot be "stolen" by another crossing, which keeps each crossing's
  wiring intact.
- **R29 — Pressing requests a crossing.** A crossing is considered *requested*
  when at least one of its buttons has been pressed and not yet reset. Buttons
  are reset after the crossing has been served.
- **R30 — Activate / deactivate the right of way.** Activating a crossing sets
  all its pedestrian lights to Green + On (walk); deactivating sets them to
  Red + On (don't walk).

### 4.7 Simulation start-up rules

- **R31 — Safe initial state.** Before a simulation runs, the engine switches
  **all** vehicle and pedestrian light groups **Off**, establishing a known,
  safe starting condition rather than leaving signals in a random state.

---

## 5. How the rules protect you (worked examples)

- *You try to add a third road to a junction you declared as having two roads.*
  → Rejected (R2): the junction is already complete.
- *You place a road at 10° next to one at 350°.* → Rejected (R6): the shortest
  gap between them is only 20°, below the 30° minimum.
- *You set a pedestrian light to Amber.* → Rejected (R18): pedestrian signals
  have no amber.
- *Two crossing-traffic signals are declared incompatible, and you turn the
  second one green while the first is already green.* → Rejected (R20): the
  model will not allow two conflicting greens at once.
- *You route a car from an incoming lane into an outgoing lane you never marked
  as a legal turn.* → Rejected (R16): the turn was not permitted.

---

## 6. A note on "identity"

Every physical part (road, lane, light, crossing, button, group) is treated as a
**unique object**. Two lights that happen to have identical settings are still
considered different lights. This matters because safety rules and turn
permissions target **specific physical pieces of equipment**, not just any
look-alike with the same configuration.

---

## 7. Where to go next

- For build, run, and developer instructions, see the [README](../README.md).
- For the detailed, method-by-method API reference, generate the Javadoc with
  `mvn javadoc:javadoc`.
- For the history of changes, see the [CHANGELOG](../CHANGELOG.md).
