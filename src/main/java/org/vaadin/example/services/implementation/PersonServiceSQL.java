package org.vaadin.example.services.implementation;

import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.vaadin.example.domain.model.Person;
import org.vaadin.example.domain.repository.PersonRepository;
import org.vaadin.example.services.IPersonService;

/**
 * Bu class, Person object ile ilgili işlemleri yönetir.
 * Verileri database üzerinden getirir, ekler, siler ve arama yapar.
 * Database işlemleri için PersonRepository kullanır.
 */
@Service
@Profile("sql") // Bu service yalnızca "sql" profile aktif olduğunda çalışır.
@ConditionalOnProperty(name = "db.enabled", havingValue = "false", matchIfMissing = true) // Eğer "db.enabled" false ise
                                                                                          // veya tanımlanmamışsa bu
                                                                                          // service etkin olur.
public class PersonServiceSQL implements IPersonService {

    private final PersonRepository repository;

    /**
     * PersonServiceSQL constructor.
     * 
     * @param repository Person object işlemlerini yöneten repository.
     */
    public PersonServiceSQL(PersonRepository repository) {
        this.repository = repository;
    }

    /**
     * Database'den bir Person object getirir.
     * 
     * @param id Aranacak object'in id'si.
     * @return Person object içeren Optional.
     */
    public Optional<Person> get(Long id) {
        return repository.findById(id);
    }

    /**
     * Yeni bir Person object ekler veya mevcut olanı günceller.
     * Eğer object mevcutsa, güncellenir; yoksa yeni kayıt oluşturulur.
     * 
     * @param entity Kaydedilecek veya güncellenecek Person object.
     * @return Kaydedilen veya güncellenmiş object.
     */
    public Person save(Person entity) {
        return repository.save(entity);
    }

    /**
     * Belirtilen id'ye sahip Person object güncellenir.
     * 
     * @param id     Güncellenecek object'in id'si.
     * @param entity Güncellenmiş object verisi.
     * @return Güncellenmiş object.
     */
    public Person update(Long id, Person entity) {
        if (repository.existsById(id)) {
            entity.setId(id);
            return repository.save(entity);
        } else {
            throw new IllegalArgumentException("Person bulunamadı!");
        }
    }

    /**
     * Belirtilen id'ye sahip Person object database'den silinir.
     * 
     * @param id Silinecek object'in id'si.
     */
    public void delete(Long id) {
        repository.deleteById(id);
    }

    /**
     * Sayfalama kullanarak Person object listesini getirir.
     * 
     * @param pageable Sayfalama bilgisi.
     * @return Sayfalı object listesi.
     */
    public Page<Person> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    /**
     * Database'deki toplam Person object sayısını döndürür.
     * 
     * @return Toplam object sayısı.
     */
    public int count() {
        return (int) repository.count();
    }

    /**
     * Belirtilen isme göre Person object arar.
     * 
     * @param name Aranacak object'in adı.
     * @return İsme göre filtrelenmiş object listesi.
     */
    @Override
    public Page<Person> searchByName(String name, Pageable pageable) {
        return (Page<Person>) repository.findByFirstNameContainingIgnoreCase(name, pageable);
    }

    /**
     * Belirtilen isme göre kaç adet Person object bulunduğunu döndürür.
     * 
     * @param name Aranacak object'in adı.
     * @return İsme göre bulunan object sayısı.
     */
    @Override
    public int countByName(String name) {
        return (int) repository.countByFirstNameContainingIgnoreCase(name);
    }
}