<script>
  import { createEventDispatcher } from 'svelte';
  import Icon from './Icon.svelte';

  export let page = 'explore';
  export let currentUser = null;
  export let listings = [];
  export let bookings = [];
  export let currentLanguage = 'en';
  export let currency = 'ETB';

  const dispatch = createEventDispatcher();
  let searchTerm = '';
  let supportTopic = 'Booking';
  let walletAmount = 500;
  let walletMessage = '';
  let supportSubmitted = false;
  $: isAmharic = currentLanguage === 'am';
  const currencyRates = { ETB: 1, USD: 0.018, EUR: 0.0165, GBP: 0.014 };

  function formatPrice(amount) {
    const converted = Number(amount || 0) * (currencyRates[currency] || 1);
    return `${currency} ${converted.toLocaleString(undefined, { maximumFractionDigits: currency === 'ETB' ? 0 : 2 })}`;
  }

  function tr(english, amharic) {
    return isAmharic ? amharic : english;
  }

  const destinations = {
    addis: { name: 'Addis Ababa', eyebrow: 'THE CAPITAL IN MOTION', description: 'Move easily between Bole, Kazanchis, Piassa and the city experiences that make Addis feel unmistakably itself.', image: 'https://images.unsplash.com/photo-1567789884554-0b844b597180?auto=format&fit=crop&w=1600&q=85', areas: 'Bole, Kazanchis, Piassa, 22 Mazoria' },
    bishoftu: { name: 'Bishoftu', eyebrow: 'LAKESIDE ESCAPES', description: 'Plan a restorative lakefront stay, a weekend drive or a slow afternoon around crater lakes and resort gardens.', image: 'https://images.unsplash.com/photo-1500534623283-312aade485b7?auto=format&fit=crop&w=1600&q=85', areas: 'Lakefront, Kuriftu, Hora Lake' },
    hawassa: { name: 'Hawassa', eyebrow: 'LAKE, LIGHT AND OPEN AIR', description: 'Find lakefront stays, easy road trips and experiences around one of Ethiopia’s most welcoming cities.', image: 'https://images.unsplash.com/photo-1500375592092-40eb2168fd21?auto=format&fit=crop&w=1600&q=85', areas: 'Lake Hawassa, Tabor, Shamana' },
    lalibela: { name: 'Lalibela', eyebrow: 'HIGHLAND HERITAGE', description: 'Build a thoughtful highland itinerary around heritage stays, mountain roads and living history.', image: 'https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1600&q=85', areas: 'Historic Churches, Highlands, Asheton' },
    'bahir-dar': { name: 'Bahir Dar', eyebrow: 'BLUE NILE HORIZONS', description: 'Stay by Lake Tana, discover the Blue Nile and make room for the slower rhythm of the waterfront.', image: 'https://images.unsplash.com/photo-1500534623283-312aade485b7?auto=format&fit=crop&w=1600&q=85', areas: 'Lake Tana, Waterfront, Blue Nile Falls' }
  };

  const faqs = [
    ['How do I make a booking?', 'Choose a verified listing, select its option and dates, complete your details, then confirm payment in the secure booking flow.'],
    ['How do I cancel?', 'Open My Bookings, select the reservation pass and use the cancellation action when the reservation is eligible.'],
    ['What happens if a provider cancels?', 'Your reservation status is updated and the support team can help review the next available option.'],
    ['How do I become a provider?', 'Start with Become a Provider, create your profile and publish your first listing from the Provider Hub.'],
    ['How are payments handled?', 'This academic build uses a simulated Easy Wallet and payment flow. Production payments require a verified payment provider integration.'],
    ['How do I contact a provider?', 'Open a listing, review the provider details, and use the available contact or support action before booking.']
  ];

  const supportCategories = ['Booking', 'Payments', 'Cancellations', 'Account', 'Providers', 'Listings', 'Reviews', 'Safety', 'Technical Issues'];
  const providerSupportCategories = ['Getting Started', 'Managing Listings', 'Bookings', 'Inventory', 'Payments', 'Reviews', 'Provider Policies', 'Account', 'Technical Issues'];
  const legalSections = {
    terms: ['Introduction', 'Definitions', 'Accounts', 'Marketplace', 'Bookings', 'Payments', 'Cancellations', 'Providers', 'Reviews', 'Prohibited Conduct', 'Intellectual Property', 'Liability', 'Disputes', 'Changes', 'Contact'],
    privacy: ['Information We Collect', 'How We Use Information', 'Account Information', 'Booking Information', 'Payment Information', 'Location Information', 'Cookies', 'Analytics', 'Third-Party Services', 'Data Retention', 'Security', 'Your Rights', 'Children', 'International Transfers', 'Policy Changes', 'Contact']
  };

  const destinationHighlights = [
    ['Where to stay', 'Verified hotels, lodges and lakefront stays'],
    ['Cars', 'Reliable vehicles for city and highland roads'],
    ['Restaurants', 'Local flavors and memorable tables'],
    ['Events', 'Culture, music and experiences'],
    ['Things to do', 'A better way to spend the day'],
    ['Shops', 'Authentic products from local makers']
  ];

  $: destination = destinations[page];
  $: visibleListings = listings.filter((listing) => {
    if (!searchTerm.trim()) return true;
    const query = searchTerm.toLowerCase();
    return [listing.title, listing.description, listing.location, listing.hostName]
      .some((value) => (value || '').toLowerCase().includes(query));
  }).slice(0, 6);

  function go(target) {
    dispatch('navigate', target);
  }

  function destinationCategory(label) {
    if (label === 'Where to stay') return 'HOTEL';
    if (label === 'Cars') return 'CAR_RENTAL';
    if (label === 'Events') return 'EVENT';
    if (label === 'Shops') return 'STORE';
    return 'ALL';
  }

  function submitWallet() {
    const amount = Number(walletAmount);
    if (!currentUser) {
      walletMessage = 'Please sign in to use your Easy Wallet.';
      return;
    }
    if (!amount || amount <= 0) {
      walletMessage = 'Enter an amount greater than zero.';
      return;
    }
    dispatch('walletTopUp', amount);
    walletMessage = `ETB ${amount.toLocaleString()} added to your demo wallet.`;
  }

  function startSupportConversation() {
    supportSubmitted = true;
    walletMessage = tr('Thanks. Your support conversation is ready for the EasyService team.', 'እናመሰግናለን። የድጋፍ ውይይትዎ ለEasyService ቡድን ተዘጋጅቷል።');
  }

  function hero(title, subtitle, image = '') {
    return { title, subtitle, image };
  }
