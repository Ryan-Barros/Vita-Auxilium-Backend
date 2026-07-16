package com.vitaauxilium.vitaauxilium.services;

import com.vitaauxilium.vitaauxilium.dto.request.EnvironmentRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.request.EnvironmentUpdateDTO;
import com.vitaauxilium.vitaauxilium.dto.response.EnvironmentResponseDTO;
import com.vitaauxilium.vitaauxilium.exception.AccessDeniedException;
import com.vitaauxilium.vitaauxilium.mapper.EnvironmentMapper;
import com.vitaauxilium.vitaauxilium.models.Environment;
import com.vitaauxilium.vitaauxilium.models.EnvironmentMember;
import com.vitaauxilium.vitaauxilium.models.Profile;
import com.vitaauxilium.vitaauxilium.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor =  Exception.class)
public class EnvironmentMemberFacadeService {
    private final EnvironmentService environmentService;
    private final EnvironmentMapper environmentMapper;

    public EnvironmentResponseDTO createWithRelation(User owner, EnvironmentRequestDTO dto){
        if(owner.getUserProfile() != Profile.FAMILY){
            throw new AccessDeniedException("Precisa ser um familiar para criar um ambiente!");
        }
        Environment env = environmentMapper.toEntity(dto);

        EnvironmentMember member = new EnvironmentMember();
        member.setUser(owner);

        env.addMember(member);

        Environment savedEnv = environmentService.create(env);

        return environmentMapper.toResponseDTO(savedEnv);
    }

    public EnvironmentResponseDTO updateWithRelation(User owner, EnvironmentUpdateDTO dto, UUID envId){
        if(owner.getUserProfile() != Profile.FAMILY){
            throw new AccessDeniedException("Precisa ser um familiar para editar um ambiente!");
        }
        Environment existingEnv = environmentService.findById(envId);

        Environment changesFromDto = environmentMapper.toEntityFromUpdateDTO(dto);

        Environment updatedEnv = environmentService.update(existingEnv, changesFromDto);

        return environmentMapper.toResponseDTO(updatedEnv);
    }
}
