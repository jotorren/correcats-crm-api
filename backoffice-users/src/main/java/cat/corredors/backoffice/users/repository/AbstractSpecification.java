package cat.corredors.backoffice.users.repository;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AbstractSpecification<T> implements Specification<T> {
	private static final long serialVersionUID = -6389606675797724643L;

	private final SpecSearchCriteria criteria;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Predicate toPredicate(final Root<T> root, final CriteriaQuery<?> query, final CriteriaBuilder builder) {
		switch (criteria.getOperation()) {
		case EQ:
			if (null == criteria.getValue()) {
				return builder.isNull(root.get(criteria.getKey()));
			} else {
				return builder.equal(root.get(criteria.getKey()), criteria.getValue());
			}
		case IN:
			In<Object> in = builder.in(root.get(criteria.getKey()));
			List<?> values = (List<?>)criteria.getValue();
			for (Object value: values) {
				in.value(value);
			}
			return in;
		case NOT:
			return builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
		case GT:
			return builder.greaterThan(root.get(criteria.getKey()), (Comparable)criteria.getValue());
		case LT:
			return builder.lessThan(root.get(criteria.getKey()), (Comparable)criteria.getValue());
		case GTE:
			return builder.greaterThanOrEqualTo(root.get(criteria.getKey()), (Comparable)criteria.getValue());
		case LTE:
			return builder.lessThanOrEqualTo(root.get(criteria.getKey()), (Comparable)criteria.getValue());
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
