---
name: gradle-snippet-imports
description: Import code from a local Kotlin Gradle project into slides with Slidev's <<< snippet import, so the Gradle sources are the single source of truth
---

# Gradle project as the source of truth

Two opposite directions exist for "compiling snippets", and they are easy to confuse:

| Direction | Mechanism | Source of truth |
|-----------|-----------|-----------------|
| slides → Kotlin files | `slidev-kotlin-snippets` (theme, see [snippets-cli](snippets-cli.md)) | the fence in `pages/*.md` |
| files → slides | `<<< @/path/File.kt#region` (Slidev core) | the `.kt` file in the Gradle module |

This reference covers the **second** one: a real Kotlin Gradle project inside the deck
repo, compiled and run by Gradle, quoted by the slides. Nothing custom is needed — the
theme renders an imported `.kt` file in its IntelliJ code window with the Kotlin
identity icon, because the language is inferred from the file extension.

Verified against Slidev 53 + `slidev-theme-kotlin` 0.13.

## Layout

The Gradle project **must live inside the deck root** (or the pnpm workspace root).
Slidev refuses a path outside it with `Code snippet path escapes the project root`, so a
sibling directory does not work.

```
my-deck/
├── slides.md
├── pages/03-sql-dsl.md
└── demo/                      # the Gradle project
    ├── build.gradle.kts
    └── src/main/kotlin/demo/Talks.kt
```

## Mark regions in the Kotlin file

```kotlin
package demo

import org.jetbrains.exposed.v1.core.Table

// #region talks
object Talks : Table("talks") {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 200)
    override val primaryKey = PrimaryKey(id)
}
// #endregion talks
```

Region markers are stripped and the region body is dedented.

## Quote it on the slide

```md
# Tables are objects, not annotated classes

<<< @/demo/src/main/kotlin/demo/Talks.kt#talks
```

- `@/` resolves from the deck root; a plain relative path resolves from the `.md` file.
- Line highlighting and line numbers still work:
  `<<< @/demo/src/main/kotlin/demo/Talks.kt#talks {2-3|4}{lines:true}`.
- The file is registered in Slidev's `watchFiles`: **editing the Kotlin file in IntelliJ
  hot-reloads the slide.** That is the payoff — full completion and a compiling module,
  and the slide follows.

## Limits to design around

- **No import folding.** The theme's fold pass runs as a `pre` markdown transformer,
  *before* Slidev expands `<<<`, so an imported file shows its `package` and `import`
  lines verbatim. Put the region around just the interesting part (start it below the
  imports) — that replaces [folded imports](folded-imports.md) for imported files.
- **No cross-slide Magic Move.** `magicMove: true` collects fences from the slide's
  markdown, and an `<<<` line is not a fence yet at that point. Evolving-snippet chains
  must stay inline fences.
- **No theme fence flags.** `no-compile` / `show-imports` live on a fence info line;
  an `<<<` import has none. It also never reaches `slidev-kotlin-snippets`, which is
  fine: Gradle already compiles the file.
- **Handout / `llms-full.txt`:** the theme inlines `<<<` imports into the static output,
  but its regex does not accept a language token after the path. Write
  `<<< @/demo/.../Talks.kt#talks`, **not** `<<< @/demo/.../Talks.kt#talks kotlin`, or the
  handout prints the raw `<<<` line.

## Choosing per slide

Both approaches can coexist in one deck:

- Real, runnable demo code (DSL usage, Spring wiring, schema/migrations) → `<<<` from the
  Gradle module, so talk and live demo cannot drift apart.
- Same example evolving, Magic Move chains, code broken on purpose under
  `InlineCompilerError` → inline fences, optionally compile-checked with
  `slidev-kotlin-snippets`.

## Keeping the module honest

The Gradle module is an ordinary project: `./gradlew build` (or `compileKotlin`) in CI is
what guarantees every quoted region still compiles. A renamed region, however, fails
silently — Slidev falls back to the whole file when a `#region` is missing, so grep the
deck for region names when refactoring.
