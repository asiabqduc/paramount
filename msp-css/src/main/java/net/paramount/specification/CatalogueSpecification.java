/**
 * 
 */
package net.paramount.specification;

import org.springframework.data.jpa.domain.Specification;

import lombok.Builder;
import net.paramount.css.entity.general.Catalogue;
import net.paramount.framework.model.SearchParameter;
import net.paramount.framework.model.SearchRequest;
import net.paramount.framework.specification.CoreSpecifications;

/**
 * @author bqduc
 *
 */
@Builder
public class CatalogueSpecification extends CoreSpecifications<Catalogue, SearchRequest>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2021774340255996625L;

	public static Specification<Catalogue> buildSpecification(final SearchParameter searchParameter) {
		return CatalogueSpecification
				.builder()
				.build()
				.buildSpecifications(searchParameter);
	}
}
