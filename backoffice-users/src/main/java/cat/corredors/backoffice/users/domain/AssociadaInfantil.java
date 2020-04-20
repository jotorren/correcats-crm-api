package cat.corredors.backoffice.users.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Entity
@Table(name = "BO_ASSOCIAT_INFANTIL")
@Data
public class AssociadaInfantil {
	
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

	@Column(name = "NICK", nullable = false, unique = true)
	private String nick;

	@Column(name = "RESPONSABLE", nullable = false)
	private String responsable;	
	
	@Column(name = "EMAIL", nullable = true)
	private String email;

	@Column(name = "DNI", nullable = true)
	private String dni;

	@Column(name = "DATA_NAIXEMENT", nullable = true)
	@Temporal(TemporalType.DATE)
//	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
	private Date dataNaixement;
		
	@Column(name = "DATA_ALTA", nullable = true)
	@Temporal(TemporalType.DATE)
//	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
	private Date dataAlta;
	
	@Column(name = "DATA_BAIXA", nullable = true)
	@Temporal(TemporalType.DATE)
//	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
	private Date dataBaixa;
	
	@Column(name = "OBSERVACIONS", nullable = true)
	private String observacions;
}
