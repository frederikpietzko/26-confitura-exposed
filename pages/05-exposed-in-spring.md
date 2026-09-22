---
layout: intro
class: section-intro
---

# Exposed in Spring Boot

## One starter, and the transaction stays Spring's

---
class: code-slide
---

# One starter, and your Boot version picks it

<DrawnAnnotation type="circle" text="exposed-spring-boot4-starter" label="Spring Boot 4" :geometry="{ label: { x: 0.5, y: 0.52 } }" :on="1">
<DrawnAnnotation type="underline" text="exposed-spring-boot-starter" label="Spring Boot 3" :geometry="{ label: { x: 0.5, y: 0.52 } }" :at="2">

```kotlin no-compile
dependencies {
    implementation("org.jetbrains.exposed:exposed-spring-boot4-starter:1.5.0")
    // Spring Boot 3
    // implementation("org.jetbrains.exposed:exposed-spring-boot-starter:1.5.0")

    runtimeOnly("org.postgresql:postgresql")
}
```

</DrawnAnnotation>
</DrawnAnnotation>

<!--
- This is the whole Exposed setup in the demo project's build file
- Click one: the starter pulls core, jdbc, dao and the Spring integration
- Click two: same library on Boot 3, only the artifact name differs - the 4 is the Boot version
- Say it: no agent, no weaving, no persistence.xml - it is a dependency like any other
- Handover: the starter brings autoconfiguration, and I opt into it explicitly -> the application class
-->

---
class: code-slide
---

# I opt into the autoconfiguration by hand

<DrawnAnnotation type="box" text="value = [ExposedAutoConfiguration::class]," label="datasource, transactions, wired" :geometry="{ label: { x: 0.5, y: 0.55 } }" :on="1">
<DrawnAnnotation type="underline" text="exclude = [DataSourceTransactionManagerAutoConfiguration::class]," label="Exposed manages the transaction" :geometry="{ label: { x: 0.5, y: 0.55 } }" :at="2">

```kotlin no-compile
@SpringBootApplication
@ImportAutoConfiguration(
    value = [ExposedAutoConfiguration::class],
    exclude = [DataSourceTransactionManagerAutoConfiguration::class],
)
class DemoApplication
```

</DrawnAnnotation>
</DrawnAnnotation>

<!--
- Four lines on the application class, and that is the entire integration
- Click one: ExposedAutoConfiguration builds the transaction manager on top of my datasource
- Click two: I exclude Spring's JDBC one so there is exactly one manager, not two fighting
- Datasource stays plain spring.datasource in application.yml - nothing Exposed specific
- Handover: with that in place, transactions look like every other Spring app -> @Transactional
-->

---
class: code-slide
---

# Transactions stay Spring's job

<DrawnAnnotation type="circle" text="@Transactional" label="plain Spring, no wrapper" :geometry="{ label: { x: 0.68, y: 0.26 } }" :on="1">
<DrawnAnnotation type="underline" text="TaxiEntity.all().toList()" label="no `transaction { }` block" :geometry="{ label: { x: 0.5, y: 0.64 } }" :at="2">

```kotlin no-compile
@Service
@Transactional
class TaxiService(
    private val driverService: DriverService,
) {
    fun getAllTaxis(): List<TaxiEntity> =
        TaxiEntity.all().toList()
}
```

</DrawnAnnotation>
</DrawnAnnotation>

<!--
- Same service you saw in the DAO section, now with its annotations
- Click one: org.springframework.transaction.annotation.Transactional - the one you already use
- Propagation, rollback rules, readOnly: all of it works, Exposed joins the Spring transaction
- Click two: no transaction { } block in the code - the starter binds it for me
- Say it: nothing new to learn here, and that is exactly the point
- Handover: one thing left that JPA used to do for me -> the schema
-->
