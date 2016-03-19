package org.sbezgin.p2016.service.transformer.impl;

import org.sbezgin.p2016.db.dto.file.AbstractFileDTO;
import org.sbezgin.p2016.db.entity.file.AbstractFile;

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
    }
}
