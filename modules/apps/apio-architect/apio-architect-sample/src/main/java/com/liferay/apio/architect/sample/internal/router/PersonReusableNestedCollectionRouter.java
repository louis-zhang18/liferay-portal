/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.apio.architect.sample.internal.router;

import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.apio.architect.router.ReusableNestedCollectionRouter;
import com.liferay.apio.architect.routes.NestedCollectionRoutes;
import com.liferay.apio.architect.sample.internal.converter.PersonConverter;
import com.liferay.apio.architect.sample.internal.dao.BlogPostingCommentModelService;
import com.liferay.apio.architect.sample.internal.dao.BlogPostingModelService;
import com.liferay.apio.architect.sample.internal.dao.PersonModelService;
import com.liferay.apio.architect.sample.internal.dto.BlogPostingCommentModel;
import com.liferay.apio.architect.sample.internal.dto.BlogPostingModel;
import com.liferay.apio.architect.sample.internal.dto.PersonModel;
import com.liferay.apio.architect.sample.internal.identifier.ModelNameModelIdIdentifier;
import com.liferay.apio.architect.sample.internal.resource.PersonCollectionResource.PersonIdentifier;
import com.liferay.apio.architect.sample.internal.type.Person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Javier Gamarra
 */
@Component(service = ReusableNestedCollectionRouter.class)
public class PersonReusableNestedCollectionRouter
	implements ReusableNestedCollectionRouter
		<Person, Long, PersonIdentifier, ModelNameModelIdIdentifier> {

	@Override
	public NestedCollectionRoutes
		<Person, Long, ModelNameModelIdIdentifier> collectionRoutes(
			NestedCollectionRoutes.Builder
				<Person, Long, ModelNameModelIdIdentifier> builder) {

		return builder.addGetter(
			(pagination, id) -> {
				Long creatorId = _getCreatorId(
					id.getModelName(), id.getModelId());

				Optional<PersonModel> personModelOptional =
					_personModelService.get(creatorId);

				List<Person> persons = personModelOptional.map(
					PersonConverter::toPerson
				).map(
					Collections::singletonList
				).orElse(
					new ArrayList<>()
				);

				return new PageItems<>(persons, persons.size());
			}).build();
	}

	private Long _getCreatorId(String modelName, long modelId) {
		if (modelName.equals("blogPosting")) {
			Optional<BlogPostingModel> blogPostingModelOptional =
				_blogPostingModelService.get(modelId);

			return blogPostingModelOptional.map(
				BlogPostingModel::getCreatorId
			).orElse(
				null
			);
		}

		Optional<BlogPostingCommentModel> blogPostingCommentModelOptional =
			_blogPostingCommentModelService.get(modelId);

		return blogPostingCommentModelOptional.map(
			BlogPostingCommentModel::getCreatorId
		).orElse(
			null
		);
	}

	@Reference
	private BlogPostingCommentModelService _blogPostingCommentModelService;

	@Reference
	private BlogPostingModelService _blogPostingModelService;

	@Reference
	private PersonModelService _personModelService;

}