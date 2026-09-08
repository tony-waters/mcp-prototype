# MCP Support Desk

This context describes the support workflow exposed through the MCP prototype. It uses support-domain language, not account-management or payments-platform language.

## Language

**Customer**:
A person who bought from the business and may contact support about orders, tickets, or refunds.
_Avoid_: customer account, user account, client, buyer

**Customer Risk Level**:
A support signal on a Customer. `normal` has no warning, `watch` warns the Support Agent without blocking eligibility, and `blocked` prevents Refund Draft creation.
_Avoid_: fraud status, account status

**Order**:
A Customer purchase that may be paid, delivered, refunded, or cancelled.
_Avoid_: purchase, transaction

**Support Agent**:
A frontline worker who handles customer support cases and uses the prototype to investigate refund requests.
_Avoid_: manager, operator, admin

**Support Manager**:
A human reviewer who approves or rejects Refund Drafts. This role is separate from the Support Agent for the prototype's safety boundary.
_Avoid_: approver, admin, operator

**Refund Case**:
A support workflow where a Support Agent investigates whether a Customer's Order should result in a Refund Draft.
_Avoid_: ticket, incident, dispute

**Refund Draft**:
A proposed refund prepared for human review. It is not a completed refund and does not move money.
_Avoid_: refund, issued refund, payment reversal

**Money**:
An amount represented as minor units plus a currency code, for example pence plus `GBP`.
_Avoid_: float amount, decimal-only amount
