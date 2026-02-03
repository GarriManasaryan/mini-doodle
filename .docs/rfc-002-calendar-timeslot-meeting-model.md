# RFC: Calendar-based TimeSlot & Meeting Scheduling Model

|           |                              |
|-----------|------------------------------|
| RFC       | #002                         |
| Submitted | 2026-02-03                   |
| Status    | Draft                        |
| Author    | Garri                        |

---

## Summary

The system needs to support user availability management, meeting scheduling, and free/busy querying at scale.  
A naive approach that directly mutates availability based on meetings leads to complex state management and data fragmentation.

This RFC proposes a **clear separation between availability (TimeSlots) and bookings (ScheduledItems)**, using calendars as contextual boundaries and working hours as constraints. Availability is immutable by meetings and used strictly for validation, while meetings represent actual occupation of time.

---

## Problem

The core challenge is modeling **availability vs actual usage of time** in a way that is:

- deterministic
- concurrency-safe
- scalable
- easy to reason about
- aligned with real-world calendar behavior

Key difficulties include:
- Partial occupation of availability windows
- Recurring events
- Concurrent writes in a multi-instance environment
- Avoiding state explosion or interval fragmentation
- Preserving auditability

---

## Goals

- Clearly separate *availability intent* from *scheduled events*
- Support single and recurring availability and meetings
- Avoid mutation or fragmentation of TimeSlots when meetings are created
- Ensure concurrency safety in a multi-instance deployment
- Enable deterministic free/busy computation
- Keep the model extensible (future scheduled item types)

---

## Non-Goals

- Merging or normalizing overlapping calendar intervals (cause well-known calendars dont do that)
- Automatically resolving conflicts between overlapping meetings
- Materializing or pre-expanding recurring events
- Implementing UI-specific constraints in the domain
- Hard deletion of domain data

---

## Description (Technical Design)

### Conceptual Model

The system is built around **three distinct concepts**, each with a single responsibility:

#### 1. Calendar
A calendar is a **context boundary**, not a container of events.

- Defines:
    - ownership (`managedByUserId`)
    - subject (`subjectUserId`)
    - working hours
- Used as a constraint layer
- Exists only in the domain model

Example:
- A parent managing a child’s calendar
- An assistant managing an executive’s calendar

---

#### 2. TimeSlot (Availability)

A `TimeSlot` represents only **declared availability**, not actual usage.

Key properties:
- Bound to a calendar
- Must fall inside calendar working hours
- Can be single or recurring
- Can restrict what can be booked (`allowedScheduledItemType`)
- Can be manually marked busy (`isBusyByUser`)

Important design decision:
> TimeSlots are **never mutated** by meetings.

They serve only as:
- validation constraints
- availability signals

This avoids:
- splitting slots into `(free → busy → free)`
- storing partial occupation state
- garbage records caused by fragmentation

---

#### 3. ScheduledItem (Meetings, Focus Time, etc.)

A `ScheduledItem` represents **actual occupation of time**.

- Has its own timing (single or recurring)
- Can overlap with other scheduled items
- Does not alter TimeSlots
- Is validated *against* TimeSlots at creation time

Meetings, focus time, and future event types all live here.

---

### Why TimeSlots and Meetings Are Separate

Initial approaches considered:

1. **Meeting ID inside TimeSlot**
    - Leads to partial occupation problems
    - Requires splitting slots or tracking sub-intervals

2. **TimeSlot with occupied intervals**
    - Makes “free or busy” non-deterministic
    - Complicates validation and querying

These approaches blur responsibilities and introduce state complexity.

Final decision:
> Availability and bookings have **different intents** and must not share lifecycle or state.

---

### Working Hours as First-Class Constraint

Calendars define working hours.
TimeSlots:
- can only be created inside working hours
- inherit the calendar’s temporal constraints

ScheduledItems:
- must fit inside TimeSlots
- are indirectly constrained by working hours

This provides:
- a single source of truth for availability rules
- consistent validation across entities

---

### Event Timing Model

Both TimeSlots and ScheduledItems share `CalendarEventTiming`:

- `SingleEventTiming`
- `RecurringEventTiming`

Recurring events use:
- start date
- local start time
- duration
- recurrence rules

There is **no end time** for recurring events.

Reasoning:
> Duration is unambiguous across day boundaries, DST, and timezone shifts, while end timestamps are not.

---

### Overlapping Events

Earlier versions included an `allowOverlap` flag.

This was intentionally removed.

Reason:
- Real-world calendars (Google, Outlook, mobile calendars) **allow overlapping events**

The system allows overlap and delegates conflict visibility to consumers.

---

### Concurrency Model

Concurrency is handled using **database-level locking**.

- Calendar-level row lock (`FOR UPDATE`)
- Applied when creating or updating TimeSlots
- Prevents concurrent invalid slot creation

Rationale:
- Application runs in multiple instances
- JVM-level locks are ineffective
- DB remains the single source of truth

---

### Domain vs Backoffice Models

The domain model and backoffice models are intentionally separate.

Reasons:
- Different validation rules
- Different required fields
- Different consumers
- Different lifecycle guarantees

This prevents leaking UI or API concerns into the core domain.

---

### Soft Deletes

All entities use `is_deleted` instead of hard deletes.

Benefits:
- Auditability
- Historical queries
- Safer rollback and recovery
- Compliance readiness


---

## Alternatives Considered

### 1. Mutating TimeSlots on Meeting Creation
Rejected:
- Causes slot fragmentation
- Complicates state transitions
- Pollutes the database

### 2. Storing Occupied Sub-Intervals in TimeSlots
Rejected:
- Non-deterministic availability
- Complex querying logic
- Hard to validate correctness

### 3. Pre-expanding Recurring Events
Rejected:
- High storage cost
- Poor scalability
- Difficult to update recurrence rules

---

## Observability

Key metrics to track:

- Number of TimeSlots per calendar
- Number of ScheduledItems per calendar
- Lock wait time on calendar updates
- Validation failure rates
- Slot vs meeting creation ratio

Logging:
- Calendar lock acquisition
- Validation failures
- Conflicting scheduled item attempts

---

## Performance & Costs

- Availability is computed dynamically, not materialized
- No N² operations for overlapping checks
- Recurring rules are evaluated on demand
- Database indexes support high read throughput

Optimizations can be introduced later:
- Materialized free/busy views
- Cached recurrence expansions

Current model prioritizes correctness and maintainability.

---

## Deployment & Rollout

### Rollout Strategy

1. Introduce new domain entities alongside existing logic
2. Enable write paths behind feature flags
3. Gradually route new calendars to the new model
4. Monitor validation failures and lock contention
5. Remove legacy logic after stabilization

### Rollback

- Disable write paths
- Revert routing flags
- No data loss due to soft deletes

---

## References

- Domain-Driven Design (Evans)
- Google Calendar & Outlook behavior analysis
- PostgreSQL concurrency and row-level locking
- RFC-style internal architecture docs

---

## Success Criteria

This RFC is successful if:

- Availability and meetings are clearly decoupled
- Partial occupation does not corrupt availability
- Concurrent slot creation remains consistent
- The model remains understandable without UI context
- Future scheduled item types can be added without refactoring
