package org.vaadin.example.views.personel;

import org.vaadin.example.services.SamplePersonService;
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
 * PersonelView sınıfı, Vaadin framework kullanılarak geliştirilmiş bir görünümü
 * temsil eder.
 * Bu görünüm, personel yönetimi için kullanılan bir kullanıcı arayüzüdür.
 * 
 * Bileşenler:
 * - SamplePersonService: Personel verilerini sağlayan servis sınıfıdır.
 * - PersonelGrid: Personel verilerinin tablo halinde görüntülenmesini sağlar.
 * - PersonelEditor: Seçilen bir personelin bilgilerini düzenlemeye olanak
 * tanır.
 * - PersonelSearch: Personel arama işlevselliği sunar.
 * - SplitLayout: Görünümde iki ana alanın bulunmasını sağlar, biri personel
 * listesi diğeri ise düzenleme alanıdır.
 * 
 * @CssImport("./themes/my-theme/personel-view.css") ile özel stil dosyası içe
 * aktarılır.
 * @PageTitle("Personel") sayfanın başlığını belirler.
 * @Route("personel") bu sınıfın "personel" URL yolunda görüneceğini belirtir.
 * 
 * @Menu(order = 1, icon = LineAwesomeIconUrl.USER_FRIENDS_SOLID) menüdeki
 *             sırasını ve ikonunu belirler.
 * @Uses(Icon.class) ikona bağlı bağımlılığı ekler.
 */
@CssImport("./themes/my-theme/personel-view.css")
@PageTitle("Personel")
@Route("personel")
@Menu(order = 1, icon = LineAwesomeIconUrl.USER_FRIENDS_SOLID)
@Uses(Icon.class)
public class PersonelView extends SplitLayout {

    private final SamplePersonService samplePersonService;
    private final PersonelGrid personelGrid;
    private final PersonelEditor personelEditor;
    private final PersonelSearch personelSearch;
    private boolean isEditorVisible = false;

    /**
     * PersonelView sınıfının kurucusu.
     * Bu metod, personel yönetim ekranını oluşturan tüm bileşenleri başlatır.
     * 
     * @param samplePersonService Personel verilerini sağlayan servis sınıfı.
     */
    public PersonelView(SamplePersonService samplePersonService) {
        this.samplePersonService = samplePersonService;

        this.personelGrid = new PersonelGrid(samplePersonService);
        this.personelEditor = new PersonelEditor(samplePersonService);
        this.personelEditor.setPersonelGrid(personelGrid);

        this.personelSearch = new PersonelSearch(personelGrid);
        personelGrid.setPersonelEditor(this.personelEditor);

        HorizontalLayout toolbar = new HorizontalLayout(personelSearch);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        setSizeFull();
        addToPrimary(new VerticalLayout(toolbar, personelGrid));
        personelEditor.setVisible(false);
        addToSecondary(personelEditor);

        setSplitterPosition(70);
    }
}
