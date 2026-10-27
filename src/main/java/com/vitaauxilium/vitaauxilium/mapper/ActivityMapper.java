package com.vitaauxilium.vitaauxilium.mapper;

import com.vitaauxilium.vitaauxilium.dto.request.ActivityRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.request.ActivityUpdateDTO;
import com.vitaauxilium.vitaauxilium.dto.response.ActivityResponseDTO;
import com.vitaauxilium.vitaauxilium.models.Activity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ActivityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "environment", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "active", ignore = true)
    Activity toEntity(ActivityRequestDTO dto);

    @Mapping(target = "environmentId", source = "environment.id")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.nameCategoria")
    ActivityResponseDTO toResponseDTO(Activity activity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "environment", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateFromDTO(ActivityUpdateDTO dto, @MappingTarget Activity activity);

    List<ActivityResponseDTO> toResponseDTOList(List<Activity> activities);
}
