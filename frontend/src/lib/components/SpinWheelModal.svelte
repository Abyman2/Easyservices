<script>
  import { currentUser, registerPromoCode } from '../stores/authStore.js';
  import Icon from './Icon.svelte';

  export let show = false;
  export let currentLanguage = 'en';
  export let onClose = () => {};

  let spinning = false;
  let rotationAngle = 0;
  let wonResult = null;
  let generatedUniqueCode = '';
  let spinsToday = 0;
  let spinErrorMsg = '';
  let copied = false;
  $: isAmharic = currentLanguage === 'am';
  $: spinLabels = isAmharic
    ? { title: 'የEasyService ዕድል መሽከርከሪያ', spins: 'የዛሬ መሽከርከሪያ', next: 'ቀጣይ መሽከርከሪያ', free: 'ነፃ (የመጀመሪያ 3)', wallet: 'የኪስ ቦርሳ', limit: 'የዕለት መሽከርከሪያ ገደብ ደርሷል። ነገ እንደገና ይሞክሩ!', spin: 'አሁን ይሽከርከሩ', spinning: 'እየተሽከረከረ ነው...', copied: 'ተቀድቷል!' }
    : { title: 'EasyService Lucky Spin Wheel', spins: 'Spins Today', next: 'Next Spin', free: 'FREE (First 3)', wallet: 'Wallet', limit: 'Daily spin limit reached (15/15 spins used today). Please try again tomorrow!', spin: 'SPIN NOW', spinning: 'Spinning Wheel...', copied: 'Copied!' };

  // 6 Sectors array
  const sectors = [
    { id: 'NO_LUCK_1', text: 'Better Luck Next Time', type: 'NONE', color: '#4B5563' },
    { id: 'EASY5', text: '5% OFF DISCOUNT', type: 'LOW', baseCode: 'EASY5', percent: 5, color: '#3B82F6' },
    { id: 'NO_LUCK_2', text: 'Try Again Tomorrow', type: 'NONE', color: '#6B7280' },
    { id: 'WIN10', text: '10% OFF DISCOUNT', type: 'LOW', baseCode: 'WIN10', percent: 10, color: '#10B981' },
    { id: 'NO_LUCK_3', text: 'Better Luck Next Time', type: 'NONE', color: '#374151' },
    { id: 'GOLD25', text: '25% OFF JACKPOT', type: 'HIGH', baseCode: 'GOLD25', percent: 25, color: '#C89B3C' }
  ];

  $: currentSpinCost = spinsToday < 3 ? 0 : (spinsToday < 9 ? 50 : 100);

  function spinWheel() {
    if (spinning) return;
    spinErrorMsg = '';
    copied = false;

    if (spinsToday >= 15) {
      spinErrorMsg = spinLabels.limit;
      return;
    }

    const cost = currentSpinCost;
    if (cost > 0) {
      if (!$currentUser || $currentUser.balance < cost) {
        spinErrorMsg = `Insufficient EasyService Wallet balance! This spin costs ETB ${cost}, but your current balance is ETB ${$currentUser?.balance?.toLocaleString() || 0}. Please top up your balance.`;
        return;
      }
      // Deduct spin cost reactively
      currentUser.update(u => ({ ...u, balance: Math.max(0, u.balance - cost) }));
    }

    spinning = true;
    wonResult = null;
    generatedUniqueCode = '';
    spinsToday += 1;

    // Weighted Probability logic:
    // 70% -> Lands on NO_LUCK (idx 0, 2, 4)
    // 22% -> Lands on LOW prize (idx 1, 3)
    // 8%  -> Lands on HIGH prize (idx 5)
    const rand = Math.random() * 100;
    let selectedIdx = 0;

    if (rand < 70) {
      const noLuckIndices = [0, 2, 4];
      selectedIdx = noLuckIndices[Math.floor(Math.random() * noLuckIndices.length)];
    } else if (rand < 92) {
      const lowIndices = [1, 3];
      selectedIdx = lowIndices[Math.floor(Math.random() * lowIndices.length)];
    } else {
      selectedIdx = 5;
    }

    // Sector angle calibration
    const sectorCenterAngle = selectedIdx * 60 + 30;
    const requiredRotation = (270 - sectorCenterAngle + 360) % 360;
    const fullSpins = 5 * 360;
    
    const currentModulo = rotationAngle % 360;
    const nextTargetAngle = rotationAngle + fullSpins + ((requiredRotation - currentModulo + 360) % 360);

    rotationAngle = nextTargetAngle;

    setTimeout(() => {
      spinning = false;
      wonResult = sectors[selectedIdx];

      if (wonResult.type !== 'NONE') {
        // Generate unique code per spin: e.g. WIN10-8492, EASY5-3910, GOLD25-7712
        const randomSuffix = Math.floor(1000 + Math.random() * 9000).toString();
        generatedUniqueCode = `${wonResult.baseCode}-${randomSuffix}`;
        registerPromoCode(generatedUniqueCode, wonResult.percent);
      }
    }, 3200);
  }

  function handleCopyCode() {
    if (!generatedUniqueCode) return;
    try {
      if (navigator.clipboard && window.isSecureContext) {
        navigator.clipboard.writeText(generatedUniqueCode);
      } else {
        const textarea = document.createElement('textarea');
        textarea.value = generatedUniqueCode;
        textarea.style.position = 'fixed';
        textarea.style.opacity = '0';
        document.body.appendChild(textarea);
        textarea.select();
        document.execCommand('copy');
        document.body.removeChild(textarea);
      }
    } catch (e) {
      console.warn('Clipboard write failed:', e);
    }
    copied = true;
    setTimeout(() => copied = false, 3000);
  }
