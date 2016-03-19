package org.sbezgin.p2016.service.transformer;

import org.sbezgin.p2016.service.transformer.BeanTransformer;

public interface BeanTransformerHolder {
    BeanTransformer getTransformer(String className);
}
