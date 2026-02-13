/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.core.request.handler;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.PreactReplacementMethodResourceReference;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;

/**
 * Add this behavior to components of which the markup must be replaced with <a href="https://preactjs.com/">Preact</a>
 * during the handling of an Ajax response. The Preact replacement method is useful in the following cases:
 * <ul>
 *     <li>Components with lots of markup, of which only little changes during Ajax requests.</li>
 *     <li>SVG markup, as jQuery does not properly replace SVG elements.</li>
 * </ul>
 * Requirements:
 * <ul>
 *     <li>
 *         The element of the component must be the only element of its parent. Other node types in the parent are fine.
 *         If you need to add an element for this requirement and it messes with your layout, you can add
 *         <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/Reference/Properties/display#contents"><code>display: contents</code></a>
 *         to its style.
 *     </li>
 * </ul>
 * Make sure you test that markup changes are properly applied for your situation. There is not a comprehensive set of
 * tests to check if this replacement method works the same as the standard method using jQuery.
 */
public class PreactReplacementEnablingBehavior extends Behavior
{
    /**
     * The code to pass to {@link IPartialPageRequestHandler#add(String, Component, String)} or
     * {@link IPartialPageRequestHandler#add(String, Component...)} to have the markup of the component replaced using
     * Preact.
     */
    public static final String PREACT = "preact";

    /** Singleton instance. */
    public static final PreactReplacementEnablingBehavior INSTANCE = new PreactReplacementEnablingBehavior();

    private static final HeaderItem PREACT_REPLACEMENT_METHOD_HEADER_ITEM =
            JavaScriptHeaderItem.forReference(PreactReplacementMethodResourceReference.get());

    private PreactReplacementEnablingBehavior()
    {}

    @Override
    public void renderHead(Component component, IHeaderResponse response)
    {
        response.render(PREACT_REPLACEMENT_METHOD_HEADER_ITEM);
    }
}
