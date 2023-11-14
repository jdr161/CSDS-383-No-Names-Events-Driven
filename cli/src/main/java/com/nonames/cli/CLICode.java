package com.nonames.cli;

public enum CLICode {
    MAIN_MENU(0),
    NO_ERROR_NO_REPEAT_OP(1),
    CONTINUE(2);

    private final int code;

    CLICode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
