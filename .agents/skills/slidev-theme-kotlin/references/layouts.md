---
name: layouts
description: The three layouts shipped by slidev-theme-kotlin and the cover text column
---

# Layouts

The theme ships three layouts. Slidev's own built-in layouts remain available, but
Kodee's layout-aware defaults and the cover text column only apply to these.

| Layout | Purpose | Kodee default |
|--------|---------|---------------|
| `default` | Standard slide | `small` / `corner` |
| `cover` | Presentation title slide | `large` / `featured` |
| `intro` | Introduction slide | `large` / `featured` |

```yaml
---
layout: cover
---

# Presentation Title
```

`cover` centers its content vertically across the full width; `intro` centers it
vertically in the content column; `default` is a plain `.slidev-layout` slot.

## The cover text column

On `cover` and `intro` slides the text sits in a column beside the featured mascot
and wraps inside it instead of running under Kodee. The column is
`--cover-text-width`, **48%** of the content box by default, which clears the
mascot's arm.

Widen it from the deck's stylesheet when the titles are short, or give a slide that
hides the mascot the full width:

```css
.slidev-layout.cover.no-mascot {
  --cover-text-width: 100%;
}
```

## Custom layouts

A custom layout with different horizontal spacing should override
`--code-window-inline-space` so code-window font sizing stays correct — see
[code-windows](code-windows.md).
