package com.btireland.talos.mygroup.myproject.facade;

import com.btireland.talos.mygroup.myproject.domain.ExampleOrder;
import com.btireland.talos.mygroup.myproject.domain.ExampleOrderItem;
import com.btireland.talos.mygroup.myproject.dto.ExampleOrderDTO;
import com.btireland.talos.mygroup.myproject.dto.ExampleOrderItemDTO;
import com.btireland.talos.mygroup.myproject.tag.UnitTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UnitTest
class ExampleOrderMapperTest {

    private ExampleOrderMapper mapper;

    @BeforeEach
    public void setUp(){
        mapper = Mappers.getMapper(ExampleOrderMapper.class);
    }

    @Test
    void toExampleOrderDTO() {
        ExampleOrder order = ExampleOrder.builder()
                .id(1L).version(0).externalReference("extRef").internalComment("my comment").createdAt(LocalDateTime.parse("2020-02-03T10:43:35"))
                .items(Arrays.asList(
                        ExampleOrderItem.builder().id(1L).version(0).productName("FTTH").quantity(2).build(),
                        ExampleOrderItem.builder().id(2L).version(0).productName("FTTC").quantity(1).build()
                ))
                .build();

        ExampleOrderDTO expected = ExampleOrderDTO.builder()
                .id(1L).externalReference("extRef").createdAt(LocalDateTime.parse("2020-02-03T10:43:35"))
                .items(Arrays.asList(
                        ExampleOrderItemDTO.builder().id(1L).productName("FTTH").quantity(2).build(),
                        ExampleOrderItemDTO.builder().id(2L).productName("FTTC").quantity(1).build()
                ))
                .build();

        ExampleOrderDTO actual = mapper.toExampleOrderDTO(order);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void toExampleOrder() {

        ExampleOrderDTO orderDTO = ExampleOrderDTO.builder()
                .id(1L).externalReference("extRef").createdAt(LocalDateTime.parse("2020-02-03T10:43:35"))
                .items(Arrays.asList(
                        ExampleOrderItemDTO.builder().id(1L).productName("FTTH").quantity(2).build(),
                        ExampleOrderItemDTO.builder().id(2L).productName("FTTC").quantity(1).build()
                ))
                .build();

        ExampleOrder expected = ExampleOrder.builder()
                .id(1L).externalReference("extRef").createdAt(LocalDateTime.parse("2020-02-03T10:43:35"))
                .items(Arrays.asList(
                        ExampleOrderItem.builder().id(1L).productName("FTTH").quantity(2).build(),
                        ExampleOrderItem.builder().id(2L).productName("FTTC").quantity(1).build()
                ))
                .build();

        ExampleOrder actual = mapper.toExampleOrder(orderDTO);
        Assertions.assertThat(actual).isEqualToIgnoringGivenFields(expected, "items");
        Assertions.assertThat(actual.getItems()).usingFieldByFieldElementComparator().containsExactly(expected.getItems().toArray(new ExampleOrderItem[]{}));
    }

    @Test
    @DisplayName("Check that DTO is copied to an previously existing entity, overriding some values but leaving untouched values present in the entity only")
    void toExampleOrderEntityAlreadyExist() {

        ExampleOrderDTO orderDTO = ExampleOrderDTO.builder()
                .id(1L).externalReference("extRef 2").createdAt(LocalDateTime.parse("2020-02-03T10:43:35"))
                .items(Arrays.asList(
                        ExampleOrderItemDTO.builder().id(1L).productName("FTTH").quantity(2).build(),
                        ExampleOrderItemDTO.builder().id(3L).productName("VOIP").quantity(1).build()
                ))
                .build();

        List<ExampleOrderItem> existingItems = new ArrayList<>();
        existingItems.add(ExampleOrderItem.builder().id(1L).productName("FTTH").quantity(2).build());
        existingItems.add(ExampleOrderItem.builder().id(2L).productName("FTTC").quantity(1).build());
        ExampleOrder existing = ExampleOrder.builder()
                .id(1L).externalReference("extRef").version(0).internalComment("my comment").createdAt(LocalDateTime.parse("2020-02-03T10:43:35"))
                .items(existingItems)
                .build();

        ExampleOrder expected = ExampleOrder.builder()
                .id(1L).externalReference("extRef 2").version(0).internalComment("my comment").createdAt(LocalDateTime.parse("2020-02-03T10:43:35"))
                .items(Arrays.asList(
                        ExampleOrderItem.builder().id(1L).productName("FTTH").quantity(2).build(),
                        ExampleOrderItem.builder().id(3L).productName("VOIP").quantity(1).build()
                ))
                .build();

        mapper.toExampleOrder(orderDTO, existing);
        Assertions.assertThat(existing).isEqualToIgnoringGivenFields(expected, "items");
        Assertions.assertThat(existing.getItems()).usingFieldByFieldElementComparator().containsExactly(expected.getItems().toArray(new ExampleOrderItem[]{}));
    }
}