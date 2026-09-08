# 05: Build Refund Eligibility Rules

**What to build:** the application can check whether a Customer's Order is eligible for this refund workflow and explain the result through a rule breakdown.

**Blocked by:** 04: Build Customer Lookup and Recent Orders

**Status:** ready-for-agent

- [ ] Eligibility requires both Customer ID and Order ID.
- [ ] Eligibility verifies that the Customer exists.
- [ ] Eligibility verifies that the Order exists and belongs to the Customer.
- [ ] Eligibility requires an active Customer.
- [ ] A blocked Customer is ineligible.
- [ ] A watch Customer can remain eligible but returns a warning check.
- [ ] Eligibility requires a delivered Order with `delivered_at`.
- [ ] The 30-day refund window is calculated from `delivered_at`.
- [ ] An already-refunded Order is ineligible.
- [ ] The result includes eligibility, reason, max refund minor units, currency, and passed/failed/warning checks.
