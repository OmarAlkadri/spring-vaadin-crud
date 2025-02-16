package org.vaadin.example.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.vaadin.example.domain.model.Person;

/**
 * Person entity'si için repository arayüzü.
 * JpaRepository ve JpaSpecificationExecutor kullanılarak,
 * temel CRUD işlemleri ve dinamik sorgulamalar desteklenmektedir.
 */
@Repository
public interface PersonRepository
        extends
        JpaRepository<Person, Long>, // Temel CRUD işlemleri için JpaRepository kullanılır.
        JpaSpecificationExecutor<Person> { // Dinamik filtreleme için JpaSpecificationExecutor eklenmiştir.

    /**
     * Verilen adın herhangi bir kısmını içeren kişileri arar.
     * Arama büyük/küçük harf duyarsız (case-insensitive) olarak yapılır.
     * 
     * @param firstName Aranacak olan kişinin adı veya adının bir kısmı.
     * @param pageable  Sayfalama ve sıralama bilgilerini içeren nesne.
     * @return Verilen adı içeren kişilerin sayfalı (paged) listesi döndürülür.
     */
    Page<Person> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);

    /**
     * Verilen adı içeren kişilerin toplam sayısını döndürür.
     * Arama büyük/küçük harf duyarsız şekilde yapılır.
     * 
     * @param firstName Aranacak olan kişinin adı veya adının bir kısmı.
     * @return Verilen adı içeren kişilerin toplam sayısı döndürülür.
     */
    int countByFirstNameContainingIgnoreCase(String firstName);
}
