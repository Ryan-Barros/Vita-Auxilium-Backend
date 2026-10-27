package com.vitaauxilium.vitaauxilium.mapper;

import com.vitaauxilium.vitaauxilium.dto.request.ReminderRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.request.ReminderUpdateDTO;
import com.vitaauxilium.vitaauxilium.dto.response.ReminderResponseDTO;
import com.vitaauxilium.vitaauxilium.models.Reminder;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ReminderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "environment", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "completed", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    Reminder toEntity(ReminderRequestDTO dto);

    @Mapping(target = "environmentId", source = "environment.id")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.nameCategoria")
    ReminderResponseDTO toResponseDTO(Reminder reminder);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "environment", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    void updateFromDTO(ReminderUpdateDTO dto, @MappingTarget Reminder reminder);

    List<ReminderResponseDTO> toResponseDTOList(List<Reminder> reminders);
}
