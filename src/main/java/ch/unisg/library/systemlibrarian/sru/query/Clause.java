package ch.unisg.library.systemlibrarian.sru.query;

import org.apache.commons.lang3.StringUtils;

public class Clause {
	private final static String SPACE = " ";
	private final SruIndex index;
	private final String value;
	private final Relation relation;
	private final BoolOp boolOp;

	public Clause(SruIndex index, String value, Relation relation) {
		this.index = index;
		this.value = value;
		this.relation = relation;
		this.boolOp = BoolOp.NONE;
	}

	private Clause(SruIndex index, String value, Relation relation, BoolOp op) {
		this.index = index;
		this.value = value;
		this.relation = relation;
		this.boolOp = op;
	}

	public Clause with(BoolOp op) {
		return new Clause(this.index, this.value, this.relation, op);
	}

	public SruIndex getIndex() {
		return index;
	}

	public String getValue() {
		return value;
	}

	public Relation getRelation() {
		return relation;
	}

	public BoolOp getBoolOp() {
		return boolOp;
	}

	public String string() {
		final String clauseString = this.index.getName() + SPACE + relation.getValue() + SPACE + StringUtils.wrapIfMissing(value, '"');
		if (BoolOp.NONE == boolOp) {
			return clauseString;
		}
		return SPACE + boolOp.getValue() + SPACE + clauseString;
	}
}
