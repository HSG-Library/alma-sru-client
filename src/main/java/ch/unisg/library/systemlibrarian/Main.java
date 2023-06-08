package ch.unisg.library.systemlibrarian;

import ch.unisg.library.systemlibrarian.scripts.FindRelatedRecords;
import ch.unisg.library.systemlibrarian.scripts.FindRootRecords;

public class Main {
	public static void main(String[] args) {
		//scriptFindRootRecords();
		scriptFindRelatedRecords();
	}

	public static void scriptFindRootRecords() {
		FindRootRecords findRootRecords = new FindRootRecords(
				"/Users/jonas/OneDrive - Universität St.Gallen/transfer/IFF-SLSP-Script-output.xlsx",
				"/Users/jonas/OneDrive - Universität St.Gallen/transfer/IFF-oberaufnahmen.xlsx"
		);
		findRootRecords.getRootRecords("A");
	}

	public static void scriptFindRelatedRecords() {
		FindRelatedRecords findRelatedRecords = new FindRelatedRecords(
				"/Users/jonas/OneDrive - Universität St.Gallen/transfer/IFF-SLSP-Script-output.xlsx",
				"/Users/jonas/OneDrive - Universität St.Gallen/transfer/IFF-verknuepfte.xlsx"
		);
		findRelatedRecords.findRelatedRecords("A");
	}
}
