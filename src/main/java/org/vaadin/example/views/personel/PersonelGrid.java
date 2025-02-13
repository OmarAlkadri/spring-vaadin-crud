package org.vaadin.example.views.personel;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.example.data.SamplePerson;
import org.vaadin.example.services.SamplePersonService;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;

/**
 * PersonelGrid sınıfı, personel verilerini gösteren ve düzenleme işlevselliği
 * sağlayan bir bileşendir.
 * Bu sınıf, personel verilerini listeleyip düzenlemeyi ve silmeyi sağlar.
 * 
 * Bileşenler:
 * - samplePersonService: Personel verilerini sağlayan servis.
 * - personelEditor: Personel düzenleme bileşeni.
 * - activeEditButton: Şu anda aktif olan düzenleme butonu.
 * - editListeners: Düzenleme işlemi hakkında dinleyiciler.
 */
public class PersonelGrid extends Grid<SamplePerson> {

    private final SamplePersonService samplePersonService; // Personel servisi
    private PersonelEditor personelEditor; // Personel düzenleyicisi
    private Button activeEditButton = null; // Aktif düzenleme butonu
    private final List<EditListener> editListeners = new ArrayList<>(); // Düzenleme dinleyicileri

    /**
     * PersonelGrid sınıfının kurucusu.
     * Bu metod, personel verilerini listelemeyi ve düzenleme işlevselliği eklemeyi
     * başlatır.
     * 
     * @param samplePersonService Personel verilerini sağlayan servis.
     */
    public PersonelGrid(SamplePersonService samplePersonService) {
        super(SamplePerson.class, false);
        this.samplePersonService = samplePersonService;

        setSizeFull(); // Grid'in tam boyutlu olmasını sağlar
        addThemeVariants(GridVariant.LUMO_NO_BORDER); // Sınırları kaldırır

        // Personel bilgilerini içeren sütunlar ekleniyor
        addColumn(SamplePerson::getFirstName).setHeader("First Name").setAutoWidth(true);
        addColumn(SamplePerson::getLastName).setHeader("Last Name").setAutoWidth(true);
        addColumn(SamplePerson::getNationalNumber).setHeader("National Number").setAutoWidth(true);
        addColumn(new ComponentRenderer<>(this::createActionsLayout)).setHeader("func").setAutoWidth(true);

        // Personel verilerini sağlayan servisi kullanarak grid'e veri ekler
        setItems(query -> samplePersonService.list(VaadinSpringDataHelpers.toSpringPageRequest(query)).stream());
    }

    /**
     * Verilen isme göre kişileri filtreler ve listeyi günceller.
     * 
     * @param name Filtrelemek için kullanılacak olan isim.
     */
    public void filterByName(String name) {
        List<SamplePerson> filteredList = samplePersonService.searchByName(name);
        setItems(filteredList);
    }

    public void refreshData() {
        setItems(query -> samplePersonService.list(VaadinSpringDataHelpers.toSpringPageRequest(query)).stream());

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
    private HorizontalLayout createActionsLayout(SamplePerson person) {
        Button editButton = new Button(VaadinIcon.EDIT.create());
        editButton.setTooltipText("Düzenle");

        Button deleteButton = new Button(VaadinIcon.TRASH.create(), e -> {
            samplePersonService.delete(person.getId());
            refreshGrid(); // Grid'i yeniler
        });
        deleteButton.setTooltipText("Silme");
        deleteButton.getStyle().set("color", "red");

        // Düzenleme butonuna tıklanıldığında yapılacak işlemler
        editButton.addClickListener(e -> {
            if (personelEditor != null) {
                if (activeEditButton != null && activeEditButton != editButton) {
                    // Eğer başka bir düzenleme butonu aktifse, önceki butonu sıfırlar
                    activeEditButton.setIcon(VaadinIcon.EDIT.create());
                    activeEditButton.setTooltipText("Düzenle");
                }

                if (personelEditor.isVisible() && activeEditButton == editButton) {
                    // Düzenleyici görünürse, kapatır
                    personelEditor.setVisible(false);
                    editButton.setIcon(VaadinIcon.EDIT.create());
                    editButton.setTooltipText("Düzenle");
                    activeEditButton = null;
                } else {
                    // Düzenleyici görünmüyorsa, açar
                    personelEditor.clearForm(); // Formu temizler
                    personelEditor.editPerson(person); // Personeli düzenlemeye başlar
                    personelEditor.setVisible(true); // Düzenleyiciyi görünür yapar

                    // Düzenleme butonunun simgesini değiştirir
                    editButton.setIcon(VaadinIcon.CLOSE.create());
                    editButton.setTooltipText("İptal");
                    activeEditButton = editButton;

                    // Dinleyicilere düzenleme işleminin başladığını bildirir
                    notifyEditListeners();
                }
            }
        });

        return new HorizontalLayout(editButton, deleteButton);
    }

    /**
     * Grid'i yeniler, tüm verileri tekrar yükler.
     */
    public void refreshGrid() {
        getDataProvider().refreshAll(); // Tüm verileri tazeler
    }

    /**
     * Düzenleme butonunu sıfırlar. Eğer bir buton aktifse, simgesini eski haline
     * getirir.
     */
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

    /**
     * Yeni bir düzenleme dinleyicisi ekler.
     * 
     * @param listener Eklemek istenen dinleyici.
     */
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
