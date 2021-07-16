package com.github.pdhbe.todolistrestfulapi.ui;

import com.github.pdhbe.todolistrestfulapi.application.RepresentationalModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class IndexController {
    private final RepresentationalModelService representationalModelService;

    @GetMapping("/")
    public ResponseEntity<?> getIndex() {
        EntityModel<NoContentResponse> noContentEntityModel = representationalModelService.toEntityModel(new NoContentResponse("Welcome TodoList RESTful API !!"));
        return ResponseEntity.ok().body(noContentEntityModel);
    }
}
