package org.vaadin.example.views.personel;

import org.vaadin.example.data.SamplePerson;
import org.vaadin.example.services.SamplePersonService;

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

    private final SamplePersonService samplePersonService;
    private PersonelGrid personelGrid;
    private SamplePerson personel;
    private final Binder<SamplePerson> binder = new Binder<>(SamplePerson.class);

    private final TextField firstName = new TextField("Ad");
    private final TextField lastName = new TextField("Soyad");
    private final TextField nationalNumber = new TextField("TC Kimlik No");

    private final Button save = new Button("Kaydet", e -> savePerson());
    private final Button cancel = new Button("İptal", e -> clearForm());

    /**
     * PersonelEditor sınıfının kurucusu.
     * Bu metod, form bileşenlerini başlatır ve düzenleme işlemi için hazır hale
     * getirir.
     * 
     * @param samplePersonService Personel verilerini yöneten servis sınıfı.
     */
    public PersonelEditor(SamplePersonService samplePersonService) {
        this.samplePersonService = samplePersonService;

        FormLayout formLayout = new FormLayout(firstName, lastName, nationalNumber);
        HorizontalLayout buttonLayout = new HorizontalLayout(save, cancel);

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        binder.bindInstanceFields(this);

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
        this.personel = new SamplePerson();
        binder.readBean(this.personel);
        save.setText("Kaydet");
    }

    /**
     * Mevcut bir personeli düzenlemek için formu doldurur.
     * 
     * @param person Düzenlenecek personel nesnesi.
     */
    public void editPerson(SamplePerson person) {
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
        try {
            binder.writeBean(personel);
            if (personel.getId() == null) {
                samplePersonService.save(personel);
                Notification.show("Yeni çalışan başarıyla kaydedildi!", 3000, Notification.Position.BOTTOM_START);
            } else {
                // Eğer mevcut bir personel ise (ID var)
                samplePersonService.update(personel.getId(), personel);
                Notification.show("Çalışan başarıyla güncellendi!", 3000, Notification.Position.BOTTOM_START);
            }
            personelGrid.refreshGrid();
            clearForm();
        } catch (ValidationException e) {
            Notification.show("Lütfen bilgileri kontrol edin!", 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
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
