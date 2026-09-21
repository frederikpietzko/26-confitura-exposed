---
name: code-windows
description: IntelliJ-style code windows - identity icons, the console output panel, font sizing, and CSS variables
---

# Code Windows

Every code fence renders as one IntelliJ Light/Darcula editor surface **without a
title bar**. A compact identity icon sits in the top-right corner and follows the
presentation color scheme.

## Identity icons

````md
```kotlin
fun main() = println("Hello")
```

```kts gradle
plugins { kotlin("jvm") }
```

```yaml toolchain
product: jvm/app
```

```xml maven
<artifactId>demo</artifactId>
```

```sql
SELECT * FROM presentations;
```
````

| Fence | Icon |
|-------|------|
| `kotlin`, `kt`, `kts` | Kotlin |
| any of those + `gradle` | Gradle |
| any of those + `jdbc` | JDBC |
| any of those + `r2dbc` | R2DBC |
| `yaml toolchain` | Amper |
| `java` | Java |
| `bash` | Terminal |
| `xml maven` | Maven |
| `sql` | PostgreSQL |
| anything else | plain code surface, no icon |

## `console` — program output

A `console` fence is **output, not code**: a flat panel in the colours of IntelliJ's
Run tool window, without the editor's outline and shadow, marked with the green run
icon. Put it under the Kotlin fence whose output it shows.

````md
```kotlin
println([1, 2, 3].map { it + 1 })
```

```console
[2, 3, 4]
```
````

When every line of output maps to one line of code, prefer
[`InlineValue`](code-decorations.md#inlinevalue) instead; keep the `console` fence
when the output is one block (a stack trace, a table).

## Sizing

One font size is calculated from the slide canvas width. By default a full-width
window fits **80 monospace characters**.

```css
:root {
  --code-window-columns: 72;
}
```

| Variable | Default | Meaning |
|----------|---------|---------|
| `--code-window-columns` | `80` | characters that fit across a full-width window |
| `--code-window-inline-space` | layout padding | horizontal space a custom layout reserves |
| `--code-window-font-size` | computed | set explicitly (e.g. `1.25rem`) to bypass column sizing |
| `--code-window-character-width` | `0.61` | the mono font's advance ratio (JetBrains Mono) |
| `--code-window-line-height` | `1.42` | line height the calculation assumes |
| `--code-window-border` | — | override one window's border colour |

A deck on another monospace font sets `--code-window-character-width` to that font's
ratio. Narrow the columns on a single slide by scoping the variable to a slide class.

## Colour variables

Each has a light and a dark-mode value:

```css
:root {
  --code-window-kotlin-color: #834df0;
  --code-window-gradle-color: #6c707e;
  --code-window-amper-color: #087cfa;
  --code-window-java-color: #e66d17;
  --code-window-terminal-color: #6c707e;
  --code-window-maven-color: #3574f0;
  --code-window-postgresql-color: #336791;
  --code-window-jdbc-color: #f59e0b;
  --code-window-r2dbc-color: #06b6d4;

  /* The `console` output panel. */
  --code-window-output-background: #f7f8fa;
  --code-window-output-color: #2b2d30;
}

html.dark {
  --code-window-kotlin-color: #a571e6;
  --code-window-output-background: #1e1f22;
  --code-window-output-color: #bcbec4;
}
```

The editor surface itself reads `--editor-bg`, `--editor-fg`, `--editor-border`, and
`--editor-shadow`.

## IntelliJ decorations

| Want | Component |
|------|-----------|
| value next to the line that produced it | [`InlineValue`](code-decorations.md#inlinevalue) |
| green smart-cast background | [`SmartCast`](code-decorations.md#smartcast) |
| yellow warning background | [`Warning`](code-decorations.md#warning) |
| grey `this: HTML` inlay hint | [`TypeHint`](code-decorations.md#typehint) |
| red squiggle + diagnostic | [`InlineCompilerError`](code-decorations.md#inlinecompilererror) |
