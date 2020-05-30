package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.craftinginterpreters.lox.TokenType.*;


public class Scanner {
    private final String source;
    private final List<Token> tokens;
    private int lexemePos = 0;
    private int currentPos = 0;
    private int line = 1;
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }

    public Scanner(String source) {
        this.source = source;
        this.tokens = new ArrayList<>();
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            this.lexemePos = this.currentPos;
            this.scanToken();
        }

        this.tokens.add(new Token(EOF, "", null, line));
        return this.tokens;
    }

    private void scanToken() {
        char c = next();

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

            case '!' -> addToken(isMatch('=') ? BANG_EQUAL : BANG);
            case '=' -> addToken(isMatch('=') ? EQUAL_EQUAL : EQUAL);
            case '<' -> addToken(isMatch('=') ? LESS_EQUAL : LESS);
            case '>' -> addToken(isMatch('=') ? GREATER_EQUAL : GREATER);

            case '\n' -> this.line++;
            case '"' -> this.string();

            case '/' -> {
                if (isMatch('/')) {
                    // A comment goes until the end of the line.
                    while (peek() != '\n' && !isAtEnd()) next();
                } else {
                    addToken(SLASH);
                }
            }

            case ' ', '\r', '\t' -> {
            }

            default -> {
                if (isDigit(c)) {
                    this.number();
                } else if (isAlpha(c)) {
                    this.identifier();
                } else {
                    Lox.error(this.line, "Unexpected character.");
                }
            }
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) next();

        String token = this.source.substring(this.lexemePos, this.currentPos);
        TokenType tokenType = Scanner.keywords.get(token);

        if (tokenType == null) {
            tokenType = IDENTIFIER;
        }

        addToken(tokenType);
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') this.line++;
            next();
        }

        if (isAtEnd()) {
            Lox.error(this.line, "Unterminated string.");
            return;
        }

        next(); // The closing "

        String value = this.source.substring(lexemePos + 1, currentPos - 1); // Trim the surrounding quotes
        this.addToken(STRING, value);
    }

    private void number() {
        while (isDigit(peek())) next();

        if (peek() == '.' && isDigit(peekNext())) {
            next(); // Consume the '.'
            while (isDigit(peek())) next(); // Consume remaining fraction parts
        }

        this.addToken(NUMBER, Double.parseDouble(this.source.substring(this.lexemePos, this.currentPos)));
    }

    private boolean isMatch(char expected) {
        if (isAtEnd()) return false;
        if (this.source.charAt(currentPos) != expected) return false;

        this.currentPos++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return this.source.charAt(this.currentPos);
    }

    private char peekNext() {
        if (this.currentPos + 1 >= this.source.length()) return '\0';
        return this.source.charAt(this.currentPos + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isAtEnd() {
        return this.currentPos >= source.length();
    }

    private char next() {
        currentPos++;
        return this.source.charAt(currentPos - 1);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object value) {
        String token = source.substring(lexemePos, currentPos);
        this.tokens.add(new Token(type, token, value, this.line));
    }
}
