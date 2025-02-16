package org.vaadin.example.services.config;

import org.springframework.context.annotation.Configuration;

/**
 * PersonServiceConfig, farklı database yapılandırmalarına göre uygun service
 * implementasyonunu sağlar.
 * Eğer db.enabled=true ise PersonServiceSQL kullanılır.
 * Eğer db.enabled=false veya tanımlanmamışsa PersonServiceDummy kullanılır.
 */
@Configuration
public class PersonServiceConfig {

    /**
     * Eğer "db.enabled" true ise PersonServiceSQL bean olarak tanımlanır.
     * Bu yapılandırma, gerçek database bağlantısı gerektiren senaryolar için
     * kullanılır.
     * 
     * @param PersonRepository Veritabanı işlemlerini yöneten repository.
     * @return PersonServiceSQL nesnesi.
     */
    /*
     * @Bean
     * 
     * @ConditionalOnProperty(name = "db.enabled", havingValue = "true")
     * PersonServiceSQL PersonServiceSQL(PersonRepository PersonRepository) {
     * return new PersonServiceSQL(PersonRepository);
     * }
     */

    /**
     * Eğer "db.enabled" false ise veya tanımlanmamışsa PersonServiceDummy bean
     * olarak tanımlanır.
     * Bu yapılandırma, test ve geliştirme amaçlı dummy verilerle çalışan service
     * sağlar.
     * 
     * @return PersonServiceDummy nesnesi.
     */
    /**
     * @Bean
     * @ConditionalOnProperty(name = "db.enabled", havingValue = "false",
     *                             matchIfMissing = true)
     *                             PersonServiceDummy PersonServiceDummy() {
     *                             return new PersonServiceDummy();
     *                             }
     */

}
