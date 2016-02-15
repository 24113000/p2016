package org.sbezgin.p2016.services.impl;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.dozer.DozerBeanMapper;
import org.sbezgin.p2016.common.P2016Exception;
import org.sbezgin.p2016.services.BeanTransformer;

import java.util.Map;

public class BeanTransformerImpl implements BeanTransformer {

    private DozerBeanMapper dozerBeanMapper;

    private BidiMap beanMap;

    @Override
    public Object transformEntityToDTO(Object obj) {
        String dtoClassName = (String) beanMap.get(obj.getClass().getCanonicalName());
        return transForm(obj, dtoClassName);
    }

    private Object transForm(Object obj, String className)  {
        try {
            Class<?> aClass = Class.forName(className);
            Object destinationEntity = aClass.newInstance();

            dozerBeanMapper.map(obj, destinationEntity);

            return destinationEntity;
        } catch (Exception e) {
            throw new P2016Exception("Cannot transform entity: " + obj.getClass().getCanonicalName(), e);
        }
    }

    @Override
    public Object transformDTOToEntity(Object obj) {
        String entityClassName = (String) beanMap.getKey(obj.getClass().getCanonicalName());
        return transForm(obj, entityClassName);
    }

    @Override
    public void copyFields(Object src, Object dest) {
        dozerBeanMapper.map(src, dest);
    }

    public DozerBeanMapper getDozerBeanMapper() {
        return dozerBeanMapper;
    }

    public void setDozerBeanMapper(DozerBeanMapper dozerBeanMapper) {
        this.dozerBeanMapper = dozerBeanMapper;
    }

    public Map<String, String> getBeanMap() {
        return beanMap;
    }

    public void setBeanMap(Map<String, String> beanMap) {
        this.beanMap = new DualHashBidiMap(beanMap);
    }
}
