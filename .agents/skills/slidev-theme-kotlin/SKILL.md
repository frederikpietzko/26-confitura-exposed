---
name: slidev-theme-kotlin
description: Author Slidev decks with the Kotlin theme (slidev-theme-kotlin) - Kodee mascot, IntelliJ-style code windows, folded imports, cross-slide magic move, hand-drawn annotations, inline compiler diagnostics, compilable Kotlin snippets, and the static handout/llms.txt build output. Use when writing or editing slides of a deck whose headmatter says `theme: kotlin`.
---

# Slidev Theme Kotlin

A [Slidev](https://sli.dev) theme for Kotlin talks: the Kodee mascot, Kotlin-tuned
Shiki highlighting, IntelliJ-looking code windows, and authoring tools that keep the
code on the slides real (compilable snippets, a crawlable handout).

Package: `slidev-theme-kotlin` · Repo: <https://github.com/nomisRev/slidev-theme-kotlin>
· License: Apache-2.0 · Node >= 20.12.0

## When to Use

- The deck's headmatter contains `theme: kotlin`
- Adding Kotlin/Java/SQL/Gradle code slides that should look like IntelliJ
- Animating code across slides, not just inside one slide
- Annotating code by hand-drawn mark, smart-cast highlight, warning, inlay hint,
  inline debugger value, or compiler error
- Placing or configuring the Kodee mascot
- Making the deck crawlable (handout, `llms.txt`) or countable (GoatCounter)
- Compiling the deck's snippets in a Gradle module, or quoting a local Gradle
  project's Kotlin files on the slides

This skill covers the **theme**. For base Slidev syntax (`v-click`, transitions,
export, headmatter), use the `slidev` skill.

## Quick Start

```yaml
---
theme: kotlin
title: My Kotlin Talk
transition: fade
themeConfig:
  kodee: greeting
---

# My Kotlin Talk
```

```bash
pnpm add slidev-theme-kotlin   # npm install also fine; pnpm needs shamefully-hoist=true
pnpm run dev
```

## Cheat Sheet

| Want | Write |
|------|-------|
| Kodee on every slide | `themeConfig: { kodee: greeting }` in headmatter |
| Different Kodee here | `kodee: wink` in slide frontmatter |
| No Kodee here | `kodee: false` |
| Kotlin code window | a ``kotlin`` fence (icon chosen from the fence language/meta) |
| Program output panel | a ``console`` fence right under the Kotlin fence |
| Keep imports visible | ``show-imports`` in the fence meta |
| Hide setup code | put `// Example` where the example starts |
| Don't compile this fence | ``no-compile`` in the fence meta |
| Quote a file from the Gradle demo project | `<<< @/demo/src/main/kotlin/demo/Talks.kt#talks` |
| Morph code into the next slide | `magic-move` as the slide separator body |
| Hand-drawn mark | `<DrawnAnnotation type="circle" text="fun main" label="…" :on="1">` |
| Compiler error squiggle | `<InlineCompilerError text="x" message="Unresolved reference: x">` |
| Smart-cast green | `<SmartCast text="nullable.length">` |
| Warning yellow | `<Warning text="val tags: Array<String>" message="…">` |
| Inlay hint | `<TypeHint :line="1" receiver="HTML">` |
| Debugger inline value | `<InlineValue text="println(x)" value="x: 1">` |

## References

| Topic | Reference |
|-------|-----------|
| Layouts (`default`, `cover`, `intro`) and the cover text column | [layouts](references/layouts.md) |
| Kodee mascot: variants, size, position, scale | [kodee](references/kodee.md) |
| Code windows: identity icons, `console` panel, sizing, CSS variables | [code-windows](references/code-windows.md) |
| Folded imports and the `// Example` marker | [folded-imports](references/folded-imports.md) |
| Magic Move **between** slides | [magic-move-between](references/magic-move-between.md) |
| `DrawnAnnotation`: marks, labels, connectors, click timing | [drawn-annotation](references/drawn-annotation.md) |
| `InlineCompilerError`, `SmartCast`, `Warning`, `TypeHint`, `InlineValue` | [code-decorations](references/code-decorations.md) |
| The development-only visual annotation editor | [annotation-editor](references/annotation-editor.md) |
| `slidev-kotlin-snippets`: compilable snippet files | [snippets-cli](references/snippets-cli.md) |
| A local Kotlin Gradle project as the source of truth (`<<<` imports) | [gradle-snippet-imports](references/gradle-snippet-imports.md) |
| Handout, `llms.txt`, sitemap, analytics, hash-mode links | [static-content](references/static-content.md) |
| Full `themeConfig` reference, fonts, IntelliJ formatting guard | [theme-config](references/theme-config.md) |

## Authoring Rules

- **Prefer `fade` over `view-transition`.** With `transition: view-transition`,
  Slidev 52 remounts every preloaded slide on a click-then-navigate sequence and
  stalls for 150 ms+.
- **Write the imports.** The theme folds them off the slide but keeps them in the
  handout and in generated snippet sources, so fences stay compilable.
- **One fence per decoration wrapper.** `InlineCompilerError`, `SmartCast`,
  `Warning`, `TypeHint`, and `InlineValue` each wrap exactly one code block; nest
  wrappers for several hints on one fence.
- **Code broken on purpose belongs under `InlineCompilerError`** — the snippet CLI
  skips those fences, while a fence under `Warning` still has to compile.
- **Nothing is drawn on a guess.** An annotation whose target is missing stays off
  the slide, warns on the console, and (while serving) shows a red chip in the
  slide's top-left corner.
- **Line numbers and highlight ranges count from the first line of real code**,
  after folding — folding never shifts `{1|2-3}` or an annotation's `:line`.

## Commands

```bash
pnpm run dev                                 # serve the deck
pnpm run build                               # build + handout + llms.txt + sitemap
pnpm run export                              # PDF (needs playwright-chromium)
npx slidev-kotlin-snippets                   # generate compilable snippet sources
npx slidev-kotlin-snippets --check           # CI: fail when sources are stale
slidev build --base /my-deck/ --router-mode hash   # GitHub Pages deep links
```

This repo's `package.json` defines `dev`, `build`, and `export`. `export:slide`,
`screenshot`, `lint`, and `visual:review` are scripts of the **theme repository**,
not of a consuming deck.

## Resources

- Theme repo: <https://github.com/nomisRev/slidev-theme-kotlin>
- npm: <https://www.npmjs.com/package/slidev-theme-kotlin>
- Slidev docs: <https://sli.dev>
- Installed copy (authoritative for the pinned version): `node_modules/slidev-theme-kotlin/README.md`
  and `node_modules/slidev-theme-kotlin/components/README.md`
