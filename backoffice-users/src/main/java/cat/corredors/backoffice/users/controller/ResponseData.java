package cat.corredors.backoffice.users.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter 
@Setter
@JsonInclude(Include.NON_NULL)
public class ResponseData<T> {

	private int code;
	private T result;

	@JsonIgnore
	private Object[] messageParams;
	private String message;
	
	public ResponseData(int code, T result) {
		this.code = code;
		this.result = result;
	}
}
