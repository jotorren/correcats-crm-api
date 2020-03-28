package cat.corredors.backoffice.users.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "BO_MUNICIPIS")
@Data
public class Municipi {

	@Id
	@Column(name = "CODI", nullable = false)
	private String codi;
	
	@Column(name = "NOM", nullable = false)
	private String nom;	
}
