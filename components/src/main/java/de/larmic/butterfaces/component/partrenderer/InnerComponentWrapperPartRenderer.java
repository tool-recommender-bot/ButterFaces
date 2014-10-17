package de.larmic.butterfaces.component.partrenderer;

import de.larmic.butterfaces.component.html.HtmlInputComponent;

import javax.faces.component.UIInput;
import javax.faces.context.ResponseWriter;
import java.io.IOException;

/**
 * Created by larmic on 27.08.14.
 */
public class InnerComponentWrapperPartRenderer {

    public void renderInnerWrapperBegin(final HtmlInputComponent component,
                                        final ResponseWriter writer) throws IOException {
        this.renderInnerWrapperBegin(component, writer, null);
    }

    public void renderInnerWrapperBegin(final HtmlInputComponent component,
                                        final ResponseWriter writer,
                                        final String additionStyleClass) throws IOException {
        final UIInput uiComponent = (UIInput) component;

        if (!component.isReadonly()) {
            final String styleClass = StringUtils.concatWithSpace(additionStyleClass,
                    Constants.INPUT_COMPONENT_MARKER,
                    Constants.BOOTSTRAP_FORM_CONTROL,
                    !uiComponent.isValid() ? Constants.INVALID_STYLE_CLASS : null);

            uiComponent.getAttributes().put("styleClass", styleClass);

            final StringBuffer defaultStyleClass = new StringBuffer();
            if (component.getHideLabel()) {
                defaultStyleClass.append(Constants.BOOTSTRAP_COL_SM_12);
            } else {
                defaultStyleClass.append(Constants.BOOTSTRAP_COL_SM_10);

                if (StringUtils.isEmpty(component.getLabel())) {
                    defaultStyleClass
                            .append(StringUtils.SPACE)
                            .append(Constants.BOOTSTRAP_COL_SM_OFFSET_2);
                }
            }

            final String inputStyleClass = component.getInputStyleClass();

            writer.startElement("div", uiComponent);
            writer.writeAttribute("class",
                    StringUtils.isEmpty(inputStyleClass) ? defaultStyleClass.toString() : inputStyleClass, null);
        }
    }

    public void renderInnerWrapperEnd(final HtmlInputComponent component, final ResponseWriter writer)
            throws IOException {
        if (!component.isReadonly()) {
            final UIInput uiComponent = (UIInput) component;

            writer.startElement("script", uiComponent);
            writer.writeText("addLabelAttributeToInnerComponent('" + component.getClientId() + "', '"
                    + component.getLabel() + "');", null);
            writer.endElement("script");

            writer.endElement("div");
        }
    }
}