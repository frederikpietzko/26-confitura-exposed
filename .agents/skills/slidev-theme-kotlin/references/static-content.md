---
name: static-content
description: Handout pages, llms.txt, sitemap, GoatCounter analytics, and hash-mode deep links
---

# Handout, `llms.txt`, Analytics, Hash Links

## Why

A Slidev build is a single-page app: without JavaScript its `index.html` contains
nothing but the title. Search engines render JavaScript for the root URL at best, and
AI crawlers (GPTBot, ClaudeBot, CCBot, …) render none — so the code on your slides
never reaches them.

## What every `slidev build` writes

| File | Contents |
|------|----------|
| `handout/index.html`, `handout/<section>.html` | one page per source file (`src:` lesson) with every slide's title, prose, and highlighted code, each linking back to the live slide |
| `llms.txt`, `llms-full.txt` | the [llmstxt.org](https://llmstxt.org) convention: an index, and the full Markdown of the deck with complete code snippets (folded imports expanded) |
| `sitemap.xml` | only when the deck's public URL is known |
| `robots.txt` | only when that URL is the host root and `public/` ships none |

A deck served from a sub-path (a GitHub project page, a folder of a blog) gets no
`robots.txt` — crawlers read it at the host root alone. Add
`Sitemap: <siteUrl>sitemap.xml` to the host's own `robots.txt` instead, as the build
reminds you.

The handout and `llms-full.txt` form one outline: the deck, then each section, then
each slide, with a slide's own headings below its title. Handout pages carry a
description, canonical URL, Open Graph tags, and the deck's `lang` (`en` when unset).

The built `index.html` (and the `404.html` copied from it) is made self-describing:
`rel="alternate"` links to the handout and `llms-full.txt`, a plain-text
`<meta name="description">` from the headmatter `info`, and a short paragraph linking
to the same files as visible text, hidden as soon as the app has mounted. That
paragraph is ordinary markup rather than `<noscript>` (which many text extractors
drop), so a crawler that renders no JavaScript still finds the content. With
`siteUrl` set the page also carries the canonical URL and a `rel="sitemap"` link.

Theme markup is translated, not dropped: a `DrawnAnnotation` with a `label` becomes a
quoted note about the code it points at, an `InlineCompilerError` becomes its
diagnostic, Magic Move blocks become their steps, and `<<< @/file#region` snippet
imports are inlined.

## Configure

```yaml
themeConfig:
  siteUrl: https://example.github.io/my-deck/
  handout:
    notes: false   # leave presenter notes out (default: included)
  # handout: false # disable the static content entirely
```

## Analytics (GoatCounter)

[GoatCounter](https://www.goatcounter.com) sets no cookies and stores no personal
data, so it needs no consent banner.

```yaml
themeConfig:
  analytics:
    goatcounter: https://mysite.goatcounter.com/count   # or just: mysite
```

Every `slidev build` then adds `count.js`, and the theme sends **one pageview per
slide**, recorded as `<base>/<slide number>` with the slide's title — so the
dashboard's path list doubles as a per-slide drop-off curve.

Never counted: clicks within a slide, the presenter and overview views, notes, print,
export. `slidev dev`, exports, and screenshots send nothing (`count.js` ignores
localhost and headless browsers). Every deck on one host can share one GoatCounter
site, since paths carry the deck's base. Add `#toggle-goatcounter` to a deck's URL to
stop counting your own visits in that browser.

## Deep links on GitHub Pages

GitHub Pages has no SPA rewrite, so `/my-deck/12` is answered by the copied
`404.html` with a 404 status. Build with Slidev's hash router:

```bash
slidev build --base /my-deck/ --router-mode hash
```

Links then look like `/my-deck/#/12` and always resolve. The theme adds a small
script to hash-mode builds that forwards old `/my-deck/12` links (and `presenter/…`,
`overview`, `notes`, `print`, `export`) to their hash equivalent, so links shared
before the switch keep working.
