package cat.corredors.backoffice.users.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "BO_CODIS_POSTALS")
@Data
public class CodiPostal {

	@Id
	@Column(name = "ID", nullable = false)
	private String internalId;
	
	@Column(name = "CODI_POSTAL", nullable = false)
	private String valor;	

	@Column(name = "MUNICIPI", nullable = false)
	private String municipi;	
}
