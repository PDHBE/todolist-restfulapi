# todo-list-restful-api

## 소개

<img width="436" alt="스크린샷 2021-07-16 오후 1 59 23" src="https://user-images.githubusercontent.com/82703938/125894280-a5ac5cf2-1444-41d7-b48f-16634c718fd1.png">

- 할 일 목록(todoList) 을 생성, 조회, 변경, 삭제 할 수 있는 RESTful API.

- 주요 기능

	- 할 일(Todo) 생성

		- 제약조건 : 할 일 이름(Title) 이 공백이여선 안된다.

	- 등록된 하나의 할 일 조회

	- 모든 할 일 목록 조회

	- 할 일 목록 페이지 단위로 조회

		- 요청시 페이지 정보를 설정하지 않으면, 기본 페이지 설정값으로 조회된다.(size = 20, number = 0)

	- 할 일 수정

		- 제약조건 1 : 이미 종료된 할 일은 수정할 수 없다.

		- 제약조건 2 : 할 일 이름(Title) 이 공백이여선 안된다.

	- 할 일 종료

	- 등록된 하나의 할 일 삭제

	- 등록된 모든 할 일 목록 삭제


## 기술 스택

- Java, Spring Framework

- H2 Database

- Spring Data JPA

- Spring HATEOAS

- Spring REST Docs

## 구조

<img width="487" alt="스크린샷 2021-07-16 오후 2 24 24" src="https://user-images.githubusercontent.com/82703938/125896170-0a68e631-9051-4f92-8e47-9b5306099923.png">

DDD(도메인 주도 설계)를 참고하여 ui 계층, application 계층, domain 계층으로 분리하였습니다. 

### ui(표현) 계층

- TodoController : 할 일(Todo) 에 대한 모든 요청을 매핑하는 역할

- IndexController : 최초 루트("/") 요청을 매핑하는 역할

- ExceptionController : TodoController, IndexController 에서 발생하는 모든 예외 처리를 담당하는 역할

- NoContentResponse : 컨텐츠가 없는 경우( 할 일 삭제 완료시 )의 응답 메세지 역할

- ErrorResponse : 예외(에러) 발생 시 상태 코드와 이유를 담고 있는 응답 메세지 역할

### application(응용) 계층

- TodoService : TodoRepository(DB) 에 접근하며, Todo 객체의 인터페이스를 호출하여 상태를 변화시키고 트랜잭션 처리를 제어하는 역할 

- LinkService : 하나의 할 일에 속하는 링크(조회,수정,종료,삭제) 와 전반적인 기능의 링크(생성,할 일 목록 조회,할 일 목록 삭제) 를 제공하는 역할

- RepresentationalModelService : 각 상황에 맞는(클래스 타입에 따라) 링크를 포함하는 Representational Model 을 제공하는 역할

- NoTodoException : TodoService 가 TodoRepository 에서 Todo 조회시 발생할 수 있는 예외  

### domain 계층

- Todo : Todo 애그리거트의 루트 엔티티로써, 스스로 도메인 로직을 검증하며 생성, 수정, 변경하는 역할

- TodoRepository : 실제 DB 에 접근하여 CRUD 하는 역할

- TodoDto : 요청에서 받고자 하는 데이터를 정의, 계층 간 전송되는 객체 역할 

- TodoStatus : 할 일이 가질 수 있는 상태를 정의하는 역할

- TitleIsBlankException : 할 일 생성, 수정시 할 일의 Title 이 공백일 경우에 발생하는 도메인 로직 예외

- AlreadyFinishedException : 이미 종료된 할 일을 수정하려는 요청에 발생하는 도메인 로직 예외


## 이슈

### lombok 리팩토링
   
- @NoArgsConstructor(access = AccessLevel.PROTECTED) 사용

	- ObjectMapper의 (역)직렬화 과정과 , JPA의 프록시 객체는 기본 생성자가 반드시 필요한데, protected의 접근 제어 수준으로 모두 사용 가능하므로 외부에서의 무분별한 생성을 막는다.

- @Data 사용 지양

	- @ToString, @EqualsAndHashCode, @Getter, @Setter, @RequiredArgsConstructor 를 모두 포함하고 있으므로, 너무 많은 것을 노출하게 되고 불안정한 객체를 생성할 가능성도 높아진다. 편리하다고 생각없이 사용하지 말자.

- @Setter 사용 지양

	- 단지 값을 세팅만 하는 것은 사용 의도도 드러나지 않고, 객체를 불안정한 상태에 놓이게 만든다. 

- @Getter 사용 주의

	- @Setter 와 같이 객체를 불안정한 상태로 변화시킬 수 있는 치명적인 상황은 만들지 않지만, 클래스의 모든 필드를 노출시킬 수 있으므로 이 역시 사용에 주의해야 한다.

- @ToString 사용 주의

	- 연관 관계 엔티티 사이의 무한 참조가 생길 수 있다. @ToString(of 또는 exclude) 사용 권장

- 클래스 상단의 @Builder 사용 지양

	- 클래스 상단의 빌더는 모든 필드를 노출시키게 된다. 따라서, 대신 생성자 상단에 @Builder 를 사용함으로써 원하는 필드만 지정하여 생성하도록 하여 안정성을 높이고, 노출을 최소화한다. 
   
### 값 검증, 예외처리

- 값에 대한 검증을 표현 계층의 Controller 에서 할지, 응용 계층의 Service 에서 할지, 도메인 계층의 엔티티가 수행할지는 사람들마다 의견도 갈리고, 나도 항상 고민해왔던 부분이다. 

- 이전 프로젝트에선 값이 유효하지 않을 경우 바로바로 재입력받을 수 있도록 사용자와 가장 가까운 표현 계층의 Controller 에서 수행하도록 하였다. 또한 @Valid, Errors 를 사용하면 쉽게 검증과 예외처리를 할 수도 있다.

- 하지만 결국 값의 검증은 도메인 로직에 달려있고, 도메인 로직을 수행할 책임이 있는 도메인 엔티티가 값 검증 역시 수행하는 것이 맞지 않나는 생각이 들었다.

- 따라서 본 프로젝트에서는 Controller 와 Service 에선 값에 대한 검증 없이 그대로 가공하여 다음 계층으로 전달만 하고, 최종적으로 도메인 엔티티가 로직을 수행하며 예외를 던지도록 설계해보았다.

- 그리고, 코드의 흐름을 이해하는데 혼란을 줄 수 있는 try - catch 구문을 모두 제거하였고,  @RestControllerAdvice 를 활용하여 한 곳에서 모든 예외를 처리하도록 하였다.

### 응답 상태코드, 응답 메시지

- 처음엔 삭제 완료시 204 NoContent, 존재하지 않을 경우엔 404 NotFound 의 응답 상태코드를 보내도록 설계하였다.

- 하지만 두 경우 모두 응답 메시지의 Body가 존재하지 않으므로 링크를 보낼수가 없게 되었고, 더 이상 링크를 통한 상태 전이가 불가능하게 되었다.

- 따라서 NoContentResponse, ErrorResponse 객체를 만들어 각 상황에 맞는 메시지와 링크를 보낼 수 있도록 변경하였다.

- 최종적으로, content 가 없거나 예외가 발생한 경우에도 계속해서 기능을 사용 할 수 있도록 하였다.