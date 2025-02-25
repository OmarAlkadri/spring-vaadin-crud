package org.vaadin.example.application.views.personel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.vaadin.example.domain.model.Person;
import org.vaadin.example.infrastructure.PersonelDataProvider;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;

public class PersonelGrid extends Div {
    private Dialog confirmDialog = new Dialog();
    private Person personToDelete;
    private final DataProvider<Person, Void> dataProvider;
    private PersonelEditor personelEditor;
    private Button activeEditButton = null;
    private final List<EditListener> editListeners = new ArrayList<>();

    private final Grid<Person> grid;
    private int currentPage = 0;
    private int pageSize = 5;
    private final Button previousButton;
    private final Button nextButton;
    private final ComboBox<Integer> pageSizeSelector;
    private final TextField totalItemsField;

    private HorizontalLayout pageNumberLayout;

    public PersonelGrid(DataProvider<Person, Void> dataProvider) {
        this.dataProvider = dataProvider;
        this.grid = new Grid<>(Person.class, false);
        grid.setClassName("force-focus-outline");

        setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        grid.addColumn(Person::getFirstName).setHeader("First Name").setAutoWidth(true);
        grid.addColumn(Person::getLastName).setHeader("Last Name").setAutoWidth(true);
        grid.addColumn(Person::getNationalNumber).setHeader("National Number").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::createActionsLayout)).setHeader("func").setAutoWidth(true);
        grid.setMaxHeight("322px");

        setHeight(null);
        setWidthFull();

        previousButton = new Button("Previous", e -> goToPreviousPage());
        nextButton = new Button("Next", e -> goToNextPage());

        pageSizeSelector = new ComboBox<>("Items per page");
        pageSizeSelector.setItems(5, 10, 20, 50);
        pageSizeSelector.setValue(pageSize);
        pageSizeSelector.addValueChangeListener(event -> {
            pageSize = event.getValue();
            currentPage = 0;
            refreshGrid();
        });

        totalItemsField = new TextField("Total Items");
        totalItemsField.setReadOnly(true);
        totalItemsField.setWidth("100px");

        HorizontalLayout leftLayout = new HorizontalLayout(pageSizeSelector, totalItemsField);
        leftLayout.setWidthFull();
        leftLayout.setJustifyContentMode(JustifyContentMode.START);

        pageNumberLayout = new HorizontalLayout();
        pageNumberLayout.setWidthFull();
        pageNumberLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        HorizontalLayout rightLayout = new HorizontalLayout(previousButton, pageNumberLayout, nextButton);
        rightLayout.setJustifyContentMode(JustifyContentMode.END);

        HorizontalLayout paginationControls = new HorizontalLayout(leftLayout, rightLayout);
        paginationControls.setWidthFull();
        paginationControls.setJustifyContentMode(JustifyContentMode.BETWEEN);
        paginationControls.setAlignItems(Alignment.END);
        paginationControls.setPadding(true);

        add(grid, paginationControls);
        refreshGrid();
        setupConfirmationDialog();
    }

    private void goToPreviousPage() {
        if (currentPage > 0) {
            currentPage--;
            refreshGrid();
        }
    }

    public void filterByName(String name) {
        if (dataProvider instanceof PersonelDataProvider) {
            ((PersonelDataProvider) dataProvider).setSearchQuery(name);
            refreshGrid();
        }
    }

    private void goToNextPage() {
        currentPage++;
        refreshGrid();
    }

    public void refreshGrid() {
        if (dataProvider instanceof PersonelDataProvider) {
            PersonelDataProvider personelDataProvider = (PersonelDataProvider) dataProvider;

            List<Person> items = personelDataProvider.fetch(new Query<>(currentPage, pageSize, null, null, null))
                    .collect(Collectors.toList());

            if (items.isEmpty() && currentPage > 0) {
                currentPage--;
                items = personelDataProvider.fetch(new Query<>(currentPage, pageSize, null, null, null))
                        .collect(Collectors.toList());
            }

            grid.setItems(items);
            previousButton.setEnabled(currentPage > 0);
            nextButton.setEnabled(personelDataProvider.hasNext());

            int totalItems = personelDataProvider.getTotalItemCount();
            totalItemsField.setValue(String.valueOf(totalItems));

            updatePageNumberLayout(personelDataProvider.getTotalPages(pageSize));
        }
    }

    private void updatePageNumberLayout(int totalPages) {
        pageNumberLayout.removeAll();
        List<Integer> pages = new ArrayList<>();

        if (totalPages > 0)
            pages.add(0);

        if (currentPage > 2) {
            pages.add(-1);
        }

        for (int i = Math.max(1, currentPage - 1); i <= Math.min(totalPages - 2, currentPage + 1); i++) {
            pages.add(i);
        }

        if (currentPage < totalPages - 3) {
            pages.add(-2);
        }

        if (totalPages > 1 && !pages.contains(totalPages - 1)) {
            pages.add(totalPages - 1);
        }

        for (Integer pageNum : pages) {
            if (pageNum == -1) {
                Button dotsButton = new Button("...");
                dotsButton.addClickListener(e -> {
                    currentPage = Math.max(0, currentPage - 3);
                    refreshGrid();
                });
                pageNumberLayout.add(dotsButton);
            } else if (pageNum == -2) {
                Button dotsButton = new Button("...");
                dotsButton.addClickListener(e -> {
                    currentPage = Math.min(totalPages - 1, currentPage + 3);
                    refreshGrid();
                });
                pageNumberLayout.add(dotsButton);
            } else {
                Button pageButton = new Button(String.valueOf(pageNum + 1));
                pageButton.addClickListener(e -> {
                    if (pageNum != currentPage) {
                        currentPage = pageNum;
                        refreshGrid();
                    }
                });

                if (pageNum == currentPage) {
                    pageButton.setEnabled(false);
                    pageButton.addClassName("selected");
                }

                pageNumberLayout.add(pageButton);
            }
        }
    }

    public void setPersonelEditor(PersonelEditor personelEditor) {
        this.personelEditor = personelEditor;
    }

    public PersonelEditor getPersonelEditor() {
        return personelEditor;
    }

    private void setupConfirmationDialog() {
        confirmDialog.add(new Text("Bu kişiyi silmek istediğinizden emin misiniz?"));

        Button confirmButton = new Button("Evet", event -> {
            if (personToDelete != null) {
                ((PersonelDataProvider) dataProvider).delete(personToDelete.getId());
                refreshGrid();
                Notification.show("Öğe başarıyla silindi!", 3000, Notification.Position.TOP_END)
                        .addThemeVariants(NotificationVariant.LUMO_WARNING);
            }
            confirmDialog.close();
        });

        Button cancelButton = new Button("İptal", event -> confirmDialog.close());
        confirmDialog.add(new HorizontalLayout(confirmButton, cancelButton));
    }

    private HorizontalLayout createActionsLayout(Person person) {
        Button editButton = new Button(VaadinIcon.EDIT.create());
        Button deleteButton = new Button(VaadinIcon.TRASH.create(), e -> {
            personToDelete = person;
            confirmDialog.open();
        });
        deleteButton.getStyle().set("color", "red");

        editButton.addClickListener(e -> {
            if (personelEditor != null) {
                if (activeEditButton != null && activeEditButton != editButton) {
                    activeEditButton.setIcon(VaadinIcon.EDIT.create());
                }

                if (personelEditor.isVisible() && activeEditButton == editButton) {
                    personelEditor.setVisible(false);
                    editButton.setIcon(VaadinIcon.EDIT.create());
                    activeEditButton = null;
                } else {
                    personelEditor.clearForm();
                    personelEditor.editPerson(person);
                    personelEditor.setVisible(true);
                    editButton.setIcon(VaadinIcon.CLOSE.create());
                    activeEditButton = editButton;
                    notifyEditListeners();
                }
            }
        });

        return new HorizontalLayout(editButton, deleteButton);
    }

    public void resetEditButton() {
        if (activeEditButton != null) {
            activeEditButton.setIcon(VaadinIcon.EDIT.create());
            activeEditButton.setTooltipText("Düzenle");
            activeEditButton = null;
        }
    }

    public interface EditListener {
        void onEdit();
    }

    public void addEditListener(EditListener listener) {
        editListeners.add(listener);
    }

    private void notifyEditListeners() {
        for (EditListener listener : editListeners) {
            listener.onEdit();
        }
    }
}
