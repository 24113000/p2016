package org.sbezgin.p2016.rest.response.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import org.sbezgin.p2016.common.P2016Exception;
import org.sbezgin.p2016.rest.response.ResponseBuilder;

import java.util.List;

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
        SimpleModule module = new SimpleModule();
        module.addSerializer(List.class, new PermissionSerializer(userID));
        mapper.registerModule(module);
        try {
            return mapper.writeValueAsString(resultContainer);
        } catch (JsonProcessingException e) {
            throw new P2016Exception(e);
        }
    }
}
