<!--
  Three pillars - Kotlin, Exposed, the database - with the two directions of
  travel drawn underneath. Used on the "what is Exposed?" slide instead of a
  bullet list: the picture is the argument, the talk is the explanation.
-->
<template>
  <div class="exposed-bridge">
    <div class="bridge-pillars">
      <section class="pillar">
        <header class="pillar-head">
          <img src="/assets/kotlin.svg" alt="" />
          <span>Kotlin</span>
        </header>
        <div class="pillar-body">
          <div class="chip">Domain Types</div>
          <div class="chip">Domain Rules</div>
          <div class="chip">Application Code</div>
        </div>
      </section>

      <section class="pillar pillar-accent">
        <header class="pillar-head">
          <img src="/assets/exposed.svg" alt="" />
          <span>Exposed</span>
        </header>
        <div class="pillar-body pillar-grid">
          <div class="chip">JDBC</div>
          <div class="chip">R2DBC</div>
          <div class="chip">Query DSL</div>
          <div class="chip">DAO</div>
        </div>
        <div class="chip-group">
          <span class="chip-group-label">Integrations</span>
          <div class="pillar-grid">
            <div class="chip">Spring Boot</div>
            <div class="chip">Ktor</div>
          </div>
        </div>
      </section>

      <section class="pillar">
        <header class="pillar-head">
          <img src="/assets/postgresql.svg" alt="" />
          <span>Database</span>
        </header>
        <div class="pillar-body">
          <div class="chip">Schemas</div>
          <div class="chip">Tables</div>
          <div class="chip">Rows &amp; Keys</div>
          <div class="chip">SQL</div>
        </div>
      </section>
    </div>

    <div class="bridge-flows">
      <div class="flow">
        <span class="flow-line" />
        <span class="flow-label">types in</span>
        <span class="flow-line" />
        <span class="flow-head flow-head-right" />
      </div>
      <div class="flow flow-back">
        <span class="flow-head flow-head-left" />
        <span class="flow-line" />
        <span class="flow-label">rows out</span>
        <span class="flow-line" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.exposed-bridge {
  --bridge-gradient: linear-gradient(120deg, var(--fundamentals-purple), var(--fundamentals-pink));
  /* The panel tint is painted on top of the gradient frame, so it has to be
     opaque - a translucent tint would let the frame flood the whole panel. */
  --bridge-surface: #ffffff;
  display: flex;
  flex-direction: column;
  gap: 2.5rem;
  margin-top: 2.6rem;
}

html.dark .exposed-bridge {
  --bridge-surface: #1f2023;
}

.bridge-pillars {
  display: grid;
  grid-template-columns: 1fr 1.35fr 1fr;
  gap: 1.75rem;
  align-items: stretch;
}

/* The gradient frame is a background, not a border: a plain border cannot be
   a gradient, and `border-image` loses the rounded corners. */
.pillar {
  display: flex;
  flex-direction: column;
  gap: 0.9rem;
  padding: 1.5rem 1.5rem 1.7rem;
  border-radius: 1.2rem;
  background:
    linear-gradient(180deg,
      color-mix(in srgb, var(--fundamentals-purple) 7%, var(--bridge-surface)),
      color-mix(in srgb, var(--fundamentals-pink) 5%, var(--bridge-surface))) padding-box,
    var(--bridge-gradient) border-box;
  border: 1.5px solid transparent;
}

.pillar-accent {
  background:
    linear-gradient(180deg,
      color-mix(in srgb, var(--fundamentals-purple) 16%, var(--bridge-surface)),
      color-mix(in srgb, var(--fundamentals-pink) 12%, var(--bridge-surface))) padding-box,
    var(--bridge-gradient) border-box;
  border-width: 3px;
  box-shadow: 0 0.8rem 2.5rem -1rem color-mix(in srgb, var(--fundamentals-purple) 45%, transparent);
}

.pillar-head {
  display: flex;
  align-items: center;
  gap: 0.7rem;
  margin-bottom: 0.35rem;
  font-size: 1.6rem;
  font-weight: 800;
  line-height: 1;
}

.pillar-head img {
  width: 2.4rem;
  height: 2.4rem;
  object-fit: contain;
}

.pillar-head span {
  background: var(--bridge-gradient);
  background-clip: text;
  -webkit-background-clip: text;
  color: transparent;
}

.pillar-body {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
  flex: 1;
}

.pillar-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.8rem;
}

.chip {
  padding: 0.9rem 0.9rem;
  border-radius: 0.7rem;
  font-size: 1.2rem;
  font-weight: 600;
  text-align: center;
  white-space: nowrap;
  color: var(--fundamentals-ink);
  background: color-mix(in srgb, var(--fundamentals-ink) 6%, transparent);
  border: 1px solid color-mix(in srgb, var(--fundamentals-purple) 30%, transparent);
}

.chip-group {
  padding: 0.9rem 0.9rem 1rem;
  border-radius: 0.85rem;
  border: 1px dashed color-mix(in srgb, var(--fundamentals-pink) 45%, transparent);
}

.chip-group-label {
  display: block;
  margin-bottom: 0.55rem;
  font-size: 0.85rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  opacity: 0.6;
}

.bridge-flows {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.flow {
  display: flex;
  align-items: center;
  gap: 0.7rem;
}

.flow-line {
  flex: 1;
  height: 3px;
  border-radius: 999px;
  background: var(--bridge-gradient);
}

.flow-back .flow-line {
  background: linear-gradient(120deg, var(--fundamentals-pink), var(--fundamentals-purple));
}

.flow-label {
  font-size: 1rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  opacity: 0.65;
}

/* Arrowheads: a CSS triangle keeps them crisp at any zoom and picks up the
   same end of the gradient the adjacent line fades into. */
.flow-head {
  width: 0;
  height: 0;
  border: 9px solid transparent;
}

.flow-head-right {
  border-left: 14px solid var(--fundamentals-pink);
  border-right: 0;
}

.flow-head-left {
  border-right: 14px solid var(--fundamentals-pink);
  border-left: 0;
}
</style>
