package com.example.movie15.global.model;

import java.util.Arrays;

public enum FileExtension {
	JPG("jpg"), PNG("png"), PDF("pdf");
	private final String Fileextension;

	FileExtension(String fileextension) {
		Fileextension = fileextension;
	}

	public String getExtension() {
		return Fileextension;
	}

	public static boolean isValidExtension(String extension) {
		return Arrays.stream(FileExtension.values())
			.anyMatch(fileType -> fileType.getExtension().equalsIgnoreCase(extension));
	}
}
