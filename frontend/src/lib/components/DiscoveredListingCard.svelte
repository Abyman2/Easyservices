<script>
  import { createEventDispatcher } from 'svelte';
  import Icon from './Icon.svelte';

  export let listing;
  export let currentLanguage = 'en';

  $: labels = currentLanguage === 'am'
    ? { source: 'የህዝብ ምንጭ', discovered: 'የተገኘ ምንጭ', publicSource: 'የህዝብ ምንጭ', price: 'ዋጋ', notListed: 'ዋጋ በይፋ አልተዘረዘረም' }
    : { source: 'Public source', discovered: 'Discovered Source', publicSource: 'Public source', price: 'Price', notListed: 'Price not publicly listed' };

  const dispatch = createEventDispatcher();

  let currentImageIndex = 0;

  $: galleryImages = (() => {
    const images = Array.isArray(listing?.imageUrls)
      ? listing.imageUrls.filter(Boolean)
      : [];

    if (listing?.imageUrl && !images.includes(listing.imageUrl)) {
      images.unshift(listing.imageUrl);
    }

    return images;
  })();

  $: if (currentImageIndex >= galleryImages.length) {
    currentImageIndex = Math.max(0, galleryImages.length - 1);
  }

  function selectListing() {
    dispatch('select', listing);
  }

  function getPriceText() {
    if (
      listing?.priceSource === 'PUBLISHED_SOURCE' &&
      listing?.price !== null &&
      listing?.price !== undefined &&
      Number(listing.price) > 0
    ) {
      const currency = listing.currency || 'ETB';
      return `${currency} ${listing.price}`;
    }

    return labels.notListed;
  }

  function getCategoryLabel() {
    const labels = {
      HOTEL: 'Hotel',
      CAR: 'Car Rental',
      STORE: 'Store',
      EVENT: 'Event'
    };

    return labels[listing?.category] || listing?.category || 'Business';
  }

  function previousImage(event) {
    event.stopPropagation();

    if (galleryImages.length <= 1) return;

    currentImageIndex =
      currentImageIndex === 0
        ? galleryImages.length - 1
        : currentImageIndex - 1;
  }

  function nextImage(event) {
    event.stopPropagation();

    if (galleryImages.length <= 1) return;

    currentImageIndex =
      currentImageIndex === galleryImages.length - 1
        ? 0
        : currentImageIndex + 1;
  }

  function selectImage(index) {
    currentImageIndex = index;
  }
</script>


