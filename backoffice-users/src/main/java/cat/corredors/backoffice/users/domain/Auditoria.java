package cat.corredors.backoffice.users.domain;

import java.util.Date;

import lombok.Data;

@Data
public class Auditoria {

	private String user;
	private Date timestamp;
	private AuditoriaOp operation;
	private Object data;
}
