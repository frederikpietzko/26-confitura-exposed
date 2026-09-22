<!--
  Reusable "artifact -> what it gives you" list. Used for the Exposed module
  overview, but nothing in here is Exposed specific: fill it with <ModuleRow>
  children.

  The rows are laid out by *this* grid (the children use `display: contents`),
  so the arrow column is one column for the whole list and every arrow lines
  up vertically no matter how long the artifact names are.
-->
<template>
  <div class="module-list">
    <slot />
  </div>
</template>

<style scoped>
.module-list {
  display: grid;
  grid-template-columns: max-content max-content 1fr;
  align-items: center;
  /* No column gap on purpose: the rows draw a hairline across all three
     cells, which a gap would cut into segments. The spacing between the
     columns is padding inside <ModuleRow>'s cells. */
  gap: 0;
  margin-top: 2.2rem;
  font-size: var(--module-list-font-size, 1.5rem);
}
</style>
