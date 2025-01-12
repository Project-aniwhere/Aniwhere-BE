package com.example.aniwhere.controller.tag;

import com.example.aniwhere.domain.category.dto.CategoryDTO;
import com.example.aniwhere.service.tag.TagService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TagSearchController {

    private final TagService tagService;

    @Operation(
            summary = "태그 조회",
            description = "태그(장르) 종류를 모두 조회합니다."
    )
    @GetMapping("/anime/tag")
    public ResponseEntity<List<CategoryDTO.CategoryResponseDTO>> getCategoriesList(){
        List<CategoryDTO.CategoryResponseDTO> categoryResponseDTOList = tagService.getCategoriesList();
        return ResponseEntity.ok(categoryResponseDTOList);
    }
}
