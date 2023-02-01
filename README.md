# SendWish Backend

### 기술 스택

<div>
<img alt="Spring Boot" src ="https://img.shields.io/badge/Spring Boot-6DB33F.svg?&style=for-the-badge&logo=Spring Boot&logoColor=white"/>
<img alt="Spring Security" src ="https://img.shields.io/badge/Spring Security-DB7093.svg?&style=for-the-badge&logo=Spring Security&logoColor=white"/>
<img alt="MySQL" src ="https://img.shields.io/badge/MySQL-4479A1.svg?&style=for-the-badge&logo=MySQL&logoColor=white"/>
<img alt="Redis" src ="https://img.shields.io/badge/Redis-DC382D.svg?&style=for-the-badge&logo=Redis&logoColor=white"/>
<img alt="Gradle" src ="https://img.shields.io/badge/Gradle-02303A.svg?&style=for-the-badge&logo=Gradle&logoColor=white"/>
<img alt="Gunicorn" src ="https://img.shields.io/badge/Gunicorn-499848.svg?&style=for-the-badge&logo=Gunicorn&logoColor=white"/>
<img alt="NGINX" src ="https://img.shields.io/badge/NGINX-009639.svg?&style=for-the-badge&logo=NGINX&logoColor=white"/>
<img alt="Flask" src ="https://img.shields.io/badge/Flask-000000.svg?&style=for-the-badge&logo=Flask&logoColor=white"/>
<img alt="Selenium" src ="https://img.shields.io/badge/Selenium-43B02A.svg?&style=for-the-badge&logo=Selenium&logoColor=white"/>
<img alt="TensorFlow" src ="https://img.shields.io/badge/TensorFlow-FF6F00.svg?&style=for-the-badge&logo=TensorFlow&logoColor=white"/>
<img alt="Docker" src ="https://img.shields.io/badge/Docker-2496ED.svg?&style=for-the-badge&logo=Docker&logoColor=white"/>
<img alt="Amazon EC2" src ="https://img.shields.io/badge/Amazon EC2-FF9900.svg?&style=for-the-badge&logo=Amazon EC2&logoColor=white"/>
<img alt="Amazon RDS" src ="https://img.shields.io/badge/Amazon RDS-527FFF.svg?&style=for-the-badge&logo=Amazon RDS&logoColor=white"/>
<img alt="Amazon S3" src ="https://img.shields.io/badge/Amazon S3-569A31.svg?&style=for-the-badge&logo=Amazon S3&logoColor=white"/>
</div>

## 1. api 명세서
https://www.notion.so/SendWish-API-1598e455c5d4434f824f3c9c71d78137

- **Member**

|  | 기능 설명 | 현재 구현 여부 | 우선순위 | 요청 | 주소값 | 요청 body |
| --- | --- | --- | --- | --- | --- | --- |
| 1) home | - 서비스 메인 | O | 下 | GET | / |  |
| 2) signup | - 회원가입 | O |  下 | POST | /signup | nickname , password |
| 3) signin | - 로그인 | O | 下 | POST | /signin | nickname , password |
| 4) add friend | - 친구추가 | O | 下 | POST | /add/friend | addMemberId , memberId |

 ****

- **Collection**

|  | 기능 설명 | 현재 구현 여부 | 남은 업무 | due - date | 우선순위 | 요청 | 주소값 | 요청 body |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1) get
CollectionsByMember | - Member별 가지고 있는 콜렉션을 보여줌 | △ | RefCount가 1인 경우를 추가하여 기능 구현
findCollectionByMemeber 로직 수정 필요 | 1/8(일) | 上 | GET | /collections/
{nickname} |  |
| 2) create
Collection | - 콜렉션 만들기 | O |  | - | 下 | POST | /collection | nickname , title |
| 3) update
Collection
Title | - 콜렉션 제목 업데이트 | O |  | - | 下 | PATCH | /collection | nickname , title |
| 4) getDetail
Colleciton | - 콜렉션 상세내용 확인 | O |  | - | 下 | GET | /collections/
{nickname}/
{collectionId} |  |
| 5) delete
Collection | - 콜렉션 삭제 | O |  | 1/8(일) | 上 | Delete | /collection/
{collectionId} |  |
| 6) shared
Collections | - 멤버가 공유하고 있는
모든 컬렉션 조회 | O |  | 1/8(일) | 上 | GET | /collection/shared/
{memberId}  |  |
| 7) shared
Collection | - 공유 컬렉션 생성 | O |  | 1/8(일) | 上 | POST | /collection/shared | memberIdList, title
, targetCollectionId |

 

- **Item**

|  | 기능 설명 | 현재 구현 여부 | 남은 업무 | due - date | 우선순위 | 요청 | 주소값 | 요청 body |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1) createItem | - 아이템을 DB에 추가
- 파이썬 통한 파싱과 연결
(1-1과 연결됨) | O |  | - | 下 | POST | /parsing | url, nickname |
| 1-1) create
HttpRequest
AndSend | - 스크래핑서버와 연결 | O |  | - | 下 |  |  |  |
| 2) enrollItem | - 아이템을 컬렉션에 추가 | O | 아이템 여러개 추가 가능하도록 | - | 下 | POST | itme/enrollment | collectionId, itemId, nickname |
| 3) returnItem | - 유저가 담은 모든 item을 리턴 | O |  | 1/8(일) | 上 | GET | /items/
{memberId} |  |
| 4) deleteItem | - 유저의 아이템을 삭제 
- 아이템 삭제시 유저의 모든 컬랙션 내부 해당 아이템이 삭제됨 | O |  | - | 上 | DELETE | /item/{nickname}/{itemId} |  |
| 5) delete CollectionItem | - 컬렉션 내부 아이템을 삭제 | O |  |  |  | DELETE | /collection/item/{collectionId}/{itemId} |  |
| 6) return Item category rank | - 해당 유저의 아이템들의 카테고리 순위 및 카테고리별 아이템 반환 | O |  |  |  | GET | /items/category/rank/{nickname} |  |
- **chat**

|  | 기능 설명 | 현재 구현 여부 | 남은 업무 | 우선순위 | 요청 | 주소값 | 요청 body |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1) createRoom | - 채팅방 생성 | O |  | 上 | POST | /chat/room | memberIdList, collectionId |
| 2) getRoomByMember | - 해당 유저의 모든 채팅방 조회 | O |  | 上 | GET | /chat/rooms/{nickname} |  |
| 3) getChatsByChatRoomId | - 채팅 내역 전부 조회
- 최신 생성순  | O | sse로 실시간 추가 | 上 | GET | /chats/{chatRoomId} |  |
- **stomp**

## 2. erd 설계
<img width="812" alt="image" src="https://user-images.githubusercontent.com/77164776/215989696-1e9705af-ad32-4a1e-9e68-f961a9280c27.png">

