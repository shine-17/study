# 템플릿

개방 폐쇄 원칙(OCP)은 코드에서 어떤 부분은 변경을 통해 그 기능이 다양해지고 확장하려는 성질이 있고, 어떤 부분은 고정되어 있고 변하지 않으려는 성질이 있음을 알 수 있다. 변화의 특성이 다른 부분을 구분해주고, 각각 다른 목적과 다른 이유에 의해 다른 시점에 독립적으로 변경될 수 있는 효율적인 구조를 만들어주는 것이 바로 이 개방 폐쇄 원칙이다.

**템플릿**이란 이렇게 바뀌는 성질이 다른 코드 중에서 변경이 거의 일어나지 않으며 일정한 패턴으로 유지되는 특성을 가진 부분을 자유롭게 변경되는 성질을 가진 부분으로부터 독립시켜서 효과적으로 활용할 수 있도록 하는 방법이다.

## 다시 보는 초난감 DAO

UserDao에는 아직 예외상황에 대한 처리가 안돼있다.

그래서 `Connection` `PreparedStatement` `ResultSet` 의 리소스를 반환(`close()`) 하기 위해 try~catch 문으로 예외처리를 적용했다.

## 변하는 것과 변하지 않는 것

### JDBC try/catch/finally 코드의 문제점

try/catch/finally 블록이 적용돼서 완성도 높은 DAO 코드가 된 UserDao지만, 막상 코드를 보면 복잡하다. 수없이 많이 사용되는 DAO 코드에서 try/catch/finally 블록이 2중으로 중첩사용되며, 모든 메서드마다 반복되어 유지보수하기가 어렵다.

### 분리와 재사용을 위한 디자인 패턴 적용

### 전략 패턴의 적용

개방 폐쇄 원칙을 잘 지키는 구조이면서도 템플릿 메서드 패턴보다 유연하고 확장성이 뛰어난 것이, 오브젝트를 아예 둘로 분리하고 클래스 레벨에서는 인터페이스를 통해서만 의존하도록 만드는 전략 패턴이다.

### DI 적용을 위한 클라이언트/컨텍스트 분리

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/0a08179c-6898-4f98-8b89-2a2ddd15c8b3/9d0acb25-9561-434e-8f73-4a4ffb6c3987/image.png)

1장에서 컨텍스트(UserDao)가 필요로 하는 전략(ConnectionMaker)의 특정 구현 클래스(DConnectionMaker) 오브젝트를 클라이언트(UserDaoTest)가 만들어서 제공해주는 방법을 사용했다. 결국 이 구조에서 전략 오브젝트 생성과 컨텍스트로의 전달을 담당하는 책임을 분리시킨 것이 바로 ObjectFactory이며, 이를 일반화한 것이 의존관계 주입(DI) 이었다.

**결국 DI란 이러한 전략 패턴의 장점을 일반적으로 활용할 수 있도록 만든 구조라고 볼 수 있다.**

## JDBC 전략 패턴의 최적화

> 중첩 클래스의 종류

다른 클래스 내부에 정의되는 클래스를 중첩 클래스(nested class)라고 한다. 중첩 클래스는 독립적으로 오비젝트로 만들어질 수 있는 정적 클래스(static class)와 자신이 정의된 클래스의 오브젝트 안에서만 만들어질 수 있는 내부 클래스(inner class)로 구분된다.

내부 클래스는 다시 범위(scope)에 따라 세 가지로 구분된다. 멤버 필드처럼 오브젝트 레벨에 정의되는 멤버 내부 클래스(member inner class)와 메서드 레벨에 정의되는 로컬 클래스(local class), 그리고 이름을 갖지 않는 익명 내부 클래스(anonymous inner class)다. 익명 내부 클래스의 범위는 선언된 위치에 따라서 다르다.
> 

## 컨텍스트와 DI

### JdbcContext의 분리

익명클래스 사용 및 클래스 분리로 아래와 같은 구조를 가지게 되었다. UserDao는 이제 JdbcContext에 의존하고 있다. 그런데 JdbcContext는 인터페이스인 DataSource와는 달리 구체 클래스다. 스프링의 DI는 기본적으로 인터페이스를 사이에 두고 의존 클래스를 바꿔서 사용하도록 하는 게 목적이다. 하지만 이 경우 JdbcContext는 그 자체로 독립적인 JDBC 컨텍스트를 제공해주는 서비스 오브젝트로서 의미가 있을 뿐이고 구현 방법이 바뀔 가능성은 없다. 따라서 인터페이스를 구현하도록 만들지 않았고, UserDao와 JdbcContext는 인터페이스를 사이에 두지 않고 DI를 적용하는 특별한 구조가 된다.

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/0a08179c-6898-4f98-8b89-2a2ddd15c8b3/c5af195b-84d3-4ec1-91a8-2d7f62b6903a/image.png)

