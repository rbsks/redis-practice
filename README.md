## Cache Annotation과 CacheManager의 TTL 문제
###### Cache Annotation
스프링에서 제공하는 @Cacheable, @CachPut, @CacheEvict는 TTL 시간을 지정할 수 없다.
그렇기 때문에 명시적으로 캐시를 지워주지 않는다면 캐시가 메모리에서 제거되지 않고 항상 메모리에 남게 되어 메모리 공간을 효율적으로 사용할 수 없게된다.

https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/cache/annotation/Cacheable.html
https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/cache/annotation/CachePut.html
https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/cache/annotation/CacheEvict.html
###### CacheManager
CacheManger를 Bean으로 등록하면 위 문제를 해결할 수 있다. 하지만 TTL 시간을 동적으로 바꿀 수 없다는 단점이 존재한다.
![RedisCacheManage](https://github.com/user-attachments/assets/699e087e-2ffc-4bb5-a6aa-eb543ac62393)
## RedisTemplate을 사용하여 TTL 관리
레디스 캐시 관리가 필요한 비즈니스 로직에서 RedisTemplate을 주입 받아 사용하면 비즈니스 로직마다 TTL 시간을 알맞게 조절할 수 있다.

하지만 매 클래스 마다 RedisTemplate을 주입 받아야하며 캐시 조회, 삭제, 업데이트 로직이 중복될 가능성이 높다. 이러한 문제는 custom annotation과 AOP의 조합으로 해결할 수 있을 거 같다.
## Custom Annotation을 사용하여 AOP  만들기

https://github.com/rbsks/redis-practice/tree/main/src/main/java/org/redis/common/aspect/cache

1. @RedisCacheable
    1. 레디스에서 캐시 조회
    2. 캐시가 존재하면 애노테이션으로 들어온 값으로 refresh 후 반환
        1. 자주 조회되는 키에 대해서는 ttl 시간을 refresh 시켜 cache hit율을 높히느 전략
    3. 캐시가 존재하지 않으면 비즈니스로직 실행 후 레디스에 캐시 저장 후 반환
    4. 레디스에 캐시 저장 시 getAndExpire 사용
2. @RedisCachePut
    1. 비즈니스로직 실행 후 레디스에 캐시 저장 후 반환
3. @RedisCacheEvict
    1. 비즈니스로직 실행 후 레디스에 캐시 삭제 후 반환
      
