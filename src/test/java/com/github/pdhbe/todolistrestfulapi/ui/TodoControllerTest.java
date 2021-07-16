package com.github.pdhbe.todolistrestfulapi.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pdhbe.todolistrestfulapi.config.RestDocsConfiguration;
import com.github.pdhbe.todolistrestfulapi.domain.Todo;
import com.github.pdhbe.todolistrestfulapi.domain.TodoDto;
import com.github.pdhbe.todolistrestfulapi.domain.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
class TodoControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TodoRepository todoRepository;

    @Test
    void createTodo_success() throws Exception {
        todoRepository.deleteAll();

        TodoDto todoDto = TodoDto.builder()
                .title("title1")
                .description("description1")
                .build();

        mockMvc.perform(post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todoDto))
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("create-todo",
                        requestFields(
                                fieldWithPath("title").description("title of todo"),
                                fieldWithPath("description").description("description of todo")
                        ),

                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location uri of todo")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("id").description("id of todo"),
                                fieldWithPath("title").description("title of todo"),
                                fieldWithPath("description").description("description of todo"),
                                fieldWithPath("createdAt").description("date time of todo created"),
                                fieldWithPath("modifiedAt").description("date time of todo modified"),
                                fieldWithPath("finishedAt").description("date time of todo finished"),
                                fieldWithPath("status").description("current status of todo")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("modify-todo").description("link to modify todo"),
                                linkWithRel("finish-todo").description("link to finish todo"),
                                linkWithRel("delete-todo").description("link to delete todo"),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("create-todo").description("link to create todo"),
                                linkWithRel("get-todo-list").description("link to get todo list"),
                                linkWithRel("get-todo-page").description("link to get todo page"),
                                linkWithRel("delete-all-todos").description("link to delete all todos")
                        )));
    }

    @Test
    void createTodo_fail_titleIsBlank() throws Exception {
        TodoDto todoDto = TodoDto.builder()
                .title("  ")
                .description("description1")
                .build();

        mockMvc.perform(post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todoDto))
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTodoList_success() throws Exception {
        todoRepository.deleteAll();
        IntStream.range(0, 5).forEach(this::saveTodo);

        mockMvc.perform(get("/todos/list")
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("get-todo-list",
                        relaxedResponseFields(
                                fieldWithPath("_embedded.todoList").description("list of all todos")
                        ),
                        links(
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("create-todo").description("link to create todo"),
                                linkWithRel("get-todo-list").description("link to get todo list"),
                                linkWithRel("get-todo-page").description("link to get todo page"),
                                linkWithRel("delete-all-todos").description("link to delete all todos")
                        )));
    }

    @Test
    void getTodoList_fail_notFound() throws Exception {
        todoRepository.deleteAll();

        mockMvc.perform(get("/todos/list")
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTodoPage_success() throws Exception {
        todoRepository.deleteAll();
        IntStream.range(0, 30).forEach(this::saveTodo);

        mockMvc.perform(get("/todos/page")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "id,ASC")
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("get-todo-page",
                        relaxedResponseFields(
                                fieldWithPath("_embedded.todoList").description("todos on the page"),
                                fieldWithPath("page").description("information of page")
                        ),
                        links(
                                linkWithRel("first").description("link to first page"),
                                linkWithRel("prev").description("link to previous page"),
                                linkWithRel("self").description("link to current page"),
                                linkWithRel("next").description("link to next page"),
                                linkWithRel("last").description("link to last page"),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("create-todo").description("link to create todo"),
                                linkWithRel("get-todo-list").description("link to get todo list"),
                                linkWithRel("get-todo-page").description("link to get todo page"),
                                linkWithRel("delete-all-todos").description("link to delete all todos")
                        )));
    }

    @Test
    void getTodoPage_success_default() throws Exception {
        IntStream.range(0,20).forEach(this::saveTodo);

        mockMvc.perform(get("/todos/page"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getTodoPage_fail_notFound() throws Exception {
        todoRepository.deleteAll();

        mockMvc.perform(get("/todos/page")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "id,ASC")
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTodo_success() throws Exception {
        Todo todo = saveTodo(1);

        mockMvc.perform(get("/todos/" + todo.getId())
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("get-todo",
                        relaxedResponseFields(
                                fieldWithPath("id").description("id of todo"),
                                fieldWithPath("title").description("title of todo"),
                                fieldWithPath("description").description("description of todo"),
                                fieldWithPath("createdAt").description("date time of todo created"),
                                fieldWithPath("modifiedAt").description("date time of todo modified"),
                                fieldWithPath("finishedAt").description("date time of todo finished"),
                                fieldWithPath("status").description("current status of todo")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("modify-todo").description("link to modify todo"),
                                linkWithRel("finish-todo").description("link to finish todo"),
                                linkWithRel("delete-todo").description("link to delete todo"),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("create-todo").description("link to create todo"),
                                linkWithRel("get-todo-list").description("link to get todo list"),
                                linkWithRel("get-todo-page").description("link to get todo page"),
                                linkWithRel("delete-all-todos").description("link to delete all todos")
                        )));
    }

    @Test
    void getTodo_fail_notFound() throws Exception {
        todoRepository.deleteAll();

        mockMvc.perform(get("/todos/" + "1234")
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void modifyTodo_success() throws Exception {
        Todo todo = saveTodo(1);
        TodoDto todoDto = TodoDto.builder()
                .title("modifyTitle")
                .description("")
                .build();

        mockMvc.perform(put("/todos/" + todo.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todoDto))
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("modify-todo",
                        requestFields(
                                fieldWithPath("title").description("title of todo"),
                                fieldWithPath("description").description("description of todo")
                        ),

                        relaxedResponseFields(
                                fieldWithPath("id").description("id of todo"),
                                fieldWithPath("title").description("title of todo"),
                                fieldWithPath("description").description("description of todo"),
                                fieldWithPath("createdAt").description("date time of todo created"),
                                fieldWithPath("modifiedAt").description("date time of todo modified"),
                                fieldWithPath("finishedAt").description("date time of todo finished"),
                                fieldWithPath("status").description("current status of todo")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("modify-todo").description("link to modify todo"),
                                linkWithRel("finish-todo").description("link to finish todo"),
                                linkWithRel("delete-todo").description("link to delete todo"),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("create-todo").description("link to create todo"),
                                linkWithRel("get-todo-list").description("link to get todo list"),
                                linkWithRel("get-todo-page").description("link to get todo page"),
                                linkWithRel("delete-all-todos").description("link to delete all todos")
                        )));
    }

    @Test
    void modifyTodo_fail_notFound() throws Exception {
        todoRepository.deleteAll();
        TodoDto todoDto = TodoDto.builder()
                .title("title")
                .description("description")
                .build();

        mockMvc.perform(put("/todos/" + "1234")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todoDto))
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void modifyTodo_fail_titleIsBlank() throws Exception {
        Todo todo = saveTodo(1);
        TodoDto todoDto = TodoDto.builder()
                .title("  ")
                .description("description")
                .build();

        mockMvc.perform(put("/todos/" + todo.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todoDto))
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void modifyTodo_fail_alreadyFinished() throws Exception {
        Todo todo = saveTodo(1);
        mockMvc.perform(patch("/todos/" + todo.getId())
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        TodoDto todoDto = TodoDto.builder()
                .title("modifyTitle")
                .description("")
                .build();

        mockMvc.perform(put("/todos/" + todo.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todoDto))
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void finishTodo_success() throws Exception {
        Todo todo = saveTodo(1);

        mockMvc.perform(patch("/todos/" + todo.getId())
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("finish-todo",
                        relaxedResponseFields(
                                fieldWithPath("id").description("id of todo"),
                                fieldWithPath("title").description("title of todo"),
                                fieldWithPath("description").description("description of todo"),
                                fieldWithPath("createdAt").description("date time of todo created"),
                                fieldWithPath("modifiedAt").description("date time of todo modified"),
                                fieldWithPath("finishedAt").description("date time of todo finished"),
                                fieldWithPath("status").description("current status of todo")
                        ),
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("modify-todo").description("link to modify todo"),
                                linkWithRel("finish-todo").description("link to finish todo"),
                                linkWithRel("delete-todo").description("link to delete todo"),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("create-todo").description("link to create todo"),
                                linkWithRel("get-todo-list").description("link to get todo list"),
                                linkWithRel("get-todo-page").description("link to get todo page"),
                                linkWithRel("delete-all-todos").description("link to delete all todos")
                        )));
    }

    @Test
    void finishTodo_fail_notFound() throws Exception {
        todoRepository.deleteAll();

        mockMvc.perform(patch("/todos/" + "1234")
                .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteAllTodos() throws Exception {
        IntStream.range(0, 30).forEach(this::saveTodo);

        mockMvc.perform(delete("/todos"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("delete-all-todos",
                        links(
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("create-todo").description("link to create todo"),
                                linkWithRel("get-todo-list").description("link to get todo list"),
                                linkWithRel("get-todo-page").description("link to get todo page"),
                                linkWithRel("delete-all-todos").description("link to delete all todos")
                        )));
    }

    @Test
    void deleteTodo() throws Exception {
        Todo todo = saveTodo(1);

        mockMvc.perform(delete("/todos/" + todo.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("delete-todo",
                        links(
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("create-todo").description("link to create todo"),
                                linkWithRel("get-todo-list").description("link to get todo list"),
                                linkWithRel("get-todo-page").description("link to get todo page"),
                                linkWithRel("delete-all-todos").description("link to delete all todos")
                        )));
    }

    private Todo saveTodo(int i) {
        Todo todo = new Todo(TodoDto.builder().title("title " + i).build());
        return todoRepository.save(todo);
    }
}