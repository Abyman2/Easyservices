<script>
  import { onMount } from 'svelte';
  import Icon from './Icon.svelte';

  export let show = false;
  export let onClose = () => {};

  let activeMode = 'CURRENCY';

  // Currency converter — live public reference rates with a local fallback.
  let currencyAmount = 100;
  let sourceCurrency = 'USD';
  let etbRate = null;
  let ratesSource = '';
  let ratesUpdated = '';
  let ratesError = '';
  let ratesLoading = false;

  async function loadRates() {
    ratesLoading = true;
    ratesError = '';
    try {
      const response = await fetch(`https://open.er-api.com/v6/latest/${sourceCurrency}`);
      if (!response.ok) throw new Error('Rate service unavailable');
      const data = await response.json();
      const liveRate = Number(data?.rates?.ETB);
      if (!Number.isFinite(liveRate) || liveRate <= 0) throw new Error('ETB rate unavailable');
      etbRate = liveRate;
      ratesSource = data?.provider || 'ExchangeRate API';
      ratesUpdated = data?.time_last_update_utc || new Date().toISOString();
    } catch (error) {
      etbRate = null;
      ratesSource = '';
      ratesUpdated = '';
      ratesError = 'Live exchange rates are unavailable right now. Please try again.';
    } finally {
      ratesLoading = false;
    }
  }

  onMount(loadRates);
  $: convertedAmount = etbRate === null ? null : Number(currencyAmount || 0) * etbRate;

  // Quick bill split
  let totalAmount = 3000;
  let peopleCount = 4;
  let quickSplitResult = null;
  function handleCalculateQuick() {
    quickSplitResult = (totalAmount / Math.max(1, peopleCount)).toFixed(2);
  }

  // Itemized receipt split
  let receiptItems = [
    { id: 1, name: 'Special Gourmet Pizza', price: 800, assignedTo: ['Abel', 'Sara'] },
    { id: 2, name: 'Ethiopian Beef Burger', price: 450, assignedTo: ['John'] },
    { id: 3, name: 'Fresh Juice & Soft Drinks', price: 300, assignedTo: ['Abel', 'Mimi'] },
    { id: 4, name: 'Crispy French Fries', price: 250, assignedTo: ['Abel', 'Sara', 'John', 'Mimi'] }
  ];
  let participants = ['Abel', 'Sara', 'John', 'Mimi'];
  $: personTotals = participants.reduce((acc, person) => {
    let sum = 0;
    receiptItems.forEach((item) => {
      if (item.assignedTo.includes(person)) sum += item.price / item.assignedTo.length;
    });
    acc[person] = sum;
    return acc;
  }, {});

  function toggleItemAssignment(item, person) {
    if (item.assignedTo.includes(person)) {
      if (item.assignedTo.length > 1) item.assignedTo = item.assignedTo.filter(p => p !== person);
    } else {
      item.assignedTo = [...item.assignedTo, person];
    }
    receiptItems = [...receiptItems];
  }

  // Payer wheel
  let wheelNames = 'Abel, Sara, John, Mimi';
  let isSpinning = false;
  let selectedPayer = null;
  let wheelRotation = 0;
  $: wheelParticipants = wheelNames.split(',').map(s => s.trim()).filter(Boolean);
  $: wheelSegmentAngle = wheelParticipants.length ? 360 / wheelParticipants.length : 360;

  function handleSpinWheel() {
    const list = wheelParticipants;
    if (!list.length) return;
    isSpinning = true;
    selectedPayer = null;
    const winnerIndex = Math.floor(Math.random() * list.length);
    const winnerOffset = 360 - (winnerIndex * wheelSegmentAngle + wheelSegmentAngle / 2);
    wheelRotation += 360 * 10 + winnerOffset;
    setTimeout(() => {
      selectedPayer = list[winnerIndex];
      isSpinning = false;
    }, 5000);
  }

  const destinations = {
    'Addis Ababa': [
      'Use daylight for first-time city exploration and keep your phone charged.',
      'For airport trips, allow extra time for traffic around Bole.',
      'Keep your hotel or host address saved offline for drivers.'
    ],
    'Bishoftu': [
      'Lakefront resorts are easiest to enjoy with an early start.',
      'Carry a light layer for evenings around the lake.',
      'Confirm your return transport before heading out of the resort area.'
    ],
    'Hawassa': [
      'Plan lake activities earlier in the day and keep valuables secure.',
      'Ask your accommodation about local boat/activity operators.',
      'Keep drinking water with you during outdoor activities.'
    ],
    'Lalibela': [
      'Wear comfortable shoes for uneven paths and long walking days.',
      'Carry a light jacket for early mornings and higher elevations.',
      'Check local access guidance before visiting heritage sites.'
    ],
    'Bahir Dar': [
      'Start lake and monastery excursions early for a more comfortable day.',
      'Carry sun protection and water for outdoor sightseeing.',
      'Arrange boat trips through a trusted hotel or established operator.'
    ]
  };
  let selectedDestination = 'Addis Ababa';

  const emergencyContacts = [
    { service: 'Police', number: '991', note: 'Addis Ababa Police Commission' },
    { service: 'Ambulance / Red Cross', number: '907', note: 'Medical emergency line in Addis Ababa' },
    { service: 'Fire & Rescue', number: '939', note: 'Fire and emergency prevention/rescue' },
    { service: 'General emergency', number: '911', note: 'General emergency number; availability can vary by area' }
  ];
