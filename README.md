
## NUMBLE 챌린지 - 강아지 인기투표 아키텍쳐


## ✔️ 프로젝트 개요

### 📖 노션 링크
[노션](https://generated-silene-2be.notion.site/3f161ab18c2c46cdb2fe53a147281473)

### ⌛️ 프로젝트 기간

`2023/05/15(월)` ~ `2023/05/28(일)`

### 🛠 프로젝트 구조


#### ERD
<img width="379" alt="image" src="https://github.com/qjatjr29/pet-vote/assets/74031333/7eb5b251-fd95-4284-935e-fd98747c0ccc">

#### CI/CD
<img width="1063" alt="image" src="https://user-images.githubusercontent.com/74031333/222036195-bfcbade2-a07d-4ecd-840d-93cd59f9708d.png">

### 🔧 사용 기술

<img width="1113" alt="image" src="https://github.com/qjatjr29/pet-vote/assets/74031333/fb3aabd3-2ec2-4aee-a766-108950736022">

![Java](https://img.shields.io/badge/-Java%2011-007396?style=plastic&logo=Java&logoColor=white)
![SpringBoot](https://img.shields.io/badge/-Spring%20Boot%202.7.11-6DB33F?style=plastic&logo=Spring%20Boot&logoColor=white)
![SpringDataJPA](https://img.shields.io/badge/-Spring%20Data%20JPA%20-6D933F?style=plastic&logo=Spring&logoColor=white)
![JUnit5](https://img.shields.io/badge/-JUnit5-%2325A162?style=plastic&logo=JUnit5&logoColor=white)
![Gradle](https://img.shields.io/badge/-Gradle%20-02303A?style=plastic&logo=Gradle&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL%208-4479A1?style=plastic&logo=MySQL&logoColor=white)
![Redis](https://img.shields.io/badge/-Redis-%23DC382D?style=plastic&logo=Redis&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-47A248?style=plastic&logo=mongodb&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/-Apache%20Kafka-%23231F20?style=plastic&logo=ApacheKafka&logoColor=white)


### 🧱 인프라
![AmazonAWS](https://img.shields.io/badge/AWS%20EC2-232F8E?style=plastic&logo=amazonec2&logoColor=white)
![AmazonAWS](https://img.shields.io/badge/AWS%20S3-569A31?style=plastic&logo=amazons3&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/-Github%20Actions-%232088FF?style=plastic&logo=GithubActions&logoColor=white)
![Docker](https://img.shields.io/badge/-Docker-%232496ED?style=plastic&logo=Docker&logoColor=white)

### 📠 협업툴
![GitHub](https://img.shields.io/badge/-GitHub-181717?style=plastic&logo=GitHub&logoColor=white)

## 🏃🏻 프로젝트 진행

### 📌 구현 필수 기능
- [x] 투표
- [x] 투표 취소
- [x] 펫 생성 / 수정 /삭제

### 📌 사용 기술
### 1. REST API 사용
``이유``  
REST API만 보고도 이것이 어떤 역할을 하는지 쉽게 이해할 수 있도록 하기 위해

``사용 방법``
1. **URI** - 정보의 자원을 표현
2. 자원에 대한 행위는 **HTTP Method**를 통해 표현
3. HTTP 응답 상태 코드를 통해 응답

### 2. REST Docs 사용

``REST Docs: 테스트 코드 기반으로 Restful API 문서를 돕는 도구``

> Swagger vs REST Docs

**_swagger_** 는 로직에 애노테이션을 이용해 명세를 작성한다.  
-> 코드가 많이 붙게되어 가독성이 떨어지게 된다.

**_REST Docs_** 는 테스트 코드에서 명세를 작성해 비즈니스 로직의 가독성에 영향이 없다.

### 3. Redis Cache
``Redis를 사용한 캐시 사용 이유``
1. 어플리케이션의 빠른 처리 성능(조회 기능에서)을 확보
2. 불필요한 트래픽을 줄이기 위해

### 4. Spring Scheduler

> 투표를 하는 이벤트마다 단건으로 조회모델을 업데이트 하는 것은 비효율적 

- 수백개의 데이터가 요청이 왔을 경우 하나하나 처리하는 것은 비효율적이라고 생각

- 레디스를 버퍼로 활용하여 투표 / 취소 이벤트 횟수를 저장
- spring scheduler를 통해 10초에 한 번씩 버퍼에 있는 모든 요청을 가져와 로직을 처리


<br />

> 데이터 정합성 맞추기

- 조회모델과 실제 데이터베이스와의 데이터 정합성을 맞추기 위해 매 시간마다 조회모델의 데이터를 갱신

### 5. Event
투표 / 투표 취소시 
계좌 이체 로직에 알림 요청로직을 추가하게 되면 로직이 섞이고 복잡해지는 문제가 발생

투표, 투표 취소는 상태가 변경된 것 -> 이벤트 발생  
**이벤트를 활용해 투표수 변경 로직을 구현**

``장점``
- 이벤트를 사용해 펫 도메인에서 알림을 요청하는 기능에 대한 의존을 제거
- 기능 확장에도 용이할 것이라 생각
    - 이체 성공시 알림을 보내는 것 말고도 다른 기능을 추가할 수 있다.
    - ex) 이메일로 이체 내역을 보내는 기능 => 이메일 발송 처리하는 핸들러를 구현

### 6. Kafka

#### ❓ 카프카 사용 이유
``가정``

1. 갑작스런 투표, 투표 취소율이 증가, 이에 따른 투표관련 요청이 증가해 **서버에 부하**가 생길 수 있다.
    -  서버의 모든 스레드가 고갈되고 응답시간이 매우 지연되는 상황
2. 서버에 문제 발생시 다른 비즈니스 로직에서도 문제가 발생하게 될 것이라고 생각.
    - 해당 기능을 처리하다 다른 핵심 비즈니스로직에 문제가 발생하는 상황이 발생
3. 서버가 다운되거나 문제가 발생됐을 경우 현재 요청된 요청이 유실될 수 있다.
    - 메시지 큐를 사용해 해결해보자.

<br />

> 메시지 큐를 도입하여 투표 관련 장애나 지연이 실제 서버로 전파되는 것을 막기위해 사용
- RabbitMQ와 달리 kafka는 토픽을 계속 유지해 특정상황이 발생해도 재생가능
- 갑작스런 트래픽 증가시 병렬처리가 중요하다고 생각 -> Kafka가 적합하다고 생각


### 7. MongoDB
#### ❓ MongoDB 사용 이유
CQRS를 적용하기 위해 조회를 담당하는 모델이 필요  
조회시 조회에 관련된 데이터만 저장하고 조회 성능이 좋은 MongoDB를 사용하기로 결정

### 8. CQRS

서비스가 커지게 되면 실제 조회에는 조회되지 않는 데이터가 생기게 된다.  
또한 조회 관련 데이터에서 외부로부터 주입받은 데이터들이 생기며 도메인과 모델이 복잡해진다.  
-> 잘못 사용시 의도하지 않은 방법으로 특정 데이터를 사용할 수 있다.  

=> CQRS로 명령과 조회의 책임을 분리해 사용

⇒ 가능한 DB에 있는 값 그대로 가져와 바로 사용하는 것을 추천

⇒ `비정규화된 데이터`를 그대로 저장

⇒  NoSQL을 사용!  

> 명령 모델을 통해 데이터가 변경되는 시점에 조회 모델을 생성하고 비정규화된 데이터를 그대로 저장.


### Commit Convention

```
feat : 새로운 기능에 대한 커밋
fix : 버그 수정에 대한 커밋
chore : 빌드 업무 수정, 패키지 매니저 수정
docs : 문서 수정에 대한 커밋
style : 코드 스타일 혹은 포맷 등에 관한 커밋
refactor :  코드 리팩토링에 대한 커밋
test : 테스트 코드 수정에 대한 커밋
rename : 파일 혹은 폴더명을 수정하거나 옮기는 작업에 대한 커밋
remove : 파일을 삭제하는 작업에 대한 커밋
```


## 💻 Code Convention

- 코드 스타일
    - google code style

- 접근제한자에 따른 코드 작성 순서
    - 필드: public -> private
    - 메서드: public -> private
    - 생성자: private -> public

- 어노테이션에 따른 코드 작성 순서
    - DB 관련 어노테이션 (ex: Entity, Table)
    - 객체 관련 어노테이션 (ex: Getter, ToString)
    - 생성 관련 어노테이션 (ex: Builder, RequiredArgsConstructor)


### 🔥 더 공부해볼 내용
1. CQRS
2. Redis

