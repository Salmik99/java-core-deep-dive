# Module 02. equals / hashCode

## 1. Зачем нужны equals и hashCode

В Java у каждого объекта есть методы, унаследованные от `Object`:

```java
public boolean equals(Object obj)
public int hashCode()
```

`equals()` отвечает за логическое сравнение объектов.

`hashCode()` нужен для hash-based коллекций:

- `HashMap`
- `HashSet`
- `LinkedHashMap`
- `LinkedHashSet`
- `ConcurrentHashMap`

Если эти методы реализованы неправильно, могут ломаться:

- поиск в `HashMap`;
- удаление из `HashSet`;
- проверка `contains`;
- уникальность объектов;
- кеши;
- бизнес-логика сравнения DTO;
- поведение entity в коллекциях.

---

## 2. `==` и `equals()`

Оператор `==` для объектов сравнивает ссылки.

Он отвечает на вопрос:

> Эти две переменные указывают на один и тот же объект в памяти?

Пример:

```java
User first = new User("Talgat");
User second = new User("Talgat");

System.out.println(first == second); // false
```

Хотя данные одинаковые, объекты разные.

Если же переменные ссылаются на один объект:

```java
User first = new User("Talgat");
User second = first;

System.out.println(first == second); // true
```

`equals()` должен отвечать на другой вопрос:

> Считаются ли эти два объекта логически равными?

Например:

```java
Client first = new Client("123456789012");
Client second = new Client("123456789012");

first == second        // false
first.equals(second)   // может быть true, если equals реализован по iin
```

---

## 3. Как работает `Object.equals()`

Если класс не переопределяет `equals()`, используется реализация из `Object`.

Упрощённо она выглядит так:

```java
public boolean equals(Object obj) {
    return this == obj;
}
```

То есть по умолчанию `equals()` работает так же, как `==`.

Пример:

```java
public class User {
    private String name;

    public User(String name) {
        this.name = name;
    }
}
```

```java
User first = new User("Talgat");
User second = new User("Talgat");

System.out.println(first.equals(second)); // false
```

Почему `false`?

Потому что `User` не переопределил `equals()`, значит используется сравнение ссылок.

---

## 4. Когда нужно переопределять equals

`equals()` нужно переопределять, если для класса существует понятие логического равенства.

Например:

```java
public final class Client {
    private final String iin;

    public Client(String iin) {
        this.iin = iin;
    }
}
```

Если в бизнесе два клиента с одинаковым `iin` считаются одним и тем же клиентом, тогда логично реализовать `equals()` по `iin`.

Но не каждый класс обязан переопределять `equals()`.

Например, сервисные классы обычно не сравниваются по состоянию:

```java
public class PaymentService {
    public void pay() {
        // business logic
    }
}
```

Для такого класса `equals()` обычно не нужен.

---

## 5. Контракт equals

Метод `equals()` должен соблюдать контракт.

### 5.1 Reflexive

Объект должен быть равен самому себе.

```java
x.equals(x) == true
```

### 5.2 Symmetric

Если `x.equals(y)` возвращает `true`, то `y.equals(x)` тоже должен возвращать `true`.

```java
x.equals(y) == true
y.equals(x) == true
```

### 5.3 Transitive

Если `x.equals(y)` и `y.equals(z)`, тогда `x.equals(z)` тоже должен быть `true`.

```java
x.equals(y) == true
y.equals(z) == true
x.equals(z) == true
```

### 5.4 Consistent

Повторные вызовы `equals()` должны возвращать один и тот же результат, пока данные объектов не изменились.

### 5.5 Null comparison

Любой объект при сравнении с `null` должен возвращать `false`.

```java
x.equals(null) == false
```

Не должно быть `NullPointerException`.

---

## 6. Правильная структура equals/hashCode

Типичная реализация:

```java
import java.util.Objects;

public final class Client {

    private final String iin;

    public Client(String iin) {
        this.iin = iin;
    }

    public String getIin() {
        return iin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Client client)) return false;

        return Objects.equals(iin, client.iin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iin);
    }
}
```

Разбор:

```java
if (this == o) return true;
```

Если это один и тот же объект, дальше проверять не нужно.

```java
if (!(o instanceof Client client)) return false;
```

Если объект другого типа, он не равен текущему.

```java
return Objects.equals(iin, client.iin);
```

Сравниваем значимое поле.

`Objects.equals()` безопасен для `null`.

---

## 7. `instanceof` vs `getClass()` в equals

Есть два популярных способа проверить тип в `equals()`.

### Вариант 1. `instanceof`

```java
if (!(o instanceof Client client)) return false;
```

Этот вариант допускает сравнение с наследниками.

### Вариант 2. `getClass()`

