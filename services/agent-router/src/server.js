/**
 * Server - Express + WebSocket setup
 */

const express = require('express');
const { WebSocketServer } = require('ws');
const http = require('http');
const { AgentState } = require('./state');
const { startSimulator } = require('./simulator');

/**
 * @param {Object} options
 * @param {number} options.port
 * @param {string} options.token
 * @param {boolean} options.simulatorMode
 */
function createServer({ port, token, simulatorMode }) {
  const app = express();
  app.use(express.json());

  const server = http.createServer(app);
  const wss = new WebSocketServer({ server, path: '/ws' });

  const state = new AgentState();
  const clients = new Set();

  // Authentication middleware
  function authenticate(req, res, next) {
    const authToken = req.headers['x-agenthud-token'];
    if (authToken !== token) {
      return res.status(401).json({ success: false, error: 'INVALID_TOKEN' });
    }
    next();
  }

  // Health check
  app.get('/health', (req, res) => {
    res.json({ status: 'ok', agents: state.getAllAgents().length });
  });

  // Action endpoint
  app.post('/action', authenticate, (req, res) => {
    const { agent_id, action, payload } = req.body;

    if (!agent_id || !action) {
      return res.status(400).json({ success: false, error: 'Missing agent_id or action' });
    }

    const result = state.handleAction(agent_id, action, payload);

    if (result.success) {
      // Broadcast update to all clients
      const agent = state.getAgent(agent_id);
      if (agent) {
        broadcast({ type: 'agent_update', ...agent });
      }
    }

    res.json(result);
  });

  // WebSocket handling
  wss.on('connection', (ws, req) => {
    // Authenticate WebSocket connection
    const authToken = req.headers['x-agenthud-token'];
    if (authToken !== token) {
      ws.close(4001, 'INVALID_TOKEN');
      return;
    }

    clients.add(ws);
    console.log(`Client connected (${clients.size} total)`);

    // Send snapshot on connect
    ws.send(JSON.stringify({
      type: 'snapshot',
      ts: Date.now(),
      agents: state.getAllAgents(),
    }));

    ws.on('close', () => {
      clients.delete(ws);
      console.log(`Client disconnected (${clients.size} total)`);
    });

    ws.on('error', (err) => {
      console.error('WebSocket error:', err.message);
      clients.delete(ws);
    });
  });

  // Broadcast to all connected clients
  function broadcast(message) {
    const data = JSON.stringify(message);
    for (const client of clients) {
      if (client.readyState === 1) { // OPEN
        client.send(data);
      }
    }
  }

  // Subscribe to state changes
  state.onUpdate((agent) => {
    broadcast({ type: 'agent_update', ...agent });
  });

  return {
    start() {
      server.listen(port, '0.0.0.0', () => {
        console.log(`Server listening on port ${port}`);

        if (simulatorMode) {
          startSimulator(state);
        }
      });
    },
    stop() {
      server.close();
    },
  };
}

module.exports = { createServer };
