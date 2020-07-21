package com.btireland.talos.mygroup.myproject.repository;

import com.btireland.talos.mygroup.myproject.config.DatabaseConfiguration;
import com.btireland.talos.mygroup.myproject.domain.ExampleOrder;
import com.btireland.talos.mygroup.myproject.tag.IntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.stream.IntStream;

// Standard Spring-boot annotation to scan entity classes and configure repositories
@DataJpaTest

// because we use a custom repository base class, we need to load it as it is not loaded by default
// by @DataJpaTest
@Import(DatabaseConfiguration.class)
@DisplayName("ExampleOrderRepository")
@IntegrationTest

// We define a test profile so that we can override the default datasource configured by Spring
// This way, we can use a H2 database with a Hikari pool in front and set the auto-commit property to false
@ActiveProfiles("test")

// We won’t use the default H2 database configuration provided by Spring as we want to
// use the one defined in the test profile
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ExampleOrderRepositoryTest {

    @Autowired
    private ExampleOrderRepository repository;

    @Test
    @DisplayName("Check that we can find an order by its external reference")
    void findByExternalReference() {

        ExampleOrder expected = ExampleOrder.builder()
                .externalReference("myRef1").type(ExampleOrder.Type.PFIB)
                .build();

        // insert 2 example orders in the DB
        repository.save(expected);
        repository.save(ExampleOrder.builder().externalReference("myRef2").type(ExampleOrder.Type.CFIB).build());

        // test that the example order we find by externalReference is the one we expect
        Assertions.assertThat(repository.findByExternalReference("myRef1")).isEqualToIgnoringGivenFields(expected, "id");
    }

    @Test
    @DisplayName("Check that it’s no pb if there is no voip resource for a given assetId")
    void findByExternalReferenceWhenNotFound() {

        repository.save(ExampleOrder.builder().externalReference("myRef1").type(ExampleOrder.Type.PFIB).build());
        repository.save(ExampleOrder.builder().externalReference("myRef2").type(ExampleOrder.Type.CFIB).build());

        Assertions.assertThat(repository.findByExternalReference("myRef3")).isNull();
    }

    @Test
    @DisplayName("Check that we can find ExampleOrder using a simple RSQL query on one field only")
    void findAllByRsqlQueryOneField() {

        ExampleOrder expected = ExampleOrder.builder()
                .externalReference("myRef2").type(ExampleOrder.Type.CFIB).version(0)
                .build();

        repository.save(ExampleOrder.builder().externalReference("myRef1").type(ExampleOrder.Type.PFIB).build());
        repository.save(ExampleOrder.builder().externalReference("myRef2").type(ExampleOrder.Type.CFIB).build());
        repository.save(ExampleOrder.builder().externalReference("myRef3").type(ExampleOrder.Type.CFIB).build());

        Assertions.assertThat(repository.findAllByRsqlQuery("externalReference=='myRef2'")).usingElementComparatorIgnoringFields("id", "createdAt").containsExactly(expected);
    }

    @Test
    @DisplayName("Check that we can find ExampleOrder using a simple RSQL query on one field with like comparator")
    void findAllByRsqlQueryOneFieldLikeComparator() {

        ExampleOrder expected1 = ExampleOrder.builder()
                .externalReference("myRef1").type(ExampleOrder.Type.PFIB).version(0)
                .build();
        ExampleOrder expected2 = ExampleOrder.builder()
                .externalReference("myRef1-1").type(ExampleOrder.Type.CFIB).version(0)
                .build();

        repository.save(ExampleOrder.builder().externalReference("myRef1").type(ExampleOrder.Type.PFIB).build());
        repository.save(ExampleOrder.builder().externalReference("myRef1-1").type(ExampleOrder.Type.CFIB).build());
        repository.save(ExampleOrder.builder().externalReference("myRef3").type(ExampleOrder.Type.CFIB).build());

        Assertions.assertThat(repository.findAllByRsqlQuery("externalReference=='myRef1*'")).usingElementComparatorIgnoringFields("id", "createdAt").containsExactly(expected1, expected2);
    }

    @Test
    @DisplayName("Test the pagination feature provided by Spring data")
    void findAllAndPaginate() {

        IntStream.of(1,2,3,4,5,6).forEach(
                i -> repository.save(ExampleOrder.builder().externalReference("myRef"+i).type(ExampleOrder.Type.PFIB).build())
        );

        // I ask for the second page (start at 0) of size 2.
        PageRequest pageRequest = PageRequest.of(1,2, Sort.by("externalReference").descending());
        Page<ExampleOrder> page = repository.findAll(pageRequest);

        // I check only the externalReference value, don’t care about the other field, so I extract it from each object in the result
        Assertions.assertThat(page).extracting(ExampleOrder::getExternalReference).containsExactly("myRef4", "myRef3");
    }

    @Test
    @DisplayName("Test the pagination feature provided by Spring data when using a RSQL query")
    void findAllByRsqlQueryAndPaginate() {

        IntStream.of(1,2,3).forEach(
                i -> repository.save(ExampleOrder.builder().externalReference("myRef"+i).type(ExampleOrder.Type.PFIB).build())
        );
        IntStream.of(4,5,6).forEach(
                i -> repository.save(ExampleOrder.builder().externalReference("myRef"+i).type(ExampleOrder.Type.CFIB).build())
        );

        // I ask for the second page (start at 0) of size 2 with a criteria type=='CFIB' that means it will select the 6th element I just created
        PageRequest pageRequest = PageRequest.of(1,2, Sort.by("externalReference").ascending());
        Page<ExampleOrder> page = repository.findAllByRsqlQuery("type=='CFIB'", pageRequest);

        // I check only the externalReference value, don’t care about the other field, so I extract it from each object in the result
        Assertions.assertThat(page).extracting(ExampleOrder::getExternalReference).containsExactly("myRef6");
    }
}