<script>
  import { createEventDispatcher } from 'svelte';
  import Icon from './Icon.svelte';

  export let currentLanguage = 'en';
  export let listings = [];

  const dispatch = createEventDispatcher();
  let isOpen = false;
  let draft = '';
  let activeIntent = null;
  let requestedLocation = '';
  let isThinking = false;
  let messages = [
    {
      role: 'assistant',
      text: 'Hi, I am Easy Assistant. I can help you find stays, cars and events, or answer booking and support questions.'
    }
  ];

  $: isAmharic = currentLanguage === 'am';
  $: copy = isAmharic
    ? {
        title: 'Easy Assistant',
        greeting: 'ሰላም፣ እኔ Easy Assistant ነኝ። መኖሪያ፣ መኪና እና ዝግጅቶችን እንዲያገኙ ወይም ስለ ቦታ ማስያዣ እና ድጋፍ ጥያቄዎችን እንዲመልሱ ልረዳዎት እችላለሁ።',
        prompt: 'ምን ልርዳዎት?',
        placeholder: 'ለምሳሌ፦ ሆቴል ፈልግ',
        send: 'ላክ',
        clear: 'ንግግሩን አጽዳ',
        hotel: 'ሆቴል ፈልግ',
        cars: 'መኪናዎችን አሳይ',
        events: 'ዝግጅቶችን አሳይ',
        help: 'የድጋፍ ገጽ',
        close: 'Easy Assistant ዝጋ'
      }
    : {
        title: 'Easy Assistant',
        greeting: 'Hi, I am Easy Assistant. I can help you find stays, cars and events, or answer booking and support questions.',
        prompt: 'What can I help with?',
        placeholder: 'For example: Find me a hotel in Bole',
        send: 'Send',
        clear: 'Clear conversation',
        hotel: 'Find a hotel',
        cars: 'Show available cars',
        events: 'Show events',
        help: 'Open customer support',
        close: 'Close Easy Assistant'
      };

  $: thinkingLabel = isAmharic ? 'እያሰብኩ ነው...' : 'Thinking...';

  function navigate(target) {
    dispatch('navigate', target);
  }

  function addMessage(role, text) {
    messages = [...messages, { role, text }];
  }

  function recommendListing() {
    const hotels = listings.filter((listing) => listing.category === 'HOTEL' && listing.availableQuantity > 0);
    const locationMatch = requestedLocation && hotels.find((listing) => `${listing.title} ${listing.location} ${listing.description}`.toLowerCase().includes(requestedLocation));
    const recommendation = locationMatch || hotels[0];
    if (!recommendation) {
      addMessage('assistant', isAmharic ? 'አሁን የሚገኝ ሆቴል ማግኘት አልቻልኩም።' : 'I could not find an available hotel right now.');
      return;
    }
    messages = [...messages, {
      role: 'assistant',
      text: isAmharic ? `${recommendation.title}ን እመክራለሁ። ዝርዝሩን ለማየት ከታች ይጫኑ።` : `I recommend ${recommendation.title}. Open it below to see the room options, price, and booking details.`,
      listing: recommendation
    }];
  }

  function respondTo(text) {
    const query = text.toLowerCase();
    if (/(pick|choose|recommend|select|one yourself|surprise me|ምረጥ|ምከረኝ)/.test(query) && activeIntent === 'HOTEL') {
      recommendListing();
      return;
    }
    if (/(hotel|stay|room|resort|lodge|ሆቴል|መኖሪያ)/.test(query)) {
      activeIntent = 'HOTEL';
      const locationMatch = query.match(/(?:near|around|in|at)\s+([^,.]+)/);
      requestedLocation = locationMatch ? locationMatch[1].trim() : '';
      addMessage('assistant', isAmharic ? 'የሆቴል አማራጮችን እያየሁ ነው። እኔ እንድመርጥልዎ ከፈለጉ “pick one yourself” ይበሉ።' : 'I will show you verified hotels and stays. You can also say “pick one yourself” and I will recommend one from the available inventory.');
      navigate('HOTEL');
      return;
    }
    if (/(car|cars|drive|rental|vehicle|መኪና)/.test(query)) {
      activeIntent = 'CAR_RENTAL';
      addMessage('assistant', isAmharic ? 'የመኪና ኪራይ አማራጮችን እያሳየሁ ነው።' : 'I will show you available car rentals.');
      navigate('CAR_RENTAL');
      return;
    }
    if (/(event|events|experience|festival|concert|ዝግጅት|ልምድ)/.test(query)) {
      activeIntent = 'EVENT';
      addMessage('assistant', isAmharic ? 'ዝግጅቶችን እና ልምዶችን እያሳየሁ ነው።' : 'I will show you events and experiences.');
      navigate('EVENT');
      return;
    }
    if (/(provider support|contact provider support|የአቅራቢ ድጋፍ)/.test(query)) {
      addMessage('assistant', isAmharic ? 'የአቅራቢ ድጋፍ ገጹን እከፍታለሁ።' : 'I will open provider support for marketplace partners.');
      navigate('provider-support');
      return;
    }
    if (/(provider|list a service|become|አቅራቢ)/.test(query)) {
      addMessage('assistant', isAmharic ? 'የአቅራቢ መረጃን እና መመዝገቢያን እከፍታለሁ።' : 'I will open the provider onboarding and listing workspace.');
      navigate('become-provider');
      return;
    }
    if (/(cancel|cancellation|refund|ሰርዝ|ስረዛ)/.test(query)) {
      addMessage('assistant', isAmharic ? 'የስረዛ መመሪያ እና የድጋፍ አማራጮች እዚህ ይገኛሉ።' : 'Cancellation rules depend on the listing. I will open support so you can review the policy and contact the team.');
      navigate('help');
      return;
    }
    if (/(book|booking|reserve|how does eas*yservice work|how do i book|ቦታ ማስያዣ|እንዴት)/.test(query)) {
      addMessage('assistant', isAmharic ? 'የቦታ ማስያዣ እርምጃዎችን እና የክፍያ ሂደቱን በድጋፍ ገጽ ይመልከቱ።' : 'Choose a verified listing, select its option and dates, enter your details, then review and pay securely. I will open the help page for the full guide.');
      navigate('help');
      return;
    }
    if (/(support|help|contact|ድጋፍ|እርዳታ)/.test(query)) {
      addMessage('assistant', isAmharic ? 'የድጋፍ ገጹን እከፍታለሁ።' : 'I will open EasyService customer support.');
      navigate('help');
      return;
    }
    addMessage('assistant', isAmharic ? 'በሆቴል፣ መኪና፣ ዝግጅት፣ ቦታ ማስያዣ፣ ስረዛ ወይም ድጋፍ ላይ ልረዳዎት እችላለሁ።' : 'I can help with hotels, cars, events, bookings, cancellations, providers, and support. Try one of the quick actions below.');
  }

  function quickAction(text, target) {
    activeIntent = target;
    addMessage('user', text);
    addMessage('assistant', target === 'HOTEL' ? (isAmharic ? 'የሆቴል አማራጮችን እያሳየሁ ነው።' : 'Here are verified hotels and stays.') : target === 'CAR_RENTAL' ? (isAmharic ? 'የመኪና አማራጮችን እያሳየሁ ነው።' : 'Here are available car rentals.') : (isAmharic ? 'ዝግጅቶችን እያሳየሁ ነው።' : 'Here are events and experiences.'));
    navigate(target);
  }

  async function askBackend() {
    const response = await fetch('/api/assistant/chat', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        language: currentLanguage,
        messages: messages.map(({ role, text }) => ({ role, text })),
        inventory: listings.map(({ id, title, category, description, location, price, availableQuantity }) => ({ id, title, category, description, location, price, availableQuantity }))
      })
    });
    if (!response.ok) throw new Error('Assistant service unavailable');
    const result = await response.json();
    if (result.text) {
      const listing = result.listingId ? listings.find((item) => item.id === result.listingId) : null;
      messages = [...messages, { role: 'assistant', text: result.text, listing }];
    }
    if (result.action) navigate(result.action);
  }

  async function submitMessage() {
    const text = draft.trim();
    if (!text || isThinking) return;
    addMessage('user', text);
    draft = '';
    isThinking = true;
    try {
      await askBackend();
    } catch {
      respondTo(text);
    } finally {
      isThinking = false;
    }
  }