```java
if (o == null || getClass() != o.getClass()) return false;
Client client = (Client) o;
```

Этот вариант требует, чтобы классы совпадали строго.

Если класс `final`, разницы почти нет, потому что наследников быть не может.

```java
public final class Client {
}
```

Для `final` value object обычно удобно использовать `instanceof`.

Если класс не `final` и может иметь наследников, нужно быть осторожнее.

Пример проблемы:

```java
class Person {
    private String iin;
}

class Employee extends Person {
    private String employeeNumber;
}
```

Если `Person` сравнивается только по `iin`, а `Employee` сравнивается по `iin` и `employeeNumber`, можно получить нарушение symmetry:

```java
person.equals(employee) // true
employee.equals(person) // false
```

Такой `equals()` неправильный.

---

## 8. Что такое hashCode

`hashCode()` возвращает число типа `int`.

```java
public int hashCode()
```

Это число используется hash-based коллекциями, чтобы быстрее находить объект.

Пример:

```java
Map<Client, String> clients = new HashMap<>();

clients.put(new Client("123"), "Talgat");
clients.get(new Client("123"));
```

Чтобы `get()` нашёл значение, новый объект `Client("123")` должен:

1. иметь такой же `hashCode()`;
2. быть равным через `equals()`.

---

## 9. Контракт hashCode

Главное правило:

> Если два объекта равны через `equals()`, то у них должен быть одинаковый `hashCode()`.

```java
if (a.equals(b)) {
    a.hashCode() == b.hashCode(); // должно быть true
}
```

Но обратное не обязательно.

Если у двух объектов одинаковый `hashCode()`, это не значит, что они равны.

```java
a.hashCode() == b.hashCode()
```

не гарантирует:

```java
a.equals(b) == true
```

Одинаковый hashCode у разных объектов называется hash collision.

---

## 10. Почему equals и hashCode нужно переопределять вместе

Плохой пример:

```java
public final class Client {

    private final String iin;

    public Client(String iin) {
        this.iin = iin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client client)) return false;
        return Objects.equals(iin, client.iin);
    }

    // hashCode не переопределён
}
```

Проблема:

```java
Set<Client> clients = new HashSet<>();

clients.add(new Client("123"));
clients.add(new Client("123"));

System.out.println(clients.size());
```

Логически мы ожидаем:

```text
1
```

Но можем получить:

```text
2
```

Почему?

Потому что `equals()` говорит, что объекты равны, но `hashCode()` остаётся из `Object`.

А `Object.hashCode()` обычно связан с identity объекта.

Два разных объекта могут иметь разные hashCode, даже если по `equals()` они равны.

Для `HashSet` это означает: объекты могут попасть в разные buckets, и коллекция не поймёт, что это дубликаты.

Правильно:

```java
@Override
public int hashCode() {
    return Objects.hash(iin);
}
```

---

## 11. Как HashMap использует hashCode и equals

Упрощённо `HashMap` работает так.

Когда мы делаем:

```java
map.put(key, value);
```

`HashMap`:

1. вызывает `key.hashCode()`;
2. по hash определяет bucket;
3. внутри bucket проверяет ключи через `equals()`;
4. если равный ключ найден — обновляет значение;
5. если не найден — добавляет новую entry.

Когда мы делаем:

```java
map.get(key);
```

`HashMap`:

1. снова вызывает `key.hashCode()`;
2. идёт в нужный bucket;
3. внутри bucket ищет ключ через `equals()`;
4. если находит — возвращает value;
5. если не находит — возвращает `null`.

Поэтому для ключей в `HashMap` критично важно, чтобы `hashCode()` и `equals()` были стабильными и согласованными.

---

## 12. Hash collision

Hash collision — ситуация, когда два разных объекта имеют одинаковый hashCode.

Пример:

```java
Client a = new Client("123");
Client b = new Client("456");
```

Теоретически:

```java
a.hashCode() == b.hashCode()
```

может быть `true`.

Это не ошибка.

`HashMap` умеет работать с коллизиями.

Если два объекта попали в один bucket, `HashMap` дополнительно использует `equals()`.

Поэтому `hashCode()` не обязан быть уникальным.

Но хороший `hashCode()` должен распределять объекты более-менее равномерно.

---

## 13. Mutable key — опасная ошибка

Очень опасно использовать изменяемый объект как ключ в `HashMap`, если изменяемое поле участвует в `equals()` и `hashCode()`.

Пример:

```java
public class ClientKey {

    private String iin;

    public ClientKey(String iin) {
        this.iin = iin;
    }

    public void setIin(String iin) {
        this.iin = iin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientKey clientKey)) return false;
        return Objects.equals(iin, clientKey.iin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iin);
    }
}
```

Использование:

