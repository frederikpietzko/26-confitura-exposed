# AGENTS.md

Working notes for agents editing this Slidev deck ("Thinking in Tables — Replacing Hibernate
Magic with Exposed Logic in Spring Boot", Confitura 2026, `theme: kotlin`).

Read the `slidev` and `slidev-theme-kotlin` skills before touching slide syntax. This file only
records **how the author wants slides to look and read** — the preferences that are not in the docs.

## Deck layout

- `slides.md` holds only headmatter, cover, agenda and the `src:` includes. Content lives in
  `pages/NN-topic.md`, one file per agenda section, numbering kept continuous.
- Shared styling goes into `styles.css` with a comment explaining *why* the rule exists.
- Section openers use `layout: intro` + `class: section-intro`. Code slides use `class: code-slide`.

## Slide design principles

- **One idea per slide.** If a slide carries two thoughts, split it.
- **Small slides.** Titles plus a single code snippet or diagram — never a wall of text.
- **No bullet lists on slides.** Bullet points belong in speaker notes, not on screen.
  Exceptions: the agenda slide.
- Code snippets and diagrams are the argument; the spoken words are the explanation.
  If something can be *said*, don't put it on the slide.
- Prefer showing a real artifact (entity class, query log, diff, DSL call) over describing it.
- Titles must be **self-explanatory statements**, not labels. `Before ORMs: every query was
  plumbing` — not `1999`. A reader should know what the slide argues from the title alone.
- Never end a section with an "apology" or "fair and balanced" summary slide. End on the
  strongest concrete slide and put the handover into its notes.

## Speaker notes

- Every content slide has an HTML-comment note block.
- Notes are **`-` bullet lists with sub-bullets**, never prose paragraphs — they have to be
  skimmable on stage.
- Notes carry: the framing, the history/context, the punchline to say out loud, what *not* to
  explain twice, questions to ask the room, and the handover to the next slide/section.
- Write them in the author's first person voice ("say it out loud", "point at the return type",
  "ask the room").
- Use plain hyphens instead of em dashes and avoid special characters in notes.

## Code snippets

- Illustrative fragments are marked `no-compile`; only snippets that really should compile go
  through the theme's snippet pipeline (`snippets/`).
- Keep snippets short enough to read from the back row; strip imports, boilerplate and anything
  that isn't part of the point.
- Snippets may be deliberately *bad* code (the JPA section relies on it) — that's the point, keep it.
- Use `sql` for query logs so they get SQL highlighting, not `console`.
- Two stacked code windows need visible separation (see the `.slidev-code-wrapper ~` rule in
  `styles.css`); Slidev's own `!important` margin will otherwise win.

## Animation

- Use the theme's cross-slide Magic Move (`magicMove: true` in the following slide's frontmatter)
  when consecutive snippets are the *same example evolving*. Do not chain unrelated snippets —
  morphing them reads as noise.
- Magic Move pairs fences by position: keep the fence count equal across chained slides.
- Magic Move needs `transition: fade` in the headmatter; `view-transition` remounts preloaded
  slides in Slidev 52+ and stalls the frame.
- Reveal a punchline (e.g. the query log proving an N+1) behind `<v-click>` so the audience first
  sees the innocent-looking code. Animate reveals in (fade + rise), don't pop them.

## Verification

- For visual/CSS changes, actually run the deck (`npx slidev --port <port>`) and inspect the
  slide — computed styles, not assumptions. Slidev's own rules frequently override custom CSS.
- Clean up screenshots and dev-server artifacts afterwards.

## Tone

- The talk is explicitly **subjective** ("My subjective problems with JPA") and fair: credit JPA
  for what it solved, then show where it forces choices. Don't turn slides into a rant.
- Recurring themes worth reinforcing: what you see is what runs, the query is the interesting
  part, and "agents and juniors fail for the same reason — the semantics aren't in the code".
