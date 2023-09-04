package com.wanted.breadcrumbs.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.breadcrumbs.entity.Board;
import com.wanted.breadcrumbs.entity.BoardResult;
import com.wanted.breadcrumbs.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl {
    private final BoardRepository boardRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Map<Long, List<String>> store = new HashMap<>();

    public String getPageInfo(Long id) {
        // 현재 페이지 정보 조회
        Board page = boardRepository.getBoardById(id).orElseThrow(()-> new RuntimeException("This page does not exist"));
        // 현재 페이지의 서브 페이지 조회
        List<Board> subList = boardRepository.getSubListById(id);
        // 현재 페이지 브로드크럼스 조회 (브로드크럼스저장소에 존재하는지)
        List<String> breadCrumbs = store.get(page.getId());
        if (breadCrumbs == null) { // 브로드크럼스 저장소에 존재하지 않는 경우
            // 현재 페이지 브로드크럼스 추가
            breadCrumbs = new LinkedList<>();
            breadCrumbs.add(page.getTitle());
            Long parentId = page.getParentId();
            getBreadCrumbs(breadCrumbs, parentId);
            // 브로드 크럼스 저장소에 보관
            store.put(page.getId(), breadCrumbs);
        }

        // 현재 페이지 조회 정보 결과 생성
        BoardResult boardResult = BoardResult.builder()
                .id(page.getId())
                .title(page.getTitle())
                .description(page.getDescription())
                .parentId(page.getParentId())
                .subPageList(subList)
                .breadCrumbs(breadCrumbs.toArray(new String[breadCrumbs.size()]))
                .build();

        String jsonPageResult = "";
        try {
            // json 문자열로 변환
            jsonPageResult = objectMapper.writeValueAsString(boardResult);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("jsonPageResult = " + jsonPageResult);

        return jsonPageResult;
    }

    // 현재 페이지 브로드크럼스 조회
    private void getBreadCrumbs(List<String> list, Long parentId) {

        while (parentId != null) {
            Board parentPage = boardRepository.getBoardById(parentId).get();
            list.add(0, parentPage.getTitle());

            parentId = parentPage.getParentId();
        }
    }
}
