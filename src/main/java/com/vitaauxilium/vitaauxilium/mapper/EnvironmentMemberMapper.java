package com.vitaauxilium.vitaauxilium.mapper;

import com.vitaauxilium.vitaauxilium.dto.response.EnvironmentMemberResponseDTO;
import com.vitaauxilium.vitaauxilium.models.EnvironmentMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EnvironmentMemberMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "environmentId", source = "environment.id")
    EnvironmentMemberResponseDTO toResponseDTO(EnvironmentMember entity);

    List<EnvironmentMemberResponseDTO> toResponseDTOList(List<EnvironmentMember> entities);
}
