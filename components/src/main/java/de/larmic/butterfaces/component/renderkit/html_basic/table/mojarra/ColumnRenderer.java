package de.larmic.butterfaces.component.renderkit.html_basic.table.mojarra;

import de.larmic.butterfaces.component.base.renderer.HtmlBasicRenderer;
import de.larmic.butterfaces.component.behavior.JsfAjaxRequest;
import de.larmic.butterfaces.component.html.HtmlTooltip;
import de.larmic.butterfaces.component.html.table.HtmlColumnNew;
import de.larmic.butterfaces.component.html.table.HtmlTableNew;
import de.larmic.butterfaces.component.renderkit.html_basic.table.TableToolbarRenderer;
import de.larmic.butterfaces.model.table.SortType;
import de.larmic.butterfaces.resolver.WebXmlParameters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import java.io.IOException;
import java.util.List;

/**
 * Created by larmic on 10.09.14.
 */
@FacesRenderer(componentFamily = HtmlColumnNew.COMPONENT_FAMILY, rendererType = HtmlColumnNew.RENDERER_TYPE)
public class ColumnRenderer extends HtmlBasicRenderer {

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        if (!component.isRendered()) {
            return;
        }

        final ResponseWriter writer = context.getResponseWriter();
        final HtmlColumnNew column = (HtmlColumnNew) component;
        final HtmlTableNew table = this.findParentTable(component);
        final int columnNumber = column.getColumnNumberUsedByTable();
        final WebXmlParameters webXmlParameters = column.getWebXmlParameters();
        final HtmlTooltip tooltip = this.findTooltip(component);
        if (tooltip != null) {
            tooltip.setFor("[data-tooltip-identifier=\"" + this.createTooltipIdentifier(column) + "\"]");
        }

        writer.startElement("th", component);
        writer.writeAttribute("id", column.getClientId(), null);
        if (column.isSortColumnEnabled() && table.getTableSortModel() != null) {
            writer.writeAttribute("class", "butter-component-table-column-header butter-component-table-column-sort", null);
        } else {
            writer.writeAttribute("class", "butter-component-table-column-header", null);
        }
        writer.writeAttribute("columnNumber", "" + columnNumber, null);

        if (this.isHideColumn(table, column)) {
            writer.writeAttribute("style", "display:none", null);
        }

        final List<String> renderIds = JsfAjaxRequest.createRerenderIds(table, "click");
        if (column.isSortColumnEnabled() && table.getModel() != null && !renderIds.isEmpty()) {
            renderIds.add(table.getClientId());
            final String ajax = TableToolbarRenderer.createModelJavaScriptCall(table.getClientId(), renderIds, "sortTableRow", table.isAjaxDisableRenderRegionsOnRequest(), columnNumber + "");
            writer.writeAttribute("onclick", ajax, null);
        }

        writer.startElement(HtmlBasicRenderer.ELEMENT_DIV, component);
        writer.writeAttribute("data-tooltip-identifier", this.createTooltipIdentifier(column), null);
        writer.writeAttribute("class", column.getHeaderStyleClass(), null);
        writer.writeAttribute("style", column.getHeaderStyle(), null);

        // render header label
        writer.startElement("span", component);
        writer.writeAttribute("class", "butter-component-table-column-label", null);
        writer.writeText(column.getLabel(), null);
        writer.endElement("span");

        if (column.isSortColumnEnabled() && table.getTableSortModel() != null && !renderIds.isEmpty()) {
            writer.startElement("span", component);
            final String tableUniqueIdentifier = table.getModelUniqueIdentifier();
            final String columnUniqueIdentifier = column.getModelUniqueIdentifier();
            final SortType sortType = table.getModel().getTableRowSortingModel().getSortType(tableUniqueIdentifier, columnUniqueIdentifier);

            final StringBuilder sortSpanStyleClass = new StringBuilder("butter-component-table-column-sort-spinner ");

            if (sortType == SortType.ASCENDING) {
                sortSpanStyleClass.append(" " + webXmlParameters.getSortAscGlyphicon());
            } else if (sortType == SortType.DESCENDING) {
                sortSpanStyleClass.append(" " + webXmlParameters.getSortDescGlyphicon());
            } else {
                sortSpanStyleClass.append(" " + webXmlParameters.getSortUnknownGlyphicon());
            }

            writer.writeAttribute("class", sortSpanStyleClass.toString(), null);
            writer.endElement("span");
        }
    }

    private String createTooltipIdentifier(HtmlColumnNew column) {
        return column.getClientId() + "_div";
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        // do nothing
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        if (!component.isRendered()) {
            return;
        }

        final ResponseWriter writer = context.getResponseWriter();

        writer.endElement(HtmlBasicRenderer.ELEMENT_DIV);
        writer.endElement("th");
    }

    private HtmlTooltip findTooltip(final UIComponent component) {
        for (UIComponent uiComponent : component.getChildren()) {
            if (uiComponent instanceof HtmlTooltip) {
                return (HtmlTooltip) uiComponent;
            }
        }

        return null;
    }

    private HtmlTableNew findParentTable(final UIComponent component) {
        return component instanceof HtmlTableNew || component == null
                ? (HtmlTableNew) component
                : findParentTable(component.getParent());
    }

    private boolean isHideColumn(final HtmlTableNew table, final HtmlColumnNew column) {
        if (table.getTableColumnVisibilityModel() != null) {
            final String tableUniqueIdentifier = table.getModelUniqueIdentifier();
            final String columnUniqueIdentifier = column.getModelUniqueIdentifier();
            final Boolean hideColumn = table.getTableColumnVisibilityModel().isColumnHidden(tableUniqueIdentifier, columnUniqueIdentifier);
            if (hideColumn != null) {
                return hideColumn;
            }
        }
        return column.isHideColumn();
    }
}
