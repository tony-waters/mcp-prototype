# 05: Build Refund Eligibility Rules

**What to build:** the application can check whether a Customer's Order is eligible for this refund workflow and explain the result through a rule breakdown.

**Blocked by:** 04: Build Customer Lookup and Recent Orders

**Status:** resolved

- [x] Eligibility requires both Customer ID and Order ID.
- [x] Eligibility verifies that the Customer exists.
- [x] Eligibility verifies that the Order exists and belongs to the Customer.
- [x] Eligibility requires an active Customer.
- [x] A blocked Customer is ineligible.
- [x] A watch Customer can remain eligible but returns a warning check.
- [x] Eligibility requires a delivered Order with `delivered_at`.
- [x] The 30-day refund window is calculated from `delivered_at`.
- [x] An already-refunded Order is ineligible.
- [x] The result includes eligibility, reason, max refund minor units, currency, and passed/failed/warning checks.

## Answer

Added `RefundEligibilityService` with repository-backed Customer and Order checks. It enforces required IDs, existence, ownership, active Customer status, blocked-risk failure, watch-risk warning, delivered status, delivered timestamp presence, the configurable 30-day refund window, and prior-refund failure. Results include the final boolean, reason, max refund amount, currency, and ordered rule checks.
