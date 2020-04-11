package cat.corredors.backoffice.users.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/health")
public interface HealthApi {

	@GetMapping(
			value = "/alive",
			produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<ResponseData<Boolean>> alive(); 
}
