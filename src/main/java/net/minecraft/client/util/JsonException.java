package net.minecraft.client.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

public class JsonException extends IOException {
	private final List<Entry> entries = Lists.<Entry>newArrayList();
	private final String message;

	public JsonException(String messageIn) {
		this.entries.add(new Entry());
		this.message = messageIn;
	}

	public JsonException(String messageIn, Throwable cause) {
		super(cause);
		this.entries.add(new Entry());
		this.message = messageIn;
	}

	public void prependJsonKey(String key) {
		((Entry) this.entries.get(0)).addJsonKey(key);
	}

	public void setFilenameAndFlush(String filenameIn) {
		(this.entries.get(0)).filename = filenameIn;
		this.entries.add(0, new Entry());
	}

	public String getMessage() {
		return "Invalid " + this.entries.get(this.entries.size() - 1) + ": " + this.message;
	}

	public static JsonException forException(Exception exception) {
		if (exception instanceof JsonException) {
			return (JsonException) exception;
		} else {
			String s = exception.getMessage();

			if (exception instanceof FileNotFoundException) {
				s = "File not found";
			}

			return new JsonException(s, exception);
		}
	}

	public static class Entry {
		private String filename;
		private final List<String> jsonKeys;

		private Entry() {
			this.jsonKeys = Lists.<String>newArrayList();
		}

		private void addJsonKey(String key) {
			this.jsonKeys.add(0, key);
		}

		public String getJsonKeys() {
			return StringUtils.join((Iterable) this.jsonKeys, "->");
		}

		public String toString() {
			if (this.filename != null) {
				return this.jsonKeys.isEmpty() ? this.filename : this.filename + " " + this.getJsonKeys();
			} else {
				return this.jsonKeys.isEmpty() ? "(Unknown file)" : "(Unknown file) " + this.getJsonKeys();
			}
		}
	}
}