```java
Map<ClientKey, String> map = new HashMap<>();

ClientKey key = new ClientKey("123");

map.put(key, "Talgat");

key.setIin("456");

System.out.println(map.get(key)); // может быть null
```

Почему так происходит?

Когда объект добавили в `HashMap`, он попал в bucket на основе hash от `"123"`.

После изменения `iin` hashCode стал другим.

Теперь `HashMap` ищет объект уже в другом bucket.

Физически объект лежит в старом bucket, а поиск идёт по новому hash.

Итог: объект как будто потерялся внутри `HashMap`.

Правило:

> Поля, участвующие в `equals()` и `hashCode()`, не должны меняться, пока объект используется как ключ в hash-based коллекции.

Лучшее решение — делать ключи immutable.

---

## 14. Value Object

Value Object — объект, который определяется своими значениями.

Примеры:

```text
Money
Email
PhoneNumber
AccountNumber
Iin
```

Для value object обычно хорошо подходят:

```text
final class
private final fields
constructor validation
equals/hashCode по всем значимым полям
no setters
```

Пример:

```java
public final class AccountNumber {

    private final String value;

    public AccountNumber(String value) {
        this.value = Objects.requireNonNull(value, "value must not be null");
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountNumber that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
```

Два `AccountNumber` с одинаковым value считаются равными:

```java
new AccountNumber("KZ123").equals(new AccountNumber("KZ123")) // true
```

---

## 15. DTO equality

DTO — объект передачи данных.

Например:

```java
public class ClientDto {
    private String iin;
    private String fullName;
}
```

Для DTO есть несколько вариантов.

### Вариант 1. Не переопределять equals/hashCode

Это нормально, если DTO не используется:

- как ключ в `HashMap`;
- в `HashSet`;
- для сравнения логической уникальности;
- в тестах через object equality.

### Вариант 2. Сравнивать по всем полям

Подходит, если DTO — просто snapshot данных.

```text
ClientDto(iin=123, fullName=Talgat)
```

равен другому DTO только если все значимые поля совпадают.

### Вариант 3. Сравнивать по business key

Например, если в контексте задачи клиент определяется по `iin`, можно сравнивать только по `iin`.

Но это решение должно быть осознанным.

Если `fullName` не участвует в `equals()`, то такие объекты будут равны:

```java
ClientDto first = new ClientDto("123", "Talgat Lukpanov");
ClientDto second = new ClientDto("123", "Talgat L.");

first.equals(second); // true, если equals только по iin
```

Это может быть правильно для бизнес-идентичности, но неправильно для сравнения полного содержимого DTO.

---

## 16. Entity equality

С Entity сложнее, чем с DTO или Value Object.

Например:

```java
@Entity
public class ClientEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String iin;
}
```

Проблемы:

1. `id` может быть `null` до сохранения в БД.
2. После сохранения `id` появляется.
3. Hibernate может использовать proxy.
4. Entity обычно mutable.
5. Entity может находиться внутри persistence context.
6. Entity может менять состояние во время жизненного цикла.

---

## 17. Почему equals по id может быть опасен

Пример:

```java
ClientEntity first = new ClientEntity();
ClientEntity second = new ClientEntity();

first.getId();  // null
second.getId(); // null
```

Если написать equals только по `id`, можно случайно получить ситуацию, где две новые entity считаются равными, потому что у обеих `id == null`.

Это неправильно.

Две новые entity без id обычно не должны автоматически считаться равными.

---

## 18. Почему equals по mutable business field тоже опасен

Допустим, entity сравнивается по `iin`.

```java
public class ClientEntity {
    private Long id;
    private String iin;
}
```

Если `iin` можно изменить, а entity уже лежит в `HashSet`, возникнет та же проблема, что с mutable key.

```java
Set<ClientEntity> clients = new HashSet<>();

ClientEntity client = new ClientEntity();
client.setIin("123");

clients.add(client);

client.setIin("456");

clients.contains(client); // может быть false
```

Если поле участвует в `hashCode()`, оно должно быть стабильным.

---

## 19. Общие подходы к Entity equality

Универсального ответа нет.

Часто используют один из подходов.

### Подход 1. Не переопределять equals/hashCode у Entity

Тогда остаётся identity comparison из `Object`.

Плюс: меньше риска сломать hash-based коллекции.

Минус: разные объекты, представляющие одну строку в БД, не будут равны через `equals()`.

### Подход 2. Сравнение по id, если id не null

Идея:

```java
if (id == null || other.id == null) {
    return false;
}

return Objects.equals(id, other.id);
```

Но нужно аккуратно реализовать `hashCode()`.

### Подход 3. Сравнение по стабильному business key

Например, `iin`, если он:

