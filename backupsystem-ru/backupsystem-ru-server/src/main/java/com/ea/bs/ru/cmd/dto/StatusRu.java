package com.ea.bs.ru.cmd.dto;

public enum StatusRu {
	NOT_DEFINED(0), AVAILABLE(1), BUSY(2), OUT_OF_ORDER(3);

	private int id;

	private StatusRu(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public static StatusRu getStateRu(int id) {
		StatusRu[] states = StatusRu.values();
		return states[id];
	}
}
