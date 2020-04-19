package cat.corredors.backoffice.users.domain;

import static cat.corredors.backoffice.users.crosscutting.BackOfficeUsersConstants.DATE_FORMAT;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Entity
@Table(name = "BO_ASSOCIAT")
@Data
public class Associada {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_SOCI", nullable = false)
	private String id;
	
	@Column(name = "ACTIVAT", nullable = false)
	private Boolean activat;
	
	@Column(name = "NOM", nullable = false)
	private String nom;
	
	@Column(name = "COGNOMS", nullable = false)
	private String cognoms;

	@Column(name = "SEXE", nullable = false)
	private String sexe;

	@Column(name = "INFANTIL", nullable = false)
	private Boolean infantil;
	
	@Column(name = "DATA_NAIXEMENT", nullable = true)
	@Temporal(TemporalType.DATE)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
	private Date dataNaixement;
	
	@Column(name = "NICK", nullable = false, unique = true)
	private String nick;
	
	@Column(name = "EMAIL", nullable = false, unique = true)
	private String email;

	@Column(name = "NIF", nullable = true)
	private String nif;
	
	@Column(name = "IBAN", nullable = true)
	private String iban;
	
	@Column(name = "TELEFON", nullable = true)
	private String telefon;
	
	@Column(name = "ADRECA", nullable = true)
	private String adreca;
	
	@Column(name = "CODI_POSTAL", nullable = true)
	private String codiPostal;
	
	@Column(name = "POBLACIO", nullable = true)
	private String poblacio;
	
	@Column(name = "QUOTA_ALTA", nullable = true)	
	private Float quotaAlta;
	
	@Column(name = "DATA_ALTA", nullable = true)
	@Temporal(TemporalType.DATE)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
	private Date dataAlta;
	
	@Column(name = "DATA_BAIXA", nullable = true)
	@Temporal(TemporalType.DATE)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
	private Date dataBaixa;
	
	@Column(name = "OBSERVACIONS", nullable = true)
	private String observacions;
}
