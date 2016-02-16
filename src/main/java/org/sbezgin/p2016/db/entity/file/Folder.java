package org.sbezgin.p2016.db.entity.file;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "folder")
@PrimaryKeyJoinColumn(name="id")
public class Folder extends AbstractFile {
}
