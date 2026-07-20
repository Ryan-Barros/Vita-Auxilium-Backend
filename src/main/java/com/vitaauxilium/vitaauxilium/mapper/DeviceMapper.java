package com.vitaauxilium.vitaauxilium.mapper;

import com.vitaauxilium.vitaauxilium.dto.request.DeviceRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.response.DeviceResponseDTO;
import com.vitaauxilium.vitaauxilium.models.Device;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface DeviceMapper {

    @Mapping(target = "environmentId", source = "environment.id")
    DeviceResponseDTO toResponseDTO(Device device);

    List<DeviceResponseDTO> toResponseDTOList(List<Device> devices);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tokenHash", source = "token")
    @Mapping(target = "data", ignore = true)
    @Mapping(target = "environment", ignore = true)
    Device toEntity(DeviceRequestDTO dto);
}
