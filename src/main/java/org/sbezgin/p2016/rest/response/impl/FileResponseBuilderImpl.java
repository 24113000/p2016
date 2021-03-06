package org.sbezgin.p2016.rest.response.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sbezgin.p2016.common.P2016Exception;
import org.sbezgin.p2016.rest.response.ResponseBuilder;

public class FileResponseBuilderImpl implements ResponseBuilder {

    private ResultContainer resultContainer = new ResultContainer();
    private Long userID;

    public FileResponseBuilderImpl(Long userID) {
        this.userID = userID;
    }

    @Override
    public ResponseBuilder setStatus(String status) {
        resultContainer.setStatus(status);
        return this;
    }

    @Override
    public ResponseBuilder setDataObject(Object obj) {
        resultContainer.setData(obj);
        return this;
    }

    @Override
    public Object buildResponse() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializerFactory(new PermissionSerializerFactory(userID));
        try {
            return mapper.writeValueAsString(resultContainer);
        } catch (JsonProcessingException e) {
            throw new P2016Exception(e);
        }
    }
}
