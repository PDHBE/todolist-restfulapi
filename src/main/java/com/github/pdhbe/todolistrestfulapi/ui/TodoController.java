package com.github.pdhbe.todolistrestfulapi.ui;

import com.github.pdhbe.todolistrestfulapi.application.RepresentationalModelService;
import com.github.pdhbe.todolistrestfulapi.application.TodoService;
import com.github.pdhbe.todolistrestfulapi.domain.Todo;
import com.github.pdhbe.todolistrestfulapi.domain.TodoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/todos", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;
    private final RepresentationalModelService representationalModelService;

    @PostMapping
    public ResponseEntity<?> createTodo(@RequestBody TodoDto todoDto) {
        Todo createdTodo = todoService.createTodo(todoDto);
        EntityModel<Todo> todoEntityModel = representationalModelService.toEntityModel(createdTodo);
        return ResponseEntity.created(createdTodo.makeSelfLinkBuilder().toUri()).body(todoEntityModel);
    }

    @GetMapping("/list")
    public ResponseEntity<?> queryTodoList() {
        List<Todo> todoList = todoService.getTodoList();
        CollectionModel<EntityModel<Todo>> todoCollectionModel = representationalModelService.toCollectionModel(todoList);
        return ResponseEntity.ok().body(todoCollectionModel);
    }

    @GetMapping("/page")
    public ResponseEntity<?> queryTodoPage(Pageable pageable) {
        Page<Todo> todoPage = todoService.getTodoPage(pageable);
        PagedModel<EntityModel<Todo>> todoPagedModel = representationalModelService.toPagedModel(todoPage);
        return ResponseEntity.ok().body(todoPagedModel);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAllTodos() {
        todoService.deleteAllTodos();
        EntityModel<NoContentResponse> noContentEntityModel = representationalModelService.toEntityModel(new NoContentResponse("All Todos deleted."));
        return ResponseEntity.ok().body(noContentEntityModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> queryTodo(@PathVariable Long id) {
        Todo queriedTodo = todoService.getTodo(id);
        EntityModel<Todo> todoEntityModel = representationalModelService.toEntityModel(queriedTodo);
        return ResponseEntity.ok().body(todoEntityModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modifyTodo(@PathVariable Long id, @RequestBody TodoDto todoDto) {
        Todo modifiedTodo = todoService.modifyTodo(id, todoDto);
        EntityModel<Todo> todoEntityModel = representationalModelService.toEntityModel(modifiedTodo);
        return ResponseEntity.ok().body(todoEntityModel);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> finishTodo(@PathVariable Long id) {
        Todo finishedTodo = todoService.finishTodo(id);
        EntityModel<Todo> todoEntityModel = representationalModelService.toEntityModel(finishedTodo);
        return ResponseEntity.ok().body(todoEntityModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        EntityModel<NoContentResponse> noContentEntityModel = representationalModelService.toEntityModel(new NoContentResponse("Todo " + id + " is deleted."));
        return ResponseEntity.ok().body(noContentEntityModel);
    }
}
