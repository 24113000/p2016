package org.sbezgin.p2016.services;

public interface BeanTransformer {

    Object transformEntityToDTO(Object obj);

    Object transformDTOToEntity(Object obj);

    void copyFields(Object src, Object dest);
}
