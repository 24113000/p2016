package org.sbezgin.p2016.rest.response;

public interface ResponseBuilder {

    String SUCCESS = "success";

    String ERROR = "error";

    ResponseBuilder setStatus(String status);

    ResponseBuilder setDataObject(Object obj);

    Object buildResponse();
}
