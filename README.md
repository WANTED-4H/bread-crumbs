# [팀 과제] 브러드크럼스(Breadcrumbs) 만들기

### 참여자
김현진, 민승현, 박성환, 임상현

---

### 요구사항
**페이지 정보 조회 API**: 특정 페이지의 정보를 조회할 수 있는 API를 구현

- 입력: 페이지 ID
- 출력: 페이지 제목, 컨텐츠, 서브 페이지 리스트, **브로드 크럼스 ( 페이지 1 > 페이지 3 > 페이지 5)**

---
### 테이블 구조
<img width="282" alt="스크린샷 2023-09-04 오후 7 48 36" src="https://github.com/tjdghks1994/wanted-pre-onboarding-backend/assets/57320084/4181dad7-c1d9-42b1-9932-dfe0faeebb4e">

---

### 시퀀스 다이어그램

![KakaoTalk_Photo_2023-09-05-08-58-56](https://github.com/WANTED-4H/bread-crumbs/assets/116651434/55e6bd55-3849-4996-8bbb-9f6edeb47876)


---
### 비즈니스 로직
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class FindBoardServiceImpl implements FindBoardService{

    private final ApplicationContext applicationContext;
    private final BoardRepository boardRepository;
    
    @Override
    @Cacheable(value = "board")
    @Transactional(readOnly = true)
    public Board findBoardByIdFromDB(Long id) {
        log.info("load Board id from DB : {}", id);
        Board boardById = boardRepository.getBoardById(id);
        return boardById;
    }


    @Override
    @Transactional(readOnly = true)
    public BoardDto findBoardById(Long id) {
        Board target = getMyBean().findBoardByIdFromDB(id);

        if(target == null){
            throw new BoardNotFoundException();
        }

        List<String> findSubPages = findSubPageById(id);
        List<Board> findBreadCrumbs = findBreadCrumbsById(id);
        List<String> breadCrumbsTitleList = new LinkedList<>();
        findBreadCrumbs.forEach(board -> breadCrumbsTitleList.add(board.getTitle()));

        return BoardDto.of(target, findSubPages, breadCrumbsTitleList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findSubPageById(Long id) {

        return boardRepository.getChildById(id).stream().map(Board::getTitle).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Board> findBreadCrumbsById(Long id) {
        
        LinkedHashMap<Long, Board> breadCrumbsMap = new LinkedHashMap<>();

        Board findMe = getMyBean().findBoardByIdFromDB(id); // 캐시에서 찾기

        breadCrumbsMap.put(findMe.getId(), findMe);

        Board parentBoard = getMyBean().findBoardByIdFromDB(findMe.getParentId()); // 캐시에서 찾기

        while(parentBoard != null && !breadCrumbsMap.containsKey(parentBoard.getId())) {
            breadCrumbsMap.put(parentBoard.getId(), parentBoard);
            parentBoard = getMyBean().findBoardByIdFromDB(parentBoard.getParentId());
        }

        List<Board> toList = new ArrayList<>();
        for(Map.Entry<Long, Board> entry : breadCrumbsMap.entrySet()) {
            System.out.println(entry.getValue());
            toList.add(entry.getValue());
        }

        Collections.reverse(toList);
        return toList;
    }
    
    private FindBoardService getMyBean() {
        return applicationContext.getBean("findBoardServiceImpl", this.getClass());
    }
```
쿼리 부분
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.wanted.breadcrumbs.mapper.BoardRepository">
    <insert id="insertBoard" parameterType="com.wanted.breadcrumbs.entity.board.Board">
        INSERT INTO board (title, contents, parent_id) VALUES (#{title}, #{contents}, #{parentId})
    </insert>

    <select id="getBoardById" parameterType="Long" resultType="com.wanted.breadcrumbs.entity.board.Board">
        SELECT * FROM board WHERE id = #{id}
    </select>

    <select id="getChildById" parameterType="Long" resultType="com.wanted.breadcrumbs.entity.board.Board">
        SELECT c.id, c.contents, c.parent_id, c.title FROM board b JOIN board c ON b.id = c.parent_id WHERE b.id = #{id}
    </select>

</mapper>
```


---
### 결과정보

ex) 1 페이지 > 2 페이지 > 3 페이지 로 페이지 구성이 되어있는 경우


![팀과제결과2](https://github.com/tjdghks1994/wanted-pre-onboarding-backend/assets/57320084/180765c8-25f0-4447-b094-1773e4824a95)

![팀과제결과3](https://github.com/tjdghks1994/wanted-pre-onboarding-backend/assets/57320084/c631bcac-8f0d-4662-b002-387bffb65e20)

![팀과제결과1](https://github.com/tjdghks1994/wanted-pre-onboarding-backend/assets/57320084/ac4ea852-a4bf-481c-8e07-e667d41c6709)

---
### 구조 설명
#### 테이블 구조
   
테이블 구조에서는 board테이블의 **기본키(PK)를 자기자신의 외래키(FK)로 가지는 Recursive한 구조**를 만들었습니다.

이를 통해 테이블은 해당 entity와 이 entity에 대한 계층 구조의 관리에만 집중할 수 있으며, 별도의 테이블을 만들어 관리하지 않아도 되는 장점이 있습니다.
    
또한, 이러한 구조는 계층구조의 변경이나 확장이 필요할 때 기존 테이블의 스키마를 수정하지 않고도 새로운 노드를 추가하거나 변경할 수 있습니다.

<br>

#### Read와 Update 성능에 대한 최적화

게시판에서 게시글의 Read와 Update는 빈도수가 잦게 일어나는 작업 중 하나로 객체를 통해 접근하는 Read와 Update 성능을 모두 최적화해야 합니다.

**- Read**

현재 board테이블을 통한 단순한 DB 조회의 경우 시스템은 트래픽이 증가할 경우 DB와의 병목 현상을 유발할 수 있습니다. 

예를 들어, 트랜잭션 1회는 clustered Index(PK)로 B+Tree 인덱싱 탐색으로 ID 하나만 가져온다는 전제 하에 한 DBConnection 당 1000TPS를 넘기기 힘듭니다.
    (옥타코어 64기가 램 기준)

따라서 Read 성능을 최적화하기 위해 Caching 방식을 도입하였습니다.

EhCache를 사용하여 메모리 조회로 PK(게시글 id)에 대하여 Global Cache로 관리하였습니다. 이는 조회 성능을 극대화하며, 여러 세션에서도 게시글을 공유할 수 있습니다.

현재는 모놀리식 구성이라 이러한 써드파티를 사용했지만, Spring Cache로 구성했기 때문에 추후 cache 분리가 된다면 redis로 변경도 가능합니다.

**- Update**

Update가 빈번하게 일어나는 경우 BreadCrumbs가 자주 수정되는 것을 고려해야 합니다.

이 경우에는 상위 Page가 수정된다고 하더라도 하위 Page들은 영향을 받지 않으므로, Update가 발생하면 캐시를 수정하여 재귀 호출을 최소화할 수 있는 방안을 도입하였습니다.


<br>


### 결론

---
위와 같은 방식을 통해 <code class="notranslate">BreadCrumbs가 추가되더라도 각각의 테이블을 효율적으로 관리할 수 있으며, Caching을 사용하여 성능 최적화</code> 가 가능합니다.
