package org.maxsure.rhapsody;

import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

@Component
public class LatexHelper {
	
	private final StringHelper stringHelper;
	
	public LatexHelper(StringHelper stringHelper) {
		this.stringHelper = Preconditions.checkNotNull(stringHelper, "stringHelper");
	}

	public String generatePackageLaTeX(String packageName) {
		packageName = stringHelper.standardiseName(packageName);
		String readableName = stringHelper.splitCamelCase(packageName);

		StringBuilder builder = new StringBuilder();
		builder.append(System.lineSeparator());
		builder.append("\\newpage" + System.lineSeparator());
		builder.append(String.format("\\subsubsection{%s}", readableName) + System.lineSeparator());
		builder.append(String.format("\\label{sec:%s}", packageName) + System.lineSeparator());
		builder.append(System.lineSeparator());
		return builder.toString();
	}

	public String generateDiagramLaTeX(String diagramName) {
		diagramName = stringHelper.standardiseName(diagramName);
		String readableName = stringHelper.splitCamelCase(diagramName);

		StringBuilder builder = new StringBuilder();
		builder.append(System.lineSeparator());
		builder.append("\\newpage" + System.lineSeparator());
		builder.append(String.format("\\subsubsubsection{%s}", readableName) + System.lineSeparator());
		builder.append(String.format("\\label{sec:%s}", diagramName) + System.lineSeparator());
		builder.append(System.lineSeparator());
		builder.append("\\begin{center}" + System.lineSeparator());
		builder.append(String.format("    \\includegraphics[width=1\\textwidth]{\"%s\"}", diagramName) + System.lineSeparator());
		builder.append(String.format("    \\captionof{figure}{%s}", readableName) + System.lineSeparator());
		builder.append(String.format("    \\label{fig:%s}", diagramName) + System.lineSeparator());
		builder.append("\\end{center}" + System.lineSeparator());
		builder.append(System.lineSeparator());
		return builder.toString();
	}

}
