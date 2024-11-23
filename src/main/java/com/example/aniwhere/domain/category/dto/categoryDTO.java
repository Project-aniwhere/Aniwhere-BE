package com.example.aniwhere.domain.category.dto;

import lombok.Getter;
import lombok.Setter;

public class categoryDTO {
    @Getter
    @Setter
    public static class categoryRequestDTO{
        private String categoryName;
    }

    @Getter
    @Setter
    public static class categoryResponseDTO{
        private String categoryName;
    }
}
