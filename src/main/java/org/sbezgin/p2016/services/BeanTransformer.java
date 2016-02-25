package org.sbezgin.p2016.services;

public interface BeanTransformer<D, E> {

    D transformEntityToDTO(E obj);

    E transformDTOToEntity(D obj);

    void copyFieldsToEntity(D src, E dest);
}
