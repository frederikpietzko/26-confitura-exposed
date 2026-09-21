---
name: magic-move-between
description: Morph code windows across slide boundaries with the `magic-move` slide separator
---

# Magic Move Between Slides

Slidev's classic Magic Move animates code inside one slide, on clicks. This theme
additionally morphs code **across slides**: put `magic-move` in the separator between
two slides and their code windows animate from one to the next when you navigate.

````md
# My Slide

```kotlin
class Person
```

---
magic-move
---

# My Slide

```kotlin
class Person(val name: String)
```
````

Equivalent in YAML frontmatter, combinable with other keys:

```yaml
---
magicMove: true
---
```

## Rules

- The separator links the slide it **precedes** to the one before it, so the last
  slide of a chain needs no marker. Repeat it to chain any number of slides.
- Code windows pair up **by position**: first fence → first fence, second → second.
  Several parallel snippets (Kotlin next to SQL) each animate independently.
- Everything outside the fences is a normal slide — headings, prose, and layout can
  change freely and follow the deck's regular slide transition.
- Navigating backward plays the animation in reverse. Deep links, the overview, and
  PDF/PNG exports render each slide's own step statically.
- Fence metadata still works per chain: identity icons, `[Title]` labels,
  `{lines:true}`. Per-click line highlighting works too — a `{1|2-3}` range on the
  fence registers clicks on that slide and steps through the ranges before navigation
  moves on, exactly like one step of a classic Magic Move block.
- Snippet imports (`<<< @/…`) do **not** participate in chains.

## Transitions

Use `transition: fade`. The departing slide's copy of the window is hidden for the
length of the transition, so only the arriving window is visible while its tokens
morph and its box resizes.

**Avoid `transition: view-transition`**: Slidev 52 switches its slideshow container
between a `div` and a `TransitionGroup` whenever a click is followed by a slide
change, remounting every preloaded slide and stalling the frame for 150 ms or more on
each such navigation. (If you do use it, the code windows of both slides are paired
through the View Transitions API instead.)

## Annotating a chain

`DrawnAnnotation` and the code decorations follow Magic Move steps and stay hidden
when their target is not part of the current step — see
[drawn-annotation](drawn-annotation.md) and [code-decorations](code-decorations.md).
