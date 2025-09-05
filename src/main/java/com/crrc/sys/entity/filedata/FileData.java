package com.crrc.sys.entity.filedata;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import com.crrc.sys.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * <p>
 * 文件数据表
 * </p>
 *
 * @author lijiangtao
 * @since 2025-06-16 11:08:30
 */
@TableName("sys_file_data")
@Schema(description = "文件数据表")
public class FileData extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1934447969308438528L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableLogic(value = "0")
    @Schema(description = "删除", example = "false")
    private Integer deleted;

    @Schema(description = "MinIO存储UUID", example = "")
    private String fileId;

    @Schema(description = "文件真实名称", example = "")
    private String actualName;

    @Schema(description = "MinIO完整存储路径", example = "")
    private String filePath;

    @Schema(description = "文件MIME类型", example = "")
    private String mimeType;

    @Schema(description = "文件大小（字节）", example = "")
    private Long fileSize;
    
    public FileData() {
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getDeleted() {
		return deleted;
	}

	public void setDeleted(Integer deleted) {
		this.deleted = deleted;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getActualName() {
		return actualName;
	}

	public void setActualName(String actualName) {
		this.actualName = actualName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	@Override
	public String toString() {
		return "FileData{" +
				"id=" + id +
				", deleted=" + deleted +
				", fileId='" + fileId + '\'' +
				", actualName='" + actualName + '\'' +
				", filePath='" + filePath + '\'' +
				", mimeType='" + mimeType + '\'' +
				", fileSize=" + fileSize +
				'}';
	}
}
