package com.vitaauxilium.vitaauxilium.mapper;

import com.vitaauxilium.vitaauxilium.dto.request.ActivityRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.response.ActivityResponseDTO;
import com.vitaauxilium.vitaauxilium.models.Activity;
import org.mapstruct.*;
import org.mapstruct.ReportingPolicy;

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
    ActivityResponseDTO toResponseDTO(Activity activity);

    List<ActivityResponseDTO> toResponseDTOList(List<Activity> activities);
}
