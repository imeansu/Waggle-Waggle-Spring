### 목차

1. 서비스 및 핵심 기능 설명
2. 플로우 차트
3. 백엔드 아키텍쳐
4. ERD
5. 핵심 기능 구현 방법

### 🔗 Github

[GitHub - imeansu/Waggle-Waggle-Spring](https://github.com/imeansu/Waggle-Waggle-Spring)

### 📲 API 명세서

[Build, Collaborate & Integrate APIs | SwaggerHub](https://app.swaggerhub.com/apis/waggle6/waggle/1.0.0-oas3)

## 1. 서비스 및 핵심 기능 설명

---

<aside>
💡 외국인 친구와 계속 말하면서 외국어 연습도 하는
메타버스를 활용한 대화 소재 추천 언어교환 모바일 어플리케이션 입니다.

</aside>

- 팔로잉 및 차단 : 상대방이 입장 중인 메타버스 대화방에 따라들어 갈 수 있습니다.
- 온라인 멤버 목록 : 현재 로그인된 사용자 목록을 볼 수 있습니다.
- 월드 주제 및 키워드 설정 : 사용자는 대화방의 주제 및 키워드를 설정할 수 있습니다.
- 대화 소재 추천 : 일정량의 대화가 쌓이면 AI 를 통해 대화 주제를 파악하고 연관된 키워드를 추천해줍니다.

## 2. 플로우 차트

---

- 플로우 차트
    
    ![Untitled](https://www.notion.so/image/https%3A%2F%2Fs3-us-west-2.amazonaws.com%2Fsecure.notion-static.com%2F5181e3a1-045a-44d3-8019-1bf4e48de128%2FUntitled.png?table=block&id=0acb975c-ce4f-450f-8c5b-3dde82bf72a3&spaceId=3ff3cc55-be4f-4375-ab1b-692684f19695&width=2000&userId=cf16c311-461d-4956-8b1a-92572df9e58c&cache=v2)
    

## 3. 백엔드 아키텍쳐

- 아키텍쳐
    
    ![Untitled](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/eda31d94-2734-4d66-aafc-0272896845bd/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAT73L2G45O3KS52Y5%2F20211110%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20211110T122415Z&X-Amz-Expires=86400&X-Amz-Signature=6e1f9a9fd1d06c86377b1f5ce74906a945c08e675f8aac94910b850268e567bb&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22)
    

## 4. ERD

---

![Untitled](https://www.notion.so/image/https%3A%2F%2Fs3-us-west-2.amazonaws.com%2Fsecure.notion-static.com%2Fdd816ad6-3af4-47d0-a3b5-58ef24ebd348%2FUntitled.png?table=block&id=058d593b-3d40-44eb-afdb-59957b8212f8&spaceId=3ff3cc55-be4f-4375-ab1b-692684f19695&width=2000&userId=cf16c311-461d-4956-8b1a-92572df9e58c&cache=v2)

- 스키마 설명
1. `Member`
    
    사용자들에 대한 정보를 저장합니다.
    
2. `World`
    
    등록된 메타버스 월드에 대한 기본 정보를 저장합니다.
    
3. `Worldroom`
    
    사용자가 개설한 월드 대화 방으로 키워드, 토픽, 오픈 여부 등의 정보를 담고 있습니다.
    
4. `EntranceRoom`
    
    Member와 WorldRoom의 N:M 관계를 N:1, M:1  관계로 풀어서 저장합니다.
    
5. `Interest`
    
    관심사 카테고리로, 자신과 1:N 관계로 계층적으로 구성됩니다.
    

## 5. 기능 구현 방법 설명

---

1. 사용자 인증을 위한 JWT
    
    최초 로그인은 Firebase 인증 서비스를 사용합니다. 이 후에는 백엔드에서 발급한 Access token과 Refresh token으로 사용자 인증, 인가를 수행합니다.
    
    ---
    
2. Server Send Event
    
    대화 소재 추천을 위해 일정량 이상의 대화 데이터가 쌓이면 이를 파이썬 AI 서버로 보내 추천 키워드를 받아옵니다. 이를 서버에서 사용자에게 푸시하도록 합니다.
    
    ---
    
3. 다중 서버 캐시 및 Pub/Sub
    
    다중 서버임을 고려했을 때, 대화 그룹의 사용자들의 SSE 연결이 여러 서버에 흩어져 있는 상황을 대비하기 위함 입니다. 파이썬 서버가 Publish 한 추천 키워드를 각 서버들이 Subscribe하여 자신에게 연결된 사용자에게 전달합니다. 또한, 대화 데이터 및 사용자 그룹 캐시 데이터를 보관합니다.
    
    ---
    
4. 파이썬 AI 서버와의 통신을 위한 메시지 큐
    
    키워드 추천 AI 의 수행 시간을 고려하여 비동기 통신 방식을 사용합니다. 
    
    ---
    
5. 에러 로그 및 AI 학습용 데이터 확보를 위한 elk 스택 
    
    사용자의 대화 데이터를 모아 AI 학습 데이터로 사용하는 시스템입니다. 이를 위해 대화 관련 데이터는 elk 스택을 사용하여 분석할 수 있도록 합니다.
    
    ---
    
6. AOP 를 활용한 로그 출력
    
    컨트롤러와 대화 관련 서비스에 AspectJ 를 통해 Logging 할 수 있도록 합니다. 이는 Logstash를 통해 ElasticSearch로 보내집니다.
    
    ---
    
7. ControllerAdvice를 통한 GlobalExceptionHandler 및 커스텀 Exception
    
    예외 처리를 통합적으로 관리하기 위해 GlobalExceptionHandler와 ErrorResponse 객체를 만들어 핸들링, 로깅, 에러메세지 전송을 하도록 합니다.
