package cat.corredors.backoffice.users.controller;

import cat.corredors.backoffice.users.domain.SearchOperation;

public class StringToSearchOperationEnumConverter extends StringToEnumConverter<SearchOperation> {

	public StringToSearchOperationEnumConverter() {
		super(SearchOperation.class);
	}

}
