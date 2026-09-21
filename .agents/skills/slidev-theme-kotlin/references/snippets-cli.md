---
name: snippets-cli
description: slidev-kotlin-snippets - turn Kotlin and Java fences into compilable source files, Knit-style
---

# `slidev-kotlin-snippets`

A Kotlin fence on a slide is a fragment: it carries its imports (folded away) but no
package, and may mix declarations with bare statements. This CLI turns Kotlin and
Java fences into source files — in the spirit of
[KotlinX Knit](https://github.com/Kotlin/kotlinx-knit) — so a Gradle module compiles
the deck's code against the real libraries and a **stale slide fails the build
instead of the talk**.

## Configure

```yaml
themeConfig:
  snippets:
    dir: src/main/kotlin/presentation/snippets    # default: src/main/kotlin/snippets
    javaDir: src/main/java/presentation/snippets  # default: src/main/java/snippets
    package: presentation.snippets                # default: snippets
    imports:                                      # added to every file
      - presentation.support.*
```

## Run

```bash
npx slidev-kotlin-snippets            # writes the files, removes stale ones
npx slidev-kotlin-snippets --check    # exits 1 when they are out of date (CI)
npx slidev-kotlin-snippets talk.md    # another entry than slides.md
```

## What it generates

Each Kotlin fence becomes `<dir>/<section>/<slide>-<title>.kt`; each Java fence
becomes `<javaDir>/<section>/<slide>-<title>.java`. Files get the package
`<package>.<section>.slide<n>`, so every step of a Magic Move chain can declare its
own `object Talks`. A Java and a Kotlin fence on the same slide deliberately share
that package, so Java ↔ Kotlin interop examples compile.

Inside a file:

- The fence's imports come first, after the `imports` from the config. Keep the
  tables, types, and values the slides use in a hand-written package and star-import
  it; a fence that defines its own `Talks` shadows the shared one, because a
  declaration in the file's own package wins over a star import.
- Kotlin top-level declarations (`fun`, `val`, `object`, `class`, … with their
  annotations, modifiers, and `context(...)` lines) stay top-level; bare statements
  such as `Talks.selectAll()` are wrapped in `suspend fun main()`. A Kotlin `package`
  line is dropped, `@file:` annotations move above the generated package.
- Java fences are emitted as compilation units with the generated package and
  imports, so they must contain **declarations**, not bare statements. Their own
  `package` line is replaced. A fence declaring a public top-level Java type is named
  after that type; when a later slide of the same section declares a public type an
  earlier slide already declared, its file goes in a directory named after the slide.
- A slide with several fences in the same language tells them apart by their first
  meta word (`jdbc`, `r2dbc`) or a letter.

## Fences that should not compile

Add `no-compile` to the meta of a fence that sketches library internals or an
intermediate step; the flag is stripped before Slidev sees it. Fences inside
an `InlineCompilerError` are skipped automatically, and so are `gradle` build-script
fences.

## Wire it into Gradle

```kotlin
val snippetsCheck by tasks.registering(Exec::class) {
    commandLine("npx", "slidev-kotlin-snippets", "--check")
}
tasks.check { dependsOn(snippetsCheck) }
```
