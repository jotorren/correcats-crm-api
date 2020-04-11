package cat.corredors.backoffice.users.domain;

import java.util.Date;

import lombok.Data;

@Data
public class AssociadaListItem {
	
	private String id;
	private String nom;
	private String cognoms;
	private String nick;
	private String email;
	private Boolean activat;
	private Date dataAlta;
	private Date dataBaixa;
	private String observacions;
}
