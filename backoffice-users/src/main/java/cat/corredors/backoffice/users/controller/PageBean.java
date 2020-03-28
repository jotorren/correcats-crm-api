package cat.corredors.backoffice.users.controller;

import lombok.Data;

@Data
public class PageBean<T> {

	private long total;
	private int offset;
	private int limit;
	private int numberOfElements;
	private T included[];
}
