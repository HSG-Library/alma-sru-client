package ch.unisg.library.systemlibrarian.sru.query;

public class BoolOpWrapper {

	public static OrWrapper OR = new OrWrapper();
	public static AndWrapper AND = new AndWrapper();

	public static class OrWrapper {

		private OrWrapper() {
		}

		public Clause wrap(final Clause clause) {
			return clause.with(BoolOp.OR);
		}
	}

	public static class AndWrapper {
		private AndWrapper() {
		}

		public Clause wrap(final Clause clause) {
			return clause.with(BoolOp.AND);
		}
	}
}