스프링의 빈 설정은 클래스 레벨이 아니라 런타임 시에 만들어지는 오브젝트 레벨의 의존관계에 따라 정의된다. 빈으로 정의되는 오브젝트 사이의 관계를 그려보면 아래와 같다. 기존에는 userDao 빈이 dataSource 빈을 직접 의존했지만 이제는 jdbcContext 빈이 그 사이에 끼게 된다.

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/0a08179c-6898-4f98-8b89-2a2ddd15c8b3/437b4989-be5e-4490-968f-f1ccb348d00f/image.png)

### JdbcContext의 특별한 DI

UserDao와 JdbcContext 사이에는 인터페이스를 사용하지 않고 DI를 적용했다. UserDao와 JdbcContext는 클래스 레벨에서 의존관계가 결정된다. 비록 런타임 시에 DI 방식으로 외부에서 오브젝트를 주입해주는 방식을 사용하긴 했지만, 의존 오브젝트의 구현 클래스를 변경할 수는 없다.

**이렇게 인터페이스를 사용하지 않고 DI를 적용하는 것은 문제가 없을까?**

인터페이스를 사용해서 구현해도 되지만 꼭 그럴 필요는 없다.

의존관계 주입(DI)이라는 개념을 충실히 따르자면, 인터페이스를 사이에 둬서 클래스 레벨에서는 의존관계가 고정되지 않게 하고, 런타임 시에 의존할 오브젝트와의 관계를 동적으로 주입해주는 것이 맞다. 따라서 인터페이스를 사용하지 않았다면 엄밀히 말해서 온전한 DI라고 볼 수는 없다.

그러나 스프링의 DI는 넓게 보자면 객체의 생성과 관계설정에 대한 제어권한을 오브젝트에서 제거하고 외부로 위임했다는 IoC라는 개념을 포괄한다. 그런 의미에서 JdbcContext를 스프링을 이용해 UserDao 객체에서 사용하게 주입했다는 건 DI의 기본을 따르고 있다고 볼 수 있다.

인터페이스를 사용해서 DI를 적용하지는 않았지만 JdbcContext를 UserDao와 DI 구조로 만들어야 할 이유는 뭘까?

1. JdbcContext가 스프링 컨테이너의 싱글톤 레지스트리에서 관리되는 싱글톤 빈이 되기 때문이다.
    - JdbcContext는 그 자체로 변경되는 상태정보를 가지고 있지 않다. 내부의 DataSource 인스턴스 변수가 있지만 읽기 전용이므로 싱글톤이 되는 데 아무런 문제가 없다.
    - JdbcContext는 JDBC 컨텍스트 메서드를 제공해주는 일종의 서비스 오브젝트로서 의미가 있기 때문에 싱글톤으로 등록돼서 여러 오브젝트에서 공유해 사용되는 것이 이상적이다.
2. JdbcContext가 DI를 통해 다른 빈에 의존하고 있기 때문이다.
    - JdbcContext는 DataSource 오브젝트를 주입받도록 되어 있다. DI를 위해서는 주입되는 오브젝트와 주입받는 오브젝트 양쪽 모두 스프링 빈으로 등록되어야 한다. 스프링이 생성하고 관리하는 IoC 대상이어야 DI에 참여할 수 있기 때문이다. 따라서 JdbcContext는 다른 빈을 DI 받기 위해서라도 스프링 빈으로 등록되어야 한다.

**실제로 스프링에서도 드물지만 이렇게 인터페이스를 사용하지 않는 클래스를 직접 의존하는 DI가 등장하는 경우가 있다.**

여기서 중요한 것은 인터페이스의 사용 여부다. 왜 인터페이스를 사용하지 않았을까?

인터페이스가 없다는 건 UserDao와 JdbcContext가 매우 긴밀한 관계를 가지고 강하게 결합되어 있다는 의미다. UserDao는 항상 JdbcContext 클래스와 함께 사용돼야 한다. 비록 클래스는 구분되어 있지만 이 둘은 강한 응집도를 가지고 있다. UserDao가 JDBC 방식 대신 JPA나 하이버네이트 같은 ORM을 사용해야 한다면 JdbcContext도 통째로 바뀌어야 한다. JdbcContext는 DataSource와 달리 테스트에서도 다른 구현으로 대체해서 사용할 이유가 없다. 이런 경우는 굳이 인터페이스를 두지 말고 강력한 결합을 가진 관계를 허용하면서 위에서 말한 두 가지 이유인, 싱글톤으로 만드는 것과 JdbcContext에 대한 DI 필요성을 위해 스프링의 빈으로 등록해서 UserDao에 DI 되도록 만들어도 좋다.

### 코드를 이용하는 수동 DI

JdbcContext를 스프링의 빈으로 등록해서 UserDao에 DI 하는 대신 **UserDao 내부에서 직접 DI를 적용하는 방법**이 있다.

이 방법을 적용하려면 문제가 있다.

1. JdbcContext를 **싱글톤 빈으로 만드는 것은 포기**해야한다.
    - 대신 Dao 마다 하나의 JdbcContext 오브젝트를 가지고 있게 하면 된다.
