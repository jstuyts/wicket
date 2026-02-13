package org.apache.wicket.core.request.handler;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.jupiter.api.Test;

class PreactReplacementEnablingBehaviorTest extends WicketTestCase
{
    @Test
    void addsJavaScriptsNeedeForPreactReplacement()
    {
        tester.startPage(TestPage.class);

        tester.assertContains("<script type=\"text/javascript\" src=\"\\.\\./resource/org\\.apache\\.wicket\\.resource\\.PreactResourceReference/preact/preact-[0-9]+\\.[0-9]+\\.[0-9]+\\.umd\\.js\"></script>");
        tester.assertContains("<script type=\"text/javascript\" src=\"\\.\\./resource/org\\.apache\\.wicket\\.ajax\\.PreactReplacementMethodResourceReference/res/js/preact-replacement-method\\.js\"></script>");
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
            add(new WebComponent("component").add(PreactReplacementEnablingBehavior.INSTANCE));
        }

        @Override
        public IResourceStream getMarkupResourceStream(MarkupContainer container,
                                                       Class<?> containerClass)
        {
            return new StringResourceStream(
                    "<html><head></head><body><span wicket:id=\"component\"></span></body></html>");
        }
    }
}
