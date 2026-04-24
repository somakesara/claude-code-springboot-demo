# Activity 5 — Documentation Demo Prompt

## The Scenario

`InventoryController` has 8 endpoints. No API documentation exists.
The team needs complete Markdown docs for the internal developer portal.

---

## Step 1 — Generation Prompt

Give Claude Code this prompt:

```
Generate complete API documentation for InventoryController in Markdown format.

For each of the 8 endpoints, include:
- HTTP method and full path
- Description (what it does, not just what the method name says)
- Authentication: which role is required
- Path parameters, query parameters — name, type, required/optional, constraints
- Request body schema (if applicable) with a realistic example
- Response schema — full field list with types and descriptions
- Example response with realistic data (not placeholder UUIDs)
- Error responses — every possible status code with example body
- Business rules enforced by this endpoint

Output as a single Markdown file suitable for a developer portal.
```

---

## What Claude Code Generates (strong)

For each endpoint it produces:

```markdown
## GET /api/v1/inventory/sku/{sku}

**Description:** Returns current inventory levels for a product by SKU.
Includes available quantity (on-hand minus reserved), reorder threshold status,
and warehouse location.

**Authentication:** ROLE_USER

**Path Parameters:**
| Parameter | Type   | Required | Description |
|-----------|--------|----------|-------------|
| sku       | String | Yes      | Product SKU (e.g. WIDGET-001) |

**Response: 200 OK**
```json
{
  "success": true,
  "data": {
    "id": "b0000000-0000-0000-0000-000000000001",
    "productId": "c0000000-0000-0000-0000-000000000001",
    "productSku": "WIDGET-001",
    "productName": "Standard Widget",
    "quantityOnHand": 500,
    "quantityReserved": 20,
    "availableQuantity": 480,
    "reorderThreshold": 50,
    "belowReorderThreshold": false,
    "unitCost": 4.99,
    "warehouseLocation": "WAREHOUSE-A",
    "updatedAt": "2026-04-23T10:15:00Z"
  },
  "timestamp": "2026-04-23T10:15:30Z"
}
```

**Error Responses:**
| Status | Condition | Example |
|--------|-----------|---------|
| 404 | SKU not found | `{"success": false, "message": "InventoryItem with SKU: WIDGET-999"}` |
| 401 | Unauthenticated | Spring Security default |
| 403 | Insufficient role | Spring Security default |
```

---

## What the Engineer Adds (the judgment layer)

After Claude generates the mechanical structure, the engineer adds:

1. **Why decisions were made:**
   > "Available quantity = on-hand minus reserved, not on-hand alone, because reserved items
   > are committed to in-flight orders and must not be allocated twice."

2. **Known limitations:**
   > "This endpoint reflects the state at read time. In high-throughput scenarios, available
   > quantity may have changed between GET and subsequent reservation. Use the reservation
   > API for atomic reserve-and-confirm."

3. **Cross-service dependencies:**
   > "Inventory levels are updated by the Fulfillment Service via Kafka topic
   > `fulfillment-service.shipment.completed`. Expect up to 30-second lag after
   > a shipment event before levels reflect in this API."

4. **Planned changes:**
   > "Q3: bulk inventory query endpoint for catalogue service integration.
   > Current endpoint requires one call per SKU."

---

## The Value Shift

| Task | Before Claude Code | With Claude Code |
|------|-------------------|------------------|
| Document 8 endpoints | 4–6 hours | 15 minutes |
| Schema accuracy | Often misses edge cases | Derived from code — accurate |
| Error code coverage | Frequently incomplete | Complete (reads GlobalExceptionHandler) |
| Business rules | Missed without code review | In the prompt |
| Why/limitations/dependencies | Engineer writes | **Still engineer writes** |

**Key message:** Claude Code eliminates the mechanical scaffolding.
The engineer's 4 hours is now 30 minutes of high-value additions only they can make.

---

## Metrics to Track

| Metric | Target | How to measure |
|---|---|---|
| Documentation Completeness | > 95% endpoints documented | Audit against OpenAPI spec |
| Time to First Draft | < 20 minutes | Timer from prompt to draft |
| Post-Generation Edit Time | < 30 minutes | Timer from draft to portal-ready |
| Portal Adoption | Trending up | Internal portal page views |
