package ch.unisg.library.systemlibrarian.sru.generator;

import com.squareup.javapoet.TypeSpec;

import java.nio.file.Path;
import java.util.List;

public class SruQueryBuilderGenerator {
	public static void main(String[] args) {
		if (args.length == 0) {
			args = new String[]{
					"target/generated-sources/",
			};
		}
		new SruQueryBuilderGenerator().generate(Path.of(args[0]));
	}

	public void generate(Path outputPath) {
		final List<SruIndexMeta> sruIndexMetaList = new SruIndexMetaCollector().collectSruIndexMeta();
		final CodeGenerator codeGenerator = new CodeGenerator(outputPath);
		List<TypeSpec> indexTypeSpecList = sruIndexMetaList.stream()
				.map(codeGenerator::generateIndexClass)
				.toList();
		codeGenerator.generateIndexList(indexTypeSpecList);
	}
}
