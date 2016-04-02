package org.sbezgin.p2016.service.transformer.impl;

import org.apache.commons.collections.CollectionUtils;
import org.sbezgin.p2016.db.dto.PermissionDTO;
import org.sbezgin.p2016.db.dto.file.AbstractFileDTO;
import org.sbezgin.p2016.db.entity.Permission;
import org.sbezgin.p2016.db.entity.file.AbstractFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractFileTransformer {

    protected abstract AbstractFileDTO getAbstractDTOInstance();

    protected abstract AbstractFile getAbstractEntityInstance();

    protected AbstractFileDTO transformFileEntityToDTO(AbstractFile obj) {
        AbstractFileDTO dto = getAbstractDTOInstance();
        dto.setId(obj.getId());
        dto.setName(obj.getName());
        dto.setPath(obj.getPath());
        dto.setParentId(obj.getParentId());
        dto.setIdPath(obj.getIdPath());
        dto.setCreateDate(obj.getCreateDate());
        dto.setUpdateDate(obj.getUpdateDate());

        List<Permission> permissions = obj.getPermissions();
        if (CollectionUtils.isNotEmpty(permissions)) {
            List<PermissionDTO> permissionDTOs = new ArrayList<>(permissions.size());
            for (Permission permission : permissions) {
                PermissionDTO permissionDTO = new PermissionDTO();
                permissionDTO.setId(permission.getId());
                permissionDTO.setUserID(permission.getUserID());
                permissionDTO.setFileDTO(dto);

                permissionDTO.setDelete(permission.getDel());
                permissionDTO.setWrite(permission.getWrite());
                permissionDTO.setRead(permission.getRead());

                permissionDTOs.add(permissionDTO);
            }

            dto.setPermissions(permissionDTOs);
        }
        return dto;
    }


    protected AbstractFile transformFileDTOToEntity(AbstractFileDTO obj) {
        AbstractFile file = getAbstractEntityInstance();
        copyFileFieldsToEntity(obj, file);
        return file;
    }

    protected void copyFileFieldsToEntity(AbstractFileDTO src, AbstractFile dest) {
        dest.setId(src.getId());
        dest.setName(src.getName());
        dest.setPath(src.getPath());
        dest.setParentId(src.getParentId());
        dest.setIdPath(src.getIdPath());
        dest.setCreateDate(src.getCreateDate());
        dest.setUpdateDate(src.getUpdateDate());

        if (CollectionUtils.isNotEmpty(src.getPermissions())){
            List<Permission> entityPermissions = dest.getPermissions();
            if (entityPermissions == null) {
                entityPermissions = new ArrayList<>();
                dest.setPermissions(entityPermissions);
            }
            if (CollectionUtils.isNotEmpty(entityPermissions)) {
                Map<Long, Permission> permissionMap = new HashMap<>(entityPermissions.size());
                for (Permission entityPermission : entityPermissions) {
                    permissionMap.put(entityPermission.getId(), entityPermission);
                }

                for (PermissionDTO permissionDTO : src.getPermissions()) {
                    Permission permission = permissionMap.get(permissionDTO.getId());
                    if (permission == null) {
                        permission = new Permission();
                    }
                    copyPermissionDTOToEntity(permissionDTO, permission, dest);
                    entityPermissions.add(permission);
                }
            } else {
                for (PermissionDTO permissionDTO : src.getPermissions()) {
                    Permission permission = new Permission();
                    copyPermissionDTOToEntity(permissionDTO, permission, dest);
                    entityPermissions.add(permission);
                }
            }
        }
    }

    //if it updates already existed permission, we don't need set id
    //if it updates new object, we also don't need set id
    public void copyPermissionDTOToEntity(PermissionDTO permissionDTO, Permission permission, AbstractFile abstractFile) {
        permission.setUserID(permissionDTO.getUserID());
        permission.setRead(permissionDTO.getRead());
        permission.setWrite(permissionDTO.getWrite());
        permission.setDel(permissionDTO.getDelete());
        permission.setAbstractFile(abstractFile);
    }
}
