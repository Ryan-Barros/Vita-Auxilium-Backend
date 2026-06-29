package com.vitaauxilium.vitaauxilium.mapper;

import com.vitaauxilium.vitaauxilium.dto.request.EnvironmentRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.response.EnvironmentResponseDTO;
import com.vitaauxilium.vitaauxilium.models.Environment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnvironmentMapper {
    Environment toEntity(EnvironmentRequestDTO dto);

    EnvironmentResponseDTO toResponseDTO(Environment environment);

    List<EnvironmentResponseDTO> toResponseDTOList(List<Environment> environments);
}
