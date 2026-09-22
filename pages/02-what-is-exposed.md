---
layout: intro
class: section-intro
---

# What is Exposed?

## JetBrains' Kotlin SQL Library

---
class: bridge-slide
---

# Exposed sits between Kotlin and SQL - and hides neither

<ExposedBridge />

<!--
- Left: my domain stays mine - data classes, val, no annotations, no compiler plugins
- Right: the database stays the database - schemas, tables, rows, SQL
- Middle is the whole talk: Exposed is a library, not a runtime that owns my objects
  - Two ways to talk to the driver: JDBC today, R2DBC for coroutines
  - Two ways to write queries: the Query DSL, or the DAO if you want entities
  - Integrations: Spring Boot is the one we will use, Ktor exists too
-  nothing here translates my object graph into SQL behind my back
  - What you see is what runs, both directions
-->

---
class: module-slide
---

# You pick the modules - nothing else comes along

<ModuleList>
  <ModuleRow name="exposed-core" tag="required">Type-safe SQL DSL</ModuleRow>
  <ModuleRow name="exposed-dao" tag="optional">Entity API on top of the DSL</ModuleRow>
  <ModuleRow name="exposed-jdbc">for blocking drivers</ModuleRow>
  <ModuleRow name="exposed-r2dbc">for non-blocking drivers, coroutines</ModuleRow>
  <ModuleRow name="exposed-spring-boot4-starter">Datasource and transactions wired up</ModuleRow>
  <ModuleRow name="exposed-java-time, -json, -money" muted>Column types you opt into</ModuleRow>
</ModuleList>

<!--
- Point at the first line and say it out loud: core is the whole library, everything else is a choice
  - No base class, no agent, no compiler plugin in this list
- exposed-dao is the one people expect to be mandatory - it is not
  - We will not use it today, the DSL section is the talk
- jdbc versus r2dbc is one dependency swap, not a rewrite of my queries
- The Spring starter is the boring one: datasource, transaction manager, done - section four
- Bottom row: the integrations exist so I don't have to hand-roll common column types (but you totally can)
- Handover: enough inventory, let us start defining tables -> yes Tables not Entities
-->

---

# Defining Tables

