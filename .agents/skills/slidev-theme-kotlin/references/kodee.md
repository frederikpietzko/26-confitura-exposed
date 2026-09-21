---
name: kodee
description: Kodee mascot configuration - variants, size, position, scale, per-slide overrides
---

# Kodee Mascot

## Enable once for the deck

```yaml
---
theme: kotlin
themeConfig:
  kodee: greeting
---
```

Kodee then uses **layout-aware defaults**: `small` in the bottom-right corner on
regular slides, `large` / `featured` on `cover` and `intro` slides.

## Per-slide overrides

```yaml
---
kodee: wink        # only the expression changes
---
```

```yaml
---
kodee: false       # hide the mascot on this slide
---
```

```yaml
---
kodee:             # full control
  variant: greeting
  size: large
  position: featured
---
```

Kodee morphs between slides (Magic Move), so changing only the variant animates the
expression rather than swapping the image.

## Options

### `variant`

`greeting`, `wink`, `wave`, `jumping`, `sitting`, `drinking`, `heart`, `in-love`,
`welcome`, `winter`, `tiny`

### `size`

| Value | Pixels |
|-------|--------|
| `small` | 200×200 (regular-layout default) |
| `medium` | 320×320 |
| `large` | 600×600 — 500×500 for the `wave` variant (cover/intro default) |

### `position`

| Value | Meaning |
|-------|---------|
| `corner` | Bottom right (regular-layout default) |
| `featured` | Prominently displayed (cover/intro default) |
| `custom` | Use with `x` and `y` |

### `scale`

Multiplies the size. `1.2` is a fifth larger. Defaults to `1`.

## Custom positioning

```yaml
---
kodee:
  variant: greeting
  position: custom
  x: 100
  y: 200
  scale: 1.2
---
```

## Notes

- The deck-wide Kodee lives in `global-bottom.vue` and follows the current slide;
  `slide-bottom.vue` renders a per-slide copy for the overview and presenter preview.
- `npm run lint` in the theme repo validates the deck's frontmatter and Kodee
  configuration; `npm run build` runs it first.
- On `cover` / `intro`, text wraps in `--cover-text-width` so it never runs under the
  mascot — see [layouts](layouts.md).
