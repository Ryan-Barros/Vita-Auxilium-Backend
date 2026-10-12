package com.vitaauxilium.vitaauxilium.mapper;

import com.vitaauxilium.vitaauxilium.dto.request.ActivityRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.response.ActivityResponseDTO;
import com.vitaauxilium.vitaauxilium.models.Activity;
import org.mapstruct.*;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ActivityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "environmentId", ignore = true)
    @Mapping(target = "category", ignore = true)
    Activity toEntity(ActivityRequestDTO dto);

    ActivityResponseDTO toResponseDTO(Activity activity);
}
