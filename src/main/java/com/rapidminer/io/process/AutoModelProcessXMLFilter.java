/**
 * Copyright (C) 2001-2018 by RapidMiner and the contributors
 * 
 * Complete list of developers available at our web site:
 * 
 * http://rapidminer.com
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
*/
package com.rapidminer.io.process;

import org.w3c.dom.Element;

import com.rapidminer.operator.ExecutionUnit;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.UserData;


/**
 * {@link ProcessXMLFilter} to handle AutoModel generated operators end exported processes in UsageStat.
 *
 * @author Peter Toth
 * @since 8.1
 */
public class AutoModelProcessXMLFilter implements ProcessXMLFilter {

	/**
	 * AutoModelState is a special {@link UserData} to indicate that an operator is generated by the AutoModel wizard.
	 * The UserData is used to differentiate the generated operators from the user selected ones in UsageStat.
	 * It has 2 states, the GENERATED is used immediately after operator generation in the wizard to tell which
	 * operators are run by the wizard itself. The EXPORTED state is attached to operators which are exported as part of
	 * a process from the AutoModel wizard.
	 */
	public enum AutoModelState implements UserData<Object> {

		GENERATED, EXPORTED;

		@Override
		public UserData<Object> copyUserData(Object newParent) {
			return this;
		}

	}

	private static final String KEY_AUTOMODEL = "automodel";

	private static final String XML_ATTRIBUTE_AUTOMODEL = "automodel";

	@Override
	public void operatorExported(final Operator op, final Element opElement) {
		AutoModelState autoModelState = getAutoModelState(op);
		if (autoModelState != null) {
			opElement.setAttribute(XML_ATTRIBUTE_AUTOMODEL, autoModelState.name());
		}
	}

	@Override
	public void executionUnitExported(final ExecutionUnit process, final Element element) {}

	@Override
	public void operatorImported(final Operator op, final Element opElement) {
		String attributeValue = opElement.getAttribute(XML_ATTRIBUTE_AUTOMODEL);
		if (attributeValue != null) {
			try {
				AutoModelState autoModelState = AutoModelState.valueOf(attributeValue);
				op.setUserData(KEY_AUTOMODEL, autoModelState);
			} catch (IllegalArgumentException e) {}
		}
	}

	@Override
	public void executionUnitImported(final ExecutionUnit process, final Element element) {}

	/**
	 * Returns an AutoModelState if an operator is generated by or exported from the AutoModel wizard or null if it
	 * isn't.
	 *
	 * @param operator
	 * @return
	 */
	public static AutoModelState getAutoModelState(Operator operator) {
		return (AutoModelState) operator.getUserData(KEY_AUTOMODEL);
	}

	/**
	 * Marks an operator as generated by or exported from the AutoModel wizard. Doesn't do anything if the
	 * autoModelState is null.
	 *
	 * @param operator
	 * @param autoModelState
	 */
	public static void setAutoModelState(Operator operator, AutoModelState autoModelState) {
		if (autoModelState != null) {
			operator.setUserData(KEY_AUTOMODEL, autoModelState);
		}
	}

}
