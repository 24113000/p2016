package org.sbezgin.p2016.service.impl;

import org.sbezgin.p2016.db.dto.file.TextFileContentDTO;
import org.sbezgin.p2016.db.dto.file.TextFileDTO;
import org.sbezgin.p2016.db.entity.file.FileContent;
import org.sbezgin.p2016.db.entity.file.TextFile;
import org.sbezgin.p2016.service.BeanTransformer;

import java.nio.charset.Charset;

public class TextFileTransformerImpl implements BeanTransformer<TextFileDTO, TextFile> {

    @Override
    public TextFileDTO transformEntityToDTO(TextFile obj) {
        return transformEntityToDTO(obj, false);
    }

    public TextFileDTO transformEntityToDTO(TextFile obj, boolean isFull) {
        TextFileDTO textFileDTO = new TextFileDTO();
        textFileDTO.setId(obj.getId());
        textFileDTO.setName(obj.getName());
        textFileDTO.setType(obj.getType());
        textFileDTO.setPath(obj.getPath());
        textFileDTO.setParentId(obj.getParentId());
        textFileDTO.setIdPath(obj.getIdPath());

        if (isFull && obj.getFileContent() != null) {
            FileContent fileContent = obj.getFileContent();

            TextFileContentDTO textFileContentDTO = new TextFileContentDTO();
            textFileContentDTO.setId(fileContent.getId());
            textFileContentDTO.setData(new String(fileContent.getData(), Charset.defaultCharset())); //TODO add default charset to JVM

            textFileDTO.setFileContent(textFileContentDTO);
        }

        return textFileDTO;
    }

    @Override
    public TextFile transformDTOToEntity(TextFileDTO obj) {
        TextFile textFile = new TextFile();

        copyFieldsToEntity(obj, textFile);

        return textFile;
    }

    @Override
    public void copyFieldsToEntity(TextFileDTO src, TextFile dest) {
        dest.setId(src.getId());
        dest.setName(src.getName());
        dest.setType(src.getType());
        dest.setPath(src.getPath());
        dest.setParentId(src.getParentId());
        dest.setIdPath(src.getIdPath());

        TextFileContentDTO textFileContentDTO = src.getFileContent();
        if (textFileContentDTO != null && dest.getFileContent() == null) {
            FileContent textFileContent = new FileContent();
            textFileContent.setId(textFileContentDTO.getId());
            textFileContent.setFile(dest);

            String data = textFileContentDTO.getData();
            if (data != null) {
                textFileContent.setData(data.getBytes(Charset.defaultCharset()));
            }

            textFileContent.setFile(dest);
            dest.setFileContent(textFileContent);
        } else if (textFileContentDTO != null && dest.getFileContent() != null) {
            FileContent destFileContent = dest.getFileContent();
            String data = textFileContentDTO.getData();
            if (data != null) {
                destFileContent.setData(data.getBytes(Charset.defaultCharset()));
            }
        }
    }
}
