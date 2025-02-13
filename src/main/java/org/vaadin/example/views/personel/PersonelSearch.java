package org.vaadin.example.views.personel;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

/**
 * PersonelSearch sınıfı, personel arama işlevselliğini sunan bir bileşendir.
 * Bu sınıf, personel listesinde arama yapmayı ve yeni bir personel eklemeyi
 * sağlar.
 * 
 * Bileşenler:
 * - searchField: Kullanıcıdan arama metnini alır.
 * - searchButton: Arama işlemini başlatır.
 * - addNewButton: Yeni bir personel eklemeyi başlatır.
 * - personelGrid: Personel verilerini gösteren grid bileşeni.
 */
public class PersonelSearch extends HorizontalLayout {

    private final TextField searchField = new TextField(); // Arama alanı
    private final Button searchButton = new Button("Search"); // Arama butonu
    private final PersonelGrid personelGrid; // Personel verilerini gösteren grid
    private final Button addNewButton; // Yeni personel ekleme butonu
    private boolean isEditorVisible = false; // Düzenleyicinin görünürlüğünü tutan bayrak

    /**
     * PersonelSearch sınıfının kurucusu.
     * Bu metod, arama alanı ve yeni personel ekleme butonunu başlatır.
     * 
     * @param personelGrid Personel verilerini görüntüleyen grid bileşeni.
     */
    public PersonelSearch(PersonelGrid personelGrid) {
        this.personelGrid = personelGrid;
        this.addNewButton = new Button("Yeni bir çalışan ekleme", e -> toggleEditor());
        searchField.setPlaceholder("Search by name...");
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchButton.addClickListener(e -> filterGrid());
        searchField.setWidth("250px");

        searchField.addValueChangeListener(event -> {
            String searchText = event.getValue().trim().toLowerCase();
            if (searchText.isEmpty()) {
                personelGrid.refreshData();
            }
        });

        setWidthFull();
        setJustifyContentMode(JustifyContentMode.BETWEEN);
        setAlignItems(Alignment.CENTER);

        // Arama alanı ve butonunu yatay düzenle yerleştiriyoruz
        HorizontalLayout searchGroup = new HorizontalLayout(searchField, searchButton);
        searchGroup.setAlignItems(Alignment.CENTER);

        add(searchGroup, addNewButton);

        personelGrid.addEditListener(this::onEdit);
    }

    /**
     * Yeni personel eklemek veya düzenleyiciyi kapatmak için butonun durumunu
     * değiştirir.
     * Eğer düzenleyici açık ise, kapatılır ve buton eski haline döner.
     * Eğer düzenleyici kapalı ise, yeni personel eklemek için düzenleyici açılır.
     */
    private void toggleEditor() {
        if (isEditorVisible) {
            personelGrid.getPersonelEditor().setVisible(false);
            isEditorVisible = false;
            addNewButton.setText("Yeni bir çalışan ekleme");
        } else {
            personelGrid.resetEditButton();
            personelGrid.getPersonelEditor().clearForm();
            personelGrid.getPersonelEditor().addNewPerson();
            personelGrid.getPersonelEditor().setVisible(true);
            isEditorVisible = true;
            addNewButton.setText("Kapat");
        }
    }

    /**
     * Arama kutusundaki değere göre grid'i filtreler.
     * 
     * Bu metod, arama kutusundaki değeri alır, boş değilse küçük harfe çevirir ve
     * personel grid'ini bu değere göre filtreler.
     */
    private void filterGrid() {
        String searchText = searchField.getValue().trim().toLowerCase();
        if (!searchText.isEmpty()) {
            personelGrid.filterByName(searchText);
        } else {
            personelGrid.refreshData();
        }
    }

    /**
     * Düzenleme butonuna tıklanıp düzenleyici açıldığında, "Yeni bir çalışan
     * ekleme" butonunu
     * eski haline döndürmek için çağrılır.
     */
    private void onEdit() {
        isEditorVisible = false;
        addNewButton.setText("Yeni bir çalışan ekleme");
    }
}
