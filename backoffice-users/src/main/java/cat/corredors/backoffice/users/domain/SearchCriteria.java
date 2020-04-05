package cat.corredors.backoffice.users.domain;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SearchCriteria {

	@NotNull
	private String key;
	@NotNull
	private SearchOperation operation;
	@NotNull
	private Object value;
}
