<script>
  import { createEventDispatcher } from 'svelte';
  import Icon from './Icon.svelte';

  export let listing;

  const dispatch = createEventDispatcher();
  let currentImageIndex = 0;

  $: galleryImages = (() => {
    const images = Array.isArray(listing?.imageUrls) ? listing.imageUrls.filter(Boolean) : [];
    if (listing?.imageUrl && !images.includes(listing.imageUrl)) images.unshift(listing.imageUrl);
    return images;
  })();
  $: if (currentImageIndex >= galleryImages.length) currentImageIndex = 0;

  function getGoogleMapsUrl() {
    const parts = [
      listing?.name,
      listing?.address,
      listing?.area,
      listing?.city,
      listing?.country
    ].filter(Boolean);

    if (parts.length === 0) {
      return null;
    }

    const query = encodeURIComponent(parts.join(', '));

    return `https://www.google.com/maps/search/?api=1&query=${query}`;
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

  function getPriceText() {
    if (
      listing?.priceSource === 'PUBLISHED_SOURCE' &&
      listing?.price !== null &&
      listing?.price !== undefined &&
      Number(listing.price) > 0
    ) {
      return `${listing.currency || 'ETB'} ${listing.price}`;
    }

    return 'Price not publicly listed';
  }

  function formatLastChecked(value) {
    if (!value) return 'Not available';

    try {
      return new Date(value).toLocaleString();
    } catch {
      return value;
    }
  }
</script>

{#if listing}
  <div class="modal-backdrop" role="presentation" on:click|self>
    <div
      class="discovery-modal"
      role="dialog"
      aria-modal="true"
      aria-label="Discovered business information"
    >

      <div class="detail-topbar">
        <button type="button" class="back-btn" on:click={() => dispatch('close')}>
          <Icon name="arrow-left" size={16} /> Back to discovery
        </button>
        <button type="button" class="close-btn" aria-label="Close" on:click={() => dispatch('close')}>×</button>
      </div>

      <!-- IMAGE -->
      <div class="modal-image">
        {#if galleryImages.length > 0}
          <img
            src={galleryImages[currentImageIndex]}
            alt={listing.name || 'Business image'}
          />
        {:else}
          <div class="image-placeholder">
            <Icon name="globe" size={46} />
            <span>No image supplied by source</span>
          </div>
        {/if}

        <span class="source-badge">
          <Icon name="globe" size={13} />
          Public Source
        </span>

      </div>

      {#if galleryImages.length > 1}
        <div class="detail-thumbnails" aria-label="Business image gallery">
          {#each galleryImages as image, index}
            <button type="button" class:active={index === currentImageIndex} on:click={() => currentImageIndex = index} aria-label={`View image ${index + 1}`}>
              <img src={image} alt="" />
            </button>
          {/each}
        </div>
      {/if}

      <!-- CONTENT -->
      <div class="modal-content">

        <div class="category">
          {getCategoryLabel()}
        </div>

        <h2>
          {listing.name || 'Business name unavailable'}
        </h2>

        {#if listing.city || listing.area}
          <div class="info-line">
              <Icon name="mappin" size={16} />

            <span>
              {#if listing.area}
                {listing.area}
                {#if listing.city}, {/if}
              {/if}
              {listing.city || ''}
            </span>
          </div>
        {/if}


        {#if listing.address}
          <div class="info-block">
            <span class="label">Address</span>

            {#if getGoogleMapsUrl()}
              <a
                href={getGoogleMapsUrl()}
                target="_blank"
                rel="noopener noreferrer"
                class="address-link"
              >
                <Icon name="mappin" size={16} />
                <span>{listing.address}</span>
              </a>
            {:else}
              <p>{listing.address}</p>
            {/if}
          </div>
        {/if}

        {#if listing.website}
          <div class="info-block source-link-block">
            <span class="label">Website</span>
            <a href={listing.website} target="_blank" rel="noopener noreferrer" class="source-link">
              <Icon name="globe" size={16} />
              <span>Visit the public source website</span>
              <Icon name="external-link" size={15} />
            </a>
          </div>
        {/if}


        {#if listing.description}
          <div class="info-block">
            <span class="label">Description</span>
            <p>{listing.description}</p>
          </div>
        {/if}


        <div class="details-grid">

          <div class="detail-box source-detail-box">
            <span class="label"><Icon name="globe" size={13} /> Public source</span>
            <strong>{listing.sourceName || listing.name || 'Public listing'}</strong>
          </div>

          <div class="detail-box">
            <span class="label">Price</span>
            <strong>{getPriceText()}</strong>
          </div>

          {#if listing.phone}
            <div class="detail-box">
              <span class="label">Phone</span>
              <strong>{listing.phone}</strong>
            </div>
          {/if}

          {#if listing.sourceName}
            <div class="detail-box">
              <span class="label">Source</span>
              <strong>{listing.sourceName}</strong>
            </div>
          {/if}

          <div class="detail-box">
            <span class="label">Last checked</span>
            <strong>{formatLastChecked(listing.lastChecked)}</strong>
          </div>

        </div>


        {#if listing.tags && listing.tags.length > 0}
          <div class="tags-section">
            <span class="label">Tags</span>

            <div class="tags">
              {#each listing.tags as tag}
                <span>{tag}</span>
              {/each}
            </div>
          </div>
        {/if}


        <div class="notice">
          <Icon name="info" size={17} />

          <div>
            <strong>Public source information</strong>
            <p>
              This business was discovered from a public source.
              It is not an EasyService provider and cannot be booked
              through EasyService.
            </p>
          </div>
        </div>


        <div class="actions">

          {#if listing.website}
            <a
              href={listing.website}
              target="_blank"
              rel="noopener noreferrer"
              class="primary-action"
            >
              <Icon name="external-link" size={17} />
              Open Source Website
            </a>
          {/if}

          {#if listing.phone}
            <a
              href={`tel:${listing.phone}`}
              class="secondary-action"
            >
              <Icon name="phone" size={17} />
              Call Business
            </a>
          {/if}

          {#if getGoogleMapsUrl()}
            <a
              href={getGoogleMapsUrl()}
              target="_blank"
              rel="noopener noreferrer"
              class="secondary-action location-action"
            >
              <Icon name="mappin" size={17} />
              View Location
            </a>
          {/if}

        </div>

      </div>

    </div>
  </div>
{/if}


<style>
  .modal-backdrop {
    position: fixed;
    inset: 0;
    z-index: 3000;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 24px;
    background: rgba(3, 10, 20, 0.78);
    backdrop-filter: blur(5px);
  }

  .discovery-modal {
    position: relative;
    width: min(720px, 100%);
    max-height: 90vh;
    overflow-y: auto;
    background: var(--bg-surface, #ffffff);
    border: 1px solid var(--border-subtle);
    border-radius: var(--radius-lg, 12px);
    box-shadow: 0 24px 70px rgba(3, 10, 20, 0.45);
  }

  .detail-topbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    min-height: 56px;
    padding: 10px 18px;
    border-bottom: 1px solid var(--border-subtle);
    background: var(--bg-surface);
  }

  .back-btn {
    display: inline-flex;
    align-items: center;
    gap: 7px;
    border: 0;
    padding: 8px 0;
    background: transparent;
    color: var(--text-main);
    font: inherit;
    font-size: 0.82rem;
    font-weight: 800;
    cursor: pointer;
  }

  .back-btn:hover { color: var(--accent-gold-hover); }

  .close-btn {
    position: absolute;
    z-index: 5;
    top: 9px;
    right: 14px;
    width: 38px;
    height: 38px;
    border: 0;
    border-radius: 50%;
    background: var(--bg-surface-secondary);
    color: var(--text-main);
    font-size: 25px;
    line-height: 1;
    cursor: pointer;
    box-shadow: 0 3px 12px rgba(15, 23, 42, 0.12);
  }

  .modal-image {
    position: relative;
    height: 280px;
    background: var(--bg-surface-secondary, #f3f4f6);
  }

  .modal-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
  }

  .detail-thumbnails {
    display: flex;
    gap: 8px;
    overflow-x: auto;
    padding: 10px 18px;
    border-bottom: 1px solid var(--border-subtle);
    background: var(--bg-surface);
  }

  .detail-thumbnails button {
    flex: 0 0 58px;
    width: 58px;
    height: 42px;
    padding: 0;
    overflow: hidden;
    border: 2px solid transparent;
    border-radius: var(--radius-sm);
    background: var(--bg-surface-secondary);
    opacity: 0.65;
    cursor: pointer;
  }

  .detail-thumbnails button.active { border-color: var(--accent-gold); opacity: 1; }
  .detail-thumbnails img { width: 100%; height: 100%; object-fit: cover; display: block; }

  .image-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 10px;
    color: var(--text-muted, #6b7280);
    font-weight: 700;
  }

  .source-badge {
    position: absolute;
    top: 16px;
    left: 16px;
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
  }

  .modal-content {
    padding: 25px;
  }

  .category {
    margin-bottom: 6px;
    color: var(--text-muted);
    font-size: 0.72rem;
    font-weight: 800;
    text-transform: uppercase;
    letter-spacing: 0.08em;
  }

  h2 {
    margin: 0 0 9px;
    color: var(--text-main, #111827);
    font-size: 1.65rem;
    line-height: 1.25;
  }

  .info-line {
    display: flex;
    align-items: center;
    gap: 7px;
    color: var(--text-muted, #6b7280);
    font-size: 0.9rem;
    margin-bottom: 20px;
  }

  .info-block {
    padding: 14px 0;
    border-top: 1px solid var(--border-subtle, #e5e7eb);
  }

  .label {
    display: block;
    margin-bottom: 5px;
    color: var(--text-muted, #6b7280);
    font-size: 0.73rem;
    font-weight: 800;
    text-transform: uppercase;
    letter-spacing: 0.06em;
  }

  .info-block p {
    margin: 0;
    color: var(--text-muted, #4b5563);
    font-size: 0.9rem;
    line-height: 1.55;
  }

  .details-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 10px;
    margin-top: 8px;
  }

  .detail-box {
    padding: 13px;
    border: 1px solid var(--border-subtle, #e5e7eb);
    border-radius: 12px;
    background: var(--bg-surface-secondary, #f8fafc);
  }

  .detail-box strong {
    color: var(--text-main, #111827);
    font-size: 0.86rem;
    word-break: break-word;
  }

  .tags-section {
    margin-top: 18px;
  }

  .tags {
    display: flex;
    flex-wrap: wrap;
    gap: 7px;
  }

  .tags span {
    padding: 6px 9px;
    border-radius: 999px;
    background: var(--bg-surface-secondary, #f3f4f6);
    color: var(--text-muted, #4b5563);
    font-size: 0.75rem;
    font-weight: 700;
  }

  .notice {
    display: flex;
    gap: 10px;
    margin-top: 20px;
    padding: 14px;
    border-radius: 12px;
    background: var(--bg-surface-secondary);
    border: 1px solid var(--border-subtle);
    color: var(--text-muted);
  }

  .notice strong {
    display: block;
    margin-bottom: 3px;
    font-size: 0.82rem;
  }

  .notice p {
    margin: 0;
    font-size: 0.78rem;
    line-height: 1.45;
  }

  .actions {
    display: flex;
    gap: 10px;
    margin-top: 20px;
  }

  .primary-action,
  .secondary-action {
    flex: 1;
    min-height: 44px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 7px;
    border-radius: 11px;
    text-decoration: none;
    font-size: 0.82rem;
    font-weight: 800;
  }

  .primary-action {
    background: var(--accent-gold);
    color: #ffffff;
    box-shadow: 0 4px 12px rgba(200, 155, 60, 0.22);
  }

  .primary-action:hover { background: var(--accent-gold-hover); }

  .secondary-action {
    border: 1px solid var(--border-subtle, #d1d5db);
    color: var(--text-main, #111827);
    background: var(--bg-surface);
  }

  .secondary-action:hover { border-color: var(--accent-gold); color: var(--accent-gold-hover); }

  .source-link {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    color: var(--text-main);
    font-size: 0.9rem;
    font-weight: 700;
    text-decoration: none;
  }

  .source-link:hover { color: var(--accent-gold-hover); }
  .source-detail-box .label { display: inline-flex; align-items: center; gap: 5px; }

  @media (max-width: 600px) {
    .modal-backdrop {
      padding: 10px;
      align-items: flex-end;
    }

    .discovery-modal {
      max-height: 94vh;
      border-radius: 20px 20px 0 0;
    }

    .modal-image {
      height: 220px;
    }

    .modal-content {
      padding: 20px;
    }

    .details-grid {
      grid-template-columns: 1fr;
    }

    .actions {
      flex-direction: column;
    }

    .address-link {
      display: flex;
      align-items: flex-start;
      gap: 8px;
      color: var(--text-main, #111827);
      text-decoration: none;
      font-size: 0.9rem;
      line-height: 1.5;
      font-weight: 700;
    }

    .address-link:hover {
      color: var(--accent-gold, #c9a227);
    }

    .location-action {
      border-color: rgba(212, 175, 55, 0.55);
    }
  }
</style>