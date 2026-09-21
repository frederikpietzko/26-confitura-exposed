---
name: theme-config
description: Full themeConfig reference, theme defaults (fonts, color schema), and the IntelliJ formatter guard
---

# Configuration

## `themeConfig` reference

Everything the theme reads from the deck's **headmatter**:

```yaml
---
theme: kotlin
themeConfig:
  kodee: greeting            # mascot variant, or an object, or false
  foldImports: true          # fold import blocks off the slide (default: true)

  drawnAnnotation:
    connect: false           # deck-wide default for annotation leader lines

  siteUrl: https://example.github.io/my-deck/
  handout:
    notes: false             # keep presenter notes out of the handout
  # handout: false           # disable handout / llms.txt / sitemap entirely

  analytics:
    goatcounter: mysite      # or the full https://mysite.goatcounter.com/count

  snippets:
    dir: src/main/kotlin/presentation/snippets
    javaDir: src/main/java/presentation/snippets
    package: presentation.snippets
    imports:
      - presentation.support.*
---
```

| Key | Reference |
|-----|-----------|
| `kodee` | [kodee](kodee.md) |
| `foldImports` | [folded-imports](folded-imports.md) |
| `drawnAnnotation.connect` | [drawn-annotation](drawn-annotation.md) |
| `siteUrl`, `handout`, `analytics` | [static-content](static-content.md) |
| `snippets` | [snippets-cli](snippets-cli.md) |

## Per-slide frontmatter the theme adds

| Key | Meaning |
|-----|---------|
| `kodee` | variant string, object, or `false` for this slide |
| `magicMove: true` | same as the bare `magic-move` slide separator |
| `drawnAnnotation.connect` | leader-line default for this slide |

## Theme defaults

From the theme's `package.json`:

```json
{
  "slidev": {
    "colorSchema": "both",
    "defaults": {
      "fonts": {
        "sans": "JetBrains Sans,Inter",
        "mono": "JetBrains Mono Local,JetBrains Mono",
        "local": "JetBrains Sans,JetBrains Mono Local",
        "weights": "400,700,800",
        "italic": true
      }
    }
  }
}
```

Locally installed **JetBrains Sans** and **JetBrains Mono** are preferred, with Inter
and Google-hosted JetBrains Mono as fallbacks. `index.html` contributes the font
preconnect head tags to every deck.

## Requirements

- Node.js >= 20.12.0
- npm, or pnpm with `shamefully-hoist=true`

```bash
npm install slidev-theme-kotlin
```

## IntelliJ formatting guard

IntelliJ recognizes only the **first** YAML block in a Markdown file as frontmatter,
so **Reformat Code** mangles later Slidev frontmatter. Enable formatter tags and stop
formatting after the headmatter:

```ini
# .editorconfig
[*.md]
ij_formatter_tags_enabled = true
```

```md
---
theme: kotlin
---

<!-- @formatter:off -->
```

## Exporting a single slide (theme repo script)

`npm run export:slide` is an AI-agent-friendly wrapper around `slidev export` that
writes one PNG instead of a directory:

```bash
npm run export:slide -- --slide 12                    # initial state, 1-based slide
npm run export:slide -- --slide 12 --click 3          # after the third click (0-based)
npm run export:slide -- --entry talk.md --slide 4 --click 0
```

Without `--output`, screenshots go to `.slidev/exports/`. `npm run screenshot`
exports every slide and click state. PNG/PDF export needs
`playwright-chromium`; if install scripts are disabled, run
`npx playwright install chromium` once.
