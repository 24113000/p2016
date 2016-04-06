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

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.List;

@Component
@Path("/fileOperation")
public class FileOperationRest {

    @Autowired
    private FileService fileService;

    @GET
    @Path("/getFile")
    public Response getFile(long fileID) {
        AbstractFileDTO file = fileService.getFileByID(fileID);
        Object respObj = new FileResponseBuilderImpl(1L)
                .setStatus(ResponseBuilder.SUCCESS)
                .setDataObject(file)
                .buildResponse();
        return Response.ok(respObj).build();

    }

    @GET
    @Path("/getFolderChildren")
    public Response getFolderChildren(long folderID, int start, int end){
        List<AbstractFileDTO> children = fileService.getChildren(folderID, start, end);
        Object respObj = new FileResponseBuilderImpl(1L)
                .setStatus(ResponseBuilder.SUCCESS)
                .setDataObject(children)
                .buildResponse();
        return Response.ok(respObj).build();
    }

    @GET
    @Path("/getFileContent")
    public Response getFileContent(long textFileID) {
        TextFileDTO textFile = fileService.getFullTextFile(textFileID);
        Object respObj = new FileResponseBuilderImpl(1L)
                .setStatus(ResponseBuilder.SUCCESS)
                .setDataObject(textFile)
                .buildResponse();
        return Response.ok(respObj).build();
    }

    @POST
    @Path("/saveFileContent")
    public Response saveFileContent(long fileID, String fileContent) {
        fileService.saveTextFileContent(fileID, fileContent);
        return Response.ok().build();
    }

    @POST
    @Path("/createFolder")
    public Response createFolder(String name, long parentID) {

        FolderDTO newFolder = new FolderDTO();
        newFolder.setName(name);
        newFolder.setParentId(parentID);
        fileService.saveFile(newFolder);

        return Response.ok().build();
    }

    @POST
    @Path("/createFile")
    public Response createFile(String fileName, String type, long parentID) {
        TextFileDTO newFile = new TextFileDTO();
        newFile.setType(FileType.valueOf(type));
        newFile.setName(fileName);
        newFile.setParentId(parentID);

        fileService.saveFile(newFile);
        return Response.ok().build();
    }

    @GET
    @Path("/deleteFile")
    public Response deleteFile(long fileID) {
        fileService.deleteFile(fileID, false);
        return Response.ok().build();
    }

    @POST
    @Path("/renameFile")
    public Response renameFile(long fileID, String newName) {
        AbstractFileDTO fileByID = fileService.getFileByID(fileID);
        fileByID.setName(newName);
        fileService.saveFile(fileByID);
        return Response.ok().build();
    }

    @POST
    @Path("/setPermission")
    public Response setPermission(long fileID, String userID, boolean read, boolean write, boolean del) {
        return Response.ok().build();
    }
}
