package org.vaadin.example.views.personel;

import org.vaadin.example.data.SamplePerson;
import org.vaadin.example.services.SamplePersonService;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

/**
 * PersonelForm, çalışan bilgilerini eklemek ve düzenlemek için kullanılan
 * bileşendir.
 * Form, kullanıcıdan alınan verileri doğrulayıp servis katmanına iletir.
 */
public class PersonelForm extends FormLayout {

    private final SamplePersonService personService;
    private final PersonelGrid personelGrid;
    private SamplePerson person;

    private final TextField firstName = new TextField("Ad");
    private final TextField lastName = new TextField("Soyad");
    private final TextField nationalNumber = new TextField("TC Kimlik No");

    private final Button saveButton = new Button("Kaydet", event -> savePerson());
    private final Button cancelButton = new Button("İptal", event -> clearForm());

    private final Binder<SamplePerson> binder = new Binder<>(SamplePerson.class);

    /**
     * PersonelForm sınıfının kurucusu.
     * Bu metod, form bileşenlerini başlatır ve verileri doğrulamak için binder'ı
     * ayarlar.
     * 
     * @param personService Personel verilerini yöneten servis
     * @param personelGrid  Personel verilerini görüntüleyen grid bileşeni
     */
    public PersonelForm(SamplePersonService personService, PersonelGrid personelGrid) {
        this.personService = personService;
        this.personelGrid = personelGrid;

        binder.bindInstanceFields(this); // Form alanlarını binder ile bağlar

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY); // Kaydet butonuna tema ekler
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY); // İptal butonuna tema ekler

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton); // Butonları yatay düzenle
                                                                                        // yerleştirir
        add(firstName, lastName, nationalNumber, buttonLayout); // Form alanlarını ekler
    }

    /**
     * Formdaki verileri kaydeder veya günceller.
     * Doğrulama hatası olması durumunda kullanıcıya uyarı gösterilir.
     */
    private void savePerson() {
        try {
            if (person == null) {
                person = new SamplePerson();
            }
            binder.writeBean(person);
            personService.save(person);
            clearForm();
            personelGrid.refreshGrid();
            Notification.show("Çalışan başarıyla kaydedildi!", 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS); // Başarı bildirimi gösterilir
        } catch (ValidationException e) {
            Notification.show("Lütfen bilgileri kontrol edin!", 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    /**
     * Formu temizler ve yeni giriş için hazır hale getirir.
     */
    private void clearForm() {
        person = null;
        binder.readBean(null);
    }
}
