package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;

import static com.craftinginterpreters.lox.TokenType.*;

public class Scanner {

    private final String source;
    private final List<Token> tokens;
    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Scanner(String source) {
        this.source = source;
        this.tokens = new ArrayList<>();
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {

        }

        this.tokens.add(new Token(EOF, "", null, line));
        return this.tokens;
    }

    private void scanToken() {
        char c = advance();

        switch (c) {
            case '(' -> addToken(LEFT_PAREN);
            case ')' -> addToken(RIGHT_PAREN);
            case '{' -> addToken(LEFT_BRACE);
            case '}' -> addToken(RIGHT_BRACE);
            case ',' -> addToken(COMMA);
            case '.' -> addToken(DOT);
            case '-' -> addToken(MINUS);
            case '+' -> addToken(PLUS);
            case ';' -> addToken(SEMICOLON);
            case '*' -> addToken(STAR);

            case '!' -> addToken(match('=') ? BANG_EQUAL : BANG);
            case '=' -> addToken(match('=') ? EQUAL_EQUAL : EQUAL);
            case '<' -> addToken(match('=') ? LESS_EQUAL : LESS);
            case '>' -> addToken(match('=') ? GREATER_EQUAL : GREATER);

            case '/' -> {
                if (match('/')) {
                    // A comment goes until the end of the line.
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(SLASH);
                }
            }

            case ' ', '\r', '\t' -> {
            }

            case '\n' -> this.line++;

            default -> Lox.error(this.line, "Unexpected character.");
        }
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;

        if (this.source.charAt(current) != expected) return false;

        this.current++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return this.source.charAt(this.current);
    }

    private boolean isAtEnd() {
        return this.current >= source.length();
    }

    private char advance() {
        current++;
        return this.source.charAt(current - 1);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String token = source.substring(start, current);
        this.tokens.add(new Token(type, token, literal, line));
    }

}
