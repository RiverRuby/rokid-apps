/**
 * Simulator - Fake agent generator for testing
 */

const FAKE_AGENTS = [
  {
    agent_id: 'agent-1',
    name: 'Frontend',
    summaries: {
      RUNNING: 'Implementing auth flow...',
      PAUSED: 'Paused by user',
      WAITING_APPROVAL: 'Delete old UI components?',
      ERROR: 'Build failed: missing dependency',
      DONE: 'Auth flow complete',
    },
    details: {
      RUNNING: 'Currently working on OAuth integration with Google. Added login button component and started on token refresh logic.',
      WAITING_APPROVAL: 'Found 12 unused UI components from the old design. Requesting permission to delete them to clean up the codebase.',
      ERROR: 'npm ERR! Could not resolve dependency: react-oauth-google@^1.0.0',
    },
  },
  {
    agent_id: 'agent-2',
    name: 'Backend',
    summaries: {
      RUNNING: 'Optimizing database queries...',
      PAUSED: 'Waiting for approval',
      WAITING_APPROVAL: 'Delete user table?',
      ERROR: 'Connection refused to DB',
      DONE: 'API endpoints updated',
    },
    details: {
      RUNNING: 'Analyzing slow queries and adding indexes. Found 3 queries that can be optimized.',
      WAITING_APPROVAL: 'Agent is requesting permission to delete the users table. This will remove all user data permanently.',
      ERROR: 'PostgreSQL connection refused. Is the database running?',
    },
  },
  {
    agent_id: 'agent-3',
    name: 'Tests',
    summaries: {
      RUNNING: 'Running test suite...',
      PAUSED: 'Tests paused',
      WAITING_APPROVAL: 'Skip flaky tests?',
      ERROR: '3 tests failed',
      DONE: 'All tests passed',
    },
    details: {
      RUNNING: 'Executing 142 tests across 28 test files...',
      WAITING_APPROVAL: 'Found 5 tests that intermittently fail. Skip them for now to unblock CI?',
      ERROR: 'FAIL src/auth.test.js: Expected token to be defined\nFAIL src/api.test.js: Timeout exceeded',
    },
  },
  {
    agent_id: 'agent-4',
    name: 'Deploy',
    summaries: {
      RUNNING: 'Deploying to staging...',
      PAUSED: 'Deployment paused',
      WAITING_APPROVAL: 'Deploy to production?',
      ERROR: 'Deployment failed',
      DONE: 'Deployed successfully',
    },
    details: {
      RUNNING: 'Building Docker image and pushing to registry...',
      WAITING_APPROVAL: 'All checks passed. Ready to deploy v2.3.1 to production. This will affect 10,000 users.',
      ERROR: 'Failed to push Docker image: unauthorized',
    },
  },
];

const STATUSES = ['RUNNING', 'PAUSED', 'WAITING_APPROVAL', 'ERROR', 'DONE'];

/**
 * Start the simulator
 * @param {import('./state').AgentState} state
 */
function startSimulator(state) {
  console.log('Starting simulator with 4 fake agents...');

  // Initialize agents
  for (const fakeAgent of FAKE_AGENTS) {
    state.setAgent({
      agent_id: fakeAgent.agent_id,
      name: fakeAgent.name,
      status: 'RUNNING',
      summary: fakeAgent.summaries.RUNNING,
      detail: fakeAgent.details?.RUNNING || '',
      ts: Date.now(),
      actions: ['pause'],
    });
  }

  // Randomly update agents
  function updateRandomAgent() {
    const fakeAgent = FAKE_AGENTS[Math.floor(Math.random() * FAKE_AGENTS.length)];
    const agent = state.getAgent(fakeAgent.agent_id);

    if (!agent) return;

    // Only update if agent is in RUNNING state (simulates agent doing work)
    // Or randomly transition to WAITING_APPROVAL or ERROR
    if (agent.status === 'RUNNING') {
      const rand = Math.random();

      if (rand < 0.3) {
        // 30% chance: needs approval
        state.setAgent({
          ...agent,
          status: 'WAITING_APPROVAL',
          summary: fakeAgent.summaries.WAITING_APPROVAL,
          detail: fakeAgent.details?.WAITING_APPROVAL || '',
        });
      } else if (rand < 0.4) {
        // 10% chance: error
        state.setAgent({
          ...agent,
          status: 'ERROR',
          summary: fakeAgent.summaries.ERROR,
          detail: fakeAgent.details?.ERROR || '',
        });
      } else if (rand < 0.5) {
        // 10% chance: done
        state.setAgent({
          ...agent,
          status: 'DONE',
          summary: fakeAgent.summaries.DONE,
          detail: '',
        });
      } else {
        // 50% chance: update progress
        state.setAgent({
          ...agent,
          summary: fakeAgent.summaries.RUNNING + ` (${Math.floor(Math.random() * 100)}%)`,
        });
      }
    } else if (agent.status === 'DONE') {
      // Reset to RUNNING after being done for a while
      state.setAgent({
        ...agent,
        status: 'RUNNING',
        summary: fakeAgent.summaries.RUNNING,
        detail: fakeAgent.details?.RUNNING || '',
      });
    }

    // Schedule next update
    const delay = 3000 + Math.random() * 7000; // 3-10 seconds
    setTimeout(updateRandomAgent, delay);
  }

  // Start update loop
  setTimeout(updateRandomAgent, 5000);
}

module.exports = { startSimulator };
