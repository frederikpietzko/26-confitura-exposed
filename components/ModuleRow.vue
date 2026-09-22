<!--
  One row of a <ModuleList>: artifact name, arrow, what it gives you.

  `display: contents` hands the three cells to the parent grid, which is what
  keeps the arrows in a single vertical line across all rows.

  Props:
    name  - the artifact, rendered in the code font
    tag   - optional badge after the description ("optional", "required", ...)
    muted - dim the row (used for the "and a few more" catch-all entry)
-->
<script setup>
defineProps({
  name: { type: String, required: true },
  tag: { type: String, default: '' },
  muted: { type: Boolean, default: false },
})
</script>

<template>
  <div class="module-row" :class="{ 'is-muted': muted }">
    <span class="module-name">{{ name }}</span>
    <span class="module-arrow" aria-hidden="true">&rarr;</span>
    <span class="module-what">
      <slot />
      <span v-if="tag" class="module-tag">{{ tag }}</span>
    </span>
  </div>
</template>

<style scoped>
.module-row {
  display: contents;
}

.module-name,
.module-arrow,
.module-what {
  padding: 0.75rem 0;
  border-bottom: 1px solid color-mix(in srgb, var(--fundamentals-purple) 18%, transparent);
  line-height: 1.3;
}

.module-row:last-child .module-name,
.module-row:last-child .module-arrow,
.module-row:last-child .module-what {
  border-bottom: none;
}

/* The hairline has to run across the whole row, so the list must not put a
   grid gap between the columns - the horizontal spacing lives in the cells. */
.module-name {
  padding-right: 1.6rem;
  font-family: var(--slidev-code-font-family, ui-monospace, monospace);
  font-size: 0.95em;
  font-weight: 700;
  white-space: nowrap;
  background: linear-gradient(120deg, var(--fundamentals-purple), var(--fundamentals-pink));
  background-clip: text;
  -webkit-background-clip: text;
  color: transparent;
}

.module-arrow {
  padding-right: 1.1rem;
  font-weight: 700;
  opacity: 0.4;
}

.module-what {
  font-weight: 600;
  color: var(--fundamentals-ink);
}

.module-tag {
  margin-left: 0.7rem;
  padding: 0.15em 0.6em;
  border-radius: 999px;
  font-size: 0.6em;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  vertical-align: middle;
  color: var(--fundamentals-purple);
  border: 1px solid color-mix(in srgb, var(--fundamentals-purple) 45%, transparent);
  background: color-mix(in srgb, var(--fundamentals-purple) 8%, transparent);
}

/* The purple badge text is too dark against the dark slide background. */
html.dark .module-tag {
  color: color-mix(in srgb, var(--fundamentals-purple) 55%, white);
  border-color: color-mix(in srgb, var(--fundamentals-purple) 70%, transparent);
}

.is-muted .module-name,
.is-muted .module-what {
  opacity: 0.6;
}
</style>
