package org.vaadin.example.services;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.vaadin.example.data.Person;
import org.vaadin.example.data.PersonRepository;

/**
 * PersonServiceSQL sınıfı, personel verileriyle ilgili iş mantığını sağlar.
 * Bu sınıf, personel verilerini listelemeyi, eklemeyi, silmeyi ve aramayı
 * sağlar.
 * Ayrıca, veritabanına yapılan işlemleri yönetmek için
 * PersonRepository'yi kullanır.
 */
@Service
@Profile("sql")
@ConditionalOnProperty(name = "db.enabled", havingValue = "false", matchIfMissing = true)
public class PersonServiceSQL implements IPersonService {

    private final PersonRepository repository;

    /**
     * PersonServiceSQL sınıfının kurucusu.
     * 
     * @param repository Personel verilerini yöneten repository nesnesi.
     */
    public PersonServiceSQL(PersonRepository repository) {
        this.repository = repository;
    }

    /**
     * Veritabanındaki bir personeli id ile getirir.
     * 
     * @param id Aranacak personelin id'si.
     * @return Personel verisini içeren Optional.
     */
    public Optional<Person> get(Long id) {
        return repository.findById(id);
    }

    /**
     * Yeni bir personel kaydeder veya mevcut olanı günceller.
     * Eğer personel mevcutsa, veritabanında güncellenir; yoksa yeni bir kayıt
     * oluşturulur.
     * 
     * @param entity Kaydedilecek veya güncellenecek personel verisi.
     * @return Kaydedilen veya güncellenmiş personel nesnesi.
     */
    public Person save(Person entity) {
        return repository.save(entity); // Personeli kaydeder veya günceller
    }

    /**
     * Mevcut bir personeli günceller.
     * Eğer verilen id ile personel mevcutsa, personel verisi güncellenir.
     * 
     * @param id     Güncellenecek personelin id'si.
     * @param entity Güncellenmiş personel verisi.
     * @return Güncellenmiş personel nesnesi.
     */
    public Person update(Long id, Person entity) {
        if (repository.existsById(id)) {
            entity.setId(id);
            return repository.save(entity);
        } else {
            throw new IllegalArgumentException("Personel bulunamadı!");
        }
    }

    /**
     * Bir personeli id'sine göre siler.
     * 
     * @param id Silinecek personelin id'si.
     */
    public void delete(Long id) {
        repository.deleteById(id);
    }

    /**
     * Sayfalama kullanarak personel listesini getirir.
     * 
     * @param pageable Sayfalama bilgisi.
     * @return Personel listesini içeren sayfa.
     */
    public Page<Person> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    /**
     * Verilen isimle eşleşen kişileri arar.
     * 
     * @param name Aranacak olan kişinin adı veya soyadı.
     * @return İsim göre eşleşen kişiler listesini döndürür.
     */
    public List<Person> searchByName(String firstName) {
        return repository.findByFirstNameContainingIgnoreCase(firstName);
    }

    /**
     * Personel sayısını getirir.
     * 
     * @return Veritabanındaki toplam personel sayısı.
     */
    public long count() {
        return repository.count();
    }
}
