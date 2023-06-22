package ch.unisg.library.systemlibrarian.sru.query;

import java.util.Optional;
import java.util.stream.Stream;

public enum Relation {
	CONTAINS_WORDS("all"),
	CONTAINS_PHRASE("="),
	LESS_THAN("<"),
	GREATER_THAN(">"),
	LESS_THAN_OR_EQUAL("<="),
	GREATER_THAN_OR_EQUAL(">="),
	EQUAL_TO("=="),
	NOT_EQUAL_TO("<>");

	private final String value;

	Relation(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static Optional<Relation> findByOperator(final String op) {
		return Stream.of(Relation.values())
				.filter(relation -> relation.value.equals(op))
				.findFirst();
	}

	@Override
	public String toString() {
		return "Relation{" +
				"value='" + value + '\'' +
				"} " + super.toString();
	}
}
