---
layout: intro
class: section-intro
---

# My subjective problems with JPA

## Why do we pretend that Databases store objects?

<!--
- this is subjective, and I use JPA myself
- forces way of thinking about persistence layer that doesn't come naturally
- Promise: ~5 minutes of "why", then the rest of the hour on an alternative
-->

---
class: code-slide
---

# Before ORMs: every query was plumbing

```kotlin no-compile
val sql = "SELECT id, name, email FROM customer WHERE id = ?"
connection.prepareStatement(sql).use { stmt ->
    stmt.setLong(1, id)
    stmt.executeQuery().use { rs ->
        if (rs.next()) Customer(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("email")
        )
        else null
    }
}
```

<!--
- This is the problem ORMs were born to solve - and it was a real problem
- Three pain points, say them, don't show them:
  - ResultSet plumbing repeated for every single query
  - no typesafety: if my schema changes, I need to grep for strings and hope for good tests
  - sql dialects not portable

- object-relational impedance mismatch
  - Objects are identity, references, behaviour
  - Relations are tuples and set operations
  - Neither is wrong, they just slice the world differently

- Timeline:
  - 2001 Gavin King writes Hibernate, frustrated with EJB2 Entity Beans
  - He joins the EJB3 expert group
  - 2006 JPA standardises Hibernate
-->

---
class: code-slide
magicMove: true
---

# After ORMs: the same job in one line

```kotlin no-compile
val customer = em.find(Customer::class.java, id)
```

<!--
- Same job, one line - this is why JPA won, and it deserves the credit

- ORMs never claimed SQL is bad; they gave us real wins:
  - Mapping without boilerplate
  - Identity map
  - Unit of work with dirty checking
  - Caching
  - Dialect portability
- Land the thesis here: the first 80% is free
-->

---
class: code-slide
magicMove: true
---

# The other 20%

```kotlin no-compile
@Query(
    value = """
        SELECT c.id, c.name, SUM(o.total) AS revenue,
               RANK() OVER 
                (PARTITION BY c.region ORDER BY SUM(o.total) DESC) 
               AS rnk
        FROM customer c JOIN orders o ON o.customer_id = c.id
        WHERE o.created_at >= :since
        GROUP BY c.id, c.name, c.region
    """,
    nativeQuery = true,
)
fun topCustomersPerRegion(since: Instant): List<Array<Any>>
```

<!--
- the other 20%
- complex queries for reporting, upserts, partial projections
- The moment the query becomes the interesting part, the ORM steps aside
- Punchline, say it out loud: the escape hatch proves the abstraction has a limit - and you still pay
  the full price of the abstraction around it
- Point at the return type: List<Array<Any>> - all type safety gone, at the worst possible moment
- jpql & jpa projections can help
- but queries are still not typesafe -> making refactoring hard
-->

---
class: code-slide
---

# The price of the magic

```kotlin no-compile
@Entity
class Customer(
    @Id @GeneratedValue
    var id: Long? = null,
    var name: String,
    @OneToMany(fetch = FetchType.LAZY, cascade = [ALL], orphanRemoval = true)
    @JoinTable(
        name = "customer_order",
        joinColumns = [JoinColumn(name = "customer_id")],
        inverseJoinColumns = [JoinColumn(name = "order_id")],
    )
    var orders: MutableList<Order> = mutableListOf(),
)
```

<!--
- Point at the code, one item at a time, don't read a list:
  - var everywhere, no-arg constructor, no data class, no val - mutability is required
  - allopen + noarg compiler plugins exist so Kotlin can pretend to be Java for Hibernate
  - The domain model is now a mapping file that happens to have behaviour
- FetchType.LAZY: one word here decides how many queries run somewhere else entirely
- @JoinTable: entities are not tables
  - Two fields in Kotlin, three tables in the database
  - The mapping is a translation layer, and I have to hold both models in my head
- Bigger point: these are not bugs, these are choices JPA made for me
- If my mental model differs, I am the one who has to adapt
-->

---
class: code-slide
---

# Your code doesn't say what your database does

```kotlin no-compile
val customers = repository.findAll()
customers.forEach { c ->
    println(c.orders.size)
}
```

<v-click>
<div class="reveal-log">

```sql
select * from customer
select * from orders where customer_id = 1
select * from orders where customer_id = 2
select * from orders where customer_id = 3
... 997 more
```

</div>
</v-click>

<!--
- nothing at the call site tells you how your query works
- query behaviour lives in annotations, fetch strategy, session state, transaction boundaries
- there isn't even any code you or an LLM can read
- this is a n+1 problem & this might run fast locally or in QA
    - and pass Code Review
    - but fails in production
- Devs & LLMs will write this, I call it "Tutorial JPA"
    - it's not even because they are dumb
    - the semantics are correct
    - but this is what everyone trained on (Devs & LLMs alike)

-->

---
class: wishlist-slide
---

# Four things I want from the alternative

<div class="wishlist">
  <div class="wish" v-click>Fits my mental model</div>
  <div class="wish" v-click>Easy for an LLM to write and debug</div>
  <div class="wish" v-click>Reviewable - I see the SQL</div>
  <div class="wish" v-click>Typesafe - compiler and refactoring</div>
</div>

<!--
- I want something that
- fits my mental model
    - I like simple things & KISS
    - I like databases & SQL
    - I don't want to hide it and abstract it away
    - I also lean into Hexagonal Architecture often
    - So clean domain models & Ports for persistence
- is easy for llms to generate
    - LLMs should be able to write this
    - correctly, if the syntax is wrong or types don't match I want a deterministic tool to give it feedback
    - and this should be achievable using a simple SKILL
- easy review
    - I wan't to know what the sql looks like
    - so that I can give my agents or collegues feedback when they write slow queries in the wrong places
- I want typesafety
    - refactoring safe (if I rename a column, IJ should be able to rename all references to it)
    - compiler checked (if I do something wrong, I want the compiler to tell me)
    - again fastest feedback loop possible for agents
-->