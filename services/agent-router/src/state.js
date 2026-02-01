/**
 * State - Agent state management
 */

/**
 * @typedef {'RUNNING' | 'PAUSED' | 'WAITING_APPROVAL' | 'ERROR' | 'DONE'} AgentStatus
 * @typedef {'pause' | 'resume' | 'approve' | 'deny' | 'ack' | 'retry'} AgentAction
 *
 * @typedef {Object} Agent
 * @property {string} agent_id
 * @property {string} name
 * @property {AgentStatus} status
 * @property {string} summary
 * @property {string} [detail]
 * @property {number} ts
 * @property {AgentAction[]} actions
 * @property {string} [link]
 */

const VALID_ACTIONS = {
  RUNNING: ['pause'],
  PAUSED: ['resume'],
  WAITING_APPROVAL: ['approve', 'deny'],
  ERROR: ['ack', 'retry'],
  DONE: [],
};

const ACTION_TRANSITIONS = {
  pause: 'PAUSED',
  resume: 'RUNNING',
  approve: 'RUNNING',
  deny: 'PAUSED',
  ack: 'PAUSED',
  retry: 'RUNNING',
};

class AgentState {
  constructor() {
    /** @type {Map<string, Agent>} */
    this.agents = new Map();

    /** @type {((agent: Agent) => void)[]} */
    this.listeners = [];
  }

  /**
   * Subscribe to agent updates
   * @param {(agent: Agent) => void} callback
   */
  onUpdate(callback) {
    this.listeners.push(callback);
  }

  /**
   * Notify all listeners of an update
   * @param {Agent} agent
   */
  notifyUpdate(agent) {
    for (const listener of this.listeners) {
      listener(agent);
    }
  }

  /**
   * Get all agents
   * @returns {Agent[]}
   */
  getAllAgents() {
    return Array.from(this.agents.values());
  }

  /**
   * Get a single agent
   * @param {string} agentId
   * @returns {Agent | undefined}
   */
  getAgent(agentId) {
    return this.agents.get(agentId);
  }

  /**
   * Set or update an agent
   * @param {Agent} agent
   */
  setAgent(agent) {
    agent.ts = Date.now();
    agent.actions = VALID_ACTIONS[agent.status] || [];
    this.agents.set(agent.agent_id, agent);
    this.notifyUpdate(agent);
  }

  /**
   * Handle an action on an agent
   * @param {string} agentId
   * @param {AgentAction} action
   * @param {Object} [payload]
   * @returns {{ success: boolean, agent_id?: string, new_status?: AgentStatus, error?: string }}
   */
  handleAction(agentId, action, payload) {
    const agent = this.agents.get(agentId);

    if (!agent) {
      return { success: false, error: 'INVALID_AGENT' };
    }

    const validActions = VALID_ACTIONS[agent.status];
    if (!validActions || !validActions.includes(action)) {
      return {
        success: false,
        error: `INVALID_ACTION: ${action} not valid for status ${agent.status}`,
      };
    }

    const newStatus = ACTION_TRANSITIONS[action];
    if (!newStatus) {
      return { success: false, error: 'INVALID_ACTION' };
    }

    agent.status = newStatus;
    agent.ts = Date.now();
    agent.actions = VALID_ACTIONS[newStatus] || [];

    // Update summary based on action
    if (action === 'approve') {
      agent.summary = 'Proceeding with approved action...';
    } else if (action === 'deny') {
      agent.summary = 'Action denied, paused';
    } else if (action === 'ack') {
      agent.summary = 'Error acknowledged, paused';
    } else if (action === 'retry') {
      agent.summary = 'Retrying...';
    } else if (action === 'pause') {
      agent.summary = 'Paused by user';
    } else if (action === 'resume') {
      agent.summary = 'Resumed, working...';
    }

    this.notifyUpdate(agent);

    return {
      success: true,
      agent_id: agentId,
      new_status: newStatus,
    };
  }
}

module.exports = { AgentState, VALID_ACTIONS };
