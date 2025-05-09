package com.project.bookviews.services.role;

import com.project.bookviews.models.Role;
import com.project.bookviews.repositories.IRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService{
    private final IRoleRepository IRoleRepository;
    @Override
    public List<Role> getAllRoles() {
        return IRoleRepository.findAll();
    }
}