</script>

{#if show}
  <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
  <!-- svelte-ignore a11y-click-events-have-key-events -->
  <div class="modal-backdrop" on:click|self={onClose} role="dialog" aria-modal="true">
    <div class="marketplace-card modal-content animate-fade-in">
      <div class="modal-header">
        <div class="modal-title-row">
          <Icon name="promo" size={22} color="var(--accent-gold)" />
          <h2>{spinLabels.title}</h2>
        </div>
        <button class="close-btn" on:click={onClose}>✕</button>
      </div>

      <!-- Tiered Rules Banner -->
      <div class="rules-bar">
        <span class="rule-chip">{spinLabels.spins}: <strong>{spinsToday}/15</strong></span>
        <span class="rule-chip cost-chip">
          {spinLabels.next}: <strong>{currentSpinCost === 0 ? spinLabels.free : `ETB ${currentSpinCost}`}</strong>
        </span>
        <span class="rule-chip wallet-chip">
          {spinLabels.wallet}: <strong>ETB {$currentUser?.balance?.toLocaleString() || 0}</strong>
        </span>
      </div>

      {#if spinErrorMsg}
        <div class="spin-error-alert">{spinErrorMsg}</div>
      {/if}

      <div class="wheel-container">
        <div class="wheel-pointer">▼</div>

        <div class="wheel-disc" style="transform: rotate({rotationAngle}deg); transition: {spinning ? 'transform 3.2s cubic-bezier(0.15, 0.9, 0.2, 1)' : 'none'};">
          <svg viewBox="0 0 200 200" width="230" height="230">
            {#each sectors as s, i}
              {@const startAngle = (i * 360 / sectors.length) * (Math.PI / 180)}
              {@const endAngle = ((i + 1) * 360 / sectors.length) * (Math.PI / 180)}
              {@const x1 = 100 + 95 * Math.cos(startAngle)}
              {@const y1 = 100 + 95 * Math.sin(startAngle)}
              {@const x2 = 100 + 95 * Math.cos(endAngle)}
              {@const y2 = 100 + 95 * Math.sin(endAngle)}
              {@const textAngle = ((i + 0.5) * 360 / sectors.length)}
              
              <path d="M 100 100 L {x1} {y1} A 95 95 0 0 1 {x2} {y2} Z" fill={s.color} opacity="0.92" stroke="#ffffff" stroke-width="1.5" />
              
              <text 
                x="100" y="32" 
                fill="#ffffff" 
                font-size="7.5" 
                font-weight="800" 
                text-anchor="middle" 
                transform="rotate({textAngle + 90}, 100, 100)">
                {isAmharic ? ({ NO_LUCK_1: 'እንደገና ይሞክሩ', EASY5: '5% ቅናሽ', NO_LUCK_2: 'ነገ ይሞክሩ', WIN10: '10% ቅናሽ', NO_LUCK_3: 'እድል ይሞክሩ', GOLD25: '25% ትልቅ ቅናሽ' }[s.id]) : `${s.text.split(' ')[0]} ${s.text.split(' ')[1] || ''}`}
              </text>
            {/each}
            <circle cx="100" cy="100" r="22" fill="var(--bg-surface)" stroke="var(--accent-gold)" stroke-width="3"/>
          </svg>
        </div>
      </div>

      {#if wonResult}
        {#if wonResult.type === 'NONE'}
          <div class="result-card result-no-luck animate-fade-in">
            <span class="result-tag">{isAmharic ? 'በሚቀጥለው ጊዜ መልካም ዕድል' : 'BETTER LUCK NEXT TIME'}</span>
            <p class="result-desc">{isAmharic ? 'በዚህ መሽከርከሪያ ቅናሽ አላሸነፉም። እንደገና ይሞክሩ!' : 'No discount won this spin. Try again for another chance!'}</p>
          </div>
        {:else}
          <div class="result-card result-win animate-fade-in">
            <span class="win-tag">🎉 CONGRATULATIONS! YOU WON A UNIQUE PROMO CODE:</span>
            <div class="code-copy-box">
              <span class="win-code">{generatedUniqueCode}</span>
              <button class="copy-btn" on:click={handleCopyCode}>
                {copied ? `✓ ${spinLabels.copied}` : (isAmharic ? '📋 ኮዱን ቅዳ' : '📋 Copy Code')}
              </button>
            </div>
            <span class="win-desc">Get <strong>{wonResult.percent}% OFF</strong> on your next checkout! Use code during booking.</span>
          </div>
        {/if}
      {/if}

      <div class="modal-actions-bar">
        <button class="btn-gold spin-btn" on:click={spinWheel} disabled={spinning}>
          {spinning ? `🎰 ${spinLabels.spinning}` : (currentSpinCost === 0 ? `🎰 ${spinLabels.spin} (${spinLabels.free})` : `🎰 ${spinLabels.spin} (-ETB ${currentSpinCost})`)}
        </button>
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
    backdrop-filter: blur(6px);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 9999;
  }

  .modal-content {
    width: 90%;
    max-width: 440px;
    padding: 24px;
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .modal-title-row {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .modal-title-row h2 {
    font-size: 1.25rem;
    font-weight: 900;
    margin: 0;
  }

  .close-btn {
    background: transparent;
    border: none;
    font-size: 1.2rem;
    color: var(--text-muted);
    cursor: pointer;
  }

  .rules-bar {
    display: flex;
    justify-content: space-between;
    gap: 8px;
    background: var(--bg-surface-secondary);
    padding: 8px 12px;
    border-radius: var(--radius-md);
    font-size: 0.78rem;
  }

  .rule-chip {
    color: var(--text-muted);
  }

  .rule-chip strong {
    color: var(--text-main);
  }

  .cost-chip strong {
    color: var(--accent-gold);
  }

  .wallet-chip strong {
    color: #10b981;
  }

  .spin-error-alert {
    background: rgba(239, 68, 68, 0.12);
    border: 1px solid rgba(239, 68, 68, 0.3);
    color: #ef4444;
    padding: 10px 14px;
    border-radius: var(--radius-md);
    font-size: 0.82rem;
    font-weight: 700;
  }

  .wheel-container {
    position: relative;
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 10px 0;
  }

  .wheel-pointer {
    position: absolute;
    top: -12px;
    font-size: 1.6rem;
    color: var(--accent-gold);
    z-index: 10;
    filter: drop-shadow(0 2px 4px rgba(0,0,0,0.4));
  }

  .wheel-disc {
    border-radius: 50%;
    box-shadow: 0 8px 24px rgba(0,0,0,0.25);
  }

  .result-card {
    padding: 14px;
    border-radius: var(--radius-lg);
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    text-align: center;
  }

  .result-no-luck {
    background: var(--bg-surface-secondary);
    border: 1px solid var(--border-subtle);
  }

  .result-tag {
    font-size: 0.78rem;
    font-weight: 900;
    color: var(--text-muted);
    letter-spacing: 0.05em;
  }

  .result-desc {
    font-size: 0.85rem;
    color: var(--text-muted);
    margin: 0;
  }

  .result-win {
    background: rgba(200, 155, 60, 0.1);
    border: 1px solid var(--accent-gold);
  }

  .win-tag {
    font-size: 0.75rem;
    font-weight: 800;
    color: var(--accent-gold);
  }

  .code-copy-box {
    display: flex;
    align-items: center;
    gap: 10px;
    background: var(--bg-surface);
    border: 1px dashed var(--accent-gold);
    padding: 6px 14px;
    border-radius: var(--radius-md);
  }

  .win-code {
    font-size: 1.3rem;
    font-weight: 900;
    color: var(--text-main);
    letter-spacing: 0.08em;
  }

  .copy-btn {
    background: var(--accent-gold);
    color: #ffffff;
    border: none;
    padding: 4px 10px;
    border-radius: var(--radius-sm);
    font-size: 0.75rem;
    font-weight: 800;
    cursor: pointer;
  }

  .win-desc {
    font-size: 0.82rem;
    color: var(--text-main);
  }

  .modal-actions-bar {
    display: flex;
    justify-content: center;
    margin-top: 4px;
  }

  .spin-btn {
    width: 100%;
    padding: 12px;
    font-size: 0.95rem;
    font-weight: 900;
  }
</style>
