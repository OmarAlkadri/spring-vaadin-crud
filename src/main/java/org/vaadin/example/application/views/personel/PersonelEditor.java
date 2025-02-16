package org.vaadin.example.application.views.personel;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.example.domain.model.Person;
import org.vaadin.example.infrastructure.PersonelDataProvider;

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
 */
public class PersonelEditor extends VerticalLayout {

    private final PersonelDataProvider dataProvider;
    private PersonelGrid personelGrid;
    private Person personel;
    private final Binder<Person> binder = new Binder<>(Person.class);

    private final TextField firstName = new TextField("Ad");
    private final TextField lastName = new TextField("Soyad");
    private final TextField nationalNumber = new TextField("TC Kimlik No");

    private final Button save = new Button("Kaydet", e -> savePerson());
    private final Button cancel = new Button("İptal", e -> clearForm());

    private final List<EditListener> editListeners = new ArrayList<>();

    public PersonelEditor(PersonelDataProvider dataProvider) {
        this.dataProvider = dataProvider;

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

    public void setPersonelGrid(PersonelGrid personelGrid) {
        this.personelGrid = personelGrid;
    }

    public void addNewPerson() {
        this.personel = new Person();
        binder.readBean(this.personel);
        save.setText("Kaydet");
    }

    public void editPerson(Person person) {
        this.personel = person;
        binder.readBean(person);
        save.setText("Düzenle");
    }

    private void savePerson() {
        if (!binder.validate().isOk()) {
            return;
        }

        try {
            binder.writeBean(personel);
            if (personel.getId() == null) {
                dataProvider.save(personel);
                Notification.show("Yeni çalışan başarıyla kaydedildi!", 3000, Notification.Position.TOP_END)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                dataProvider.update(personel.getId(), personel);
                Notification.show("Çalışan başarıyla güncellendi!", 3000, Notification.Position.TOP_END)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
            dataProvider.refreshAll();
            personelGrid.refreshGrid();
            clearForm();
            setVisible(false);
            notifyEditListeners();
        } catch (ValidationException e) {
            Notification.show("Lütfen bilgileri kontrol edin!", 3000, Notification.Position.TOP_END)
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

    public void clearForm() {
        binder.readBean(null);
    }
}
