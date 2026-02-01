# Agent Router

WebSocket and HTTP server for AgentHUD communication.

## Overview

This service acts as the bridge between coding agents and the AgentHUD app on Rokid glasses. It maintains the state of all agents and broadcasts updates to connected clients.

## Quick Start

```bash
cd services/agent-router
npm install
npm start
```

## Configuration

Copy `.env.example` to `.env` and configure:

```bash
cp .env.example .env
```

Environment variables:
- `PORT` - Server port (default: 8787)
- `TOKEN` - Authentication token
- `SIMULATOR_MODE` - Enable fake agent simulation (default: false)

## Endpoints

### WebSocket: `/ws`

Connect to receive real-time agent updates.

Headers:
```
X-AgentHUD-Token: <token>
```

Messages:
- `snapshot` - Full state on connect
- `agent_update` - Individual agent changes

### HTTP: `POST /action`

Send actions to agents.

Headers:
```
Content-Type: application/json
X-AgentHUD-Token: <token>
```

Body:
```json
{
  "type": "agent_action",
  "agent_id": "agent-1",
  "action": "approve"
}
```

## Simulator Mode

For testing without real agents:

```bash
SIMULATOR_MODE=true npm start
```

Creates 4 fake agents that randomly change state.

## Message Schema

See `docs/EVENT_SCHEMA.md` in the root directory for full message specifications.

## Directory Structure

```
agent-router/
├── README.md
├── package.json
├── .env.example
└── src/
    ├── index.js          # Entry point
    ├── server.js         # Express + WebSocket setup
    ├── state.js          # Agent state management
    ├── simulator.js      # Fake agent generator
    └── types.js          # JSDoc type definitions
```

## Development

```bash
# Install dependencies
npm install

# Run with auto-reload
npm run dev

# Run tests
npm test
```

## Startup Output

When running, the server displays:

```
=================================
AgentHUD Router v1.0.0
=================================
LAN IP:  192.168.1.x
WS URL:  ws://192.168.1.x:8787/ws
HTTP:    http://192.168.1.x:8787/action
Token:   abc123...

Simulator: ENABLED (4 fake agents)
```

## Testing with curl

```bash
# Health check
curl http://localhost:8787/health

# Send action
curl -X POST http://localhost:8787/action \
  -H "Content-Type: application/json" \
  -H "X-AgentHUD-Token: your-token" \
  -d '{"agent_id":"agent-1","action":"approve"}'
```

## Future Extensions

- [ ] Real agent adapters (Claude, GPT, Cursor)
- [ ] OAuth authentication
- [ ] Multi-user support
- [ ] Persistent state storage
- [ ] Action history logging
