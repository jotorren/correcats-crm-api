package cat.corredors.backoffice.users.domain;

import static cat.corredors.backoffice.users.crosscutting.BackOfficeUsersConstants.DATE_FORMAT;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class AssociadaListItem {
	
	private String id;
	private String nom;
	private String cognoms;
	private String nick;
	private String email;
	private Boolean activat;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
	private Date dataAlta;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
	private Date dataBaixa;
	private Boolean infantil;
	private String observacions;
}
