package com.github.pdhbe.todolistrestfulapi.ui;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ErrorResponse {
    String status;
    String msg;

    @Builder
    public ErrorResponse(String status, String msg) {
        this.status = status;
        this.msg = msg;
    }
}
