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
    public List<String> findSubPageById(Long id) {

        return boardRepository.getChildById(id).stream().map(Board::getTitle).toList();
    }

    @Override
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
![팀과제결과2](https://github.com/tjdghks1994/wanted-pre-onboarding-backend/assets/57320084/180765c8-25f0-4447-b094-1773e4824a95)

![팀과제결과3](https://github.com/tjdghks1994/wanted-pre-onboarding-backend/assets/57320084/c631bcac-8f0d-4662-b002-387bffb65e20)

![팀과제결과1](https://github.com/tjdghks1994/wanted-pre-onboarding-backend/assets/57320084/ac4ea852-a4bf-481c-8e07-e667d41c6709)

---
### 구조 설명
먼저 게시글의 특성에 대해 곰곰히 생각해보았습니다.

게시글은 읽기와 수정이 잘 일어나는 객체 특성상, Read의 성능과 Update의 성능 모두를 지켜야 한다고 생각합니다.

Read의 성능은 Caching 방식으로 극대화 할 수 있습니다.

기존의 DB에서 Recursive만으로 조회해 오는것은, 트랜잭션 1회는 clustered Index(PK)로 B+Tree 인덱싱 탐색으로 ID 하나만 가져온다는 전제 하에 한 DBConnection 당 1000TPS를 넘기기 힘듭니다.

(서버의 자원에 따라 다릅니다만 옥타코어, 64기가 램 기준입니다.)

대용량으로 트래픽이 몰리게 되면 DB와 병목현상이 발생할 수 있습니다.

이 프로젝트는 모놀리식으로 구성되어 있어 Redis 같은 Inmemory DB를 추가로 사용하지 않고 써드파티로 EhCache를 통해 구성했습니다.

하지만 spring Cache로 구성했기 때문에 차 후 Cache가 분리되어야 한다면 의존성 주입을 Redis로 변경하도록 하면됩니다.

따라서 메모리 조회로 가져오는 EhCache를 통해 다른 세션에서도 조회할 수 있는 게시글을 Global Cache로 관리하게 해주어 조회 성능을 극대화하였습니다.

차 후 조회 기능이 아닌 수정, 삭제, 등록이 일어날 시 Spring Cache 어노테이션들을 활용해 맞춰주면 된다 생각합니다.


그 후, Update가 빈번하게 일어나는 경우입니다.

BreadCrumbs가 수정되는 경우, 상위 ParentId인 Page만 수정이 일어나면 SubPage들은 영향을 받지 않습니다. 

따라서 Update가 일어났을 시, 캐시를 수정하는 방안으로 적용하면 재귀호출 또한 줄어들 것입니다.

Content가 수정되는 경우는 큰 문제가 없으니 넘어가도록 하겠습니다.
