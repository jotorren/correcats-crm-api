package cat.corredors.backoffice.users.domain;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class AssociadaForm {
	
	@NotBlank
	private String nom;
	
	@NotBlank
	private String cognoms;
	
	@NotBlank
	private String nick;
	
	@NotBlank
	private String email;

	private String nif;
	private String iban;
	
	private String telefon;
	private String adreca;
	private String codiPostal;
	private String poblacio;
	
	private Float quotaAlta;
	private String dataAlta;
	
	private String observacions;
}
