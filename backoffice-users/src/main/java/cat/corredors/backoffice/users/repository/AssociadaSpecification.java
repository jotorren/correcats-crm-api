package cat.corredors.backoffice.users.repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import cat.corredors.backoffice.users.domain.Associada;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AssociadaSpecification implements Specification<Associada> {
	private static final long serialVersionUID = -6389606675797724643L;

	private final SpecSearchCriteria criteria;

	@Override
	public Predicate toPredicate(final Root<Associada> root, final CriteriaQuery<?> query, final CriteriaBuilder builder) {
		switch (criteria.getOperation()) {
		case EQUALITY:
			return builder.equal(root.get(criteria.getKey()), criteria.getValue());
		case NEGATION:
			return builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
		case GREATER_THAN:
			return builder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
		case LESS_THAN:
			return builder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
		case LIKE:
			return builder.like(root.get(criteria.getKey()), criteria.getValue().toString());
		case STARTS_WITH:
			//return builder.like(root.get(criteria.getKey()), criteria.getValue() + "%");
			return builder.like(builder.lower(root.get(criteria.getKey())), ((String)criteria.getValue()).toLowerCase() + "%");
		case ENDS_WITH:
			//return builder.like(root.get(criteria.getKey()), "%" + criteria.getValue());
			return builder.like(builder.lower(root.get(criteria.getKey())), "%" + ((String)criteria.getValue()).toLowerCase());
		case CONTAINS:
			//return builder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
			return builder.like(builder.lower(root.get(criteria.getKey())), "%" + ((String)criteria.getValue()).toLowerCase() + "%");
		default:
			return null;
		}
	}
}
