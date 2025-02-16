package org.vaadin.example.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vaadin.example.domain.model.Person;

/**
 * IPersonService, personel yönetimi için gerekli servis metodlarını tanımlar.
 */
public interface IPersonService {
    /**
     * Belirtilen ID'ye sahip personeli getirir.
     */
    Optional<Person> get(Long id);

    /**
     * Yeni bir personel kaydeder.
     */
    Person save(Person entity);

    /**
     * Belirtilen ID'ye sahip personeli siler.
     */
    void delete(Long id);

    /**
     * Sayfalı olarak personel listesini döndürür.
     */
    Page<Person> list(Pageable pageable);

    /**
     * Belirtilen isim kriterine göre personel araması yapar.
     */
    Page<Person> searchByName(String name, Pageable pageable);

    /**
     * Belirtilen isim kriterine göre toplam personel sayısını döndürür.
     */
    int countByName(String name);

    /**
     * Veritabanındaki toplam personel sayısını döndürür.
     */
    int count();

    /**
     * Mevcut bir personelin bilgilerini günceller.
     * 
     * Karmaşıklık: Güncellenen nesnenin mevcut ID ile eşleşmesi ve doğru şekilde
     * işlenmesi gerekir. Veritabanı tarafında doğrulama mekanizmaları olmalıdır.
     */
    Person update(Long id, Person entity);
}
