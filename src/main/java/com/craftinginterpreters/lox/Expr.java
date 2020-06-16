package com.craftinginterpreters.lox;

import java.util.List;

abstract class Expr {

	abstract <R> R accept(Visitor<R> visitor);

	interface Visitor<R> {
		R visit(Binary binary);
		R visit(Grouping grouping);
		R visit(Literal literal);
		R visit(Unary unary);
	}

	static class Binary extends Expr {
		final Expr left;
		final Token operator;
		final Expr right;

		Binary(Expr left, Token operator, Expr right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visit(this);
		}
	}

	static class Grouping extends Expr {
		final Expr expression;

		Grouping(Expr expression) {
			this.expression = expression;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visit(this);
		}
	}

	static class Literal extends Expr {
		final Object value;

		Literal(Object value) {
			this.value = value;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visit(this);
		}
	}

	static class Unary extends Expr {
		final Token operator;
		final Expr right;

		Unary(Token operator, Expr right) {
			this.operator = operator;
			this.right = right;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visit(this);
		}
	}

}