<article class="discovered-card">

  <!-- =====================================================
       DISCOVERED IMAGE GALLERY
       ===================================================== -->

  <div class="discovered-image">

    {#if galleryImages.length > 0}

      <img
        src={galleryImages[currentImageIndex]}
        alt={`${listing?.name || 'Discovered business'} photo ${currentImageIndex + 1}`}
        loading="lazy"
      />

      <!-- Source badge -->
      <div class="source-badge">
        <Icon name="globe" size={12} />
        {labels.discovered}
      </div>


      {#if galleryImages.length > 1}

        <!-- Previous -->
        <button
          type="button"
          class="gallery-arrow gallery-prev"
          aria-label="Previous photo"
          on:click={previousImage}
        >
          &lt;
        </button>


        <!-- Next -->
        <button
          type="button"
          class="gallery-arrow gallery-next"
          aria-label="Next photo"
          on:click={nextImage}
        >
          &gt;
        </button>


        <!-- Photo counter -->
        <div class="gallery-counter">
          {currentImageIndex + 1} / {galleryImages.length}
        </div>

      {/if}


    {:else}

      <div class="image-placeholder">

        <Icon name="globe" size={34} />

        <span>{labels.publicSource}</span>

      </div>

      <div class="source-badge">
        <Icon name="globe" size={12} />
        {labels.discovered}
      </div>

    {/if}

  </div>


  <!-- =====================================================
       THUMBNAILS
       ===================================================== -->

  {#if galleryImages.length > 1}

    <div class="gallery-thumbnails" aria-label="Business photos">

      {#each galleryImages as image, index}

        <button
          type="button"
          class:active={index === currentImageIndex}
          class="gallery-thumbnail"
          aria-label={`View photo ${index + 1}`}
          aria-current={index === currentImageIndex ? 'true' : undefined}
          on:click={() => selectImage(index)}
        >
          <img
            src={image}
            alt=""
            loading="lazy"
          />
        </button>

      {/each}

    </div>

  {/if}


  <!-- =====================================================
       CONTENT
       ===================================================== -->

  <div class="discovered-content">

    <div class="category-label">
      {getCategoryLabel()}
    </div>


    <h3>
      {listing?.name || 'Unnamed business'}
    </h3>


    {#if listing?.city || listing?.area}

      <div class="location-row">

        <Icon name="mappin" size={14} />

        <span>

          {#if listing?.area}
            {listing.area}

            {#if listing?.city}
              ,
            {/if}
          {/if}

          {listing?.city || ''}

        </span>

      </div>

    {/if}


    {#if listing?.description}

      <p class="description">
        {listing.description}
      </p>

    {/if}


    <div class="discovered-details">

      <div class="detail-row">

        <span class="detail-label">
          {labels.price}
        </span>

        <span class="detail-value">
          {getPriceText()}
        </span>

      </div>


      {#if listing?.phone}

        <div class="detail-row">

          <span class="detail-label">
            Phone
          </span>

          <span class="detail-value">
            {listing.phone}
          </span>

        </div>

      {/if}

    </div>


    <!-- =================================================
         ACTIONS
         ================================================= -->

    <div class="card-actions">

      <button
        type="button"
        class="details-btn"
        on:click={selectListing}
      >
        <Icon name="info" size={15} />
        View Information
      </button>


      {#if listing?.website}

        <a
          class="website-btn"
          href={listing.website}
          target="_blank"
          rel="noopener noreferrer"
        >
          <Icon name="external-link" size={15} />
          Visit Website
        </a>

      {/if}

    </div>

  </div>

</article>


<style>

  /* =======================================================
     CARD
     ======================================================= */

  .discovered-card {
    position: relative;
    overflow: hidden;

    display: flex;
    flex-direction: column;

    background: var(--bg-surface, #ffffff);

    border: 1px solid var(--border-subtle, #e5e7eb);

    border-radius: var(--radius-lg, 12px);

    box-shadow: var(--card-shadow, 0 4px 16px -2px rgba(17, 24, 39, 0.06));

    transition:
      transform 0.2s ease,
      box-shadow 0.2s ease,
      border-color 0.2s ease;
  }


  .discovered-card:hover {
    transform: translateY(-3px);

    box-shadow: var(--card-shadow-hover, 0 16px 32px -4px rgba(17, 24, 39, 0.12));

    border-color:
      var(--accent-gold);
  }


  /* =======================================================
     MAIN IMAGE
     ======================================================= */

  .discovered-image {
    position: relative;

    height: 210px;

    overflow: hidden;

    background:
      var(--bg-surface-secondary, #f3f4f6);
  }


  .discovered-image img {
    width: 100%;
    height: 100%;

    object-fit: cover;

    display: block;

    transition:
      opacity 0.2s ease;
  }


  /* =======================================================
     PLACEHOLDER
     ======================================================= */

  .image-placeholder {
    width: 100%;
    height: 100%;

    display: flex;
    flex-direction: column;

    align-items: center;
    justify-content: center;

    gap: 8px;

    color:
      var(--text-muted, #6b7280);

    background:
      linear-gradient(
        135deg,
        var(--bg-surface-secondary, #f3f4f6),
        var(--bg-surface, #ffffff)
      );

    font-size: 0.78rem;
    font-weight: 700;
  }


  /* =======================================================
     SOURCE BADGE
     ======================================================= */

  .source-badge {
    position: absolute;

    top: 14px;
    left: 14px;

    display: inline-flex;
    align-items: center;

    gap: 6px;

    padding: 7px 11px;

    border-radius: 999px;

    background: rgba(7, 29, 59, 0.92);

    color: #ffffff;

    border: 1px solid var(--accent-gold);

    font-size: 0.72rem;

    font-weight: 800;

    letter-spacing: 0.01em;

    box-shadow:
      0 4px 12px rgba(15, 23, 42, 0.22),
      inset 0 0 0 1px rgba(255, 255, 255, 0.06);

    backdrop-filter: blur(10px);

    z-index: 3;
  }


  .source-badge :global(svg) {
    color: var(--accent-gold);

    flex-shrink: 0;
  }


  /* =======================================================
     GALLERY ARROWS
     ======================================================= */

  .gallery-arrow {
    position: absolute;

    top: 50%;
    transform: translateY(-50%);

    width: 38px;
    height: 38px;

    display: flex;
    align-items: center;
    justify-content: center;

    border: 1px solid rgba(255, 255, 255, 0.55);
    border-radius: 50%;

    background: rgba(7, 29, 59, 0.78);
    color: #ffffff;

    cursor: pointer;

    z-index: 4;

    backdrop-filter: blur(8px);

    box-shadow:
      0 4px 12px rgba(0, 0, 0, 0.18);

    font-size: 30px;
    line-height: 1;
    font-weight: 300;

    padding: 0 0 3px 0;

    transition:
      background 0.2s ease,
      transform 0.2s ease;
  }


  .gallery-arrow:hover {
    background:
      rgba(7, 29, 59, 0.95);

    transform:
      translateY(-50%) scale(1.06);
  }


  .gallery-prev {
    left: 12px;
  }


  .gallery-next {
    right: 12px;
  }


  /* =======================================================
     PHOTO COUNTER
     ======================================================= */

  .gallery-counter {
    position: absolute;

    right: 13px;
    bottom: 13px;

    padding: 6px 10px;

    border-radius: 999px;

    background:
      rgba(7, 29, 59, 0.82);

    color: #ffffff;

    border:
      1px solid rgba(255, 255, 255, 0.28);

    font-size: 0.72rem;

    font-weight: 800;

    backdrop-filter: blur(8px);

    z-index: 4;
  }


  /* =======================================================
     THUMBNAIL STRIP
     ======================================================= */

  .gallery-thumbnails {
    display: flex;

    gap: 7px;

    padding: 9px 10px;

    overflow-x: auto;

    background:
      var(--bg-surface, #ffffff);

    border-bottom:
      1px solid var(--border-subtle, #e5e7eb);

    scrollbar-width: thin;
  }


  .gallery-thumbnail {
    flex: 0 0 54px;

    width: 54px;
    height: 42px;

    padding: 0;

    overflow: hidden;

    border:
      2px solid transparent;

    border-radius: 8px;

    background:
      var(--bg-surface-secondary, #f3f4f6);

    cursor: pointer;

    opacity: 0.65;

    transition:
      opacity 0.2s ease,
      border-color 0.2s ease,
      transform 0.2s ease;
  }


  .gallery-thumbnail:hover {
    opacity: 0.9;

    transform:
      translateY(-1px);
  }


  .gallery-thumbnail.active {
    opacity: 1;

    border-color: var(--accent-gold);

    box-shadow:
      0 0 0 1px rgba(212, 175, 55, 0.18);
  }


  .gallery-thumbnail img {
    width: 100%;
    height: 100%;

    object-fit: cover;

    display: block;
  }


  /* =======================================================
     CONTENT
     ======================================================= */

  .discovered-content {
    display: flex;
    flex-direction: column;

    padding: 18px;

    gap: 8px;
  }


  .category-label {
    color:
      var(--text-muted, #6b7280);

    font-size: 0.7rem;

    font-weight: 800;

    text-transform: uppercase;

    letter-spacing: 0.08em;
  }


  h3 {
    margin: 0;

    color: var(--text-main, #111827);

    font-size: 1.15rem;

    line-height: 1.3;
  }


  .location-row {
    display: flex;

    align-items: center;

    gap: 6px;

    color:
      var(--text-muted, #6b7280);

    font-size: 0.84rem;
  }


  .description {
    margin: 5px 0 2px;

    color: var(--text-muted, #4b5563);

    font-size: 0.86rem;

    line-height: 1.5;

    display: -webkit-box;

    -webkit-line-clamp: 3;

    -webkit-box-orient: vertical;

    overflow: hidden;
  }


  /* =======================================================
     DETAILS
     ======================================================= */

  .discovered-details {
    margin-top: 6px;

    padding-top: 10px;

    border-top:
      1px solid var(--border-subtle, #e5e7eb);
  }


  .detail-row {
    display: flex;

    justify-content: space-between;

    gap: 12px;

    padding: 4px 0;
  }


  .detail-label {
    color:
      var(--text-muted, #6b7280);

    font-size: 0.78rem;

    font-weight: 700;
  }


  .detail-value {
    color: var(--text-main, #111827);

    font-size: 0.8rem;

    font-weight: 700;

    text-align: right;
  }


  /* =======================================================
     ACTIONS
     ======================================================= */

  .card-actions {
    display: flex;

    gap: 8px;

    margin-top: 8px;
  }


  .details-btn,
  .website-btn {
    min-height: 40px;

    display: inline-flex;

    align-items: center;
    justify-content: center;

    gap: 6px;

    padding: 9px 12px;

    border-radius: 10px;

    font-size: 0.78rem;

    font-weight: 800;

    cursor: pointer;

    text-decoration: none;
  }


  .details-btn {
    flex: 1;

    border: 1px solid var(--accent-gold);
    background: var(--accent-gold);
    color: #ffffff;
    box-shadow: 0 4px 12px rgba(200, 155, 60, 0.22);
  }


  .details-btn:hover {
    background: var(--accent-gold-hover);
    border-color: var(--accent-gold-hover);
    color: #ffffff;
  }


  .website-btn {
    border:
      1px solid var(--border-subtle, #d1d5db);

    background: var(--bg-surface-secondary, #f8fafc);
    color: var(--text-main, #111827);
  }


  .website-btn:hover {
    border-color: var(--accent-gold);
    color: var(--accent-gold-hover);
  }


  /* =======================================================
     MOBILE
     ======================================================= */

  @media (max-width: 640px) {

    .discovered-image {
      height: 190px;
    }


    .discovered-content {
      padding: 15px;
    }


    .gallery-arrow {
      width: 34px;
      height: 34px;
    }


    .gallery-thumbnail {
      flex-basis: 48px;

      width: 48px;
      height: 38px;
    }


    .card-actions {
      flex-direction: column;
    }


    .website-btn {
      width: 100%;
    }

  }

</style>