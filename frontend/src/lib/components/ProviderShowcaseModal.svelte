<script>
  import Icon from './Icon.svelte';

  export let listing = null;
  export let onClose = () => {};
  export let onSelectVariant = (variantListing) => {};
  export let onToggleFavorite = (id) => {};
  export let isFavorite = false;
  export let currentLanguage = 'en';
  export let currency = 'ETB';

  const currencyRates = { ETB: 1, USD: 0.018, EUR: 0.0165, GBP: 0.014 };
  function formatPrice(amount) {
    const converted = Number(amount || 0) * (currencyRates[currency] || 1);
    return `${currency} ${converted.toLocaleString(undefined, { maximumFractionDigits: currency === 'ETB' ? 0 : 2 })}`;
  }

  $: labels = currentLanguage === 'am'
    ? { rooms: 'ክፍሎች እና ስዊቶች', roomsDesc: 'ከተለያዩ ምቹና ሰፊ ክፍሎች ይምረጡ።', location: 'አካባቢን ይመልከቱ', available: 'ይገኛል', units: 'ክፍሎች', book: 'አሁን ይያዙ', nights: 'የሚቆዩባቸው ሌሊቶች' }
    : { rooms: 'Rooms & Suites', roomsDesc: 'Choose from a variety of comfortable and spacious rooms.', location: 'View Location', available: 'Available', units: 'units', book: 'Book Now', nights: 'Nights to stay' };

  let selectedVariantIndex = 0;
  let stayNights = 1;
  let rentalDurationType = 'DAYS'; // 'HOURS' | 'DAYS'
  let rentalDurationCount = 1;
  let includeDriver = false; // Car rental driver option (+500 ETB/day)
  let shareMsg = '';
  let selectedHeroImg = '';

  // Category-specific gallery thumbnail arrays (strictly matched to category!)
  const categoryGalleries = {
    HOTEL: [
      'https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=300&q=80',
      'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=300&q=80',
      'https://images.unsplash.com/photo-1591088398332-8a7791972843?auto=format&fit=crop&w=300&q=80',
      'https://images.unsplash.com/photo-1618773928121-c32242e63f39?auto=format&fit=crop&w=300&q=80'
    ],
    CAR_RENTAL: [
      'https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?auto=format&fit=crop&w=300&q=80',
      'https://images.unsplash.com/photo-1549399542-7e3f8b79c341?auto=format&fit=crop&w=300&q=80',
      'https://images.unsplash.com/photo-1617814076367-b759c7d7e738?auto=format&fit=crop&w=300&q=80',
      'https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=300&q=80'
    ],
    EVENT: [
      'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=300&q=80',
      'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=300&q=80',
      'https://images.unsplash.com/photo-1501386761578-eac5c94b800a?auto=format&fit=crop&w=300&q=80',
      'https://images.unsplash.com/photo-1492684223066-81342ee5ff30?auto=format&fit=crop&w=300&q=80'
    ],
    STORE: [
      'https://images.unsplash.com/photo-1517256064527-09c73fc73e38?auto=format&fit=crop&w=300&q=80',
      'https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=300&q=80',
      'https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=300&q=80',
      'https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?auto=format&fit=crop&w=300&q=80'
    ]
  };

  $: currentGallery = listing ? (categoryGalleries[listing.category] || categoryGalleries.HOTEL) : categoryGalleries.HOTEL;
  $: heroImage = selectedHeroImg || listing?.imageUrl || currentGallery[0];

  // Multi-option room, car, ticket, and product inventories mapped by provider listing
  const providerInventories = {
    HOTEL: [
      {
        variantId: 'var_h1',
        title: 'Standard Room',
        desc: 'Comfortable room with garden view, perfect for couples or solo travelers.',
        specs: { guests: '2 Guests', beds: '1 Bed', size: '28 m²' },
        pricePerUnit: 3500,
        availableCount: 5,
        unitLabel: '/ night',
        img: 'https://images.unsplash.com/photo-1566665797739-1674de7a421a?auto=format&fit=crop&w=600&q=80'
      },
      {
        variantId: 'var_h2',
        title: 'Deluxe Room',
        desc: 'Spacious room with lake view and modern amenities.',
        specs: { guests: '2 Guests', beds: '1 King Bed', size: '35 m²' },
        pricePerUnit: 5000,
        availableCount: 3,
        unitLabel: '/ night',
        img: 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=600&q=80'
      },
      {
        variantId: 'var_h3',
        title: 'Family Room',
        desc: 'Ideal for families, with extra space and comfortable bedding.',
        specs: { guests: '4 Guests', beds: '2 Beds', size: '45 m²' },
        pricePerUnit: 7000,
        availableCount: 5,
        unitLabel: '/ night',
        img: 'https://images.unsplash.com/photo-1591088398332-8a7791972843?auto=format&fit=crop&w=600&q=80'
      },
      {
        variantId: 'var_h4',
        title: 'Villa Suite',
        desc: 'Luxurious villa with living area, lake view and premium amenities.',
        specs: { guests: '5 Guests', beds: '2 King Beds', size: '80 m²' },
        pricePerUnit: 10000,
        availableCount: 3,
        unitLabel: '/ night',
        img: 'https://images.unsplash.com/photo-1618773928121-c32242e63f39?auto=format&fit=crop&w=600&q=80'
      }
    ],
    CAR_RENTAL: [
      {
        variantId: 'var_c1',
        title: 'Toyota Land Cruiser V8 4×4',
        desc: 'Heavy-duty 4x4 off-road vehicle suitable for highland tours and rugged terrain.',
        specs: { guests: '7 Seats', beds: 'Automatic', size: 'Diesel • 2024' },
        pricePerUnit: 3500,
        hourlyRate: 500,
        driverFee: 500,
        availableCount: 3,
        unitLabel: '/ day',
        img: 'https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?auto=format&fit=crop&w=600&q=80'
      },
      {
        variantId: 'var_c2',
        title: 'Hyundai Tucson SUV',
        desc: 'Fuel-efficient crossover SUV, ideal for city driving and smooth airport transfers.',
        specs: { guests: '5 Seats', beds: 'Automatic', size: 'Petrol • 2023' },
        pricePerUnit: 2500,
        hourlyRate: 350,
        driverFee: 500,
        availableCount: 5,
        unitLabel: '/ day',
        img: 'https://images.unsplash.com/photo-1549399542-7e3f8b79c341?auto=format&fit=crop&w=600&q=80'
      },
      {
        variantId: 'var_c3',
        title: 'Mercedes-Benz E-Class Executive',
        desc: 'VIP executive leather-seat luxury sedan for VIP delegation reception and events.',
        specs: { guests: '5 Seats', beds: 'Automatic', size: 'Executive • 2024' },
        pricePerUnit: 4000,
        hourlyRate: 600,
        driverFee: 500,
        availableCount: 2,
        unitLabel: '/ day',
        img: 'https://images.unsplash.com/photo-1617814076367-b759c7d7e738?auto=format&fit=crop&w=600&q=80'
      }
    ],
    EVENT: [
      {
        variantId: 'var_e1',
        title: 'Regular Entrance Pass',
        desc: 'Access to main stage music performances, cultural food court, and artisan booths.',
        specs: { guests: 'General Entrance', beds: 'Main Stage', size: 'Open Lawn' },
        pricePerUnit: 500,
        availableCount: 120,
        unitLabel: '/ ticket',
        img: 'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=600&q=80'
      },
      {
        variantId: 'var_e2',
        title: 'VIP Elevated Lounge Pass',
        desc: 'Exclusive VIP elevated viewing lounge, complimentary drinks, and private bar access.',
        specs: { guests: 'VIP Lounge', beds: 'Free Drinks', size: 'Elevated View' },
        pricePerUnit: 1500,
        availableCount: 35,
        unitLabel: '/ ticket',
        img: 'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=600&q=80'
      },
      {
        variantId: 'var_e3',
        title: 'VVIP Artist Meet & Greet Pass',
        desc: 'Backstage pass with exclusive artist meet & greet, photo ops, and private dinner buffet.',
        specs: { guests: 'Backstage Access', beds: 'Artist Dinner', size: 'Private Lounge' },
        pricePerUnit: 3000,
        availableCount: 10,
        unitLabel: '/ ticket',
        img: 'https://images.unsplash.com/photo-1501386761578-eac5c94b800a?auto=format&fit=crop&w=600&q=80'
      }
    ],
    STORE: [
      {
        variantId: 'var_s1',
        title: 'Authentic Ceramic Jebena Set',
        desc: 'Handcrafted clay jebena coffee pot with 6 ceramic cups and wooden serving tray.',
        specs: { guests: '12-Piece Set', beds: 'Organic Clay', size: 'Handcrafted' },
        pricePerUnit: 450,
        availableCount: 20,
        unitLabel: '/ set',
        img: 'https://images.unsplash.com/photo-1517256064527-09c73fc73e38?auto=format&fit=crop&w=600&q=80'
      },
      {
        variantId: 'var_s2',
        title: 'Handwoven Silk Habesha Kemis',
        desc: 'Traditional Ethiopian white cotton dress with gold embroidered border work.',
        specs: { guests: 'Pure Silk', beds: 'Size M/L/XL', size: 'Gold Border' },
        pricePerUnit: 3200,
        availableCount: 8,
        unitLabel: '/ piece',
        img: 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=600&q=80'
      }
    ]
  };

  $: listingVariants = (listing?.variants && listing.variants.length)
    ? listing.variants.map((v) => ({
        variantId: v.id,
        title: v.title,
        desc: v.desc,
        specs: {
          guests: v.badge || 'Available option',
          beds: v.unitLabel || '',
          size: `${v.availableCount} in stock`
        },
        pricePerUnit: v.price,
        hourlyRate: v.hourlyRate || Math.round((v.price || 0) / 7),
        driverFee: 500,
        availableCount: v.availableCount,
        unitLabel: v.unitLabel,
        img: listing.imageUrl
      }))
    : (listing ? (providerInventories[listing.category] || providerInventories.HOTEL) : []);
  $: categoryVariants = listingVariants;
  $: currentVariant = categoryVariants[selectedVariantIndex] || categoryVariants[0];

  $: calculatedPrice = listing?.category === 'HOTEL' 
    ? (currentVariant ? currentVariant.pricePerUnit * stayNights : 0)
    : (listing?.category === 'CAR_RENTAL'
      ? (rentalDurationType === 'HOURS' 
          ? ((currentVariant?.hourlyRate || 400) + (includeDriver ? 100 : 0)) * rentalDurationCount 
          : ((currentVariant?.pricePerUnit || 3500) + (includeDriver ? 500 : 0)) * rentalDurationCount)
      : (currentVariant?.pricePerUnit || 0));

  function handleProceedToBooking(variantIndex = null) {
    if (variantIndex !== null) {
      selectedVariantIndex = variantIndex;
    }
    const selectedUnit = categoryVariants[selectedVariantIndex] || currentVariant;
    if (!selectedUnit) return;

    const variantListing = {
      ...listing,
      title: `${listing.title} — ${selectedUnit.title}`,
      price: selectedUnit.pricePerUnit,
      availableQuantity: selectedUnit.availableCount,
      variantDetails: {
        variantId: selectedUnit.id,
        variantName: selectedUnit.title,
        specs: `${selectedUnit.specs.guests} • ${selectedUnit.specs.beds} • ${selectedUnit.specs.size}`,
        withDriver: listing.category === 'CAR_RENTAL' ? includeDriver : false,
        stayNights: listing.category === 'HOTEL' ? stayNights : null,
        rentalDuration: listing.category === 'CAR_RENTAL' ? `${rentalDurationCount} ${rentalDurationType.toLowerCase()}` : null
      }
    };

    onSelectVariant(variantListing);
  }

  function handleShare() {
    const shareText = `${listing.title} — EasyService Ethiopia (${listing.location || 'Ethiopia'})`;
    try {
      if (navigator.clipboard && window.isSecureContext) {
        navigator.clipboard.writeText(shareText);
      }
    } catch {
      /* ignore */
    }
    shareMsg = 'Link copied — share this listing with a friend.';
    setTimeout(() => shareMsg = '', 2500);
  }

  function openMaps() {
    const q = encodeURIComponent(`${listing.location || 'Addis Ababa'}, Ethiopia`);
    window.open(`https://www.google.com/maps/search/?api=1&query=${q}`, '_blank', 'noopener');
  }

  function formatEventDate(value) {
    if (!value) return 'Date not listed';
    return new Date(`${value}T00:00:00`).toLocaleDateString('en-US', { weekday: 'long', month: 'long', day: 'numeric', year: 'numeric' });
  }

  function toCalendarDate(dateValue, timeValue) {
    if (!dateValue) return '';
    return `${dateValue.replaceAll('-', '')}T${(timeValue || '00:00').replace(':', '')}00`;
  }

  function addEventToCalendar() {
    if (!listing?.eventDate) return;
    const params = new URLSearchParams({
      action: 'TEMPLATE',
      text: listing.title,
      dates: `${toCalendarDate(listing.eventDate, listing.eventStartTime)}/${toCalendarDate(listing.eventDate, listing.eventEndTime)}`,
      details: listing.description || '',
      location: listing.location || 'Ethiopia'
    });
    window.open(`https://calendar.google.com/calendar/render?${params.toString()}`, '_blank', 'noopener,noreferrer');
  }
