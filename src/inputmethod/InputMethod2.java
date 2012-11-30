package inputmethod;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;

import segment.Segment;

public class InputMethod2 {
	String spelling_ = null;
	//Hashtable<String, Double> outputWords_ = null;
	String outputWords_ = null;
	int[][] phraseCount_ = null;
	ArrayList<Character> words_ = null;
	Hashtable<String, String> dict_ = null;
	//Hashtable<String, Double>wordPr_ = null;
	/**
	 * TODO
	 * two ArrayList can replace Hashtable
	 */
	//word Probability for the same spelling 
	ArrayList<Integer>wordCount_ = null;
//	ArrayList<Double>wordPr_ = null;
	
	String wordFilename_ = "word.txt";
//	String proFilename_ = "probability.txt";
	String proFilename_ = "P_0_1900";
	String dictFilename_ = "dict";
	
	InputMethod2(){
		words_ = new ArrayList<Character>();
		//outputWords_= new Hashtable<String, Double>();
		dict_ = new Hashtable<String, String>();
		wordCount_ = new ArrayList<Integer>();
//		wordPr_ = new ArrayList<Double>();
	}
	
	private void readDictFile() {
		DataInputStream dictFilein = null;
		try {
			dictFilein = new DataInputStream(new FileInputStream(dictFilename_));
			BufferedReader br = new BufferedReader(new InputStreamReader(dictFilein));
			String line = br.readLine();
			while(line != null) {
				String temp[] = line.split("\t");
				String index = temp[0];
				String values = temp[1];
				dict_.put(index, values);
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readWordFile() {
		FileInputStream wordFilein = null;
		try {
			wordFilein = new FileInputStream(wordFilename_);
			BufferedReader br = new BufferedReader(new InputStreamReader(wordFilein));
			String line = br.readLine();
			while(line != null) {
				String[] strs = line.split("\\|");
				words_.add(strs[0].toCharArray()[0]);
				//CAUTION
				wordCount_.add(Integer.parseInt(strs[1]));
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//init probability_ with size
		phraseCount_ = new int[words_.size()][words_.size()];
	}
	
	private void readPrFile() {
		FileInputStream proFilein = null;
		try {
			proFilein = new FileInputStream(proFilename_);
			BufferedReader br = new BufferedReader(new InputStreamReader(proFilein));
			String line = br.readLine();
			int i = 0;
			while(line != null) {
				String[] strs = line.split(" ");
				for(int j = 0; j < strs.length; j++){
					phraseCount_[i][j] = Integer.valueOf(strs[j]);
				}
				i++;
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readFile() {
		//sequence can't be changed
		this.readWordFile();
		this.readPrFile();
		this.readDictFile();
	}
	
	private void computeWordPr() {
		//Enumeration<String> e  = dict_.elements();
		//TODO new skill
		Enumeration<String> keys = dict_.keys();
		String key = "";
		while(keys.hasMoreElements()){
			key = keys.nextElement();
			int index = words_.indexOf(key);
		}
	}
	
	/**
	 * 
	 * @param spelling
	 * 		spelling for s a single word,suck as 'hao', 'de'.
	 * @return
	 * 		waiting words for the spelling.
	 * 		e.g.'康慷糠扛抗亢炕伉闶钪' for 'kang'
	 */
	private String findWaitingWords(String spelling) {
		String waitingWords = dict_.get(spelling);
		return waitingWords;
	}
	
	public String segment(String spelling){
		Segment segment=new Segment();
	    String result = segment.getFullSeg(spelling);
	    return result;
	}
	
	/**
	 * 
	 * @param spelling
	 * 		spelling for multi-words, suck as 'haode', 'meiyou', 'wodegushi'
	 */
	public void computeWords2(String spelling) {
		String[] segmentedSpelling = segment(spelling).split(" ");
		String[] seg = segmentedSpelling;

		ArrayList<String> waitingSentence = new ArrayList<String>();
		ArrayList<Integer> waitingCount = new ArrayList<Integer>();
		char[] firstWords = findWaitingWords(seg[0]).toCharArray();
		for(char ch: firstWords) {
			//use wordCount instead of word probability, maybe it's good
			StringBuffer sb = new StringBuffer();
			sb.append(ch);
			waitingSentence.add(sb.toString());
			waitingCount.add((wordCount_.indexOf(words_.indexOf(ch))) + 1);
		}
		
		//waitingSentence
		//waitingPr
		/**
		 * CORE
		 */
		for(int k = 1; k < seg.length; k++) {
			//char[] words0 = findWaitingWords(seg[k - 1]).toCharArray();
			//System.out.println(findWaitingWords(seg[k]));
			char[] words = findWaitingWords(seg[k]).toCharArray();
			//probability for this round under k

			String[][] laterSentence = new String[waitingSentence.size()][words.length];
			int[][] laterCount = new int[waitingSentence.size()][words.length];
			for(int sentence_i = 0; sentence_i < waitingSentence.size(); sentence_i++) {
				//取出候选句子中最后一个字
				String sentence = waitingSentence.get(sentence_i);
				char[] words0 = sentence.toCharArray();
				char lastWord = words0[words0.length - 1];
				//得到候选句子的count之积
				int sentenceCount = waitingCount.get(sentence_i);
				
				//遍历当前字的所有可能
				for(int word_i = 0; word_i < words.length; word_i++) {
					char thisWord = words[word_i];
					int row = words_.indexOf(lastWord);
					int col = words_.indexOf(thisWord);
					int eachCount = 1;
					if(row >= 0 && col >= 0) {
						eachCount = this.phraseCount_[row][col] + 1;
					}
					//处理句子，加入候选矩阵
					StringBuffer sb = new StringBuffer(sentence);
					sb.append(thisWord);
					laterSentence[sentence_i][word_i] = sb.toString();
					//处理出现次数，加入候选矩阵
					int c = sentenceCount * eachCount;
					laterCount[sentence_i][word_i] = c;
				}
			}
			int waitingSentenceSize = waitingSentence.size();
			waitingSentence.clear();
			waitingCount.clear();
			for(int sentenceCount = 0; sentenceCount < waitingSentenceSize; sentenceCount++) {
				for(int wordCount = 0; wordCount < words.length; wordCount++) {
					waitingSentence.add(laterSentence[sentenceCount][wordCount]);
					waitingCount.add(laterCount[sentenceCount][wordCount]);
				}
			}
		}

		
		System.out.println(waitingCount.size());
		@SuppressWarnings("unchecked")
		ArrayList<Integer> waitingCountCopy = (ArrayList<Integer>) waitingCount.clone();
		Collections.sort(waitingCountCopy, Collections.reverseOrder());
		for(int i = 0; i < 5; i++){
			int index = waitingCount.indexOf(waitingCountCopy.get(i));
			String sentence = waitingSentence.get(index);
			System.out.println(sentence + waitingCountCopy.get(i));
		}

	}
	
	
	
	/**
	 * 
	 * @param spelling
	 * 		spelling for multi-words, suck as 'haode', 'meiyou', 'wodegushi'
	 */
	public void computeWords(String spelling) {
		String[] segmentedSpelling = segment(spelling).split(" ");
		StringBuffer buffer = new StringBuffer();
		ArrayList<String> ws = new ArrayList<String>();
		ArrayList<Double> pros = new ArrayList<Double>();
		String last = "";
		ArrayList<Integer> mulPr = new ArrayList<Integer>();
		for(int k = 0; k < segmentedSpelling.length - 1 /*-1 for 2gram*/; k++) {
			String[] seg = segmentedSpelling;
			char[] words1 = null;
			if(k == 0) {
				words1 = findWaitingWords(seg[k]).toCharArray();
			}
			else {
				words1 = last.toCharArray();
			}
			char[] words2 = findWaitingWords(seg[k + 1]).toCharArray();
			for(int i = 0; i < words1.length; i++)
				System.out.print(words1[i]);
			System.out.println();
			for(int i = 0; i < words2.length; i++)
				System.out.print(words2[i]);
			System.out.println();
			int max_i = 0;
			int max_j = 0;
			double max_pro = 0;
			boolean hasChanged= false;
			char word1 = ' ';
			char word2 = ' ';
			double probability = 0;
			for(int i = 0; i < words1.length; i++) {
				for(int j = 0; j < words2.length; j++) {
					probability = 0;
					int row = words_.indexOf(words1[i]);
					int col = words_.indexOf(words2[j]);
					if(row < 0 || col < 0)
						continue;
					//System.out.println(row + " " + col);
					probability = phraseCount_[row][col];
					if(probability > max_pro) {
						hasChanged = true;
						max_pro = probability;
						max_i = i;
						max_j = j;
					}
//					if(probability > 0){
						StringBuffer bb = new StringBuffer();
						bb.append(words1[i]);
						bb.append(words2[j]);
						ws.add(bb.toString());
						pros.add(probability);
//					}
				}
			}
			
			if(hasChanged) {
				word1 = words1[max_i];
				word2 = words2[max_j];
			}
			//no matched phrase, so we should choose default word for every spelling, 
			//such as '不' for 'bu', '好' for 'hao', which is studied from training data
			else {
				/**
				 * 
				 * 
				 */
				System.out.println("choose default");
				int maxCount = 0;
				char maxChar = ' ';
				boolean hasChanegd2 = false;
				for(int i = 0; i < words1.length; i++) {
					int index = words_.indexOf(words1[i]);
					if(index == -1)
						continue;
					int count = wordCount_.get(index);
					if(count > maxCount) {
						hasChanegd2 = true;
						maxCount = count;
						maxChar = words1[i];
					}
				}
				if(!hasChanegd2) {
					/**
					 * 即在选择默认字时，该读音内候选字都没有在语料库中出现。这是语料库太小所致。
					 */
					System.out.println("error in InputMethod:computeWord:hasChanegd2");
					maxChar = words1[0];
				}
				word1 = maxChar;
				maxCount = 0;
				maxChar = ' ';
				hasChanegd2 = false;
				for(int i = 0; i < words2.length; i++) {
					int index = words_.indexOf(words2[i]);
					if(index == -1)
						continue;
					int count = wordCount_.get(index);
					if(count > maxCount) {
						hasChanegd2 = true;
						maxCount = count;
						maxChar = words2[i];
					}
				}
				if(!hasChanegd2) {
					/**
					 * 即在选择默认字时，该读音内候选字都没有在语料库中出现。这是语料库太小所致。
					 */
					System.out.println("error in InputMethod:computeWord:hasChanegd2");
					maxChar = words2[0];
				}
				word2 = maxChar;
			}
			if(k == 0) {
				buffer.append(word1);
				buffer.append(word2);
				//System.out.print("here");
			}
			else {
				buffer.append(word2);
			}
			last = String.valueOf(words2[max_j]);
		}
		for(int i = 0; i < 10; i++){
			//System.out.println("Size of pros is " + pros.size());
			ArrayList<Double> pp = new ArrayList<Double>();
			for(int count = 0; count < pros.size(); count++){
				pp.add(pros.get(count));
			}
			Collections.sort(pp, Collections.reverseOrder());
			Double p = pp.get(0);
			System.out.println("p current max is " + p);
			int index = pros.indexOf(p);
			String o = ws.get(index);
			System.out.println(o);
			pros.remove(index);
			ws.remove(index);
			System.out.println("Size of pros is " + pros.size());
		}
		//TODO sfjuiihiu
		this.outputWords_ = buffer.toString();
		System.out.println(outputWords_);
	}
	
	
	
	public static void main(String args[]) {
		InputMethod2 method = new InputMethod2();
		method.readFile();
		//method.computeWords("xi'anshiwo");
		method.computeWords2("jintianzaoshang");
	}
}
