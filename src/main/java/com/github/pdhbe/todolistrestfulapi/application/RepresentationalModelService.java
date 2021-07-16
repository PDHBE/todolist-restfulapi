package com.github.pdhbe.todolistrestfulapi.application;

import com.github.pdhbe.todolistrestfulapi.domain.Todo;
import com.github.pdhbe.todolistrestfulapi.ui.ErrorResponse;
import com.github.pdhbe.todolistrestfulapi.ui.NoContentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RepresentationalModelService {
    private final LinkService linkService;
    private final PagedResourcesAssembler<Todo> pagedResourcesAssembler;

    public EntityModel<Todo> toEntityModel(Todo todo) {
        Links todoLinks = linkService.todoLinks(todo);
        Links totalLinks = linkService.overallLinks();
        return EntityModel.of(todo, todoLinks.and(totalLinks));
    }

    public EntityModel<NoContentResponse> toEntityModel(NoContentResponse noContentResponse) {
        Links totalLinks = linkService.overallLinks();
        return EntityModel.of(noContentResponse, totalLinks);
    }

    public EntityModel<ErrorResponse> toEntityModel(ErrorResponse errorResponse) {
        Links totalLinks = linkService.overallLinks();
        return EntityModel.of(errorResponse, totalLinks);
    }

    public CollectionModel<EntityModel<Todo>> toCollectionModel(List<Todo> todoList) {
        Links totalLinks = linkService.overallLinks();
        List<EntityModel<Todo>> todoEntityModels = todoList.stream().map(this::addTodoLinksOnly).collect(Collectors.toList());
        return CollectionModel.of(todoEntityModels, totalLinks);
    }

    public PagedModel<EntityModel<Todo>> toPagedModel(Page<Todo> todoPage) {
        Links totalLinks = linkService.overallLinks();
        PagedModel<EntityModel<Todo>> todoPagedModel = pagedResourcesAssembler.toModel(todoPage, this::addTodoLinksOnly);
        todoPagedModel.add(totalLinks);
        return todoPagedModel;
    }

    private EntityModel<Todo> addTodoLinksOnly(Todo todo) {
        Links todoLinks = linkService.todoLinks(todo);
        return EntityModel.of(todo, todoLinks);
    }
}
