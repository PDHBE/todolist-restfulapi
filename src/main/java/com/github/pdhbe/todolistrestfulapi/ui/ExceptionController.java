package com.github.pdhbe.todolistrestfulapi.ui;

import com.github.pdhbe.todolistrestfulapi.application.NoTodoException;
import com.github.pdhbe.todolistrestfulapi.application.RepresentationalModelService;
import com.github.pdhbe.todolistrestfulapi.domain.AlreadyFinishedException;
import com.github.pdhbe.todolistrestfulapi.domain.TitleIsBlankException;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {
    private final RepresentationalModelService representationalModelService;

    @ExceptionHandler(TitleIsBlankException.class)
    public ResponseEntity<?> handleTitleIsBlankException() {
        ErrorResponse errorResponse = ErrorResponse.builder().status("400 Bad Request").msg("Title must not be blank").build();
        EntityModel<ErrorResponse> errorResponseEntityModel = representationalModelService.toEntityModel(errorResponse);
        return ResponseEntity.badRequest().body(errorResponseEntityModel);
    }

    @ExceptionHandler(AlreadyFinishedException.class)
    public ResponseEntity<?> handleAlreadyFinishedException() {
        ErrorResponse errorResponse = ErrorResponse.builder().status("400 Bad Request").msg("Cannot modify Todo already finished").build();
        EntityModel<ErrorResponse> errorResponseEntityModel = representationalModelService.toEntityModel(errorResponse);
        return ResponseEntity.badRequest().body(errorResponseEntityModel);
    }

    @ExceptionHandler(NoTodoException.class)
    public ResponseEntity<?> handleNoTodoException() {
        ErrorResponse errorResponse = ErrorResponse.builder().status("404 Not Found").msg("Does not exist").build();
        EntityModel<ErrorResponse> errorResponseEntityModel = representationalModelService.toEntityModel(errorResponse);
        return ResponseEntity.badRequest().body(errorResponseEntityModel);
    }
}
