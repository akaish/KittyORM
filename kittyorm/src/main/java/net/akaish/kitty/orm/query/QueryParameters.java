
/*
 * ---
 *
 *  Copyright (c) 2018 Denis Bogomolov (akaish)
 *
 * This work is licensed under a
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://creativecommons.org/licenses/by-nc-nd/4.0/
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

package net.akaish.kitty.orm.query;

import net.akaish.kitty.orm.enums.AscDesc;

/**
 * Simple class for setting and storing query parameters such as LIMIT, OFFSET, ORDER_BY and GROUP_BY.
 * Also, don't forget that using LIMIT keyword (and setLimit()) without GROUP_BY would cause limiting not by
 * id\rowid but by, probably order how rows are physically stored in file. SQLite, I love you.
 * Created by akaish on 07.04.18.
 * @author akaish (Denis Bogomolov)
 */
public class QueryParameters {
	private Long limit = null;
	private Long offset = null;
	private String[] groupByColumns = null;
	private String[] orderByColumns = null;
	private AscDesc orderAscDesc = AscDesc.ASCENDING;

	public Long getLimit() {
		return limit;
	}

	/**
	 * Sets LIMIT keyword for query. Don't forget that using LIMIT keyword (and setLimit()) without
	 * GROUP_BY would cause limiting not by id\rowid but by, probably order how rows are physically stored in file.
	 * <br> Also used PostgreSQL styled syntax, like LIMIT 10, OFFSET 20, liming on syntax style LIMIT 20, 10
	 * not supported by this library.
	 * @param limit limit of rows for query to process
	 * @return this
	 */
	public QueryParameters setLimit(Long limit) {
		this.limit = limit;
		return this;
	}

	public Long getOffset() {
		return offset;
	}

	public QueryParameters setOffset(Long offset) {
		this.offset = offset;
		return this;
	}

	public String[] getGroupByColumns() {
		return groupByColumns;
	}

	public QueryParameters setGroupByColumns(String... groupByColumns) {
		this.groupByColumns = groupByColumns;
		return this;
	}

	public String[] getOrderByColumns() {
		return orderByColumns;
	}

	public QueryParameters setOrderByColumns(String... orderByColumns) {
		this.orderByColumns = orderByColumns;
		return this;
	}

	/**
	 * Returns order ASCENDING or DESCENDING, see ({@link AscDesc#ASCENDING}).
	 * By default order is ASCENDING.
	 * @return
	 */
	public AscDesc getOrderAscDesc() {
		return orderAscDesc;
	}

	public QueryParameters setOrderAscDesc(AscDesc orderAscDesc) {
		this.orderAscDesc = orderAscDesc;
		return this;
	}
}
