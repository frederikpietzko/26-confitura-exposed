---
name: folded-imports
description: Imports and setup code are folded off the slide but kept in the source, handout, and generated snippets
---

# Folded Imports

A snippet on a slide reads best without its imports, but a snippet that carries them
is complete: it compiles as written, a checker such as Knit can verify it, and the
handout shows the reader which package every symbol comes from. **So write the
imports — the theme folds them away.**

````md
```kotlin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

fun numbers(): Flow<Int> = flowOf(1, 2, 3)
```
````

The slide shows only `fun numbers()…`; the handout and `llms-full.txt` print the
fence in full.

## The `// Example` marker

When an example needs more than imports — a table definition, a helper it calls — put
a `// Example` comment where the example starts. Everything above it leaves the
slide, and so does the marker line.

````md
```kotlin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

fun numbers(): Flow<Int> = flowOf(1, 2, 3)

// Example: reuse the flow above without showing it
fun doubled(): Flow<Int> = numbers().map { it * 2 }
```
````

The window shows `fun doubled()…`. The comment is ordinary Kotlin, so the fence still
compiles and the handout keeps it. A description after the colon is optional.

## Rules

- Folding applies to `kotlin`, `kt`, `kts`, `java`, and `scala` fences.
- **Without a marker**, only a block of `import` lines at the top of the fence folds;
  a `package` line or `@file:` annotation before it stays visible.
- **With a marker**, everything above it folds. Only the *first* `// Example` line in
  a fence counts, and only when there is code above it.
- **Line numbers, `{1|2-3}` highlight ranges, and annotation targets all count from
  the first line of real code**, so folding never shifts them.
- Cross-slide Magic Move chains and classic in-slide `magic-move` blocks fold too,
  step by step.

## Opting out

Add `show-imports` to a fence when the slide is about the imports themselves:

````md
```kotlin show-imports
import kotlinx.coroutines.flow.Flow
```
````

Or keep every import visible deck-wide:

```yaml
themeConfig:
  foldImports: false
```
