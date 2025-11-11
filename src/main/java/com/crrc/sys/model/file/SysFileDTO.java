package com.crrc.sys.model.file;

/**
 * 文件实体
 *
 * @author 谭永强
 * @date 2022-11-30
 */
public class SysFileDTO {

    /**
     * 附件ID
     */
    private Long fileId;
    /**
     * 附件头ID
     */
    private Long fileHeaderId;
    /**
     * 附件原名称
     */
    private String oldFileName;
    /**
     * 组名称
     */
    private String groupName;
    /**
     * 文件地址
     */
    private String filePath;
    /**
     * 访问地址
     */
    private String url;

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public Long getFileHeaderId() {
        return fileHeaderId;
    }

    public void setFileHeaderId(Long fileHeaderId) {
        this.fileHeaderId = fileHeaderId;
    }

    public String getOldFileName() {
        return oldFileName;
    }

    public void setOldFileName(String oldFileName) {
        this.oldFileName = oldFileName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
