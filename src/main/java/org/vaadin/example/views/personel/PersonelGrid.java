package org.vaadin.example.views.personel;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.vaadin.example.data.Person;
import org.vaadin.example.services.IPersonService;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;

/**
 * PersonelGrid sınıfı, personel verilerini gösteren ve düzenleme işlevselliği
 * sağlayan bir bileşendir.
 * Bu sınıf, personel verilerini listeleyip düzenlemeyi ve silmeyi sağlar.
 * 
 * Bileşenler:
 * - PersonService: Personel verilerini sağlayan servis.
 * - personelEditor: Personel düzenleme bileşeni.
 * - activeEditButton: Şu anda aktif olan düzenleme butonu.
 * - editListeners: Düzenleme işlemi hakkında dinleyiciler.
 */
public class PersonelGrid extends Div {

    private final IPersonService personService;
    private PersonelEditor personelEditor;
    private Button activeEditButton = null;
    private final List<EditListener> editListeners = new ArrayList<>();

    private final Grid<Person> grid;
    private int currentPage = 0;
    private int pageSize = 5;
    private final Button previousButton;
    private final Button nextButton;
    private final ComboBox<Integer> pageSizeSelector;

    private HorizontalLayout pageNumberLayout;

    /**
     * PersonelGrid sınıfının kurucusu.
     * Bu metod, personel verilerini listelemeyi ve düzenleme işlevselliği eklemeyi
     * başlatır.
     * 
     * @param IPersonService Personel verilerini sağlayan servis.
     */
    public PersonelGrid(IPersonService personService) {
        this.personService = personService;
        this.grid = new Grid<>(Person.class, false);
        grid.setClassName("force-focus-outline");

        setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        grid.addColumn(Person::getFirstName).setHeader("First Name").setAutoWidth(true);
        grid.addColumn(Person::getLastName).setHeader("Last Name").setAutoWidth(true);
        grid.addColumn(Person::getNationalNumber).setHeader("National Number").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::createActionsLayout)).setHeader("func").setAutoWidth(true);
        grid.setMaxHeight("325px");

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

        HorizontalLayout leftLayout = new HorizontalLayout(pageSizeSelector);
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
    }

    private void goToPreviousPage() {
        if (currentPage > 0) {
            currentPage--;
            refreshGrid();
        }
    }

    private void goToNextPage() {
        currentPage++;
        refreshGrid();
    }

    public void refreshData() {
        grid.setItems(query -> personService.list(VaadinSpringDataHelpers.toSpringPageRequest(query)).stream());

    }

    /**
     * Personel düzenleyici bileşenini ayarlamak için kullanılan metod.
     * 
     * @param personelEditor Personel düzenleyici bileşeni.
     */
    public void setPersonelEditor(PersonelEditor personelEditor) {
        this.personelEditor = personelEditor;
    }

    /**
     * Personel düzenleyici bileşenini almak için kullanılan metod.
     * 
     * @return Personel düzenleyici bileşeni.
     */
    public PersonelEditor getPersonelEditor() {
        return personelEditor;
    }

    /**
     * Personel için düzenleme ve silme butonlarını içeren bir yatay düzen
     * oluşturur.
     * 
     * @param person Düzenleme ve silme işlemi yapılacak personel.
     * @return Düzenleme ve silme butonlarını içeren bir yatay düzen.
     */
    private HorizontalLayout createActionsLayout(Person person) {
        Button editButton = new Button(VaadinIcon.EDIT.create());
        Button deleteButton = new Button(VaadinIcon.TRASH.create(), e -> {
            personService.delete(person.getId());
            refreshGrid();
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

    public void filterByName(String name) {
        List<Person> filteredList = personService.searchByName(name);
        grid.setItems(filteredList);
    }

    /**
     * Grid'i yeniler, tüm verileri tekrar yükler.
     */
    public void refreshGrid() {
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        Page<Person> personPage = personService.list(pageable);
        grid.setItems(personPage.getContent());
        previousButton.setEnabled(currentPage > 0);
        nextButton.setEnabled(personPage.hasNext());
        updatePageNumberLayout(personPage.getTotalPages());
    }

    private void updatePageNumberLayout(int totalPages) {
        pageNumberLayout.removeAll();
        for (int i = 0; i < totalPages; i++) {
            final int pageIndex = i; // تعريف متغير نهائي محلي
            Button pageButton = new Button(String.valueOf(pageIndex + 1));
            pageButton.addClickListener(e -> {
                currentPage = pageIndex;
                refreshGrid();
            });
            pageButton.setEnabled(pageIndex != currentPage);
            pageNumberLayout.add(pageButton);
        }

    }

    public void resetEditButton() {
        if (activeEditButton != null) {
            activeEditButton.setIcon(VaadinIcon.EDIT.create());
            activeEditButton.setTooltipText("Düzenle");
            activeEditButton = null;
        }
    }

    /**
     * Düzenleme işlemi için dinleyici arayüzü.
     */
    public interface EditListener {
        void onEdit();
    }

    public void addEditListener(EditListener listener) {
        editListeners.add(listener);
    }

    /**
     * Tüm dinleyicilere düzenleme işleminin başladığını bildirir.
     */
    private void notifyEditListeners() {
        for (EditListener listener : editListeners) {
            listener.onEdit();
        }
    }
}