</script>

{#if listing}
  <div class="property-detail-page animate-fade-in">
    
    <!-- Navigation Breadcrumb & Back Action Bar -->
    <div class="page-nav-bar">
      <button class="back-btn" on:click={onClose}>
        ← Back to Listings
      </button>
      
      <div class="top-right-actions">
        <span class="category-pill">{listing.category === 'HOTEL' ? 'Hotel' : listing.category === 'CAR_RENTAL' ? 'Car Rental' : listing.category === 'EVENT' ? 'Event' : 'Store'}</span>
        <button class="action-btn favorite-action {isFavorite ? 'saved' : ''}" on:click={() => listing?.id && onToggleFavorite(listing.id)} aria-pressed={isFavorite}>
          <Icon name={isFavorite ? 'heart-filled' : 'heart'} size={16} color={isFavorite ? '#ef4444' : 'currentColor'} />
          {isFavorite ? 'Saved' : 'Save'}
        </button>
        <button class="action-btn" on:click={handleShare}>{shareMsg ? '✓ Copied' : '🔗 Share'}</button>
      </div>
    </div>

    <!-- Main Property Grid Layout (Image Left / Info Right) -->
    <div class="property-showcase-grid">
      
      <!-- Left Column: Large Hero Image + Gallery Thumbnails -->
      <div class="gallery-column">
        <div class="hero-image-wrapper">
          <img src={heroImage} alt={listing.title} class="hero-img" />
        </div>
        <div class="thumbnails-strip">
          <button class="thumb-btn" on:click={() => selectedHeroImg = listing.imageUrl}>
            <img src={listing.imageUrl} alt="Main" class="thumb-img" />
          </button>
          {#each currentGallery.slice(0, 3) as thumb, i}
            <button class="thumb-btn" on:click={() => selectedHeroImg = thumb}>
              {#if i === 2}
                <div class="thumb-img more-overlay">
                  <img src={thumb} alt="Thumb {i + 1}" />
                  <span class="overlay-text">+{currentGallery.length}</span>
                </div>
              {:else}
                <img src={thumb} alt="Thumb {i + 1}" class="thumb-img" />
              {/if}
            </button>
          {/each}
        </div>
      </div>

      <!-- Right Column: Property Information -->
      <div class="info-column">
        <h1 class="property-title">{listing.title}</h1>

        <div class="rating-loc-row">
          <span class="rating-tag">⭐ {Number(listing.rating || 0).toFixed(1)} ({listing.reviewCount || 0} reviews)</span>
          <span class="dot">•</span>
          <span class="location-tag">📍 {listing.location || 'Bishoftu, Oromia, Ethiopia'}</span>
        </div>

        <p class="property-desc">
          {listing.description || 'Stunning provider listing offering exceptional service, top-tier amenities, and professional host support across Ethiopia.'}
        </p>

        {#if listing.category === 'EVENT'}
          <div class="event-detail-panel">
            <div><span>Date</span><strong>{formatEventDate(listing.eventDate)}</strong></div>
            <div><span>Time</span><strong>{listing.eventStartTime || 'Time not listed'}{listing.eventEndTime ? ` – ${listing.eventEndTime}` : ''}</strong></div>
            <div><span>Category</span><strong>{listing.eventCategory || 'Event'}</strong></div>
            <button class="btn-gold calendar-btn" on:click={addEventToCalendar} disabled={!listing.eventDate}><Icon name="calendar" size={16} /> Add to Calendar</button>
          </div>
        {/if}

        <!-- Category-Specific Amenities Icons Bar -->
        <div class="amenities-row">
          {#if listing.category === 'HOTEL'}
            {#each (listing.amenities || ['Free WiFi', 'Swimming Pool', 'Restaurant', 'Spa & Wellness', 'Gym', 'Free Parking']).filter((amenity) => !/^no\b/i.test(amenity.trim())) as amenity}<span class="amenity-item">{amenity}</span>{/each}
          {:else if listing.category === 'CAR_RENTAL'}
            <span class="amenity-item">📍 GPS Navigation</span>
            <span class="amenity-item">🛋️ Leather Interior</span>
            <span class="amenity-item">❄️ Air Conditioning</span>
            <span class="amenity-item">🛡️ Full Insurance</span>
            <span class="amenity-item">🏔️ 4×4 Off-Road</span>
            <span class="amenity-item">♾️ Unlimited Mileage</span>
          {:else if listing.category === 'EVENT'}
            <span class="amenity-item">🎤 Main Stage View</span>
            <span class="amenity-item">🍲 Cultural Food Court</span>
            <span class="amenity-item">🍸 VIP Lounge</span>
            <span class="amenity-item">🎵 Live Performance</span>
            <span class="amenity-item">📸 Artist Meet & Greet</span>
            <span class="amenity-item">🅿️ Venue Parking</span>
          {:else}
            <span class="amenity-item">✋ 100% Handcrafted</span>
            <span class="amenity-item">🧵 Silk Embroidery</span>
            <span class="amenity-item">🏺 Organic Clay</span>
            <span class="amenity-item">🚚 Express Delivery</span>
            <span class="amenity-item">🏅 Quality Guarantee</span>
            <span class="amenity-item">🎁 Gift Packaging</span>
          {/if}
        </div>

        <!-- Property Info Horizontal Grid -->
        <div class="property-meta-grid">
          {#if listing.category === 'HOTEL'}
            <div class="meta-col">
              <span class="meta-lbl">Check-in</span>
              <strong class="meta-val">{listing.checkIn || '2:00 PM'}</strong>
            </div>
            <div class="meta-col">
              <span class="meta-lbl">Check-out</span>
              <strong class="meta-val">{listing.checkOut || '12:00 PM'}</strong>
            </div>
            <div class="meta-col">
              <span class="meta-lbl">Cancellation</span>
              <strong class="meta-val">{listing.cancellationPolicy || 'Free cancellation'}</strong>
            </div>
            <div class="meta-col">
              <span class="meta-lbl">Property Type</span>
              <strong class="meta-val">Resort / Hotel</strong>
            </div>
            <div class="meta-col">
              <span class="meta-lbl">Languages</span>
              <strong class="meta-val">English, Amharic</strong>
            </div>
          {:else if listing.category === 'CAR_RENTAL'}
            <div class="meta-col">
              <span class="meta-lbl">Pickup Time</span>
              <strong class="meta-val">8:00 AM</strong>
            </div>
            <div class="meta-col">
              <span class="meta-lbl">Return Time</span>
              <strong class="meta-val">6:00 PM</strong>
            </div>
            <div class="meta-col">
              <span class="meta-lbl">Cancellation</span>
              <strong class="meta-val">Free 24h Cancel</strong>
            </div>
            <div class="meta-col">
              <span class="meta-lbl">Vehicle Fleet</span>
              <strong class="meta-val">Verified Fleet</strong>
            </div>
            <div class="meta-col">
              <span class="meta-lbl">Driver Option</span>
              <strong class="meta-val">+500 ETB/day</strong>
            </div>
          {:else if listing.category === 'EVENT'}
            <div class="meta-col">
              <span class="meta-lbl">Gates Open</span>
              <strong class="meta-val">4:00 PM</strong>
            </div>
            <div class="meta-col">
              <span class="meta-lbl">Show Starts</span>
              <strong class="meta-val">6:30 PM</strong>
            </div>
            <div class="meta-col">
              <span class="meta-lbl">Entry Policy</span>
              <strong class="meta-val">QR Pass Entry</strong>
            </div>
            <div class="meta-col">
              <span class="meta-lbl">Category</span>
              <strong class="meta-val">Festival / Music</strong>
            </div>
            <div class="meta-col">
              <span class="meta-lbl">Age Policy</span>
              <strong class="meta-val">All Ages</strong>
            </div>
          {:else}
            <div class="meta-col">
              <span class="meta-lbl">Shipping</span>
              <strong class="meta-val">24-48 Hours</strong>
            </div>
            <div class="meta-col">
              <span class="meta-lbl">Return Policy</span>
              <strong class="meta-val">7 Days Guarantee</strong>
            </div>
            <div class="meta-col">
              <span class="meta-lbl">Seller Status</span>
              <strong class="meta-val">Verified Artisan</strong>
            </div>
            <div class="meta-col">
              <span class="meta-lbl">Origin</span>
              <strong class="meta-val">Addis Ababa</strong>
            </div>
            <div class="meta-col">
              <span class="meta-lbl">Material</span>
              <strong class="meta-val">100% Authentic</strong>
            </div>
          {/if}
        </div>
      </div>

    </div>

    <!-- Section Header -->
    <div class="rooms-section-header">
      <div>
        <h2>
          {#if listing.category === 'HOTEL'}
            {labels.rooms}
          {:else if listing.category === 'CAR_RENTAL'}
            Fleet Vehicles & Driver Options
          {:else if listing.category === 'EVENT'}
            Ticket Passes & VIP Tiers
          {:else}
            Craft Products & Handcrafted Items
          {/if}
        </h2>
        <p class="section-sub">
          {#if listing.category === 'HOTEL'}
            {labels.roomsDesc}
          {:else if listing.category === 'CAR_RENTAL'}
            Choose from a variety of premium and 4×4 off-road vehicles.
          {:else if listing.category === 'EVENT'}
            Choose your festival entrance tier and VIP lounge passes.
          {:else}
            Select your handcrafted artisan product item or variant.
          {/if}
        </p>
      </div>
      <button class="btn-outline map-btn" on:click={openMaps}>🗺️ {labels.location}</button>
    </div>

    <!-- Horizontal Cards Grid for Rooms & Options -->
    <div class="rooms-cards-grid">
      {#each categoryVariants as v, idx}
        <div class="room-unit-card marketplace-card {selectedVariantIndex === idx ? 'selected' : ''}">
          <img src={v.img} alt={v.title} class="room-card-img" />

          <div class="room-card-body">
            <h3 class="room-title">{v.title}</h3>

            <!-- Spec Chips Row -->
            <div class="room-specs-row">
              <span>👥 {v.specs.guests}</span>
              <span>🛏️ {v.specs.beds}</span>
              <span>📐 {v.specs.size}</span>
            </div>

            <p class="room-desc">{v.desc}</p>

            <!-- Stock Counter Pill (Visually Obvious) -->
            <div class="stock-pill-wrapper">
              <span class="stock-pill {v.availableCount <= 0 ? 'depleted' : ''}">
                🟢 {labels.available}: {v.availableCount} {labels.units}
              </span>
            </div>

            <div class="room-card-footer">
              <div class="room-price-box">
                <span class="price-num">{formatPrice(v.pricePerUnit)}</span>
                <span class="price-label">{v.unitLabel}</span>
              </div>

              <button class="btn-gold view-details-btn" on:click={() => handleProceedToBooking(idx)}>
                {labels.book}
              </button>
            </div>
          </div>
        </div>
      {/each}
    </div>

    <!-- Car Rental Driver Calculator Toggle (if Car Rental category) -->
    <!-- Stay / rental duration controls -->
    {#if listing.category === 'HOTEL'}
      <div class="driver-option-banner marketplace-card">
        <label class="driver-toggle-label">
          <span>🌙 {labels.nights}</span>
          <input id="stayNightsInput" type="number" min="1" max="30" bind:value={stayNights} class="input-field" style="max-width:120px" />
        </label>
      </div>
    {/if}

    {#if listing.category === 'CAR_RENTAL'}
      <div class="driver-option-banner marketplace-card">
        <label class="driver-toggle-label">
          <input type="checkbox" bind:checked={includeDriver} />
          <span>👨‍✈️ <strong>Include Professional Driver / Chauffeur</strong> (+500 ETB / day)</span>
        </label>
        <div class="driver-toggle-label" style="margin-top:12px; gap:16px;">
          <label for="rentalDurationType">Duration</label>
          <select id="rentalDurationType" bind:value={rentalDurationType} class="input-field" style="max-width:140px">
            <option value="DAYS">Days</option>
            <option value="HOURS">Hours</option>
          </select>
          <input id="rentalDurationCount" type="number" min="1" max="30" bind:value={rentalDurationCount} class="input-field" style="max-width:100px" />
        </div>
      </div>
    {/if}

  </div>
{/if}

<style>
  .property-detail-page {
    width: 100%;
    max-width: 1280px;
    margin: 0 auto;
    padding: 24px 16px 60px 16px;
    display: flex;
    flex-direction: column;
    gap: 28px;
    background: var(--bg-surface);
  }

  .page-nav-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-bottom: 14px;
    border-bottom: 1px solid var(--border-subtle);
  }

  .back-btn {
    background: var(--bg-surface-secondary);
    border: 1px solid var(--border-subtle);
    padding: 8px 16px;
    border-radius: var(--radius-md);
    font-size: 0.88rem;
    font-weight: 800;
    color: var(--text-main);
    cursor: pointer;
    transition: background 0.2s ease;
  }

  .back-btn:hover {
    background: var(--border-subtle);
  }

  .category-pill {
    background: var(--bg-surface-secondary);
    border: 1px solid var(--border-subtle);
    padding: 6px 14px;
    border-radius: 9999px;
    font-size: 0.8rem;
    font-weight: 800;
    color: var(--text-muted);
  }

  .top-right-actions {
    display: flex;
    align-items: center;
    gap: 14px;
  }

  .action-btn {
    min-height: 34px;
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 7px 10px;
    background: transparent;
    border: 1px solid var(--border-subtle);
    border-radius: var(--radius-sm);
    font-size: 0.88rem;
    font-weight: 800;
    color: var(--text-muted);
    cursor: pointer;
  }

  .action-btn:hover,
  .action-btn.saved {
    border-color: var(--accent-gold);
    color: var(--accent-gold-hover);
  }

  .property-showcase-grid {
    display: grid;
    grid-template-columns: 1.1fr 0.9fr;
    gap: 36px;
  }

  .gallery-column {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .hero-image-wrapper {
    width: 100%;
    height: 380px;
    border-radius: var(--radius-xl);
    overflow: hidden;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  }

  .hero-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .thumbnails-strip {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 10px;
    height: 85px;
  }

  .thumb-btn {
    border: none;
    padding: 0;
    background: transparent;
    cursor: pointer;
    height: 85px;
  }

  .thumb-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: var(--radius-md);
  }

  .more-overlay {
    position: relative;
    overflow: hidden;
  }

  .more-overlay img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .overlay-text {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.6);
    color: #ffffff;
    font-weight: 900;
    font-size: 1.25rem;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .info-column {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .property-title {
    font-size: 2.2rem;
    font-weight: 900;
    color: var(--text-main);
    line-height: 1.15;
  }

  .rating-loc-row {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 0.92rem;
    color: var(--text-muted);
  }

  .rating-tag {
    font-weight: 800;
    color: var(--text-main);
  }

  .property-desc {
    font-size: 0.95rem;
    color: var(--text-muted);
    line-height: 1.6;
  }

  .event-detail-panel {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 10px;
    margin: 18px 0;
    padding: 14px;
    border: 1px solid var(--border-subtle);
    border-radius: var(--radius-md);
    background: var(--bg-surface-secondary);
  }

  .event-detail-panel div { display: grid; gap: 4px; }
  .event-detail-panel span { color: var(--text-muted); font-size: 0.7rem; font-weight: 800; text-transform: uppercase; }
  .event-detail-panel strong { color: var(--text-main); font-size: 0.82rem; }
  .calendar-btn { grid-column: 1 / -1; justify-self: start; }

  .amenities-row {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
    padding: 14px 0;
    border-top: 1px solid var(--border-subtle);
    border-bottom: 1px solid var(--border-subtle);
  }

  .amenity-item {
    font-size: 0.88rem;
    font-weight: 800;
    color: var(--text-main);
  }

  .property-meta-grid {
    display: grid;
    grid-template-columns: repeat(5, 1fr);
    gap: 14px;
    padding-top: 10px;
  }

  .meta-col {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  .meta-lbl {
    font-size: 0.78rem;
    color: var(--text-muted);
  }

  .meta-val {
    font-size: 0.9rem;
    font-weight: 800;
    color: var(--text-main);
  }

  .rooms-section-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    margin-top: 20px;
    padding-top: 24px;
    border-top: 1px solid var(--border-subtle);
  }

  .rooms-section-header h2 {
    font-size: 1.6rem;
    font-weight: 900;
  }

  .section-sub {
    font-size: 0.9rem;
    color: var(--text-muted);
  }

  .map-btn {
    padding: 8px 18px;
    font-size: 0.88rem;
    font-weight: 800;
  }

  .rooms-cards-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
  }

  .room-unit-card {
    display: flex;
    flex-direction: column;
    border: 1px solid var(--border-subtle);
    border-radius: var(--radius-xl);
    overflow: hidden;
    background: var(--bg-surface);
    transition: transform 0.2s ease, box-shadow 0.2s ease;
  }

  .room-unit-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 30px rgba(0, 0, 0, 0.1);
  }

  .room-unit-card.selected {
    border: 2px solid var(--accent-gold);
  }

  .room-card-img {
    width: 100%;
    height: 180px;
    object-fit: cover;
  }

  .room-card-body {
    padding: 18px;
    display: flex;
    flex-direction: column;
    gap: 10px;
    flex-grow: 1;
  }

  .room-title {
    font-size: 1.15rem;
    font-weight: 900;
    color: var(--text-main);
  }

  .room-specs-row {
    display: flex;
    gap: 10px;
    font-size: 0.8rem;
    color: var(--text-muted);
    font-weight: 700;
  }

  .room-desc {
    font-size: 0.85rem;
    color: var(--text-muted);
    line-height: 1.45;
    height: 42px;
    overflow: hidden;
  }

  .stock-pill-wrapper {
    margin-top: 4px;
  }

  .stock-pill {
    display: inline-block;
    background: rgba(16, 185, 129, 0.12);
    color: #10b981;
    font-size: 0.78rem;
    font-weight: 800;
    padding: 4px 12px;
    border-radius: 9999px;
  }

  .stock-pill.depleted {
    background: rgba(239, 68, 68, 0.12);
    color: #ef4444;
  }

  .room-card-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: auto;
    padding-top: 12px;
    border-top: 1px solid var(--border-subtle);
  }

  .room-price-box {
    display: flex;
    flex-direction: column;
  }

  .price-num {
    font-size: 1.25rem;
    font-weight: 900;
    color: var(--text-main);
  }

  .price-label {
    font-size: 0.75rem;
    color: var(--text-muted);
  }

  .view-details-btn {
    padding: 8px 16px;
    font-size: 0.82rem;
    font-weight: 800;
  }

  .driver-option-banner {
    background: var(--bg-surface-secondary);
    padding: 16px 22px;
    border-radius: var(--radius-lg);
    margin-top: 10px;
  }

  .driver-toggle-label {
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 0.95rem;
    cursor: pointer;
  }

  @media (max-width: 1024px) {
    .property-showcase-grid {
      grid-template-columns: 1fr;
    }
    .rooms-cards-grid {
      grid-template-columns: repeat(2, 1fr);
    }
  }

  @media (max-width: 640px) {
    .rooms-cards-grid {
      grid-template-columns: 1fr;
    }
    .event-detail-panel { grid-template-columns: 1fr; }
    .calendar-btn { grid-column: auto; }
  }
</style>
