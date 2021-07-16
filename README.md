# todolist-restfulapi

1. lombok 리팩토링
   @NoArgsConstructor(access = AccessLevel.PROTECTED) JPA에서는 프록시 객체가 필요하므로 기본 생성자 하나가 반드시 있어야 합니다. 이때 접근지시자는 protected면 됩니다. (낮은 접근지시자를 사용)
   @Data는 사용하지 말자, 너무 많은 것들을 해준다.
   @Setter는 사용하지 말자, 객체는 변경 포인트를 남발하지 말자.
   @ToString 무한 참조가 생길 수 있다. 조심하자. (개인적으로 @ToString(of = {"") 권장)
   클래스 상단의 @Builder X, 생성자 위에 @Builder OK
   Lombok이 자동으로 해주는 것들을 남용하다 보면 코드의 안전성이 낮아집니다. 특히 도메인 엔티티는 모든 레이어에서 사용되는 객체이니 특별히 신경을 더 많이 써야 합니다. 이 부분은 모든 객체에 해당되는 부분입니다.
   
2. 예외처리
컨트롤러에서 @Valid, Errors 로 검증하던것을 모두 도메인 계층으로
   
혼란을 주는 try catch 모두 제거, 한 곳에서 일괄적으로 처리

log를 남기도록