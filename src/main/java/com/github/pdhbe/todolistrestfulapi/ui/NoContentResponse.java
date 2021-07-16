package com.github.pdhbe.todolistrestfulapi.ui;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class NoContentResponse {
    String msg;

    public NoContentResponse(String msg) {
        this.msg = msg;
    }
}
