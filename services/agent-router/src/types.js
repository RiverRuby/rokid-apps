/**
 * Type Definitions for Agent Router
 *
 * This file contains JSDoc type definitions for the agent router.
 * These types match the EVENT_SCHEMA.md documentation.
 */

/**
 * Agent status values
 * @typedef {'RUNNING' | 'PAUSED' | 'WAITING_APPROVAL' | 'ERROR' | 'DONE'} AgentStatus
 */

/**
 * Agent action values
 * @typedef {'pause' | 'resume' | 'approve' | 'deny' | 'ack' | 'retry'} AgentAction
 */

/**
 * Agent object
 * @typedef {Object} Agent
 * @property {string} agent_id - Unique identifier for the agent
 * @property {string} name - Human-readable agent name
 * @property {AgentStatus} status - Current agent status
 * @property {string} summary - One-line description of current state
 * @property {string} [detail] - Extended description (multi-line)
 * @property {number} ts - Unix timestamp of last update
 * @property {AgentAction[]} actions - Available actions for current status
 * @property {string} [link] - URL for more details (PR, issue, etc.)
 */

/**
 * Snapshot message - sent on WebSocket connect
 * @typedef {Object} SnapshotMessage
 * @property {'snapshot'} type
 * @property {number} ts - Unix timestamp
 * @property {Agent[]} agents - All current agents
 */

/**
 * Agent update message - sent when agent state changes
 * @typedef {Object} AgentUpdateMessage
 * @property {'agent_update'} type
 * @property {string} agent_id
 * @property {string} name
 * @property {AgentStatus} status
 * @property {string} summary
 * @property {string} [detail]
 * @property {number} ts
 * @property {AgentAction[]} actions
 * @property {string} [link]
 */

/**
 * Agent action request - sent from HUD to router
 * @typedef {Object} AgentActionRequest
 * @property {'agent_action'} type
 * @property {string} agent_id
 * @property {AgentAction} action
 * @property {Object} [payload]
 * @property {number} ts
 */

/**
 * Action response - returned from /action endpoint
 * @typedef {Object} ActionResponse
 * @property {boolean} success
 * @property {string} [agent_id]
 * @property {AgentStatus} [new_status]
 * @property {string} [error]
 */

/**
 * Valid actions for each status
 * @type {Record<AgentStatus, AgentAction[]>}
 */
const VALID_ACTIONS_MAP = {
  RUNNING: ['pause'],
  PAUSED: ['resume'],
  WAITING_APPROVAL: ['approve', 'deny'],
  ERROR: ['ack', 'retry'],
  DONE: [],
};

/**
 * Action to new status transitions
 * @type {Record<AgentAction, AgentStatus>}
 */
const ACTION_TRANSITIONS_MAP = {
  pause: 'PAUSED',
  resume: 'RUNNING',
  approve: 'RUNNING',
  deny: 'PAUSED',
  ack: 'PAUSED',
  retry: 'RUNNING',
};

module.exports = {
  VALID_ACTIONS_MAP,
  ACTION_TRANSITIONS_MAP,
};
