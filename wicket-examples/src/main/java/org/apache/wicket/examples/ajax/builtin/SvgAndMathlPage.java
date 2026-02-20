package org.apache.wicket.examples.ajax.builtin;

import static org.apache.wicket.core.request.handler.XmlReplacementEnablingBehavior.MATHML_NAMESPACE_URI;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.core.request.handler.XmlReplacementEnablingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Demo page for XML, showing manipulation of SVG and MathML.
 */
public class SvgAndMathlPage extends BasePage
{
    private static final List<Integer> ONE_TO_NINE = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);

    private final IModel<Integer> firstNumberModel = Model.of(1);
    private final IModel<Operator> operatorModel = Model.of(Operator.ADD);
    private final IModel<Integer> secondNumberModel = Model.of(1);
    private final IModel<Integer> outcomeModel = new IModel<>()
    {
        @Override
        public Integer getObject()
        {
            return operatorModel.getObject().compute(
                    firstNumberModel.getObject(),
                    secondNumberModel.getObject());
        }

        @Override
        public void detach()
        {
            firstNumberModel.detach();
            operatorModel.detach();
            secondNumberModel.detach();
        }
    };

    @Override
    protected void onInitialize()
    {
        super.onInitialize();

        var firstNumberDropDown = new DropDownChoice<>("firstNumberDropDown", firstNumberModel, ONE_TO_NINE);
        var operatorDropDown = new DropDownChoice<>("operatorDropDown", operatorModel, OPERATORS);
        var secondNumberDropDown = new DropDownChoice<>("secondNumberDropDown", secondNumberModel, ONE_TO_NINE);

        var outcome = new Label("outcome", outcomeModel)
                .add(new XmlReplacementEnablingBehavior(MATHML_NAMESPACE_URI))
                .setOutputMarkupId(true);
        var firstNumber = new Label("firstNumber", firstNumberModel)
        {
            @Override
            protected void onInitialize()
            {
                super.onInitialize();
                
                add(new XmlReplacementEnablingBehavior(MATHML_NAMESPACE_URI));
                setOutputMarkupId(true);
                var linkThis = this;
                add(new AjaxEventBehavior("click")
                {
                    @Override
                    protected void onEvent(AjaxRequestTarget target)
                    {
                        var currentValue = firstNumberModel.getObject();
                        var newValue = currentValue == 9 ? 1 : currentValue + 1;
                        firstNumberModel.setObject(newValue);
                        target.add(XmlReplacementEnablingBehavior.XML, linkThis, outcome);
                        target.add(firstNumberDropDown);
                    }
                });
            }
        };
        var operator = new Label("operator", operatorModel)
        {
            @Override
            protected void onInitialize()
            {
                super.onInitialize();
                
                add(new XmlReplacementEnablingBehavior(MATHML_NAMESPACE_URI));
                setOutputMarkupId(true);
                var linkThis = this;
                add(new AjaxEventBehavior("click")
                {
                    @Override
                    protected void onEvent(AjaxRequestTarget target)
                    {
                        var currentOrdinal = operatorModel.getObject().ordinal();
                        var newOrdinal = currentOrdinal == NUMBER_OF_OPERATORS - 1 ? 0 : currentOrdinal + 1;
                        operatorModel.setObject(Operator.values()[newOrdinal]);
                        target.add(XmlReplacementEnablingBehavior.XML, linkThis, outcome);
                        target.add(operatorDropDown);
                    }
                });
            }
        };
        var secondNumber = new Label("secondNumber", secondNumberModel)
        {
            @Override
            protected void onInitialize()
            {
                super.onInitialize();
                
                add(new XmlReplacementEnablingBehavior(MATHML_NAMESPACE_URI));
                setOutputMarkupId(true);
                var linkThis = this;
                add(new AjaxEventBehavior("click")
                {
                    @Override
                    protected void onEvent(AjaxRequestTarget target)
                    {
                        var currentValue = secondNumberModel.getObject();
                        var newValue = currentValue == 9 ? 1 : currentValue + 1;
                        secondNumberModel.setObject(newValue);
                        target.add(XmlReplacementEnablingBehavior.XML, linkThis, outcome);
                        target.add(secondNumberDropDown);
                    }
                });
            }
        };
        add(firstNumber, operator, secondNumber, outcome);

        var form = new Form<>("mathmlForm");
        firstNumberDropDown.setOutputMarkupId(true);
        firstNumberDropDown.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target)
            {
                target.add(XmlReplacementEnablingBehavior.XML, firstNumber, outcome);
            }
        });
        operatorDropDown.setOutputMarkupId(true);
        operatorDropDown.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target)
            {
                target.add(XmlReplacementEnablingBehavior.XML, operator, outcome);
            }
        });
        secondNumberDropDown.setOutputMarkupId(true);
        secondNumberDropDown.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target)
            {
                target.add(XmlReplacementEnablingBehavior.XML, secondNumber, outcome);
            }
        });

        add(
                form.add(
                        firstNumberDropDown,
                        operatorDropDown,
                        secondNumberDropDown
                )
        );
    }

    private enum Operator {
        ADD("+") {
            @Override
            int compute(int firstNumber, int secondNubmer)
            {
                return firstNumber + secondNubmer;
            }
        },
        SUBTRACT("-") {
            @Override
            int compute(int firstNumber, int secondNubmer)
            {
                return firstNumber - secondNubmer;
            }
        },
        MULTIPLY("×") {
            @Override
            int compute(int firstNumber, int secondNubmer)
            {
                return firstNumber * secondNubmer;
            }
        },
        DIVIDE("÷") {
            @Override
            int compute(int firstNumber, int secondNubmer)
            {
                return firstNumber / secondNubmer;
            }
        };

        private final String text;

        Operator(String text) {
            this.text = text;
        }
        
        abstract int compute(int firstNumber, int secondNubmer);

        @Override
        public String toString()
        {
            return text;
        }
    }
    
    private static final List<Operator> OPERATORS = Arrays.asList(Operator.values());
    private static final int NUMBER_OF_OPERATORS = OPERATORS.size();
}
