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

public class BatchCollectionResponse implements BatchTxtFile {

	private ResponseHead head;

	private List<String> titles;

	private String titlesLine;

	private List<ResponseContent> contents;

	private Map<String, ResponseContent> orderIdRspContentPair;

	public BatchCollectionResponse() {
		this.contents = new ArrayList<ResponseContent>();
		this.orderIdRspContentPair = new HashMap<String, ResponseContent>();
	}

	public BatchCollectionResponse(List<String> titles) {
		this();
		this.titles = titles;
		this.titlesLine = UnionPayFileUtils.parseTxtTitlesLine(this.titles);
	}

	public BatchCollectionResponse(File file, String charset) throws IOException {
		this();
		init(new BufferedReader(new InputStreamReader(new FileInputStream(file), charset)));
	}

	public BatchCollectionResponse(String fileContent) throws IOException {
		this();
		init(new BufferedReader(new StringReader(fileContent)));
	}

	private void init(BufferedReader bufferedReader) throws IOException {
		String line = null;
		int count = 0;
		while ((line = bufferedReader.readLine()) != null) {
			if (count == 0) {
				this.head = UnionPayFileUtils.parseTxtLine(line, Arrays.asList(ResponseHead.TITLES),
						ResponseHead.class);
			} else if (count == 1) {
				this.titles = new ArrayList<String>(Arrays.asList(line.split("\\|")));
				this.titlesLine = UnionPayFileUtils.parseTxtTitlesLine(this.titles);
			} else {
				ResponseContent responseContent = UnionPayFileUtils.parseTxtLine(line, this.titles,
						ResponseContent.class);
				responseContent.setTitles(this.titles);
				contents.add(responseContent);
				orderIdRspContentPair.put(responseContent.getOrderId(), responseContent);
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

	public ResponseHead getHead() {
		return head;
	}

	public void setHead(ResponseHead head) {
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

	public List<ResponseContent> getContents() {
		return contents;
	}

	public void setContents(List<ResponseContent> contents) {
		this.contents = contents;
	}

	public ResponseContent getContentByOrderId(String orderId) {
		return this.orderIdRspContentPair == null ? null : orderIdRspContentPair.get(orderId);
	}

	public static void main(String[] args) throws IOException {
		BatchCollectionResponse batchCollectionResponse = new BatchCollectionResponse(
				new File("C:/Users/hkun/Desktop/批量文件示例V1.9/商户/txt/代收/DK00000000700000000000001201509110003O.txt"),
				"GBK");
		System.out.println(ToStringBuilder.reflectionToString(batchCollectionResponse, new MultiLineToStringStyle()));
		System.out.println(batchCollectionResponse.getTxtFileContent());
	}

}
