package cat.corredors.backoffice.users.crosscutting;

import lombok.Data;

@Data
public class PageBean<T> {

	private long total;
	private int offset;
	private int limit;
	private int numberOfElements;
	private T included[];
}
