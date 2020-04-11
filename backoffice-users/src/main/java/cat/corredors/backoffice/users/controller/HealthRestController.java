package cat.corredors.backoffice.users.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import cat.corredors.backoffice.users.crosscutting.BackOfficeUsersConstants;

@RestController
public class HealthRestController implements HealthApi {

	@Override
	public ResponseEntity<ResponseData<Boolean>> alive() {
		return ResponseEntity.ok(new ResponseData<Boolean>(BackOfficeUsersConstants.REST.InfoCodes.INF_001, true));
	}

	
}
