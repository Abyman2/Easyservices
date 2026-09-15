<script>
  import Icon from './Icon.svelte';

  export let booking = null;
  export let currentLanguage = 'en';
  export let onClose = () => {};
  export let onCancel = (id) => {};
  export let onComplete = (id) => {};
  export let onReview = (id, rating, reviewText) => {};
  let cancellationReason = 'Change of plans';
  let rating = 5;
  let reviewText = '';
  const customerCancellationReasons = ['Change of plans', 'Found another option', 'Dates no longer work', 'Booked by mistake', 'Travel disruption'];
  $: isAmharic = currentLanguage === 'am';
  $: labels = isAmharic
    ? { close: 'ዝጋ', cancel: 'ሰርዝ እና ገንዘብ መልስ', finish: 'እንደተጠናቀቀ ምልክት አድርግ', reason: 'የስረዛ ምክንያት' }
    : { close: 'Close Pass', cancel: 'Cancel & Instant Wallet Refund (BR-14)', finish: 'Mark as finished', reason: 'Cancellation reason' };
</script>

{#if booking}
  <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
  <!-- svelte-ignore a11y-click-events-have-key-events -->
  <div class="modal-backdrop" on:click|self={onClose} role="dialog" aria-modal="true">
    <div class="marketplace-card booking-details-modal animate-fade-in">
      <div class="modal-header">
        <div class="header-left">
          <span class="badge-passport">📖 EASYSERVICE RESERVATION PASS</span>
          <h2>{booking.listingTitle}</h2>
        </div>
        <button class="close-btn" on:click={onClose}>✕</button>
      </div>

      <div class="pass-status-bar">
        <span class="status-chip {booking.status === 'CONFIRMED' || booking.status === 'COMPLETED' ? 'active' : 'cancelled'}">
          ● {booking.status === 'COMPLETED' ? 'COMPLETED' : booking.status === 'CONFIRMED' ? 'VALID / ACTIVE RESERVATION' : 'REFUNDED & CANCELLED'}
        </span>
        <span class="tx-id">Tx Ref: <code>{booking.id}</code></span>
      </div>

      <div class="pass-body-grid">
        <!-- Left Image & Location -->
        <div class="pass-media-column">
          <img src={booking.imageUrl} alt={booking.listingTitle} class="pass-cover-img" />
          <div class="media-meta">
            <p class="loc-pin"><Icon name="mappin" size={14} color="var(--accent-gold)" /> {booking.location}</p>
            <p class="date-tag">🗓️ Booking Date: <strong>{booking.bookingDate}</strong></p>
          </div>
        </div>

        <!-- Right Entry Pass Details & QR Code -->
        <div class="pass-info-column">
          <div class="qr-box">
            <div class="qr-code-graphic">
              <svg viewBox="0 0 100 100" width="110" height="110">
                <!-- Simulated QR matrix lines -->
                <rect x="0" y="0" width="30" height="30" fill="var(--text-main)" />
                <rect x="5" y="5" width="20" height="20" fill="var(--bg-surface)" />
                <rect x="10" y="10" width="10" height="10" fill="var(--text-main)" />

                <rect x="70" y="0" width="30" height="30" fill="var(--text-main)" />
                <rect x="75" y="5" width="20" height="20" fill="var(--bg-surface)" />
                <rect x="80" y="10" width="10" height="10" fill="var(--text-main)" />

                <rect x="0" y="70" width="30" height="30" fill="var(--text-main)" />
                <rect x="5" y="75" width="20" height="20" fill="var(--bg-surface)" />
                <rect x="10" y="80" width="10" height="10" fill="var(--text-main)" />

                <rect x="35" y="35" width="12" height="12" fill="var(--accent-gold)" />
                <rect x="50" y="20" width="15" height="8" fill="var(--text-main)" />
                <rect x="40" y="55" width="20" height="20" fill="var(--text-main)" />
                <rect x="65" y="65" width="15" height="15" fill="var(--accent-gold)" />
              </svg>
            </div>
            <span class="qr-label">SCAN AT CHECK-IN</span>
          </div>

          <div class="breakdown-list">
            <div class="breakdown-item">
              <span class="lbl">Customer Identity</span>
              <span class="val">✓ Fayda Verified</span>
            </div>
            <div class="breakdown-item">
              <span class="lbl">Reserved Quantity</span>
              <span class="val">{booking.quantity} Pass(es)</span>
            </div>
            <div class="breakdown-item">
              <span class="lbl">Simulated Wallet Paid</span>
              <span class="val price">ETB {booking.totalAmount.toLocaleString()}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <button class="btn-outline" on:click={onClose}>{labels.close}</button>
        {#if booking.status === 'CONFIRMED'}
            <select class="reason-select" bind:value={cancellationReason} aria-label={labels.reason}>
            {#each customerCancellationReasons as reason}<option value={reason}>{reason}</option>{/each}
          </select>
          <button class="btn-outline cancel-btn" on:click={() => { onCancel(booking.id, cancellationReason); onClose(); }}>
            {labels.cancel}
          </button>
        {/if}
        {#if booking.status === 'CONFIRMED'}<button class="btn-gold" on:click={() => onComplete(booking.id)}>{labels.finish}</button>{/if}
      </div>
      {#if booking.status === 'COMPLETED' && !booking.rating}
        <div class="review-box"><h3>How was your experience?</h3><div class="rating-row">{#each [1, 2, 3, 4, 5] as value}<button class:chosen={value <= rating} on:click={() => rating = value} aria-label={`${value} stars`}>★</button>{/each}</div><textarea bind:value={reviewText} placeholder="Tell the provider what went well" rows="3"></textarea><button class="btn-gold" on:click={() => onReview(booking.id, rating, reviewText)}>Submit rating and review</button></div>
      {:else if booking.rating}<div class="review-box"><h3>Your review</h3><div class="rating-row">{'★'.repeat(booking.rating)}{'☆'.repeat(5 - booking.rating)}</div><p>{booking.reviewText}</p></div>{/if}
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
    background: rgba(0, 0, 0, 0.75);
    backdrop-filter: blur(8px);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
  }

  .booking-details-modal {
    width: 90%;
    max-width: 620px;
    padding: 24px;
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
  }

  .badge-passport {
    font-size: 0.68rem;
    font-weight: 800;
    color: var(--accent-gold);
    letter-spacing: 0.08em;
  }

  .header-left h2 {
    font-size: 1.35rem;
    font-weight: 800;
    color: var(--text-main);
  }

  .close-btn {
    background: transparent;
    border: none;
    color: var(--text-muted);
    font-size: 1.25rem;
    cursor: pointer;
  }

  .pass-status-bar {
    background: var(--bg-surface-secondary);
    padding: 10px 14px;
    border-radius: var(--radius-md);
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .status-chip {
    font-size: 0.75rem;
    font-weight: 800;
  }

  .status-chip.active { color: var(--status-success-text); }
  .status-chip.cancelled { color: var(--accent-terracotta); }

  .tx-id {
    font-size: 0.78rem;
    color: var(--text-muted);
  }

  .pass-body-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
  }

  .pass-cover-img {
    width: 100%;
    height: 160px;
    object-fit: cover;
    border-radius: var(--radius-md);
  }

  .media-meta {
    margin-top: 10px;
    display: flex;
    flex-direction: column;
    gap: 4px;
    font-size: 0.85rem;
  }

  .loc-pin { font-weight: 700; color: var(--text-main); }
  .date-tag { color: var(--text-muted); }

  .pass-info-column {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
  }

  .qr-box {
    background: #ffffff;
    padding: 12px;
    border-radius: var(--radius-md);
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 6px;
    border: 1px solid var(--border-subtle);
  }

  .qr-label {
    font-size: 0.65rem;
    font-weight: 800;
    color: #111827;
    letter-spacing: 0.08em;
  }

  .breakdown-list {
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 6px;
  }

  .breakdown-item {
    display: flex;
    justify-content: space-between;
    font-size: 0.82rem;
  }

  .breakdown-item .lbl { color: var(--text-muted); }
  .breakdown-item .val { font-weight: 700; color: var(--text-main); }
  .breakdown-item .price { color: var(--accent-gold); font-size: 0.95rem; }

  .modal-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    border-top: 1px solid var(--border-subtle);
    padding-top: 14px;
  }

  .reason-select { min-width: 0; flex: 1 1 150px; padding: 10px; border: 1px solid var(--border-subtle); border-radius: var(--radius-md); background: var(--bg-surface); color: var(--text-main); }

  .cancel-btn {
    border-color: var(--accent-terracotta);
    color: var(--accent-terracotta);
  }

  .cancel-btn:hover {
    background: var(--accent-terracotta);
    color: #ffffff;
  }

  @media (max-width: 600px) {
    .modal-backdrop { align-items: flex-start; overflow-y: auto; padding: 12px 0; }
    .booking-details-modal { width: calc(100% - 24px); max-height: calc(100vh - 24px); overflow-y: auto; padding: 16px; gap: 12px; }
    .header-left h2 { font-size: 1.15rem; line-height: 1.2; margin: 6px 0 0; overflow-wrap: anywhere; }
    .pass-status-bar, .breakdown-item { align-items: flex-start; gap: 8px; flex-wrap: wrap; }
    .tx-id { overflow-wrap: anywhere; }
    .pass-body-grid { grid-template-columns: 1fr; gap: 14px; }
    .pass-cover-img { height: 150px; }
    .modal-footer { flex-direction: column; align-items: stretch; gap: 8px; }
    .modal-footer button, .reason-select { width: 100%; min-height: 44px; }
    .review-box textarea { width: 100%; box-sizing: border-box; }
  }
</style>
