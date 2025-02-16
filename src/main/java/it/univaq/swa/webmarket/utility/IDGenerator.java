package it.univaq.swa.webmarket.utility;

import java.util.Objects;

public class IDGenerator {

	private static IDGenerator generator = null;

	private static Long id = 0L;

	public static IDGenerator getGenerator() {
		if (Objects.isNull(generator)) {
			generator = new IDGenerator();
		}
		return generator;
	}

	public Long generateID() {
		id++;
		return id;
	}
}
