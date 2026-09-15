<script>
  import { createEventDispatcher } from 'svelte';
  import Icon from './Icon.svelte';

  export let currentLanguage = 'en';
  export let listings = [];
  export let discoveredListings = [];

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

  function recommendPublicDiscovery() {
    const recommendation = discoveredListings[0];
    if (!recommendation) {
      addMessage('assistant', isAmharic ? 'አሁን የህዝብ ምንጭ ውጤት የለም።' : 'There are no public-source discoveries available right now.');
      return;
    }
    const source = recommendation.sourceName || 'a public source';
    const details = [
      recommendation.location ? `- Address/location: ${recommendation.location}` : '',
      recommendation.website || recommendation.sourceUrl ? `- Website: ${recommendation.website || recommendation.sourceUrl}` : '',
      recommendation.phone ? `- Phone: ${recommendation.phone}` : '',
      recommendation.price ? `- Published price: ${recommendation.price}` : '- Price was not publicly listed; check the source website or call the business.'
    ].filter(Boolean).join('\n');
    messages = [...messages, {
      role: 'assistant',
      text: isAmharic ? `${recommendation.name || recommendation.title} ከ${source} ተገኝቷል። በቀጥታ በEasyService ማስያዝ አይችሉም፣ ነገር ግን መረጃውን ከታች ማግኘት ይችላሉ።\n${details}` : `I found this from ${source}. Although you cannot book it directly here, you can still get their information below:\n${details}`,
      listing: { ...recommendation, isPublicSource: true }
    }];
  }

  function respondTo(text) {
    const query = text.toLowerCase();
    if (/(tired|exhausted|worn out|need a rest|need rest|want to rest|wanna rest|sleepy|burned out|burnt out|overwhelmed|ደክሞኛል|እረፍት)/.test(query)) {
      addMessage('assistant', isAmharic
        ? 'የደከመዎት ይመስላል። ትንሽ እረፍት ይገባዎታል። ጸጥ ያለ ሆቴል ወይም የሚያረጋጋ ቦታ እንድፈልግልዎ ይፈልጋሉ፣ ወይስ ትንሽ መወያየት ይመርጣሉ?'
        : 'That sounds exhausting. You deserve a real break. Would you like me to find a quiet stay or a relaxing place nearby, or would you rather just talk for a moment? Tell me your city if you want me to narrow it down.');
      return;
    }
    if (/(how are you|how r u|hello|hi |hey |good morning|good afternoon|good evening|ሰላም|እንዴት ነህ)/.test(query)) {
      addMessage('assistant', isAmharic
        ? 'ደህና ነኝ፣ እናመሰግናለሁ! ለእርስዎ ጉዞ ሆቴል፣ መኪና፣ ዝግጅት ወይም የአካባቢ ምንጭ መረጃ ልፈልግልዎ እችላለሁ።'
        : 'I am doing well, thank you. I am ready to help plan your EasyService day. Are you looking for a stay, a car, an event, or something local to discover?');
      return;
    }
    if (/(what's up|whats up|\bsup\b|how is it going|how's it going)/.test(query)) {
      addMessage('assistant', isAmharic
        ? 'ሁሉም ጥሩ ነው። ለዛሬ ቀንዎ ምግብ፣ ጸጥ ያለ ቦታ ወይም የምሽት ዝግጅት ማግኘት እችላለሁ። እርስዎ ምን ስሜት ላይ ነዎት?'
        : "Not much, I'm here and ready to help. We can find food, a quiet place, or something fun for tonight. What are you in the mood for?");
      return;
    }
    if (/(can u talk|can you talk|talk to me|just talk|chat with me|keep me company|lonely|are you there)/.test(query)) {
      addMessage('assistant', isAmharic
        ? 'እሺ፣ እዚህ ነኝ። ስለ ቀንዎ መነጋገር ወይም ለዛሬ ምሽት እቅድ ማውጣት እንችላለን። አሁን ምን እያሰቡ ነው?'
        : 'Of course. I\'m here with you. We can just talk, or we can figure out something nice for the rest of your day. What\'s on your mind?');
      return;
    }
    if (/(what else|anything else|what more|and then)/.test(query)) {
      addMessage('assistant', isAmharic
        ? 'ከእኔ ጋር መነጋገር፣ ምግብ መፈለግ፣ ጸጥ ያለ ቦታ ማግኘት ወይም የምሽት ዝግጅት ማቀድ እንችላለን። አሁን የትኛው ይስማማዎታል?'
        : 'We can keep talking, find something to eat, look for a quiet place, or plan an evening event. What sounds best right now?');
      return;
    }
    if (/(eat|food|hungry|meal|dinner|lunch|breakfast|restaurant|coffee shop|cafe)/.test(query)) {
      if (discoveredListings.length > 0) {
        recommendPublicDiscovery();
      } else {
        addMessage('assistant', isAmharic
          ? 'ምን መብላት እንደሚፈልጉ ልፈልግልዎ እችላለሁ። የትኛው ከተማ ነዎት፣ እና ምግብ ቤት፣ ካፌ ወይስ ፈጣን ምግብ ይፈልጋሉ?'
          : 'Absolutely. I can help you find somewhere to eat. What city are you in, and are you in the mood for a restaurant, a cafe, or something quick?');
      }
      return;
    }
    if (/(from bole|in bole|near bole|around bole|from addis|in addis)/.test(query)) {
      addMessage('assistant', isAmharic
        ? 'ቦሌ አካባቢ እንዳሉ ገባኝ። ከቦሌ አካባቢ ሆቴል፣ መኪና፣ ምግብ ቤት ወይም ዝግጅት እንድፈልግ ይፈልጋሉ?'
        : "Got it, you're around Bole. I can narrow the options to Bole for a stay, car, restaurant, or event. Which one should I look for?");
      return;
    }
    if (/(fun day|have fun|something fun|what can i do|what is there to do|what's there to do|something to do|things to do|where should i go|just got off work|after work|hyped|excited|bored|do sth|do something|tonight|this evening|ደስ የሚል|መዝናናት)/.test(query)) {
      activeIntent = 'EVENT';
      addMessage('assistant', isAmharic
        ? 'ለመዝናናት ዝግጅቶችን፣ ልምዶችን እና ከህዝብ ምንጮች የተገኙ ቦታዎችን እንድፈልግልዎ እችላለሁ። በአሁኑ ጊዜ ዝግጅቶችን እና የአካባቢ ግኝቶችን እከፍታለሁ።'
        : 'You just got off work and want to do something fun, got it. I can help you choose an event, a relaxed place to hang out, or a local discovery. What city are you in, and are you feeling social or more laid-back?');
      navigate('EVENT');
      return;
    }
    if (/(public source|discovered|nearby business|restaurant|coffee shop|cafe|shopping|souvenir|public|ህዝብ ምንጭ)/.test(query) && discoveredListings.length > 0) {
      recommendPublicDiscovery();
      return;
    }
    if (/(chill|cozy|cosy|quiet|relaxing|relaxed|evening|date night|night out|spot|place to unwind|lounge|cafe|restaurant)/.test(query)) {
      activeIntent = 'EVENT';
      addMessage('assistant', isAmharic
        ? 'ምቾት ያለው ምሽት ጥሩ ሀሳብ ነው። የምሽት ዝግጅት፣ ጸጥ ያለ ቦታ ወይም ካፌ ይፈልጋሉ? ከተማዎንም ይንገሩኝ።'
        : 'A cozy evening sounds lovely. Would you prefer an evening event, a quiet spot, or a cafe? Tell me your city and I’ll narrow it down.');
      navigate('EVENT');
      return;
    }
    if (/(public source|discovered|nearby business|restaurant|shopping|souvenir|public|coffee shop|cafe|ህዝብ ምንጭ)/.test(query)) {
      recommendPublicDiscovery();
      return;
    }
    if (/(chose|choose|picked|selected|i chose|i picked)/.test(query)) {
      addMessage('assistant', isAmharic
        ? 'ጥሩ ምርጫ ነው። አሁን መኪና፣ የሳምንቱ መጨረሻ ዝግጅት እና ወደ ቤት የሚወስዱትን የኢትዮጵያ ስጦታ ልፈልግልዎ እችላለሁ።'
        : 'Great choice. I can now help with the rest of your trip: a driver, weekend events, and an Ethiopian gift to take home. Choose a category below.');
      return;
    }
    const wantsCar = /(car|driver|drive|rental|vehicle|መኪና)/.test(query);
    const wantsHotel = /(hotel|stay|room|resort|lodge|weekend|ሆቴል|መኖሪያ)/.test(query);
    const wantsEvent = /(event|events|entertainment|weekend|concert|festival|where to go|things to do|ዝግጅት|ልምድ)/.test(query);
    const wantsStore = /(take|home country|gift|souvenir|shop|shopping|product|ስጦታ|ግዢ)/.test(query);
    if ([wantsCar, wantsHotel, wantsEvent, wantsStore].filter(Boolean).length >= 2) {
      addMessage('assistant', isAmharic
        ? 'ለሳምንቱ መጨረሻ መኖሪያ፣ ሹፌር/መኪና፣ ዝግጅቶች እና ወደ ቤት የሚወስዱ ስጦታዎችን ልፈልግልዎ እችላለሁ። የትኛውን መጀመሪያ እንይ?'
        : 'I can help plan the whole visit: a weekend stay, a car with a driver, things to do around your meeting, and an Ethiopian gift to take home. Which should we start with?');
      return;
    }
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
        inventory: [
          ...listings.map(({ id, title, category, description, location, price, availableQuantity }) => ({ id, title, category, description, location, price, availableQuantity, sourceType: 'EASYSERVICE', bookable: true })),
          ...discoveredListings.map((listing) => ({ id: listing.id, title: listing.name || listing.title, category: listing.category, description: listing.description, location: listing.city || listing.location, price: listing.price, priceSource: listing.priceSource, availableQuantity: 0, sourceType: 'PUBLIC_SOURCE', bookable: false, website: listing.website, sourceUrl: listing.sourceUrl, phone: listing.phone, imageUrl: listing.imageUrl, sourceName: listing.sourceName }))
        ]
      })
    });
    if (!response.ok) throw new Error('Assistant service unavailable');
    const result = await response.json();
    if (result.text) {
      const verifiedListing = result.listingId ? listings.find((item) => item.id === result.listingId) : null;
      const publicListing = result.listingId ? discoveredListings.find((item) => item.id === result.listingId) : null;
      const listing = verifiedListing || (publicListing ? { ...publicListing, isPublicSource: true } : null);
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
        <div class="assistant-message {message.role}"><span>{message.text}{#if message.listing}<button class="assistant-listing-action" on:click={() => dispatch(message.listing.isPublicSource ? 'openDiscoveredListing' : 'openListing', message.listing)}><Icon name={message.listing.isPublicSource ? 'globe' : 'bed'} size={14} /> {message.listing.isPublicSource ? 'Open source' : 'Open listing'}</button>{/if}</span></div>
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
  .assistant-messages { display: grid; gap: 10px; max-height: 300px; overflow-y: auto; padding: 16px; background: var(--bg-surface-secondary); }.assistant-message { display: flex; }.assistant-message span { max-width: 86%; padding: 10px 12px; border-radius: 10px; line-height: 1.45; font-size: .9rem; white-space: pre-wrap; }.assistant-message.assistant span { color: var(--text-main); background: var(--bg-surface); border: 1px solid var(--border-subtle); }.assistant-message.user { justify-content: flex-end; }.assistant-message.user span { color: #fff; background: #0b1930; }.assistant-listing-action { display: flex; align-items: center; gap: 6px; margin-top: 10px; padding: 8px 10px; border: 0; border-radius: 7px; background: var(--accent-gold); color: #fff; font: inherit; font-size: .78rem; font-weight: 800; cursor: pointer; }
  .assistant-quick-actions { display: flex; flex-wrap: wrap; gap: 7px; padding: 12px 16px 4px; }.assistant-quick-actions button { display: inline-flex; align-items: center; gap: 5px; padding: 7px 9px; border: 1px solid var(--border-subtle); border-radius: 999px; background: var(--bg-surface); color: var(--text-main); font: inherit; font-size: .75rem; cursor: pointer; }.assistant-quick-actions button:hover { border-color: var(--accent-gold); }
  .assistant-composer { display: flex; gap: 8px; padding: 12px 16px 8px; }.assistant-composer input { min-width: 0; flex: 1; padding: 11px 12px; border: 1px solid var(--border-subtle); border-radius: 8px; background: var(--bg-surface); color: var(--text-main); font: inherit; }.assistant-composer .btn-gold { padding: 0 13px; }.assistant-clear { display: block; margin: 0 auto 12px; border: 0; background: transparent; color: var(--text-muted); font: inherit; font-size: .75rem; cursor: pointer; }.assistant-clear:hover { color: var(--text-main); }
  @media (max-width: 560px) { .assistant-launcher { right: 16px; bottom: 16px; }.assistant-panel { right: 16px; bottom: 72px; }.assistant-launcher-label { display: none; } }
</style>
