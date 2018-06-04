package com.mybank.pc.task;

import cn.hutool.core.io.FileUtil;
import com.jfinal.core.JFinal;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;
import com.jfinal.render.RenderManager;
import com.jfplugin.mail.MailKit;
import com.mybank.pc.core.CoreConfig;
import com.mybank.pc.kits.AppKit;
import com.mybank.pc.kits.DateKit;
import com.mybank.pc.kits.FileKit;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DelTmpExcelTask implements Runnable {
    @Override
    public void run() {
        File file= FileUtil.file(PathKit.getWebRootPath() + AppKit.getExcelPath());
        LogKit.info("开始清理昨日临时的excel文件,路径为:"+file.getPath());
        LogKit.info("清理前临时文件数量为=>"+FileUtil.size(file));
        FileUtil.clean(file);
        LogKit.info("临时生成的excel清理完毕,清理后数量为=>"+FileUtil.size(file));

    }
}
