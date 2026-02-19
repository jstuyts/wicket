package org.apache.wicket.core.request.handler;

import org.apache.wicket.markup.MarkupType;
import org.apache.wicket.markup.html.panel.Panel;

public class MathmlSubexpressionPanel extends Panel
{
    public MathmlSubexpressionPanel(String id)
    {
        super(id);
    }

    @Override
    public MarkupType getMarkupType()
    {
        return new MarkupType("xml", "application/mathml+xml");
    }
}
