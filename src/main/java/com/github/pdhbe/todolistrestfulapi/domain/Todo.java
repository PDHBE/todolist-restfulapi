package com.github.pdhbe.todolistrestfulapi.domain;

import com.github.pdhbe.todolistrestfulapi.ui.TodoController;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Todo {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime finishedAt;
    @Enumerated(value = EnumType.STRING)
    private TodoStatus status;

    public Todo(TodoDto todoDto) {
        if (todoDto.getTitle().isBlank()) {
            throw new TitleIsBlankException();
        }
        title = todoDto.getTitle();
        description = todoDto.getDescription();
        createdAt = LocalDateTime.now();
        status = TodoStatus.CREATED;
    }

    public Todo modify(TodoDto todoDto) {
        if (status == TodoStatus.FINISHED) {
            throw new AlreadyFinishedException();
        }
        if (todoDto.getTitle().isBlank()) {
            throw new TitleIsBlankException();
        }
        title = todoDto.getTitle();
        description = todoDto.getDescription();
        modifiedAt = LocalDateTime.now();
        status = TodoStatus.MODIFIED;
        return this;
    }

    public Todo finish() {
        finishedAt = LocalDateTime.now();
        status = TodoStatus.FINISHED;
        return this;
    }

    public WebMvcLinkBuilder makeSelfLinkBuilder() {
        return linkTo(TodoController.class).slash(id);
    }
}