</script>

<button class="assistant-launcher" class:open={isOpen} on:click={() => isOpen = !isOpen} aria-label={isOpen ? copy.close : copy.title} title={copy.title}>
  <Icon name="sparkles" size={20} />
  <span class="assistant-launcher-label">{copy.title}</span>
</button>

{#if isOpen}
  <section class="assistant-panel marketplace-card" aria-label={copy.title}>
    <header class="assistant-header">
      <div class="assistant-identity"><span class="assistant-avatar"><Icon name="sparkles" size={18} /></span><div><strong>{copy.title}</strong><small>{copy.prompt}</small></div></div>
      <button class="assistant-close" on:click={() => isOpen = false} aria-label={copy.close}>×</button>
    </header>

    <div class="assistant-messages" aria-live="polite">
      {#each messages as message}
        <div class="assistant-message {message.role}"><span>{message.text}{#if message.listing}<button class="assistant-listing-action" on:click={() => dispatch('openListing', message.listing)}><Icon name="bed" size={14} /> Open listing</button>{/if}</span></div>
      {/each}
      {#if isThinking}<div class="assistant-message assistant"><span>{thinkingLabel}</span></div>{/if}
    </div>

    <div class="assistant-quick-actions">
      <button on:click={() => quickAction(copy.hotel, 'HOTEL')}><Icon name="bed" size={14} /> {copy.hotel}</button>
      <button on:click={() => quickAction(copy.cars, 'CAR_RENTAL')}><Icon name="car" size={14} /> {copy.cars}</button>
      <button on:click={() => quickAction(copy.events, 'EVENT')}><Icon name="ticket" size={14} /> {copy.events}</button>
      <button on:click={() => navigate('help')}><Icon name="help" size={14} /> {copy.help}</button>
    </div>

    <form class="assistant-composer" on:submit|preventDefault={submitMessage}>
      <input bind:value={draft} placeholder={copy.placeholder} aria-label={copy.placeholder} />
      <button class="btn-gold" type="submit" disabled={!draft.trim() || isThinking}>{isThinking ? '...' : copy.send}</button>
    </form>
    <button class="assistant-clear" on:click={() => messages = [{ role: 'assistant', text: copy.greeting }]}>{copy.clear}</button>
  </section>
{/if}

<style>
  .assistant-launcher { position: fixed; right: 24px; bottom: 24px; z-index: 1800; display: inline-flex; align-items: center; gap: 8px; padding: 12px 16px; border: 1px solid var(--accent-gold); border-radius: 999px; background: var(--accent-gold); color: #fff; font: inherit; font-weight: 800; cursor: pointer; box-shadow: 0 12px 28px rgba(17,24,39,.2); transition: transform .2s ease, box-shadow .2s ease; }
  .assistant-launcher:hover, .assistant-launcher.open { transform: translateY(-2px); box-shadow: 0 16px 34px rgba(17,24,39,.26); }
  .assistant-panel { position: fixed; right: 24px; bottom: 82px; z-index: 1800; width: min(390px, calc(100vw - 32px)); overflow: hidden; padding: 0; border: 1px solid var(--border-subtle); background: var(--bg-surface); box-shadow: 0 24px 60px rgba(17,24,39,.22); }
  .assistant-header { display: flex; align-items: center; justify-content: space-between; padding: 16px; color: #fff; background: linear-gradient(120deg, #0b1930, #24435d); }
  .assistant-identity { display: flex; align-items: center; gap: 10px; }.assistant-identity div { display: grid; gap: 3px; }.assistant-identity small { color: rgba(255,255,255,.72); }.assistant-avatar { display: grid; place-items: center; width: 36px; height: 36px; border-radius: 50%; color: #0b1930; background: var(--accent-gold); }.assistant-close { border: 0; background: transparent; color: #fff; font-size: 1.5rem; cursor: pointer; }
  .assistant-messages { display: grid; gap: 10px; max-height: 300px; overflow-y: auto; padding: 16px; background: var(--bg-surface-secondary); }.assistant-message { display: flex; }.assistant-message span { max-width: 86%; padding: 10px 12px; border-radius: 10px; line-height: 1.45; font-size: .9rem; }.assistant-message.assistant span { color: var(--text-main); background: var(--bg-surface); border: 1px solid var(--border-subtle); }.assistant-message.user { justify-content: flex-end; }.assistant-message.user span { color: #fff; background: #0b1930; }.assistant-listing-action { display: flex; align-items: center; gap: 6px; margin-top: 10px; padding: 8px 10px; border: 0; border-radius: 7px; background: var(--accent-gold); color: #fff; font: inherit; font-size: .78rem; font-weight: 800; cursor: pointer; }
  .assistant-quick-actions { display: flex; flex-wrap: wrap; gap: 7px; padding: 12px 16px 4px; }.assistant-quick-actions button { display: inline-flex; align-items: center; gap: 5px; padding: 7px 9px; border: 1px solid var(--border-subtle); border-radius: 999px; background: var(--bg-surface); color: var(--text-main); font: inherit; font-size: .75rem; cursor: pointer; }.assistant-quick-actions button:hover { border-color: var(--accent-gold); }
  .assistant-composer { display: flex; gap: 8px; padding: 12px 16px 8px; }.assistant-composer input { min-width: 0; flex: 1; padding: 11px 12px; border: 1px solid var(--border-subtle); border-radius: 8px; background: var(--bg-surface); color: var(--text-main); font: inherit; }.assistant-composer .btn-gold { padding: 0 13px; }.assistant-clear { display: block; margin: 0 auto 12px; border: 0; background: transparent; color: var(--text-muted); font: inherit; font-size: .75rem; cursor: pointer; }.assistant-clear:hover { color: var(--text-main); }
  @media (max-width: 560px) { .assistant-launcher { right: 16px; bottom: 16px; }.assistant-panel { right: 16px; bottom: 72px; }.assistant-launcher-label { display: none; } }
</style>
