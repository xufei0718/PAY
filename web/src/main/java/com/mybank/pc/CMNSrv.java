package com.mybank.pc;

import cn.hutool.core.io.IoUtil;
import com.cybermkd.mongo.kit.MongoKit;
import com.jfinal.kit.LogKit;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mybank.pc.kits.ResKit;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.InputStream;

public class CMNSrv {
    private static final String mongoDb =ResKit.getConfig("mongodb.db");
    private static final String mongoCon =ResKit.getConfig("mongodb.con");
    /**
     * 保存文件服务
     * @param file 需要保存的文件对象
     * @param fileType 文件扩展名
     * @return 文件保存后的唯一标识
     */
    public static String saveFile(File file , String fileType ){
        String mongoID =null;
        try {
        MongoClient mongoClient=MongoKit.INSTANCE.getClient();
        MongoDatabase myDatabase = mongoClient.getDatabase(mongoDb);
        GridFSBucket gridFSBucket = GridFSBuckets.create(myDatabase,mongoCon);
        GridFSUploadOptions options = new GridFSUploadOptions()
                .metadata(new Document("fileType", fileType));
         mongoID= gridFSBucket.uploadFromStream(file.getName(),IoUtil.toStream(file),options).toString();

        LogKit.debug("保存文件mongoID:" + mongoID);

        } catch (Exception e) {
            LogKit.error("文件保存失败：" + e.getMessage());
        }


      return mongoID;
    }

    /**
     * 读取上传的文件
     * @param ufileID 文件保存时的唯一标识
     * @return mongo文件对象
     */
    public static MongoFileVO loadFile(String ufileID){

        MongoClient mongoClient=MongoKit.INSTANCE.getClient();
        MongoDatabase myDatabase = mongoClient.getDatabase(mongoDb);
        GridFSBucket gridFSBucket = GridFSBuckets.create(myDatabase,mongoCon);
        MongoFileVO mongoFileVO=new MongoFileVO();

        GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(new ObjectId(ufileID));
        GridFSFile gridFSFile = downloadStream.getGridFSFile();
        mongoFileVO.setInputStream(downloadStream);
        mongoFileVO.setFileType(gridFSFile.getMetadata().get("fileType").toString());
        mongoFileVO.setFileName(gridFSFile.getFilename());
        mongoFileVO.setLength(gridFSFile.getLength());
        return mongoFileVO;
    }

     public static class MongoFileVO{
        private InputStream inputStream;
        private String fileType;
        private String fileName;
        private long length;

        public InputStream getInputStream() {
            return inputStream;
        }

        public void setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public long getLength() {
            return length;
        }

        public void setLength(long length) {
            this.length = length;
        }
    }


}
