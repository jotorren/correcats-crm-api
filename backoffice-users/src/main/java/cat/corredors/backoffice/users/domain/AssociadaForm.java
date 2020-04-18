package cat.corredors.backoffice.users.domain;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class AssociadaForm {

	private Boolean activat;
	
	@NotBlank
	private String nom;
	
	@NotBlank
	private String cognoms;
	
	@NotBlank
	private String sexe;

	private Boolean infantil;
	
	private Date dataNaixement;
	
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
//	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private Date dataAlta;
	private Date dataBaixa;
	
	private String observacions;
}