- обязателен;
- уникален;
- не меняется после создания;
- действительно определяет identity.

Тогда можно сравнивать entity по `iin`.

Но если business key mutable, это опасно.

---

## 20. Практическое правило для backend

### Value Object

Например:

```text
AccountNumber
Money
Iin
Email
PhoneNumber
```

Обычно:

```text
immutable
equals/hashCode по значимым полям
можно использовать как ключ в Map
```

### DTO

Например:

```text
ClientResponse
CreateClientRequest
PaymentDto
```

Зависит от задачи.

DTO не всегда обязан иметь `equals/hashCode`.

Если нужен — нужно понять, что считается равенством:

```text
по всем полям
или по business key
```

### Entity

Например:

```text
ClientEntity
AccountEntity
TransactionEntity
```

С Entity нужно быть осторожным.

Не стоит автоматически генерировать `equals/hashCode` по всем полям через IDE или Lombok.

Особенно опасно включать:

```java
@OneToMany
@ManyToOne
@ManyToMany
```

и другие связи.

Это может привести к:

- рекурсии;
- ленивым загрузкам;
- проблемам с Hibernate proxy;
- нестабильному hashCode;
- плохой производительности.

---

## 21. Lombok и equals/hashCode

Lombok может сгенерировать equals/hashCode автоматически:

```java
@EqualsAndHashCode
public class Client {
    private String iin;
    private String fullName;
}
```

Или через `@Data`:

```java
@Data
public class Client {
    private String iin;
    private String fullName;
}
```

Но с `@Data` нужно быть осторожным.

`@Data` генерирует:

- getters;
- setters;
- toString;
- equals;
- hashCode;
- required args constructor.

Для DTO это иногда удобно.

Для Entity часто опасно.

Почему?

Потому что Lombok может включить в `equals/hashCode` поля, которые не должны участвовать в сравнении:

- mutable fields;
- collections;
- relations;
- lazy-loaded associations.

Для Entity лучше явно контролировать, какие поля участвуют в equality.

---

## 22. Records и equals/hashCode

В Java record автоматически генерирует:

- constructor;
- accessors;
- equals;
- hashCode;
- toString.

Пример:

```java
public record AccountNumber(String value) {
}
```

Два record равны, если равны все компоненты record.

```java
AccountNumber first = new AccountNumber("KZ123");
AccountNumber second = new AccountNumber("KZ123");

first.equals(second); // true
```

Record хорошо подходит для простых immutable DTO и value objects.

Но нужно помнить:

```text
record сравнивает все компоненты
```

Если нужно сравнение только по одному business key, record может быть не лучшим вариантом или нужно очень аккуратно проектировать его состав.

---

## 23. Типичные ошибки

### Ошибка 1. Переопределили equals, но забыли hashCode

```java
@Override
public boolean equals(Object o) {
    // custom logic
}
```

Но нет:

```java
@Override
public int hashCode() {
    // custom logic
}
```

Результат: проблемы в `HashMap` и `HashSet`.

### Ошибка 2. Включили mutable field в hashCode

```java
private String status;
```

Если `status` меняется, а участвует в `hashCode()`, объект опасно использовать в hash-based коллекциях.

### Ошибка 3. Сравнение строк через ==

Плохо:

```java
if (client.getIin() == "123") {
}
```

Правильно:

```java
if ("123".equals(client.getIin())) {
}
```

Или:

```java
Objects.equals(client.getIin(), "123")
```

### Ошибка 4. Автоматически сгенерированный equals/hashCode у Entity

Особенно через Lombok `@Data`.

Для Entity это может быть опасно.

### Ошибка 5. Нарушение symmetry при наследовании

Если родитель и наследник сравниваются по разным правилам, можно получить:

```java
parent.equals(child) == true
child.equals(parent) == false
```

Такой `equals()` неправильный.

---

## 24. Итог

Главные идеи:

1. `==` сравнивает ссылки.
2. `equals()` сравнивает логическое равенство.
3. По умолчанию `Object.equals()` работает как `==`.
4. Если переопределяешь `equals()`, почти всегда нужно переопределять `hashCode()`.
5. Если `a.equals(b) == true`, то `a.hashCode() == b.hashCode()` тоже должен быть true.
6. Одинаковый `hashCode()` не гарантирует `equals()`.
7. `HashMap` сначала использует `hashCode()`, потом `equals()`.
8. Mutable key в `HashMap` опасен.
9. Value Object обычно должен быть immutable и иметь equals/hashCode по значимым полям.
10. DTO equality зависит от задачи.
11. Entity equality — сложная тема, особенно с JPA/Hibernate.
12. Lombok `@Data` на Entity часто опасен.
13. Record автоматически генерирует equals/hashCode по всем компонентам.
