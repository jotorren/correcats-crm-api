package cat.corredors.backoffice.users.crosscutting;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter 
@Setter
public class ErrorBean implements Serializable {
	private static final long serialVersionUID = -4046876242645505718L;

	private int code;
	private String message;
}
