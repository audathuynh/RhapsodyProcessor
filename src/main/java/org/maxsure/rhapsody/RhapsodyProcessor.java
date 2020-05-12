package org.maxsure.rhapsody;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import com.google.common.base.Preconditions;
import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPDiagram;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.RhapsodyAppServer;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RhapsodyProcessor {

    private final List<IRPProject> activeProjects = new ArrayList<>();

    private final LatexHelper latexHelper;

    private final StringHelper stringHelper;

    public RhapsodyProcessor(StringHelper stringHelper, LatexHelper latexHelper,
            @Value("classpath:rhapsody.dll") Resource fileResource) {
        this.latexHelper = Preconditions.checkNotNull(latexHelper, "latexHelper");
        this.stringHelper = Preconditions.checkNotNull(stringHelper, "stringHelper");

        File dir = new File(new File(System.getProperty("user.home")), ".rhapsodyprocessor");
        File dllFile = new File(dir, "rhapsody.dll");
        if (Files.notExists(dllFile.toPath())) {
            dllFile.getParentFile().mkdirs();
            try (InputStream inputStream = fileResource.getInputStream()) {
                Files.copy(inputStream, dllFile.toPath());
            } catch (IOException e) {
                log.error("Error when reading and coying file resource", e);
            }
        }

        @SuppressWarnings("unchecked")
        List<String> rhpAppIDs = RhapsodyAppServer.getActiveRhapsodyApplicationIDList();
        for (String rhpAppID : rhpAppIDs) {
            IRPApplication rhapsodyApp =
                    RhapsodyAppServer.getActiveRhapsodyApplicationByID(rhpAppID);
            activeProjects.add(rhapsodyApp.activeProject());
        }
    }

    @SuppressWarnings("unchecked")
    public void exportDiagrams(Path outputPath) throws IOException {
        for (IRPProject activeProject : activeProjects) {
            activeProject.setPropertyValue("General.Graphics.ExportedDiagramScale", "NoPagination");

            String projectName = activeProject.getName();
            Path latexFilePath =
                    Paths.get(outputPath.toAbsolutePath().toString(), projectName + ".txt");
            if (Files.exists(latexFilePath)) {
                Files.delete(latexFilePath);
            }

            List<IRPPackage> packages = activeProject.getPackages().toList();
            Collections.sort(packages, Comparator.comparing(IRPPackage::getName));

            log.info("Starting to export [{}] diagrams in the project [{}]...", packages.size(),
                    projectName);
            for (IRPPackage pkg : packages) {
                List<IRPDiagram> diagrams = pkg.getSequenceDiagrams().toList();
                int diagramSize = diagrams.size();
                if (diagramSize > 0) {
                    String packageName = pkg.getName();
                    log.info("Exporting [{}] sequence diagrams in the package [{}]...", diagramSize,
                            packageName);

                    String latex = latexHelper.generatePackageLaTeX(packageName);
                    appendToLaTeXFile(latexFilePath, latex);

                    exportSequenceDiagram(diagrams, outputPath, latexFilePath);
                }
            }

            List<IRPDiagram> diagrams = activeProject.getSequenceDiagrams().toList();
            if (!diagrams.isEmpty()) {
                log.info("\nStarting to export [{}] diagrams not in packages...", diagrams.size());
                exportSequenceDiagram(diagrams, outputPath, latexFilePath);
            }
        }
    }

    public void exportSequenceDiagram(List<IRPDiagram> diagrams, Path figureOutputPath,
            Path latexFilePath) throws IOException {
        Collections.sort(diagrams, Comparator.comparing(IRPDiagram::getName));

        StringBuilder latexBuilder = new StringBuilder();
        for (IRPDiagram diagram : diagrams) {
            String diagramName = stringHelper.standardiseName(diagram.getName());

            String photoFileName = diagramName + ".jpg";
            Path photoFilePath =
                    Paths.get(figureOutputPath.toAbsolutePath().toString(), photoFileName);
            if (Files.exists(photoFilePath)) {
                Files.delete(photoFilePath);
            }
            String photoFilePathStr = photoFilePath.toAbsolutePath().toString();
            log.info(photoFilePathStr);
            diagram.getPictureAs(photoFilePathStr, "JPEG", 0, null);

            String diagramLatex = latexHelper.generateDiagramLaTeX(diagramName);
            latexBuilder.append(diagramLatex);
        }

        appendToLaTeXFile(latexFilePath, latexBuilder.toString());
    }

    private void appendToLaTeXFile(Path latexFilePath, String str) {
        File latexFile = new File(latexFilePath.toAbsolutePath().toString());
        try (FileWriter writer = new FileWriter(latexFile, true)) {
            writer.write(str);
        } catch (IOException e) {
            log.error("error when writing into latex file", e);
        }
    }

}
