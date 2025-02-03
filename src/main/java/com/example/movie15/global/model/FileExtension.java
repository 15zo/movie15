package com.example.movie15.global.model;



import java.util.Arrays;

public enum FileExtension {
	JPG("jpg"),
	PNG("png"),
	PDF("pdf"),
	UNKNOWN("unknown");
	private final String extension;

	FileExtension(String extension) {
		this.extension = extension;
	}

	public String getExtension() {
		return extension;
	}

	public static boolean isValidExtension(String extension) {
		return Arrays.stream(FileExtension.values())
			.anyMatch(fileType -> fileType.getExtension().equalsIgnoreCase(extension));
	}
}
