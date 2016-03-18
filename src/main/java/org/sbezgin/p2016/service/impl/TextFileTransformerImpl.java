package org.sbezgin.p2016.service.impl;

import org.sbezgin.p2016.db.dto.file.AbstractFileDTO;
import org.sbezgin.p2016.db.dto.file.TextFileContentDTO;
import org.sbezgin.p2016.db.dto.file.TextFileDTO;
import org.sbezgin.p2016.db.entity.file.AbstractFile;
import org.sbezgin.p2016.db.entity.file.FileContent;
import org.sbezgin.p2016.db.entity.file.TextFile;
import org.sbezgin.p2016.service.BeanTransformer;

import java.nio.charset.Charset;

public class TextFileTransformerImpl extends AbstractFileTransformer implements BeanTransformer<TextFileDTO, TextFile> {

    @Override
    public TextFileDTO transformEntityToDTO(TextFile obj) {
        return transformEntityToDTO(obj, false);
    }


    public TextFileDTO transformEntityToDTO(TextFile obj, boolean isFull) {

        TextFileDTO textFileDTO = (TextFileDTO) transformFileEntityToDTO(obj);
        textFileDTO.setType(obj.getType());

        if (isFull && obj.getFileContent() != null) {
            FileContent fileContent = obj.getFileContent();

            TextFileContentDTO textFileContentDTO = new TextFileContentDTO();
            textFileContentDTO.setId(fileContent.getId());
            textFileContentDTO.setData(new String(fileContent.getData(), Charset.defaultCharset()));

            textFileDTO.setFileContent(textFileContentDTO);
        }

        return textFileDTO;
    }

    @Override
    public TextFile transformDTOToEntity(TextFileDTO obj) {
        TextFile textFile = (TextFile) transformFileDTOToEntity(obj);

        copyFieldsToEntity(obj, textFile);

        return textFile;
    }

    @Override
    public void copyFieldsToEntity(TextFileDTO src, TextFile dest) {
        TextFileContentDTO textFileContentDTO = src.getFileContent();
        dest.setType(src.getType());
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

    @Override
    protected AbstractFileDTO getAbstractDTOInstance() {
        return new TextFileDTO();
    }

    @Override
    protected AbstractFile getAbstractEntityInstance() {
        return new TextFile();
    }
}
