# Module 01. Object Model

## Цель модуля

Разобраться, что в Java реально хранит переменная объектного типа, как работают ссылки, почему Java является pass-by-value языком, чем отличаются mutable и immutable объекты, и зачем нужны defensive copy.

После этого модуля должно быть понятно:

- почему `user2 = user1` не создаёт новый объект;
- что именно сравнивает `==` у объектов;
- почему метод может изменить объект, но не может заменить ссылку у вызывающего кода;
- где появляется проблема общего изменяемого состояния;
- как immutable-классы защищают свои данные.

## 1. Объект и ссылка

В Java переменная объектного типа хранит не сам объект, а ссылку на объект.

```java
MutableUser user1 = new MutableUser("Talgat", List.of("USER"));
MutableUser user2 = user1;
```

Что происходит:

- в heap создаётся объект `MutableUser`;
- в переменную `user1` записывается ссылка на этот объект;
- при `user2 = user1` копируется ссылка, а не объект;
- `user1` и `user2` указывают на один и тот же объект.

Поэтому изменение через одну переменную видно через другую:

```java
MutableUser user1 = new MutableUser("Talgat", List.of("USER"));
MutableUser user2 = user1;

user2.setName("Arman");

System.out.println(user1.getName()); // Arman
System.out.println(user2.getName()); // Arman
```

Главная мысль: переменная объектного типа не является объектом. Это ссылка, через которую можно обратиться к объекту.

## 2. `==` для объектов

Оператор `==` у объектов сравнивает ссылки.

Он отвечает на вопрос: эти две переменные указывают на один и тот же объект?

```java
MutableUser user1 = new MutableUser("Talgat", List.of("USER"));
MutableUser user2 = new MutableUser("Talgat", List.of("USER"));

System.out.println(user1 == user2); // false
```

Данные внутри одинаковые, но объектов два. Поэтому ссылки разные.

```java
MutableUser user1 = new MutableUser("Talgat", List.of("USER"));
MutableUser user2 = user1;

System.out.println(user1 == user2); // true
```

Здесь объект один, а переменных две. Поэтому ссылки одинаковые.

Важно не путать:

- `==` проверяет, один ли это объект;
- `equals()` проверяет логическое равенство, если класс правильно переопределил этот метод.

`equals()` и `hashCode()` подробно разбираются в следующем модуле.

## 3. Java всегда pass-by-value

Java всегда передаёт параметры в методы по значению.

Для примитивов копируется само значение:

```java
int x = 10;
```

Если передать `x` в метод, метод получит копию значения `10`.

Для объектов тоже копируется значение. Но значение переменной объектного типа - это ссылка.

```java
MutableUser user = new MutableUser("Talgat", List.of("USER"));

changeName(user);
```

Метод получает копию ссылки на тот же объект:

```java
public void changeName(MutableUser user) {
    user.setName("Arman");
}
```

Метод может изменить объект через полученную ссылку:

```java
MutableUser user = new MutableUser("Talgat", List.of("USER"));

changeName(user);

System.out.println(user.getName()); // Arman
```

Но метод не получает доступ к самой переменной вызывающего кода. Он получает только копию её значения.

## 4. Почему метод не может заменить ссылку вызывающего кода

Пример:

```java
public void reassignMutableUser(MutableUser user) {
    user = new MutableUser("Dias", List.of("ADMIN"));
    user.setName("Nurlan");
}
```

Параметр `user` внутри метода - это локальная переменная метода.

Когда выполняется строка:

```java
user = new MutableUser("Dias", List.of("ADMIN"));
```

меняется только локальная переменная `user` внутри метода. Переменная снаружи продолжает ссылаться на старый объект.

```java
MutableUser user = new MutableUser("Talgat", List.of("USER"));

reassignMutableUser(user);

System.out.println(user.getName()); // Talgat
```

Правило:

- метод может изменить объект, если получил ссылку на него;
- метод не может заменить ссылку, которая хранится в переменной вызывающего кода.

## 5. Mutable object

Mutable object - это объект, состояние которого можно изменить после создания.

```java
public class MutableUser {

    private String name;
    private List<String> roles;

    public void setName(String name) {
        this.name = name;
    }
}
```

Такой объект можно изменить:

```java
MutableUser user = new MutableUser("Talgat", List.of("USER"));

user.setName("Arman");
```

Mutable-объекты удобны, когда состояние действительно должно меняться. Но у них есть риск: разные части программы могут держать ссылку на один и тот же объект и менять его состояние неожиданно друг для друга.

## 6. Общее изменяемое состояние

Проблема появляется, когда объект сохраняет или отдаёт наружу изменяемую коллекцию.

```java
public class MutableUser {

    private String name;
    private List<String> roles;

    public MutableUser(String name, List<String> roles) {
        this.name = name;
        this.roles = roles;
    }

    public List<String> getRoles() {
        return roles;
    }
}
```

### Конструктор сохраняет внешнюю ссылку

