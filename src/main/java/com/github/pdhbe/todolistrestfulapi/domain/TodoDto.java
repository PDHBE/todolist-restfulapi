package com.github.pdhbe.todolistrestfulapi.domain;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TodoDto {
    private String title;
    private String description;

    @Builder
    public TodoDto(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
