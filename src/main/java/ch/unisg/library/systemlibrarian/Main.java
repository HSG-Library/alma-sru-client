package ch.unisg.library.systemlibrarian;

import ch.unisg.library.systemlibrarian.scripts.FindRelatedRecords;
import ch.unisg.library.systemlibrarian.scripts.FindRootRecords;
import ch.unisg.library.systemlibrarian.scripts.SruScript;

public class Main {
	public static void main(String[] args) {
		//scriptFindRootRecords();
		//scriptFindRelatedRecords();
	}

	public static void scriptFindRootRecords() {
		SruScript findRootRecords = new FindRootRecords()
				.input("/Users/jonas/OneDrive - Universität St.Gallen/transfer/IFF-SLSP-Script-output.xlsx", "A")
				.output("/Users/jonas/OneDrive - Universität St.Gallen/transfer/IFF-oberaufnahmen.xlsx");

		findRootRecords.processFiles();
	}

	public static void scriptFindRelatedRecords() {
		SruScript findRelatedRecords = new FindRelatedRecords()
				.input("/Users/jonas/OneDrive - Universität St.Gallen/transfer/IFF-SLSP-Script-output.xlsx", "A")
				.output("/Users/jonas/OneDrive - Universität St.Gallen/transfer/IFF-verknuepfte.xlsx");
		findRelatedRecords.processFiles();
	}
}
