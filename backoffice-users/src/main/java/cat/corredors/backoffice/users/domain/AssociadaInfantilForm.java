package cat.corredors.backoffice.users.domain;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class AssociadaInfantilForm {

	private Boolean activat;
	
	@NotBlank
	private String nom;
	
	@NotBlank
	private String cognoms;
	
	@NotBlank
	private String sexe;

	@NotBlank
	private String nick;
	
	@NotBlank
	private String responsable;	
	
	private String email;
	private String dni;
	private Date dataNaixement;
	private Date dataAlta;
	private Date dataBaixa;
	private String observacions;
}
