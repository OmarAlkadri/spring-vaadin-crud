package org.vaadin.example.views.personel;

import org.vaadin.example.services.IPersonService;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Gelişmiş ve optimize edilmiş PersonelView sınıfı.
 * Bu sınıf, personel yönetim ekranının ana bileşenlerini içerir ve
 * Vaadin framework kullanarak kullanıcı arayüzünü oluşturur.
 */
@CssImport("./themes/my-theme/personel-view.css")
@PageTitle("Personel")
@Route("personel")
@Menu(order = 1, icon = LineAwesomeIconUrl.USER_FRIENDS_SOLID)
@Uses(Icon.class)
public class PersonelView extends SplitLayout {

    private final IPersonService personService;
    private final PersonelGrid personelGrid;
    private final PersonelEditor personelEditor;
    private final PersonelSearch personelSearch;

    /**
     * PersonelView sınıfının kurucusu.
     * Bu metod, bileşenleri başlatır ve kullanıcı arayüzünü oluşturur.
     * 
     * @param personService Personel verilerini sağlayan servis.
     */
    public PersonelView(IPersonService personService) {
        this.personService = personService;

        this.personelGrid = new PersonelGrid(this.personService);
        this.personelEditor = new PersonelEditor(this.personService);

        this.personelEditor.setPersonelGrid(personelGrid);
        personelGrid.setPersonelEditor(personelEditor);
        this.personelSearch = new PersonelSearch(personelGrid);

        HorizontalLayout toolbar = createToolbar();

        configureLayout(toolbar);
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
    private void configureLayout(HorizontalLayout toolbar) {
        setSizeFull();
        addToPrimary(new VerticalLayout(toolbar, personelGrid));
        personelEditor.setVisible(false);
        addToSecondary(personelEditor);
        setSplitterPosition(70);
    }
}
