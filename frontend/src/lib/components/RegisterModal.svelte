<script>
  import { currentUser } from '../stores/authStore.js';

  export let show = false;
  export let onClose = () => {};

  let name = '';
  let email = '';
  let customerType = 'ETHIOPIAN'; // 'ETHIOPIAN' | 'FOREIGNER'
  let identityValue = '';
  let errorMsg = '';

  function handleRegister() {
    errorMsg = '';
    if (!name || !email || !identityValue) {
      errorMsg = 'Please complete all required fields.';
      return;
    }

    // BR-01 Validation
    if (customerType === 'ETHIOPIAN' && !identityValue.toUpperCase().startsWith('FY')) {
      errorMsg = 'BR-01 Violation: Ethiopian customers must provide a valid Fayda ID (starting with FY).';
      return;
    }

    if (customerType === 'FOREIGNER' && !identityValue.toUpperCase().startsWith('P')) {
      errorMsg = 'BR-01 Violation: Foreign customers must provide a valid Passport number (starting with P).';
      return;
    }

    $currentUser = {
      id: 'cust_' + Date.now(),
      name,
      email,
      customerType,
      identityType: customerType === 'ETHIOPIAN' ? 'Fayda ID' : 'Passport',
      identityStatus: customerType === 'ETHIOPIAN' ? 'FAYDA VERIFIED' : 'PASSPORT VERIFIED',
      balance: 5000.00,
      role: 'CUSTOMER'
    };

    onClose();
  }
</script>

{#if show}
  <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
  <!-- svelte-ignore a11y-click-events-have-key-events -->
  <div class="modal-backdrop" on:click|self={onClose} role="dialog" aria-modal="true">
    <div class="marketplace-card modal-content animate-fade-in">
      <div class="modal-header">
        <h2>Customer Registration</h2>
        <button class="close-btn" on:click={onClose}>✕</button>
      </div>

      {#if errorMsg}
        <div class="alert alert-error">{errorMsg}</div>
      {/if}

      <div class="form-group">
        <label for="nameInput">Full Name</label>
        <input id="nameInput" type="text" placeholder="e.g. Abebe Kebede" bind:value={name} class="input-field" />
      </div>

      <div class="form-group">
        <label for="emailInput">AAU / Email Address</label>
        <input id="emailInput" type="email" placeholder="name@aau.edu.et" bind:value={email} class="input-field" />
      </div>

      <div class="form-group">
        <label for="customerTypeSelect">Customer Type (BR-01)</label>
        <select id="customerTypeSelect" bind:value={customerType} class="input-field">
          <option value="ETHIOPIAN">Ethiopian National (Fayda Required)</option>
          <option value="FOREIGNER">Foreign Resident (Passport Required)</option>
        </select>
      </div>

      <div class="form-group">
        <label for="identityInput">
          {customerType === 'ETHIOPIAN' ? 'Fayda National ID (e.g. FY123456)' : 'Passport Number (e.g. P987654)'}
        </label>
        <input id="identityInput" type="text" bind:value={identityValue} class="input-field" />
      </div>

      <div class="modal-actions">
        <button class="btn-outline" on:click={onClose}>Cancel</button>
        <button class="btn-gold" on:click={handleRegister}>Complete Registration</button>
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
    background: rgba(0, 0, 0, 0.7);
    backdrop-filter: blur(8px);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 11000;
  }

  .modal-content {
    width: 90%;
    max-width: 500px;
    padding: 32px;
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .close-btn {
    background: transparent;
    border: none;
    color: var(--text-muted);
    font-size: 1.5rem;
    cursor: pointer;
  }

  .alert-error {
    background: rgba(239, 68, 68, 0.2);
    border: 1px solid rgba(239, 68, 68, 0.4);
    color: #fca5a5;
    padding: 12px;
    border-radius: var(--radius-sm);
    font-size: 0.9rem;
    font-weight: 600;
  }

  .modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 8px;
  }
</style>
