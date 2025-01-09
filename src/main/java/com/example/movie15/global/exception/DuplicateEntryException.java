package com.example.movie15.global.exception;

public class DuplicateEntryException extends CustomException {
	public DuplicateEntryException(final ExceptionType exceptionType) {
		super(exceptionType);
	}
}
