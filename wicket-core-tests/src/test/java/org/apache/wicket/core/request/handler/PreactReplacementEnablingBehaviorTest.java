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

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.SimplePanel;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PreactReplacementEnablingBehaviorTest extends WicketTestCase
{
    private static final String SOME_ID = "some-id";

    @Test
    void addsJavaScriptsNeededForPreactReplacement()
    {
        tester.startPage(TestPage.class);

        tester.assertContains("<script type=\"text/javascript\" src=\"\\.\\./resource/org\\.apache\\.wicket\\.resource\\.PreactResourceReference/preact/preact-[0-9]+\\.[0-9]+\\.[0-9]+\\.umd\\.js\"></script>");
        tester.assertContains("<script type=\"text/javascript\" src=\"\\.\\./resource/org\\.apache\\.wicket\\.ajax\\.PreactReplacementMethodResourceReference/res/js/preact-replacement-method\\.js\"></script>");
    }

    @Test
    void doesNotAllowBindingToMultipleComponents()
    {
        var behavior = new PreactReplacementEnablingBehavior();
        behavior.bind(new WebMarkupContainer(SOME_ID));

        Assertions.assertThrows(IllegalStateException.class, () -> behavior.bind(new WebMarkupContainer(SOME_ID)));
    }

    @Test
    void enablesOutputOfMarkupIdOnBindingToComponent()
    {
        var behavior = new PreactReplacementEnablingBehavior();
        var component = new WebMarkupContainer(SOME_ID);
        behavior.bind(component);

        Assertions.assertTrue(component.getOutputMarkupId());
    }

    @Test
    void disablesRenderingOfWicketTags()
    {
        tester.startPage(TestPage.class);

        tester.assertContainsNot("<wicket:panel>");
    }

    public static class TestPage extends WebPage implements IMarkupResourceStreamProvider
    {
        /** */
        private static final long serialVersionUID = 1L;

        /**
         * Construct.
         */
        public TestPage()
        {
            var component = new WebMarkupContainer("component");
            component.add(new PreactReplacementEnablingBehavior());
            add(component);

            var componentChild = new SimplePanel("component-child");
            component.add(componentChild);
        }

        @Override
        public IResourceStream getMarkupResourceStream(MarkupContainer container,
                                                       Class<?> containerClass)
        {
            return new StringResourceStream(
                    "<html><head></head><body><span wicket:id=\"component\"><span wicket:id=\"component-child\"></span></span></body></html>");
        }
    }
}
