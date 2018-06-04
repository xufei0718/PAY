package com.mybank.pc.kits.unionpay.acp.file.collection.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Arrays;

import com.mybank.pc.kits.unionpay.acp.file.UnionPayFileUtils;

public class BatchCollectionReject {

	private RejectContent rejectContent;

	public BatchCollectionReject() {

	}

	public BatchCollectionReject(File file, String charset) throws IOException {
		this();
		init(new BufferedReader(new InputStreamReader(new FileInputStream(file), charset)));
	}

	public BatchCollectionReject(String fileContent) throws IOException {
		this();
		init(new BufferedReader(new StringReader(fileContent)));
	}

	private void init(BufferedReader bufferedReader) throws IOException {
		String line = null;
		int count = 0;
		while ((line = bufferedReader.readLine()) != null) {
			if (count == 0) {
				this.rejectContent = UnionPayFileUtils.parseTxtLine(line, Arrays.asList(RejectContent.TITLES),
						RejectContent.class);
			}
		}
	}

	public RejectContent getRejectContent() {
		return rejectContent;
	}

	public void setRejectContent(RejectContent rejectContent) {
		this.rejectContent = rejectContent;
	}

}
