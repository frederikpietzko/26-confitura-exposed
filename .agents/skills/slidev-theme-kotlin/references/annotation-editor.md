---
name: annotation-editor
description: The development-only visual editor that writes DrawnAnnotation geometry back into the Markdown source
---

# Visual Annotation Editor (development only)

Opt in from the consuming deck's `vite.config.ts`:

```ts
import { defineConfig } from 'vite'
import { drawnAnnotationEditor } from 'slidev-theme-kotlin/annotation-editor'

export default defineConfig({
  plugins: [drawnAnnotationEditor()],
})
```

The locator, toolbar, and write endpoint exist **only while Vite serves the deck**;
builds and exports render saved geometry without any editor code. A deck without the
writer plugin reports a configuration error instead of opening an editor that cannot
save.

## Using it

Press **Alt+Shift+A** or click **Edit annotations** to start a *frozen* edit session
on the current slide and click state: Slidev navigation — arrow keys, space, page
keys, click-to-navigate, swipes — is suspended until you leave.

- Drag a visible label to move it; select it and drag its right handle to set its
  maximum width.
- Labels, the width handle, and connector endpoint/curve handles are keyboard
  focusable: focus one and nudge with the arrow keys (**Shift** for larger steps).
- Every change stays a **local draft** — the Markdown is never written mid-gesture,
  so there is no HMR remount and no replayed entrance animation while you work.
- **Done editing annotations** commits all changed drafts as normalized `:geometry`
  bindings on exactly the edited `DrawnAnnotation` tags, then exits.
- **Esc** cancels in stages: the active drag, then the selection, then (after a
  confirmation) the whole session, discarding its drafts. There is no undo.

## Toolbar controls

| Control | Effect |
|---------|--------|
| Hide / Show connector | writes `:connect` only where the wanted state differs from the slide's or deck's `drawnAnnotation.connect` default, and removes the binding when it matches |
| Make connector manual / Use automatic connector | materializes the current automatic route as draft geometry, or drops manual geometry so the route follows target and label again |
| Curve / Straighten connector | adds a quadratic control point on the chord midpoint, or removes it while endpoints stay put |
| Reset annotation | removes the selected tag's saved `:geometry` |
| Reset slide | removes saved geometry of every annotation on the slide (after a confirmation) |
| Reload saved geometry | discards drafts and adopts the saved source |

Removing saved geometry is the one exception to draft-only editing (a draft can only
overlay the source, not delete from it), so those controls write immediately.

## Conflicts

Source revisions are checked. A conflicting commit **fails without overwriting
anyone**: the session stays open, the drafts stay visible, and the status names the
failure and the recovery paths — fix and press Done again, or **Reload saved
geometry**.

## The geometry prop

`geometry` is the source-local public prop; its values are fractions of the concrete
`.slidev-layout`, so they survive presentation scaling and a nested annotation
canvas. No annotation ID or generated stylesheet is involved.

```html
<DrawnAnnotation text="String?" label="nullable return type" :on="2"
  :geometry="{ label: { x: 0.7125, y: 0.1864, width: 0.1944 } }">
```

You can also write it by hand. Geometry wins over automatic placement and is never
moved by obstacles.

## Problem chips

While serving, an annotation that cannot find its target — an unmatched `text`,
`selector`, or `target`, an `occurrence` past the last match, or an
`InlineCompilerError` / `SmartCast` / `Warning` / `TypeHint` / `InlineValue` whose
target is missing — puts a red chip in the slide's top-left corner next to the
console warning. Hover for the full message. The chip appears only once the
annotation's click has been reached and the slide has settled (so a Magic Move step
that legitimately lacks the text never flags one) and disappears when the target
turns up. Builds, exports, the presenter view, and print mode never render it.
