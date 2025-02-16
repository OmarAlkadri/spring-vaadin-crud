package org.vaadin.example.application.controllers;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.vaadin.example.domain.model.Person;
import org.vaadin.example.services.IPersonService;

/**
 * Personel verileri için REST API Controller sınıfı.
 * Bu sınıf, IPersonService ile etkileşim kurarak CRUD işlemlerini
 * gerçekleştirir.
 */
@RestController
@RequestMapping("/api/personel")
public class PersonelController {

    private final IPersonService personService;

    /**
     * PersonelController sınıfı constructor'ı.
     * 
     * @param personService Person servisi bağımlılığı enjekte edilir.
     */
    public PersonelController(IPersonService personService) {
        this.personService = personService;
    }

    /**
     * Belirtilen ID'ye sahip personeli getirir.
     * 
     * @param id Kişinin benzersiz kimliği.
     * @return Eğer kişi bulunursa 200 OK ve kişi nesnesi, aksi halde 404 Not Found
     *         döndürülür.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Person> getPerson(@PathVariable Long id) {
        Optional<Person> person = personService.get(id);
        return person.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Yeni bir person kaydı oluşturur.
     * 
     * @param person Kaydedilecek person nesnesi.
     * @return Kaydedilen person nesnesi.
     */
    @PostMapping
    public ResponseEntity<Person> createPerson(@RequestBody Person person) {
        return ResponseEntity.ok(personService.save(person));
    }

    /**
     * Var olan bir person kaydını günceller.
     * 
     * @param id     Güncellenecek kişinin ID'si.
     * @param person Güncellenmiş person bilgileri.
     * @return Güncellenmiş person nesnesi veya kişi bulunamazsa 404 Not Found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Person> updatePerson(@PathVariable Long id, @RequestBody Person person) {
        try {
            return ResponseEntity.ok(personService.update(id, person));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Belirtilen ID'ye sahip person kaydını siler.
     * 
     * @param id Silinecek person'in ID'si.
     * @return Başarı durumunda 204 No Content döndürülür.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Tüm person kayıtlarını sayfalı şekilde döndürür.
     * 
     * @param pageable Sayfalama bilgileri.
     * @return Sayfalı person listesi.
     */
    @GetMapping("/list")
    public ResponseEntity<Page<Person>> listPersons(Pageable pageable) {
        Page<Person> page = personService.list(pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * Sistemdeki toplam person sayısını döndürür.
     * 
     * @return Toplam person sayısı.
     */
    @GetMapping("/count")
    public ResponseEntity<Integer> countPersons() {
        return ResponseEntity.ok(personService.count());
    }

    /**
     * İsme göre person araması yapar.
     * 
     * @param name     Aranacak isim veya ismin bir kısmı.
     * @param pageable Sayfalama bilgileri.
     * @return Sayfalı arama sonuçları.
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Person>> searchByName(@RequestParam String name, Pageable pageable) {
        return ResponseEntity.ok(personService.searchByName(name, pageable));
    }

    /**
     * İsme göre kaç person olduğunu döndürür.
     * 
     * @param name Aranacak isim veya ismin bir kısmı.
     * @return Belirtilen ismi içeren toplam person sayısı.
     */
    @GetMapping("/search/count")
    public ResponseEntity<Integer> countByName(@RequestParam String name) {
        return ResponseEntity.ok(personService.countByName(name));
    }
}
