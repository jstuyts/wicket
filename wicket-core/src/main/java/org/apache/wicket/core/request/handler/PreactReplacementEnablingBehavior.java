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
 *         to its style. If the element is not the only element of its parent, an error will be logged.
 *     </li>
 * </ul>
 * Limitations:
 * <ul>
 *     <li>
 *         If the markup IDs change on each Ajax request, using Preact won't help. In fact, it is probably slower than
 *         the standard replacement using jQuery where the browser updates the DOM. This can happen if, for example, you
 *         use {@link org.apache.wicket.markup.repeater.AbstractRepeater repeaters}. If you use Preact for markup
 *         replacement, use stable markup IDs if possible.
 *     </li>
 *     <li>
 *         Preact cannot insert Wicket tags into the DOM exactly the same as they are inserted when the browser
 *         initially loads the markup. To prevent rendering problems Wicket tags are always stripped for components to
 *         which this behavior is added.
 *     </li>
 *     <li>
 *         <strong>DO NOT USE</strong> Preact for replacing markup if the Ajax response adds event listeners to the
 *         elements being updated. As Preact tries to avoid replacing an element, the original listeners will still be
 *         present if replacement is not necessary. The event listeners in the Ajax response will be
 *         added to the still existing listeners, resulting in events being processed multiple times.
 *     </li>
 * </ul>
 * Make sure you test that markup changes are properly applied for your situation. There is not a comprehensive set of
 * tests to check if this replacement method works exactly the same as the standard method using jQuery.
 */
public class PreactReplacementEnablingBehavior extends Behavior
{
    /**
     * The identifier to pass to {@link IPartialPageRequestHandler#add(String, Component, String)} or
     * {@link IPartialPageRequestHandler#add(String, Component...)} to have the markup of the component replaced using
     * Preact.
     */
    public static final String PREACT = "preact";

    private static final HeaderItem PREACT_REPLACEMENT_METHOD_HEADER_ITEM =
            JavaScriptHeaderItem.forReference(PreactReplacementMethodResourceReference.get());

    private boolean hasBeenBound;

    private transient boolean previousStripWicketTags;

    /**
     * Create a new instance. Instances cannot be shared between components.
     */
    public PreactReplacementEnablingBehavior()
    {}

    @Override
    public void bind(Component component)
    {
        if (hasBeenBound) {
            throw new IllegalStateException("this kind of handler cannot be attached to multiple components");
        }
        hasBeenBound = true;
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response)
    {
        response.render(PREACT_REPLACEMENT_METHOD_HEADER_ITEM);
    }

    @Override
    public void beforeRender(Component component)
    {
        var markupSettings = component.getApplication().getMarkupSettings();
        previousStripWicketTags = markupSettings.getStripWicketTags();
        markupSettings.setStripWicketTags(true);
    }

    @Override
    public void afterRender(Component component)
    {
        component.getApplication().getMarkupSettings().setStripWicketTags(previousStripWicketTags);
    }
}
