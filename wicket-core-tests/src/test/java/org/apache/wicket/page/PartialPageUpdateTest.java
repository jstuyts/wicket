package org.apache.wicket.page;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.wicket.request.Response;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.jupiter.api.Test;

class PartialPageUpdateTest extends WicketTestCase
{
    @Test
    void returnsNoReplacementMethodIfNotSpecified()
    {
        var page = new PageForPartialUpdate();

        tester.startPage(page);

        var update = new MockPartialPageUpdate(page);

        update.add(page.alternativeReplacement, "theMarkupId");

        assertNull(update.getReplacementMethod("theMarkupId"));
    }

    @Test
    void returnsNoReplacementMethodIfNull()
    {
        var page = new PageForPartialUpdate();

        tester.startPage(page);

        var update = new MockPartialPageUpdate(page);

        update.add(null, page.alternativeReplacement, "theMarkupId");

        assertNull(update.getReplacementMethod("theMarkupId"));
    }

    @Test
    void returnsReplacementMethodIfSpecified()
    {
        var page = new PageForPartialUpdate();

        tester.startPage(page);

        var update = new MockPartialPageUpdate(page);

        update.add("replacementMethod", page.alternativeReplacement, "theMarkupId");

        assertEquals("replacementMethod", update.getReplacementMethod("theMarkupId"));
    }

    private static class MockPartialPageUpdate extends PartialPageUpdate
    {

        public MockPartialPageUpdate(PageForPartialUpdate page)
        {
            super(page);
        }

        @Override
        protected void writeFooter(Response response, String encoding)
        {
        }

        @Override
        protected void writeHeader(Response response, String encoding)
        {
        }

        @Override
        protected void writeComponent(Response response, String markupId, CharSequence contents)
        {
        }

        @Override
        protected void writePriorityEvaluation(Response response, CharSequence contents)
        {
        }

        @Override
        protected void writeHeaderContribution(Response response, CharSequence contents)
        {
        }

        @Override
        protected void writeEvaluation(Response response, CharSequence contents)
        {
        }

        @Override
        public void setContentType(WebResponse response, String encoding)
        {
        }
    }
}
