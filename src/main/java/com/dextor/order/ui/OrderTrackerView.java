package com.dextor.order.ui;

import com.dextor.base.ui.component.ViewToolbar;
import com.dextor.order.Order;
import com.dextor.order.OrderService;
import com.dextor.order.OrderStatus;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
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
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import static com.vaadin.flow.spring.data.VaadinSpringDataHelpers.toSpringPageRequest;

@Route("orders")
@PageTitle("Order Tracker")
@Menu(order = 1, icon = "vaadin:clipboard-text", title = "Order Tracker")
public class OrderTrackerView extends Main {

    private final OrderService orderService;

    private final Span totalOrdersCount;
    private final TextField customerName;
    private final TextField itemName;
    private final NumberField quantity;
    private final NumberField totalPrice;
    private final TextArea notes;
    private final Button createOrderBtn;
    private final Grid<Order> orderGrid;

    public OrderTrackerView(OrderService orderService) {
        this.orderService = orderService;

        // Statistics card
        totalOrdersCount = new Span();
        totalOrdersCount.addClassNames(
                LumoUtility.FontSize.XXXLARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.TextColor.PRIMARY
        );

        updateStatistics();

        var ordersCard = createStatCard("Total Orders", totalOrdersCount);
        var statsLayout = new HorizontalLayout(ordersCard);
        statsLayout.setWidthFull();

        // Form fields
        customerName = new TextField();
        customerName.setPlaceholder("Customer name");
        customerName.setAriaLabel("Customer name");
        customerName.setMaxLength(Order.CUSTOMER_NAME_MAX_LENGTH);
        customerName.setRequired(true);

        itemName = new TextField();
        itemName.setPlaceholder("Item name");
        itemName.setAriaLabel("Item name");
        itemName.setMaxLength(Order.ITEM_NAME_MAX_LENGTH);
        itemName.setRequired(true);

        quantity = new NumberField();
        quantity.setPlaceholder("Quantity");
        quantity.setAriaLabel("Quantity");
        quantity.setMin(1);
        quantity.setStep(1);
        quantity.setValue(1.0);
        quantity.setRequired(true);

        totalPrice = new NumberField();
        totalPrice.setPlaceholder("Total price");
        totalPrice.setAriaLabel("Total price");
        totalPrice.setMin(0);
        totalPrice.setStep(0.01);
        totalPrice.setValue(0.0);
        totalPrice.setRequired(true);
        totalPrice.setPrefixComponent(new Span("$"));

        notes = new TextArea();
        notes.setPlaceholder("Notes (optional)");
        notes.setAriaLabel("Order notes");
        notes.setMaxLength(Order.NOTES_MAX_LENGTH);
        notes.setWidthFull();

        createOrderBtn = new Button("Create Order", event -> createOrder());
        createOrderBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Grid
        var currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        var dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(getLocale())
                .withZone(ZoneId.systemDefault());

        orderGrid = new Grid<>();
        orderGrid.setItems(query -> orderService.list(toSpringPageRequest(query)).stream());
        orderGrid.addColumn(Order::getCustomerName).setHeader("Customer").setAutoWidth(true);
        orderGrid.addColumn(Order::getItemName).setHeader("Item").setAutoWidth(true);
        orderGrid.addColumn(Order::getQuantity).setHeader("Quantity").setAutoWidth(true);
        orderGrid.addColumn(order -> currencyFormatter.format(order.getTotalPrice())).setHeader("Total Price").setAutoWidth(true);
        orderGrid.addComponentColumn(order -> createStatusBadge(order.getStatus())).setHeader("Status").setAutoWidth(true);
        orderGrid.addColumn(order -> dateTimeFormatter.format(order.getOrderDate())).setHeader("Order Date").setAutoWidth(true);
        orderGrid.addComponentColumn(order -> createStatusSelector(order)).setHeader("Update Status").setAutoWidth(true);
        orderGrid.setSizeFull();

        setSizeFull();
        addClassNames(
                LumoUtility.BoxSizing.BORDER,
                LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM,
                LumoUtility.Gap.MEDIUM
        );

        add(new ViewToolbar("Order Management", ViewToolbar.group(customerName, itemName, quantity, totalPrice, createOrderBtn)));
        add(statsLayout);
        add(notes);
        add(orderGrid);
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

    private Span createStatusBadge(OrderStatus status) {
        Span badge = new Span(status.getDisplayName());
        badge.getElement().getThemeList().add("badge");
        
        // Add color based on status
        switch (status) {
            case PENDING:
                badge.getElement().getThemeList().add("warning");
                break;
            case PROCESSING:
                badge.getElement().getThemeList().add("primary");
                break;
            case SHIPPED:
                badge.getElement().getThemeList().add("contrast");
                break;
            case DELIVERED:
                badge.getElement().getThemeList().add("success");
                break;
            case CANCELLED:
                badge.getElement().getThemeList().add("error");
                break;
        }
        
        return badge;
    }

    private ComboBox<OrderStatus> createStatusSelector(Order order) {
        var statusComboBox = new ComboBox<OrderStatus>();
        statusComboBox.setItems(OrderStatus.values());
        statusComboBox.setItemLabelGenerator(OrderStatus::getDisplayName);
        statusComboBox.setValue(order.getStatus());
        
        // Add color-coded renderer for dropdown items
        statusComboBox.setRenderer(new com.vaadin.flow.data.renderer.ComponentRenderer<>(status -> {
            return createStatusBadge(status);
        }));
        
        statusComboBox.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                orderService.updateOrderStatus(order, event.getValue());
                orderGrid.getDataProvider().refreshAll();
                Notification.show("Status updated", 2000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
        });
        return statusComboBox;
    }

    private void createOrder() {
        if (customerName.isEmpty() || itemName.isEmpty() || quantity.isEmpty() || totalPrice.isEmpty()) {
            Notification.show("Please fill in all required fields", 3000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        orderService.createOrder(
                customerName.getValue(),
                itemName.getValue(),
                quantity.getValue().intValue(),
                totalPrice.getValue(),
                notes.getValue()
        );

        orderGrid.getDataProvider().refreshAll();
        updateStatistics();
        clearForm();

        Notification.show("Order created successfully", 3000, Notification.Position.BOTTOM_END)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void updateStatistics() {
        totalOrdersCount.setText(String.valueOf(orderService.count()));
    }

    private void clearForm() {
        customerName.clear();
        itemName.clear();
        quantity.setValue(1.0);
        totalPrice.setValue(0.0);
        notes.clear();
    }
}

