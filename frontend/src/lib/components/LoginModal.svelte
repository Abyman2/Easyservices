<script>
  import { demoUsers, currentUser } from '../stores/authStore.js';
  import { userBookings } from '../stores/bookingStore.js';
  import Icon from './Icon.svelte';

  export let show = false;
  export let onOpenRegister = () => {};

  let email = '';
  let password = '';
  let loginError = '';

  function handleLogin() {
    loginError = '';
    if (!email) {
      loginError = 'Please enter an email address or select a quick login account.';
      return;
    }
    
    // Find the user by email (ignore password for testing purposes)
    const foundUser = demoUsers.find(u => u.email.toLowerCase() === email.toLowerCase());
    
    if (foundUser) {
      $currentUser = foundUser;
      userBookings.set([]); // Reset bookings so it starts fresh!
      email = '';
      password = '';
    } else {
      loginError = 'Authentication failed. Please check your credentials or use a test account.';
    }
  }

  function handleQuickLogin(user) {
    loginError = '';
    $currentUser = user;
    userBookings.set([]); // Reset bookings so it starts fresh!
    email = '';
    password = '';
  }
</script>

{#if show && !$currentUser}
  <div class="modal-backdrop" role="dialog" aria-modal="true">
    <div class="marketplace-card modal-content animate-fade-in">
      <div class="modal-header">
        <div>
          <h2><Icon name="user" size={20} color="var(--accent-gold)" /> EasyService Login</h2>
          <p class="subtitle">Secure Authentication via Fayda / AAU SSO</p>
        </div>
      </div>

      {#if loginError}
        <div class="alert alert-error">{loginError}</div>
      {/if}

      <div class="step-body">
        <div class="form-group">
          <label for="emailInput">Email Address</label>
          <input id="emailInput" type="email" bind:value={email} placeholder="name@aau.edu.et" class="input-field" on:keydown={(e) => e.key === 'Enter' && handleLogin()} />
        </div>
        
        <div class="form-group">
          <label for="passwordInput">Password</label>
          <input id="passwordInput" type="password" bind:value={password} placeholder="••••••••" class="input-field" on:keydown={(e) => e.key === 'Enter' && handleLogin()} />
        </div>

        <button class="btn-gold login-btn" on:click={handleLogin}>Log In</button>
        <button class="btn-outline" type="button" on:click={onOpenRegister}>Create a new customer account</button>

        <hr />

        <div class="quick-login-section">
          <h4>🧪 Testing: Quick Login Accounts</h4>
          <p class="test-note">Select a profile below to instantly log in and start a fresh session.</p>
          
          <div class="test-users-grid">
            {#each demoUsers as user}
              <button class="test-user-card" on:click={() => handleQuickLogin(user)}>
                <div class="test-user-header">
                  <strong>{user.name}</strong>
                  <span class="badge {user.role === 'PROVIDER' ? 'provider-badge' : 'customer-badge'}">{user.role}</span>
                </div>
                <div class="test-user-meta">
                  <span>{user.email}</span>
                  <span>ETB {user.balance.toLocaleString()}</span>
                </div>
              </button>
            {/each}
          </div>
        </div>
      </div>
    </div>
  </div>
{/if}

<style>
  .modal-backdrop {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background: rgba(0, 0, 0, 0.85);
    backdrop-filter: blur(10px);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 9999;
  }

  .modal-content {
    width: 92%;
    max-width: 500px;
    padding: 30px;
    display: flex;
    flex-direction: column;
    gap: 16px;
    background: var(--bg-surface);
  }

  .modal-header h2 {
    font-size: 1.35rem;
    font-weight: 900;
    color: var(--text-main);
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .subtitle {
    font-size: 0.8rem;
    color: var(--text-muted);
  }

  .step-body {
    display: flex;
    flex-direction: column;
    gap: 14px;
  }

  .form-group {
    display: flex;
    flex-direction: column;
    gap: 6px;
  }

  .form-group label {
    font-size: 0.76rem;
    font-weight: 800;
    color: var(--text-muted);
  }

  .input-field {
    padding: 10px 12px;
    font-size: 0.95rem;
    border-radius: var(--radius-sm);
    border: 1px solid var(--border-subtle);
    background: var(--bg-main);
    color: var(--text-main);
  }

  .login-btn {
    width: 100%;
    padding: 12px;
    font-size: 1rem;
    margin-top: 10px;
  }

  hr {
    border: none;
    height: 1px;
    background: var(--border-subtle);
    margin: 10px 0;
  }

  .quick-login-section {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  .quick-login-section h4 {
    font-size: 0.95rem;
    font-weight: 800;
  }

  .test-note {
    font-size: 0.8rem;
    color: var(--text-muted);
  }

  .test-users-grid {
    display: flex;
    flex-direction: column;
    gap: 8px;
    max-height: 200px;
    overflow-y: auto;
    padding-right: 4px;
  }

  .test-user-card {
    background: var(--bg-surface-secondary);
    border: 1px solid var(--border-subtle);
    padding: 10px 14px;
    border-radius: var(--radius-md);
    cursor: pointer;
    text-align: left;
    display: flex;
    flex-direction: column;
    gap: 4px;
    transition: all 0.2s ease;
  }

  .test-user-card:hover {
    border-color: var(--accent-gold);
    background: rgba(212, 175, 55, 0.05);
  }

  .test-user-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 0.9rem;
    font-weight: 800;
    color: var(--text-main);
  }

  .test-user-meta {
    display: flex;
    justify-content: space-between;
    font-size: 0.75rem;
    color: var(--text-muted);
  }

  .badge {
    font-size: 0.65rem;
    padding: 2px 6px;
    border-radius: 4px;
    font-weight: 800;
  }

  .customer-badge {
    background: rgba(16, 185, 129, 0.15);
    color: var(--status-success-text);
  }

  .provider-badge {
    background: rgba(200, 155, 60, 0.15);
    color: var(--accent-gold);
  }
</style>
