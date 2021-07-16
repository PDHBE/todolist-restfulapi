package com.github.pdhbe.todolistrestfulapi.application;

import com.github.pdhbe.todolistrestfulapi.domain.Todo;
import com.github.pdhbe.todolistrestfulapi.domain.TodoDto;
import com.github.pdhbe.todolistrestfulapi.domain.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;

    @Transactional
    public Todo createTodo(TodoDto todoDto) {
        Todo todo = new Todo(todoDto);
        return todoRepository.save(todo);
    }

    @Transactional(readOnly = true)
    public List<Todo> getTodoList() {
        List<Todo> todoList = todoRepository.findAll();
        if (todoList.isEmpty()) {
            throw new NoTodoException();
        }
        return todoList;
    }

    @Transactional(readOnly = true)
    public Page<Todo> getTodoPage(Pageable pageable) {
        Page<Todo> todoPage = todoRepository.findAll(pageable);
        if (todoPage.isEmpty()) {
            throw new NoTodoException();
        }
        return todoPage;
    }

    @Transactional(readOnly = true)
    public Todo getTodo(Long id) {
        Optional<Todo> optionalTodo = todoRepository.findById(id);
        if (optionalTodo.isEmpty()) {
            throw new NoTodoException();
        }
        return optionalTodo.get();
    }

    @Transactional
    public Todo modifyTodo(Long id, TodoDto todoDto) {
        Optional<Todo> optionalTodo = todoRepository.findById(id);
        if (optionalTodo.isEmpty()) {
            throw new NoTodoException();
        }

        Todo todo = optionalTodo.get();
        return todo.modify(todoDto);
    }

    @Transactional
    public Todo finishTodo(Long id) {
        Optional<Todo> optionalTodo = todoRepository.findById(id);
        if (optionalTodo.isEmpty()) {
            throw new NoTodoException();
        }

        Todo todo = optionalTodo.get();
        return todo.finish();
    }

    @Transactional
    public void deleteTodo(Long id) {
        if (todoRepository.findById(id).isEmpty()) {
            throw new NoTodoException();
        }
        todoRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllTodos() {
        if (todoRepository.count() == 0) {
            throw new NoTodoException();
        }
        todoRepository.deleteAll();
    }
}
