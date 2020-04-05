package cat.corredors.backoffice.users.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import cat.corredors.backoffice.users.domain.Associada;
import cat.corredors.backoffice.users.domain.SearchOperation;

public final class AssociadaSpecificationsBuilder {

	private final List<SpecSearchCriteria> params;

	public AssociadaSpecificationsBuilder() {
		params = new ArrayList<>();
	}

	// API

	public final AssociadaSpecificationsBuilder with(final String key, final String operation, final Object value,
			final String prefix, final String suffix) {
		return with(null, key, operation, value, prefix, suffix);
	}

	public final AssociadaSpecificationsBuilder with(final String orPredicate, final String key, final String operation,
			final Object value, final String prefix, final String suffix) {
		SearchOperation op = SearchOperation.getSimpleOperation(operation.charAt(0));
		if (op != null) {
			if (op == SearchOperation.EQ) { // the operation may be complex operation
				final boolean startWithAsterisk = prefix != null && prefix.contains(SearchOperation.ZERO_OR_MORE_REGEX);
				final boolean endWithAsterisk = suffix != null && suffix.contains(SearchOperation.ZERO_OR_MORE_REGEX);

				if (startWithAsterisk && endWithAsterisk) {
					op = SearchOperation.CONTAINS;
				} else if (startWithAsterisk) {
					op = SearchOperation.ENDS_WITH;
				} else if (endWithAsterisk) {
					op = SearchOperation.STARTS_WITH;
				}
			}
			params.add(new SpecSearchCriteria(orPredicate, key, op, value));
		}
		return this;
	}

	public Specification<Associada> build() {
		if (params.size() == 0)
			return null;

		Specification<Associada> result = new AssociadaSpecification(params.get(0));

		for (int i = 1; i < params.size(); i++) {
			result = params.get(i).isOrPredicate()
					? Specification.where(result).or(new AssociadaSpecification(params.get(i)))
					: Specification.where(result).and(new AssociadaSpecification(params.get(i)));
		}

		return result;
	}

	public final AssociadaSpecificationsBuilder with(AssociadaSpecification spec) {
		params.add(spec.getCriteria());
		return this;
	}

	public final AssociadaSpecificationsBuilder with(SpecSearchCriteria criteria) {
		params.add(criteria);
		return this;
	}
}
