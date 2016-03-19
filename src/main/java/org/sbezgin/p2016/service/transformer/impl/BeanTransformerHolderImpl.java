package org.sbezgin.p2016.service.transformer.impl;

import org.sbezgin.p2016.service.transformer.BeanTransformer;
import org.sbezgin.p2016.service.transformer.BeanTransformerHolder;

import java.util.Map;

public class BeanTransformerHolderImpl implements BeanTransformerHolder {
    private Map<String, BeanTransformer> mappedTransformers;
    private BeanTransformer generalTransformer;

    @Override
    public BeanTransformer getTransformer(String className) {
        BeanTransformer beanTransformer = mappedTransformers.get(className);
        if (beanTransformer == null) {
            return generalTransformer;
        }
        return beanTransformer;
    }

    public Map<String, BeanTransformer> getMappedTransformers() {
        return mappedTransformers;
    }

    public void setMappedTransformers(Map<String, BeanTransformer> mappedTransformers) {
        this.mappedTransformers = mappedTransformers;
    }

    public BeanTransformer getGeneralTransformer() {
        return generalTransformer;
    }

    public void setGeneralTransformer(BeanTransformer generalTransformer) {
        this.generalTransformer = generalTransformer;
    }
}
