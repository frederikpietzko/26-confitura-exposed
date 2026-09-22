---
layout: intro
class: section-intro
---

# Schemas & Migrations

## The tables are Kotlin, so the diff can be generated

---
class: pipeline-slide
---

# The plugin diffs my tables against a real database

<MigrationPipeline />

<!--
- This is a build plugin, not a runtime feature - nothing of this happens in production
- Left: the same table objects from section two, no extra descriptor, no XML
- Middle is the only choice I make: something to compare against
  - H2 in memory when I want it fast and dockerless
  - a Testcontainer when I want the real dialect, thrown away afterwards
  - any JDBC url when I want to diff against a database that already exists
- Before diffing, Flyway replays what is already in db/migration, so the diff is the delta
- Right: a script lands in my source tree - I read it, I edit it, I commit it
- Say it: nothing is applied to any database here, hibernate.hbm2ddl.auto is not back
- Handover: that is the idea - here is the config -> Gradle
-->

---
class: code-slide
---

# In Gradle it is a plugin, a package and a task

<DrawnAnnotation type="circle" text="tablesPackage.set(&quot;com.github.frederikpietzko.demo.taxi.tables&quot;)" label="where the tables live" :geometry="{ label: { x: 0.5, y: 0.66 } }" :on="1">
<DrawnAnnotation type="underline" text="testContainersImageName.set(&quot;postgres:18-alpine&quot;)" label="the database to diff against" :geometry="{ label: { x: 0.5, y: 0.66 } }" :at="2">

```kotlin no-compile
plugins {
    id("org.jetbrains.exposed.plugin") version "1.5.0"
}

exposed {
    migrations {
        tablesPackage.set("com.github.frederikpietzko.demo.taxi.tables")
        testContainersImageName.set("postgres:18-alpine")
    }
}
```

```bash
./gradlew generateMigrations
```

</DrawnAnnotation>
</DrawnAnnotation>

<!--
- This is the demo project's build file, nothing left out
- Click one: the package it scans - table objects are just classes on the classpath
- Click two: swap this line for databaseUrl, databaseUser, databasePassword and it
  talks to H2 or to my own database instead
- fileDirectory defaults to src/main/resources/db/migration, where Flyway already looks
- Last line: a normal Gradle task, so CI can run it and fail on a stale schema
- Handover: and the same thing exists for Maven -> the pom
-->

---
class: code-slide
---

# Maven gets the same plugin, same options

<DrawnAnnotation type="box" text="exposed-maven-plugin" label="same tooling, other build" :geometry="{ label: { x: 0.5, y: 0.58 } }" :on="1">
<DrawnAnnotation type="underline" text="postgres:18-alpine" label="the options are identical" :geometry="{ label: { x: 0.5, y: 0.58 } }" :at="2">

```xml
<plugin>
    <groupId>org.jetbrains.exposed.plugin</groupId>
    <artifactId>exposed-maven-plugin</artifactId>
    <version>1.5.0</version>
    <configuration>
        <tablesPackage>com.example.taxi.tables</tablesPackage>
        <testContainersImageName>postgres:18-alpine</testContainersImageName>
    </configuration>
</plugin>
```

```bash
mvn exposed:generate-migrations
```

</DrawnAnnotation>
</DrawnAnnotation>

<!--
- Same feature, so this slide is short on purpose
- Click one: one plugin entry, no archetype, no extra module
- Click two: the parameter names match the Gradle ones one to one
- Every parameter can be overridden on the command line with -Dexposed.migrations.x
- Bind it to process-classes with an executions block if I want it on every build
- Handover: enough configuration - this is what comes out -> the script
-->

---
class: code-slide
---

# What comes out is plain SQL I review and commit

<DrawnAnnotation type="underline" text="val firstName = varchar(&quot;first_name&quot;, 50)" label="the length I wrote" :geometry="{ label: { x: 0.5, y: 0.5 } }" :on="1">
<DrawnAnnotation type="box" text="first_name VARCHAR(50) NOT NULL," label="the length I get" :geometry="{ label: { x: 0.5, y: 0.5 } }" :at="2">

```kotlin no-compile
object DriverTable : Table("driver") {
    val id = long("id").autoIncrement()
    override val primaryKey = PrimaryKey(id)
    val firstName = varchar("first_name", 50)
    val lastName = varchar("last_name", 50)
}
```

```sql
-- V20260922133700__INITIAL_SCHEMA.sql
CREATE TABLE IF NOT EXISTS driver (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL
);
```

</DrawnAnnotation>
</DrawnAnnotation>

<!--
- One table from the demo project, and the file the plugin wrote for it
- Click one and two: what I declared and what the database gets, side by side
- Nothing to guess: no naming strategy, no dialect default, no provider opinion
- The file is a Flyway migration like any other - I can edit it before committing
- Say it out loud: the schema is generated, the decision to apply it stays mine
- Handover: enough slides about it - let me generate one in front of you -> live demo
-->

---
layout: intro
class: section-intro
kodee: jumping
---

# Live Demo

<!--
- Slides are over for a moment - this is the demo project you have been reading all along
- Plan is mine to set on stage - schema generation is only one part of it
- If the demo gods are unkind: the generated script is in the repo, I will show that instead
- Handover: back to the deck afterwards - where Exposed is going next -> future plans
-->
