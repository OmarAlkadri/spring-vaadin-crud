package org.vaadin.example.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PersonRepository
        extends
        JpaRepository<Person, Long>,
        JpaSpecificationExecutor<Person> {

    /**
     * Verilen adı içeren kişileri arar (büyük/küçük harf duyarsız).
     * 
     * @param firstName Aranacak olan kişinin adı.
     * @return Verilen adı içeren kişilerin listesini döndürür.
     */
    List<Person> findByFirstNameContainingIgnoreCase(String firstName);
}
