---
name: drawn-annotation
description: DrawnAnnotation - hand-drawn marks, labels, connectors, placement, click timing, styling, troubleshooting
---

# `DrawnAnnotation`

One generic, hand-drawn annotation, auto-registered for every deck using the theme.
It marks something on the slide — an element, or an exact piece of text inside a
Shiki or Magic Move code block — and optionally connects that mark to another element
or to a text label.

````md
<DrawnAnnotation type="circle" text="fun main" label="every program starts here" :at="1">

```kotlin
fun main() {
    println("My first Kotlin program!")
}
```

</DrawnAnnotation>
````

Everything is measured in the slide's own coordinate system, so annotations keep
their proportions in the presenter view, in exports, and at any window size. Nothing
is drawn on a guess: an annotation that cannot find its target stays off the slide
and says why on the console.

## What to mark

| prop | default | meaning |
|------|---------|---------|
| `type` | `underline` | short form of `source-type` |
| `source-type` | `type` | `underline`, `circle`, `box`, `strike-through`, or `none` |
| `selector` | `[data-annotate]` | element inside the slot to mark |
| `text` | – | exact text inside the slot to mark instead of an element; `\n` continues the match on the next source line |
| `occurrence` | `1` | which occurrence of `text`, 1-based |
| `line` | – | one-based source line of a code block to search on; occurrences counted on that line only |
| `multiline` | – | mark every visual line of the match; on by default for `underline` and `strike-through` |
| `padding` | `4` | space between the marked box and the stroke, in slide pixels |

`text` is the way to reach a token inside a code block. It works in plain Shiki
blocks and in `magic-move` blocks alike, and the mark follows the token while Magic
Move animates it; when the text is not part of the current step, the annotation stays
hidden. `line` and `occurrence` count from the code block's **first line of real
code**, after folded imports.

## What to point at

| prop | default | meaning |
|------|---------|---------|
| `label` | – | Markdown text next to the mark |
| `target` | – | selector of another element to connect the mark to |
| `target-x`, `target-y` | `50`, `50` | point inside the target, in % of its box |
| `target-radius` | `3` | size of the mark there, in % of the target's width |
| `target-type` | `circle` | `underline`, `circle`, `box`, `strike-through`, or `none` |
| `target-mark` | `true` | show the default target circle when `target-type` is omitted |
| `connect` | `true` | draw the leader line; unset, the slide's `drawnAnnotation.connect` decides, then `themeConfig.drawnAnnotation.connect` |
| `arrow` | `false` | arrow head at the end of the leader line |
| `curve` | `0.12` | curvature of the leader line, as a fraction of its length |

Turn leader lines off once for a deck that places labels by hand:

```yaml
---
theme: kotlin
themeConfig:
  drawnAnnotation:
    connect: false
---
```

```yaml
---
drawnAnnotation:
  connect: true   # this slide draws its leaders again
---
```

### Label Markdown

`label` accepts a small, safe Markdown subset (embedded HTML is escaped): backticked
inline `code`, a leading `>` for a block quote, `**strong**` / `__strong__`,
`*emphasis*` / `_emphasis_`, and `\n` for a line break. Inline code and quotes use
the local-first JetBrains Mono stack.

## Where the label goes

Automatic placement is deterministic: `auto` prefers below a mark in the upper half
of the slide and above one in the lower half, then falls back to the other sides; an
explicit `placement` is a contract and never drifts. Candidates stay clear of the
slide's laid-out content (including `v-click` content that is laid out but hidden),
of the annotation's own mark, of earlier labels, and of `avoid-selector` matches.

| prop | default | meaning |
|------|---------|---------|
| `placement` | `auto` | `up`, `down`, `left`, `right`, or automatic |
| `geometry.label` | – | label centre (`x`, `y`) and optional maximum `width`, normalized to the slide |
| `geometry.connector` | – | manual `start`/`end`, optionally a quadratic `control` |
| `gap` | `28` | distance between mark and automatic label, in slide pixels |
| `clearance` | `16` | space an automatic label keeps from obstacles |
| `avoid-selector` | – | extra elements an automatic label must not cover |

Geometry wins over automatic placement and is never moved by obstacles — see
[annotation-editor](annotation-editor.md).

## When it is drawn

No `at` and no `on` → part of the initial slide state, adding no click. `at` / `on`
create a reveal using Slidev's native `v-click` ordering.

