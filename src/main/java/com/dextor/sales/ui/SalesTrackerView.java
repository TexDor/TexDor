package com.dextor.sales.ui;

import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import com.dextor.base.ui.component.ViewToolbar;
import com.dextor.sales.Sale;
import com.dextor.sales.SaleService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import static com.vaadin.flow.spring.data.VaadinSpringDataHelpers.toSpringPageRequest;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route("sales")
@PageTitle("Sales Tracker")
@Menu(order = 2, icon = "vaadin:chart-line", title = "Sales Tracker")
public class SalesTrackerView extends Main {

    private final SaleService saleService;

    private final Span totalSalesCount;
    private final Span totalRevenueAmount;
    private final TextField customerName;
    private final TextField itemName;
    private final NumberField quantity;
    private final NumberField unitPrice;
    private final TextArea notes;
    private final Button recordSaleBtn;
    private final Grid<Sale> salesGrid;

    public SalesTrackerView(SaleService saleService) {
        this.saleService = saleService;

        // Statistics cards
        totalSalesCount = new Span();
        totalSalesCount.addClassNames(
                LumoUtility.FontSize.XXXLARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.TextColor.PRIMARY
        );

        totalRevenueAmount = new Span();
        totalRevenueAmount.addClassNames(
                LumoUtility.FontSize.XXXLARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.TextColor.SUCCESS
        );

        updateStatistics();

        var salesCard = createStatCard("Total Sales", totalSalesCount);
        var revenueCard = createStatCard("Total Revenue", totalRevenueAmount);

        var statsLayout = new HorizontalLayout(salesCard, revenueCard);
        statsLayout.setWidthFull();
        statsLayout.addClassNames(LumoUtility.Gap.MEDIUM);

        // Form fields
        customerName = new TextField();
        customerName.setPlaceholder("Customer name");
        customerName.setAriaLabel("Customer name");
        customerName.setMaxLength(Sale.CUSTOMER_NAME_MAX_LENGTH);
        customerName.setRequired(true);

        itemName = new TextField();
        itemName.setPlaceholder("Item name");
        itemName.setAriaLabel("Item name");
        itemName.setMaxLength(Sale.ITEM_NAME_MAX_LENGTH);
        itemName.setRequired(true);

        quantity = new NumberField();
        quantity.setPlaceholder("Quantity");
        quantity.setAriaLabel("Quantity");
        quantity.setMin(1);
        quantity.setStep(1);
        quantity.setValue(1.0);
        quantity.setRequired(true);

        unitPrice = new NumberField();
        unitPrice.setPlaceholder("Unit price");
        unitPrice.setAriaLabel("Unit price");
        unitPrice.setMin(0);
        unitPrice.setStep(0.01);
        unitPrice.setValue(0.0);
        unitPrice.setRequired(true);
        unitPrice.setPrefixComponent(new Span("$"));

        notes = new TextArea();
        notes.setPlaceholder("Notes (optional)");
        notes.setAriaLabel("Sale notes");
        notes.setMaxLength(Sale.NOTES_MAX_LENGTH);
        notes.setWidthFull();

        recordSaleBtn = new Button("Record Sale", event -> recordSale());
        recordSaleBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Grid
        var currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        var dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(getLocale())
                .withZone(ZoneId.systemDefault());

        salesGrid = new Grid<>();
        salesGrid.setItems(query -> saleService.list(toSpringPageRequest(query)).stream());
        salesGrid.addColumn(Sale::getCustomerName).setHeader("Customer").setAutoWidth(true);
        salesGrid.addColumn(Sale::getItemName).setHeader("Item").setAutoWidth(true);
        salesGrid.addColumn(Sale::getQuantity).setHeader("Quantity").setAutoWidth(true);
        salesGrid.addColumn(sale -> currencyFormatter.format(sale.getUnitPrice())).setHeader("Unit Price").setAutoWidth(true);
        salesGrid.addColumn(sale -> currencyFormatter.format(sale.getTotalAmount())).setHeader("Total Amount").setAutoWidth(true);
        salesGrid.addColumn(sale -> dateTimeFormatter.format(sale.getSaleDate())).setHeader("Sale Date").setAutoWidth(true);
        salesGrid.setSizeFull();

        setSizeFull();
        addClassNames(
                LumoUtility.BoxSizing.BORDER,
                LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM,
                LumoUtility.Gap.MEDIUM
        );

        add(new ViewToolbar("Sales Management", ViewToolbar.group(customerName, itemName, quantity, unitPrice, recordSaleBtn)));
        add(statsLayout);
        add(notes);
        add(salesGrid);
    }

    private Div createStatCard(String title, Span value) {
        var titleSpan = new Span(title);
        titleSpan.addClassNames(
                LumoUtility.FontSize.SMALL,
                LumoUtility.TextColor.SECONDARY,
                LumoUtility.FontWeight.MEDIUM
        );

        var content = new VerticalLayout(titleSpan, value);
        content.setSpacing(false);
        content.setPadding(false);

        var card = new Div(content);
        card.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.Padding.LARGE
        );
        card.setWidth("50%");
        return card;
    }

    private void recordSale() {
        if (customerName.isEmpty() || itemName.isEmpty() || quantity.isEmpty() || unitPrice.isEmpty()) {
            Notification.show("Please fill in all required fields", 3000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        saleService.createSale(
                customerName.getValue(),
                itemName.getValue(),
                quantity.getValue().intValue(),
                unitPrice.getValue(),
                notes.getValue()
        );

        salesGrid.getDataProvider().refreshAll();
        updateStatistics();
        clearForm();

        Notification.show("Sale recorded successfully", 3000, Notification.Position.BOTTOM_END)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void updateStatistics() {
        totalSalesCount.setText(String.valueOf(saleService.count()));
        var currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        totalRevenueAmount.setText(currencyFormatter.format(saleService.getTotalRevenue()));
    }

    private void clearForm() {
        customerName.clear();
        itemName.clear();
        quantity.setValue(1.0);
        unitPrice.setValue(0.0);
        notes.clear();
    }
}

