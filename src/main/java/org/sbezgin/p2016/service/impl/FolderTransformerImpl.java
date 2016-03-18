package org.sbezgin.p2016.service.impl;

import org.sbezgin.p2016.db.dto.file.AbstractFileDTO;
import org.sbezgin.p2016.db.dto.file.FolderDTO;
import org.sbezgin.p2016.db.entity.file.AbstractFile;
import org.sbezgin.p2016.db.entity.file.Folder;
import org.sbezgin.p2016.service.BeanTransformer;

public class FolderTransformerImpl extends AbstractFileTransformer implements BeanTransformer<FolderDTO, Folder> {
    @Override
    protected AbstractFileDTO getAbstractDTOInstance() {
        return new FolderDTO();
    }

    @Override
    protected AbstractFile getAbstractEntityInstance() {
        return new Folder();
    }

    @Override
    public FolderDTO transformEntityToDTO(Folder obj) {
        return (FolderDTO) transformFileEntityToDTO(obj);
    }

    @Override
    public Folder transformDTOToEntity(FolderDTO obj) {
        return (Folder) transformFileDTOToEntity(obj);
    }

    @Override
    public void copyFieldsToEntity(FolderDTO src, Folder dest) {
        copyFileFieldsToEntity(src, dest);
    }
}