</script>

{#if show}
  <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
  <!-- svelte-ignore a11y-click-events-have-key-events -->
  <div class="modal-backdrop" on:click|self={onClose} role="dialog" aria-modal="true">
    <div class="marketplace-card modal-content animate-fade-in">
      <div class="modal-header">
        <div>
          <h2><Icon name="sparkles" size={18} color="var(--accent-gold)" /> Easy Tools</h2>
          <p class="subtitle">Useful travel, money and group-planning tools for your EasyService trip.</p>
        </div>
        <button class="close-btn" on:click={onClose} aria-label="Close Easy Tools">✕</button>
      </div>

      <div class="mode-tabs">
        <button class="mode-tab {activeMode === 'CURRENCY' ? 'active' : ''}" on:click={() => activeMode = 'CURRENCY'}>💱 Currency</button>
        <button class="mode-tab {activeMode === 'GUIDE' ? 'active' : ''}" on:click={() => activeMode = 'GUIDE'}>📍 Local Guide</button>
        <button class="mode-tab {activeMode === 'EMERGENCY' ? 'active' : ''}" on:click={() => activeMode = 'EMERGENCY'}>🚨 Emergency</button>
        <button class="mode-tab {activeMode === 'QUICK' ? 'active' : ''}" on:click={() => activeMode = 'QUICK'}>⚡ Split</button>
        <button class="mode-tab {activeMode === 'WHEEL' ? 'active' : ''}" on:click={() => activeMode = 'WHEEL'}>🎡 Wheel</button>
      </div>

      {#if activeMode === 'CURRENCY'}
        <div class="tool-pane">
          <div class="tool-hero"><span class="tool-icon">💱</span><div><h3>Live Currency Converter</h3><p>Convert USD, EUR or GBP to ETB using the latest available exchange rate.</p></div></div>
          <div class="form-grid">
            <div class="form-field"><label for="currencyAmount">Amount</label><input id="currencyAmount" type="number" min="0" bind:value={currencyAmount} class="input-field" /></div>
            <div class="form-field"><label for="sourceCurrency">From</label><select id="sourceCurrency" bind:value={sourceCurrency} on:change={loadRates} class="input-field"><option value="USD">US Dollar (USD)</option><option value="EUR">Euro (EUR)</option><option value="GBP">Pound (GBP)</option></select></div>
          </div>
          <div class="result-card currency-result"><span class="result-label">{Number(currencyAmount || 0).toLocaleString()} {sourceCurrency} ≈</span><strong class="result-amount">{convertedAmount === null ? 'Unavailable' : `${convertedAmount.toLocaleString(undefined, { maximumFractionDigits: 2 })} ETB`}</strong></div>
          {#if ratesError}<div class="warning-note">{ratesError}</div>{/if}
          <div class="rate-row"><span>{etbRate === null ? 'No live rate loaded' : `1 ${sourceCurrency} = ${etbRate.toFixed(4)} ETB`}</span><span>{ratesLoading ? 'Updating…' : ratesSource ? `${ratesSource} • Retrieved ${ratesUpdated}` : 'Live rate required'}</span><button class="refresh-btn" on:click={loadRates} disabled={ratesLoading} aria-label="Refresh exchange rate">↻</button></div>
        </div>
      {:else if activeMode === 'GUIDE'}
        <div class="tool-pane">
          <div class="tool-hero"><span class="tool-icon">📍</span><div><h3>Local Travel Guide</h3><p>Quick practical reminders for common Ethiopian destinations in the marketplace.</p></div></div>
          <div class="destination-grid">{#each Object.keys(destinations) as destination}<button class="destination-chip {selectedDestination === destination ? 'selected' : ''}" on:click={() => selectedDestination = destination}>{destination}</button>{/each}</div>
          <div class="advice-card"><h3>{selectedDestination}</h3><ul>{#each destinations[selectedDestination] as tip}<li>{tip}</li>{/each}</ul></div>
          <div class="tip-note">💡 EasyService tip: save your booking address, provider phone number and return transport details before leaving Wi‑Fi.</div>
        </div>
      {:else if activeMode === 'EMERGENCY'}
        <div class="tool-pane">
          <div class="tool-hero"><span class="tool-icon">🚨</span><div><h3>Emergency Contacts</h3><p>Keep these useful Addis Ababa/Ethiopia emergency numbers close while travelling.</p></div></div>
          <div class="emergency-grid">{#each emergencyContacts as contact}<a class="emergency-card" href={`tel:${contact.number}`}><div class="emergency-service">{contact.service}</div><strong>{contact.number}</strong><small>{contact.note}</small><span>Tap to call →</span></a>{/each}</div>
          <div class="warning-note">Emergency numbers can vary by location. In a serious emergency, give the operator your exact location and nearby landmark.</div>
        </div>
      {:else if activeMode === 'QUICK'}
        <div class="tool-pane"><div class="form-grid"><div class="form-field"><label for="totalBillInput">Total Bill Amount (ETB)</label><input id="totalBillInput" type="number" min="0" bind:value={totalAmount} class="input-field" /></div><div class="form-field"><label for="peopleCountInput">Number of People</label><input id="peopleCountInput" type="number" min="1" max="50" bind:value={peopleCount} class="input-field" /></div></div><button class="btn-gold calc-btn" on:click={handleCalculateQuick}>Calculate Per Person Share</button>{#if quickSplitResult !== null}<div class="result-card"><span class="result-label">Each Person Pays:</span><strong class="result-amount">ETB {Number(quickSplitResult).toLocaleString()}</strong></div>{/if}</div>
      {:else if activeMode === 'WHEEL'}
        <div class="tool-pane text-center"><div class="form-field"><label for="wheelParticipantsInput">Participant Names (comma separated)</label><input id="wheelParticipantsInput" type="text" bind:value={wheelNames} class="input-field" /></div><div class="wheel-stage"><div class="wheel-pointer">▼</div><div class="wheel-spinner" style={`--wheel-rotation: ${wheelRotation}deg; --wheel-segments: ${Math.max(1, wheelParticipants.length)};`}><div class="wheel-segments">{#each wheelParticipants as name, index}<span class="wheel-name" style={`--segment-index: ${index}; --segment-angle: ${wheelSegmentAngle}deg;`}>{name}</span>{/each}</div><span class="wheel-center-icon">🎯</span></div><button class="btn-gold spin-act-btn" disabled={isSpinning || wheelParticipants.length < 2} on:click={handleSpinWheel}>{isSpinning ? 'Spinning for 5 seconds...' : 'SPIN THE WHEEL! 🎡'}</button></div>{#if selectedPayer}<div class="winner-banner animate-fade-in"><span class="winner-title">🎉 WINNER ANNOUNCED 🎉</span><h3 class="winner-name">{selectedPayer} PAYS THE BILL!</h3></div>{/if}</div>
      {/if}

      <div class="modal-footer"><button class="btn-outline" on:click={onClose}>Close</button></div>
    </div>
  </div>
{/if}

<style>
  .modal-backdrop { position: fixed; inset: 0; width: 100vw; height: 100vh; background: rgba(0,0,0,.75); backdrop-filter: blur(8px); display:flex; justify-content:center; align-items:center; z-index:1000; }
  .modal-content { width:92%; max-width:760px; padding:28px; display:flex; flex-direction:column; gap:16px; max-height:90vh; overflow-y:auto; }
  .modal-header { display:flex; justify-content:space-between; gap:16px; align-items:flex-start; }
  .modal-header h2 { font-size:1.35rem; font-weight:900; color:var(--text-main); display:flex; align-items:center; gap:8px; }
  .subtitle { font-size:.82rem; color:var(--text-muted); margin-top:4px; }
  .close-btn { background:transparent; border:0; font-size:1.25rem; cursor:pointer; color:var(--text-muted); }
  .mode-tabs { display:flex; gap:6px; background:var(--bg-surface-secondary); padding:6px; border-radius:var(--radius-md); overflow-x:auto; }
  .mode-tab { flex:1; min-width:max-content; padding:9px 10px; font-size:.78rem; font-weight:800; border:0; border-radius:var(--radius-sm); background:transparent; cursor:pointer; color:var(--text-muted); }
  .mode-tab.active { background:var(--bg-surface); color:var(--accent-gold); }
  .tool-pane { display:flex; flex-direction:column; gap:16px; }
  .tool-hero { display:flex; align-items:center; gap:12px; padding:14px; border:1px solid var(--border-subtle); border-radius:var(--radius-md); background:var(--bg-surface-secondary); }
  .tool-icon { font-size:1.8rem; }
  .tool-hero h3 { margin:0; font-size:1rem; }
  .tool-hero p { margin:4px 0 0; color:var(--text-muted); font-size:.78rem; }
  .form-grid { display:grid; grid-template-columns:1fr 1fr; gap:12px; }
  .form-field { display:flex; flex-direction:column; gap:6px; }
  .form-field label { font-size:.72rem; font-weight:800; color:var(--text-muted); }
  .input-field { width:100%; box-sizing:border-box; padding:10px 12px; border:1px solid var(--border-subtle); border-radius:var(--radius-sm); background:var(--bg-surface); color:var(--text-main); }
  .btn-gold { border:0; background:var(--accent-gold); color:#fff; font-weight:900; border-radius:var(--radius-md); padding:11px 15px; cursor:pointer; }
  .btn-outline { border:1px solid var(--border-subtle); background:transparent; color:var(--text-main); padding:10px 15px; border-radius:var(--radius-md); cursor:pointer; }
  .result-card { padding:16px; border-radius:var(--radius-md); background:var(--bg-surface-secondary); display:flex; justify-content:space-between; align-items:center; gap:10px; }
  .result-label { color:var(--text-muted); font-size:.78rem; }
  .result-amount { color:var(--accent-gold); font-size:1.35rem; }
  .rate-row { display:flex; align-items:center; gap:8px; color:var(--text-muted); font-size:.7rem; flex-wrap:wrap; }
  .rate-row span:last-of-type { margin-left:auto; }
  .refresh-btn { border:1px solid var(--border-subtle); background:transparent; border-radius:50%; width:28px; height:28px; cursor:pointer; color:var(--text-main); }
  .destination-grid { display:flex; gap:7px; flex-wrap:wrap; }
  .destination-chip { border:1px solid var(--border-subtle); background:var(--bg-surface); color:var(--text-main); border-radius:999px; padding:8px 12px; cursor:pointer; font-weight:700; font-size:.75rem; }
  .destination-chip.selected { border-color:var(--accent-gold); color:var(--accent-gold); background:var(--accent-gold-light); }
  .advice-card { padding:16px; border-radius:var(--radius-md); background:var(--bg-surface-secondary); }
  .advice-card h3 { margin:0 0 8px; }
  .advice-card ul { margin:0; padding-left:20px; display:grid; gap:8px; color:var(--text-muted); font-size:.82rem; }
  .tip-note,.warning-note { padding:12px; border-radius:var(--radius-md); background:var(--accent-gold-light); color:var(--text-main); font-size:.76rem; line-height:1.4; }
  .emergency-grid { display:grid; grid-template-columns:1fr 1fr; gap:10px; }
  .emergency-card { text-decoration:none; color:var(--text-main); border:1px solid var(--border-subtle); border-radius:var(--radius-md); padding:14px; background:var(--bg-surface-secondary); display:flex; flex-direction:column; gap:4px; }
  .emergency-card strong { font-size:1.45rem; color:var(--accent-gold); }
  .emergency-card small { color:var(--text-muted); font-size:.72rem; line-height:1.35; }
  .emergency-card span { font-size:.7rem; font-weight:800; margin-top:5px; }
  .wheel-stage { position:relative; display:flex; flex-direction:column; align-items:center; gap:18px; padding:10px; }
  .wheel-pointer { position:absolute; top:0; z-index:2; color:var(--accent-gold); font-size:1.8rem; line-height:1; }
  .wheel-spinner { position:relative; width:220px; height:220px; border-radius:50%; border:10px solid var(--accent-gold); background:conic-gradient(from 0deg, var(--accent-gold-light) 0deg 45deg, var(--bg-surface-secondary) 45deg 90deg, #e9d8aa 90deg 135deg, var(--bg-surface-secondary) 135deg 180deg, var(--accent-gold-light) 180deg 225deg, var(--bg-surface-secondary) 225deg 270deg, #e9d8aa 270deg 315deg, var(--bg-surface-secondary) 315deg 360deg); display:flex; align-items:center; justify-content:center; box-shadow:0 10px 35px rgba(0,0,0,.12); transform:rotate(var(--wheel-rotation)); transition:transform 5s cubic-bezier(.12,.75,.15,1); }
  .wheel-segments { position:absolute; inset:0; border-radius:50%; }
  .wheel-name { position:absolute; left:50%; top:50%; width:92px; margin-left:-46px; transform:rotate(calc(var(--segment-index) * var(--segment-angle))) translateY(-82px) rotate(calc(var(--segment-index) * var(--segment-angle) * -1)); text-align:center; font-size:.75rem; font-weight:900; color:var(--text-main); overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
  .wheel-center-icon { position:relative; z-index:1; font-size:2rem; transform:rotate(calc(var(--wheel-rotation) * -1)); }
  .spin-act-btn { min-width:220px; }
  .winner-banner { text-align:center; padding:16px; border-radius:var(--radius-md); background:var(--accent-gold-light); }
  .winner-title { font-size:.72rem; font-weight:900; color:var(--accent-gold); }
  .winner-name { margin:5px 0 0; }
  .modal-footer { display:flex; justify-content:flex-end; padding-top:4px; }
  @media (max-width:600px) { .modal-content { width:calc(100% - 24px); padding:18px; max-height:88vh; } .form-grid,.emergency-grid { grid-template-columns:1fr; } .mode-tabs { gap:4px; } .mode-tab { padding:8px; } .rate-row span:last-of-type { margin-left:0; } .result-card { flex-direction:column; align-items:flex-start; } }
</style>
