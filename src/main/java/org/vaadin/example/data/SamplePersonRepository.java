package org.vaadin.example.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SamplePersonRepository
        extends
        JpaRepository<SamplePerson, Long>,
        JpaSpecificationExecutor<SamplePerson> {

    /**
     * Verilen adı içeren kişileri arar (büyük/küçük harf duyarsız).
     * 
     * @param firstName Aranacak olan kişinin adı.
     * @return Verilen adı içeren kişilerin listesini döndürür.
     */
    List<SamplePerson> findByFirstNameContainingIgnoreCase(String firstName);
}
