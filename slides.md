class: inverse, center, middle

# Haladó JPA

---

class: inverse, center, middle

# Tematika

---

# Tematika 1.

* Áttekintés
* JPA verziók
* Bonyolult kapcsolatok, lazy, eager fetch
* Entity graph
* Cascade műveletek
* Detach, merge
* Tranzakciókezelés, persistence context
* Flush

---

# Tematika 2.

* Bulk műveletek
* Query hints
* Refresh
* Lock
* First level és shared cache
* Natív lekérdezések
* Best practices

---

# Források

* [Mike Keith, Merrick Schincariol: Pro JPA 2 (Expert's Voice in Java) 2nd ed. Edition](https://www.amazon.com/Pro-JPA-Experts-Voice-Java/dp/1430249269)
* [Vlad Mihalcea:High-Performance Java Persistence](https://vladmihalcea.com/books/high-performance-java-persistence/)
* [Hibernate Documentation 5.6](https://hibernate.org/orm/documentation/5.6/)

---

class: inverse, center, middle

# Bevezetés

---

# Alapfogalmak 1.

* Perzisztens technológiák
  * JDBC
  * JPA
  * jOOQ
* DataSource
  * Legacy: C3PO, Apache DBCP
  * New: Hikari CP, [vibur-dbcp](http://www.vibur.org/)
* JPA
  * Hibernate (5.6)
  * EclipseLink (3.0.2)
* Entity
  * State field
  * Relationship field

---

# Alapfogalmak 2.

* Persistence unit
* Persistence context
* Entity manager
  * Application managed
  * Container managed
* JPQL
* Criteria API

---

# Verziók

* [JSR 220](https://jcp.org/en/jsr/detail?id=220) Enterprise JavaBeans 3.0
* [JSR 317](https://jcp.org/en/jsr/detail?id=317) Java Persistence 2.0
* [JSR 338](https://jcp.org/en/jsr/detail?id=317) Java Persistence 2.1
* [Jakarta Persistence 3.0](https://jakarta.ee/specifications/persistence/3.0/)

---

# JPA 2.0

* 2009\. december
* Kivált az EJB szabványból

---

# JPA 2.0 - Kiterjesztett ORM funkcionalitás

* Beágyazott objektumokat tartalmazó kollekciók (element collections)
* Map támogatása entitások és beágyazott objektumok esetén is
* One-to-many és one-to-one esetén is lehet join table
* Unidirectional one-to-many join column
* Orphan removal
* Oszlop a sorrend megtartására (`@OrderColumn` annotáció)
* Hozzáférés típusok kombinálása (`@Access`)
* Derived id: összetett kulcs, mely tartalmaz egy külső kulcsot is (pl. `project_id` = `department_id` + `project_short_name`)
* Több szinten beágyazott objektumok (Complex Embedded Objects)

---

# JPA 2.0 - Lekérdező nyelv

* Date, time, timestamp literálok
* Szűrés leszármazott típusra (`TYPE`)
* Map support
* Collection input paraméter (`IN`)
* `CASE`
* `NULLIF`, `COALESCE`
* Scalar operation (`SELECT LENGTH(e.name) FROM Employee e`)
* `INDEX` kollekcióban az elem indexe
* Projection query-ben változó (`SELECT new CustInfo(c.name, a) FROM Customer c JOIN c.address a`)

---

# JPA 2.0 további újdonságok

* `TypedQuery<T>`
* Criteria query API
* Shared cache
* Validálás támogatása
* Pessimistic lock
* `getMetamodel()`

---

# JPA 2.1 újdonságok 1.

* 2013\. május
* Attribute Converter, `@Converter`
* Criteria Update/Delete
* Tárolt eljárások
* Programmatic named query
* `@ConstructorResult`

---

# JPA 2.1 újdonságok 2.

* Séma generálás
* Entity graph
* JPQL: allekérdezések, függvények, JOIN ON, TREAT (downcast)
* Unsynchronized Persistence Context
* CDI in entity listeners

---

# JPA 2.2

* Lekérdezések streameket is tudnak visszaadni, háttérben optimalizálhat `ScrollableResult`-tal
* Repeatable annotációk
* Java 8 Date and Time API támogatása
* `AttributeConverter`-ben CDI injection
* JDK 9 module system Persistence Provider Discovery Mechanism

---

# Jakarta Persistence 3.0

```xml
<dependency>
    <groupId>org.eclipse.persistence</groupId>
    <artifactId>org.eclipse.persistence.jpa</artifactId>
    <version>3.0.2</version>
</dependency>

<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core-jakarta</artifactId>
    <version>5.6.1.Final</version>
</dependency>
```

* Csomagnév: `jakarta.persistence`
* `persistence.xml` névtér

---

# Példa alkalmazás

* H2
* Hibernate
* Lombok
  
Teszteléshez:

* JUnit 5, AssertJ, `hibernate-testing`

---

# Séma generálás

* Példákban

```xml
<property name="javax.persistence.schema-generation.database.action" value="drop-and-create" />
```

Valójában:

* Flyway vagy Liquibase

---

# Teszteset

```java
class LocationServiceTest {

    EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void init() {
        entityManagerFactory = Persistence.createEntityManagerFactory("pu");
    }

    @Test
    void testSaveAndList() {
        doInJPA(() -> entityManagerFactory, (em) -> {
            LocationsDao locationsDao = new LocationsDao(em);
            locationsDao.saveLocation(new Location( "a", 1, 2));
            List<Location> locations = locationsDao.listLocations();

            assertThat(locations)
                    .extracting(Location::getName)
                    .containsExactly("a");
        });
    }
```

---

class: inverse, center, middle

# Naplózás

---

# Cél

* A performancia problémák több, mint a fele adatbáziskezelésnél keresendő
* Definition of Done: tartalmazza az adatbázis műveletek 

---

# Naplózás

* Ne használjuk a `hibernate.show_sql` property-t, hiszen konzolra naplóz
* Helyette pl. Logback, `logback.xml`

```xml
<logger name="org.hibernate.SQL" level="debug" />
```

* `persistence.xml`

```xml
<property name="hibernate.format_sql" value="true"/>
<property name="hibernate.use_sql_comments" value="true"/>
```

---

# P6Spy

* [P6Spy](https://github.com/p6spy/p6spy)
* `spy.properties`

---

# datasource-proxy, datasource-assert

```xml
<dependency>
    <groupId>net.ttddyy</groupId>
    <artifactId>datasource-proxy</artifactId>
    <version>1.7</version>
</dependency>
```

```xml
<dependency>
    <groupId>net.ttddyy</groupId>
    <artifactId>datasource-assert</artifactId>
    <version>1.0</version>
    <scope>test</scope>
</dependency>
```

---

# datasource-proxy

```java
PrettyQueryEntryCreator creator = new PrettyQueryEntryCreator();
creator.setMultiline(true);
SystemOutQueryLoggingListener listener = new SystemOutQueryLoggingListener();
listener.setQueryLogEntryCreator(creator);

DataSource dataSource = ProxyDataSourceBuilder.create(targetDataSource)
  .name("ProxyDataSource")
  .countQuery()
  .multiline()
  .listener(listener)
  .logSlowQueryToSysOut(1, TimeUnit.MINUTES)
  .build();
```

---

# datasource-assert

```java
JdbcDataSource targetDataSource = new JdbcDataSource();
targetDataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");

dataSource = new ProxyTestDataSource(targetDataSource);

Map<String, Object> properties = new HashMap<>();
properties.put("javax.persistence.nonJtaDataSource", dataSource);
properties.put("javax.persistence.transactionType", "RESOURCE_LOCAL");

entityManagerFactory = Persistence.createEntityManagerFactory("pu", properties);
```

---

# Assert

```java
assertTrue(dataSource.getQueryExecutions().size() < 10);
```

```java
@AfterEach
void denit() {
    dataSource.reset();
}
```

---

class: inverse, center, middle

# Write behind cache

---

# Write behind cache

* Akkor módosít, mikor feltétlen szükséges
* Rövid ideig lockol
* Ezzel transparent statement batching
* Dirty checking
* Hibernate alapértelmezett dirty checking: másolat készítése
* Ha ugyanarra módosítjuk, nincs update

---

# Immutable

* Hibernate `@Immutable` annotáció
* Gyorsítja, hiszen nem kell dirty checking

---

# Dirty checking gyorsítás

* Weaving
* Bájtkód manipuláció
* Típusai
  * Statikus - build time, `hibernate-enhance-maven-plugin`
  * Dynamic weaving - agent, classloading time

```xml
<plugin>
    <groupId>org.hibernate.orm.tooling</groupId>
    <artifactId>hibernate-enhance-maven-plugin</artifactId>
    <version>${hibernate.version}</version>
    <executions>
        <execution>
            <configuration>
                <enableDirtyTracking>true</enableDirtyTracking>
            </configuration>
            <goals>
                <goal>enhance</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

---

# Hibernate weaving

* Dirty tracking
* Lazy init `@Basic`
* Enable association management

---

class: inverse, center, middle

# Basic lazy betöltés

---

# Basic lazy betöltés

* Weavinggel

```xml
<enableLazyInitialization>true</enableLazyInitialization>
```

```java
@Basic(fetch = FetchType.LAZY)
@Lob
private String description;
```

* Csak a `@Lob` értékkel együtt
* Helyette használható lazy `@OneToOne` kapcsolat

---

# EclipseLink

* Támogatja?

---

class: inverse, center, middle

# Id generálás

---

# Milyen a jó id?

* Egyedi, nem tartalmazhat null értéket
* Ne legyen beágyazott jelentése
  * Biztonsági szempontból is aggályos lehet
* Időben ne változzon
* Ne legyen összetett
* Elosztott rendszerben
  * UUID
* Egész adatbázisban egyedi?

---

# Id típusa

* Jelentése alapján
  * Surrogate key
  * Natural id
* Típus alapján
  * Egyszerű
    * Egész szám  
    * UUID
      * Nem javasolt, a mérete és az indexelése miatt nem olyan hatékony [Vlad]
      * Manuálisan nagyon nehezen kezelhető
  * Összetett

---

# Id generálás típusai

* Sequence
* Identity
* Table
* Auto (provider választhat)
* Performancia szempontjából
  * Válasszuk a sequence alapú generálást [Vlad]
  * Identity esetén a batch műveleteknél lassulás
  * Table esetén lockolni kell

---

# Hibernate ID generálás

* Sequence esetén a `org.hibernate.id.enhanced.SequenceStyleGenerator` generátort használja
* Optimizer nélkül minden azonosító lekéréséhez az adatbázishoz kell fordulni
* Javasolt: `pooled-lo`
  * Amennyiben kiosztja az `1` értéket, az azt jelenti, hogy lefoglalta a `[1, 1+5)` intervallumot,
    a következő kiosztott érték a `6`, ez lefoglalja a `[6, 6+5)`, stb.

```java
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "location_generator")
@GenericGenerator(
        name = "location_generator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
                @Parameter(name = "sequence_name", value = "location_sequence"),
                @Parameter(name = "initial_value", value = "1"),
                @Parameter(name = "increment_size", value = "5"),
                @Parameter(name = "optimizer", value = "pooled-lo")
        })
private Long id;
```

```sql
create sequence location_sequence start with 1 increment by 5
```

---

# Natural id

* Hibernate kiegészítés

```java
@Entity
public class Book {

	@Id
	private Long id;

	@NaturalId
	private String isbn;

}
```

```java
Book book = entityManager
	.unwrap(Session.class)
	.byNaturalId( Book.class )
	.using( "isbn", "978-9730228236" )
	.load();
```

---

class: inverse, center, middle

# Kapcsolatok

---

# Irány (direction)

* Egyirányú (unidirectional)
* Kétirányú (bidirectional)
  * Két egyirányú kapcsolat, összekötve
* Most frequent direction of traversal

---

# Számosság (cardinality)

* One
* Many
  * Java Collection

---

# Kötelezőség (ordinality)

* Mandatory
* Optional

---

# Mapping

* Egy-egy, `@OneToOne`
* Egy-több, `@OneToMany`
* Több-egy, `@ManyToOne`
* Több-több, `@ManyToMany`

---

# Egyirányú one-to-one

* Külső kulcs, owner ahol az annotáció van
* Owner oldal, ahol az annotáció

---

# Kétirányú one-to-one

* `mappedBy` az inverz oldalra
* Owner, mely a kapcsolatot vezérli
* Ez jelzi, hogy nem ő vezérel, ez csak a másik irány
* Használjunk beállító metódusokat
  * Hibernate weaving - enable association management 

---

# Egyirányú many-to-one

* Külső kulcs, join column
* Owning side, owner
* Külső kulcs, owner a many oldalon, ahol az annotáció van
* `@JoinColumn` annotációval személyre szabható
* Performancia szempontjából a leghatékonyabb [Vlad]

---

# Egyirányú one-to-many

* Az owning side-hoz tartozó tábla nem tud több referenciát tartalmazni a másik táblára
* Join table
* JPA 2.0-től `@JoinColumn`
* Amennyiben tudjuk, kerüljük [Vlad]
* Beszúráskor több SQL:

```sql
INSERT INTO EMPLOYEE (CV, EMP_NAME) VALUES (?, ?);
INSERT INTO PHONE (PHONE_NUMBER, PHONE_TYPE) VALUES (?, ?)
UPDATE PHONE SET EMPLOYEE_ID = ? WHERE (ID = ?)
```

---

# Kétirányú one-to-many

* one-to-many és many-to-one
* many-to-one az owning, ott a join column
* one-to-many az inverse side, `mappedBy` használandó
* Kapcsolatot az owner side vezérli
* Használjunk beállító metódusokat (`add()`, `remove()`)
* Akkor használjuk, ha kevés gyermek rekord van [Vlad]
* Attól, hogy szülő-gyermek kapcsolat, még nem biztos, hogy szükség van a kollekcióra,
    inkább használjunk egyirányú many-to-one-t [Vlad]


---

# Many-to-many

* Join table
* Kerüljük, inkább két many-to-one kapcsolatot használjunk [Vlad]

---

# `getReference` metódus

* A `find()` metódus speciális formája
* Kapcsolatnál akarjuk az entitást használni, és ismerjük az id-ját
* Nem kell a teljes entitást betölteni
* Proxy, csak az elsődleges kulcsa lesz felhasználva külső kulcsként
* Shared cache esetén a `find()` elegendő

---

class: inverse, center, middle



# Lazy műveletek

---

# Lazy műveletek

* Performancia
* Nem minden kapcsolatot akarunk betölteni
* Újboli adatbázishoz fordulás több erőforrást használhat
* Default
  * Single value esetén eager (one-to-one, one-to-many)
  * Collection value esetén lazy (one-to-many, many-to-many)
* Felülbírálható
  * `fetch` attribútum `FetchType.EAGER`, `FetchType.LAZY`
* Proxy object

---

# EclipseLink megoldás

* Weaving
* Bájtkód manipuláció
* Típusai
  * Statikus - build time, [eclipselink-staticweave-maven-plugin](https://github.com/craigday/eclipselink-staticweave-maven-plugin)
  * Dynamic weaving - agent, classloading time

---

# Statikus weaving Mavennel

```xml
<plugin>
    <groupId>de.empulse.eclipselink</groupId>
    <artifactId>staticweave-maven-plugin</artifactId>
    <version>1.0.0</version>
    <executions>
        <execution>
            <phase>process-classes</phase>
            <goals>
                <goal>weave</goal>
            </goals>
            <configuration>
                <persistenceXMLLocation>META-INF/persistence.xml</persistenceXMLLocation>
                <logLevel>ALL</logLevel>
            </configuration>
        </execution>
    </executions>
    <dependencies>
      <!-- org.eclipse.persistence:org.eclipse.persistence.jpa függőség -->
    </dependencies>
</plugin>
```

---

# Dinamikus weaving

* Java agent
* `-javaagent` kapcsoló

---

# EclipseLink weaving

* Lazy loading
* Change tracking
* Fetch groups
* Belső optimalizálások

---

# Lazy betöltése

* getter hívása, szükséges nyitott persistence context?
  * Hibernate esetén: `LazyInitializationException`
  * EclipseLink esetén persistence contextet nyit
* Hogyan ellenőrizhető?
  * `PersistenceUtil.isLoaded(Object entity, String attributeName)`
  * `org.hibernate.Hibernate.isInitialized(..)`
  * `org.eclipse.persistence.indirection.IndirectList` `isInstantiated()` metódus

---

# N + 1 probléma

* Getter hívás esetén
* `FetchType.EAGER` esetén
* One-to-many, N + 1 lekérdezés

---

# join fetch

* JPQL `join fetch`
* `distinct` használata

---

# Több one-to-many kapcsolat

* fetch join esetén
* descartes szorzat
* Megoldás: több select, építve a first level cache-re

---

# Entity graph

* JPA 2.1 bevezetésével
* Explicit megadása
  * Annotációkkal
  * `EntityGraph` API
* Default entity graph
  * Összes eager mezőkből összeálló gráf

---

# Entity graph hint

* `javax.persistence.fetchgraph` csak az explicit gráfban megadott mezőket
* `javax.persistence.loadgraph` csak az explicit gráfban megadott, és a default gráfban mezőket
* Hint, mert további mezőket is betölthet (pl. id és version mezőket mindig betölti)

---

# Kaszkád műveletek

* Entitáson végzett műveletek kiterjesztése a kapcsolatban álló entitásokra
* `javax.persistence.CascadeType` `PERSIST`, `MERGE`, `REMOVE`, `REFRESH`, `ALL`

---

# Törlés

* A kapcsolódó entitás null értékre állítása, vagy collection-ből remove
* A kapcsolat, kapcsolódó entitás kezelése a fejlesztő feladata
* Kivétel ez alól a kaszkádolt műveletek (merge, remove)

---

# Orphan removal

* One-to-one vagy one-to-many esetén, ha az owner oldal, vagy a kapcsolat törlésre kerül
* Owner kerül törlésre: cascade
* Kapcsolat törlése: `orphanRemoval = true`
* Pl. magazin adatbázisban törlésre kerül, ha `library.getMagazines().remove(magazine);`

---

class: inverse, center, middle

# @MapsId

---

# @MapsId

* JPA 2.0 vezette be a derived id fogalmát
* `@OneToOne` és `@OneToOne` annotáció esetén `@MapsId` annotáció használata
* Megosztja az id-t, azaz a owner side kulcsa egyben külső kulcs is
* Performancia szempontjából hatékonyabb [Vlad]

---
class: inverse, center, middle

# Projection query

---

# Projection query

* Entity query-t csak módosításkor használjunk, lekérdezéskor projection query [Vlad]

---
class: inverse, center, middle
# Lapozás

---

# Lapozás

* `WARN  o.h.h.i.ast.QueryTranslatorImpl - HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!`
* Megoldás: külön select a szülőre, majd `IN` select a gyermekekkel együtt

---

class: inverse, center, middle

# Detach, merge

---

# Entitás életciklus

<img src="entity-lifecycle.gif" alt="Entitás életciklus" width="600"/>

---

# Detach/merge

* Detach: entitás nincs a persistence contexthez rendelve
  * Amikor a persistence context bezárásra kerül
  * entity manager `clear()` metódus
  * Explicit detach, entity manager `detache()`
* Merge: persistence contexthez rendelés
  * Újra példányosított, de létező entitás visszacsatolása
  * Detach-elt entitás visszacsatolása
  * Id alapján
  * Nem a paramétert csatolja vissza, hanem visszatér egy új példánnyal

---

class: inverse, center, middle

# Tranzakciókezelés

---

# Tranzakciókezelés tulajdonságai

* ACID tulajdonságok
* Izolációs problémák
  * Dirty read
  * Non-repeatable reads
  * Phantom reads
* Izolációs szintek
  * `READ UNCOMMITTED`
  * `READ COMMITTED`
  * `REPEATABLE READ`
  * `SERIALIZABLE`

---

# Elosztott tranzakciókezelés

* Two-phase commit
* Transaction coordinator and transactional
resources

---

# JPA tranzakciókezelés

* `RESOURCE_LOCAL`
  * JPA önmaga kezeli a tranzakciót
  * `EntityTransaction` interfészen keresztül programozott módon
* `JTA`
  * Delegálva a JTA-nak
  * Resource-ként részt vehet elosztott tranzakcióban
  * Deklaratív tranzakciókezelés
  * Conatiner-managed entity manager mindig ezt használja

---

# Persistence Context élettartama

* Tranzakciónyi (transaction-scoped persistence context)
* Tranzakciókon átívelő (extended persistence context)
  * `@PersistenceContext(type = PersistenceContextType.EXTENDED)` annotációval

---

# Change tracking

* `@ChangeTracking` annotációval
* Deferred Change Detection Policy (`ChangeTrackingType.DEFERRED`)
  * alapbeállítás, készít egy másolatot, és mentéskor összehasonlítja attribútumonként a mentendőt és a másolatot
* Object-Level Change Tracking Policy (`ChangeTrackingType.OBJECT`)
  * objektum szinten menti, hogy történt-e módosítás, és csak ekkor ellenőrzi
  * weaving, nem működik reflection esetén
  * sok módosult attribútum estén
* Attribute Change Tracking Policy (`ChangeTrackingType.ATTRIBUTE`)
  * sok attribútumból csak kevés módosul

---

# Query-k és a nem commitolt változások kapcsolata

* Lekérdezések SQL-t futtatnak
* Alapesetben a persistence provider gondoskodik a query előtt az adatbázisba szinkronizálásról
* Performancia problémák lehetségesek, hiszen feleslegesen akar flush-ölni
* `FlushModeType`
  * `AUTO` - persistence providerre bízva
  * `COMMIT` - nem kell a persistence providernek semmit sem tennie

---

# flush() metódus

* Ugyanazon mechanizmus hívása, ami commitnál is hívódik
* Ha `FlushModeType.COMMIT` esetén akarok flush-t
* Ha native query-t akarok utána

---

class: inverse, center, middle



# Tömeges műveletek

---

# Tömeges (bulk) műveletek

* Átugorják a persistence contextet, direkt az adatbázison hajtódik végre
* Saját tranzakciója legyen, vagy a tranzakció elején fusson
* Shared cache megfelelő részét invalidálja a provider (performancia problémák jelentkezhetnek)
* Nem követi a kaszkádolt kapcsolatokat (bulk művelettel kell törölni azokat is, pl. `IN` klauza)
* Probléma a lockkal és verziózással

---

class: inverse, center, middle



# Query hintek

---

# Query hintek

* Szöveges név, objektum érték
* Új funkciók bevezetése új API nélkül
* Két fajtája
  * Szabványos, pl. query timeout
  * Gyártófüggő

---

class: inverse, center, middle



# Refresh

---

# Refresh

* Ha nem az entity manageren keresztül változott az adatbázis
* Hosszan megnyitott persistence contextnél érdekes
* `refresh()` metódus
* Managednek kell lennie az entitásnak
* Addigi változtatások elvesznek

---

class: inverse, center, middle



# Lock

---

# Lock

* Adatok konzisztenciájának megtartása
* Konkurrens hozzáférés
* Később jön, nyer

---

# Optimistic locking

* Olvasásnál nem biztos, hogy visszaírásra kerül, vagy ritka konkurrens módosítás
* Íráskor kell ellenőrizni, hogy történt-e az entitáson módosítás
  * Ha igen, rollback, és `OptimisticLockException`
  * Egész v. timestamp (lehetőleg egészt használjunk)
* Nagy terhelés esetén sok rollback
* `@Version` annotációval ellátott mező
  * Módosításkor ez is módosul
  * Nem minden provider követeli meg (saját cache vagy speciális SQL)

---

# Advanced optimistic locking

* `LockModeType`
  * `OPTIMISTIC` - optimistic read lock (régen `READ`)
  * `OPTIMISTIC_FORCE_INCREMENT` - optimistic write lock (régen `WRITE`)

---

# Pessimistic locking

* JPA 2.0 vezette be
* Az adott entitást szinkron módon zárolja
* Javasolt gyakori módosításkor
* `LockModeType`
  * `PESSIMISTIC_READ`
  * `PESSIMISTIC_WRITE`
  * `PESSIMISTIC_FORCE_INCREMENT`

---

# Locking API

* `EntityManager`
  * `lock(Object entity, LockModeType lockMode)`
  * `find(Class entityType,Object id, LockModeType lockMode)`
  * `refresh(Object entity, LockModeType lockMode)`
* `Query.setLockMode(LockModeType lockMode)`
* `javax.persistence.lock.timeout` legtöbb provider támogatja

---

class: inverse, center, middle



# Cache

---

# Cache szintjei

* Kétszintű
  * Persistence context, Entity manager szinten (first level)
  * Shared cached, Entity manager factory szinten (nem a legjobb elnevezés a second level)

---

# Shared cache

* `Cache` interface
  * `contains()`
  * evolve metódusok
* `EntityManagerFactory.getCache()`

---

# Deklaratív cache

* Persistence unit szinten
* Entitás szinten
* `javax.persistence.sharedCache.mode`
  * `NOT_SPECIFIED` - persistence provider dönt
  * `ALL`
  * `NONE`
  * `DISABLE_SELECTIVE`, `@Cacheable(false)`
  * `ENABLE_SELECTIVE`, `@Cacheable(true)`
---

# Dynamic cache management

* `find()` metódus vagy query hint (shared cache-re van hatással, nem a persistence contextre)
* `javax.persistence.cache.retrieveMode` és `javax.persistence.cache.storeMode` paraméter
* `CacheRetrieveMode` és `CacheStoreMode`

---

# Típusok

* `CacheRetrieveMode`
  * `USE` cache-ből olvas
  * `BYPASS` cache megkerülésével mindig adatbázisból olvas
* `CacheStoreMode`
  * `USE` cache-be beteszi a felolvasott entitásokat
  * `BYPASS` nem teszi cache-be a felolvasott entitásokat
  * `REFRESH` mindig frissíti a cache-t - ha az adatbázist más is írja
* `refresh()` metódusnál is használható a `CacheStoreMode.REFRESH`

---

# Best practices

* Alkalmazás ne módosítsa a cache-t explicit módon
* Cache törlése pl. automatizált tesztelésnél
* Több kliens módosítja az adatbázist
  * Nem jó megoldás a cache kikapcsolása
  * Helyette: lock, refresh

---

class: inverse, center, middle



# Entitások verziózása

---

# Entitások verziózása

* History vagy time variant data
* Adatbázis szinten pl. triggerekkel
* Entity listener
* Hibernate Envers
* EclipseLink: Historical Sessions
* Javasolt megoldás:
  * Nem hisztorizált fej
  * Hisztorizált gyermek entitások (együtt változó attribútumok egyben)

---

class: inverse, center, middle



# SQL lekérdezések

---

# SQL lekérdezések

* Native query
* Adatbázis által nyújtott plusz funkcionalitás kihasználása
* Általa visszaadott entitások managed státuszúak
  * Vigyázni kell, hogy minden mezőt kérdezzünk le, különben üresen kerül vissza

---

class: inverse, center, middle



# Legjobb gyakorlatok

---

# Séma generálás

* Ne a JPA provider generálja a sémákat
* Flyway, Liquibase

---

# Séma verziózás eszköz tulajdonságok

  * SQL/XML leírás
  * Platform függetlenség
  * Lightweight
  * Visszaállás korábbi verzióra
  * Indítás paranccssorból, alkalmazásból
  * Cluster támogatás
  * Placeholder támogatás
  * Modularizáció
  * Több séma támogatása
  * Metadata table alapján  

---

# Query best practices 1.

* Named query-k használata
  * Precompile, indításkor, gyorsabb
  * XML-ben felülbírálhatóak akár native query-vel
* Paraméterek használata
  * Gyors, biztonságos
* Report query
  * Tranzakción kívül
  * Csak amire szükség van: projection query

---

# Query best practices 2.

* Nem kell félni a gyártófüggő hintektől
* Folyamatosan figyelni a generált SQL utasításokat
* Lapozás

---

# Staging

* Native query-vel létrehozhatóak olyan entitások, melyek más táblához vannak rendelve
* Temporális táblából adatbetöltés

---

class: inverse, center, middle



# EclipseLink specialitások

---

# Read-only query

* `eclipselink.read-only` hint
* A visszaadott entitások nem managed státuszúak, nincs change tracking
* Pl. migrációnál, riportnál

---

# Join fetch

* Kapcsolt entitásokat egy query-ben kéri le
* `@JoinFetch` annotáció
* `eclipselink.join-fetch` hint

```java
@Entity public class Employee implements Serializable {

  ...

  @OneToMany(cascade=ALL, mappedBy="owner")
  @JoinFetch(value=OUTER)
  public Collection<Employee> getManagedEmployees() {
    return managedEmployees;
  }

  ...
}
```

---

# Batch reading

* `eclipselink.batch`
* A kapcsolt entitásokat külön query-ben kéri le
* Nem joinolja be az egész eredeti entitást, azt külön kéri le, így nincs adatduplikáció
* Típusai:
  * `JOIN` - csak az id az eredeti entitásból
  * `EXISTS` - allekérdezéssel
  * `IN` - id-k in paraméterként

[Batch fetching - optimizing object graph loading ](http://java-persistence-performance.blogspot.hu/2010/08/batch-fetching-optimizing-object-graph.html)

---

# Fetch size

* `eclipselink.jdbc.fetch-size` hint
* Egyszerre mennyi sort hozzon át a hálózaton
* Sok sor esetén
* Mindkét irányban eltérő sorok száma esetén a beállítása teljesítménycsökkenést okozhat

---

# Batch writing

* `eclipselink.jdbc.batch-writing` pu property
* Értékként csak a `JDBC` használatos
* `eclipselink.jdbc.batch-writing.size`, alapértelmezetten 100
* Paraméterezett SQL
* Előkészíti az insert és update műveleteket, utána egyben küldi

[Batch Writing, and Dynamic vs Parametrized SQL, how well does your database perform?](http://java-persistence-performance.blogspot.hu/2013/05/batch-writing-and-dynamic-vs.html)

---

# Query cache

* Shared cache csak id alapján tud lekérdezni a cache-ből
* A lekérdezés visszatérési értékét is tudja cache-elni
* `eclipselink.query-results-cache` query hint

---

# Esettanulmány

[How to improve JPA performance by 1,825%](http://java-persistence-performance.blogspot.hu/2011/06/how-to-improve-jpa-performance-by-1825.html)
