
/*
 * ---
 *
 *  Copyright (c) 2018 Denis Bogomolov (akaish)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file is a part of KittyORM project (KittyORM library), more information at
 * https://akaish.github.io/KittyORMPages/license/
 *
 * ---
 */

package net.akaish.kitty.orm;

/**
 * Created by akaish on 14.11.17.
 * @author akaish (Denis Bogomolov)
 */

public class KittyMMEntry<M extends KittyModel, D extends KittyMapper<M>> {
	private final M model;
	private final D mapper;

	KittyMMEntry(M model, D mapper) {
		super();
		this.model = model;
		this.mapper = mapper;
	}

	public M getModelClass() {
		return model;
	}

	public D getMapper() { return mapper; }
}