```java
List<String> roles = new ArrayList<>();
roles.add("USER");

MutableUser user = new MutableUser("Talgat", roles);

roles.add("ADMIN");

System.out.println(user.getRoles()); // [USER, ADMIN]
```

Мы изменили внешний список `roles`, но вместе с ним изменилось состояние `user`.

### Getter отдаёт внутреннюю коллекцию

```java
MutableUser user = new MutableUser("Talgat", new ArrayList<>(List.of("USER")));

user.getRoles().add("ADMIN");

System.out.println(user.getRoles()); // [USER, ADMIN]
```

Внешний код получил прямой доступ к внутреннему списку объекта.

## 7. Immutable object

Immutable object - это объект, состояние которого нельзя изменить после создания.

```java
public final class ImmutableUser {

    private final String name;
    private final List<String> roles;

    public ImmutableUser(String name, List<String> roles) {
        this.name = name;
        this.roles = List.copyOf(roles);
    }

    public String getName() {
        return name;
    }

    public List<String> getRoles() {
        return roles;
    }
}
```

Признаки immutable-класса:

- класс `final`, чтобы наследник не мог сломать поведение;
- поля `private final`;
- нет сеттеров;
- в конструкторе делается defensive copy изменяемых входных данных;
- наружу не отдаётся изменяемое внутреннее состояние.

`List.copyOf(roles)` создаёт неизменяемую копию списка. Поэтому внешний код не может изменить состояние `ImmutableUser` через старую ссылку на список.

## 8. Defensive copy

Defensive copy - это защитное копирование.

Плохо:

```java
this.roles = roles;
```

Так объект сохраняет чужую изменяемую ссылку.

Хорошо:

```java
this.roles = List.copyOf(roles);
```

Теперь объект хранит собственную неизменяемую копию.

```java
List<String> roles = new ArrayList<>();
roles.add("USER");

ImmutableUser user = new ImmutableUser("Talgat", roles);

roles.add("ADMIN");

System.out.println(user.getRoles()); // [USER]
```

Внешний список изменился, но состояние `ImmutableUser` осталось прежним.

## 9. `final` не делает объект immutable

`final` у переменной запрещает переприсвоить саму переменную.

```java
final MutableUser user = new MutableUser("Talgat", List.of("USER"));
```

Так нельзя:

```java
user = new MutableUser("Arman", List.of("ADMIN")); // compile error
```

Но сам объект менять можно:

```java
user.setName("Arman"); // ok
```

`final` у переменной означает: эта переменная всегда будет ссылаться на тот же объект.

Это не означает: объект нельзя менять.

Чтобы объект был immutable, нужно правильно спроектировать сам класс.

## 10. Где это важно в backend

### DTO

Если DTO mutable, сервисы могут менять входной объект:

```java
public void process(ClientDto clientDto) {
    normalize(clientDto);
    save(clientDto);
}
```

Метод `normalize()` может изменить тот же объект, который потом будет сохранён. Это не всегда ошибка, но такое поведение должно быть явным и ожидаемым.

### Entity

JPA Entity обычно mutable, потому что Hibernate управляет состоянием объекта.

С entity важно:

- не отдавать наружу изменяемые коллекции без контроля;
- аккуратно проектировать `equals()` и `hashCode()`;
- помнить, что объект может находиться в persistence context и отслеживаться Hibernate.

### Коллекции внутри объектов

Если объект содержит `List`, `Map` или `Set`, нужно заранее решить:

- можно ли внешнему коду менять эту коллекцию;
- нужно ли возвращать копию;
- нужно ли делать объект immutable.

Плохой вариант:

```java
public List<String> getRoles() {
    return roles;
}
```

Более безопасный вариант для mutable-класса:

```java
public List<String> getRoles() {
    return List.copyOf(roles);
}
```

Нормальный вариант для immutable-класса, если внутри уже хранится immutable list:

```java
public List<String> getRoles() {
    return roles;
}
```

## 11. Практические проверки в тестах

Файл `ObjectModelTest` закрепляет идеи модуля:

- присваивание копирует ссылку;
- разные объекты с одинаковыми данными имеют разные ссылки;
- метод может изменить mutable-объект;
- метод не может заменить ссылку вызывающего кода;
- mutable-класс может разделять внешнюю коллекцию;
- immutable-класс защищается через defensive copy;
- `getRoles()` у immutable-класса возвращает неизменяемый список;
- добавление роли к immutable-пользователю создаёт новый объект;
- `final` переменная всё ещё может ссылаться на mutable-объект.

## 12. Краткий итог

Главные идеи:

- переменная объектного типа хранит ссылку;
- присваивание копирует ссылку, а не объект;
- `==` у объектов сравнивает ссылки;
- Java всегда pass-by-value;
- для объектов в метод передаётся копия ссылки;
- метод может изменить объект через ссылку;
- метод не может заменить ссылку у вызывающего кода;
- mutable object можно менять после создания;
- immutable object нельзя менять после создания;
- `final` не делает объект immutable;
- для изменяемых внутренних полей нужна defensive copy.
