---
layout: intro
class: section-intro
---

# Exposed in Spring Boot

---
class: code-slide
---

# Spring Boot Starters

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
- Click one: ExposedAutoConfiguration builds the transaction manager on top of my datasource
- Click two: I exclude Spring's JDBC one so there is exactly one manager, not two fighting
- Datasource stays plain spring.datasource in application.yml - nothing Exposed specific
- Handover: with that in place, transactions look like every other Spring app -> @Transactional
-->

---
class: code-slide
---

# Idiomatic Transactions in Spring

<DrawnAnnotation type="underline" text="@Transactional" label="plain Spring, no wrapper" :geometry="{ label: { x: 0.68, y: 0.26 } }" :on="1">
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
- Click one: normal Spring Data Transactional
- Propagation, rollback rules, readOnly: all of it works, Exposed joins the Spring transaction
- Click two: no transaction { } block in the code - the starter binds it for me
- you can also mix the annotation and the transaction block
- Handover: one thing left that JPA used to do for me -> the schema
-->
