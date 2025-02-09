## CineLink

![[image.webp]]
#### 당신과 영화의 특별한 만남, ClineLink가 이어드립니다.
- **최신 영화 및 상영관 정보 제공**: 개봉 예정작과 현재 상영 중인 영화 정보를 확인할 수 있습니다.
- **간편한 예매 및 결제**: 원하는 영화, 극장, 좌석을 선택하고 간편하게 결제할 수 있습니다.
- **실시간 예매 현황 반영**: 예매 가능한 좌석이 자동으로 업데이트되어 최신 정보를 제공합니다.
- **알림 기능 제공**: 예매 및 결제 완료 시 이메일 및 푸시 알림을 통해 정보를 제공합니다.

## Movie15 팀원
|팀원|태그|담당기능| Github 주소|
|----|----|----|--------|
|장대산|리더|영화 및 상영관 관리 | https://github.com/daesan12|
|류병길|부리더|영화 예매, 결제 | https://github.com/fbqudrlf09|
|김휘웅|팀원|회원가입 로그인 및 문의 | https://github.com/hwiung|
|김명호|팀원|알림 및 문의 |https://github.com/KimMyungHo919|

## 기술 스택
#### Back-end
[![My Skills](https://skillicons.dev/icons?i=idea,java,spring,mysql,redis,docker,rabbitmq,aws,github,githubactions,docker)](https://skillicons.dev)
- IDE : Intellij
- JDK : openjdk Java 17
- Framework :
    - Spring Framework
    - Spring Security
- 데이터 베이스
    - Mysql
    - redis
- 외부 API & 라이브러리
    - TMDB API
    - 토스페이먼츠 API
    - RabbitMQ
- Infra
    - AWS EC2
    - AWS S3
    - AWS ElasticCache
- CI / CD
    - Git Actions
    - Docker


## 프로젝트 기능
<details>
<summary>회원가입 / 로그인</summary>
<div markdown="1">
- 시스템 초기 실행 시, AdminInitializer를 활용하여 기본 관리자 계정을 자동으로 생성하고 관리자 계정의 존재 여부를 검증함
- 권한
    - USER, ADMIN
    - 인증 / 인가
    - Spring Security와 JWT를 기반으로 한 접근 제어 및 권한 관리
        - @PreAuthorize 및 @AuthenticationPrincipal을 활용한 권한 검증 및 접근 제어를 함으로써 관리자 전용 API 보호 및 유저별 접근을 제한하였음
        - 로그인 시 Access Token과 Refresh Token이 발급되며, Redis를 통해서 Refresh Token을 저장 및 검증함.
        - TTL(Time - To - Live) 설정을 통해 불필요한 데이터 저장 방지 및 Redis 기반 블랙리스트 토큰 관리로 로그아웃 또는 만료된 토큰이 재사용되는 걸 방지하였음
</div>
</details>

<details>
<summary>영화 관리</summary>
<div markdown="1">
- 영화 정보 등록 및 관리(관리자)    
    - 매일 새벽3시에 인기영화 40개를 영화관에게 제공
    - 영화상영시간,제목,줄거리,장르,포스터이미지,트레일러등을 제공
    - 인기영화에서 벗어난 영화는 1주일 뒤 논리적 삭제
    - 인기영화에서 벗어났다가 다시 복귀 한 영화는 논리적 삭제 복구
- 영화 조회    
    - 영화장르,상영시간,제목,지점별,상영중인 영화별 검색
- 상영 시간 관리

    - 특정 극장에서 해당 영화가 언제 상영되는지 관리        
    - 상영 시간표에 따른 예약 가능 여부 반영
</div>
</details>

<details>
<summary>영화관</summary>
<div markdown="1">
- 영화관 생성(관리자)
    - 지역,이름을 입력
- 영화관은 여러 관람관을 추가할 수 있음(관리자)
- 영화관 조회
    - 영화관의 지역과 이름으로 조회가능
</div>
</details>

<details>
<summary>영화관</summary>
<div markdown="1">
- 관람관은 종류별로 좌석갯수와 등급이 다름
- 노란색:VIP좌석
- 하늘색:일반좌석
-![[1 2.webp]]![[2.webp]]![[3.webp]]![[4.webp]]![[5.webp]]
</div>
</details>

<details>
<summary>영화 상영 스케쥴</summary>
<div markdown="1">
- 영화 상영스케줄 생성(관리자)
    - 상영날짜,상영시작시간,영화관id,영화id를 가지고있음
    - 영화의 상영시간+영화준비(청소) 시간 10분으로 계산해 상영종료시간이 자동 계산
    - 같은 관람관에는 상영시간이 겹칠 수 없음
</div>
</details>

<details>
<summary>영화 예매</summary>
<div markdown="1">
- 영화 예매 생성
    - Redis를 통한 동시성 제어
- 예매 조회, 수정, 취소
</div>
</details>

<details>
<summary>결제 시스템 연동</summary>
<div markdown="1">
- 결제 API    
    - Toss결제 흐름도    
    - ![[payment-widget-flow2.png]]        
- 결제 요청, 내역 확인, 취소
</div>
</details>

<details>
<summary>알림 시스템</summary>
<div markdown="1">
- JavaMailSender - 회원가입 시 이메일 인증
- RabbitMQ(x-delay-message) - 회원가입 이메일발송 10분 후 유저데이터 조회 → 미인증시 해당 유저 데이터 삭제
- RabbitMQ(x-delay-message) - 결제성공시 영화시작 30분전 이메일전송 예약
- RabbitMQ - 결제성공 , 결제취소시 이메일 발송
</div>
</details>

<details>
<summary>리뷰</summary>
<div markdown="1">
- 리뷰 생성, 조회, 수정, 삭제 → 조회 시 페이지 처리, 삭제 시 본인 또는 관리자만 삭제 가능
- 영화 당 1인 1건 작성 가능
</div>
</details>

<details>
<summary>문의 사항</summary>
<div markdown="1">
- 문의 사항 작성 시 파일 첨부 가능
- 관리자는 문의 상태를 PENDING, ANSWERED로 변경할 수 있음
- 문의 사항의 상태가 ANSWERED 일 경우, 사용자는 자신의 문의 사항을 수정할 수 없음
</div>
</details>

## 인프라 설계도
![[인프라_설계도531_.drawio.webp]]


## API 명세서
https://www.notion.so/teamsparta/cca7fb9345e549a9b278e8510e8e6288?v=ab8ed301c39040bda8d283fc700b7b6e&pvs=4


## ERD

![[롯데십오네마_ERD.webp]]