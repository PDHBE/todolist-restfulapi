package com.github.pdhbe.todolistrestfulapi.application;

import com.github.pdhbe.todolistrestfulapi.domain.Todo;
import com.github.pdhbe.todolistrestfulapi.ui.TodoController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Service
public class LinkService {
    public Links todoLinks(Todo todo) {
        return Links.of(todo.makeSelfLinkBuilder().withSelfRel(),
                todo.makeSelfLinkBuilder().withRel("modify-todo"),
                todo.makeSelfLinkBuilder().withRel("finish-todo"),
                todo.makeSelfLinkBuilder().withRel("delete-todo"));
    }

    public Links overallLinks() {
        return Links.of(Link.of("/docs/index.html").withRel("profile"),
                linkTo(TodoController.class).withRel("create-todo"),
                linkTo(TodoController.class).slash("/list").withRel("get-todo-list"),
                linkTo(TodoController.class).slash("/page").withRel("get-todo-page"),
                linkTo(TodoController.class).withRel("delete-all-todos"));
    }
}
