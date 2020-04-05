package cat.corredors.backoffice.users.controller;

import cat.corredors.backoffice.users.domain.SearchOperation;

public class StringToSearchOperationEnumDeserializer extends StringToEnumDeserializer<SearchOperation> {

	public StringToSearchOperationEnumDeserializer() {
		super(SearchOperation.class);
	}
}
