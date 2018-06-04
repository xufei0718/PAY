package com.mybank.pc.kits.unionpay.acp.file.collection.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.mybank.pc.kits.unionpay.acp.file.BatchTxtFile;
import com.mybank.pc.kits.unionpay.acp.file.MultiLineToStringStyle;
import com.mybank.pc.kits.unionpay.acp.file.UnionPayFileUtils;

public class BatchCollectionRequest implements BatchTxtFile {

	public static final String[] DEFAULT_TITLES = new String[] { "orderId", "currencyCode", "txnAmt", "accType",
			"accNo", "customerNm", "bizType", "certifTp", "certifId", "phoneNo", "postscript", "reqReserved1" };

	public static final String DEFAULT_TITLES_LINE = "orderId|currencyCode|txnAmt|accType|accNo|customerNm|bizType|certifTp|certifId|phoneNo|postscript|reqReserved1";

	private RequestHead head;

	private List<String> titles;

	private String titlesLine;

	private List<RequestContent> contents;

	private Map<String, RequestContent> orderIdReqContentPair;

	public BatchCollectionRequest() {
		this.titles = new ArrayList<String>(Arrays.asList(DEFAULT_TITLES));
		this.titlesLine = DEFAULT_TITLES_LINE;
		this.contents = new ArrayList<RequestContent>();
		this.orderIdReqContentPair = new HashMap<String, RequestContent>();
	}

	public BatchCollectionRequest(List<String> titles) {
		this();
		this.titles = titles;
		this.titlesLine = UnionPayFileUtils.parseTxtTitlesLine(this.titles);
	}

	public BatchCollectionRequest(File file, String charset) throws IOException {
		this();
		init(new BufferedReader(new InputStreamReader(new FileInputStream(file), charset)));
	}

	public BatchCollectionRequest(String fileContent) throws IOException {
		this();
		init(new BufferedReader(new StringReader(fileContent)));
	}

	private void init(BufferedReader bufferedReader) throws IOException {
		String line = null;
		int count = 0;
		while ((line = bufferedReader.readLine()) != null) {
			if (count == 0) {
				this.head = UnionPayFileUtils.parseTxtLine(line, Arrays.asList(RequestHead.TITLES), RequestHead.class);
			} else if (count == 1) {
				this.titles = new ArrayList<String>(Arrays.asList(line.split("\\|")));
				this.titlesLine = UnionPayFileUtils.parseTxtTitlesLine(this.titles);
			} else {
				RequestContent requestContent = UnionPayFileUtils.parseTxtLine(line, this.titles, RequestContent.class);
				contents.add(requestContent);
				orderIdReqContentPair.put(requestContent.getOrderId(), requestContent);
			}
			++count;
		}
	}

	@Override
	public String getTxtFileContent() {
		if (this.head == null || CollectionUtils.isEmpty(this.titles) || StringUtils.isBlank(this.titlesLine)
				|| CollectionUtils.isEmpty(this.contents)) {
			throw new RuntimeException("存在非法数据!!");
		}

		StringBuffer fileContentBuffer = new StringBuffer(200);
		fileContentBuffer.append(this.head.getTxtLine()).append("\r\n");
		fileContentBuffer.append(this.titlesLine).append("\r\n");
		for (int i = 0, size = contents.size(); i < size; ++i) {
			fileContentBuffer.append(contents.get(i).getTxtLine());
			if (i < size - 1) {
				fileContentBuffer.append("\r\n");
			}
		}
		return fileContentBuffer.toString();
	}

	public RequestHead getHead() {
		return head;
	}

	public void setHead(RequestHead head) {
		this.head = head;
	}

	public List<String> getTitles() {
		return titles;
	}

	public void setTitles(List<String> titles) {
		this.titles = titles;
	}

	public String getTitlesLine() {
		return titlesLine;
	}

	public void setTitlesLine(String titlesLine) {
		this.titlesLine = titlesLine;
	}

	public List<RequestContent> getContents() {
		return contents;
	}

	public void setContents(List<RequestContent> contents) {
		this.contents = contents;
	}

	public void addContent(RequestContent content) {
		this.contents.add(content);
		this.orderIdReqContentPair.put(content.getOrderId(), content);
	}

	public RequestContent getContentByOrderId(String orderId) {
		return this.orderIdReqContentPair == null ? null : orderIdReqContentPair.get(orderId);
	}

	public static void main(String[] args) throws IOException {
		BatchCollectionRequest batchCollectionRequest = new BatchCollectionRequest(
				new File("C:/Users/hkun/Desktop/批量文件示例V1.9/商户/txt/代收/DK00000000700000000000001201509110003I.txt"),
				"GBK");
		System.out.println(ToStringBuilder.reflectionToString(batchCollectionRequest, new MultiLineToStringStyle()));

		System.out.println(batchCollectionRequest.getTxtFileContent());
	}

}
