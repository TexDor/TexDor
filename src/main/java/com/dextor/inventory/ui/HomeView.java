package com.dextor.inventory.ui;

import java.text.NumberFormat;
import java.util.Locale;

import com.dextor.base.ui.component.ViewToolbar;
import com.dextor.inventory.InventoryItem;
import com.dextor.inventory.InventoryService;
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

@Route("")
@PageTitle("Home - Inventory")
@Menu(order = 0, icon = "vaadin:home", title = "Home")
public class HomeView extends Main {

    private final InventoryService inventoryService;

    private final Span totalItemsCount;
    private final Span totalQuantityCount;
    private final TextField itemName;
    private final TextArea itemDescription;
    private final NumberField itemQuantity;
    private final NumberField itemPrice;
    private final Button addItemBtn;
    private final Grid<InventoryItem> inventoryGrid;

    public HomeView(InventoryService inventoryService) {
        this.inventoryService = inventoryService;

        // Statistics cards
        totalItemsCount = new Span();
        totalItemsCount.addClassNames(
                LumoUtility.FontSize.XXXLARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.TextColor.PRIMARY
        );

        totalQuantityCount = new Span();
        totalQuantityCount.addClassNames(
                LumoUtility.FontSize.XXXLARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.TextColor.PRIMARY
        );

        updateStatistics();

        var itemsCard = createStatCard("Total Items", totalItemsCount);
        var quantityCard = createStatCard("Total Quantity", totalQuantityCount);

        var statsLayout = new HorizontalLayout(itemsCard, quantityCard);
        statsLayout.setWidthFull();
        statsLayout.addClassNames(LumoUtility.Gap.MEDIUM);

        // Form fields
        itemName = new TextField();
        itemName.setPlaceholder("Item name");
        itemName.setAriaLabel("Item name");
        itemName.setMaxLength(InventoryItem.NAME_MAX_LENGTH);
        itemName.setRequired(true);

        itemDescription = new TextArea();
        itemDescription.setPlaceholder("Description (optional)");
        itemDescription.setAriaLabel("Item description");
        itemDescription.setMaxLength(InventoryItem.DESCRIPTION_MAX_LENGTH);

        itemQuantity = new NumberField();
        itemQuantity.setPlaceholder("Quantity");
        itemQuantity.setAriaLabel("Quantity");
        itemQuantity.setMin(0);
        itemQuantity.setStep(1);
        itemQuantity.setValue(0.0);
        itemQuantity.setRequired(true);

        itemPrice = new NumberField();
        itemPrice.setPlaceholder("Price");
        itemPrice.setAriaLabel("Price");
        itemPrice.setMin(0);
        itemPrice.setStep(0.01);
        itemPrice.setValue(0.0);
        itemPrice.setRequired(true);
        itemPrice.setPrefixComponent(new Span("$"));

        addItemBtn = new Button("Add Item", event -> addItem());
        addItemBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Grid
        var currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        inventoryGrid = new Grid<>();
        inventoryGrid.setItems(query -> inventoryService.list(toSpringPageRequest(query)).stream());
        inventoryGrid.addColumn(InventoryItem::getName).setHeader("Item Name").setAutoWidth(true);
        inventoryGrid.addColumn(InventoryItem::getDescription).setHeader("Description").setAutoWidth(true);
        inventoryGrid.addColumn(InventoryItem::getQuantity).setHeader("Quantity").setAutoWidth(true);
        inventoryGrid.addColumn(item -> currencyFormatter.format(item.getPrice())).setHeader("Price").setAutoWidth(true);
        inventoryGrid.setSizeFull();

        setSizeFull();
        addClassNames(
                LumoUtility.BoxSizing.BORDER,
                LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM,
                LumoUtility.Gap.MEDIUM
        );

        add(new ViewToolbar("Inventory Management", ViewToolbar.group(itemName, itemQuantity, itemPrice, addItemBtn)));
        add(statsLayout);
        add(itemDescription);
        add(inventoryGrid);
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

    private void addItem() {
        if (itemName.isEmpty() || itemQuantity.isEmpty() || itemPrice.isEmpty()) {
            Notification.show("Please fill in all required fields", 3000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        inventoryService.createItem(
                itemName.getValue(),
                itemDescription.getValue(),
                itemQuantity.getValue().intValue(),
                itemPrice.getValue()
        );

        inventoryGrid.getDataProvider().refreshAll();
        updateStatistics();
        clearForm();

        Notification.show("Item added successfully", 3000, Notification.Position.BOTTOM_END)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void updateStatistics() {
        totalItemsCount.setText(String.valueOf(inventoryService.count()));
        totalQuantityCount.setText(String.valueOf(inventoryService.getTotalQuantity()));
    }

    private void clearForm() {
        itemName.clear();
        itemDescription.clear();
        itemQuantity.setValue(0.0);
        itemPrice.setValue(0.0);
    }
}

