package org.vaadin.example.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.vaadin.example.data.SamplePerson;
import org.vaadin.example.data.SamplePersonRepository;

/**
 * SamplePersonService sınıfı, personel verileriyle ilgili iş mantığını sağlar.
 * Bu sınıf, personel verilerini listelemeyi, eklemeyi, silmeyi ve aramayı
 * sağlar.
 * Ayrıca, veritabanına yapılan işlemleri yönetmek için
 * SamplePersonRepository'yi kullanır.
 */
@Service
public class SamplePersonService implements ISamplePersonService {

    private final SamplePersonRepository repository;

    /**
     * SamplePersonService sınıfının kurucusu.
     * 
     * @param repository Personel verilerini yöneten repository nesnesi.
     */
    public SamplePersonService(SamplePersonRepository repository) {
        this.repository = repository;
    }

    /**
     * Veritabanındaki bir personeli id ile getirir.
     * 
     * @param id Aranacak personelin id'si.
     * @return Personel verisini içeren Optional.
     */
    public Optional<SamplePerson> get(Long id) {
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
    public SamplePerson save(SamplePerson entity) {
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
    public SamplePerson update(Long id, SamplePerson entity) {
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
    public Page<SamplePerson> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    /**
     * Verilen isimle eşleşen kişileri arar.
     * 
     * @param name Aranacak olan kişinin adı veya soyadı.
     * @return İsim göre eşleşen kişiler listesini döndürür.
     */
    public List<SamplePerson> searchByName(String firstName) {
        return repository.findByFirstNameContainingIgnoreCase(firstName);
    }

    /**
     * Filtreleme ve sayfalama kullanarak personel listesini getirir.
     * 
     * @param pageable Sayfalama bilgisi.
     * @param filter   Personel verileri üzerinde uygulanacak filtre.
     * @return Filtrelenmiş personel listesini içeren sayfa.
     */
    public Page<SamplePerson> list(Pageable pageable, Specification<SamplePerson> filter) {
        return repository.findAll(filter, pageable);
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
