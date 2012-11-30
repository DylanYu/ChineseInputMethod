package gram;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

public class ReadIn {
	BufferedReader br_ = null;
	ArrayList<Character> words_ = new ArrayList<Character>();
	ArrayList<Integer> wordCount_ = new ArrayList<Integer>();
	StringBuffer content_ = new StringBuffer();
	int size_ = 0;
	ArrayList<Character> otherWords_ = new ArrayList<Character>();
	ArrayList<Integer> otherWordsCount_ = new ArrayList<Integer>();
	
	ReadIn() {
		try {
			FileInputStream filein = new FileInputStream("Corpus/pku_test_utf8");
			//Solved all encoding problems?
			br_ = new BufferedReader(new InputStreamReader(filein, "UTF-8"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	ReadIn(String fileName) {
		try {
			FileInputStream filein = new FileInputStream(fileName);
			br_ = new BufferedReader(new InputStreamReader(filein));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void read() {
		String line;
		try {
			line = br_.readLine();
			while(line != null) {
				content_.append(line);
				content_.append("\n");
				char[] chars = line.toCharArray();
				for(char each : chars){
					//only process Chinese
					if(!String.valueOf(each).matches("[\\u4e00-\\u9fff]+")) {
						continue;
					}
					if(!words_.contains(each)){
						words_.add(each);
						wordCount_.add(1);
						size_++;
					}
					else {
						//word exists, count value ++
						int index = words_.indexOf(each);
						int value = wordCount_.get(index) + 1;
						wordCount_.set(index, value);
					}
				}
				line = br_.readLine();
			}
			br_.close();
		} catch (IOException e) {
			e.printStackTrace();
			}
	}
	
	private void wordCountAnalyse() {
		int countUnder5 = 0;
		int i = 0;
		for(int count: wordCount_) {
			if(count < 2){
				System.out.print(words_.get(i) + " ");
				countUnder5++;
			}
			i++;
			if((i+1) % 100 == 0)
				System.out.println(i);
		}
		System.out.println(countUnder5 + " / " + words_.size());
	}
	
	public ArrayList<Character> getWords_() {
		return words_;
	}

	public int getSize_() {
		return size_;
	}

	public ArrayList<Integer> getWordCount_() {
		return wordCount_;
	}

	public StringBuffer getContent_() {
		return content_;
	}
	
	public static void main(String args[]) {
		ReadIn readObj = new ReadIn("Corpus/pku_test_utf8");
		readObj.read();
		readObj.wordCountAnalyse();
	}
	
}
