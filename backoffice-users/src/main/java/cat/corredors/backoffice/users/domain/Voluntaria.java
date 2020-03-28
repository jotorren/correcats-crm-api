package cat.corredors.backoffice.users.domain;

import lombok.Data;

@Data
public class Voluntaria {

	private String nom;
	private String cognoms;
	
	private String nick;

	private String email;
	
	private Boolean associada;
}
