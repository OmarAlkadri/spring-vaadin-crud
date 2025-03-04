package org.vaadin.example.application.views.personel;

import org.vaadin.example.infrastructure.PersonelDataProvider;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Gelişmiş ve optimize edilmiş PersonelView sınıfı.
 * Bu sınıf, personel yönetim ekranının ana bileşenlerini içerir.
 */
// @CssImport("./themes/my-theme/personel-view.css")
@PageTitle("Personel")
@Route("personel")
@Menu(order = 1, icon = LineAwesomeIconUrl.USER_FRIENDS_SOLID)
@Uses(Icon.class)
public class PersonelView extends SplitLayout {

    private final PersonelDataProvider personelDataProvider;
    private final PersonelGrid personelGrid;
    private final PersonelEditor personelEditor;
    private final PersonelSearch personelSearch;

    /**
     * PersonelView sınıfının kurucusu.
     * 
     * @param personelDataProvider Personel verilerini sağlayan veri sağlayıcı.
     */
    public PersonelView(PersonelDataProvider personelDataProvider) {
        this.personelDataProvider = personelDataProvider;

        this.personelGrid = new PersonelGrid(this.personelDataProvider);
        this.personelEditor = new PersonelEditor(this.personelDataProvider);

        this.personelEditor.setPersonelGrid(personelGrid);
        personelGrid.setPersonelEditor(personelEditor);
        this.personelSearch = new PersonelSearch(personelGrid);

        HorizontalLayout toolbar = createToolbar();
        Button refreshButton = createRefreshButton();

        configureLayout(toolbar, refreshButton);
    }

    private Button createRefreshButton() {
        Button refreshButton = new Button("Yenile", event -> {
            personelDataProvider.addNewMockPerson();
            personelGrid.refreshGrid();
        });

        refreshButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        refreshButton.setWidthFull();
        refreshButton.setMaxWidth("150px");
        return refreshButton;
    }

    /**
     * Araç çubuğunu oluşturur ve ayarlar.
     * 
     * @return Oluşturulan HorizontalLayout nesnesi.
     */
    private HorizontalLayout createToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout(personelSearch);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        return toolbar;
    }

    /**
     * Sayfa düzenini ayarlar ve bileşenleri uygun yerlere ekler.
     * 
     * @param toolbar Arama ve ekleme butonlarını içeren araç çubuğu.
     */
    private void configureLayout(HorizontalLayout toolbar, Button refreshButton) {
        setSizeFull();

        HorizontalLayout refreshButtonWrapper = new HorizontalLayout(refreshButton);
        refreshButtonWrapper.setWidthFull();
        refreshButtonWrapper.setJustifyContentMode(JustifyContentMode.END);

        VerticalLayout layout = new VerticalLayout(toolbar, personelGrid, refreshButtonWrapper);
        layout.setSizeFull();
        layout.setSpacing(true);
        layout.setAlignItems(Alignment.STRETCH);
        layout.setFlexGrow(1, personelGrid);

        addToPrimary(layout);
        personelEditor.setVisible(false);
        addToSecondary(personelEditor);
        setSplitterPosition(70);
    }
}
