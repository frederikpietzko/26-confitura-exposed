---
layout: intro
class: section-intro
---

# My subjective problems with JPA

## Why do we pretend that Databases store objects?

<!--
- Frame it first: this is subjective, and I use JPA myself
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
  - No identity: load the same row twice, get two unrelated objects
  - Every dialect its own SQL; every table change is a shotgun edit
- Drop the term once: object-relational impedance mismatch
  - Objects are identity, references, behaviour
  - Relations are tuples and set operations
  - Neither is wrong, they just slice the world differently
- Timeline, spoken not shown:
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
- Where it stops helping: reporting, window functions, CTEs, bulk updates, upserts, partial projections
- The moment the query becomes the interesting part, the ORM steps aside
- Punchline, say it out loud: the escape hatch proves the abstraction has a limit - and you still pay
  the full price of the abstraction around it
- Point at the return type: List<Array<Any>> - all type safety gone, at the worst possible moment
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
  - equals/hashCode on an entity: the classic trap, id is null until flush
  - The domain model is now a mapping file that happens to have behaviour
- FetchType.LAZY: one word here decides how many queries run somewhere else entirely
  - Set up the next slide with it, don't explain N+1 yet
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
- Show the three lines first, let them look innocent - then click to reveal the log
- Only query log of the talk - do not explain N+1 twice
- The point is not "N+1 exists", everyone knows that
- The point: nothing at the call site tells you
  - Behaviour lives in annotations, fetch strategy, session state, transaction boundaries
  - Everywhere except the line you are reading
- Other side of the same coin: LazyInitializationException
  - The type says List<Order>, the truth depends on a transaction that already closed
- One sentence to nail it: the type system tells you what you have, JPA decides when it is there
-->

---
class: code-slide
---

# Which PR adds an N+1?

```kotlin no-compile
// A
fun report(): List<Row> =
    repo.findAll().map { Row(it.name, it.orders.size) }

// B
fun report(): List<Row> =
    repo.findAllWithOrders().map { Row(it.name, it.orders.size) }
```

<!--
- State it as a hypothesis: agents - and juniors - are bad at JPA, for the same reason
  - Not because they are dumb, but because the semantics are not in the code they read
  - The diff looks identical
- An LLM pattern-matches to the canonical tutorial shape: entity, @OneToMany, repository, for-loop
  - That shape is exactly the one that falls over in production
- Honesty beat: agents write very good tutorial JPA - that is the trap
- Ask the room: would you let an agent review this PR?
- Hand over: so what would I rather read? -> what is Exposed?
-->