| prop | default | meaning |
|------|---------|---------|
| `at` | – | click that draws the mark and leader line |
| `label-at` | `at` | click that writes the label |
| `until` | – | click that removes the annotation (exclusive) |
| `on` | – | `at` and `until` in one: `:on="1"` is `:at="1" :until="2"` |
| `sequential` | `true` | nested annotations sharing a click draw one after the other |
| `insert` | `false` | give the annotation its own click inside a Magic Move block |
| `passive` | `false` | observe an existing `v-click` step at `at` without registering a click |
| `wait` | `true` | hold the drawing back until the annotated element stopped moving |
| `track` | `true` | keep the annotation glued to elements that move |

`:on="0"` shows an annotation immediately and removes it on click 1. `on` next to an
explicit `at` / `until` wins and is reported as a mistake.

### Stages

A connector is drawn in three stages: the source mark finishes, then the leader line
reaches its destination, then the target mark. A same-click label waits for those
strokes; `label-at` gives the label a later click.

```html
<DrawnAnnotation
  source-type="circle"
  selector="[data-a]"
  target="[data-b]"
  target-type="none"
  arrow
  :at="1"
  label="B follows from A"
  :label-at="2"
>
  <div class="card">Element <b data-a>A</b> is annotated first</div>
  <div class="card">And the line is drawn to element <b data-b>B</b></div>
</DrawnAnnotation>
```

### Two marks on one click

Two annotations nested on the *same* click draw one after the other ("point at it,
then circle it" as a single reveal). Set `:sequential="false"` on the inner one to
draw both at once.

## Inside a Magic Move block

By default an annotation shares its click with the step that morphs into place.
`insert` gives it a step of its own: every step from `at` onwards is pushed one click
later, so the annotation gets a click where the code stands still.

```html
<DrawnAnnotation type="underline" text="val greeting" label="read-only" insert :on="2">
```

Annotations keep counting in the slide's own clicks (the number in the URL). An
annotation inside an `insert` block must use **plain numbers** — Slidev's `+1`
ordering is not available there.

Use `passive` when nearby `v-click` / `v-clicks` content already owns the `at` step.

`wait` (on by default) holds the drawing back until the annotated element stopped
moving; it waits on the actual finite animations in the slot rather than guessing
durations. It only delays the entrance. Set `:wait="false"` to draw immediately.

## How it looks

| prop | default | meaning |
|------|---------|---------|
| `geometry` | – | normalized `{ label, connector }`; the dev editor writes this binding |
| `options` | – | [rough.js options](https://github.com/rough-stuff/rough/wiki#options) |
| `iterations` | `2` | how many times each shape is drawn over itself |
| `color` | `--drawn-annotation-color` | stroke and label colour |
| `stroke-width` | `2` | stroke width, in slide pixels |
| `duration` | `500` | how long one stage takes, in ms |

Defaults match Rough Notation (2px stroke, roughness 1.5, drawn twice), so a mark
looks like Slidev's own `v-mark`. `:options="{ roughness: 2.6 }"` wobblier;
`:options="{ roughness: 0, bowing: 0 }"` with `:iterations="1"` gives clean geometric
lines. Randomness is seeded from the annotation's own props, so a mark keeps its
shape across a Magic Move transition; pass `seed` in `options` to pick one by hand.

| CSS variable | default | meaning |
|--------------|---------|---------|
| `--drawn-annotation-color` | the theme primary (`--slidev-theme-primary`) | stroke and label colour |
| `--drawn-annotation-label-font` | `--slidev-font-sans` | label font family |
| `--drawn-annotation-code-font` | local-first JetBrains Mono stack | inline code / quote font |
| `--drawn-annotation-label-size` | `28px` | label font size |
| `--drawn-annotation-label-weight` | `800` | label font weight |

When pointing the colour at another variable, give a hard fallback
(`var(--my-purple, #7954f6)`): a token that resolves invalid makes the strokes vanish.

## When nothing appears

Warnings are printed once on the console, prefixed `[DrawnAnnotation]`, after the
click has been reached and the slide has settled. While serving, a red chip also
appears in the slide's top-left corner (never in builds, exports, presenter, print).

- **Text … was not found in the slot** — `text` must match the rendered text exactly,
  spaces included; inside Magic Move it must be part of the current step.
- **Text … matches only N times** — lower `occurrence`, or make the text unique.
- **Selector … matched nothing inside the slot** — add `data-annotate` to the element,
  or use `text` for code.
- **Target … matched nothing on the slide** — the `target` selector found nothing.
- **… must be a plain click number** — `+1` ordering is unavailable under `insert`.
- **`on` is `at` and `until` in one** — drop the separate `at` / `until`.
