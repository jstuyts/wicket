package org.apache.wicket.examples.ajax.builtin;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.core.request.handler.PreactReplacementEnablingBehavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;

/**
 * Demo page for Preact, showing manipulation of SVG and MathML.
 */
public class SvgAndMathlPagePreact extends BasePage
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

        var ticTacToe = new WebMarkupContainer("ticTacToe");
        ticTacToe.add(new PreactReplacementEnablingBehavior());
        add(ticTacToe);

        var boardStateModel = Model.of(new SquareState[] {
                SquareState.EMPTY,
                SquareState.EMPTY,
                SquareState.EMPTY,
                SquareState.EMPTY,
                SquareState.EMPTY,
                SquareState.EMPTY,
                SquareState.EMPTY,
                SquareState.EMPTY,
                SquareState.EMPTY
        });
        var nextTurnModel = Model.of(SquareState.CROSS);

        var svgButtonsForm = new Form<>("svgForm");
        svgButtonsForm.setOutputMarkupId(true);
        add(svgButtonsForm);

        var squares = new WebMarkupContainer[9];
        for (var column = 'A'; column < 'D'; column += 1) {
            for (var row = 1; row < 4; row += 1) {
                var squareId = String.valueOf(column) + row;
                var index = (column - 'A') * 3 + row - 1;
                var squareStateModel = LambdaModel.of(boardStateModel,
                        boardState -> boardState[index],
                        (boardState, squareState) -> boardState[index] = squareState);
                var square = new WebMarkupContainer("square" + squareId, squareStateModel);
                square.add(new PreactReplacementEnablingBehavior());
                ticTacToe.add(square);
                squares[index] = square;
                
                var circle = new WebMarkupContainer("circle" + squareId);
                circle.add(new PreactReplacementEnablingBehavior());
                circle.add(AttributeAppender.replace("class", boardStateModel.map(boardState -> boardState[index] == SquareState.CIRCLE ? "circle" : "hidden")));
                square.add(circle);

                var cross = new WebMarkupContainer("cross" + squareId);
                cross.add(new PreactReplacementEnablingBehavior());
                cross.add(AttributeAppender.replace("class", boardStateModel.map(boardState -> boardState[index] == SquareState.CROSS ? "cross" : "hidden")));
                square.add(cross);

                var button = new AjaxButton("button" + squareId) {
                    @Override
                    protected void onConfigure()
                    {
                        super.onConfigure();

                        setEnabled(boardStateModel.getObject()[index] == SquareState.EMPTY);
                    }

                    @Override
                    protected void onSubmit(AjaxRequestTarget target)
                    {
                        boardStateModel.getObject()[index] = nextTurnModel.getObject();
                        nextTurnModel.setObject(nextTurnModel.getObject() == SquareState.CROSS ? SquareState.CIRCLE : SquareState.CROSS);

                        target.add(PreactReplacementEnablingBehavior.PREACT, squares[index]);
                        target.add(this);
                    }
                };
                button.setOutputMarkupId(true);
                svgButtonsForm.add(button);
            }
        }

        var resetButton = new AjaxButton("reset") {
            @Override
            protected void onSubmit(AjaxRequestTarget target)
            {
                boardStateModel.setObject(new SquareState[] {
                        SquareState.EMPTY,
                        SquareState.EMPTY,
                        SquareState.EMPTY,
                        SquareState.EMPTY,
                        SquareState.EMPTY,
                        SquareState.EMPTY,
                        SquareState.EMPTY,
                        SquareState.EMPTY,
                        SquareState.EMPTY
                });
                nextTurnModel.setObject(SquareState.CROSS);

                target.add(PreactReplacementEnablingBehavior.PREACT, ticTacToe);
                target.add(svgButtonsForm);
            }
        };
        svgButtonsForm.add(resetButton);

        var firstNumberDropDown = new DropDownChoice<>("firstNumberDropDown", firstNumberModel, ONE_TO_NINE);
        var operatorDropDown = new DropDownChoice<>("operatorDropDown", operatorModel, OPERATORS);
        var secondNumberDropDown = new DropDownChoice<>("secondNumberDropDown", secondNumberModel, ONE_TO_NINE);

        var outcome = new Label("outcome", outcomeModel)
                .add(new PreactReplacementEnablingBehavior());
        var firstNumber = new Label("firstNumber", firstNumberModel)
        {
            @Override
            protected void onInitialize()
            {
                super.onInitialize();
                
                add(new PreactReplacementEnablingBehavior());
            }
        };
        var operator = new Label("operator", operatorModel)
        {
            @Override
            protected void onInitialize()
            {
                super.onInitialize();
                
                add(new PreactReplacementEnablingBehavior());
            }
        };
        var secondNumber = new Label("secondNumber", secondNumberModel)
        {
            @Override
            protected void onInitialize()
            {
                super.onInitialize();
                
                add(new PreactReplacementEnablingBehavior());
            }
        };
        add(firstNumber, operator, secondNumber, outcome);

        var form = new Form<>("mathmlForm");
        firstNumberDropDown.setOutputMarkupId(true);
        firstNumberDropDown.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target)
            {
                target.add(PreactReplacementEnablingBehavior.PREACT, firstNumber, outcome);
            }
        });
        operatorDropDown.setOutputMarkupId(true);
        operatorDropDown.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target)
            {
                target.add(PreactReplacementEnablingBehavior.PREACT, operator, outcome);
            }
        });
        secondNumberDropDown.setOutputMarkupId(true);
        secondNumberDropDown.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target)
            {
                target.add(PreactReplacementEnablingBehavior.PREACT, secondNumber, outcome);
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

    @Override
    public void renderHead(IHeaderResponse response)
    {
        super.renderHead(response);
        
        response.render(CssHeaderItem.forReference(new CssResourceReference(SvgAndMathlPagePreact.class, "tic-tac-toe.css")));
    }

    private enum SquareState {
        EMPTY,
        CIRCLE,
        CROSS
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
}
