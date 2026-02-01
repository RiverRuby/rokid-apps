# Event Schema

WebSocket and HTTP message formats for AgentHUD communication.

## Overview

```
┌─────────────┐     WebSocket      ┌──────────────┐
│  AgentHUD   │◄──────────────────►│ agent-router │
│  (Android)  │                    │  (Node.js)   │
│             │     HTTP POST      │              │
│             │───────────────────►│              │
└─────────────┘                    └──────────────┘
```

**WebSocket Endpoint**: `ws://<host>:8787/ws`
**HTTP Action Endpoint**: `POST http://<host>:8787/action`

## Authentication

All requests require a shared secret token in the header:

```
X-AgentHUD-Token: <token>
```

## WebSocket Messages

### snapshot

Sent immediately when a client connects. Contains the current state of all agents.

```json
{
  "type": "snapshot",
  "ts": 1712345678,
  "agents": [
    {
      "agent_id": "agent-1",
      "name": "Frontend",
      "status": "RUNNING",
      "summary": "Implementing auth flow...",
      "detail": "Currently working on OAuth integration with Google.",
      "ts": 1712345670,
      "actions": ["pause"],
      "link": "https://github.com/user/repo/pull/42"
    },
    {
      "agent_id": "agent-2",
      "name": "Backend",
      "status": "WAITING_APPROVAL",
      "summary": "Delete user table?",
      "detail": "Agent is requesting permission to delete the users table.",
      "ts": 1712345675,
      "actions": ["approve", "deny"],
      "link": null
    }
  ]
}
```

### agent_update

Sent whenever an agent's state changes. Broadcast to all connected clients.

```json
{
  "type": "agent_update",
  "agent_id": "agent-1",
  "name": "Frontend",
  "status": "RUNNING",
  "summary": "Implementing auth flow...",
  "detail": "Currently working on OAuth integration with Google. Added login button component and started on token refresh logic.",
  "ts": 1712345678,
  "actions": ["pause"],
  "link": "https://github.com/user/repo/pull/42"
}
```

## HTTP Messages

### agent_action

Sent from AgentHUD to agent-router to perform an action on an agent.

**Request**:
```http
POST /action HTTP/1.1
Host: <host>:8787
Content-Type: application/json
X-AgentHUD-Token: <token>

{
  "type": "agent_action",
  "agent_id": "agent-1",
  "action": "approve",
  "payload": {},
  "ts": 1712345678
}
```

**Response (Success)**:
```json
{
  "success": true,
  "agent_id": "agent-1",
  "new_status": "RUNNING"
}
```

**Response (Error)**:
```json
{
  "success": false,
  "error": "Invalid action for current status"
}
```

## Field Definitions

### Agent Object

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `agent_id` | string | Yes | Unique identifier for the agent |
| `name` | string | Yes | Human-readable agent name |
| `status` | string | Yes | Current agent status (see Status Values) |
| `summary` | string | Yes | One-line description of current state |
| `detail` | string | No | Extended description (multi-line) |
| `ts` | number | Yes | Unix timestamp of last update |
| `actions` | string[] | Yes | Available actions for current status |
| `link` | string | No | URL for more details (PR, issue, etc.) |

### Status Values

| Status | Description | Available Actions |
|--------|-------------|-------------------|
| `RUNNING` | Agent is actively working on a task | `pause` |
| `PAUSED` | Agent has been paused by user | `resume` |
| `WAITING_APPROVAL` | Agent needs user decision to proceed | `approve`, `deny` |
| `ERROR` | Agent encountered an error | `ack`, `retry` |
| `DONE` | Agent has completed its task | (none) |

### Action Values

| Action | Valid For Status | Result |
|--------|-----------------|--------|
| `pause` | RUNNING | Changes to PAUSED |
| `resume` | PAUSED | Changes to RUNNING |
| `approve` | WAITING_APPROVAL | Proceeds with action, changes to RUNNING |
| `deny` | WAITING_APPROVAL | Rejects action, changes to PAUSED or DONE |
| `ack` | ERROR | Acknowledges error, changes to PAUSED |
| `retry` | ERROR | Retries operation, changes to RUNNING |

## Error Codes

| Code | Description |
|------|-------------|
| `INVALID_TOKEN` | Authentication token missing or invalid |
| `INVALID_AGENT` | Agent ID not found |
| `INVALID_ACTION` | Action not valid for current agent status |
| `INTERNAL_ERROR` | Server-side error |

## Example Flows

### Agent Completes Task

```
Server                              Client (AgentHUD)
  │                                      │
  │  ────────── agent_update ──────────► │
  │  { status: "RUNNING",                │
  │    summary: "Building..." }          │
  │                                      │
  │  ────────── agent_update ──────────► │
  │  { status: "DONE",                   │
  │    summary: "Build complete" }       │
  │                                      │
```

### User Approves Action

```
Server                              Client (AgentHUD)
  │                                      │
  │  ────────── agent_update ──────────► │
  │  { status: "WAITING_APPROVAL",       │
  │    summary: "Delete table?" }        │
  │                                      │
  │  ◄─────── agent_action (POST) ────── │
  │  { action: "approve" }               │
  │                                      │
  │  ─────── HTTP response ────────────► │
  │  { success: true }                   │
  │                                      │
  │  ────────── agent_update ──────────► │
  │  { status: "RUNNING",                │
  │    summary: "Deleting table..." }    │
  │                                      │
```

### Reconnection with Snapshot

```
Server                              Client (AgentHUD)
  │                                      │
  │         ◄──── WebSocket connect ──── │
  │                                      │
  │  ────────── snapshot ──────────────► │
  │  { agents: [ ... ] }                 │
  │                                      │
  │  ────────── agent_update ──────────► │
  │  (subsequent updates)                │
  │                                      │
```

## Simulator Mode

The agent-router can run in simulator mode for testing. In this mode:

- 4 fake agents are created: Frontend, Backend, Tests, Deploy
- Agents randomly change state every 3-10 seconds
- Periodically enter WAITING_APPROVAL and ERROR states
- Actions properly resolve states:
  - `approve`/`deny` → resolves WAITING_APPROVAL to RUNNING/PAUSED
  - `ack` → clears ERROR to PAUSED
  - `pause`/`resume` → toggles RUNNING/PAUSED

Enable simulator mode by setting environment variable:
```bash
SIMULATOR_MODE=true node src/index.js
```

## TypeScript Type Definitions

```typescript
type AgentStatus = 'RUNNING' | 'PAUSED' | 'WAITING_APPROVAL' | 'ERROR' | 'DONE';
type AgentAction = 'pause' | 'resume' | 'approve' | 'deny' | 'ack' | 'retry';

interface Agent {
  agent_id: string;
  name: string;
  status: AgentStatus;
  summary: string;
  detail?: string;
  ts: number;
  actions: AgentAction[];
  link?: string;
}

interface SnapshotMessage {
  type: 'snapshot';
  ts: number;
  agents: Agent[];
}

interface AgentUpdateMessage {
  type: 'agent_update';
  agent_id: string;
  name: string;
  status: AgentStatus;
  summary: string;
  detail?: string;
  ts: number;
  actions: AgentAction[];
  link?: string;
}

interface AgentActionRequest {
  type: 'agent_action';
  agent_id: string;
  action: AgentAction;
  payload?: Record<string, any>;
  ts: number;
}

interface ActionResponse {
  success: boolean;
  agent_id?: string;
  new_status?: AgentStatus;
  error?: string;
}
```
