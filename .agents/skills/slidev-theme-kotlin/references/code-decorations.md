---
name: code-decorations
description: InlineCompilerError, SmartCast, Warning, TypeHint and InlineValue - IntelliJ decorations over code fences
---

# Code Decorations

Five components that reproduce IntelliJ's in-editor decorations. All of them:

- wrap **exactly one fenced code block** (nest wrappers for several decorations),
- match **exact rendered code text** even when Shiki split it across token spans,
- work in ordinary Shiki fences and in `magic-move` blocks, staying hidden when the
  target is not part of the current step,
- wait for a Magic Move step to settle before appearing, and are shown synchronously
  in print mode so exports capture them,
- warn on the console (and show a red chip while serving) when the target is
  missing, ambiguous, or invalid.

Shared target props: `text` (`\n` continues the match on the next line, indentation
included), `line` (one-based, counted from the first line of *real* code after
folded imports), `occurrence` (one-based, default `1`).

Shared click timing: no timing prop (or `at="0"`) shows it from the initial state;
`at` reveals, `until` hides at its first exclusive click, relative values like `"+1"`
are supported. `on` is both in one — `:on="2"` equals `:at="2" :until="3"` — and
takes precedence over `at` / `until`.

## `InlineCompilerError`

An IntelliJ-style squiggle under a compiler error, with its message beside the code.

````md
<InlineCompilerError text="userName" message="Unresolved reference: userName" :on="1">

```kotlin
println(userName)
```

</InlineCompilerError>
````

| prop | required | meaning |
|------|----------|---------|
| `message` | yes | diagnostic shown beside the code and announced to screen readers; same Markdown as a `DrawnAnnotation` label |
| `text` | one of `text` / `line` | exact rendered code to underline |
| `line` | one of `text` / `line` | one-based line; also narrows a `text` search |
| `occurrence` | no | one-based match of `text`; defaults to `1` |

A diagnostic can cover several lines — every covered line gets its own squiggle and
the message sits after the line the match *starts* on:

````md
<InlineCompilerError text="when (state) {\n  is Loading -> spinner()\n}" message="'when' expression must be exhaustive">
````

Given only `line`, it underlines that line's non-whitespace code. A target on an
empty line cannot be displayed.

| CSS variable | default |
|--------------|---------|
| `--inline-compiler-error-color` | IntelliJ's error reds |
| `--inline-compiler-error-message-size` | the annotated code's font size |

Fences inside `InlineCompilerError` are skipped by the snippet CLI — this is where
deliberately broken code belongs.

## `SmartCast`

IntelliJ's smart-cast green behind an exact piece of code.

````md
<SmartCast text="nullable.length">

```kotlin
val nullable: String? = null
val length =
  if (nullable != null) nullable.length
  else -1
```

</SmartCast>
````

| prop | required | meaning |
|------|----------|---------|
| `text` | yes | exact rendered code to paint green |
| `line` | no | one-based line that narrows the search |
| `occurrence` | no | defaults to `1` |
| `message` | no | text after the end of the match's first line |

Unlike `DrawnAnnotation` and `InlineCompilerError`, which draw an independent mark
over the code, `SmartCast` paints the background of the matched token elements
themselves — so it needs no position tracking and travels with Magic Move for free.

| CSS variable | default |
|--------------|---------|
| `--smart-cast-color` | `#dcf8df` light / `#294436` dark |
| `--smart-cast-message-color` | `#3b7a45` light / `#7bc47f` dark |
| `--smart-cast-message-size` | the annotated code's font size |

## `Warning`

IntelliJ's warning yellow — `SmartCast` in another colour, with the same props.

````md
<Warning text="val tags: Array<String>" message="Property with 'Array' type in a 'data' class: it is recommended to override 'equals()' and 'hashCode()'">

```kotlin
data class Tags(val key: String, val tags: Array<String>)
```

</Warning>
````

`message` is optional; without it, only the background is painted. A fence under
`Warning` **still compiles and is still generated** by the snippet CLI — put code
that is broken on purpose under `InlineCompilerError` instead.

| CSS variable | default |
|--------------|---------|
| `--warning-color` | `#faf4d7` light / `#52503a` dark |
| `--warning-message-color` | `#9a7b0f` light / `#d4b44a` dark |
| `--warning-message-size` | the annotated code's font size |

## `TypeHint`

IntelliJ's inlay hint for a lambda with receiver: the grey `this: HTML` pill after
the `{` that opens the lambda. For DSL slides. Nest one wrapper per hint.

````md
<TypeHint :line="1" receiver="HTML">
<TypeHint :line="2" receiver="BODY" :at="1">

```kotlin
html {
  body {
    p { +"Hello" }
  }
}
```

</TypeHint>
</TypeHint>
````

A `context(...)` block gets the IDE's other shape, `context(Clock)` without `this:`:

````md
<TypeHint :line="2" context="Clock.System">

```kotlin
fun liveTalks(talks: List<Talk>): List<Talk> =
  context(Clock.System) {
    talks.filter { it.isLive() }
  }
```

</TypeHint>
````

| prop | required | meaning |
|------|----------|---------|
| `receiver` | one of the two | rendered as `this: <receiver>` |
| `context` | one of the two | rendered as `context(<context>)` |
| `line` | no | one-based line that narrows the `text` search |
| `text` | no | exact code the pill follows; defaults to `{` |
| `occurrence` | no | defaults to `1` |

When code follows the brace on the same line (`body { }`), the pill sits after that
code instead of covering it.

| CSS variable | default |
|--------------|---------|
| `--type-hint-color` | `#6c707e` light / `#8c8c8c` dark |
| `--type-hint-background` | `#ebecf0` light / `#3c3f41` dark |
| `--type-hint-size` | 85% of the annotated code's font size |

## `InlineValue`

IntelliJ's debugger inline value: the grey `numbers: [1, 2, 3]` written after a line
while the IDE is paused there. For output slides where the reader's eye should never
leave the line. Nest one wrapper per value.

````md
<InlineValue text="println(numbers)" value="numbers: [1, 2, 3]">
<InlineValue :line="5" text="[1, 2, 3]" value="[2, 3, 4]">

```kotlin
val numbers = [1, 2, 3]
println(numbers)

[1, 2, 3].map { it + 1 }
```

</InlineValue>
</InlineValue>
````

| prop | required | meaning |
|------|----------|---------|
| `value` | yes | the text shown after the line |
| `text` | yes | exact rendered code on the line |
| `line` | no | one-based line that narrows the search |
| `occurrence` | no | defaults to `1` |

`value` is written as given: `name: value` reads as the debugger labels a variable, a
bare `[2, 3, 4]` suits an expression that names nothing.

**When to use which**: when every line of a `console` fence maps to one line of code,
replace the fence with `InlineValue`; when the output is one block (a stack trace, a
table) keep the `console` fence, which the theme renders as a run panel.

| CSS variable | default |
|--------------|---------|
| `--inline-value-color` | `#787878` on both schemes |
| `--inline-value-size` | the annotated code's font size |
