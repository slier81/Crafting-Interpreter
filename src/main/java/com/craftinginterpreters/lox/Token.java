package com.craftinginterpreters.lox;

public class Token {
    final TokenType type;
    final String lexeme;
    final Object value;
    final int line;

    public Token(TokenType type, String lexeme, Object value, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.value = value;
        this.line = line;
    }

    public String toString() {
        return type + " " + lexeme + " " + value;
    }
}