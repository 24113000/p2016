package org.sbezgin.p2016.rest;

import org.sbezgin.p2016.common.FileType;
import org.sbezgin.p2016.db.dto.file.AbstractFileDTO;
import org.sbezgin.p2016.db.dto.file.FolderDTO;
import org.sbezgin.p2016.db.dto.file.TextFileDTO;
import org.sbezgin.p2016.rest.response.ResponseBuilder;
import org.sbezgin.p2016.rest.response.impl.FileResponseBuilderImpl;
import org.sbezgin.p2016.service.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Component
@Path("/fileOperation")
public class FileOperationRest {

    @Autowired
    private FileService fileService;

    @GET
    @Path("/getFile/{fileID}")
    public Response getFile(@PathParam("fileID") long fileID) {
        AbstractFileDTO file = fileService.getFileByID(fileID);
        Object respObj = new FileResponseBuilderImpl(1L)
                .setStatus(ResponseBuilder.SUCCESS)
                .setDataObject(file)
                .buildResponse();
        return Response.ok(respObj).build();
    }

    @GET
    @Path("/getFolderChildren/{folderID}/{start}/{end}")
    public Response getFolderChildren(
            @PathParam("folderID") long folderID,
            @PathParam("start") int start,
            @PathParam("end") int end
    ){
        List<AbstractFileDTO> children = fileService.getChildren(folderID, start, end);
        Object respObj = new FileResponseBuilderImpl(1L)
                .setStatus(ResponseBuilder.SUCCESS)
                .setDataObject(children)
                .buildResponse();
        return Response.ok(respObj).build();
    }

    @GET
    @Path("/getFileContent/{fileID}")
    public Response getFileContent(@PathParam("fileID") long textFileID) {
        TextFileDTO textFile = fileService.getFullTextFile(textFileID);
        Object respObj = new FileResponseBuilderImpl(1L)
                .setStatus(ResponseBuilder.SUCCESS)
                .setDataObject(textFile)
                .buildResponse();
        return Response.ok(respObj).build();
    }

    @POST
    @Path("/saveFileContent")
    public Response saveFileContent(
            @FormParam("fileID") long fileID,
            @FormParam("fileContent") String fileContent
    ) {
        fileService.saveTextFileContent(fileID, fileContent);
        return Response.ok().build();
    }

    @POST
    @Path("/createFolder")
    public Response createFolder(
            @FormParam("name") String name,
            @FormParam("parentID") long parentID
    ) {
        FolderDTO newFolder = new FolderDTO();
        newFolder.setName(name);
        newFolder.setParentId(parentID);
        fileService.saveFile(newFolder);

        return Response.ok().build();
    }

    @POST
    @Path("/createFile")
    public Response createFile(
            @FormParam("fileName") String fileName,
            @FormParam("type") String type,
            @FormParam("parentID") long parentID
    ) {
        TextFileDTO newFile = new TextFileDTO();
        newFile.setType(FileType.valueOf(type));
        newFile.setName(fileName);
        newFile.setParentId(parentID);

        fileService.saveFile(newFile);
        return Response.ok().build();
    }

    @GET
    @Path("/deleteFile/{fileID}")
    public Response deleteFile(@PathParam("fileID") long fileID) {
        fileService.deleteFile(fileID, false);
        return Response.ok().build();
    }

    @POST
    @Path("/renameFile")
    public Response renameFile(
            @FormParam("fileID") long fileID,
            @FormParam("newName") String newName
    ) {
        fileService.renameFile(fileID, newName);
        return Response.ok().build();
    }

    @POST
    @Path("/setPermission")
    public Response setPermission(
            @FormParam("fileID") long fileID,
            @FormParam("userID") String userID,
            @FormParam("read") boolean read,
            @FormParam("write") boolean write,
            @FormParam("del") boolean del
    ) {
        return Response.ok().build();
    }

    public FileService getFileService() {
        return fileService;
    }

    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }
}
