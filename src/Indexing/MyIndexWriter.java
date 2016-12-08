package Indexing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Classes.Path;

public class MyIndexWriter {
	// I suggest you to write very efficient code here, otherwise, your memory
	// cannot hold our corpus...
	Map<Integer, String> docId = new HashMap<Integer, String>();
	HashMap<String, Map<Integer, Integer>> indexBuild = new HashMap<String, Map<Integer, Integer>>();
	Map<String, Integer> termFreq = new HashMap<String, Integer>();
	String path;

	public MyIndexWriter(String type) throws IOException {
		// This constructor should initiate the FileWriter to output your index
		// files
		// remember to close files if you finish writing the index
		if (type.equals("trecweb"))
			path = Path.IndexWebDir;
		else
			path = Path.IndexTextDir;
		System.out.println(path);
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
	}

	public void IndexADocument(String docno, String content) throws IOException {
		// you are strongly suggested to build the index by installments
		// you need to assign the new non-negative integer docId to each
		// document, which will be used in MyIndexReader
		content = content.replaceAll("[^a-zA-Z\\s]", "");
		int docNumber = docId.size() + 1;
		docId.put(docNumber, docno);
		String[] words = content.split(" ");
		for (int i = 0; i < words.length; i++) {
			if (words[i].length() <= 1 || words[i].matches("\\s+")) {
				continue;
			}
			indexCreate(words[i], docNumber);
			termFreqBuild(words[i]);

		}
		if (indexBuild.size() % 10000 == 0) {
			writeIndex();
			indexBuild.clear();
		}
	}

	public void indexCreate(String keyWord, int docId) {
		if (indexBuild.containsKey(keyWord)) {
			Map<Integer, Integer> index = indexBuild.get(keyWord);
			if (indexBuild.get(keyWord).containsKey(docId)) {
				int count = index.get(docId);
				index.put(docId, count + 1);
			} else {
				index.put(docId, 1);
			}
		} else {
			Map<Integer, Integer> index = new HashMap<Integer, Integer>();
			index.put(docId, 1);
			indexBuild.put(keyWord, index);
		}
	}

	public void termFreqBuild(String keyWord) {
		if (termFreq.containsKey(keyWord)) {
			int count = termFreq.get(keyWord);
			termFreq.put(keyWord, count + 1);
		} else {
			termFreq.put(keyWord, 1);
		}
	}

	public void Close() throws IOException {
		// close the index writer, and you should output all the buffered
		// content (if any).
		// if you write your index into several files, you need to fuse them
		// here.
		if (indexBuild.size() > 0)
			writeIndex();
		writeTerm();
		writeDocId();
	}

	private void writeIndex() throws IOException {
		for (Map.Entry<String, Map<Integer, Integer>> entry : indexBuild.entrySet()) {
			String word = entry.getKey();
			int i = word.charAt(0);
			int j = i % 5;
			BufferedWriter indexWrite = new BufferedWriter(new FileWriter(path + j + ".txt", true));
			Map<Integer, Integer> count = entry.getValue();
			for (Map.Entry<Integer, Integer> interEntry : count.entrySet()) {
				indexWrite.write(word + " " + interEntry.getKey() + " " + interEntry.getValue());
				indexWrite.newLine();
				indexWrite.flush();
			}
			indexWrite.close();
		}
	}

	private void writeTerm() throws IOException {
		String fileName = path + "termFile.txt";
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
		for (Map.Entry<String, Integer> entry : termFreq.entrySet()) {
			String word = entry.getKey();
			int count = entry.getValue();
			bw.write(word + " " + count);
			bw.newLine();
			bw.flush();
		}
		bw.close();
	}

	private void writeDocId() throws IOException {
		String fileName = path + "docId.txt";
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
		for (Map.Entry<Integer, String> entry : docId.entrySet()) {
			Integer docID = entry.getKey();
			String docNo = entry.getValue();
			bw.write(docID + " " + docNo);
			bw.newLine();
			bw.flush();
		}
		bw.close();
	}

}
