/*
    Copyright 2011, Strategic Gains, Inc.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package org.restexpress.common.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author toddf
 * @since Oct 7, 2011
 */
public abstract class StringUtils
{
	public static final String EMPTY_STRING = "";

	public static String join(String delimiter, Collection<? extends Object> objects)
	{
		if (objects == null || objects.isEmpty())
		{
			return EMPTY_STRING;
		}
		
		Iterator<? extends Object> iterator = objects.iterator();
		StringBuilder builder = new StringBuilder();
		builder.append(iterator.next());

		while(iterator.hasNext())
		{
			builder.append(delimiter)
				.append(iterator.next());
		}
		
		return builder.toString();
	}
	
	public static String join(String delimiter, Object... objects)
	{
		return join(delimiter, Arrays.asList(objects));
	}

	private StringUtils()
	{
		// prevents instantiation.
	}
}

