package com.vitaauxilium.vitaauxilium.services;

import com.vitaauxilium.vitaauxilium.models.EnvironmentMember;
import com.vitaauxilium.vitaauxilium.repositories.EnvironmentMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class EnvironmentMemberService {

    private final EnvironmentMemberRepository environmentMemberRepository;

    @Transactional(readOnly = true)
    public List<EnvironmentMember> findAll() {
        return environmentMemberRepository.findAll();
    }

    @Transactional(readOnly = true)
    public EnvironmentMember findById(UUID id) {
        return environmentMemberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Membro não encontrado!"));
    }

    public EnvironmentMember create(EnvironmentMember environmentMember) {
        return environmentMemberRepository.save(environmentMember);
    }

    public void delete(UUID id) {
        EnvironmentMember environmentMember = environmentMemberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Membro não encontrado!"));
        environmentMemberRepository.delete(environmentMember);
    }
}
