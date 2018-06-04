package com.mybank.pc.merchant.info;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.ehcache.CacheKit;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.rtf.RtfWriter2;
import com.mybank.pc.merchant.model.MerchantInfo;
import com.mybank.pc.merchant.model.MerchantUser;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MerchantInfoSrv {
    public synchronized String  getMerchantNo(String merType){
            Integer maxID = Db.queryInt("select max(id) from merchant_info");
            return String.valueOf(Integer.valueOf(merType)*100000+((maxID==null?0:maxID))+1);
    }
    public MerchantInfo getMerchantInfoByUserID(Integer userID){
        MerchantUser merchantUser = MerchantUser.dao.findFirst("select * from merchant_user mu where mu.userID=? ",userID);
        if(merchantUser!=null){
            return MerchantInfo.dao.findFirstByCache("merInfo","getMerchantInfoByMerID_"+merchantUser.getMerchantID(),"select * from merchant_info mi where mi.ID=?",merchantUser.getMerchantID());
        }else{
            return null;
        }

    }
    public void removeCacheMerchantInfo(Integer merID){
        CacheKit.remove("merInfo","getMerchantInfoByMerID_"+merID);
    }

    public void createAgreeDoc(MerchantInfo mi ,File docFile){
        Document document = new Document(PageSize.A4);
        try {
            RtfWriter2.getInstance(document, new FileOutputStream(docFile));
            document.open();


            //设置合同头


            Paragraph p = new Paragraph("委托代扣授权表", new Font(Font.NORMAL, 18, Font.BOLD) );
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

//            document.add(new Paragraph("为保护您的权益，以下内容请务必填写完整。"));
            // 设置中文字体
            // BaseFont bfFont =
            // BaseFont.createFont("STSongStd-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            // Font chinaFont = new Font();
            /*
             * 创建有三列的表格
             */
            Table table = new Table(4);
            table.setWidth(100);
            int width[] = {25,25,20,30};//设置每列宽度比例
            table.setWidths(width);
            table.setBorderWidth(1);
            table.setBorderColor(Color.BLACK);
            table.setPadding(20);
            table.setSpacing(0);


            Cell cell = new Cell("授权人");//单元格
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new Cell("");
            cell.setColspan(3);//设置表格为三列

            table.addCell(cell);

            cell = new Cell("被授权单位");
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new Cell("北京迈伴客金融信息服务有限公司");
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(3);
            table.addCell(cell);

            cell = new Cell("被授权业务描述");
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new Cell("");
            cell.setColspan(3);
            table.addCell(cell);

            cell = new Cell("授权人联系电话");
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new Cell("");
            cell.setColspan(3);
            table.addCell(cell);

            cell = new Cell("授权的银行账号");
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new Cell("");
            cell.setColspan(3);
            table.addCell(cell);

            cell = new Cell("开户银行");
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new Cell("");
            table.addCell(cell);

            cell = new Cell("开户行所在地");
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new Cell("");
            table.addCell(cell);


            cell = new Cell("授权人身份证号码");
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new Cell("");
            cell.setColspan(3);
            table.addCell(cell);

            cell = new Cell("授权人声明");
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new Cell("");
            cell.setColspan(3);

            cell.add(getCellParagraph("本人就本项授权作如下声明及保证:"));
            cell.add(getCellParagraph("(1) 本人同意并授权贵司通过银行代扣款支付的方式从本人在表内所填的账号中将应付款项支付给贵司。"));
            cell.add(getCellParagraph("(2) 本人知晓此份授权书是用户开通委托划扣的必备法律文件之一，并保证本授权表所填的各项资料均准确无误、真实合法，没有提供任何虛假信息;"));
            cell.add(getCellParagraph("(3) 若贵司有证据证明本人所提供的身份证明或相关证明文件虚假、无效，本人将独立承担由此引起的一切损失及法律纠纷;"));
            cell.add(getCellParagraph("(4) 本人如要求更改授权内容(如银行账户的变更等),必须及时通知贵司，重新签订授权书，授权资料以最新授权书为准;"));
            cell.add(getCellParagraph("(5) 本人如需终止授权，应及时到被授权单位办理相关授权终止手续; 因本人未能及时办理上述手续造成被转账事实而发生缴费纠纷的、甚至造成经济损失的，将由本人自行承担;"));
            cell.add(getCellParagraph("(6)本人在签订本授权时需配合提供以下证照(复印件),与本授权一同保留:"));
            cell.add(getCellParagraph("    1) 身份证(正反面)    2) 授权银行账号(正反面)"));
            cell.add(getCellParagraph("本人确认上述提供的有关本人的各项资料均真实有效并明确知悉本声明所涉及的各项权责，由此产生的风险本人自行承担，特此声明。(注: 请在下划线上抄录此段内容。)"));
            Paragraph ph = new Paragraph("______________________________________________________");
            ph.setAlignment(Element.ALIGN_CENTER);
            ph.setSpacingAfter(2);
            ph.setSpacingBefore(2);
            cell.add(ph);
            cell.add(ph);
            table.addCell(cell);


            cell = new Cell("授权人签名");
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new Cell("");
            table.addCell(cell);

            cell = new Cell("日期");

            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new Cell("    年    月    日");
            table.addCell(cell);

            cell = new Cell("备注");
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);

            cell = new Cell("");
            cell.setColspan(3);
            cell.add(getCellParagraphBZ("1、授权人签名后请在“授权人签名”处以及“授权人声明”处摁上手印，以示确认。"));
            cell.add(getCellParagraphBZ("2、本授权自授权人本人在“授权人签名”处签字之日起生效。"));
            cell.add(getCellParagraphBZ("3、本授权一式三份，授权人保留一份，并随相关业务合作协议的终止或延长而相应终止或延长(另有约定的除外)。"));
            table.addCell(cell);

            document.add(table);
            document.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    private Paragraph getCellParagraph(String content){
        Paragraph paragraph =new Paragraph(content,new Font(Font.NORMAL, 10, Font.NORMAL));
        paragraph.setFirstLineIndent(28);
        paragraph.setSpacingAfter(2);
        paragraph.setSpacingBefore(2);
        return paragraph;
    }
    private Paragraph getCellParagraphBZ(String content){
        Paragraph paragraph =new Paragraph(content,new Font(Font.NORMAL, 10, Font.NORMAL));
        paragraph.setSpacingAfter(2);
        paragraph.setSpacingBefore(2);
        return paragraph;
    }
}
