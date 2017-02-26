package com.ea.bs.api.web.ru;

public enum StatusRu {
	NON_DEFINITO(0), AVAILABLE(1), STARTING(2), BUSY(3), UNREACHABLE(4), OUT_OF_ORDER(5);

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