</script>

{#if page === 'explore'}
  {@const content = hero(tr('Explore Ethiopia', 'ኢትዮጵያን ያስሱ'), tr('Discover stays, cars, events and shops in one trusted marketplace.', 'መኖሪያዎችን፣ መኪናዎችን፣ ዝግጅቶችን እና ሱቆችን በአንድ የታመነ የገበያ ቦታ ያግኙ።'))}
  <section class="site-hero site-hero-explore" style={content.image ? `--hero-image: url('${content.image}')` : ''}>
    <div class="site-hero-copy"><span class="eyebrow">{tr('EASYSERVICE DISCOVERY', 'EASYSERVICE ፍለጋ')}</span><h1>{content.title}</h1><p>{content.subtitle}</p>
      <div class="site-search"><Icon name="search" size={18} /><input bind:value={searchTerm} placeholder={tr('Search EasyService', 'EasyService ይፈልጉ')} aria-label={tr('Search EasyService', 'EasyService ይፈልጉ')} /></div>
    </div>
  </section>
  <section class="site-content"><div class="section-heading"><span class="eyebrow">{tr('A SIMPLE PLACE TO START', 'ለመጀመር ቀላል ቦታ')}</span><h2>{tr('Choose your next direction.', 'ቀጣዩን መድረሻዎን ይምረጡ።')}</h2></div>
    <div class="site-category-grid">
      <button on:click={() => go('stays')}><Icon name="bed" size={24} /><strong>{tr('Stays', 'መኖሪያ')}</strong><span>{tr('Hotels, resorts and lodges', 'ሆቴሎች፣ ሪዞርቶች እና ሎጆች')}</span></button>
      <button on:click={() => go('drive')}><Icon name="car" size={24} /><strong>{tr('Drive', 'መኪና')}</strong><span>{tr('Cars for city and highland roads', 'ለከተማ እና ለደጋ መንገዶች መኪናዎች')}</span></button>
      <button on:click={() => go('experiences')}><Icon name="ticket" size={24} /><strong>{tr('Experiences', 'ልምዶች')}</strong><span>{tr('Events, culture and music', 'ዝግጅቶች፣ ባህል እና ሙዚቃ')}</span></button>
      <button on:click={() => go('shop')}><Icon name="bag" size={24} /><strong>{tr('Shop', 'ግዢ')}</strong><span>{tr('Crafts, coffee and local goods', 'የእጅ ሥራዎች፣ ቡና እና የአካባቢ ዕቃዎች')}</span></button>
    </div>
    <div class="section-heading compact"><span class="eyebrow">{tr('FEATURED NOW', 'አሁን የተመረጡ')}</span><h2>{tr('Find something worth the journey.', 'ለጉዞዎ የሚገባ ነገር ያግኙ።')}</h2></div>
    <div class="site-mini-grid">{#each visibleListings as listing}<button class="site-listing" on:click={() => dispatch('openListing', listing)}><img src={listing.imageUrl} alt={listing.title} /><span><strong>{listing.title}</strong><small>{listing.location || 'Ethiopia'} · {formatPrice(listing.price)}</small></span></button>{/each}</div>
  </section>
{:else if ['stays', 'drive', 'experiences', 'shop'].includes(page)}
  {@const category = page === 'stays' ? 'HOTEL' : page === 'drive' ? 'CAR_RENTAL' : page === 'experiences' ? 'EVENT' : 'STORE'}
  <section class="site-hero category-hero"><div class="site-hero-copy"><span class="eyebrow">{tr(page === 'stays' ? 'STAYS IN ETHIOPIA' : page === 'drive' ? 'DRIVE ACROSS ETHIOPIA' : page === 'experiences' ? 'EXPERIENCE ETHIOPIA' : 'SHOP ETHIOPIAN CRAFT', page === 'stays' ? 'በኢትዮጵያ መኖሪያዎች' : page === 'drive' ? 'በኢትዮጵያ ይጓዙ' : page === 'experiences' ? 'የኢትዮጵያ ልምድ' : 'የኢትዮጵያ የእጅ ሥራ')}</span><h1>{tr(page === 'stays' ? 'Find your next stay.' : page === 'drive' ? 'Go further across Ethiopia.' : page === 'experiences' ? 'Make room for something memorable.' : 'Bring Ethiopia home.', page === 'stays' ? 'ቀጣዩን መኖሪያዎን ያግኙ።' : page === 'drive' ? 'በኢትዮጵያ ርቀው ይጓዙ።' : page === 'experiences' ? 'የማይረሳ ልምድ ይፍጠሩ።' : 'ኢትዮጵያን ወደ ቤትዎ ይዘው ይሂዱ።')}</h1><p>{tr('Use the existing EasyService marketplace to compare verified providers, availability and transparent pricing.', 'የተረጋገጡ አቅራቢዎችን፣ መገኘትን እና ግልጽ ዋጋዎችን ለማወዳደር EasyServiceን ይጠቀሙ።')}</p><button class="btn-gold" on:click={() => dispatch('category', category)}>{tr('Browse', 'ይመልከቱ')} {tr(page, page === 'stays' ? 'መኖሪያ' : page === 'drive' ? 'መኪና' : page === 'experiences' ? 'ልምዶች' : 'ግዢ')}</button></div></section>
  <section class="site-content"><div class="filter-strip"><input bind:value={searchTerm} placeholder={tr(`Search ${page}`, 'ይፈልጉ')} aria-label={tr(`Search ${page}`, 'ይፈልጉ')} /><button class="btn-outline" on:click={() => dispatch('category', category)}>{tr('Open marketplace', 'የገበያ ቦታውን ይክፈቱ')}</button></div><div class="site-mini-grid">{#each listings.filter((listing) => listing.category === category && (!searchTerm.trim() || `${listing.title} ${listing.description} ${listing.location}`.toLowerCase().includes(searchTerm.toLowerCase()))).slice(0, 8) as listing}<button class="site-listing" on:click={() => dispatch('openListing', listing)}><img src={listing.imageUrl} alt={listing.title} /><span><strong>{listing.title}</strong><small>{listing.location || 'Ethiopia'} · {formatPrice(listing.price)}</small></span></button>{/each}</div></section>
{:else if destination}
  <section class="site-hero destination-hero" style={`--hero-image: url('${destination.image}')`}><div class="site-hero-copy"><span class="eyebrow">{destination.eyebrow}</span><h1>{tr(`Discover ${destination.name}`, `${destination.name}ን ያግኙ`)}</h1><p>{destination.description}</p><button class="btn-gold" on:click={() => dispatch('location', page === 'addis' ? 'Addis' : destination.name)}>{tr(`Explore ${destination.name}`, `${destination.name}ን ያስሱ`)}</button></div></section>
  <section class="site-content destination-content"><div class="section-heading"><span class="eyebrow">{tr('YOUR LOCAL STARTING POINT', 'የአካባቢዎ መነሻ')}</span><h2>{tr('What brings you here?', 'ወደዚህ ያመጣዎት ምንድነው?')}</h2><p>{tr(`Explore stays, cars, experiences and shops around ${destination.name}.`, `በ${destination.name} ዙሪያ መኖሪያዎችን፣ መኪናዎችን፣ ልምዶችን እና ሱቆችን ያስሱ።`)}</p></div><div class="site-category-grid">{#each destinationHighlights as item}<button on:click={() => dispatch('category', destinationCategory(item[0]))}><Icon name={item[0] === 'Cars' ? 'car' : item[0] === 'Events' ? 'ticket' : item[0] === 'Shops' ? 'bag' : 'bed'} size={24} /><strong>{tr(item[0], item[0])}</strong><span>{tr(item[1], item[1])}</span></button>{/each}</div><div class="destination-map-panel"><div><span class="eyebrow">{tr('EXPLORE THE MAP', 'ካርታውን ያስሱ')}</span><h2>{tr(`Explore ${destination.name}`, `${destination.name}ን ያስሱ`)}</h2><p>{tr('Compare nearby services, verified providers and the places locals return to.', 'በአቅራቢያ ያሉ አገልግሎቶችን፣ የተረጋገጡ አቅራቢዎችን እና የአካባቢውን ተወዳጅ ቦታዎች ያወዳድሩ።')}</p><button class="btn-gold" on:click={() => dispatch('location', destination.name)}>{tr('Explore nearby services', 'በአቅራቢያ ያሉ አገልግሎቶችን ያስሱ')}</button></div><div class="destination-note"><strong>{tr('Popular areas', 'ታዋቂ አካባቢዎች')}</strong><span>{destination.areas}</span></div></div></section>
{:else if page === 'about'}
  <section class="site-hero plain-hero"><div class="site-hero-copy"><span class="eyebrow">{tr('BUILT IN ETHIOPIA', 'በኢትዮጵያ የተገነባ')}</span><h1>{tr('Making Ethiopia easier to discover, experience and connect.', 'ኢትዮጵያን ማወቅ፣ መለማመድ እና መገናኘት ቀላል እንዲሆን እንሰራለን።')}</h1><p>{tr('EasyService brings trusted local businesses and curious customers into one clear marketplace.', 'EasyService የታመኑ የአካባቢ ንግዶችን እና ደንበኞችን በአንድ ግልጽ የገበያ ቦታ ያገናኛል።')}</p></div></section>
  <section class="site-content prose-grid"><article><span class="eyebrow">{tr('OUR MISSION', 'ተልዕኮአችን')}</span><h2>{tr('One dependable starting point.', 'አንድ የታመነ መነሻ ቦታ።')}</h2><p>{tr('From a lakefront stay to a highland road trip, EasyService helps people compare, choose and book with more confidence.', 'ከሐይቅ ዳርቻ መኖሪያ እስከ ደጋ የመንገድ ጉዞ፣ EasyService ሰዎች በመተማመን እንዲያወዳድሩ፣ እንዲመርጡ እና እንዲያስይዙ ይረዳል።')}</p></article><article><span class="eyebrow">{tr('WHAT WE BRING TOGETHER', 'የምናገናኘው')}</span><h2>{tr('Stays, transportation, experiences and shopping.', 'መኖሪያዎች፣ መጓጓዣ፣ ልምዶች እና ግዢ።')}</h2><p>{tr('We are building an ecosystem for customers and providers, with transparent inventory and a distinctly Ethiopian point of view.', 'ግልጽ የምርት መረጃ እና የኢትዮጵያ ልዩ እይታ ያለው ለደንበኞችና ለአቅራቢዎች ስርዓት እየገነባን ነው።')}</p></article></section><section class="site-content info-grid"><article><Icon name="heart" size={24} color="var(--accent-gold)" /><h3>{tr('Our vision', 'ራዕያችን')}</h3><p>{tr('A connected Ethiopian ecosystem where every customer and business can move with confidence.', 'እያንዳንዱ ደንበኛና ንግድ በመተማመን የሚንቀሳቀስበት የተገናኘ የኢትዮጵያ ስርዓት።')}</p></article><article><Icon name="globe" size={24} color="var(--accent-gold)" /><h3>{tr('Made in Ethiopia', 'በኢትዮጵያ የተሰራ')}</h3><p>{tr('Built for Ethiopia, with local context, local businesses and local ambition at the center.', 'ለኢትዮጵያ የተሰራ፣ የአካባቢ እውቀትን፣ ንግዶችን እና ምኞትን መሃል ያደረገ።')}</p></article><article><Icon name="sparkles" size={24} color="var(--accent-gold)" /><h3>{tr('What comes next', 'ቀጣዩ እርምጃ')}</h3><p>{tr('Restaurants, rides, delivery, travel tools and Easy Assistant can grow from this foundation.', 'ምግብ ቤቶች፣ መጓጓዣ፣ ማድረሻ፣ የጉዞ መሳሪያዎች እና Easy Assistant ከዚህ መሰረት ሊያድጉ ይችላሉ።')}</p></article></section>
{:else if page === 'how-it-works'}
  <section class="site-hero plain-hero"><div class="site-hero-copy"><span class="eyebrow">{tr('THE EASYSERVICE METHOD', 'የEASYSERVICE መንገድ')}</span><h1>{tr('Discover. Compare. Choose. Go.', 'ያግኙ። ያወዳድሩ። ይምረጡ። ይጓዙ።')}</h1><p>{tr('A clear path for customers and a practical growth loop for providers.', 'ለደንበኞች ግልጽ መንገድ እና ለአቅራቢዎች ተግባራዊ የእድገት መንገድ።')}</p></div></section>
  <section class="site-content"><div class="section-heading"><span class="eyebrow">{tr('FOR CUSTOMERS', 'ለደንበኞች')}</span><h2>{tr('A simple path from idea to experience.', 'ከሀሳብ እስከ ልምድ ቀላል መንገድ።')}</h2></div><div class="steps-grid">{#each ['Discover what fits', 'Compare transparent options', 'Choose a verified provider', 'Book and pay securely', 'Enjoy the experience', 'Review and return'] as step, index}<article><span>0{index + 1}</span><h3>{tr(step, step)}</h3><p>{tr('Every step keeps the important details visible so decisions feel simple.', 'እያንዳንዱ እርምጃ ወሳኝ ዝርዝሮችን ግልጽ ያደርጋል።')}</p></article>{/each}</div><div class="callout"><span class="eyebrow">{tr('FOR PROVIDERS', 'ለአቅራቢዎች')}</span><h2>{tr('The growth loop is just as clear.', 'የእድገት መንገዱም እንዲሁ ግልጽ ነው።')}</h2><p>{tr('Register, create a listing, add inventory, receive bookings, manage customers and grow through reviews.', 'ይመዝገቡ፣ ዝርዝር መረጃ ይፍጠሩ፣ ዕቃ ይጨምሩ፣ ቦታ ማስያዣ ይቀበሉ፣ ደንበኞችን ያስተዳድሩ እና በግምገማዎች ያድጉ።')}</p><button class="btn-gold" on:click={() => go('become-provider')}>{tr('Become a Provider', 'አቅራቢ ይሁኑ')}</button></div></section>
{:else if page === 'trust'}
  <section class="site-hero plain-hero"><div class="site-hero-copy"><span class="eyebrow">{tr('TRUST & SAFETY', 'እምነት እና ደህንነት')}</span><h1>{tr('Your trust matters.', 'እምነትዎ ያስፈልጋል።')}</h1><p>{tr('Clear records, verified providers and practical safety guidance are part of the experience.', 'ግልጽ መረጃዎች፣ የተረጋገጡ አቅራቢዎች እና ተግባራዊ የደህንነት መመሪያዎች የተሞክሮው አካል ናቸው።')}</p></div></section>
  <section class="site-content info-grid">{#each [['Provider verification', 'Provider profiles are presented with identity and marketplace context.'], ['Secure payments', 'The current build uses a simulated wallet flow and never asks for raw card credentials.'], ['Booking protection', 'Reservations keep dates, status, quantity and provider details together.'], ['Reviews with context', 'Reviews should be tied to completed reservations to protect authenticity.'], ['Fraud prevention', 'Suspicious activity may be investigated and reported through support.'], ['Customer safety', 'Meet in public, protect your account, verify details and contact support when something feels unsafe.'], ['Provider safety', 'Keep conversations on-platform, document bookings and report suspicious requests.'], ['Reporting', 'Report a listing, provider or suspicious activity to the EasyService team.']] as item}<article><Icon name="shield" size={22} color="var(--accent-gold)" /><h3>{tr(item[0], item[0])}</h3><p>{tr(item[1], item[1])}</p></article>{/each}</section><section class="site-content"><div class="callout"><h2>{tr('See something unsafe?', 'አስተማማኝ ያልሆነ ነገር አዩ?')}</h2><p>{tr('Our support team can help review a listing, provider or interaction.', 'የድጋፍ ቡድናችን ዝርዝር መረጃን፣ አቅራቢን ወይም ግንኙነትን ለመመርመር ሊረዳ ይችላል።')}</p><button class="btn-gold" on:click={() => go('help')}>{tr('Report a Safety Concern', 'የደህንነት ጉዳይ ሪፖርት ያድርጉ')}</button></div></section>
{:else if page === 'terms' || page === 'privacy'}
  <section class="site-hero legal-hero"><div class="site-hero-copy"><span class="eyebrow">EASYSERVICE {page === 'terms' ? tr('TERMS OF SERVICE', 'የአገልግሎት ውሎች') : tr('PRIVACY POLICY', 'የግላዊነት ፖሊሲ')}</span><h1>{page === 'terms' ? tr('The rules should be readable.', 'ደንቦቹ ሊነበቡ ይገባል።') : tr('Privacy should be understandable.', 'ግላዊነት ሊገባ ይገባል።')}</h1><p>{tr('Last updated September 15, 2026 · Academic demonstration draft', 'መጨረሻ የተሻሻለው መስከረም 15፣ 2026 · የትምህርት ማሳያ ረቂቅ')}</p></div></section>
  <section class="site-content legal-layout"><nav><strong>{tr('Contents', 'ይዘቶች')}</strong>{#each legalSections[page] as heading}<a href={`#${heading.toLowerCase().replaceAll(' ', '-')}`}>{heading}</a>{/each}</nav><article><h2>{page === 'terms' ? tr('A practical marketplace agreement', 'ተግባራዊ የገበያ ስምምነት') : tr('A clear explanation of data use', 'የመረጃ አጠቃቀም ግልጽ ማብራሪያ')}</h2><p>{tr('This academic demonstration draft explains the responsibilities, choices and safeguards associated with using EasyService. Before commercial launch, qualified legal and privacy professionals should review the final version.', 'ይህ የትምህርት ማሳያ ረቂቅ EasyServiceን ሲጠቀሙ ያሉ ኃላፊነቶችን፣ ምርጫዎችን እና ጥበቃዎችን ያብራራል። ከንግድ ማስጀመር በፊት የመጨረሻው ሰነድ በባለሙያዎች መገምገም አለበት።')}</p>{#each legalSections[page] as heading}<section id={heading.toLowerCase().replaceAll(' ', '-')}><h3>{heading}</h3><p>{tr(`EasyService uses this section to explain the responsibilities, choices and safeguards associated with ${heading.toLowerCase()}. The language should remain specific, useful and easy to revisit.`, `EasyService ይህንን ክፍል በመጠቀም ከ${heading} ጋር የተያያዙ ኃላፊነቶችን፣ ምርጫዎችን እና ጥበቃዎችን ያብራራል።`)}</p></section>{/each}</article></section>
{:else if page === 'help' || page === 'provider-support'}
  {@const supportList = page === 'help' ? supportCategories : providerSupportCategories}
  <section class="site-hero plain-hero support-hero"><div class="site-hero-copy"><span class="eyebrow">{page === 'help' ? tr('CUSTOMER SUPPORT', 'የደንበኛ ድጋፍ') : tr('PROVIDER SUPPORT', 'የአቅራቢ ድጋፍ')}</span><h1>{page === 'help' ? tr('How can we help?', 'እንዴት ልንረዳዎት እንችላለን?') : tr('Help for the people who make the marketplace.', 'የገበያ ቦታውን ለሚያስኬዱ ሰዎች ድጋፍ።')}</h1><p>{tr('Search practical answers, browse a support category or start a conversation with the EasyService team.', 'ተግባራዊ መልሶችን ይፈልጉ፣ የድጋፍ ምድብ ይምረጡ ወይም ከEasyService ቡድን ጋር ውይይት ይጀምሩ።')}</p><div class="site-search"><Icon name="search" size={18} /><input bind:value={searchTerm} placeholder={tr('Search EasyService Help', 'የEasyService እርዳታ ይፈልጉ')} aria-label={tr('Search EasyService Help', 'የEasyService እርዳታ ይፈልጉ')} /></div></div></section>
  <section class="site-content support-page"><div class="support-category-grid">{#each supportList as category}<button class="support-category" on:click={() => searchTerm = category}><Icon name={page === 'help' ? 'life-buoy' : 'building'} size={20} /><strong>{category}</strong><span>{tr('Browse helpful answers', 'ጠቃሚ መልሶችን ይመልከቱ')}</span></button>{/each}</div><div class="section-heading compact"><span class="eyebrow">{tr('POPULAR QUESTIONS', 'ታዋቂ ጥያቄዎች')}</span><h2>{tr('Answers for the moments that matter.', 'ለአስፈላጊ ጊዜዎች መልሶች።')}</h2></div><div class="info-grid">{#each faqs.filter((faq) => !searchTerm || faq.join(' ').toLowerCase().includes(searchTerm.toLowerCase())) as faq}<article><h3>{tr(faq[0], faq[0])}</h3><p>{tr(faq[1], faq[1])}</p></article>{/each}</div><div class="support-form"><span class="eyebrow">{tr('CONTACT SUPPORT', 'ድጋፍን ያነጋግሩ')}</span><h2>{tr('Still need help?', 'አሁንም እርዳታ ይፈልጋሉ?')}</h2><select bind:value={supportTopic} class="input-field">{#each supportList as category}<option>{category}</option>{/each}</select><textarea class="input-field" rows="4" placeholder={tr('Tell us what happened', 'የተፈጠረውን ይንገሩን')}></textarea><div class="support-actions"><button class="btn-gold" on:click={startSupportConversation}>{tr('Start a conversation', 'ውይይት ይጀምሩ')}</button><button class="btn-outline" on:click={startSupportConversation}>{tr(page === 'help' ? 'Contact Support' : 'Contact Provider Support', page === 'help' ? 'ድጋፍን ያነጋግሩ' : 'የአቅራቢ ድጋፍን ያነጋግሩ')}</button></div>{#if supportSubmitted}<p class="form-message">{walletMessage}</p><div class="ticket-preview"><strong>{tr('Support ticket', 'የድጋፍ ትኬት')}</strong><span>ES-{new Date().getFullYear()}-10428 · {tr('Open', 'ክፍት')}</span><small>{tr('Conversation will appear here when support replies.', 'ድጋፍ ሲመልስ ውይይቱ እዚህ ይታያል።')}</small></div>{/if}</div></section>
{:else if page === 'passport'}
  <section class="site-hero passport-hero"><div class="site-hero-copy"><span class="eyebrow">EASYSERVICE PASSPORT</span><h1>{currentUser?.name || tr('Your travel profile', 'የጉዞ መገለጫዎ')}</h1><p>{currentUser ? tr('Your verified customer identity and marketplace activity, together in one place.', 'የተረጋገጠ የደንበኛ መለያዎ እና የገበያ እንቅስቃሴዎ በአንድ ቦታ።') : tr('Sign in to view your customer profile and activity.', 'የደንበኛ መገለጫዎን እና እንቅስቃሴዎን ለማየት ይግቡ።')}</p></div></section>
  <section class="site-content"><div class="passport-stats"><div><strong>{bookings.length}</strong><span>Bookings</span></div><div><strong>{new Set(bookings.map((booking) => booking.location).filter(Boolean)).size}</strong><span>Destinations</span></div><div><strong>{currentUser?.identityStatus || 'Not signed in'}</strong><span>Identity status</span></div></div><div class="callout"><h2>Your EasyService history belongs here.</h2><p>Saved destinations, favorite places, reviews and preferences can grow from this foundation.</p><button class="btn-gold" on:click={() => go('my-bookings')}>View My Bookings</button></div></section>
{:else if page === 'wallet'}
  <section class="site-hero wallet-hero"><div class="site-hero-copy"><span class="eyebrow">EASY WALLET</span><h1>{tr('Keep the next booking simple.', 'ቀጣዩን ቦታ ማስያዝ ቀላል ያድርጉ።')}</h1><p>{tr('A demo wallet foundation for balance, refunds and transaction history.', 'ለቀሪ ሂሳብ፣ ተመላሽ እና የግብይት ታሪክ የማሳያ የኪስ ቦርሳ።')}</p></div></section>
  <section class="site-content wallet-layout"><div class="wallet-balance"><span>Available balance</span><strong>ETB {Number(currentUser?.balance || 0).toLocaleString(undefined, { minimumFractionDigits: 2 })}</strong><small>Demo balance · production payments require a secure provider</small></div><div class="wallet-actions"><label for="walletAmount">Add demo funds</label><input id="walletAmount" type="number" min="1" bind:value={walletAmount} class="input-field" /><button class="btn-gold" on:click={submitWallet}>Add Money</button>{#if walletMessage}<p class="form-message">{walletMessage}</p>{/if}</div><div class="callout"><h2>Recent activity</h2>{#if bookings.length === 0}<p>No wallet activity yet. Your confirmed bookings will appear here.</p>{:else}{#each bookings.slice(0, 5) as booking}<p class="activity-row"><span>{booking.listingTitle}</span><strong>ETB {Number(booking.totalAmount || 0).toLocaleString()}</strong></p>{/each}{/if}</div></section>
{:else if page === 'become-provider'}
  <section class="site-hero provider-hero"><div class="site-hero-copy"><span class="eyebrow">{tr('FOR ETHIOPIAN BUSINESSES', 'ለኢትዮጵያ ንግዶች')}</span><h1>{tr('Turn your service into a business on EasyService.', 'አገልግሎትዎን በEasyService ንግድ ያድርጉ።')}</h1><p>{tr('Create a provider profile, publish inventory, receive bookings and grow through trusted reviews.', 'የአቅራቢ መገለጫ ይፍጠሩ፣ ዕቃዎን ያትሙ፣ ቦታ ማስያዣዎችን ይቀበሉ እና በታመኑ ግምገማዎች ያድጉ።')}</p><button class="btn-gold" on:click={() => dispatch('provider')}>{tr('Become a Provider', 'አቅራቢ ይሁኑ')}</button></div></section>
  <section class="site-content"><div class="steps-grid">{#each [['Create your provider account', 'የአቅራቢ መለያዎን ይፍጠሩ'], ['Build your listing', 'ዝርዝር መረጃዎን ያዘጋጁ'], ['Add inventory', 'ዕቃዎን ይጨምሩ'], ['Receive bookings', 'ቦታ ማስያዣዎችን ይቀበሉ'], ['Grow with reviews', 'በግምገማዎች ያድጉ']] as step, index}<article><span>0{index + 1}</span><h3>{tr(step[0], step[1])}</h3><p>{tr('Simple tools keep the work visible from first listing to repeat customer.', 'ቀላል መሳሪያዎች ከመጀመሪያው ዝርዝር እስከ ተደጋጋሚ ደንበኛ ድረስ ስራውን ግልጽ ያደርጋሉ።')}</p></article>{/each}</div></section>
{/if}

{#if !['terms', 'privacy'].includes(page)}
  <section class="site-lower-band">
    <div class="site-lower-copy">
      <span class="eyebrow">THE EASYSERVICE DIFFERENCE</span>
      <h2>{page === 'become-provider' ? tr('A better storefront for businesses with somewhere to go.', 'ወደፊት ለሚሄዱ ንግዶች የተሻለ መድረክ።') : page === 'wallet' ? tr('Keep the important details in one place.', 'አስፈላጊ ዝርዝሮችን በአንድ ቦታ ያስቀምጡ።') : tr('Made for the way people move through Ethiopia.', 'ሰዎች በኢትዮጵያ ለሚጓዙበት መንገድ የተሰራ።')}</h2>
      <p>{page === 'become-provider' ? tr('Show what makes your service special, keep availability clear and turn discovery into a real booking.', 'አገልግሎትዎን ልዩ የሚያደርገውን ያሳዩ፣ መገኘቱን ግልጽ ያድርጉ እና ፍለጋን ወደ እውነተኛ ቦታ ማስያዣ ይቀይሩ።') : tr('From the first search to the final reservation, EasyService keeps choices, prices and people connected.', 'ከመጀመሪያው ፍለጋ እስከ መጨረሻው ቦታ ማስያዣ፣ EasyService ምርጫዎችን፣ ዋጋዎችን እና ሰዎችን ያገናኛል።')}</p>
      <button class="btn-gold" on:click={() => go(page === 'become-provider' ? 'provider' : 'explore')}>{page === 'become-provider' ? tr('Open Provider Hub', 'የአቅራቢ ማዕከልን ይክፈቱ') : tr('Explore the marketplace', 'የገበያ ቦታውን ያስሱ')}</button>
    </div>
    <div class="site-lower-image"><img src="https://images.unsplash.com/photo-1530789253388-582c481c54b0?auto=format&fit=crop&w=1100&q=85" alt="Traveller looking across an Ethiopian landscape" /></div>
  </section>
{/if}

<style>
  .site-hero { min-height: 500px; display: flex; align-items: center; padding: 64px clamp(24px, 8vw, 140px); position: relative; isolation: isolate; background: linear-gradient(90deg, rgba(8,18,34,.96) 0%, rgba(8,18,34,.72) 42%, rgba(8,18,34,.18) 100%), var(--hero-image, linear-gradient(135deg, #0b1930, #34495b)); background-size: cover; background-position: center; color: #fff; }
  .site-hero::after { content: ''; position: absolute; inset: auto 0 0; height: 150px; z-index: -1; background: linear-gradient(transparent, rgba(8,18,34,.42)); pointer-events: none; }
  .site-hero-copy { max-width: 820px; width: 100%; padding-left: 22px; border-left: 3px solid var(--accent-gold); animation: siteReveal .65s ease both; }
  .site-hero h1 { max-width: 820px; margin: 16px 0 20px; font-size: clamp(2.8rem, 5vw, 5.2rem); line-height: .98; letter-spacing: -.04em; font-weight: 800; }
  .site-hero p { max-width: 650px; color: rgba(255,255,255,.84); font-size: clamp(1rem, 1.5vw, 1.25rem); line-height: 1.7; }
  .plain-hero { background: radial-gradient(circle at 78% 22%, rgba(200,155,60,.22), transparent 28%), linear-gradient(115deg, #0b1930 0%, #16283d 58%, #3b403b 100%); }
  .category-hero { background-image: linear-gradient(90deg, rgba(8,18,34,.92), rgba(8,18,34,.32)), var(--hero-image, url('https://images.unsplash.com/photo-1500534623283-312aade485b7?auto=format&fit=crop&w=1800&q=85')); }
  .destination-hero { min-height: 650px; }
  .site-search { max-width: 600px; display: flex; align-items: center; gap: 10px; margin-top: 34px; padding: 6px 18px; background: #fff; border-radius: 8px; color: #6b7280; box-shadow: 0 14px 35px rgba(0,0,0,.18); }
  .site-search input { flex: 1; border: 0; padding: 13px 0; outline: 0; font: inherit; color: #111827; }
  .site-content { max-width: 1240px; margin: 0 auto; padding: 96px 32px; }
  .site-content:nth-of-type(even) { max-width: none; padding-left: max(32px, calc((100vw - 1176px) / 2)); padding-right: max(32px, calc((100vw - 1176px) / 2)); background: var(--bg-surface-secondary); }
  .section-heading { max-width: 720px; margin-bottom: 36px; }.section-heading.compact { margin-top: 96px; }
  .section-heading h2, .prose-grid h2, .callout h2 { margin: 8px 0 12px; font-size: clamp(1.8rem, 3vw, 3rem); line-height: 1.1; }
  .section-heading p, .prose-grid p, .callout p { color: var(--text-muted); line-height: 1.75; }
  .eyebrow { color: var(--accent-gold); font-size: .72rem; font-weight: 800; letter-spacing: .14em; text-transform: uppercase; }
  .site-category-grid, .site-mini-grid, .info-grid, .steps-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
  .site-category-grid button, .site-listing, .info-grid article, .steps-grid article, .support-form, .wallet-balance, .wallet-actions { border: 1px solid var(--border-subtle); background: var(--bg-surface); border-radius: 10px; color: var(--text-main); text-align: left; padding: 26px; box-shadow: 0 10px 30px rgba(17,24,39,.06); }
  .site-category-grid button { min-height: 180px; display: flex; flex-direction: column; justify-content: flex-end; gap: 9px; cursor: pointer; transition: transform .25s ease, border-color .25s ease, box-shadow .25s ease; }.site-category-grid button:hover { transform: translateY(-6px); border-color: var(--accent-gold); box-shadow: 0 18px 36px rgba(17,24,39,.12); }
  .site-category-grid strong, .site-listing strong { font-size: 1.08rem; }.site-category-grid span, .site-listing small, .info-grid p, .steps-grid p { color: var(--text-muted); line-height: 1.65; }
  .site-mini-grid { grid-template-columns: repeat(3, 1fr); }.site-listing { display: flex; align-items: center; gap: 16px; cursor: pointer; min-height: 112px; }.site-listing img { width: 94px; height: 78px; object-fit: cover; border-radius: 7px; }.site-listing span { display: grid; gap: 7px; }
  .filter-strip { display: flex; gap: 12px; margin-bottom: 28px; }.filter-strip input, .support-form textarea { flex: 1; }.filter-strip input { border: 1px solid var(--border-subtle); background: var(--bg-surface); color: var(--text-main); padding: 12px 14px; border-radius: 8px; font: inherit; }
  .destination-note, .callout { margin-top: 48px; border-left: 3px solid var(--accent-gold); padding: 24px 28px; background: var(--bg-surface-secondary); display: grid; gap: 8px; }.destination-note span { color: var(--text-muted); }
  .prose-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 80px; }.steps-grid { grid-template-columns: repeat(3, 1fr); }.steps-grid article span { color: var(--accent-gold); font-weight: 800; font-size: 1.1rem; }.steps-grid h3 { margin: 22px 0 10px; font-size: 1.2rem; }
  .info-grid { grid-template-columns: repeat(3, 1fr); }.info-grid article { min-height: 190px; display: grid; align-content: start; gap: 14px; }.info-grid h3 { font-size: 1.1rem; }
  .site-lower-band { max-width: 1240px; margin: 0 auto; padding: 40px 32px 110px; display: grid; grid-template-columns: 1fr 1fr; gap: 54px; align-items: center; }.site-lower-copy { padding: 28px 0; }.site-lower-copy h2 { max-width: 600px; margin: 12px 0 16px; font-size: clamp(2rem, 4vw, 4rem); line-height: 1.03; letter-spacing: -.03em; }.site-lower-copy p { max-width: 540px; margin-bottom: 26px; color: var(--text-muted); font-size: 1.05rem; line-height: 1.75; }.site-lower-image { height: 420px; overflow: hidden; border-radius: 12px; }.site-lower-image img { width: 100%; height: 100%; object-fit: cover; }
  @keyframes siteReveal { from { opacity: 0; transform: translateY(18px); } to { opacity: 1; transform: translateY(0); } }
  .legal-layout { display: grid; grid-template-columns: 220px 1fr; gap: 58px; align-items: start; }.legal-layout nav { position: sticky; top: 24px; display: grid; gap: 12px; }.legal-layout nav a { color: var(--text-muted); text-decoration: none; }.legal-layout article { max-width: 760px; }.legal-layout article > p, .legal-layout section p { color: var(--text-muted); line-height: 1.8; }.legal-layout h2 { font-size: 2rem; margin-bottom: 14px; }.legal-layout section { padding: 28px 0; border-bottom: 1px solid var(--border-subtle); }.legal-layout h3 { margin-bottom: 8px; }
  .support-category-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }.support-category { min-height: 140px; display: grid; align-content: end; gap: 8px; padding: 22px; border: 1px solid var(--border-subtle); border-radius: 10px; background: var(--bg-surface); color: var(--text-main); text-align: left; cursor: pointer; box-shadow: 0 10px 30px rgba(17,24,39,.06); }.support-category:hover { border-color: var(--accent-gold); transform: translateY(-3px); }.support-category span { color: var(--text-muted); font-size: .88rem; }.support-actions { display: flex; flex-wrap: wrap; gap: 12px; }.ticket-preview { display: grid; gap: 6px; padding: 18px; border-left: 3px solid var(--accent-gold); background: var(--bg-surface-secondary); }.ticket-preview span, .ticket-preview small { color: var(--text-muted); }.destination-map-panel { margin-top: 52px; padding: 34px; display: grid; grid-template-columns: 1.3fr 1fr; gap: 28px; align-items: center; border-radius: 12px; color: #fff; background: linear-gradient(120deg, #0b1930, #24435d); }.destination-map-panel p { max-width: 600px; color: rgba(255,255,255,.78); line-height: 1.7; }.destination-map-panel .destination-note { margin-top: 0; background: rgba(255,255,255,.09); border-left-color: var(--accent-gold); }.destination-map-panel .destination-note span { color: rgba(255,255,255,.8); }.legal-layout article { font-size: 1rem; }.legal-layout article > p, .legal-layout section p { font-size: 1rem; }
  .support-form { display: grid; gap: 12px; max-width: 680px; margin-top: 34px; }.support-form h2 { margin-bottom: 4px; }.form-message { color: var(--status-success-text); font-weight: 700; }.passport-stats { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }.passport-stats div { padding: 26px; background: var(--bg-surface); border: 1px solid var(--border-subtle); border-radius: 8px; display: grid; gap: 6px; }.passport-stats strong { font-size: 1.8rem; }.passport-stats span, .wallet-balance span, .wallet-balance small { color: var(--text-muted); }.wallet-layout { display: grid; grid-template-columns: 1.2fr 1fr; gap: 18px; }.wallet-balance { display: grid; gap: 12px; }.wallet-balance strong { font-size: 2.8rem; color: var(--accent-gold); }.wallet-actions { display: grid; gap: 10px; }.activity-row { display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid var(--border-subtle); }.wallet-layout .callout { grid-column: 1 / -1; }
  @media (max-width: 760px) { .site-hero, .destination-hero { min-height: 520px; padding: 52px 20px; }.site-hero h1 { font-size: 3.3rem; }.site-content, .site-content:nth-of-type(even) { padding: 56px 18px; }.site-category-grid, .site-mini-grid, .info-grid, .steps-grid, .prose-grid, .passport-stats, .wallet-layout, .site-lower-band, .support-category-grid, .destination-map-panel { grid-template-columns: 1fr; }.site-lower-band { padding: 20px 18px 70px; gap: 24px; }.site-lower-image { height: 280px; }.legal-layout { grid-template-columns: 1fr; gap: 30px; }.legal-layout nav { position: static; }.filter-strip { flex-direction: column; } }
</style>
