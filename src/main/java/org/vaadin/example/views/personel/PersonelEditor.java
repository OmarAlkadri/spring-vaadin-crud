package org.vaadin.example.views.personel;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.example.data.Person;
import org.vaadin.example.services.IPersonService;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

/**
 * PersonelEditor sınıfı, personel eklemek veya düzenlemek için kullanılan bir
 * bileşendir.
 * Bu sınıf, kullanıcıların yeni bir çalışan eklemesine veya mevcut çalışan
 * bilgilerini düzenlemesine olanak tanır.
 * 
 * Bileşenler:
 * - firstName: Çalışanın adını girebilmek için kullanılan TextField bileşeni.
 * - lastName: Çalışanın soyadını girebilmek için kullanılan TextField bileşeni.
 * - nationalNumber: Çalışanın TC Kimlik numarasını girebilmek için kullanılan
 * TextField bileşeni.
 * - save: Çalışan bilgilerini kaydetmek için kullanılan buton.
 * - cancel: İşlemden vazgeçmek için kullanılan buton.
 * - binder: Form verilerinin doğruluğunu kontrol eden ve verileri bağlı olan
 * nesne.
 * 
 * Bu sınıfın amacı, personel bilgilerini düzenlemek veya yeni bir personel
 * eklemektir.
 */
public class PersonelEditor extends VerticalLayout {

    private final IPersonService personService;
    private PersonelGrid personelGrid;
    private Person personel;
    private final Binder<Person> binder = new Binder<>(Person.class);

    private final TextField firstName = new TextField("Ad");
    private final TextField lastName = new TextField("Soyad");
    private final TextField nationalNumber = new TextField("TC Kimlik No");

    private final Button save = new Button("Kaydet", e -> savePerson());
    private final Button cancel = new Button("İptal", e -> clearForm());

    private final List<EditListener> editListeners = new ArrayList<>();

    /**
     * PersonelEditor sınıfının kurucusu.
     * Bu metod, form bileşenlerini başlatır ve düzenleme işlemi için hazır hale
     * getirir.
     * 
     * @param personService Personel verilerini yöneten servis sınıfı.
     */
    public PersonelEditor(IPersonService personService) {
        this.personService = personService;

        FormLayout formLayout = new FormLayout(firstName, lastName, nationalNumber);
        HorizontalLayout buttonLayout = new HorizontalLayout(save, cancel);

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        binder.forField(firstName)
                .asRequired("Ad alanı boş olamaz!")
                .bind(Person::getFirstName, Person::setFirstName);

        binder.forField(lastName)
                .asRequired("Soyad alanı boş olamaz!")
                .bind(Person::getLastName, Person::setLastName);

        binder.forField(nationalNumber)
                .asRequired("TC Kimlik No boş olamaz!")
                .bind(Person::getNationalNumber, Person::setNationalNumber);

        add(formLayout, buttonLayout);
        setWidth("400px");
    }

    /**
     * PersonelEditor bileşenine PersonelGrid bileşenini ekler.
     * PersonelGrid, düzenleme işlemi yapıldıktan sonra gridin yenilenmesini sağlar.
     * 
     * @param personelGrid Personel verilerini görüntüleyen grid bileşeni.
     */
    public void setPersonelGrid(PersonelGrid personelGrid) {
        this.personelGrid = personelGrid;
    }

    /**
     * Yeni bir personel eklemek için formu hazırlar.
     * Formda var olan veriler temizlenir ve yeni bir personel nesnesi oluşturulur.
     */
    public void addNewPerson() {
        this.personel = new Person();
        binder.readBean(this.personel);
        save.setText("Kaydet");
    }

    /**
     * Mevcut bir personeli düzenlemek için formu doldurur.
     * 
     * @param person Düzenlenecek personel nesnesi.
     */
    public void editPerson(Person person) {
        this.personel = person;
        binder.readBean(person);
        save.setText("Düzenle");
    }

    /**
     * Personel bilgilerini kaydeder.
     * Formdaki veriler, servis sınıfına kaydedilir ve grid yenilenir.
     * Eğer formda hata varsa, hata mesajı gösterilir.
     */
    private void savePerson() {
        if (!binder.validate().isOk()) {
            return; // Eğer validasyon başarısızsa işlemi durdur
        }

        try {
            binder.writeBean(personel);
            if (personel.getId() == null) {
                personService.save(personel);
                Notification.show("Yeni çalışan başarıyla kaydedildi!", 3000, Notification.Position.BOTTOM_START);
            } else {
                personService.update(personel.getId(), personel);
                Notification.show("Çalışan başarıyla güncellendi!", 3000, Notification.Position.BOTTOM_START);
            }
            personelGrid.refreshGrid();
            clearForm();
            setVisible(false);
            notifyEditListeners();

        } catch (ValidationException e) {
            Notification.show("Lütfen bilgileri kontrol edin!", 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    public void addEditListener(EditListener listener) {
        editListeners.add(listener);
    }

    private void notifyEditListeners() {
        for (EditListener listener : editListeners) {
            listener.onEdit();
        }
    }

    public interface EditListener {
        void onEdit();
    }

    /**
     * Formu temizler.
     * Düzenleme veya ekleme işlemi sonrasında formda herhangi bir veri kalmaması
     * sağlanır.
     */
    public void clearForm() {
        binder.readBean(null);
    }
}
