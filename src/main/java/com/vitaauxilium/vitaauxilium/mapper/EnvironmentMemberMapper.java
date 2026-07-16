package com.vitaauxilium.vitaauxilium.mapper;

import com.vitaauxilium.vitaauxilium.dto.response.EnvironmentMemberResponseDTO;
import com.vitaauxilium.vitaauxilium.models.EnvironmentMember;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnvironmentMemberMapper {
    EnvironmentMemberResponseDTO toResponseDTO(EnvironmentMember entity);

    List<EnvironmentMemberResponseDTO> toResponseDTOList(List<EnvironmentMember> entities);
}
