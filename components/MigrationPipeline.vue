<!--
  How `generateMigrations` actually works: the table objects on one side, a
  throwaway database on the other, and the diff between them written out as a
  migration file. Used instead of a bullet list - the middle column is the only
  thing the audience has to choose, so it is the accented one.
-->
<template>
  <div class="migration-pipeline">
    <section class="stage">
      <header class="stage-head">
        <img src="/assets/kotlin.svg" alt="" />
        <span>Table objects</span>
      </header>
      <div class="stage-body">
        <div class="chip">DriverTable</div>
        <div class="chip">TaxiTable</div>
        <div class="chip">TaxiRideTable</div>
      </div>
    </section>

    <div class="arrow">
      <span class="arrow-label">compared to</span>
      <span class="arrow-shaft">
        <span class="arrow-line" />
        <span class="arrow-head" />
      </span>
    </div>

    <section class="stage stage-accent">
      <header class="stage-head">
        <img src="/assets/postgresql.svg" alt="" />
        <span>Reference database</span>
      </header>
      <div class="stage-body">
        <div class="chip">H2, in memory</div>
        <div class="chip">Testcontainer</div>
        <div class="chip">Any JDBC url</div>
      </div>
      <div class="chip-group">
        <span class="chip-group-label">before the diff</span>
        <div class="chip">Flyway replays <code>db/migration</code></div>
      </div>
    </section>

    <div class="arrow">
      <span class="arrow-label">difference</span>
      <span class="arrow-shaft">
        <span class="arrow-line" />
        <span class="arrow-head" />
      </span>
    </div>

    <section class="stage">
      <header class="stage-head">
        <img src="/assets/exposed.svg" alt="" />
        <span>New script</span>
      </header>
      <div class="stage-body">
        <div class="chip">V…__CREATE_TABLE.sql</div>
        <div class="chip chip-muted">reviewed by me</div>
        <div class="chip chip-muted">committed by me</div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.migration-pipeline {
  --pipeline-gradient: linear-gradient(120deg, var(--fundamentals-purple), var(--fundamentals-pink));
  /* Opaque, like the bridge diagram: the tint is painted over the gradient
     frame, so a translucent colour would let the frame flood the panel. */
  --pipeline-surface: #ffffff;
  display: grid;
  grid-template-columns: 1fr auto 1.15fr auto 1fr;
  align-items: center;
  gap: 1.1rem;
  margin-top: 3rem;
}

html.dark .migration-pipeline {
  --pipeline-surface: #1f2023;
}

/* Gradient frame as a background, not a border - `border-image` would lose
   the rounded corners. */
.stage {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
  padding: 1.4rem 1.3rem 1.6rem;
  border-radius: 1.2rem;
  background:
    linear-gradient(180deg,
      color-mix(in srgb, var(--fundamentals-purple) 7%, var(--pipeline-surface)),
      color-mix(in srgb, var(--fundamentals-pink) 5%, var(--pipeline-surface))) padding-box,
    var(--pipeline-gradient) border-box;
  border: 1.5px solid transparent;
}

.stage-accent {
  background:
    linear-gradient(180deg,
      color-mix(in srgb, var(--fundamentals-purple) 16%, var(--pipeline-surface)),
      color-mix(in srgb, var(--fundamentals-pink) 12%, var(--pipeline-surface))) padding-box,
    var(--pipeline-gradient) border-box;
  border-width: 3px;
  box-shadow: 0 0.8rem 2.5rem -1rem color-mix(in srgb, var(--fundamentals-purple) 45%, transparent);
}

.stage-head {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  font-size: 1.45rem;
  font-weight: 800;
  line-height: 1.15;
}

.stage-head img {
  width: 2.2rem;
  height: 2.2rem;
  flex: none;
  object-fit: contain;
}

.stage-head span {
  background: var(--pipeline-gradient);
  background-clip: text;
  -webkit-background-clip: text;
  color: transparent;
}

.stage-body {
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
  flex: 1;
}

.chip {
  padding: 0.75rem 0.8rem;
  border-radius: 0.7rem;
  font-size: 1.15rem;
  font-weight: 600;
  text-align: center;
  white-space: nowrap;
  color: var(--fundamentals-ink);
  background: color-mix(in srgb, var(--fundamentals-ink) 6%, transparent);
  border: 1px solid color-mix(in srgb, var(--fundamentals-purple) 30%, transparent);
}

.chip code {
  font-size: 0.95em;
}

.chip-muted {
  border-style: dashed;
  opacity: 0.6;
}

.chip-group {
  padding: 0.7rem 0.7rem 0.8rem;
  border-radius: 0.85rem;
  border: 1px dashed color-mix(in srgb, var(--fundamentals-pink) 45%, transparent);
}

.chip-group-label {
  display: block;
  margin-bottom: 0.45rem;
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  opacity: 0.6;
}

.arrow {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.35rem;
  width: 6.5rem;
}

.arrow-label {
  font-size: 0.85rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  opacity: 0.65;
  text-align: center;
}

/* Shaft and head on one row so the triangle always sits on the line, whatever
   the column width ends up being. */
.arrow-shaft {
  display: flex;
  align-items: center;
  width: 100%;
}

.arrow-line {
  flex: 1;
  height: 3px;
  border-radius: 999px;
  background: var(--pipeline-gradient);
}

/* A CSS triangle stays crisp at any zoom and picks up the colour the line
   fades into. */
.arrow-head {
  flex: none;
  width: 0;
  height: 0;
  border: 8px solid transparent;
  border-left: 13px solid var(--fundamentals-pink);
  border-right: 0;
}
</style>
