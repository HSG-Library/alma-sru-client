package ch.unisg.library.systemlibrarian.sru.generator;

import com.squareup.javapoet.TypeSpec;

import java.nio.file.Path;
import java.util.List;

public class SruIndexGenerator {
	public static void main(String[] args) {
		if (args.length == 0) {
			args = new String[]{
					"https://slsp-hsg.alma.exlibrisgroup.com/view/sru/41SLSP_HSG",
					"target/generated-sources/",
			};
		}
		new SruIndexGenerator().generate(args[0], Path.of(args[1]));
	}

	public void generate(final String sruBaseUrl, final Path outputPath) {
		final List<SruIndexMeta> sruIndexMetaList = new SruIndexMetaCollector().collectSruIndexMeta(sruBaseUrl);
		final CodeGenerator codeGenerator = new CodeGenerator(outputPath);
		List<TypeSpec> indexTypeSpecList = sruIndexMetaList.stream()
				.map(codeGenerator::generateIndexClass)
				.toList();
		codeGenerator.generateIndexList(indexTypeSpecList);
	}
}