2. JdbcContext를 스프링 빈으로 등록할 수 없다.
    - JdbcContext는 다른 빈을 인터페이스를 통해 간접적으로 의존하고 있어 스프링 빈으로 등록되어야 하는데, 이 경우 JdbcContext에 대한 제어권을 UserDao에게 DI까지 맡기는 것이다.
    - UserDao가 JdbcContext에 주입해줄 의존 오브젝트인 DataSource를 UserDao가 대신 DI 받아서 JdbcContext에 DI 적용해주어  UserDao가 임시로 DI 컨테이너처럼 동작하게 만드는 것이다.

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/0a08179c-6898-4f98-8b89-2a2ddd15c8b3/86754c3d-435b-4d74-81c0-558e8b57e248/image.png)

이 방법의 장점은 굳이 인터페이스를 두지 않아도 될 만큼 긴밀한 관계를 갖는 DAO 클래스와 JdbcContext를 어색하게 따로 빈으로 분리하지 않고 내부에서 직접 만들어 사용하면서도 다른 오브젝트에 대한 DI를 적용할 수 있다는 점이다.

## 템플릿과 콜백

UserDao와 StatementStrategy, JdbcContext를 이용해 만든 코드는 일종의 전략 패턴이 적용된 것이라고 볼 수 있다. **복잡하지만 바뀌지 않는 일정한 패턴을 갖는 작업 흐름이 존재하고 그 중 일부분만 자주 바꿔서 사용해야 하는 경우에 적합한 구조**다. 전략 패턴의 기본 구조에 익명 내부 클래스를 활용한 방식이다. 이런 방식을 스프링에서는 템플릿/콜백 패턴이라고 한다.

전략 패턴의 컨텍스트를 **템플릿**이라고 하고, 익명 내부 클래스로 만들어지는 오브젝트를 **콜백**이라고 한다.

템플릿의 작업 흐름 중 특정 기능을 위해 한 번 호출 되는 것이 일반적이므로 템플릿/콜백 패턴의 콜백은 단일 메소드 인터페이스를 사용한다.

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/0a08179c-6898-4f98-8b89-2a2ddd15c8b3/b11f7617-2984-4b75-b08c-2989be919c4f/image.png)

- 클라이언트의 역할은 템플릿 안에서 실행될 로직을 담은 콜백 오브젝트를 만들고, 콜백이 참조할 정보를 제공하는 것이다. 만들어진 콜백은 클라이언트가 템플릿의 메서드를 호출할 때 파라미터로 전달된다.
- 템플릿은 정해진 작업 흐름을 따라 직업을 진행하다가 내부에서 생성한 참조정보를 가지고 콜백 오브젝트의 메서드를 호출한다. 콜백은 클라이언트 메서드에 있는 정보와 템플릿이 제공한 참조정보를 이용해서 작업을 수행하고 그 결과를 다시 템플릿에 돌려준다.
- 템플릿은 콜백이 돌려준 정보를 사용해서 작업을 마저 수행한다. 경우에 따라 최종 결과를 클라이언트에 다시 돌려주기도 한다.

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/0a08179c-6898-4f98-8b89-2a2ddd15c8b3/386a51d1-f83a-42bb-b529-8bd7bab2305a/image.png)

### 콜백의 분리와 재활용

이번에는 복잡한 익명 내부 클래스의 사용을 최소화하는 방법을 찾아보자.

변하는 부분(SQL 문장)

```java
public void deleteAll() throws SQLException {
		this.jdbcContext.executeSql("delete from users");
	}
```

변하지 않는 부분

```java
public void executeSql(final String query) throws SQLException {
		workWithStatementStrategy(
			new StatementStrategy() {
				public PreparedStatement makePreparedStatement(Connection c)
						throws SQLException {
					return c.prepareStatement(query);
				}
			}
		);
	}
```

기존에 변하는 부분(SQL 문장)과 변하지 않는 부분을 분리시켰다. 

### 콜백과 템플릿의 결합

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/0a08179c-6898-4f98-8b89-2a2ddd15c8b3/69d5a7ca-66fb-462b-adf4-f5b9610929cd/image.png)

`executeSql()` 메서드는 UserDao만 사용하기에는 재사용성이 조금 아쉽다. 이렇게 재사용 가능한 콜백을 담고 있는 메서드라면 DAO가 공유할 수 있는 템플릿 클래스 안으로 옮겨도 된다.

일반적으로는 성격이 다른 코드들은 가능한 한 분리하는 편이 낫지만, 이 경우는 반대다. 하나의 목적을 위해 서로 긴밀하게 연관되어 동작하는 응집력이 강한 코드들이기 때문에 한 군데 모여 있는 게 유리하다. 구체적인 구현과 내부의 전략 패턴, 코드에 의한 DI, 익명 내부 클래스 등의 기술은 최대한 감춰두고, 외부에는 꼭 필요한 기능을 제공하는 단순한 메서드만 노출해주는 것이다.

### 스프링의 JdbcTemplate

스프링은 JDBC를 이용하는 DAO에서 사용할 수 있도록 준비된 다양한 템플릿과 콜백을 제공한다. 스프링이 제공하는 JDBC 코드용 기본 템플릿은 JdbcTemplate이다. 앞에서 만들었던 JdbcContext와 유사하지만 훨씬 강력하고 편리한 기능을 제공해준다.
