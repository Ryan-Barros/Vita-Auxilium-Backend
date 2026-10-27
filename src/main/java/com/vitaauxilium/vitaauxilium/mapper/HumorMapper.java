package com.vitaauxilium.vitaauxilium.mapper;

import com.vitaauxilium.vitaauxilium.dto.request.HumorRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.response.HumorResponseDTO;
import com.vitaauxilium.vitaauxilium.models.Humor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface HumorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "environment", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "activity", ignore = true)
    @Mapping(target = "registrationTime", ignore = true)
    @Mapping(target = "intensity", ignore = true)
    Humor toEntity(HumorRequestDTO dto);

    @Mapping(target = "environmentId", source = "environment.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.name")
    @Mapping(target = "activityId", source = "activity.id")
    @Mapping(target = "humorLabel", source = "humor.description")
    HumorResponseDTO toResponseDTO(Humor entity);

    List<HumorResponseDTO> toResponseDTOList(List<Humor> entities);
}
