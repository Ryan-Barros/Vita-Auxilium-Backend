package com.vitaauxilium.vitaauxilium.mapper;

import com.vitaauxilium.vitaauxilium.dto.request.DeviceRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.response.DeviceResponseDTO;
import com.vitaauxilium.vitaauxilium.models.Device;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeviceMapper {
    DeviceResponseDTO toResponseDTO(Device device);

    List<DeviceResponseDTO> toResponseDTOList(List<Device> devices);

    Device toEntity(DeviceRequestDTO dto);
}
