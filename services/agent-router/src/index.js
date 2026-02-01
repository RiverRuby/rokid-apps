/**
 * Agent Router - Entry Point
 *
 * WebSocket and HTTP server for AgentHUD communication.
 * See docs/EVENT_SCHEMA.md for message formats.
 */

require('dotenv').config();

const { createServer } = require('./server');

const PORT = parseInt(process.env.PORT || '8787', 10);
const TOKEN = process.env.TOKEN || 'default-token';
const SIMULATOR_MODE = process.env.SIMULATOR_MODE === 'true';

// Get LAN IP for display
function getLanIp() {
  const { networkInterfaces } = require('os');
  const nets = networkInterfaces();

  for (const name of Object.keys(nets)) {
    for (const net of nets[name]) {
      if (net.family === 'IPv4' && !net.internal) {
        return net.address;
      }
    }
  }
  return 'localhost';
}

async function main() {
  const lanIp = getLanIp();

  console.log('=================================');
  console.log('AgentHUD Router v1.0.0');
  console.log('=================================');
  console.log(`LAN IP:  ${lanIp}`);
  console.log(`WS URL:  ws://${lanIp}:${PORT}/ws`);
  console.log(`HTTP:    http://${lanIp}:${PORT}/action`);
  console.log(`Token:   ${TOKEN.substring(0, 8)}...`);
  console.log('');
  console.log(`Simulator: ${SIMULATOR_MODE ? 'ENABLED (4 fake agents)' : 'DISABLED'}`);
  console.log('');

  const server = createServer({
    port: PORT,
    token: TOKEN,
    simulatorMode: SIMULATOR_MODE,
  });

  server.start();

  console.log('Server started. Press Ctrl+C to stop.');
  console.log('');

  if (SIMULATOR_MODE) {
    console.log('Example curl command:');
    console.log(`curl -X POST http://${lanIp}:${PORT}/action \\`);
    console.log(`  -H "Content-Type: application/json" \\`);
    console.log(`  -H "X-AgentHUD-Token: ${TOKEN}" \\`);
    console.log(`  -d '{"agent_id":"agent-1","action":"approve"}'`);
    console.log('');
  }
}

main().catch((err) => {
  console.error('Failed to start server:', err);
  process.exit(1);
});
