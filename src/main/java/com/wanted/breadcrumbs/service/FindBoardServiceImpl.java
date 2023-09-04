package com.wanted.breadcrumbs.service;

import com.wanted.breadcrumbs.entity.board.Board;
import com.wanted.breadcrumbs.entity.board.dto.BoardDto;
import com.wanted.breadcrumbs.exception.BoardNotFoundException;
import com.wanted.breadcrumbs.mapper.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

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
        //캐시에서 존재하면 캐시에서
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
        //그렇지 못하다면 DB에서 가져오고 캐시로
        //반복문을 하면 알아서 DB로 필요한 쿼리만 요청해서 가져올것임.

        Collections.reverse(toList);
        return toList;
    }


    //Bean으로 가져온 이유? AOP 적용 시키기 위해. @cacheable 은 aop이기 때문에 this로 호출하면 메서드만 호출됨.
    private FindBoardService getMyBean() {
        return applicationContext.getBean("findBoardServiceImpl", this.getClass());
    }
}
