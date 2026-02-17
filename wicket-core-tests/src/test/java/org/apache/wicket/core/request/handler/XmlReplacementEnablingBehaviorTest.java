package org.apache.wicket.core.request.handler;

import static org.apache.wicket.markup.parser.XmlTag.TagType.CLOSE;
import static org.apache.wicket.markup.parser.XmlTag.TagType.OPEN;
import static org.apache.wicket.markup.parser.XmlTag.TagType.OPEN_CLOSE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class XmlReplacementEnablingBehaviorTest extends WicketTestCase
{
    private static final String SOME_ELEMENT = "someelement";
    private static final String SOME_ID = "some-id";
    private static final String SOME_NAMESPACE_URI = "some-namespace-uri";

    @Test
    void addsJavaScriptsNeededForXmlReplacement()
    {
        tester.startPage(XmlReplacementEnablingBehaviorTest.TestPage.class);

        tester.assertContains("<script type=\"text/javascript\" src=\"\\.\\./resource/org\\.apache\\.wicket\\.ajax\\.XmlReplacementMethodResourceReference/res/js/xml-replacement-method\\.js\"></script>");
    }

    @Test
    void doesNotAcceptNullNamespaceUri()
    {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new XmlReplacementEnablingBehavior(null));
    }

    @Test
    void doesNotAllowBindingToMultipleComponents()
    {
        var behavior = new XmlReplacementEnablingBehavior(SOME_NAMESPACE_URI);
        behavior.bind(new WebMarkupContainer(SOME_ID));

        Assertions.assertThrows(IllegalStateException.class, () -> behavior.bind(new WebMarkupContainer(SOME_ID)));
    }
    
    @Test
    void setsNamespaceUriOnComponentOpenTag()
    {
        var behavior = new XmlReplacementEnablingBehavior("http://example.com/the-namespace-uri");
        var tag = new ComponentTag(SOME_ELEMENT, OPEN);

        behavior.onComponentTag(new WebMarkupContainer(SOME_ID), tag);

        assertEquals("http://example.com/the-namespace-uri", tag.getAttribute("xmlns"));
    }
    
    @Test
    void setsNamespaceUriOnComponentOpenCloseTag()
    {
        var behavior = new XmlReplacementEnablingBehavior("http://example.com/the-namespace-uri");
        var tag = new ComponentTag(SOME_ELEMENT, OPEN_CLOSE);

        behavior.onComponentTag(new WebMarkupContainer(SOME_ID), tag);

        assertEquals("http://example.com/the-namespace-uri", tag.getAttribute("xmlns"));
    }
    
    @Test
    void doesNotSetNamespaceUriOnComponentCloseTag()
    {
        var behavior = new XmlReplacementEnablingBehavior("http://example.com/the-namespace-uri");
        var tag = new ComponentTag(SOME_ELEMENT, CLOSE);

        behavior.onComponentTag(new WebMarkupContainer(SOME_ID), tag);

        assertNull(tag.getAttribute("xmlns"));
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
            add(new WebComponent("component").add(new XmlReplacementEnablingBehavior(SOME_NAMESPACE_URI)));
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
