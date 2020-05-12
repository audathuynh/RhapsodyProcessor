package org.maxsure.rhapsody;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class Program implements CommandLineRunner {

	private final RhapsodyProcessor processor;

	public Program(RhapsodyProcessor processor) {
		this.processor = Preconditions.checkNotNull(processor, "processor");
	}

	public static void main(String[] args) {
		SpringApplication.run(Program.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("Running the app...");
		Path diagramOutputPath;
		if (args.length < 1 || Strings.isNullOrEmpty(args[0])) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
					.withLocale(Locale.getDefault())
					.withZone(ZoneId.systemDefault());
			String folderName = formatter.format(Instant.now());
			diagramOutputPath = Paths.get(System.getProperty("user.dir"), folderName);
			Files.createDirectories(diagramOutputPath);
		} else {
			diagramOutputPath = Paths.get(args[0]);
		}

		try {
			processor.exportDiagrams(diagramOutputPath);
		} catch (Exception e) {
			log.error("Error when processing...", e);
		}
	}

}
