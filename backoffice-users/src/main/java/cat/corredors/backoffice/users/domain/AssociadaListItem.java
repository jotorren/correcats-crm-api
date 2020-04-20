package cat.corredors.backoffice.users.domain;

import java.util.Date;

import lombok.Data;

@Data
public class AssociadaListItem {
	
	private String id;
	private String nom;
	private String cognoms;
	private String nick;
	private String responsable;
//	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
	private Date dataNaixement;
}
